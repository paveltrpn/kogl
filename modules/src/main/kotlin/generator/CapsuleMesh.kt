package generator

import algebra.Vector3
import algebra.toRadians

class CapsuleMesh(
    mRadius: Float = 1.0f,
    mSize: Float = 0.5f,
    mSlices: Int = 32,
    mSegments: Int = 4,
    mRings: Int = 8,
    mStart: Float = 0.0f,
    mSweep: Float = toRadians(360.0f)
) : GeneratorMesh {

    private val _impl: GeneratorMesh = MergeMesh(
        CylinderMesh(mRadius, mSize, mSlices, mSegments, mStart, mSweep),
        TranslateMesh(
            SphereMesh(mRadius, mSlices, mRings, mStart, mSweep, 0.0f, toRadians(90.0f)),
            Vector3(0.0f, 0.0f, mSize)
        ),
        TranslateMesh(
            SphereMesh(mRadius, mSlices, mRings, mStart, mSweep, toRadians(90.0f), toRadians(90.0f)),
            Vector3(0.0f, 0.0f, -mSize)
        )
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}
