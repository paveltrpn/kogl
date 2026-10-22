package generator

import algebra.*
import kotlin.math.*
import kotlin.test.*

private const val _EPS = 1e-5f

private fun _assertClose(expected: Float, actual: Float) {
    assertTrue(
        kotlin.math.abs(expected - actual) <= _EPS,
        "expected $expected but was $actual"
    )
}

// ============================================================================
// ======================= ParametricMeshTest =================================
// ============================================================================
class ParametricMeshTest {

    @Test
    fun countsMatchSegments() {
        val mesh = ParametricMesh({ t: Vector2 ->
            val vertex = MeshVertex()
            vertex.mTexCoord = t
            vertex
        }, IntPair(3, 5))

        assertEquals(24, mesh.vertices().count())
        assertEquals(30, mesh.triangles().count())
    }

    @Test
    fun zeroSegmentsYieldEmptyMesh() {
        val mesh = ParametricMesh({ t: Vector2 ->
            val vertex = MeshVertex()
            vertex.mTexCoord = t
            vertex
        }, IntPair(0, 5))

        assertEquals(0, mesh.vertices().count())
        assertEquals(0, mesh.triangles().count())
    }

    @Test
    fun triangleOrderingMatchesCpp() {
        val mesh = ParametricMesh({ t: Vector2 ->
            val vertex = MeshVertex()
            vertex.mTexCoord = t
            vertex
        }, IntPair(2, 1))

        val triangles = mesh.triangles().toList()
        assertEquals(4, triangles.size)

        assertEquals(listOf(0, 1, 3), triangles[0].mVertices.toList())
        assertEquals(listOf(1, 4, 3), triangles[1].mVertices.toList())
        assertEquals(listOf(1, 2, 4), triangles[2].mVertices.toList())
        assertEquals(listOf(2, 5, 4), triangles[3].mVertices.toList())
    }
}

// ============================================================================
// ======================= SphereMeshTest =====================================
// ============================================================================
class SphereMeshTest {

    @Test
    fun counts() {
        val mesh = SphereMesh(mRadius = 1.0f, mSlices = 8, mSegments = 4)
        assertEquals(45, mesh.vertices().count())
        assertEquals(64, mesh.triangles().count())
    }

    @Test
    fun verticesOnSphereWithOutwardNormals() {
        val radius = 2.5f
        val mesh = SphereMesh(mRadius = radius, mSlices = 16, mSegments = 8)

        for (vertex in mesh.vertices()) {
            val p = vertex.mPosition
            _assertClose(radius, sqrt(p.x * p.x + p.y * p.y + p.z * p.z))

            val n = vertex.mNormal
            _assertClose(p.x / radius, n.x)
            _assertClose(p.y / radius, n.y)
            _assertClose(p.z / radius, n.z)
        }
    }
}

// ============================================================================
// ======================= CylinderMeshTest ===================================
// ============================================================================
class CylinderMeshTest {

    @Test
    fun radialDistanceConstant() {
        val radius = 1.5f
        val size = 2.0f
        val mesh = CylinderMesh(mRadius = radius, mSize = size, mSlices = 16, mSegments = 4)

        for (vertex in mesh.vertices()) {
            val p = vertex.mPosition
            _assertClose(radius, sqrt(p.x * p.x + p.y * p.y))
            assertTrue(kotlin.math.abs(p.z) <= size + _EPS)
        }
    }
}

// ============================================================================
// ======================= TorusMeshTest ======================================
// ============================================================================
class TorusMeshTest {

    @Test
    fun vertexDistanceFromMajorCircleIsMinor() {
        val minor = 0.3f
        val major = 1.2f
        val mesh = TorusMesh(mMinor = minor, mMajor = major, mSlices = 8, mSegments = 16)

        for (vertex in mesh.vertices()) {
            val p = vertex.mPosition
            val ringRadius = sqrt(p.x * p.x + p.y * p.y)
            val distance = sqrt((ringRadius - major) * (ringRadius - major) + p.z * p.z)
            _assertClose(minor, distance)
        }
    }
}

// ============================================================================
// ======================= BoxMeshTest ========================================
// ============================================================================
class BoxMeshTest {

    @Test
    fun counts() {
        val mesh = BoxMesh(mSize = Vector3(1.0f, 1.0f, 1.0f), mSegments = IntTriple(8, 8, 8))
        assertEquals(486, mesh.vertices().count())
        assertEquals(768, mesh.triangles().count())
    }

