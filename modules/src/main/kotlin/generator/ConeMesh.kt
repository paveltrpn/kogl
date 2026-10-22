package generator

import algebra.Vector2
import algebra.toRadians

class ConeMesh(
    mRadius: Float = 1.0f,
    mSize: Float = 1.0f,
    mSlices: Int = 32,
    mSegments: Int = 8,
    mStart: Float = 0.0f,
    mSweep: Float = toRadians(360.0f)
) : GeneratorMesh {

    private val _impl: GeneratorMesh = AxisSwapMesh(
        LatheMesh(
            LineShape(
                Vector2(mSize, 0.0f),
                Vector2(-mSize, mRadius),
                mSegments
            ),
            Vector2(1.0f, 0.0f),
            mSlices,
            mStart,
            mSweep
        ),
        Axis.Y, Axis.Z, Axis.X
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}
