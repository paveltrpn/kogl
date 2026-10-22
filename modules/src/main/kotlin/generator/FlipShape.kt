package generator

import algebra.Vector2

class FlipShape(
    private val _shape: GeneratorShape
) : TransformShape(_shape, flipEval()) {

    override fun edges(): Sequence<Edge> = _shape.edges().map { edge ->
        Edge(edge.mVertices[1], edge.mVertices[0])
    }
}

private fun flipEval(): (ShapeVertex) -> Unit {
    return { vertex ->
        vertex.mTangent = Vector2(-vertex.mTangent.x, -vertex.mTangent.y)
    }
}
