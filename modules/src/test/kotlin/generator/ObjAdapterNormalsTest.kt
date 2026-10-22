package generator

import algebra.*
import kotlin.test.*

private fun _vec(a: FloatArray, i: Int): Vector3 {
    return Vector3(a[i * 3], a[i * 3 + 1], a[i * 3 + 2])
}

private fun _dot(a: Vector3, b: Vector3): Float {
    return a.x * b.x + a.y * b.y + a.z * b.z
}

// ============================================================================
// ======================= ObjAdapterNormalsTest ==============================
// ============================================================================
class ObjAdapterNormalsTest {

    @Test
    fun icoSphereHasFlatOutwardUnitNormals() {
        val mesh = IcoSphereMesh(1.0f, 1).toObjMesh("rock")

        assertEquals(mesh.triangles.size * 3, mesh.verticesCount)

        for (i in 0 until mesh.verticesCount) {
            val p = _vec(mesh.vertices, i)
            val n = _vec(mesh.vnormals, i)

            val len = n.length()
            assertTrue(len > 0.99f && len < 1.01f, "normal not unit: $len")

            val plen = p.length()
            assertTrue(plen > 0.0f)
            val d = _dot(n, p.scale(1.0f / plen))
            assertTrue(d > 0.5f, "normal not outward: dot=$d")
        }
    }

    @Test
    fun boxHasSixFlatAxisNormals() {
        val mesh = BoxMesh(Vector3(14.0f, 0.4f, 2.0f)).toObjMesh("box")

        val distinct = HashSet<String>()
        for (i in 0 until mesh.verticesCount) {
            val n = _vec(mesh.vnormals, i)
            val len = n.length()
            assertTrue(len > 0.99f && len < 1.01f, "normal not unit: $len")
            val nx = if (kotlin.math.abs(n.x) < 1e-6f) 0.0f else n.x
            val ny = if (kotlin.math.abs(n.y) < 1e-6f) 0.0f else n.y
            val nz = if (kotlin.math.abs(n.z) < 1e-6f) 0.0f else n.z
            distinct.add("$nx,$ny,$nz")
        }

        assertEquals(6, distinct.size)
    }

    @Test
    fun icosahedronSegmentsZeroYieldsNoTriangles() {
        val mesh = IcosahedronMesh(1.0f, 0).toObjMesh("crystal")
        assertEquals(0, mesh.triangles.size)

        val meshSeg1 = IcosahedronMesh(1.0f, 1).toObjMesh("crystal1")
        assertEquals(20, meshSeg1.triangles.size)
    }
}
