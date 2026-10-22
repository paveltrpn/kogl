package generator

class UvFlipMesh(
    mMesh: GeneratorMesh,
    mU: Boolean,
    mV: Boolean
) : GeneratorMesh {

    private val _mesh: TransformMesh = TransformMesh(
        mMesh,
        { vertex ->
            if (mU) {
                vertex.mTexCoord.x = 1.0f - vertex.mTexCoord.x
            }
            if (mV) {
                vertex.mTexCoord.y = 1.0f - vertex.mTexCoord.y
            }
        }
    )

    override fun vertices(): Sequence<MeshVertex> = _mesh.vertices()

    override fun triangles(): Sequence<Triangle> = _mesh.triangles()
}
