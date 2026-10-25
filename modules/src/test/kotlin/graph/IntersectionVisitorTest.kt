package graph

import algebra.*
import mesh.*
import spatial.*
import kotlin.test.*

private const val INTERSECTION_TEST_EPSILON = 1e-4f

private fun assertIntersectionClose(expected: Float, actual: Float): Unit {
    assertTrue(
        kotlin.math.abs(expected - actual) <= INTERSECTION_TEST_EPSILON,
        "expected $expected but was $actual"
    )
}

private fun assertIntersectionPoint(expected: Vector3, actual: Vector3): Unit {
    assertIntersectionClose(expected.x, actual.x)
    assertIntersectionClose(expected.y, actual.y)
    assertIntersectionClose(expected.z, actual.z)
}

private fun intersectionTriangle(z: Float): FloatArray {
    return floatArrayOf(
        -1.0f, -1.0f, z,
        1.0f, -1.0f, z,
        0.0f, 1.0f, z
    )
}

private fun intersectionRay(
    eye: Vector3 = Vector3(),
    direction: Vector3 = Vector3(0.0f, 0.0f, 1.0f)
): Ray {
    return Ray(eye, direction, 0.0f, 100.0f)
}

private fun intersectionShaderOffset(x: Float, y: Float, z: Float): Matrix4 {
    val result = algebra.offset(x, y, z)
    result.transpose()
    return result
}

// ============================================================================
// ======================= IntersectionTestMesh ===============================
// ============================================================================

private class IntersectionTestMesh(private val _positions: FloatArray) :
    Mesh("intersectionTest"), TriangleSoupMesh {
    init {
        if (_positions.isNotEmpty()) {
            updateAABB(_positions)
        }
    }

    override val positions: FloatArray
        get(): FloatArray {
            return _positions
        }

    override fun transform(tr: Matrix4): Unit {
    }
}

// ============================================================================
// ======================= UnsupportedIntersectionMesh ========================
// ============================================================================

private class UnsupportedIntersectionMesh(positions: FloatArray) : Mesh("unsupportedIntersection") {
    init {
        updateAABB(positions)
    }

    override fun transform(tr: Matrix4): Unit {
    }
}

// ============================================================================
// ======================= IntersectionVisitorTest ============================
// ============================================================================

class IntersectionVisitorTest {
    @Test
    fun exactTriangleMissInsideNonEmptyAabbReturnsNull(): Unit {
        val drawable = SpinableDrawable(IntersectionTestMesh(intersectionTriangle(5.0f)))
        val visitor = IntersectionVisitor(
            intersectionRay(Vector3(0.9f, 0.9f, 0.0f))
        )

        drawable.accept(visitor)

        assertNull(visitor.result)
    }

    @Test
    fun nearestHitIsIndependentOfTraversalOrderAndSharedMeshIdentity(): Unit {
        val mesh = IntersectionTestMesh(intersectionTriangle(5.0f))
        val farther = SpinableDrawable(mesh)
        val nearer = SpinableDrawable(mesh)
        val ray = intersectionRay()
        nearer.modelMatrix = intersectionShaderOffset(0.0f, 0.0f, -2.0f)
        nearer.isFrustumCulled = true

        val forwardRoot = Group()
        forwardRoot.addChild(farther)
        forwardRoot.addChild(nearer)
        val forwardVisitor = IntersectionVisitor(ray)
        forwardRoot.accept(forwardVisitor)

        val forwardResult = forwardVisitor.result
        assertNotNull(forwardResult)
        assertSame(nearer, forwardResult.drawable)
        assertIntersectionClose(3.0f, forwardResult.distance)
        assertIntersectionPoint(Vector3(0.0f, 0.0f, 3.0f), forwardResult.position)

        val reverseRoot = Group()
        reverseRoot.addChild(nearer)
        reverseRoot.addChild(farther)
        val reverseVisitor = IntersectionVisitor(ray)
        reverseRoot.accept(reverseVisitor)

        val reverseResult = reverseVisitor.result
        assertNotNull(reverseResult)
        assertSame(nearer, reverseResult.drawable)
        assertIntersectionClose(3.0f, reverseResult.distance)

        val farOnlyRoot = Group()
        farOnlyRoot.addChild(farther)
        val farOnlyVisitor = IntersectionVisitor(ray)
        farOnlyRoot.accept(farOnlyVisitor)
        assertSame(farther, farOnlyVisitor.result?.drawable)
    }

    @Test
    fun usesTranslatedAndNonUniformlyScaledModelMatrices(): Unit {
        val drawable = SpinableDrawable(IntersectionTestMesh(intersectionTriangle(1.0f)))
        val visitor = IntersectionVisitor(
            intersectionRay(Vector3(2.0f, 0.5f, 0.0f))
        )
        drawable.modelMatrix = algebra.scale(2.0f, 3.0f, 1.0f).multiply(
            intersectionShaderOffset(1.0f, 2.0f, 4.0f)
        )

        drawable.accept(visitor)

        val result = visitor.result
        assertNotNull(result)
        assertIntersectionClose(5.0f, result.distance)
        assertIntersectionPoint(Vector3(2.0f, 0.5f, 5.0f), result.position)
    }

