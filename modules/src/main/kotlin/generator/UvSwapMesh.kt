package generator

import algebra.Vector2

class UvSwapMesh(
    mMesh: GeneratorMesh
) : GeneratorMesh {

    private val _mesh: TransformMesh = TransformMesh(mMesh, uvSwapMutator())

    override fun vertices(): Sequence<MeshVertex> = _mesh.vertices()

    override fun triangles(): Sequence<Triangle> = _mesh.triangles()
}

private fun uvSwapMutator(): (MeshVertex) -> Unit {
    return { vertex ->
        vertex.mTexCoord = Vector2(vertex.mTexCoord.y, vertex.mTexCoord.x)
    }
}
