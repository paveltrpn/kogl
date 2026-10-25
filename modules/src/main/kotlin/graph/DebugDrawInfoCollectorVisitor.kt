package graph

import algebra.*

// ============================================================================
// ======================= DebugLightInfo =====================================
// ============================================================================

data class DebugLightInfo(
    val modelMatrix: Matrix4,
    val color: Vector3
)

// ============================================================================
// ======================= DebugAABBInfo ======================================
// ============================================================================

data class DebugAABBInfo(
    val modelMatrix: Matrix4
)

// ============================================================================
// ======================= DebugDrawInfo ======================================
// ============================================================================

data class DebugDrawInfo(
    val lights: List<DebugLightInfo>,
    val aabbs: List<DebugAABBInfo>
)

// ============================================================================
// ======================= DebugDrawInfoCollectorVisitor ======================
// ============================================================================

class DebugDrawInfoCollectorVisitor : TransformVisitor() {
    private val _lights = mutableListOf<DebugLightInfo>()
    private val _aabbs = mutableListOf<DebugAABBInfo>()

    val info: DebugDrawInfo
        get(): DebugDrawInfo {
            return DebugDrawInfo(_lights, _aabbs)
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
        when (node) {
            is OmniLight -> {
                val position = mModelMatrixStack.top.vecMultiply(Vector3(0.0f, 0.0f, 0.0f))

                val offset = algebra.offset(position)
                offset.transpose()

                val model = algebra.scale(0.3f).multiply(offset)
                val color = Vector3(node.color.x, node.color.y, node.color.z)

                _lights.add(DebugLightInfo(model, color))
            }

            is Drawable -> {
                if (node.hasMesh) {
                    val box = node.mesh.boundingBox

                    val offset = algebra.offset(box.min)
                    offset.transpose()

                    val boxLocal = algebra.scale(box.size).multiply(offset)
                    val model = boxLocal.multiply(node.modelMatrix)

                    _aabbs.add(DebugAABBInfo(model))
                }
            }
        }
    }
}
