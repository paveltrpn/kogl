package generator

import algebra.*

// ============================================================================
// ======================= CircleShape ========================================
// ============================================================================

class CircleShape(
    mRadius: Float = 1.0f,
    mSegments: Int = 32,
    mStart: Float = 0.0f,
    mSweep: Float = toRadians(360.0f)
) : ParametricShape(_circleEval(mRadius, mStart, mSweep), mSegments)

private fun _circleEval(radius: Float, start: Float, sweep: Float): (Float) -> ShapeVertex {
    return { t: Float ->
        val angle = t * sweep + start
        val sine = kotlin.math.sin(angle)
        val cosine = kotlin.math.cos(angle)

        val vertex = ShapeVertex()
        vertex.mPosition = Vector2(radius * cosine, radius * sine)
        vertex.mTangent = Vector2(-sine, cosine)
        vertex.mTexCoord = t
        vertex
    }
}
