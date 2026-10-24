package graph

import algebra.*
import mesh.*
import kotlin.test.*

private const val _DEBUG_EPS = 1e-5f

private fun _assertDebugClose(expected: Float, actual: Float): Unit {
    assertTrue(
        kotlin.math.abs(expected - actual) <= _DEBUG_EPS,
        "expected $expected but was $actual"
    )
}

private fun _assertDebugPoint(expected: Vector3, actual: Vector3): Unit {
    _assertDebugClose(expected.x, actual.x)
    _assertDebugClose(expected.y, actual.y)
    _assertDebugClose(expected.z, actual.z)
}

private fun _shaderOffset(x: Float, y: Float, z: Float): Matrix4 {
    val result = algebra.offset(x, y, z)
    result.transpose()
    return result
}

private fun _shaderPoint(model: Matrix4, point: Vector3): Vector3 {
    val transposed = model.copy()
    transposed.transpose()
    return transposed.vecMultiply(point)
}

private fun _unitBounds(): AABB {
    return AABB(
        Vector3(0.0f, 0.0f, 0.0f),
        Vector3(1.0f, 1.0f, 1.0f)
    )
}

// ============================================================================
// ======================= DebugTestMesh ======================================
// ============================================================================
private class DebugTestMesh(bounds: AABB) : Mesh("debugTest") {
    init {
        mAABB = bounds
    }

    override fun transform(tr: Matrix4): Unit {
    }
}

// ============================================================================
// ======================= DebugDrawInfoCollectorVisitorTest ==================
// ============================================================================
class DebugDrawInfoCollectorVisitorTest {
    @Test
    fun collectsLightsThroughNestedGroupsAndOnlyActiveSwitchBranches(): Unit {
        val root = TransformGroup()
        val group = Group()
        val nested = TransformGroup()
        val switch = Switch()
        val state = StateGroup()
        val visible = OmniLight()
        val hidden = OmniLight()
        val hiddenDrawable = SpinableDrawable(DebugTestMesh(_unitBounds()))

        root.matrix = algebra.offset(10.0f, 0.0f, 0.0f)
        nested.matrix = algebra.offset(0.0f, 2.0f, 0.0f)
        visible.color = Vector3(0.2f, 0.4f, 0.6f)

        state.addChild(visible)
        switch.addChild(true, state)
        switch.addChild(false, hidden)
        switch.addChild(false, hiddenDrawable)
        nested.addChild(switch)
        group.addChild(nested)
        root.addChild(group)

        val collector = DebugDrawInfoCollectorVisitor()
        root.accept(collector)
        val info = collector.info

        assertEquals(1, info.lights.size)
        assertTrue(info.aabbs.isEmpty())

        val light = info.lights[0]
        _assertDebugPoint(
            Vector3(10.0f, 2.0f, 0.0f),
            _shaderPoint(light.modelMatrix, Vector3(0.0f, 0.0f, 0.0f))
        )
        _assertDebugPoint(
            Vector3(10.3f, 2.3f, 0.3f),
            _shaderPoint(light.modelMatrix, Vector3(1.0f, 1.0f, 1.0f))
        )
        _assertDebugPoint(Vector3(0.2f, 0.4f, 0.6f), light.color)

        visible.color.set(9.0f, 8.0f, 7.0f)
        _assertDebugPoint(Vector3(0.2f, 0.4f, 0.6f), light.color)
    }

    @Test
    fun composesNonCenteredBoxBoundsWithCurrentDrawableModelMatrix(): Unit {
        val bounds = AABB(
            Vector3(1.0f, 2.0f, 3.0f),
            Vector3(3.0f, 6.0f, 7.0f)
        )
        val drawable = SpinableDrawable(DebugTestMesh(bounds))
        val root = Group()

        drawable.modelMatrix = algebra.scale(2.0f, 3.0f, 4.0f).multiply(
            _shaderOffset(10.0f, 20.0f, 30.0f)
        )
        root.addChild(drawable)

        val collector = DebugDrawInfoCollectorVisitor()
        root.accept(collector)
        val info = collector.info

        assertEquals(1, info.aabbs.size)
        assertTrue(info.lights.isEmpty())

        val model = info.aabbs[0].modelMatrix
        _assertDebugPoint(
            Vector3(12.0f, 26.0f, 42.0f),
            _shaderPoint(model, Vector3(0.0f, 0.0f, 0.0f))
        )
        _assertDebugPoint(
            Vector3(16.0f, 38.0f, 58.0f),
            _shaderPoint(model, Vector3(1.0f, 1.0f, 1.0f))
        )
    }