    @Test
    fun verticesWithinBounds() {
        val size = Vector3(1.0f, 2.0f, 3.0f)
        val mesh = BoxMesh(mSize = size, mSegments = IntTriple(4, 2, 3))

        for (vertex in mesh.vertices()) {
            val p = vertex.mPosition
            assertTrue(kotlin.math.abs(p.x) <= size.x + _EPS, "x out of bounds: $p")
            assertTrue(kotlin.math.abs(p.y) <= size.y + _EPS, "y out of bounds: $p")
            assertTrue(kotlin.math.abs(p.z) <= size.z + _EPS, "z out of bounds: $p")

            val onFaceX = kotlin.math.abs(kotlin.math.abs(p.x) - size.x) <= _EPS
            val onFaceY = kotlin.math.abs(kotlin.math.abs(p.y) - size.y) <= _EPS
            val onFaceZ = kotlin.math.abs(kotlin.math.abs(p.z) - size.z) <= _EPS
            assertTrue(onFaceX || onFaceY || onFaceZ, "vertex not on any face: $p")
        }
    }

    @Test
    fun sequencesAreCold() {
        val mesh = BoxMesh()
        assertEquals(mesh.vertices().count(), mesh.vertices().count())
        assertEquals(mesh.triangles().count(), mesh.triangles().count())
    }
}

// ============================================================================
// ======================= TubeMeshTest =======================================
// ============================================================================
class TubeMeshTest {

    @Test
    fun countsAreSumOfTwoCylinders() {
        val tube = TubeMesh(mRadius = 1.0f, mInnerRadius = 0.5f, mSlices = 8, mSegments = 2)
        val cylinder = CylinderMesh(mRadius = 1.0f, mSlices = 8, mSegments = 2)

        assertEquals(2 * cylinder.vertices().count(), tube.vertices().count())
        assertEquals(2 * cylinder.triangles().count(), tube.triangles().count())
    }

    @Test
    fun innerAndOuterRadiiPresent() {
        val outer = 1.0f
        val inner = 0.5f
        val tube = TubeMesh(mRadius = outer, mInnerRadius = inner, mSlices = 8, mSegments = 2)

        val radii = tube.vertices().map { vertex: MeshVertex ->
            sqrt(vertex.mPosition.x * vertex.mPosition.x + vertex.mPosition.y * vertex.mPosition.y)
        }.toSet()

        assertTrue(radii.any { r: Float -> kotlin.math.abs(r - outer) <= _EPS })
        assertTrue(radii.any { r: Float -> kotlin.math.abs(r - inner) <= _EPS })
        assertTrue(radii.all { r: Float ->
            kotlin.math.abs(r - outer) <= _EPS || kotlin.math.abs(r - inner) <= _EPS
        })
    }
}

// ============================================================================
// ======================= ConeMeshTest =======================================
// ============================================================================
class ConeMeshTest {

    @Test
    fun tipAtPositiveZ() {
        val size = 1.5f
        val mesh = ConeMesh(mRadius = 1.0f, mSize = size, mSlices = 8, mSegments = 4)

        val hasTip = mesh.vertices().any { vertex: MeshVertex ->
            kotlin.math.abs(vertex.mPosition.z - size) <= _EPS &&
                sqrt(vertex.mPosition.x * vertex.mPosition.x + vertex.mPosition.y * vertex.mPosition.y) <= _EPS
        }
        assertTrue(hasTip)
    }
}

// ============================================================================
// ======================= ObjAdapterTest =====================================
// ============================================================================
class ObjAdapterTest {

    @Test
    fun arraysAreConsistent() {
        val mesh = SphereMesh(mRadius = 1.0f, mSlices = 8, mSegments = 4)
        val obj = mesh.toObjMesh("sphere")

        assertEquals(192, obj.verticesCount)
        assertEquals(192 * 3, obj.vnormals.size)
        assertEquals(192 * 2, obj.txcoords.size)
        assertEquals(64, obj.triangles.size)

        for (triangle in obj.triangles) {
            assertEquals(3, triangle.vertexIndex.size)
            for (i in 0 until 3) {
                assertEquals(triangle.vertexIndex[i], triangle.normalIndex[i])
                assertEquals(triangle.vertexIndex[i], triangle.texCoordIndex[i])
                assertTrue(triangle.vertexIndex[i] in 0 until 192)
            }

            val base = triangle.normalIndex[0] * 3
            for (i in 1 until 3) {
                val corner = triangle.normalIndex[i] * 3
                for (c in 0 until 3) {
                    assertEquals(obj.vnormals[base + c], obj.vnormals[corner + c])
                }
            }
        }
    }
}

// ============================================================================
// ======================= LatheHandednessTest ================================
// ============================================================================
class LatheHandednessTest {

    @Test
    fun rotationIsCounterClockwiseAroundAxis() {
        val rotated = rotate(Vector3(1.0f, 0.0f, 0.0f), Vector3(1.0f, 0.0f, 0.0f), toRadians(90.0f))
        _assertClose(1.0f, rotated.x)
        _assertClose(0.0f, rotated.y)
        _assertClose(0.0f, rotated.z)

        val aroundZ = rotate(Vector3(1.0f, 0.0f, 0.0f), Vector3(0.0f, 0.0f, 1.0f), toRadians(90.0f))
        _assertClose(0.0f, aroundZ.x)
        _assertClose(1.0f, aroundZ.y)
        _assertClose(0.0f, aroundZ.z)
    }
}
