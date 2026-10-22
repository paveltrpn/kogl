package generator

import algebra.*

// ============================================================================
// ======================= ShapeToPath ========================================
// ============================================================================
class ShapeToPath(
    private val _shape: GeneratorShape
) : GeneratorPath {

    override fun vertices(): Sequence<PathVertex> = _shape.vertices().map { shapeVertex: ShapeVertex ->
        val vertex = PathVertex()
        vertex.mPosition = Vector3(
            shapeVertex.mPosition.x,
            shapeVertex.mPosition.y,
            0.0f
        )
        vertex.mTangent = Vector3(
            shapeVertex.mTangent.x,
            shapeVertex.mTangent.y,
            0.0f
        )
        vertex.mNormal = Vector3(0.0f, 0.0f, 1.0f)
        vertex.mTexCoord = shapeVertex.mTexCoord
        vertex
    }

    override fun edges(): Sequence<Edge> = _shape.edges()
}
