package generator

import algebra.Vector3

class TranslateMesh(
    mMesh: GeneratorMesh,
    mDelta: Vector3
) : GeneratorMesh {

    private val _mesh: TransformMesh = TransformMesh(mMesh, translateMutator(mDelta))

    override fun vertices(): Sequence<MeshVertex> = _mesh.vertices()

    override fun triangles(): Sequence<Triangle> = _mesh.triangles()
}

private fun translateMutator(delta: Vector3): (MeshVertex) -> Unit {
    val dx = delta.x
    val dy = delta.y
    val dz = delta.z
    return { vertex ->
        vertex.mPosition = Vector3(
            vertex.mPosition.x + dx,
            vertex.mPosition.y + dy,
            vertex.mPosition.z + dz
        )
    }
}
