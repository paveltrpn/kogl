package generator

import algebra.*

// ============================================================================
// ======================= LatheMesh ==========================================
// ============================================================================
class LatheMesh(
    private val _shape: GeneratorShape,
    axis: Vector2,
    private val _slices: Int = 32,
    private val _start: Float = 0.0f,
    private val _sweep: Float = toRadians(360.0f)
) : GeneratorMesh {

    private val _axis: Vector3 = Vector3(axis.x, axis.y, 0.0f)

    override fun vertices(): Sequence<MeshVertex> = sequence {
        val deltaAngle = _sweep / _slices
        for (shapeVertex in _shape.vertices()) {
            val shapeNormal = shapeVertex.normal()
            for (i in 0.._slices) {
                val angle = i * deltaAngle + _start

                val vertex = MeshVertex()
                vertex.mPosition = rotate(
                    Vector3(shapeVertex.mPosition.x, shapeVertex.mPosition.y, 0.0f),
                    _axis,
                    angle
                )
                vertex.mNormal = rotate(
                    Vector3(shapeNormal.x, shapeNormal.y, 0.0f),
                    _axis,
                    angle
                )
                vertex.mTexCoord = Vector2(shapeVertex.mTexCoord, angle / _sweep)
                yield(vertex)
            }
        }
    }

    override fun triangles(): Sequence<Triangle> = sequence {
        val delta = _slices + 1
        for (edge in _shape.edges()) {
            for (s in 0 until _slices) {
                val e0 = edge.mVertices[0] * delta + s
                val e1 = edge.mVertices[1] * delta + s
                yield(Triangle(e0, e1, e1 + 1))
                yield(Triangle(e0, e1 + 1, e0 + 1))
            }
        }
    }
}
