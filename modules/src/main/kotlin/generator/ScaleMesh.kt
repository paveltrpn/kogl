package generator

import algebra.Vector3

class ScaleMesh(
    mMesh: GeneratorMesh,
    mScale: Vector3
) : GeneratorMesh {

    private val _mesh: TransformMesh = TransformMesh(mMesh, scaleMutator(mScale))

    override fun vertices(): Sequence<MeshVertex> = _mesh.vertices()

    override fun triangles(): Sequence<Triangle> = _mesh.triangles()
}

private fun scaleMutator(scaleFactor: Vector3): (MeshVertex) -> Unit {
    val factors = Vector3(scaleFactor.x, scaleFactor.y, scaleFactor.z)
    return { vertex ->
        vertex.mPosition = mul(vertex.mPosition, factors)
        vertex.mNormal = normalize(mul(factors, vertex.mNormal))
    }
}
