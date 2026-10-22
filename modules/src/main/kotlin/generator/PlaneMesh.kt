package generator

import algebra.*

// ============================================================================
// ======================= PlaneMesh ==========================================
// ============================================================================
internal class PlaneMesh(
    size: Vector2,
    segments: IntPair
) : ParametricMesh(_planeEval(size), segments)

private fun _planeEval(size: Vector2): (Vector2) -> MeshVertex {
    val sx = size.x
    val sy = size.y
    return { t: Vector2 ->
        val vertex = MeshVertex()
        vertex.mPosition = Vector3(
            -sx + 2.0f * t.x * sx,
            -sy + 2.0f * t.y * sy,
            0.0f
        )
        vertex.mNormal = Vector3(0.0f, 0.0f, 1.0f)
        vertex.mTexCoord = Vector2(t.x, t.y)
        vertex
    }
}
