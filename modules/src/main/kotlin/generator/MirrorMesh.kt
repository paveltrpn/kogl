package generator

// ============================================================================
// ======================= MirrorMesh =========================================
// ============================================================================
class MirrorMesh(
    mMesh: GeneratorMesh,
    mAxis: Axis
) : GeneratorMesh {

    private val _mesh: MergeMesh = MergeMesh(
        mMesh,
        AxisFlipMesh(
            mMesh,
            mAxis == Axis.X,
            mAxis == Axis.Y,
            mAxis == Axis.Z
        )
    )

    override fun vertices(): Sequence<MeshVertex> = _mesh.vertices()

    override fun triangles(): Sequence<Triangle> = _mesh.triangles()
}
