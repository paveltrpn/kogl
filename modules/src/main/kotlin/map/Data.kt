package map

import kotlinx.serialization.Serializable

// ============================================================================
// ======================= Payload ============================================
// ============================================================================

@Serializable
data class GroupPayload(val children: MutableList<NodeData>) {}

@Serializable
data class StateGroupPayload(val program: String, val children: MutableList<NodeData>) {}

@Serializable
data class TransformGroupPayload(
    val trnasform_type: String,
    val matrix: FloatArray,
    val data: FloatArray,
    val child: NodeData
) {}

@Serializable
data class DrawablePayload(val mesh: String, val material: String) {}

@Serializable
data class FlyaroundDrawablePayload(
    val mesh: String,
    val material: String,
    val origin: FloatArray,
    val axis: FloatArray,
    val anglSpeed: Float
) {}

// ============================================================================
// ======================= Data ===============================================
// ============================================================================

@Serializable
open class NodeData(open val type: String) {}

data class GroupData(override val type: String, val payload: GroupPayload) :
    NodeData(type) {}

data class StateGroupData(override val type: String, val payload: StateGroupPayload) :
    NodeData(type) {}

data class TransformGroupData(override val type: String, val payload: TransformGroupPayload) :
    NodeData(type) {}

data class DrawableData(override val type: String, val payload: DrawablePayload) :
    NodeData(type) {}

data class FlyaroundDrawableData(override val type: String, val payload: FlyaroundDrawablePayload) :
    NodeData(type) {}


