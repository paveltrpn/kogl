package generator

import algebra.*
import kotlin.math.*
import kotlin.test.*

private const val _EPS2 = 1e-5f

private fun _assertClose2(expected: Float, actual: Float) {
    assertTrue(
        kotlin.math.abs(expected - actual) <= _EPS2,
        "expected $expected but was $actual"
    )
}

// ============================================================================
// ======================= DiskMeshTest =======================================
// ============================================================================
class DiskMeshTest {

    @Test
    fun flatRingInXYPlane() {
        val radius = 2.0f
        val inner = 0.5f
        val mesh = DiskMesh(mRadius = radius, mInnerRadius = inner, mSlices = 16, mRings = 3)

        for (vertex in mesh.vertices()) {
            val p = vertex.mPosition
            val r = sqrt(p.x * p.x + p.y * p.y)
            assertTrue(r >= inner - _EPS2 && r <= radius + _EPS2, "radius $r out of [$inner, $radius]")
            _assertClose2(0.0f, p.z)
        }
    }

    @Test
    fun counts() {
        val mesh = DiskMesh(mRadius = 1.0f, mInnerRadius = 0.0f, mSlices = 8, mRings = 2)
        assertEquals(3 * 9, mesh.vertices().count())
        assertEquals(2 * 8 * 2, mesh.triangles().count())
    }
}

// ============================================================================
// ======================= CapsuleMeshTest ====================================
// ============================================================================
class CapsuleMeshTest {

    @Test
    fun verticesWithinBounds() {
        val radius = 0.5f
        val size = 1.0f
        val mesh = CapsuleMesh(mRadius = radius, mSize = size, mSlices = 8, mSegments = 2, mRings = 2)

        for (vertex in mesh.vertices()) {
            val p = vertex.mPosition
            val r = sqrt(p.x * p.x + p.y * p.y)
            assertTrue(r <= radius + _EPS2, "radial $r exceeds $radius")
            assertTrue(kotlin.math.abs(p.z) <= size + radius + _EPS2, "z out of bounds: $p")
        }
    }
}

// ============================================================================
// ======================= IcoSphereMeshTest ==================================
// ============================================================================
class IcoSphereMeshTest {

    @Test
    fun verticesOnSphere() {
        val radius = 1.5f
        val mesh = IcoSphereMesh(mRadius = radius, mSegments = 2)

        for (vertex in mesh.vertices()) {
            val p = vertex.mPosition
            _assertClose2(radius, sqrt(p.x * p.x + p.y * p.y + p.z * p.z))
        }
    }
}

// ============================================================================
// ======================= IcosahedronMeshTest ================================
// ============================================================================
class IcosahedronMeshTest {

    @Test
    fun baseCounts() {
        val mesh = IcosahedronMesh(mRadius = 1.0f, mSegments = 1)
        assertEquals(20 * 3, mesh.vertices().count())
        assertEquals(20, mesh.triangles().count())
    }
}

// ============================================================================
// ======================= DodecahedronMeshTest ===============================
// ============================================================================
class DodecahedronMeshTest {

    @Test
    fun verticesOnSphere() {
        val radius = 1.0f
        val mesh = DodecahedronMesh(mRadius = radius, mSegments = 1, mRings = 1)

        for (vertex in mesh.vertices()) {
            val p = vertex.mPosition
            assertTrue(sqrt(p.x * p.x + p.y * p.y + p.z * p.z) <= radius + _EPS2)
        }
    }
}

// ============================================================================
// ======================= RoundedBoxMeshTest =================================
// ============================================================================
class RoundedBoxMeshTest {

    @Test
    fun countsArePositiveAndConsistent() {
        val mesh = RoundedBoxMesh()
        val vertexCount = mesh.vertices().count()
        val triangleCount = mesh.triangles().count()
        assertTrue(vertexCount > 0)
        assertTrue(triangleCount > 0)

        for (triangle in mesh.triangles()) {
            for (i in 0 until 3) {
                assertTrue(triangle.mVertices[i] in 0 until vertexCount)
            }
        }
    }
}

// ============================================================================
// ======================= SphericalConeMeshTest ==============================
// ============================================================================
class SphericalConeMeshTest {

