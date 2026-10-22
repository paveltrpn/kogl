package generator

import algebra.*

// ============================================================================
// ======================= TubeMesh ===========================================
// ============================================================================
class TubeMesh(
    mRadius: Float = 1.0f,
    mInnerRadius: Float = 0.75f,
    mSize: Float = 1.0f,
    mSlices: Int = 32,
    mSegments: Int = 8,
    mStart: Float = 0.0f,
    mSweep: Float = toRadians(360.0f)
) : GeneratorMesh {

    private val _impl: GeneratorMesh = MergeMesh(
        CylinderMesh(mRadius, mSize, mSlices, mSegments, mStart, mSweep),
        FlipMesh(
            UvFlipMesh(
                CylinderMesh(mInnerRadius, mSize, mSlices, mSegments, mStart, mSweep),
                mU = true,
                mV = false
            )
        )
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}
