package generator

import algebra.*

// ============================================================================
// ======================= TorusMesh ==========================================
// ============================================================================
class TorusMesh(
    mMinor: Float = 0.25f,
    mMajor: Float = 1.0f,
    mSlices: Int = 16,
    mSegments: Int = 32,
    mMinorStart: Float = 0.0f,
    mMinorSweep: Float = toRadians(360.0f),
    mMajorStart: Float = 0.0f,
    mMajorSweep: Float = toRadians(360.0f)
) : GeneratorMesh {

    private val _impl: GeneratorMesh = AxisSwapMesh(
        LatheMesh(
            TranslateShape(
                CircleShape(mMinor, mSlices, mMinorStart + toRadians(90.0f), mMinorSweep),
                Vector2(0.0f, mMajor)
            ),
            Vector2(1.0f, 0.0f),
            mSegments,
            mMajorStart,
            mMajorSweep
        ),
        Axis.Y, Axis.Z, Axis.X
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}
