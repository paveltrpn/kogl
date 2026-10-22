package generator

import algebra.*

// ============================================================================
// ======================= KnotPath ===========================================
// ============================================================================
class KnotPath(
    mP: Int = 2,
    mQ: Int = 3,
    mSegments: Int = 96
) : ParametricPath(_knotPathEval(mP, mQ), mSegments)

private fun _knot(p: Int, q: Int, t: Float): Vector3 {
    val angle = t * toRadians(360.0f)
    val pt = p * angle
    val qt = q * angle
    val sinqt = kotlin.math.sin(qt)
    val r = 0.5f * (2.0f + sinqt)
    return Vector3(
        r * kotlin.math.cos(pt),
        r * kotlin.math.sin(pt),
        r * kotlin.math.cos(qt)
    )
}

private fun _knotPathEval(p: Int, q: Int): (Float) -> PathVertex {
    return { t: Float ->
        val vertex = PathVertex()
        vertex.mPosition = _knot(p, q, t)
        val prev = _knot(p, q, t - 0.01f)
        val next = _knot(p, q, t + 0.01f)
        val diff = sub(next, prev)
        vertex.mTangent = normalize(diff)
        vertex.mNormal = normalize(cross(diff, add(next, prev)))
        vertex.mTexCoord = t
        vertex
    }
}
