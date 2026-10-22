package generator

import algebra.*

// ============================================================================
// ======================= SubdivideMesh ======================================
// ============================================================================
class SubdivideMesh(
    private val _mesh: GeneratorMesh,
    private val _t: Float = 0.5f
) : GeneratorMesh {

    private val _vertexCache: List<MeshVertex>
    private val _edgeCache: List<IntPair>
    private val _edgeMap: Map<Long, Int>

    init {
        val vertices = ArrayList<MeshVertex>()
        for (vertex in _mesh.vertices()) {
            vertices.add(_copyVertex(vertex))
        }
        val edges = ArrayList<IntPair>()
        val edgeMap = HashMap<Long, Int>()
        for (triangle in _mesh.triangles()) {
            for (i in 0 until 3) {
                val j = (i + 1) % 3
                var a = triangle.mVertices[i]
                var b = triangle.mVertices[j]
                if (a > b) {
                    val temp = a
                    a = b
                    b = temp
                }
                val key = _edgeKey(a, b)
                if (!edgeMap.containsKey(key)) {
                    edgeMap[key] = edges.size
                    edges.add(IntPair(a, b))
                }
            }
        }
        _vertexCache = vertices
        _edgeCache = edges
        _edgeMap = edgeMap
    }

    override fun vertices(): Sequence<MeshVertex> = sequence {
        for (vertex in _vertexCache) {
            yield(_copyVertex(vertex))
        }
        for (edge in _edgeCache) {
            val v1 = _vertexCache[edge.mX]
            val v2 = _vertexCache[edge.mY]
            val result = MeshVertex()
            result.mPosition = mix(v1.mPosition, v2.mPosition, _t)
            result.mTexCoord = mix(v1.mTexCoord, v2.mTexCoord, _t)
            result.mNormal = normalize(mix(v1.mNormal, v2.mNormal, _t))
            yield(result)
        }
    }

    override fun triangles(): Sequence<Triangle> = sequence {
        for (triangle in _mesh.triangles()) {
            val a = triangle.mVertices[0]
            val b = triangle.mVertices[1]
            val c = triangle.mVertices[2]
            yield(Triangle(
                a,
                _vertexFromEdge(a, b),
                _vertexFromEdge(c, a)
            ))
            yield(Triangle(
                b,
                _vertexFromEdge(b, c),
                _vertexFromEdge(a, b)
            ))
            yield(Triangle(
                c,
                _vertexFromEdge(c, a),
                _vertexFromEdge(b, c)
            ))
            yield(Triangle(
                _vertexFromEdge(a, b),
                _vertexFromEdge(b, c),
                _vertexFromEdge(c, a)
            ))
        }
    }

    private fun _vertexFromEdge(a: Int, b: Int): Int {
        var first = a
        var second = b
        if (first > second) {
            val temp = first
            first = second
            second = temp
        }
        return _vertexCache.size + _edgeMap.getValue(_edgeKey(first, second))
    }
}

private fun _edgeKey(a: Int, b: Int): Long {
    return (a.toLong() shl 32) or (b.toLong() and 0xffffffffL)
}

private fun _copyVertex(vertex: MeshVertex): MeshVertex {
    val copy = MeshVertex()
    copy.mPosition = Vector3(
        vertex.mPosition.x,
        vertex.mPosition.y,
        vertex.mPosition.z
    )
    copy.mNormal = Vector3(
        vertex.mNormal.x,
        vertex.mNormal.y,
        vertex.mNormal.z
    )
    copy.mTexCoord = Vector2(
        vertex.mTexCoord.x,
        vertex.mTexCoord.y
    )
    return copy
}
