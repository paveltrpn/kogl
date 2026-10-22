package generator

// ============================================================================
// ======================= SubdivideShape =====================================
// ============================================================================
class SubdivideShape(
    private val _shape: GeneratorShape
) : GeneratorShape {

    private val _vertexCache: List<ShapeVertex> = _shape.vertices().toList()

    override fun vertices(): Sequence<ShapeVertex> = sequence {
        for (vertex in _vertexCache) {
            yield(vertex)
        }
        for (edge in _shape.edges()) {
            val v1 = _vertexCache[edge.mVertices[0]]
            val v2 = _vertexCache[edge.mVertices[1]]
            val vertex = ShapeVertex()
            vertex.mPosition = mix(v1.mPosition, v2.mPosition, 0.5f)
            vertex.mTangent = normalize(mix(v1.mTangent, v2.mTangent, 0.5f))
            vertex.mTexCoord = 0.5f * v1.mTexCoord + 0.5f * v2.mTexCoord
            yield(vertex)
        }
    }

    override fun edges(): Sequence<Edge> = sequence {
        val offset = _vertexCache.size
        var index = 0
        for (edge in _shape.edges()) {
            yield(Edge(edge.mVertices[0], offset + index))
            yield(Edge(offset + index, edge.mVertices[1]))
            index += 1
        }
    }
}
