package mesh

import algebra.*

// ============================================================================
// ======================= AABB ===============================================
// ============================================================================

data class AABB(
    val min: Vector3 = Vector3(),
    val max: Vector3 = Vector3()
) {
    val center: Vector3
        get() = Vector3(
            (min.x + max.x) / 2.0f,
            (min.y + max.y) / 2.0f,
            (min.z + max.z) / 2.0f
        )

    val size: Vector3
        get() {
            return Vector3(max.x - min.x, max.y - min.y, max.z - min.z)
        }

    fun contains(point: Vector3): Boolean {
        return point.x in min.x..max.x &&
                point.y in min.y..max.y &&
                point.z in min.z..max.z
    }

    fun intersects(other: AABB): Boolean {
        return min.x <= other.max.x && max.x >= other.min.x &&
                min.y <= other.max.y && max.y >= other.min.y &&
                min.z <= other.max.z && max.z >= other.min.z
    }
}