package generator

import algebra.*

// ============================================================================
// ======================= TranslateMesh ======================================
// ============================================================================
class TranslateMesh(
    mMesh: GeneratorMesh,
    mDelta: Vector3
) : GeneratorMesh {

    private val _mesh: TransformMesh = TransformMesh(mMesh, _translateMutator(mDelta))

    override fun vertices(): Sequence<MeshVertex> = _mesh.vertices()

    override fun triangles(): Sequence<Triangle> = _mesh.triangles()
}

private fun _translateMutator(delta: Vector3): (MeshVertex) -> Unit {
    val dx = delta.x
    val dy = delta.y
    val dz = delta.z
    return { vertex: MeshVertex ->
        vertex.mPosition = Vector3(
            vertex.mPosition.x + dx,
            vertex.mPosition.y + dy,
            vertex.mPosition.z + dz
        )
    }
}
