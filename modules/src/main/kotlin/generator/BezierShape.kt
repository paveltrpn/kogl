package generator

import algebra.*

// ============================================================================
// ======================= BezierShape ========================================
// ============================================================================

class BezierShape(
    mControlPoints: List<Vector2>,
    mSegments: Int = 16
) : ParametricShape(_bezierEval(mControlPoints), mSegments)

private fun _bezierEval(controlPoints: List<Vector2>): (Float) -> ShapeVertex {
    require(controlPoints.size > 1) { "BezierShape requires more than one control point." }
    val count = controlPoints.size
    val xs = FloatArray(count) { index: Int -> controlPoints[index].x }
    val ys = FloatArray(count) { index: Int -> controlPoints[index].y }
    val epsilon = 1.1920929e-7f
    return { t: Float ->
        val vertex = ShapeVertex()
        vertex.mPosition = _bezierPoint(xs, ys, t)
        var tangent = _bezierTangent(xs, ys, t)
        if (dot(tangent, tangent) < epsilon) {
            tangent = _bezierTangent(xs, ys, t + 10.0f * epsilon)
        }
        vertex.mTangent = normalize(tangent)
        vertex.mTexCoord = t
        vertex
    }
}

private fun _bezierPoint(xs: FloatArray, ys: FloatArray, t: Float): Vector2 {
    val count = xs.size
    val tempX = xs.copyOf()
    val tempY = ys.copyOf()
    val u = 1.0f - t
    for (k in count - 1 downTo 1) {
        for (i in 0 until k) {
            tempX[i] = u * tempX[i] + t * tempX[i + 1]
            tempY[i] = u * tempY[i] + t * tempY[i + 1]
        }
    }
    return Vector2(tempX[0], tempY[0])
}

private fun _bezierTangent(xs: FloatArray, ys: FloatArray, t: Float): Vector2 {
    val count = xs.size
    val factor = (count - 1).toFloat()
    val dx = FloatArray(count - 1) { index: Int -> factor * (xs[index + 1] - xs[index]) }
    val dy = FloatArray(count - 1) { index: Int -> factor * (ys[index + 1] - ys[index]) }
    return _bezierPoint(dx, dy, t)
}
