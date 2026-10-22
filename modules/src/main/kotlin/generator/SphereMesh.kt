package generator

import algebra.Vector2
import algebra.toRadians

class SphereMesh(
    mRadius: Float = 1.0f,
    mSlices: Int = 32,
    mSegments: Int = 16,
    mSliceStart: Float = 0.0f,
    mSliceSweep: Float = toRadians(360.0f),
    mSegmentStart: Float = 0.0f,
    mSegmentSweep: Float = toRadians(180.0f)
) : GeneratorMesh {

    private val _impl: GeneratorMesh = AxisSwapMesh(
        LatheMesh(
            CircleShape(mRadius, mSegments, mSegmentStart, mSegmentSweep),
            Vector2(1.0f, 0.0f),
            mSlices,
            mSliceStart,
            mSliceSweep
        ),
        Axis.Y, Axis.Z, Axis.X
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}
