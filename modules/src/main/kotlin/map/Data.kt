package map

// ============================================================================
// ======================= Payload ============================================
// ============================================================================

data class GroupPayload(val children: MutableList<NodeData>) {}

data class StateGroupPayload(val program: String, val children: MutableList<NodeData>) {}

data class TransformPayload(
    val trnasform_type: String,
    val matrix: FloatArray,
    val data: FloatArray,
    val child: NodeData
) {}

data class DrawablePayload(val mesh: String, val material: String) {}

// ============================================================================
// ======================= Data ===============================================
// ============================================================================

open class NodeData(open val type: String) {}

data class GroupData(override val type: String, val payload: GroupPayload) :
    NodeData(type) {}

data class StateGroupData(override val type: String, val payload: StateGroupPayload) :
    NodeData(type) {}

data class TransformData(override val type: String, val payload: TransformPayload) :
    NodeData(type) {}

data class Drawable(override val type: String, val payload: DrawablePayload) :
    NodeData(type) {}


