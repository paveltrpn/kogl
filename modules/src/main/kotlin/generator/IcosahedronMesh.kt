package generator

import algebra.Vector3

class IcosahedronMesh(
    mRadius: Float = 1.0f,
    mSegments: Int = 1
) : GeneratorMesh {

    private val _radius = mRadius
    private val _segments = mSegments
    private val _faceVertexCount = TriangleMesh(1.0f, mSegments).vertices().count()

    override fun vertices(): Sequence<MeshVertex> = sequence {
        for (i in _TRIANGLES.indices) {
            val face = _TRIANGLES[i]
            val faceMesh = TriangleMesh(_VERTICES[face[0]], _VERTICES[face[1]], _VERTICES[face[2]], _segments)
            for (vertex in faceMesh.vertices()) {
                vertex.mPosition = scale(vertex.mPosition, _radius)
                yield(vertex)
            }
        }
    }

    override fun triangles(): Sequence<Triangle> = sequence {
        for (i in _TRIANGLES.indices) {
            val face = _TRIANGLES[i]
            val faceMesh = TriangleMesh(_VERTICES[face[0]], _VERTICES[face[1]], _VERTICES[face[2]], _segments)
            val base = i * _faceVertexCount
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
        private val _D: Float = kotlin.math.sqrt(1.0f + _A * _A)
        private val _X: Float = 1.0f / _D
        private val _Y: Float = _A / _D

        private val _VERTICES: Array<Vector3> = arrayOf(
            Vector3(-_X, _Y, 0.0f),
            Vector3(_X, _Y, 0.0f),
            Vector3(-_X, -_Y, 0.0f),
            Vector3(_X, -_Y, 0.0f),
            Vector3(0.0f, -_X, _Y),
            Vector3(0.0f, _X, _Y),
            Vector3(0.0f, -_X, -_Y),
            Vector3(0.0f, _X, -_Y),
            Vector3(_Y, 0.0f, -_X),
            Vector3(_Y, 0.0f, _X),
            Vector3(-_Y, 0.0f, -_X),
            Vector3(-_Y, 0.0f, _X)
        )

        private val _TRIANGLES: Array<IntArray> = arrayOf(
            intArrayOf(0, 11, 5),
            intArrayOf(0, 5, 1),
            intArrayOf(0, 1, 7),
            intArrayOf(0, 7, 10),
            intArrayOf(0, 10, 11),
            intArrayOf(1, 5, 9),
            intArrayOf(5, 11, 4),
            intArrayOf(11, 10, 2),
            intArrayOf(10, 7, 6),
            intArrayOf(7, 1, 8),
            intArrayOf(3, 9, 4),
            intArrayOf(3, 4, 2),
            intArrayOf(3, 2, 6),
            intArrayOf(3, 6, 8),
            intArrayOf(3, 8, 9),
            intArrayOf(4, 9, 5),
            intArrayOf(2, 4, 11),
            intArrayOf(6, 2, 10),
            intArrayOf(8, 6, 7),
            intArrayOf(9, 8, 1)
        )
    }
}
