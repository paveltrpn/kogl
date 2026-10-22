package generator

import algebra.Vector3

class SubdividePath(
    private val _path: GeneratorPath
) : GeneratorPath {

    private val _vertexCache: List<PathVertex> = _path.vertices().toList()

    override fun vertices(): Sequence<PathVertex> = sequence {
        for (cached in _vertexCache) {
            val vertex = PathVertex()
            vertex.mPosition = Vector3(cached.mPosition.x, cached.mPosition.y, cached.mPosition.z)
            vertex.mTangent = Vector3(cached.mTangent.x, cached.mTangent.y, cached.mTangent.z)
            vertex.mNormal = Vector3(cached.mNormal.x, cached.mNormal.y, cached.mNormal.z)
            vertex.mTexCoord = cached.mTexCoord
            yield(vertex)
        }
        for (edge in _path.edges()) {
            val v1 = _vertexCache[edge.mVertices[0]]
            val v2 = _vertexCache[edge.mVertices[1]]
            val vertex = PathVertex()
            vertex.mPosition = mix(v1.mPosition, v2.mPosition, 0.5f)
            vertex.mTangent = normalize(mix(v1.mTangent, v2.mTangent, 0.5f))
            vertex.mNormal = normalize(mix(v1.mNormal, v2.mNormal, 0.5f))
            vertex.mTexCoord = 0.5f * v1.mTexCoord + 0.5f * v2.mTexCoord
            yield(vertex)
        }
    }

    override fun edges(): Sequence<Edge> = sequence {
        val offset = _vertexCache.size
        var index = 0
        for (edge in _path.edges()) {
            yield(Edge(edge.mVertices[0], offset + index))
            yield(Edge(offset + index, edge.mVertices[1]))
            index += 1
        }
    }
}