    @Test
    fun countsArePositiveAndConsistent() {
        val mesh = SphericalConeMesh(mRadius = 1.0f, mSize = 1.0f, mSlices = 8, mSegments = 2, mRings = 2)
        val vertexCount = mesh.vertices().count()
        assertTrue(vertexCount > 0)

        for (triangle in mesh.triangles()) {
            for (i in 0 until 3) {
                assertTrue(triangle.mVertices[i] in 0 until vertexCount)
            }
        }
    }
}

// ============================================================================
// ======================= MeshWrapperTest ====================================
// ============================================================================
class MeshWrapperTest {

    @Test
    fun repeatMeshOffsetsInstances() {
        val base = _PlaneMeshPublic(Vector2(1.0f, 1.0f), IntPair(1, 1))
        val mesh = RepeatMesh(base, 3, Vector3(2.0f, 0.0f, 0.0f))

        assertEquals(3 * base.vertices().count(), mesh.vertices().count())
        assertEquals(3 * base.triangles().count(), mesh.triangles().count())

        val xs = mesh.vertices().map { vertex: MeshVertex -> vertex.mPosition.x }.toList()
        assertTrue(xs.any { x: Float -> x > 3.0f })
    }

    @Test
    fun rotateMeshRotatesPositions() {
        val base = _PlaneMeshPublic(Vector2(1.0f, 1.0f), IntPair(1, 1))
        val mesh = RotateMesh(base, algebra.toRadians(90.0f), Vector3(0.0f, 0.0f, 1.0f))

        for (vertex in mesh.vertices()) {
            val p = vertex.mPosition
            _assertClose2(0.0f, p.z)
        }
    }

    @Test
    fun subdivideMeshSplitsTriangles() {
        val base = _PlaneMeshPublic(Vector2(1.0f, 1.0f), IntPair(1, 1))
        val mesh = SubdivideMesh(base)

        assertEquals(4 * base.triangles().count(), mesh.triangles().count())
    }

    @Test
    fun mergePathOffsetsEdges() {
        val path = LinePath(
            Vector3(0.0f, 0.0f, 0.0f),
            Vector3(1.0f, 0.0f, 0.0f),
            Vector3(1.0f, 0.0f, 0.0f),
            4
        )
        val merged = MergePath(path, path)

        assertEquals(2 * path.vertices().count(), merged.vertices().count())
        assertEquals(2 * path.edges().count(), merged.edges().count())

        val maxIndex = merged.edges().maxOf { edge: Edge -> edge.mVertices[1] }
        assertEquals(2 * path.vertices().count() - 1, maxIndex)
    }

    private fun _PlaneMeshPublic(size: Vector2, segments: IntPair): GeneratorMesh {
        return ParametricMesh({ t: Vector2 ->
            val vertex = MeshVertex()
            vertex.mPosition = Vector3(
                -size.x + 2.0f * t.x * size.x,
                -size.y + 2.0f * t.y * size.y,
                0.0f
            )
            vertex.mNormal = Vector3(0.0f, 0.0f, 1.0f)
            vertex.mTexCoord = t
            vertex
        }, segments)
    }
}

// ============================================================================
// ======================= ComposedMeshTest ===================================
// ============================================================================
class ComposedMeshTest {

    @Test
    fun extrudeMeshCounts() {
        val shape = CircleShape(mRadius = 0.5f, mSegments = 6)
        val path = LinePath(
            Vector3(0.0f, 0.0f, -1.0f),
            Vector3(0.0f, 0.0f, 1.0f),
            Vector3(1.0f, 0.0f, 0.0f),
            4
        )
        val mesh = ExtrudeMesh(shape, path)

        assertEquals(7 * 5, mesh.vertices().count())
        assertEquals(2 * 6 * 4, mesh.triangles().count())
    }

    @Test
    fun torusKnotIndicesInRange() {
        val mesh = TorusKnotMesh(mP = 2, mQ = 3, mSlices = 6, mSegments = 24)
        val vertexCount = mesh.vertices().count()
        assertTrue(vertexCount > 0)

        for (triangle in mesh.triangles()) {
            for (i in 0 until 3) {
                assertTrue(triangle.mVertices[i] in 0 until vertexCount)
            }
        }
    }

