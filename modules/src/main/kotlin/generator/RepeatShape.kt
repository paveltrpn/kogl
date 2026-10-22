package generator

import algebra.Vector2

class RepeatShape(
    private val _shape: GeneratorShape,
    mInstances: Int,
    mDelta: Vector2
) : GeneratorShape {

    private val _instances: Int = mInstances
    private val _deltaX: Float = mDelta.x
    private val _deltaY: Float = mDelta.y
    private val _vertexCount: Int = _shape.vertices().count()

    override fun vertices(): Sequence<ShapeVertex> = sequence {
        if (_vertexCount == 0) {
            return@sequence
        }
        var deltaX = 0.0f
        var deltaY = 0.0f
        for (i in 0 until _instances) {
            for (vertex in _shape.vertices()) {
                vertex.mPosition = Vector2(
                    vertex.mPosition.x + deltaX,
                    vertex.mPosition.y + deltaY
                )
                yield(vertex)
            }
            deltaX += _deltaX
            deltaY += _deltaY
        }
    }

    override fun edges(): Sequence<Edge> = sequence {
        if (_vertexCount == 0) {
            return@sequence
        }
        for (i in 0 until _instances) {
            val offset = i * _vertexCount
            for (edge in _shape.edges()) {
                yield(Edge(
                    edge.mVertices[0] + offset,
                    edge.mVertices[1] + offset
                ))
            }
        }
    }
}
