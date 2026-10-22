package generator

// ============================================================================
// ======================= IcoSphereMesh ======================================
// ============================================================================
class IcoSphereMesh(
    mRadius: Float = 1.0f,
    mSegments: Int = 4
) : GeneratorMesh {

    private val _impl: GeneratorMesh = SpherifyMesh(
        IcosahedronMesh(1.0f, mSegments),
        mRadius,
        1.0f
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}
