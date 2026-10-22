package generator

import algebra.Vector2

class LineShape(
    start: Vector2,
    end: Vector2,
    segments: Int = 16
) : ParametricShape(lineEval(start, end), segments)

private fun lineEval(start: Vector2, end: Vector2): (Float) -> ShapeVertex {
    val sx = start.x
    val sy = start.y
    val dx = end.x - start.x
    val dy = end.y - start.y
    val tangent = normalize(Vector2(dx, dy))
    val tx = tangent.x
    val ty = tangent.y
    return { t ->
        val vertex = ShapeVertex()
        vertex.mPosition = Vector2(sx + t * dx, sy + t * dy)
        vertex.mTangent = Vector2(tx, ty)
        vertex.mTexCoord = t
        vertex
    }
}