    @Test
    fun usesRotatedModelMatricesAndTransformedBroadPhaseBounds(): Unit {
        val drawable = SpinableDrawable(IntersectionTestMesh(intersectionTriangle(0.0f)))
        val visitor = IntersectionVisitor(
            intersectionRay(
                Vector3(0.5f, 2.0f, 0.5f),
                Vector3(0.0f, -1.0f, 0.0f)
            )
        )
        drawable.modelMatrix = algebra.rotation(Vector3(1.0f, 0.0f, 0.0f), 90.0f)

        drawable.accept(visitor)

        val result = visitor.result
        assertNotNull(result)
        assertIntersectionClose(2.0f, result.distance)
        assertIntersectionPoint(Vector3(0.5f, 0.0f, 0.5f), result.position)
    }

    @Test
    fun usesPostAnimationModelMatrixInsteadOfRebuildingLocalTransform(): Unit {
        val root = TransformGroup()
        val drawable = SpinableDrawable(IntersectionTestMesh(intersectionTriangle(1.0f)))
        root.matrix = algebra.offset(5.0f, 0.0f, 0.0f)
        drawable.origin = Vector3(1.0f, 2.0f, 3.0f)
        drawable.scale = Vector3(2.0f, 3.0f, 4.0f)
        root.addChild(drawable)
        root.accept(AnimationVisitor(1.0f))
        val visitor = IntersectionVisitor(
            intersectionRay(Vector3(7.0f, 0.5f, 0.0f))
        )

        root.accept(visitor)

        val result = visitor.result
        assertNotNull(result)
        assertIntersectionClose(7.0f, result.distance)
        assertIntersectionPoint(Vector3(7.0f, 0.5f, 7.0f), result.position)
    }

    @Test
    fun traversesNestedGroupsAndStateGroupsButNotHiddenSwitchBranches(): Unit {
        val mesh = IntersectionTestMesh(intersectionTriangle(5.0f))
        val visible = SpinableDrawable(mesh)
        val hidden = SpinableDrawable(mesh)
        val root = Group()
        val group = Group()
        val switch = Switch()
        val state = StateGroup()
        hidden.modelMatrix = intersectionShaderOffset(0.0f, 0.0f, -4.0f)

        state.addChild(visible)
        switch.addChild(true, state)
        switch.addChild(false, hidden)
        group.addChild(switch)
        group.addChild(Behavior())
        root.addChild(group)
        val visitor = IntersectionVisitor(intersectionRay())
        root.accept(visitor)

        val result = visitor.result
        assertNotNull(result)
        assertSame(visible, result.drawable)
        assertIntersectionClose(5.0f, result.distance)
    }

    @Test
    fun skipsMeshlessUnsupportedEmptyAndDegenerateGeometry(): Unit {
        val root = Group()
        val empty = SpinableDrawable(IntersectionTestMesh(floatArrayOf()))
        val degenerate = SpinableDrawable(IntersectionTestMesh(intersectionTriangle(5.0f)))
        degenerate.modelMatrix = algebra.scale(0.0f)
        root.addChild(SpinableDrawable())
        root.addChild(SpinableDrawable(UnsupportedIntersectionMesh(intersectionTriangle(5.0f))))
        root.addChild(empty)
        root.addChild(degenerate)
        val visitor = IntersectionVisitor(intersectionRay())

        root.accept(visitor)

        assertNull(visitor.result)
    }

    @Test
    fun reportsHitPositionDistanceAndZeroBasedTriangleIndex(): Unit {
        val positions = intersectionTriangle(8.0f) + intersectionTriangle(4.0f)
        val drawable = SpinableDrawable(IntersectionTestMesh(positions))
        val visitor = IntersectionVisitor(intersectionRay())

        drawable.accept(visitor)

        val result = visitor.result
        assertNotNull(result)
        assertEquals(1, result.triangleIndex)
        assertIntersectionClose(4.0f, result.distance)
        assertIntersectionPoint(Vector3(0.0f, 0.0f, 4.0f), result.position)
    }

    @Test
    fun doesNotMutateRayModelBoundsOrMeshAndResultPositionIsIndependent(): Unit {
        val mesh = IntersectionTestMesh(intersectionTriangle(1.0f))
        val drawable = SpinableDrawable(mesh)
        val ray = intersectionRay(Vector3(2.0f, 0.5f, 0.0f))
        drawable.modelMatrix = algebra.scale(2.0f, 3.0f, 1.0f).multiply(
            intersectionShaderOffset(1.0f, 2.0f, 4.0f)
        )

        val originalPositions = mesh.positions.copyOf()
        val originalModel = drawable.modelMatrix.data.copyOf()
        val originalMin = mesh.boundingBox.min.data.copyOf()
        val originalMax = mesh.boundingBox.max.data.copyOf()
        val originalEye = ray.eye.data.copyOf()
        val originalDirection = ray.direction.data.copyOf()
        val visitor = IntersectionVisitor(ray)
        drawable.accept(visitor)

        val result = visitor.result
        assertNotNull(result)
        assertContentEquals(originalPositions, mesh.positions)
        assertContentEquals(originalModel, drawable.modelMatrix.data)
        assertContentEquals(originalMin, mesh.boundingBox.min.data)
        assertContentEquals(originalMax, mesh.boundingBox.max.data)
        assertContentEquals(originalEye, ray.eye.data)
        assertContentEquals(originalDirection, ray.direction.data)

        val originalHit = result.position.data.copyOf()
        ray.eye.set(20.0f, 30.0f, 40.0f)
        ray.direction.set(1.0f, 0.0f, 0.0f)
        assertContentEquals(originalHit, result.position.data)
    }
}
