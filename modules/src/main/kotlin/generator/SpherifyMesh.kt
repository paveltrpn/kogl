package generator

// ============================================================================
// ======================= SpherifyMesh =======================================
// ============================================================================
class SpherifyMesh(
    mMesh: GeneratorMesh,
    mRadius: Float,
    mFactor: Float
) : GeneratorMesh {

    private val _mesh: TransformMesh = TransformMesh(mMesh, _spherifyMutator(mRadius, mFactor))

    override fun vertices(): Sequence<MeshVertex> = _mesh.vertices()

    override fun triangles(): Sequence<Triangle> = _mesh.triangles()
}

private fun _spherifyMutator(radius: Float, factor: Float): (MeshVertex) -> Unit {
    return { vertex: MeshVertex ->
        vertex.mPosition = mix(
            vertex.mPosition,
            scale(normalize(vertex.mPosition), radius),
            factor
        )
        vertex.mNormal = normalize(mix(
            vertex.mNormal,
            normalize(vertex.mPosition),
            factor
        ))
    }
}
