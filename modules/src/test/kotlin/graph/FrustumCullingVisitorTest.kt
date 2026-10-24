package graph

import algebra.*
import kotlin.test.*

// ============================================================================
// ======================= FrustumCullingVisitorTest ==========================
// ============================================================================
class FrustumCullingVisitorTest {
    @Test
    fun resetsMeshlessDrawablesToUnculled(): Unit {
        val spinable = SpinableDrawable()
        val flyaround = FlyaroundDrawable()
        val root = Group()

        spinable.isFrustumCulled = true
        flyaround.isFrustumCulled = true
        root.addChild(spinable)
        root.addChild(flyaround)

        root.accept(FrustumCullingVisitor(Matrix4()))

        assertFalse(spinable.isFrustumCulled)
        assertFalse(flyaround.isFrustumCulled)
    }

    @Test
    fun ignoresNonDrawableLeaves(): Unit {
        val behavior = Behavior()

        behavior.accept(FrustumCullingVisitor(Matrix4()))
    }

    @Test
    fun traversesOnlyActiveBranchesWithoutTouchingPrograms(): Unit {
        val root = Group()
        val switch = Switch()
        val state = StateGroup()
        val visible = SpinableDrawable()
        val visibleInState = FlyaroundDrawable()
        val hidden = SpinableDrawable()

        visible.isFrustumCulled = true
        visibleInState.isFrustumCulled = true
        hidden.isFrustumCulled = true

        state.addChild(visibleInState)
        switch.addChild(true, visible)
        switch.addChild(true, state)
        switch.addChild(false, hidden)
        root.addChild(switch)

        root.accept(FrustumCullingVisitor(Matrix4()))

        assertFalse(visible.isFrustumCulled)
        assertFalse(visibleInState.isFrustumCulled)
        assertTrue(hidden.isFrustumCulled)
    }
}
