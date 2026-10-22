package generator

import algebra.Vector2

class GridShape(
    mSize: Vector2 = Vector2(1.0f, 1.0f),
    mSegments: IntPair = IntPair(4, 4),
    mSubSegments: IntPair = IntPair(2, 2)
) : GeneratorShape {

    private val _impl: GeneratorShape

    init {
        val sizeX = mSize.x
        val sizeY = mSize.y
        val horizontalInstances: Int
        if (mSegments.mY < 1) {
            horizontalInstances = 0
        } else {
            horizontalInstances = mSegments.mY + 1
        }
        val verticalInstances: Int
        if (mSegments.mX < 1) {
            verticalInstances = 0
        } else {
            verticalInstances = mSegments.mX + 1
        }
        _impl = MergeShape(
            RepeatShape(
                LineShape(
                    Vector2(-sizeX, -sizeY),
                    Vector2(sizeX, -sizeY),
                    mSegments.mX * mSubSegments.mX
                ),
                horizontalInstances,
                Vector2(0.0f, 2.0f * sizeY / maxOf(mSegments.mY, 1))
            ),
            RepeatShape(
                LineShape(
                    Vector2(-sizeX, -sizeY),
                    Vector2(-sizeX, sizeY),
                    mSegments.mY * mSubSegments.mY
                ),
                verticalInstances,
                Vector2(2.0f * sizeX / maxOf(mSegments.mX, 1), 0.0f)
            )
        )
    }

    override fun vertices(): Sequence<ShapeVertex> = _impl.vertices()

    override fun edges(): Sequence<Edge> = _impl.edges()
}
