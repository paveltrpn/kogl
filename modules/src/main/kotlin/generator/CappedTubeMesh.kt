package generator

import algebra.Vector3
import algebra.toRadians

private class TubeCap(
    mRadius: Float,
    mInnerRadius: Float,
    mDistance: Float,
    mSlices: Int,
    mRings: Int,
    mStart: Float,
    mSweep: Float
) : GeneratorMesh {

    private val _impl: GeneratorMesh = TranslateMesh(
        DiskMesh(mRadius, mInnerRadius, mSlices, mRings, mStart, mSweep),
        Vector3(0.0f, 0.0f, mDistance)
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}

class CappedTubeMesh(
    mRadius: Float = 1.0f,
    mInnerRadius: Float = 0.75f,
    mSize: Float = 1.0f,
    mSlices: Int = 32,
    mSegments: Int = 8,
    mRings: Int = 1,
    mStart: Float = 0.0f,
    mSweep: Float = toRadians(360.0f)
) : GeneratorMesh {

    private val _impl: GeneratorMesh = MergeMesh(
        TubeMesh(mRadius, mInnerRadius, mSize, mSlices, mSegments, mStart, mSweep),
        TubeCap(mRadius, mInnerRadius, mSize, mSlices, mRings, mStart, mSweep),
        FlipMesh(TubeCap(mRadius, mInnerRadius, -mSize, mSlices, mRings, mStart, mSweep))
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}
