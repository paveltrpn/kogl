package graph

import algebra.Matrix4
import algebra.Vector3

data class OmniLightData(
    val position: Vector3,
    val color: Vector3,
    val intensity: Float,
    val radius: Float
)

// ============================================================================
// ======================= LightCollectorVisitor ==============================
// ============================================================================

class LightCollectorVisitor : DrawCallVisitorBase(1.0f, Matrix4().idtt()) {
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
                val position = _modelMatrixStack.top.vecMultiply(Vector3(0.0f, 0.0f, 0.0f))

                _omniLights.add(OmniLightData(position, node.color, node.intensity, node.radius))
            }
        }
    }
}
