package generator

import algebra.*

// ============================================================================
// ======================= ExtrudeMesh ========================================
// ============================================================================

class ExtrudeMesh(
    private val _shape: GeneratorShape,
    private val _path: GeneratorPath
) : GeneratorMesh {

    private val _shapeVertexCount: Int = _shape.vertices().count()

    override fun vertices(): Sequence<MeshVertex> = sequence {
        for (pathVertex in _path.vertices()) {
            val pathBinormal = pathVertex.binormal()
            for (shapeVertex in _shape.vertices()) {
                val vertex = MeshVertex()
                vertex.mPosition = add(
                    pathVertex.mPosition,
                    add(
                        scale(pathVertex.mNormal, shapeVertex.mPosition.x),
                        scale(pathBinormal, shapeVertex.mPosition.y)
                    )
                )
                val shapeNormal = shapeVertex.normal()
                vertex.mNormal = add(
                    scale(pathVertex.mNormal, shapeNormal.x),
                    scale(pathBinormal, shapeNormal.y)
                )
                vertex.mTexCoord = Vector2(shapeVertex.mTexCoord, pathVertex.mTexCoord)
                yield(vertex)
            }
        }
    }

    override fun triangles(): Sequence<Triangle> = sequence {
        for (pathEdge in _path.edges()) {
            val p0 = pathEdge.mVertices[0] * _shapeVertexCount
            val p1 = pathEdge.mVertices[1] * _shapeVertexCount
            for (shapeEdge in _shape.edges()) {
                val s0 = shapeEdge.mVertices[0]
                val s1 = shapeEdge.mVertices[1]
                yield(Triangle(s0 + p0, s1 + p0, s1 + p1))
                yield(Triangle(s0 + p0, s1 + p1, s0 + p1))
            }
        }
    }
}
