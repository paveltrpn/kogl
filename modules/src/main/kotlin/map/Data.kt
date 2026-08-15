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
data class TransformPayload(
    val trnasform_type: String,
    val matrix: FloatArray,
    val data: FloatArray,
    val child: NodeData
) {}

@Serializable
data class DrawablePayload(val mesh: String, val material: String, val data: FloatArray) {}

@Serializable
data class SpinableDrawablePayload(
    val mesh: String,
    val material: String,
    val data: FloatArray,
    val axis: FloatArray,
    val angl: Float
) {}

@Serializable
data class FlyaroundDrawablePayload(
    val mesh: String,
    val material: String,
    val data: FloatArray,
    val origin: FloatArray,
    val axis: FloatArray,
    val angl: Float,
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

data class TransformData(override val type: String, val payload: TransformPayload) :
    NodeData(type) {}

data class DrawableData(override val type: String, val payload: DrawablePayload) :
    NodeData(type) {}

data class SpinableDrawableData(override val type: String, val payload: SpinableDrawablePayload) :
    NodeData(type) {}

data class FlyaroundDrawableData(override val type: String, val payload: FlyaroundDrawablePayload) :
    NodeData(type) {}


