package generator

// ============================================================================
// ======================= FlipPath ===========================================
// ============================================================================

class FlipPath(
    private val _path: GeneratorPath
) : GeneratorPath {

    override fun vertices(): Sequence<PathVertex> = _path.vertices().map { vertex: PathVertex ->
        vertex.mTangent = scale(vertex.mTangent, -1.0f)
        vertex.mNormal = scale(vertex.mNormal, -1.0f)
        vertex
    }

    override fun edges(): Sequence<Edge> = _path.edges().map { edge: Edge ->
        Edge(edge.mVertices[1], edge.mVertices[0])
    }
}
