package generator

import algebra.Vector3

class TeapotMesh(
    mSegments: Int = 8
) : GeneratorMesh {

    private val _segments: Int = mSegments

    private val _patchVertexCount: Int =
        BezierMesh(_teapotData[0], IntPair(_segments, _segments)).vertices().count()

    override fun vertices(): Sequence<MeshVertex> = sequence {
        for (index in 0 until _teapotData.size) {
            val patch = BezierMesh(_teapotData[index], IntPair(_segments, _segments))
            for (vertex in patch.vertices()) {
                yield(vertex)
            }
        }
    }

    override fun triangles(): Sequence<Triangle> = sequence {
        for (index in 0 until _teapotData.size) {
            val patch = BezierMesh(_teapotData[index], IntPair(_segments, _segments))
            val base = index * _patchVertexCount
            for (triangle in patch.triangles()) {
                yield(Triangle(
                    triangle.mVertices[0] + base,
                    triangle.mVertices[1] + base,
                    triangle.mVertices[2] + base
                ))
            }
        }
    }

    private companion object {

    private val _teapotData: Array<Array<Array<Vector3>>> = arrayOf(
        arrayOf(
            arrayOf(Vector3(1.40f, 0.00f, 2.40f), Vector3(1.40f, -0.78f, 2.40f), Vector3(0.78f, -1.40f, 2.40f), Vector3(0.00f, -1.40f, 2.40f)),
            arrayOf(Vector3(1.34f, 0.00f, 2.53f), Vector3(1.34f, -0.75f, 2.53f), Vector3(0.75f, -1.34f, 2.53f), Vector3(0.00f, -1.34f, 2.53f)),
            arrayOf(Vector3(1.44f, 0.00f, 2.53f), Vector3(1.44f, -0.81f, 2.53f), Vector3(0.81f, -1.44f, 2.53f), Vector3(0.00f, -1.44f, 2.53f)),
            arrayOf(Vector3(1.50f, 0.00f, 2.40f), Vector3(1.50f, -0.84f, 2.40f), Vector3(0.84f, -1.50f, 2.40f), Vector3(0.00f, -1.50f, 2.40f))
        ),
        arrayOf(
            arrayOf(Vector3(0.00f, -1.40f, 2.40f), Vector3(-0.78f, -1.40f, 2.40f), Vector3(-1.40f, -0.78f, 2.40f), Vector3(-1.40f, 0.00f, 2.40f)),
            arrayOf(Vector3(0.00f, -1.34f, 2.53f), Vector3(-0.75f, -1.34f, 2.53f), Vector3(-1.34f, -0.75f, 2.53f), Vector3(-1.34f, 0.00f, 2.53f)),
            arrayOf(Vector3(0.00f, -1.44f, 2.53f), Vector3(-0.81f, -1.44f, 2.53f), Vector3(-1.44f, -0.81f, 2.53f), Vector3(-1.44f, 0.00f, 2.53f)),
            arrayOf(Vector3(0.00f, -1.50f, 2.40f), Vector3(-0.84f, -1.50f, 2.40f), Vector3(-1.50f, -0.84f, 2.40f), Vector3(-1.50f, 0.00f, 2.40f))
        ),
        arrayOf(
            arrayOf(Vector3(-1.40f, 0.00f, 2.40f), Vector3(-1.40f, 0.78f, 2.40f), Vector3(-0.78f, 1.40f, 2.40f), Vector3(0.00f, 1.40f, 2.40f)),
            arrayOf(Vector3(-1.34f, 0.00f, 2.53f), Vector3(-1.34f, 0.75f, 2.53f), Vector3(-0.75f, 1.34f, 2.53f), Vector3(0.00f, 1.34f, 2.53f)),
            arrayOf(Vector3(-1.44f, 0.00f, 2.53f), Vector3(-1.44f, 0.81f, 2.53f), Vector3(-0.81f, 1.44f, 2.53f), Vector3(0.00f, 1.44f, 2.53f)),
            arrayOf(Vector3(-1.50f, 0.00f, 2.40f), Vector3(-1.50f, 0.84f, 2.40f), Vector3(-0.84f, 1.50f, 2.40f), Vector3(0.00f, 1.50f, 2.40f))
        ),
        arrayOf(
            arrayOf(Vector3(0.00f, 1.40f, 2.40f), Vector3(0.78f, 1.40f, 2.40f), Vector3(1.40f, 0.78f, 2.40f), Vector3(1.40f, 0.00f, 2.40f)),
            arrayOf(Vector3(0.00f, 1.34f, 2.53f), Vector3(0.75f, 1.34f, 2.53f), Vector3(1.34f, 0.75f, 2.53f), Vector3(1.34f, 0.00f, 2.53f)),
            arrayOf(Vector3(0.00f, 1.44f, 2.53f), Vector3(0.81f, 1.44f, 2.53f), Vector3(1.44f, 0.81f, 2.53f), Vector3(1.44f, 0.00f, 2.53f)),
            arrayOf(Vector3(0.00f, 1.50f, 2.40f), Vector3(0.84f, 1.50f, 2.40f), Vector3(1.50f, 0.84f, 2.40f), Vector3(1.50f, 0.00f, 2.40f))
        ),
        arrayOf(
            arrayOf(Vector3(1.50f, 0.00f, 2.40f), Vector3(1.50f, -0.84f, 2.40f), Vector3(0.84f, -1.50f, 2.40f), Vector3(0.00f, -1.50f, 2.40f)),
            arrayOf(Vector3(1.75f, 0.00f, 1.88f), Vector3(1.75f, -0.98f, 1.88f), Vector3(0.98f, -1.75f, 1.88f), Vector3(0.00f, -1.75f, 1.88f)),
            arrayOf(Vector3(2.00f, 0.00f, 1.35f), Vector3(2.00f, -1.12f, 1.35f), Vector3(1.12f, -2.00f, 1.35f), Vector3(0.00f, -2.00f, 1.35f)),
            arrayOf(Vector3(2.00f, 0.00f, 0.90f), Vector3(2.00f, -1.12f, 0.90f), Vector3(1.12f, -2.00f, 0.90f), Vector3(0.00f, -2.00f, 0.90f))
        ),
        arrayOf(
            arrayOf(Vector3(0.00f, -1.50f, 2.40f), Vector3(-0.84f, -1.50f, 2.40f), Vector3(-1.50f, -0.84f, 2.40f), Vector3(-1.50f, 0.00f, 2.40f)),
            arrayOf(Vector3(0.00f, -1.75f, 1.88f), Vector3(-0.98f, -1.75f, 1.88f), Vector3(-1.75f, -0.98f, 1.88f), Vector3(-1.75f, 0.00f, 1.88f)),
            arrayOf(Vector3(0.00f, -2.00f, 1.35f), Vector3(-1.12f, -2.00f, 1.35f), Vector3(-2.00f, -1.12f, 1.35f), Vector3(-2.00f, 0.00f, 1.35f)),
            arrayOf(Vector3(0.00f, -2.00f, 0.90f), Vector3(-1.12f, -2.00f, 0.90f), Vector3(-2.00f, -1.12f, 0.90f), Vector3(-2.00f, 0.00f, 0.90f))
        ),
        arrayOf(
            arrayOf(Vector3(-1.50f, 0.00f, 2.40f), Vector3(-1.50f, 0.84f, 2.40f), Vector3(-0.84f, 1.50f, 2.40f), Vector3(0.00f, 1.50f, 2.40f)),
            arrayOf(Vector3(-1.75f, 0.00f, 1.88f), Vector3(-1.75f, 0.98f, 1.88f), Vector3(-0.98f, 1.75f, 1.88f), Vector3(0.00f, 1.75f, 1.88f)),
            arrayOf(Vector3(-2.00f, 0.00f, 1.35f), Vector3(-2.00f, 1.12f, 1.35f), Vector3(-1.12f, 2.00f, 1.35f), Vector3(0.00f, 2.00f, 1.35f)),
            arrayOf(Vector3(-2.00f, 0.00f, 0.90f), Vector3(-2.00f, 1.12f, 0.90f), Vector3(-1.12f, 2.00f, 0.90f), Vector3(0.00f, 2.00f, 0.90f))
        ),
        arrayOf(
            arrayOf(Vector3(0.00f, 1.50f, 2.40f), Vector3(0.84f, 1.50f, 2.40f), Vector3(1.50f, 0.84f, 2.40f), Vector3(1.50f, 0.00f, 2.40f)),
            arrayOf(Vector3(0.00f, 1.75f, 1.88f), Vector3(0.98f, 1.75f, 1.88f), Vector3(1.75f, 0.98f, 1.88f), Vector3(1.75f, 0.00f, 1.88f)),
            arrayOf(Vector3(0.00f, 2.00f, 1.35f), Vector3(1.12f, 2.00f, 1.35f), Vector3(2.00f, 1.12f, 1.35f), Vector3(2.00f, 0.00f, 1.35f)),
            arrayOf(Vector3(0.00f, 2.00f, 0.90f), Vector3(1.12f, 2.00f, 0.90f), Vector3(2.00f, 1.12f, 0.90f), Vector3(2.00f, 0.00f, 0.90f))
        ),
        arrayOf(
            arrayOf(Vector3(2.00f, 0.00f, 0.90f), Vector3(2.00f, -1.12f, 0.90f), Vector3(1.12f, -2.00f, 0.90f), Vector3(0.00f, -2.00f, 0.90f)),
            arrayOf(Vector3(2.00f, 0.00f, 0.45f), Vector3(2.00f, -1.12f, 0.45f), Vector3(1.12f, -2.00f, 0.45f), Vector3(0.00f, -2.00f, 0.45f)),
            arrayOf(Vector3(1.50f, 0.00f, 0.23f), Vector3(1.50f, -0.84f, 0.23f), Vector3(0.84f, -1.50f, 0.23f), Vector3(0.00f, -1.50f, 0.23f)),
            arrayOf(Vector3(1.50f, 0.00f, 0.15f), Vector3(1.50f, -0.84f, 0.15f), Vector3(0.84f, -1.50f, 0.15f), Vector3(0.00f, -1.50f, 0.15f))
        ),
        arrayOf(
            arrayOf(Vector3(0.00f, -2.00f, 0.90f), Vector3(-1.12f, -2.00f, 0.90f), Vector3(-2.00f, -1.12f, 0.90f), Vector3(-2.00f, 0.00f, 0.90f)),
            arrayOf(Vector3(0.00f, -2.00f, 0.45f), Vector3(-1.12f, -2.00f, 0.45f), Vector3(-2.00f, -1.12f, 0.45f), Vector3(-2.00f, 0.00f, 0.45f)),
            arrayOf(Vector3(0.00f, -1.50f, 0.23f), Vector3(-0.84f, -1.50f, 0.23f), Vector3(-1.50f, -0.84f, 0.23f), Vector3(-1.50f, 0.00f, 0.23f)),
            arrayOf(Vector3(0.00f, -1.50f, 0.15f), Vector3(-0.84f, -1.50f, 0.15f), Vector3(-1.50f, -0.84f, 0.15f), Vector3(-1.50f, 0.00f, 0.15f))
        ),
        arrayOf(
            arrayOf(Vector3(-2.00f, 0.00f, 0.90f), Vector3(-2.00f, 1.12f, 0.90f), Vector3(-1.12f, 2.00f, 0.90f), Vector3(0.00f, 2.00f, 0.90f)),
            arrayOf(Vector3(-2.00f, 0.00f, 0.45f), Vector3(-2.00f, 1.12f, 0.45f), Vector3(-1.12f, 2.00f, 0.45f), Vector3(0.00f, 2.00f, 0.45f)),
            arrayOf(Vector3(-1.50f, 0.00f, 0.23f), Vector3(-1.50f, 0.84f, 0.23f), Vector3(-0.84f, 1.50f, 0.23f), Vector3(0.00f, 1.50f, 0.23f)),
            arrayOf(Vector3(-1.50f, 0.00f, 0.15f), Vector3(-1.50f, 0.84f, 0.15f), Vector3(-0.84f, 1.50f, 0.15f), Vector3(0.00f, 1.50f, 0.15f))
        ),
        arrayOf(
            arrayOf(Vector3(0.00f, 2.00f, 0.90f), Vector3(1.12f, 2.00f, 0.90f), Vector3(2.00f, 1.12f, 0.90f), Vector3(2.00f, 0.00f, 0.90f)),
            arrayOf(Vector3(0.00f, 2.00f, 0.45f), Vector3(1.12f, 2.00f, 0.45f), Vector3(2.00f, 1.12f, 0.45f), Vector3(2.00f, 0.00f, 0.45f)),
            arrayOf(Vector3(0.00f, 1.50f, 0.23f), Vector3(0.84f, 1.50f, 0.23f), Vector3(1.50f, 0.84f, 0.23f), Vector3(1.50f, 0.00f, 0.23f)),
            arrayOf(Vector3(0.00f, 1.50f, 0.15f), Vector3(0.84f, 1.50f, 0.15f), Vector3(1.50f, 0.84f, 0.15f), Vector3(1.50f, 0.00f, 0.15f))
        ),
        arrayOf(
            arrayOf(Vector3(-1.60f, 0.00f, 2.03f), Vector3(-1.60f, -0.30f, 2.03f), Vector3(-1.50f, -0.30f, 2.25f), Vector3(-1.50f, 0.00f, 2.25f)),
            arrayOf(Vector3(-2.30f, 0.00f, 2.03f), Vector3(-2.30f, -0.30f, 2.03f), Vector3(-2.50f, -0.30f, 2.25f), Vector3(-2.50f, 0.00f, 2.25f)),
            arrayOf(Vector3(-2.70f, 0.00f, 2.03f), Vector3(-2.70f, -0.30f, 2.03f), Vector3(-3.00f, -0.30f, 2.25f), Vector3(-3.00f, 0.00f, 2.25f)),
            arrayOf(Vector3(-2.70f, 0.00f, 1.80f), Vector3(-2.70f, -0.30f, 1.80f), Vector3(-3.00f, -0.30f, 1.80f), Vector3(-3.00f, 0.00f, 1.80f))
        ),
        arrayOf(
            arrayOf(Vector3(-1.50f, 0.00f, 2.25f), Vector3(-1.50f, 0.30f, 2.25f), Vector3(-1.60f, 0.30f, 2.03f), Vector3(-1.60f, 0.00f, 2.03f)),
            arrayOf(Vector3(-2.50f, 0.00f, 2.25f), Vector3(-2.50f, 0.30f, 2.25f), Vector3(-2.30f, 0.30f, 2.03f), Vector3(-2.30f, 0.00f, 2.03f)),
            arrayOf(Vector3(-3.00f, 0.00f, 2.25f), Vector3(-3.00f, 0.30f, 2.25f), Vector3(-2.70f, 0.30f, 2.03f), Vector3(-2.70f, 0.00f, 2.03f)),
            arrayOf(Vector3(-3.00f, 0.00f, 1.80f), Vector3(-3.00f, 0.30f, 1.80f), Vector3(-2.70f, 0.30f, 1.80f), Vector3(-2.70f, 0.00f, 1.80f))
        ),
        arrayOf(
            arrayOf(Vector3(-2.70f, 0.00f, 1.80f), Vector3(-2.70f, -0.30f, 1.80f), Vector3(-3.00f, -0.30f, 1.80f), Vector3(-3.00f, 0.00f, 1.80f)),
            arrayOf(Vector3(-2.70f, 0.00f, 1.58f), Vector3(-2.70f, -0.30f, 1.58f), Vector3(-3.00f, -0.30f, 1.35f), Vector3(-3.00f, 0.00f, 1.35f)),
            arrayOf(Vector3(-2.50f, 0.00f, 1.13f), Vector3(-2.50f, -0.30f, 1.13f), Vector3(-2.65f, -0.30f, 0.94f), Vector3(-2.65f, 0.00f, 0.94f)),
            arrayOf(Vector3(-2.00f, 0.00f, 0.90f), Vector3(-2.00f, -0.30f, 0.90f), Vector3(-1.90f, -0.30f, 0.60f), Vector3(-1.90f, 0.00f, 0.60f))
        ),
        arrayOf(
            arrayOf(Vector3(-3.00f, 0.00f, 1.80f), Vector3(-3.00f, 0.30f, 1.80f), Vector3(-2.70f, 0.30f, 1.80f), Vector3(-2.70f, 0.00f, 1.80f)),
            arrayOf(Vector3(-3.00f, 0.00f, 1.35f), Vector3(-3.00f, 0.30f, 1.35f), Vector3(-2.70f, 0.30f, 1.58f), Vector3(-2.70f, 0.00f, 1.58f)),
            arrayOf(Vector3(-2.65f, 0.00f, 0.94f), Vector3(-2.65f, 0.30f, 0.94f), Vector3(-2.50f, 0.30f, 1.13f), Vector3(-2.50f, 0.00f, 1.13f)),
            arrayOf(Vector3(-1.90f, 0.00f, 0.60f), Vector3(-1.90f, 0.30f, 0.60f), Vector3(-2.00f, 0.30f, 0.90f), Vector3(-2.00f, 0.00f, 0.90f))
        ),
        arrayOf(
            arrayOf(Vector3(1.70f, 0.00f, 1.43f), Vector3(1.70f, -0.66f, 1.43f), Vector3(1.70f, -0.66f, 0.60f), Vector3(1.70f, 0.00f, 0.60f)),
            arrayOf(Vector3(2.60f, 0.00f, 1.43f), Vector3(2.60f, -0.66f, 1.43f), Vector3(3.10f, -0.66f, 0.83f), Vector3(3.10f, 0.00f, 0.83f)),
            arrayOf(Vector3(2.30f, 0.00f, 2.10f), Vector3(2.30f, -0.25f, 2.10f), Vector3(2.40f, -0.25f, 2.03f), Vector3(2.40f, 0.00f, 2.03f)),
            arrayOf(Vector3(2.70f, 0.00f, 2.40f), Vector3(2.70f, -0.25f, 2.40f), Vector3(3.30f, -0.25f, 2.40f), Vector3(3.30f, 0.00f, 2.40f))
        ),
        arrayOf(
            arrayOf(Vector3(1.70f, 0.00f, 0.60f), Vector3(1.70f, 0.66f, 0.60f), Vector3(1.70f, 0.66f, 1.43f), Vector3(1.70f, 0.00f, 1.43f)),
            arrayOf(Vector3(3.10f, 0.00f, 0.83f), Vector3(3.10f, 0.66f, 0.83f), Vector3(2.60f, 0.66f, 1.43f), Vector3(2.60f, 0.00f, 1.43f)),
            arrayOf(Vector3(2.40f, 0.00f, 2.03f), Vector3(2.40f, 0.25f, 2.03f), Vector3(2.30f, 0.25f, 2.10f), Vector3(2.30f, 0.00f, 2.10f)),
            arrayOf(Vector3(3.30f, 0.00f, 2.40f), Vector3(3.30f, 0.25f, 2.40f), Vector3(2.70f, 0.25f, 2.40f), Vector3(2.70f, 0.00f, 2.40f))
        ),
        arrayOf(
            arrayOf(Vector3(2.70f, 0.00f, 2.40f), Vector3(2.70f, -0.25f, 2.40f), Vector3(3.30f, -0.25f, 2.40f), Vector3(3.30f, 0.00f, 2.40f)),
            arrayOf(Vector3(2.80f, 0.00f, 2.48f), Vector3(2.80f, -0.25f, 2.48f), Vector3(3.53f, -0.25f, 2.49f), Vector3(3.53f, 0.00f, 2.49f)),
            arrayOf(Vector3(2.90f, 0.00f, 2.48f), Vector3(2.90f, -0.15f, 2.48f), Vector3(3.45f, -0.15f, 2.51f), Vector3(3.45f, 0.00f, 2.51f)),
            arrayOf(Vector3(2.80f, 0.00f, 2.40f), Vector3(2.80f, -0.15f, 2.40f), Vector3(3.20f, -0.15f, 2.40f), Vector3(3.20f, 0.00f, 2.40f))
        ),
        arrayOf(
            arrayOf(Vector3(3.30f, 0.00f, 2.40f), Vector3(3.30f, 0.25f, 2.40f), Vector3(2.70f, 0.25f, 2.40f), Vector3(2.70f, 0.00f, 2.40f)),
            arrayOf(Vector3(3.53f, 0.00f, 2.49f), Vector3(3.53f, 0.25f, 2.49f), Vector3(2.80f, 0.25f, 2.48f), Vector3(2.80f, 0.00f, 2.48f)),
            arrayOf(Vector3(3.45f, 0.00f, 2.51f), Vector3(3.45f, 0.15f, 2.51f), Vector3(2.90f, 0.15f, 2.48f), Vector3(2.90f, 0.00f, 2.48f)),
            arrayOf(Vector3(3.20f, 0.00f, 2.40f), Vector3(3.20f, 0.15f, 2.40f), Vector3(2.80f, 0.15f, 2.40f), Vector3(2.80f, 0.00f, 2.40f))
        ),
        arrayOf(
            arrayOf(Vector3(0.00f, 0.00f, 3.15f), Vector3(0.00f, 0.00f, 3.15f), Vector3(0.00f, 0.00f, 3.15f), Vector3(0.00f, 0.00f, 3.15f)),
            arrayOf(Vector3(0.80f, 0.00f, 3.15f), Vector3(0.80f, -0.45f, 3.15f), Vector3(0.45f, -0.80f, 3.15f), Vector3(0.00f, -0.80f, 3.15f)),
            arrayOf(Vector3(0.00f, 0.00f, 2.85f), Vector3(0.00f, 0.00f, 2.85f), Vector3(0.00f, 0.00f, 2.85f), Vector3(0.00f, 0.00f, 2.85f)),
            arrayOf(Vector3(0.20f, 0.00f, 2.70f), Vector3(0.20f, -0.11f, 2.70f), Vector3(0.11f, -0.20f, 2.70f), Vector3(0.00f, -0.20f, 2.70f))
        ),
        arrayOf(
            arrayOf(Vector3(0.00f, 0.00f, 3.15f), Vector3(0.00f, 0.00f, 3.15f), Vector3(0.00f, 0.00f, 3.15f), Vector3(0.00f, 0.00f, 3.15f)),
            arrayOf(Vector3(0.00f, -0.80f, 3.15f), Vector3(-0.45f, -0.80f, 3.15f), Vector3(-0.80f, -0.45f, 3.15f), Vector3(-0.80f, 0.00f, 3.15f)),
            arrayOf(Vector3(0.00f, 0.00f, 2.85f), Vector3(0.00f, 0.00f, 2.85f), Vector3(0.00f, 0.00f, 2.85f), Vector3(0.00f, 0.00f, 2.85f)),
            arrayOf(Vector3(0.00f, -0.20f, 2.70f), Vector3(-0.11f, -0.20f, 2.70f), Vector3(-0.20f, -0.11f, 2.70f), Vector3(-0.20f, 0.00f, 2.70f))
        ),
        arrayOf(
            arrayOf(Vector3(0.00f, 0.00f, 3.15f), Vector3(0.00f, 0.00f, 3.15f), Vector3(0.00f, 0.00f, 3.15f), Vector3(0.00f, 0.00f, 3.15f)),
            arrayOf(Vector3(-0.80f, 0.00f, 3.15f), Vector3(-0.80f, 0.45f, 3.15f), Vector3(-0.45f, 0.80f, 3.15f), Vector3(0.00f, 0.80f, 3.15f)),
            arrayOf(Vector3(0.00f, 0.00f, 2.85f), Vector3(0.00f, 0.00f, 2.85f), Vector3(0.00f, 0.00f, 2.85f), Vector3(0.00f, 0.00f, 2.85f)),
            arrayOf(Vector3(-0.20f, 0.00f, 2.70f), Vector3(-0.20f, 0.11f, 2.70f), Vector3(-0.11f, 0.20f, 2.70f), Vector3(0.00f, 0.20f, 2.70f))
        ),
        arrayOf(
            arrayOf(Vector3(0.00f, 0.00f, 3.15f), Vector3(0.00f, 0.00f, 3.15f), Vector3(0.00f, 0.00f, 3.15f), Vector3(0.00f, 0.00f, 3.15f)),
            arrayOf(Vector3(0.00f, 0.80f, 3.15f), Vector3(0.45f, 0.80f, 3.15f), Vector3(0.80f, 0.45f, 3.15f), Vector3(0.80f, 0.00f, 3.15f)),
            arrayOf(Vector3(0.00f, 0.00f, 2.85f), Vector3(0.00f, 0.00f, 2.85f), Vector3(0.00f, 0.00f, 2.85f), Vector3(0.00f, 0.00f, 2.85f)),
            arrayOf(Vector3(0.00f, 0.20f, 2.70f), Vector3(0.11f, 0.20f, 2.70f), Vector3(0.20f, 0.11f, 2.70f), Vector3(0.20f, 0.00f, 2.70f))
        ),
        arrayOf(
            arrayOf(Vector3(0.20f, 0.00f, 2.70f), Vector3(0.20f, -0.11f, 2.70f), Vector3(0.11f, -0.20f, 2.70f), Vector3(0.00f, -0.20f, 2.70f)),
            arrayOf(Vector3(0.40f, 0.00f, 2.55f), Vector3(0.40f, -0.22f, 2.55f), Vector3(0.22f, -0.40f, 2.55f), Vector3(0.00f, -0.40f, 2.55f)),
            arrayOf(Vector3(1.30f, 0.00f, 2.55f), Vector3(1.30f, -0.73f, 2.55f), Vector3(0.73f, -1.30f, 2.55f), Vector3(0.00f, -1.30f, 2.55f)),
            arrayOf(Vector3(1.30f, 0.00f, 2.40f), Vector3(1.30f, -0.73f, 2.40f), Vector3(0.73f, -1.30f, 2.40f), Vector3(0.00f, -1.30f, 2.40f))
        ),
        arrayOf(
            arrayOf(Vector3(0.00f, -0.20f, 2.70f), Vector3(-0.11f, -0.20f, 2.70f), Vector3(-0.20f, -0.11f, 2.70f), Vector3(-0.20f, 0.00f, 2.70f)),
            arrayOf(Vector3(0.00f, -0.40f, 2.55f), Vector3(-0.22f, -0.40f, 2.55f), Vector3(-0.40f, -0.22f, 2.55f), Vector3(-0.40f, 0.00f, 2.55f)),
            arrayOf(Vector3(0.00f, -1.30f, 2.55f), Vector3(-0.73f, -1.30f, 2.55f), Vector3(-1.30f, -0.73f, 2.55f), Vector3(-1.30f, 0.00f, 2.55f)),
            arrayOf(Vector3(0.00f, -1.30f, 2.40f), Vector3(-0.73f, -1.30f, 2.40f), Vector3(-1.30f, -0.73f, 2.40f), Vector3(-1.30f, 0.00f, 2.40f))
        ),
        arrayOf(
            arrayOf(Vector3(-0.20f, 0.00f, 2.70f), Vector3(-0.20f, 0.11f, 2.70f), Vector3(-0.11f, 0.20f, 2.70f), Vector3(0.00f, 0.20f, 2.70f)),
            arrayOf(Vector3(-0.40f, 0.00f, 2.55f), Vector3(-0.40f, 0.22f, 2.55f), Vector3(-0.22f, 0.40f, 2.55f), Vector3(0.00f, 0.40f, 2.55f)),
            arrayOf(Vector3(-1.30f, 0.00f, 2.55f), Vector3(-1.30f, 0.73f, 2.55f), Vector3(-0.73f, 1.30f, 2.55f), Vector3(0.00f, 1.30f, 2.55f)),
            arrayOf(Vector3(-1.30f, 0.00f, 2.40f), Vector3(-1.30f, 0.73f, 2.40f), Vector3(-0.73f, 1.30f, 2.40f), Vector3(0.00f, 1.30f, 2.40f))
        ),
        arrayOf(
            arrayOf(Vector3(0.00f, 0.20f, 2.70f), Vector3(0.11f, 0.20f, 2.70f), Vector3(0.20f, 0.11f, 2.70f), Vector3(0.20f, 0.00f, 2.70f)),
            arrayOf(Vector3(0.00f, 0.40f, 2.55f), Vector3(0.22f, 0.40f, 2.55f), Vector3(0.40f, 0.22f, 2.55f), Vector3(0.40f, 0.00f, 2.55f)),
            arrayOf(Vector3(0.00f, 1.30f, 2.55f), Vector3(0.73f, 1.30f, 2.55f), Vector3(1.30f, 0.73f, 2.55f), Vector3(1.30f, 0.00f, 2.55f)),
            arrayOf(Vector3(0.00f, 1.30f, 2.40f), Vector3(0.73f, 1.30f, 2.40f), Vector3(1.30f, 0.73f, 2.40f), Vector3(1.30f, 0.00f, 2.40f))
        ),
        arrayOf(
            arrayOf(Vector3(0.00f, 0.00f, 0.00f), Vector3(0.00f, 0.00f, 0.00f), Vector3(0.00f, 0.00f, 0.00f), Vector3(0.00f, 0.00f, 0.00f)),
            arrayOf(Vector3(1.43f, 0.00f, 0.00f), Vector3(1.43f, 0.80f, 0.00f), Vector3(0.80f, 1.43f, 0.00f), Vector3(0.00f, 1.43f, 0.00f)),
            arrayOf(Vector3(1.50f, 0.00f, 0.08f), Vector3(1.50f, 0.84f, 0.08f), Vector3(0.84f, 1.50f, 0.08f), Vector3(0.00f, 1.50f, 0.08f)),
            arrayOf(Vector3(1.50f, 0.00f, 0.15f), Vector3(1.50f, 0.84f, 0.15f), Vector3(0.84f, 1.50f, 0.15f), Vector3(0.00f, 1.50f, 0.15f))
        ),
        arrayOf(
            arrayOf(Vector3(0.00f, 0.00f, 0.00f), Vector3(0.00f, 0.00f, 0.00f), Vector3(0.00f, 0.00f, 0.00f), Vector3(0.00f, 0.00f, 0.00f)),
            arrayOf(Vector3(0.00f, 1.43f, 0.00f), Vector3(-0.80f, 1.43f, 0.00f), Vector3(-1.43f, 0.80f, 0.00f), Vector3(-1.43f, 0.00f, 0.00f)),
            arrayOf(Vector3(0.00f, 1.50f, 0.08f), Vector3(-0.84f, 1.50f, 0.08f), Vector3(-1.50f, 0.84f, 0.08f), Vector3(-1.50f, 0.00f, 0.08f)),
            arrayOf(Vector3(0.00f, 1.50f, 0.15f), Vector3(-0.84f, 1.50f, 0.15f), Vector3(-1.50f, 0.84f, 0.15f), Vector3(-1.50f, 0.00f, 0.15f))
        ),
        arrayOf(
            arrayOf(Vector3(0.00f, 0.00f, 0.00f), Vector3(0.00f, 0.00f, 0.00f), Vector3(0.00f, 0.00f, 0.00f), Vector3(0.00f, 0.00f, 0.00f)),
            arrayOf(Vector3(-1.43f, 0.00f, 0.00f), Vector3(-1.43f, -0.80f, 0.00f), Vector3(-0.80f, -1.43f, 0.00f), Vector3(0.00f, -1.43f, 0.00f)),
            arrayOf(Vector3(-1.50f, 0.00f, 0.08f), Vector3(-1.50f, -0.84f, 0.08f), Vector3(-0.84f, -1.50f, 0.08f), Vector3(0.00f, -1.50f, 0.08f)),
            arrayOf(Vector3(-1.50f, 0.00f, 0.15f), Vector3(-1.50f, -0.84f, 0.15f), Vector3(-0.84f, -1.50f, 0.15f), Vector3(0.00f, -1.50f, 0.15f))
        ),
        arrayOf(
            arrayOf(Vector3(0.00f, 0.00f, 0.00f), Vector3(0.00f, 0.00f, 0.00f), Vector3(0.00f, 0.00f, 0.00f), Vector3(0.00f, 0.00f, 0.00f)),
            arrayOf(Vector3(0.00f, -1.43f, 0.00f), Vector3(0.80f, -1.43f, 0.00f), Vector3(1.43f, -0.80f, 0.00f), Vector3(1.43f, 0.00f, 0.00f)),
            arrayOf(Vector3(0.00f, -1.50f, 0.08f), Vector3(0.84f, -1.50f, 0.08f), Vector3(1.50f, -0.84f, 0.08f), Vector3(1.50f, 0.00f, 0.08f)),
            arrayOf(Vector3(0.00f, -1.50f, 0.15f), Vector3(0.84f, -1.50f, 0.15f), Vector3(1.50f, -0.84f, 0.15f), Vector3(1.50f, 0.00f, 0.15f))
        )
    )
    }
}
