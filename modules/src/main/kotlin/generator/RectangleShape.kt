package generator

import algebra.Vector2

class RectangleShape(
    mSize: Vector2 = Vector2(1.0f, 1.0f),
    mSegments: IntPair = IntPair(8, 8)
) : GeneratorShape {

    private val _impl: GeneratorShape

    init {
        val sizeX = mSize.x
        val sizeY = mSize.y
        _impl = MergeShape(
            LineShape(Vector2(sizeX, -sizeY), Vector2(sizeX, sizeY), mSegments.mY),
            LineShape(Vector2(sizeX, sizeY), Vector2(-sizeX, sizeY), mSegments.mX),
            LineShape(Vector2(-sizeX, sizeY), Vector2(-sizeX, -sizeY), mSegments.mY),
            LineShape(Vector2(-sizeX, -sizeY), Vector2(sizeX, -sizeY), mSegments.mX)
        )
    }

    override fun vertices(): Sequence<ShapeVertex> = _impl.vertices()

    override fun edges(): Sequence<Edge> = _impl.edges()
}
