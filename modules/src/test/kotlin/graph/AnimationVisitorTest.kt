package graph

import algebra.*
import kotlin.test.*

private const val _ANIMATION_EPS = 1e-5f

private fun _assertAnimationClose(expected: Float, actual: Float): Unit {
    assertTrue(
        kotlin.math.abs(expected - actual) <= _ANIMATION_EPS,
        "expected $expected but was $actual"
    )
}

private fun _shaderPosition(model: Matrix4): Vector4 {
    val transposed = model.copy()
    transposed.transpose()
    return transposed.vecMultiply(Vector4(0.0f, 0.0f, 0.0f, 1.0f))
}

// ============================================================================
// ======================= AnimationVisitorTest ===============================
// ============================================================================
class AnimationVisitorTest {
    @Test
    fun updatesBothAnimatedDrawableTypesOncePerVisit(): Unit {
        val spinable = SpinableDrawable()
        val flyaround = FlyaroundDrawable()
        val root = Group()

        spinable.anglSpeed = 8.0f
        flyaround.anglSpeed = 8.0f
        spinable.isFrustumCulled = true
        flyaround.isFrustumCulled = true
        root.addChild(spinable)
        root.addChild(flyaround)

        val visitor = AnimationVisitor(0.5f)
        root.accept(visitor)

        _assertAnimationClose(4.0f, spinable.angl)
        _assertAnimationClose(4.0f, flyaround.angl)

        root.accept(visitor)

        _assertAnimationClose(8.0f, spinable.angl)
        _assertAnimationClose(8.0f, flyaround.angl)
    }

    @Test
    fun traversesNestedTransformsGroupsSwitchesAndStateGroups(): Unit {
        val root = TransformGroup()
        val group = Group()
        val nested = TransformGroup()
        val switch = Switch()
        val state = StateGroup()
        val visible = SpinableDrawable()
        val hidden = SpinableDrawable()

        root.matrix = algebra.offset(10.0f, 0.0f, 0.0f)
        nested.matrix = algebra.offset(0.0f, 2.0f, 0.0f)
        visible.origin = Vector3(1.0f, 0.0f, 0.0f)
        hidden.anglSpeed = 12.0f

        state.addChild(visible)
        switch.addChild(true, state)
        switch.addChild(false, hidden)
        nested.addChild(switch)
        group.addChild(nested)
        root.addChild(group)

        root.accept(AnimationVisitor(1.0f))

        val position = _shaderPosition(visible.modelMatrix)
        _assertAnimationClose(11.0f, position.x)
        _assertAnimationClose(2.0f, position.y)
        _assertAnimationClose(0.0f, position.z)
        _assertAnimationClose(1.0f, position.w)
        _assertAnimationClose(0.0f, hidden.angl)
    }
}
