package generator

import algebra.Vector3
import algebra.toRadians

class HelixPath(
    mRadius: Float = 1.0f,
    mSize: Float = 1.0f,
    mSegments: Int = 32,
    mStart: Float = 0.0f,
    mSweep: Float = toRadians(720.0f)
) : ParametricPath(helixPathEval(mRadius, mSize, mStart, mSweep), mSegments)

private fun helixPathEval(
    radius: Float,
    size: Float,
    start: Float,
    sweep: Float
): (Float) -> PathVertex {
    return { t ->
        val vertex = PathVertex()
        val angle = start + t * sweep
        val sine = kotlin.math.sin(angle)
        val cosine = kotlin.math.cos(angle)
        vertex.mPosition = Vector3(
            radius * cosine,
            radius * sine,
            2.0f * t * size - size
        )
        vertex.mTangent = normalize(Vector3(
            -radius * sine,
            radius * cosine,
            2.0f * size / sweep
        ))
        vertex.mNormal = Vector3(cosine, sine, 0.0f)
        vertex.mTexCoord = t
        vertex
    }
}
