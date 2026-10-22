package graph

import algebra.*
import graphdsl.*
import kotlin.test.*

private const val _EPS = 1e-5f

private fun _assertClose(expected: Float, actual: Float) {
    assertTrue(
        kotlin.math.abs(expected - actual) <= _EPS,
        "expected $expected but was $actual"
    )
}

private fun _assertClose(expected: Vector3, actual: Vector3) {
    _assertClose(expected.x, actual.x)
    _assertClose(expected.y, actual.y)
    _assertClose(expected.z, actual.z)
}

// ============================================================================
// ======================= LightCollectorVisitorTest ==========================
// ============================================================================
class LightCollectorVisitorTest {

    @Test
    fun collectsOmniLightWorldPositionFromTransformChain() {
        val root = TransformGroup()
        root.matrix = algebra.offset(Vector3(10.0f, 8.0f, 10.0f))

        val child = TransformGroup()
        child.matrix = algebra.offset(Vector3(1.0f, 2.0f, 3.0f))

        val light = buildLight {
            omniLight {
                color = Vector3(1.0f, 0.9f, 0.7f)
                intensity = 2.0f
                radius = 40.0f
            }
        }

        child.addChild(light)
        root.addChild(child)

        val collector = LightCollectorVisitor()
        root.accept(collector)

        assertEquals(1, collector.omniLights.size)

        val collected = collector.omniLights[0]
        _assertClose(Vector3(11.0f, 10.0f, 13.0f), collected.position)
        _assertClose(Vector3(1.0f, 0.9f, 0.7f), collected.color)
        _assertClose(2.0f, collected.intensity)
        _assertClose(40.0f, collected.radius)
    }

    @Test
    fun collectsMultipleLightsAcrossBranches() {
        val root = TransformGroup()
        root.matrix = algebra.offset(Vector3(0.0f, 0.0f, 0.0f))

        val branchA = TransformGroup()
        branchA.matrix = algebra.offset(Vector3(5.0f, 0.0f, 0.0f))
        branchA.addChild(buildLight { omniLight {} })

        val branchB = TransformGroup()
        branchB.matrix = algebra.offset(Vector3(-5.0f, 3.0f, 0.0f))
        branchB.addChild(buildLight { omniLight {} })

        root.addChild(branchA)
        root.addChild(branchB)

        val collector = LightCollectorVisitor()
        root.accept(collector)

        assertEquals(2, collector.omniLights.size)
        _assertClose(Vector3(5.0f, 0.0f, 0.0f), collector.omniLights[0].position)
        _assertClose(Vector3(-5.0f, 3.0f, 0.0f), collector.omniLights[1].position)
    }

    @Test
    fun ignoresNonLightLeaves() {
        val root = TransformGroup()
        root.matrix = algebra.offset(Vector3(0.0f, 0.0f, 0.0f))
        root.addChild(SpinableDrawable())

        val collector = LightCollectorVisitor()
        root.accept(collector)

        assertEquals(0, collector.omniLights.size)
    }

    @Test
    fun traversesStateGroupWithoutTouchingProgram() {
        val root = TransformGroup()
        root.matrix = algebra.offset(Vector3(0.0f, 0.0f, 0.0f))

        val state = StateGroup()
        val inner = TransformGroup()
        inner.matrix = algebra.offset(Vector3(0.0f, 7.0f, 0.0f))
        inner.addChild(buildLight { omniLight {} })
        state.addChild(inner)

        root.addChild(state)

        val collector = LightCollectorVisitor()
        root.accept(collector)

        assertEquals(1, collector.omniLights.size)
        _assertClose(Vector3(0.0f, 7.0f, 0.0f), collector.omniLights[0].position)
    }
}
