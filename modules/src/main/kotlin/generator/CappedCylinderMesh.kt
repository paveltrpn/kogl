package generator

import algebra.Vector3
import algebra.toRadians

private class Cap(
    mRadius: Float,
    mDistance: Float,
    mSlices: Int,
    mRings: Int,
    mStart: Float,
    mSweep: Float
) : GeneratorMesh {

    private val _impl: GeneratorMesh = TranslateMesh(
        DiskMesh(mRadius, 0.0f, mSlices, mRings, mStart, mSweep),
        Vector3(0.0f, 0.0f, mDistance)
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}

class CappedCylinderMesh(
    mRadius: Float = 1.0f,
    mSize: Float = 1.0f,
    mSlices: Int = 32,
    mSegments: Int = 8,
    mRings: Int = 4,
    mStart: Float = 0.0f,
    mSweep: Float = toRadians(360.0f)
) : GeneratorMesh {

    private val _impl: GeneratorMesh = MergeMesh(
        CylinderMesh(mRadius, mSize, mSlices, mSegments, mStart, mSweep),
        Cap(mRadius, mSize, mSlices, mRings, mStart, mSweep),
        UvFlipMesh(
            FlipMesh(Cap(mRadius, -mSize, mSlices, mRings, mStart, mSweep)),
            mU = true,
            mV = false
        )
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}
