package generator

import algebra.*

// ============================================================================
// ======================= LinePath ===========================================
// ============================================================================
class LinePath(
    mStart: Vector3 = Vector3(0.0f, 0.0f, -1.0f),
    mEnd: Vector3 = Vector3(0.0f, 0.0f, 1.0f),
    mNormal: Vector3 = Vector3(1.0f, 0.0f, 0.0f),
    mSegments: Int = 8
) : ParametricPath(_linePathEval(mStart, mEnd, mNormal), mSegments)

private fun _linePathEval(start: Vector3, end: Vector3, normal: Vector3): (Float) -> PathVertex {
    val sx = start.x
    val sy = start.y
    val sz = start.z
    val dx = end.x - start.x
    val dy = end.y - start.y
    val dz = end.z - start.z
    val tangent = normalize(Vector3(dx, dy, dz))
    val tx = tangent.x
    val ty = tangent.y
    val tz = tangent.z
    val nx = normal.x
    val ny = normal.y
    val nz = normal.z
    return { t: Float ->
        val vertex = PathVertex()
        vertex.mPosition = Vector3(sx + t * dx, sy + t * dy, sz + t * dz)
        vertex.mTangent = Vector3(tx, ty, tz)
        vertex.mNormal = Vector3(nx, ny, nz)
        vertex.mTexCoord = t
        vertex
    }
}
