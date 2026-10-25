package graph

import algebra.*
import mesh.*
import spatial.*

// ============================================================================
// ======================= IntersectionResult =================================
// ============================================================================

data class IntersectionResult(
    val drawable: Drawable,
    val position: Vector3,
    val distance: Float,
    val triangleIndex: Int
)

// ============================================================================
// ======================= IntersectionVisitor ================================
// ============================================================================

class IntersectionVisitor(private val _ray: Ray) : TransformVisitor() {
    private var _result: IntersectionResult? = null

    val result: IntersectionResult?
        get(): IntersectionResult? {
            return _result
        }

    override fun apply(node: Group): Unit {
        node.traverse(this)
    }

    override fun apply(node: Switch): Unit {
        node.traverse(this)
    }

    override fun apply(node: StateGroup): Unit {
        node.traverse(this)
    }

    override fun apply(node: Leaf): Unit {
        if (node !is Drawable || !node.hasMesh) {
            return
        }

        val mesh = node.mesh
        if (mesh !is TriangleSoupMesh || mesh.positions.size < 9) {
            return
        }

        val model = node.modelMatrix.copy()
        model.transpose()
        val worldBounds = transformBounds(mesh.boundingBox, model) ?: return
        if (!_ray.intersects(worldBounds)) {
            return
        }

        val positions = mesh.positions
        val triangleCount = positions.size / 9
        for (triangleIndex in 0 until triangleCount) {
            val offset = triangleIndex * 9
            val a = transformPoint(
                model,
                positions[offset],
                positions[offset + 1],
                positions[offset + 2]
            ) ?: continue
            val b = transformPoint(
                model,
                positions[offset + 3],
                positions[offset + 4],
                positions[offset + 5]
            ) ?: continue
            val c = transformPoint(
                model,
                positions[offset + 6],
                positions[offset + 7],
                positions[offset + 8]
            ) ?: continue

            val distance = _ray.intersectTriangle(a, b, c) ?: continue
            val current = _result
            if (!distance.isFinite() || distance < 0.0f) {
                continue
            }
            if (current != null && distance >= current.distance) {
                continue
            }

            _result = IntersectionResult(
                node,
                Vector3(
                    _ray.eye.x + _ray.direction.x * distance,
                    _ray.eye.y + _ray.direction.y * distance,
                    _ray.eye.z + _ray.direction.z * distance
                ),
                distance,
                triangleIndex
            )
        }
    }

    private fun transformBounds(bounds: AABB, model: Matrix4): AABB? {
        var minX = Float.POSITIVE_INFINITY
        var minY = Float.POSITIVE_INFINITY
        var minZ = Float.POSITIVE_INFINITY
        var maxX = Float.NEGATIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY
        var maxZ = Float.NEGATIVE_INFINITY

        for (cornerIndex in 0 until 8) {
            var x = bounds.min.x
            var y = bounds.min.y
            var z = bounds.min.z

            if ((cornerIndex and 1) != 0) {
                x = bounds.max.x
            }
            if ((cornerIndex and 2) != 0) {
                y = bounds.max.y
            }
            if ((cornerIndex and 4) != 0) {
                z = bounds.max.z
            }

            val point = transformPoint(model, x, y, z) ?: return null
            minX = minOf(minX, point.x)
            minY = minOf(minY, point.y)
            minZ = minOf(minZ, point.z)
            maxX = maxOf(maxX, point.x)
            maxY = maxOf(maxY, point.y)
            maxZ = maxOf(maxZ, point.z)
        }

        return AABB(Vector3(minX, minY, minZ), Vector3(maxX, maxY, maxZ))
    }

    private fun transformPoint(model: Matrix4, x: Float, y: Float, z: Float): Vector3? {
        if (!x.isFinite() || !y.isFinite() || !z.isFinite()) {
            return null
        }

        val point = model.vecMultiply(Vector3(x, y, z))
        if (!point.x.isFinite() || !point.y.isFinite() || !point.z.isFinite()) {
            return null
        }

        return point
    }
}