    @Test
    fun springMeshCounts() {
        val mesh = SpringMesh(mMinor = 0.1f, mMajor = 0.5f, mSize = 2.0f, mSlices = 4, mSegments = 8)
        assertEquals(5 * 9, mesh.vertices().count())
        assertEquals(2 * 4 * 8, mesh.triangles().count())
    }

    @Test
    fun cappedCylinderClosedBounds() {
        val radius = 1.0f
        val size = 1.0f
        val mesh = CappedCylinderMesh(mRadius = radius, mSize = size, mSlices = 8, mSegments = 2, mRings = 2)

        var minZ = Float.MAX_VALUE
        var maxZ = -Float.MAX_VALUE
        for (vertex in mesh.vertices()) {
            val p = vertex.mPosition
            val r = sqrt(p.x * p.x + p.y * p.y)
            assertTrue(r <= radius + _EPS2, "radial $r exceeds $radius")
            minZ = minOf(minZ, p.z)
            maxZ = maxOf(maxZ, p.z)
        }
        _assertClose2(-size, minZ)
        _assertClose2(size, maxZ)
    }

    @Test
    fun cappedTubeHasBothRadiiAndCaps() {
        val mesh = CappedTubeMesh(
            mRadius = 1.0f, mInnerRadius = 0.5f, mSize = 1.0f,
            mSlices = 8, mSegments = 2, mRings = 2
        )
        val vertexCount = mesh.vertices().count()
        assertTrue(vertexCount > 0)

        for (triangle in mesh.triangles()) {
            for (i in 0 until 3) {
                assertTrue(triangle.mVertices[i] in 0 until vertexCount)
            }
        }

        val zs = mesh.vertices().map { vertex: MeshVertex -> vertex.mPosition.z }
        _assertClose2(-1.0f, zs.min())
        _assertClose2(1.0f, zs.max())
    }

    @Test
    fun cappedConeClosedBottom() {
        val mesh = CappedConeMesh(mRadius = 1.0f, mSize = 1.0f, mSlices = 8, mSegments = 2, mRings = 2)
        val zs = mesh.vertices().map { vertex: MeshVertex -> vertex.mPosition.z }
        _assertClose2(-1.0f, zs.min())
        assertTrue(zs.max() > 0.9f)
    }

    @Test
    fun teapotCounts() {
        val segments = 4
        val mesh = TeapotMesh(mSegments = segments)
        assertEquals(32 * (segments + 1) * (segments + 1), mesh.vertices().count())
        assertEquals(32 * 2 * segments * segments, mesh.triangles().count())
    }

    @Test
    fun teapotIndicesInRange() {
        val mesh = TeapotMesh(mSegments = 4)
        val vertexCount = mesh.vertices().count()
        for (triangle in mesh.triangles()) {
            for (i in 0 until 3) {
                assertTrue(triangle.mVertices[i] in 0 until vertexCount)
            }
        }
    }

    @Test
    fun bezierMeshFlatPatch() {
        val controlPoints = arrayOf(
            arrayOf(Vector3(0.0f, 0.0f, 0.0f), Vector3(0.0f, 1.0f, 0.0f)),
            arrayOf(Vector3(1.0f, 0.0f, 0.0f), Vector3(1.0f, 1.0f, 0.0f))
        )
        val mesh = BezierMesh(controlPoints, IntPair(4, 4))
        assertEquals(25, mesh.vertices().count())
        assertEquals(32, mesh.triangles().count())

        for (vertex in mesh.vertices()) {
            _assertClose2(0.0f, vertex.mPosition.z)
        }
    }
}

// ============================================================================
// ======================= ShapeWrapperTest ===================================
// ============================================================================
class ShapeWrapperTest {

    @Test
    fun rectangleShapeCounts() {
        val shape = RectangleShape(Vector2(1.0f, 2.0f), IntPair(2, 3))
        assertEquals(2 * (2 + 1) + 2 * (3 + 1), shape.vertices().count())
        assertEquals(2 * 2 + 2 * 3, shape.edges().count())
    }

    @Test
    fun circleSubdivideDoublesEdges() {
        val circle = CircleShape(mRadius = 1.0f, mSegments = 8)
        val subdivided = SubdivideShape(circle)

        assertEquals(2 * circle.edges().count(), subdivided.edges().count())
        assertEquals(circle.vertices().count() + circle.edges().count(), subdivided.vertices().count())
    }
}
