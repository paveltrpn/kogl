package generator

import algebra.*

// ============================================================================
// ======================= BoxFace ============================================
// ============================================================================

internal class BoxFace(
    size: Vector2,
    segments: IntPair,
    delta: Float
) : GeneratorMesh {

    private val _impl: GeneratorMesh = TranslateMesh(
        PlaneMesh(size, segments),
        Vector3(0.0f, 0.0f, delta)
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}

// ============================================================================
// ======================= BoxFaces ===========================================
// ============================================================================

internal class BoxFaces(
    size: Vector2,
    segments: IntPair,
    delta: Float
) : GeneratorMesh {

    private val _impl: GeneratorMesh = MergeMesh(
        BoxFace(size, segments, delta),
        UvFlipMesh(
            FlipMesh(BoxFace(size, segments, -delta)),
            mU = true,
            mV = false
        )
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}

// ============================================================================
// ======================= BoxMesh ============================================
// ============================================================================

class BoxMesh(
    mSize: Vector3 = Vector3(1.0f, 1.0f, 1.0f),
    mSegments: IntTriple = IntTriple(8, 8, 8)
) : GeneratorMesh {

    private val _impl: GeneratorMesh = MergeMesh(
        AxisSwapMesh(
            BoxFaces(
                Vector2(mSize.y, mSize.z),
                IntPair(mSegments.mY, mSegments.mZ),
                mSize.x
            ),
            Axis.Z, Axis.X, Axis.Y
        ),
        UvFlipMesh(
            AxisSwapMesh(
                BoxFaces(
                    Vector2(mSize.x, mSize.z),
                    IntPair(mSegments.mX, mSegments.mZ),
                    mSize.y
                ),
                Axis.X, Axis.Z, Axis.Y
            ),
            mU = true,
            mV = false
        ),
        BoxFaces(
            Vector2(mSize.x, mSize.y),
            IntPair(mSegments.mX, mSegments.mY),
            mSize.z
        )
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}
