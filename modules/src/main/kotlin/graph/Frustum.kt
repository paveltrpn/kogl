package graph

import algebra.*
import mesh.*

// ============================================================================
// ======================= Frustum ============================================
// ============================================================================

class Frustum(viewMatrix: Matrix4) {
    private val _viewMatrix = viewMatrix.copy()

    init {
        _viewMatrix.transpose()
    }

    fun intersects(bounds: AABB, modelMatrix: Matrix4): Boolean {
        val transposedModel = modelMatrix.copy()
        transposedModel.transpose()

        val viewModel = _viewMatrix.multiply(transposedModel)

        var outsideLeft = true
        var outsideRight = true
        var outsideBottom = true
        var outsideTop = true
        var outsideNear = true
        var outsideFar = true

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

            val clip = viewModel.vecMultiply(Vector4(x, y, z, 1.0f))

            outsideLeft = outsideLeft && clip.x < -clip.w
            outsideRight = outsideRight && clip.x > clip.w
            outsideBottom = outsideBottom && clip.y < -clip.w
            outsideTop = outsideTop && clip.y > clip.w
            outsideNear = outsideNear && clip.z < -clip.w
            outsideFar = outsideFar && clip.z > clip.w

            if (!outsideLeft && !outsideRight &&
                !outsideBottom && !outsideTop &&
                !outsideNear && !outsideFar
            ) {
                return true
            }
        }

        return false
    }
}
