package generator

import algebra.*

// ============================================================================
// ======================= FlipShape ==========================================
// ============================================================================

class FlipShape(
    private val _shape: GeneratorShape
) : TransformShape(_shape, _flipEval()) {

    override fun edges(): Sequence<Edge> = _shape.edges().map { edge: Edge ->
        Edge(edge.mVertices[1], edge.mVertices[0])
    }
}

private fun _flipEval(): (ShapeVertex) -> Unit {
    return { vertex: ShapeVertex ->
        vertex.mTangent = Vector2(-vertex.mTangent.x, -vertex.mTangent.y)
    }
}
