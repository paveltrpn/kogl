package generator

// ============================================================================
// ======================= FlipMesh ===========================================
// ============================================================================

class FlipMesh(
    private val _mesh: GeneratorMesh
) : GeneratorMesh {

    override fun vertices(): Sequence<MeshVertex> = _mesh.vertices().map { vertex: MeshVertex ->
        vertex.mNormal = scale(vertex.mNormal, -1.0f)
        vertex
    }

    override fun triangles(): Sequence<Triangle> = _mesh.triangles().map { triangle: Triangle ->
        Triangle(
            triangle.mVertices[2],
            triangle.mVertices[1],
            triangle.mVertices[0]
        )
    }
}
