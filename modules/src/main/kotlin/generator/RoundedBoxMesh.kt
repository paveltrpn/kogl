package generator

import algebra.*

// ============================================================================
// ======================= BoxEdge ============================================
// ============================================================================
internal class BoxEdge(
    position: Vector2,
    radius: Float,
    size: Float,
    slices: Int,
    segments: Int
) : GeneratorMesh {

    private val _impl: GeneratorMesh = TranslateMesh(
        CylinderMesh(radius, size, slices, segments, 0.0f, toRadians(90.0f)),
        Vector3(position.x, position.y, 0.0f)
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}

// ============================================================================
// ======================= BoxEdges ===========================================
// ============================================================================
internal class BoxEdges(
    size: Vector3,
    radius: Float,
    slices: Int,
    segments: Int
) : GeneratorMesh {

    private val _impl: GeneratorMesh = MirrorMesh(
        MirrorMesh(
            BoxEdge(Vector2(size.x, size.y), radius, size.z, slices, segments),
            Axis.Y
        ),
        Axis.X
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}

// ============================================================================
// ======================= BoxCorner ==========================================
// ============================================================================
internal class BoxCorner(
    position: Vector3,
    radius: Float,
    slices: Int
) : GeneratorMesh {

    private val _impl: GeneratorMesh = TranslateMesh(
        SphericalTriangleMesh(
            Vector3(radius, 0.0f, 0.0f),
            Vector3(0.0f, radius, 0.0f),
            Vector3(0.0f, 0.0f, radius),
            slices
        ),
        position
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}

// ============================================================================
// ======================= BoxCorners =========================================
// ============================================================================
internal class BoxCorners(
    size: Vector3,
    radius: Float,
    slices: Int
) : GeneratorMesh {

    private val _impl: GeneratorMesh = MirrorMesh(
        MirrorMesh(
            MirrorMesh(
                BoxCorner(size, radius, slices),
                Axis.X
            ),
            Axis.Y
        ),
        Axis.Z
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}

// ============================================================================
// ======================= RoundedBoxMesh =====================================
// ============================================================================
class RoundedBoxMesh(
    mRadius: Float = 0.25f,
    mSize: Vector3 = Vector3(0.75f, 0.75f, 0.75f),
    mSlices: Int = 4,
    mSegments: IntTriple = IntTriple(8, 8, 8)
) : GeneratorMesh {

    private val _impl: GeneratorMesh = MergeMesh(
        AxisSwapMesh(
            BoxFaces(
                Vector2(mSize.y, mSize.z),
                IntPair(mSegments.mY, mSegments.mZ),
                mSize.x + mRadius
            ),
            Axis.Z, Axis.X, Axis.Y
        ),
        UvFlipMesh(
            AxisSwapMesh(
                BoxFaces(
                    Vector2(mSize.x, mSize.z),
                    IntPair(mSegments.mX, mSegments.mZ),
                    mSize.y + mRadius
                ),
                Axis.X, Axis.Z, Axis.Y
            ),
            mU = true,
            mV = false
        ),
        BoxFaces(
            Vector2(mSize.x, mSize.y),
            IntPair(mSegments.mX, mSegments.mY),
            mSize.z + mRadius
        ),
        AxisSwapMesh(
            BoxEdges(
                Vector3(mSize.z, mSize.y, mSize.x),
                mRadius,
                mSlices,
                mSegments.mX
            ),
            Axis.Z, Axis.Y, Axis.X
        ),
        AxisSwapMesh(
            BoxEdges(
                Vector3(mSize.x, mSize.z, mSize.y),
                mRadius,
                mSlices,
                mSegments.mY
            ),
            Axis.X, Axis.Z, Axis.Y
        ),
        BoxEdges(mSize, mRadius, mSlices, mSegments.mZ),
        BoxCorners(mSize, mRadius, mSlices)
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}