    @Test
    fun collectsBoxesFromPostAnimationModelMatrices(): Unit {
        val root = TransformGroup()
        val drawable = SpinableDrawable(DebugTestMesh(_unitBounds()))

        root.matrix = algebra.offset(5.0f, 0.0f, 0.0f)
        drawable.origin = Vector3(1.0f, 2.0f, 3.0f)
        drawable.scale = Vector3(2.0f, 3.0f, 4.0f)
        root.addChild(drawable)

        root.accept(AnimationVisitor(1.0f))

        val collector = DebugDrawInfoCollectorVisitor()
        root.accept(collector)
        val model = collector.info.aabbs.single().modelMatrix

        _assertDebugPoint(
            Vector3(6.0f, 2.0f, 3.0f),
            _shaderPoint(model, Vector3(0.0f, 0.0f, 0.0f))
        )
        _assertDebugPoint(
            Vector3(8.0f, 5.0f, 7.0f),
            _shaderPoint(model, Vector3(1.0f, 1.0f, 1.0f))
        )
    }

    @Test
    fun includesCulledAndDegenerateGeometryAndMoreThanSixteenLights(): Unit {
        val bounds = AABB(
            Vector3(2.0f, 3.0f, 4.0f),
            Vector3(2.0f, 3.0f, 4.0f)
        )
        val drawable = SpinableDrawable(DebugTestMesh(bounds))
        val root = Group()

        drawable.isFrustumCulled = true
        root.addChild(drawable)

        for (index in 0 until 20) {
            val light = OmniLight()
            light.color = Vector3(index.toFloat(), 0.0f, 0.0f)
            root.addChild(light)
        }

        val collector = DebugDrawInfoCollectorVisitor()
        root.accept(collector)
        val info = collector.info

        assertEquals(20, info.lights.size)
        assertEquals(1, info.aabbs.size)
        _assertDebugClose(19.0f, info.lights[19].color.x)
    }

    @Test
    fun emitsOneBoxPerDrawableSharingTheSameMesh(): Unit {
        val sharedMesh = DebugTestMesh(_unitBounds())
        val first = SpinableDrawable(sharedMesh)
        val second = SpinableDrawable(sharedMesh)
        val root = Group()

        first.modelMatrix = _shaderOffset(1.0f, 0.0f, 0.0f)
        second.modelMatrix = _shaderOffset(2.0f, 0.0f, 0.0f)
        root.addChild(first)
        root.addChild(second)

        val collector = DebugDrawInfoCollectorVisitor()
        root.accept(collector)
        val info = collector.info

        assertEquals(2, info.aabbs.size)
        _assertDebugPoint(
            Vector3(1.0f, 0.0f, 0.0f),
            _shaderPoint(info.aabbs[0].modelMatrix, Vector3())
        )
        _assertDebugPoint(
            Vector3(2.0f, 0.0f, 0.0f),
            _shaderPoint(info.aabbs[1].modelMatrix, Vector3())
        )
    }

    @Test
    fun skipsMeshlessDrawablesAndSpotlights(): Unit {
        val root = Group()
        root.addChild(SpinableDrawable())
        root.addChild(SpotLight())

        val collector = DebugDrawInfoCollectorVisitor()
        root.accept(collector)

        assertTrue(collector.info.lights.isEmpty())
        assertTrue(collector.info.aabbs.isEmpty())
    }

    @Test
    fun returnsEmptyInfoForEmptyInput(): Unit {
        val collector = DebugDrawInfoCollectorVisitor()
        Group().accept(collector)

        assertTrue(collector.info.lights.isEmpty())
        assertTrue(collector.info.aabbs.isEmpty())
    }

    @Test
    fun freshCollectorsProduceIndependentFrameSnapshots(): Unit {
        val light = OmniLight()
        val root = Group()
        light.color = Vector3(1.0f, 2.0f, 3.0f)
        root.addChild(light)

        val firstCollector = DebugDrawInfoCollectorVisitor()
        root.accept(firstCollector)
        val firstInfo = firstCollector.info

        light.color.set(4.0f, 5.0f, 6.0f)

        val secondCollector = DebugDrawInfoCollectorVisitor()
        root.accept(secondCollector)
        val secondInfo = secondCollector.info

        assertEquals(1, firstInfo.lights.size)
        assertEquals(1, secondInfo.lights.size)
        _assertDebugPoint(Vector3(1.0f, 2.0f, 3.0f), firstInfo.lights[0].color)
        _assertDebugPoint(Vector3(4.0f, 5.0f, 6.0f), secondInfo.lights[0].color)
    }
}
