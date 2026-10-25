package spatial

import algebra.*
import graph.*
import mesh.*
import kotlin.math.*

private const val RAY_EPSILON = 1e-6

private fun isFiniteVector(vector: Vector3): Boolean {
    return vector.x.isFinite() && vector.y.isFinite() && vector.z.isFinite()
}

// ============================================================================
// ======================= Ray ================================================
// ============================================================================

class Ray(
    eye: Vector3,
    direction: Vector3,
    val near: Float = 0.0f,
    val far: Float = Float.POSITIVE_INFINITY
) {
    val eye: Vector3 = Vector3(eye.x, eye.y, eye.z)
    val direction: Vector3

    init {
        val x = direction.x.toDouble()
        val y = direction.y.toDouble()
        val z = direction.z.toDouble()
        val length = sqrt(x * x + y * y + z * z)

        if (length.isFinite() && length > 0.0) {
            this.direction = Vector3(
                (x / length).toFloat(),
                (y / length).toFloat(),
                (z / length).toFloat()
            )
        } else {
            this.direction = Vector3()
        }
    }

    companion object {
        fun fromScreen(
            camera: Flycam,
            posX: Int,
            posY: Int,
            viewportWidth: Int,
            viewportHeight: Int
        ): Ray? {
            if (viewportWidth <= 0 || viewportHeight <= 0) {
                return null
            }
            if (posX < 0 || posX >= viewportWidth || posY < 0 || posY >= viewportHeight) {
                return null
            }

            val eye = camera.eye.inversed()
            if (!isFiniteVector(eye)) {
                return null
            }

            val ndcX = (2.0 * posX.toDouble() / viewportWidth.toDouble() - 1.0).toFloat()
            val ndcY = (1.0 - 2.0 * posY.toDouble() / viewportHeight.toDouble()).toFloat()
            val combined = camera.viewMatrix.copy()
            combined.transpose()
            val inverse = combined.inverted() ?: return null

            val nearPoint = unproject(inverse, ndcX, ndcY, -1.0f) ?: return null
            val farPoint = unproject(inverse, ndcX, ndcY, 1.0f) ?: return null

            val nearX = nearPoint.x.toDouble() - eye.x.toDouble()
            val nearY = nearPoint.y.toDouble() - eye.y.toDouble()
            val nearZ = nearPoint.z.toDouble() - eye.z.toDouble()
            val farX = farPoint.x.toDouble() - eye.x.toDouble()
            val farY = farPoint.y.toDouble() - eye.y.toDouble()
            val farZ = farPoint.z.toDouble() - eye.z.toDouble()

            val nearDistance = sqrt(nearX * nearX + nearY * nearY + nearZ * nearZ)
            val farDistance = sqrt(farX * farX + farY * farY + farZ * farZ)
            val directionLength = sqrt(farX * farX + farY * farY + farZ * farZ)

            if (!nearDistance.isFinite() || !farDistance.isFinite() || !directionLength.isFinite()) {
                return null
            }
            if (nearDistance < 0.0 || farDistance <= nearDistance || directionLength <= 0.0) {
                return null
            }

            val direction = Vector3(
                (farX / directionLength).toFloat(),
                (farY / directionLength).toFloat(),
                (farZ / directionLength).toFloat()
            )
            if (!isFiniteVector(direction)) {
                return null
            }

            val nearLimit = nearDistance.toFloat()
            val farLimit = farDistance.toFloat()
            if (!nearLimit.isFinite() || !farLimit.isFinite()) {
                return null
            }

            return Ray(eye, direction, nearLimit, farLimit)
        }

        private fun unproject(matrix: Matrix4, x: Float, y: Float, z: Float): Vector3? {
            val point = matrix.vecMultiply(Vector4(x, y, z, 1.0f))
            if (!point.x.isFinite() || !point.y.isFinite() ||
                !point.z.isFinite() || !point.w.isFinite()
            ) {
                return null
            }
            if (abs(point.w.toDouble()) <= RAY_EPSILON) {
                return null
            }

            val result = Vector3(point.x / point.w, point.y / point.w, point.z / point.w)
            if (!isFiniteVector(result)) {
                return null
            }

            return result
        }
    }

    fun intersects(bounds: AABB): Boolean {
        if (!hasValidInterval() || !isFiniteVector(eye) || !hasValidDirection()) {
            return false
        }
        if (!isFiniteVector(bounds.min) || !isFiniteVector(bounds.max)) {
            return false
        }

        var entry = near.toDouble()
        var exit = far.toDouble()

        for (axis in 0 until 3) {
            val origin = eye[axis].toDouble()
            val directionComponent = direction[axis].toDouble()
            val minimum = bounds.min[axis].toDouble()
            val maximum = bounds.max[axis].toDouble()

            if (abs(directionComponent) <= RAY_EPSILON) {
                if (origin < minimum || origin > maximum) {
                    return false
                }
                continue
            }

            var first = (minimum - origin) / directionComponent
            var second = (maximum - origin) / directionComponent
            if (first > second) {
                val temporary = first
                first = second
                second = temporary
            }

            entry = maxOf(entry, first)
            exit = minOf(exit, second)
            if (entry > exit + RAY_EPSILON) {
                return false
            }
        }

        return entry <= exit + RAY_EPSILON
    }

    fun intersectTriangle(a: Vector3, b: Vector3, c: Vector3): Float? {
        if (!hasValidInterval() || !isFiniteVector(eye) || !hasValidDirection() ||
            !isFiniteVector(a) || !isFiniteVector(b) || !isFiniteVector(c)
        ) {
            return null
        }

        val edge1X = b.x.toDouble() - a.x.toDouble()
        val edge1Y = b.y.toDouble() - a.y.toDouble()
        val edge1Z = b.z.toDouble() - a.z.toDouble()
        val edge2X = c.x.toDouble() - a.x.toDouble()
        val edge2Y = c.y.toDouble() - a.y.toDouble()
        val edge2Z = c.z.toDouble() - a.z.toDouble()

        val directionX = direction.x.toDouble()
        val directionY = direction.y.toDouble()
        val directionZ = direction.z.toDouble()
        val pX = directionY * edge2Z - directionZ * edge2Y
        val pY = directionZ * edge2X - directionX * edge2Z
        val pZ = directionX * edge2Y - directionY * edge2X
        val determinant = edge1X * pX + edge1Y * pY + edge1Z * pZ

        if (!determinant.isFinite() || abs(determinant) <= RAY_EPSILON) {
            return null
        }

        val inverseDeterminant = 1.0 / determinant
        val offsetX = eye.x.toDouble() - a.x.toDouble()
        val offsetY = eye.y.toDouble() - a.y.toDouble()
        val offsetZ = eye.z.toDouble() - a.z.toDouble()
        val u = (offsetX * pX + offsetY * pY + offsetZ * pZ) * inverseDeterminant

        if (!u.isFinite() || u < -RAY_EPSILON || u > 1.0 + RAY_EPSILON) {
            return null
        }

        val qX = offsetY * edge1Z - offsetZ * edge1Y
        val qY = offsetZ * edge1X - offsetX * edge1Z
        val qZ = offsetX * edge1Y - offsetY * edge1X
        val v = (directionX * qX + directionY * qY + directionZ * qZ) * inverseDeterminant

        if (!v.isFinite() || v < -RAY_EPSILON || u + v > 1.0 + RAY_EPSILON) {
            return null
        }

        val distance = (edge2X * qX + edge2Y * qY + edge2Z * qZ) * inverseDeterminant
        if (!distance.isFinite() || distance < -RAY_EPSILON) {
            return null
        }
        if (distance < near.toDouble() - RAY_EPSILON ||
            distance > far.toDouble() + RAY_EPSILON
        ) {
            return null
        }

        return maxOf(0.0, distance).toFloat()
    }

    private fun hasValidInterval(): Boolean {
        return near.isFinite() && !far.isNaN() && near >= 0.0f && far >= near
    }

    private fun hasValidDirection(): Boolean {
        if (!isFiniteVector(direction)) {
            return false
        }

        return direction.x != 0.0f || direction.y != 0.0f || direction.z != 0.0f
    }
}
