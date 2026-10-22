package graph

import algebra.*

// ============================================================================
// ======================= OmniLightData ======================================
// ============================================================================

data class OmniLightData(
    val position: Vector3,
    val color: Vector3,
    val intensity: Float,
    val radius: Float
)

// ============================================================================
// ======================= LightCollectorVisitor ==============================
// ============================================================================

class LightCollectorVisitor : TransformVisitor() {
    private val _omniLights = mutableListOf<OmniLightData>()

    val omniLights: List<OmniLightData>
        get(): List<OmniLightData> {
            return _omniLights
        }

    override fun apply(node: StateGroup): Unit {
        node.traverse(this)
    }

    override fun apply(node: Leaf): Unit {
        when (node) {
            is OmniLight -> {
                val position = mModelMatrixStack.top.vecMultiply(Vector3(0.0f, 0.0f, 0.0f))

                _omniLights.add(OmniLightData(position, node.color, node.intensity, node.radius))
            }
        }
    }
}
