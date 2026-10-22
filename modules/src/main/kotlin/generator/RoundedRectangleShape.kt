package generator

import algebra.*

// ============================================================================
// ======================= RoundedRectangleShape ==============================
// ============================================================================
class RoundedRectangleShape(
    mRadius: Float = 0.25f,
    mSize: Vector2 = Vector2(0.75f, 0.75f),
    mSlices: Int = 4,
    mSegments: IntPair = IntPair(8, 8)
) : GeneratorShape {

    private val _impl: GeneratorShape

    init {
        val sizeX = mSize.x
        val sizeY = mSize.y
        _impl = MergeShape(
            LineShape(
                Vector2(sizeX + mRadius, -sizeY),
                Vector2(sizeX + mRadius, sizeY),
                mSegments.mY
            ),
            TranslateShape(
                CircleShape(mRadius, mSlices, 0.0f, toRadians(90.0f)),
                Vector2(sizeX, sizeY)
            ),
            LineShape(
                Vector2(sizeX, sizeY + mRadius),
                Vector2(-sizeX, sizeY + mRadius),
                mSegments.mX
            ),
            TranslateShape(
                CircleShape(mRadius, mSlices, toRadians(90.0f), toRadians(90.0f)),
                Vector2(-sizeX, sizeY)
            ),
            LineShape(
                Vector2(-sizeX - mRadius, sizeY),
                Vector2(-sizeX - mRadius, -sizeY),
                mSegments.mY
            ),
            TranslateShape(
                CircleShape(mRadius, mSlices, toRadians(180.0f), toRadians(90.0f)),
                Vector2(-sizeX, -sizeY)
            ),
            LineShape(
                Vector2(-sizeX, -sizeY - mRadius),
                Vector2(sizeX, -sizeY - mRadius),
                mSegments.mX
            ),
            TranslateShape(
                CircleShape(mRadius, mSlices, toRadians(270.0f), toRadians(90.0f)),
                Vector2(sizeX, -sizeY)
            )
        )
    }

    override fun vertices(): Sequence<ShapeVertex> = _impl.vertices()

    override fun edges(): Sequence<Edge> = _impl.edges()
}
