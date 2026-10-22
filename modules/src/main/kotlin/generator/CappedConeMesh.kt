package generator

import algebra.Vector3
import algebra.toRadians

class CappedConeMesh(
    mRadius: Float = 1.0f,
    mSize: Float = 1.0f,
    mSlices: Int = 32,
    mSegments: Int = 8,
    mRings: Int = 4,
    mStart: Float = 0.0f,
    mSweep: Float = toRadians(360.0f)
) : GeneratorMesh {

    private val _impl: GeneratorMesh = MergeMesh(
        ConeMesh(mRadius, mSize, mSlices, mSegments, mStart, mSweep),
        UvFlipMesh(
            FlipMesh(
                TranslateMesh(
                    DiskMesh(mRadius, 0.0f, mSlices, mRings),
                    Vector3(0.0f, 0.0f, -mSize)
                )
            ),
            mU = true,
            mV = false
        )
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}
