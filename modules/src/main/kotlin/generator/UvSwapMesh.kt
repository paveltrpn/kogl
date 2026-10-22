package generator

import algebra.*

// ============================================================================
// ======================= UvSwapMesh =========================================
// ============================================================================
class UvSwapMesh(
    mMesh: GeneratorMesh
) : GeneratorMesh {

    private val _mesh: TransformMesh = TransformMesh(mMesh, _uvSwapMutator())

    override fun vertices(): Sequence<MeshVertex> = _mesh.vertices()

    override fun triangles(): Sequence<Triangle> = _mesh.triangles()
}

private fun _uvSwapMutator(): (MeshVertex) -> Unit {
    return { vertex: MeshVertex ->
        vertex.mTexCoord = Vector2(vertex.mTexCoord.y, vertex.mTexCoord.x)
    }
}
