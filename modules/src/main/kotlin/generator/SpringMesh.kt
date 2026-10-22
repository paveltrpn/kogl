package generator

import algebra.toRadians

class SpringMesh(
    mMinor: Float = 0.25f,
    mMajor: Float = 1.0f,
    mSize: Float = 1.0f,
    mSlices: Int = 8,
    mSegments: Int = 32,
    mMinorStart: Float = 0.0f,
    mMinorSweep: Float = toRadians(360.0f),
    mMajorStart: Float = 0.0f,
    mMajorSweep: Float = toRadians(720.0f)
) : GeneratorMesh {

    private val _impl: GeneratorMesh = ExtrudeMesh(
        CircleShape(mMinor, mSlices, mMinorStart, mMinorSweep),
        HelixPath(mMajor, mSize, mSegments, mMajorStart, mMajorSweep)
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}
