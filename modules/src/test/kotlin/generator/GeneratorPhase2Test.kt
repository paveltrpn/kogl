package generator

import algebra.Vector2
import algebra.Vector3
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val EPS2 = 1e-5f

private fun assertClose2(expected: Float, actual: Float) {
    assertTrue(
        kotlin.math.abs(expected - actual) <= EPS2,
        "expected $expected but was $actual"
    )
}

class DiskMeshTest {

    @Test
    fun flatRingInXYPlane() {
        val radius = 2.0f
        val inner = 0.5f
        val mesh = DiskMesh(mRadius = radius, mInnerRadius = inner, mSlices = 16, mRings = 3)

        for (vertex in mesh.vertices()) {
            val p = vertex.mPosition
            val r = sqrt(p.x * p.x + p.y * p.y)
            assertTrue(r >= inner - EPS2 && r <= radius + EPS2, "radius $r out of [$inner, $radius]")
            assertClose2(0.0f, p.z)
        }
    }

    @Test
    fun counts() {
        val mesh = DiskMesh(mRadius = 1.0f, mInnerRadius = 0.0f, mSlices = 8, mRings = 2)
        assertEquals(3 * 9, mesh.vertices().count())
        assertEquals(2 * 8 * 2, mesh.triangles().count())
    }
}

class CapsuleMeshTest {

    @Test
    fun verticesWithinBounds() {
        val radius = 0.5f
        val size = 1.0f
        val mesh = CapsuleMesh(mRadius = radius, mSize = size, mSlices = 8, mSegments = 2, mRings = 2)

        for (vertex in mesh.vertices()) {
            val p = vertex.mPosition
            val r = sqrt(p.x * p.x + p.y * p.y)
            assertTrue(r <= radius + EPS2, "radial $r exceeds $radius")
            assertTrue(kotlin.math.abs(p.z) <= size + radius + EPS2, "z out of bounds: $p")
        }
    }
}

class IcoSphereMeshTest {

    @Test
    fun verticesOnSphere() {
        val radius = 1.5f
        val mesh = IcoSphereMesh(mRadius = radius, mSegments = 2)

        for (vertex in mesh.vertices()) {
            val p = vertex.mPosition
            assertClose2(radius, sqrt(p.x * p.x + p.y * p.y + p.z * p.z))
        }
    }
}

class IcosahedronMeshTest {

    @Test
    fun baseCounts() {
        val mesh = IcosahedronMesh(mRadius = 1.0f, mSegments = 1)
        assertEquals(20 * 3, mesh.vertices().count())
        assertEquals(20, mesh.triangles().count())
    }
}

class DodecahedronMeshTest {

    @Test
    fun verticesOnSphere() {
        val radius = 1.0f
        val mesh = DodecahedronMesh(mRadius = radius, mSegments = 1, mRings = 1)

        for (vertex in mesh.vertices()) {
            val p = vertex.mPosition
            assertTrue(sqrt(p.x * p.x + p.y * p.y + p.z * p.z) <= radius + EPS2)
        }
    }
}

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

class MeshWrapperTest {

    @Test
    fun repeatMeshOffsetsInstances() {
        val base = PlaneMeshPublic(Vector2(1.0f, 1.0f), IntPair(1, 1))
        val mesh = RepeatMesh(base, 3, Vector3(2.0f, 0.0f, 0.0f))

        assertEquals(3 * base.vertices().count(), mesh.vertices().count())
        assertEquals(3 * base.triangles().count(), mesh.triangles().count())

        val xs = mesh.vertices().map { vertex -> vertex.mPosition.x }.toList()
        assertTrue(xs.any { x -> x > 3.0f })
    }

    @Test
    fun rotateMeshRotatesPositions() {
        val base = PlaneMeshPublic(Vector2(1.0f, 1.0f), IntPair(1, 1))
        val mesh = RotateMesh(base, algebra.toRadians(90.0f), Vector3(0.0f, 0.0f, 1.0f))

        for (vertex in mesh.vertices()) {
            val p = vertex.mPosition
            assertClose2(0.0f, p.z)
        }
    }

    @Test
    fun subdivideMeshSplitsTriangles() {
        val base = PlaneMeshPublic(Vector2(1.0f, 1.0f), IntPair(1, 1))
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

        val maxIndex = merged.edges().maxOf { edge -> edge.mVertices[1] }
        assertEquals(2 * path.vertices().count() - 1, maxIndex)
    }

    private fun PlaneMeshPublic(size: Vector2, segments: IntPair): GeneratorMesh {
        return ParametricMesh({ t ->
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
            assertTrue(r <= radius + EPS2, "radial $r exceeds $radius")
            minZ = minOf(minZ, p.z)
            maxZ = maxOf(maxZ, p.z)
        }
        assertClose2(-size, minZ)
        assertClose2(size, maxZ)
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

        val zs = mesh.vertices().map { vertex -> vertex.mPosition.z }
        assertClose2(-1.0f, zs.min())
        assertClose2(1.0f, zs.max())
    }

    @Test
    fun cappedConeClosedBottom() {
        val mesh = CappedConeMesh(mRadius = 1.0f, mSize = 1.0f, mSlices = 8, mSegments = 2, mRings = 2)
        val zs = mesh.vertices().map { vertex -> vertex.mPosition.z }
        assertClose2(-1.0f, zs.min())
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
            assertClose2(0.0f, vertex.mPosition.z)
        }
    }
}

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
