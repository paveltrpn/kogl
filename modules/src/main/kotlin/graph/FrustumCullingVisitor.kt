package graph

import algebra.*
import mesh.*

// ============================================================================
// ======================= FrustumCullingVisitor ==============================
// ============================================================================

class FrustumCullingVisitor(viewMatrix: Matrix4) : TransformVisitor() {
    private val _frustum = Frustum(viewMatrix)

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
        when (node) {
            is Drawable -> {
                if (!node.hasMesh || node.mesh is InterleavedMesh) {
                    node.isFrustumCulled = false
                    return
                }

                node.isFrustumCulled = !_frustum.intersects(
                    node.mesh.boundingBox,
                    node.modelMatrix
                )
            }
        }
    }
}
