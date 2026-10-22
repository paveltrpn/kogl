package generator

import algebra.Vector3

class DodecahedronMesh(
    mRadius: Float = 1.0f,
    mSegments: Int = 1,
    mRings: Int = 1
) : GeneratorMesh {

    private val _radius = mRadius
    private val _segments = mSegments
    private val _rings = mRings
    private val _faceVertexCount = ConvexPolygonMesh(1.0f, 5, mSegments, mRings).vertices().count()

    override fun vertices(): Sequence<MeshVertex> = sequence {
        for (faceIndex in _POLYGONS.indices) {
            val faceMesh = ConvexPolygonMesh(makeVertices(faceIndex), _segments, _rings)
            for (vertex in faceMesh.vertices()) {
                vertex.mPosition = scale(vertex.mPosition, _radius)
                yield(vertex)
            }
        }
    }

    override fun triangles(): Sequence<Triangle> = sequence {
        for (faceIndex in _POLYGONS.indices) {
            val faceMesh = ConvexPolygonMesh(makeVertices(faceIndex), _segments, _rings)
            val base = faceIndex * _faceVertexCount
            for (triangle in faceMesh.triangles()) {
                yield(Triangle(
                    triangle.mVertices[0] + base,
                    triangle.mVertices[1] + base,
                    triangle.mVertices[2] + base
                ))
            }
        }
    }

    private companion object {

        private val _A: Float = ((1.0 + kotlin.math.sqrt(5.0)) / 2.0).toFloat()

        private val _VERTICES: Array<Vector3> = arrayOf(
            Vector3(1.0f, 1.0f, -1.0f),
            Vector3(-1.0f, 1.0f, -1.0f),
            Vector3(1.0f, -1.0f, -1.0f),
            Vector3(-1.0f, -1.0f, -1.0f),
            Vector3(1.0f, 1.0f, 1.0f),
            Vector3(-1.0f, 1.0f, 1.0f),
            Vector3(1.0f, -1.0f, 1.0f),
            Vector3(-1.0f, -1.0f, 1.0f),

            Vector3(0.0f, 1.0f / _A, -_A),
            Vector3(0.0f, -1.0f / _A, -_A),
            Vector3(0.0f, 1.0f / _A, _A),
            Vector3(0.0f, -1.0f / _A, _A),

            Vector3(1.0f / _A, _A, 0.0f),
            Vector3(-1.0f / _A, _A, 0.0f),
            Vector3(1.0f / _A, -_A, 0.0f),
            Vector3(-1.0f / _A, -_A, 0.0f),

            Vector3(_A, 0.0f, -1.0f / _A),
            Vector3(-_A, 0.0f, -1.0f / _A),
            Vector3(_A, 0.0f, 1.0f / _A),
            Vector3(-_A, 0.0f, 1.0f / _A)
        )

        private val _POLYGONS: Array<IntArray> = arrayOf(
            intArrayOf(0, 12, 4, 18, 16),
            intArrayOf(18, 6, 14, 2, 16),
            intArrayOf(4, 10, 11, 6, 18),
            intArrayOf(11, 7, 15, 14, 6),
            intArrayOf(7, 11, 10, 5, 19),
            intArrayOf(17, 3, 15, 7, 19),
            intArrayOf(9, 2, 14, 15, 3),
            intArrayOf(1, 8, 9, 3, 17),
            intArrayOf(10, 4, 12, 13, 5),
            intArrayOf(5, 13, 1, 17, 19),
            intArrayOf(0, 8, 1, 13, 12),
            intArrayOf(0, 16, 2, 9, 8)
        )

        private fun makeVertices(faceIndex: Int): List<Vector3> {
            val result = ArrayList<Vector3>(5)
            for (i in 0 until 5) {
                result.add(normalize(_VERTICES[_POLYGONS[faceIndex][i]]))
            }
            return result
        }
    }
}
