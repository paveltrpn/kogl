package generator

import algebra.Vector3
import algebra.toRadians

class KnotPath(
    mP: Int = 2,
    mQ: Int = 3,
    mSegments: Int = 96
) : ParametricPath(knotPathEval(mP, mQ), mSegments)

private fun knot(p: Int, q: Int, t: Float): Vector3 {
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

private fun knotPathEval(p: Int, q: Int): (Float) -> PathVertex {
    return { t ->
        val vertex = PathVertex()
        vertex.mPosition = knot(p, q, t)
        val prev = knot(p, q, t - 0.01f)
        val next = knot(p, q, t + 0.01f)
        val diff = sub(next, prev)
        vertex.mTangent = normalize(diff)
        vertex.mNormal = normalize(cross(diff, add(next, prev)))
        vertex.mTexCoord = t
        vertex
    }
}
