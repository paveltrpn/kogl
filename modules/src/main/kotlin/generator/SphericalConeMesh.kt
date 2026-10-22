package generator

import algebra.Vector3
import algebra.toRadians
import kotlin.math.asin
import kotlin.math.sqrt

class SphericalConeMesh(
    mRadius: Float = 1.0f,
    mSize: Float = 1.0f,
    mSlices: Int = 32,
    mSegments: Int = 8,
    mRings: Int = 4,
    mStart: Float = 0.0f,
    mSweep: Float = toRadians(360.0f)
) : GeneratorMesh {

    private val _impl: GeneratorMesh = TranslateMesh(
        MergeMesh(
            ConeMesh(
                mRadius,
                calcSphericalConeSize(mRadius, mSize),
                mSlices,
                mSegments,
                mStart,
                mSweep
            ),
            AxisFlipMesh(
                TranslateMesh(
                    SphereMesh(
                        2.0f * mSize,
                        mSlices,
                        mRings,
                        mStart,
                        mSweep,
                        0.0f,
                        asin(mRadius / (2.0f * mSize))
                    ),
                    Vector3(0.0f, 0.0f, -calcSphericalConeSize(mRadius, mSize))
                ),
                false,
                false,
                true
            )
        ),
        Vector3(0.0f, 0.0f, mSize - calcSphericalConeSize(mRadius, mSize))
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}

private fun calcSphericalConeSize(radius: Float, size: Float): Float {
    return 0.5f * sqrt(2.0f * size * 2.0f * size - radius * radius)
}
