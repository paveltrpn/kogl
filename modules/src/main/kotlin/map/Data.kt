package map

import kotlinx.serialization.*

// ============================================================================
// ======================= GroupPayload =======================================
// ============================================================================

@Serializable
data class GroupPayload(val children: MutableList<NodeData>) {}

// ============================================================================
// ======================= StateGroupPayload ==================================
// ============================================================================

@Serializable
data class StateGroupPayload(val program: String, val children: MutableList<NodeData>) {}

// ============================================================================
// ======================= TransformGroupPayload ==============================
// ============================================================================

@Serializable
data class TransformGroupPayload(
    val trnasform_type: String,
    val matrix: FloatArray,
    val data: FloatArray,
    val child: NodeData
) {}

// ============================================================================
// ======================= DrawablePayload ====================================
// ============================================================================

@Serializable
data class DrawablePayload(val mesh: String, val material: String) {}

// ============================================================================
// ======================= FlyaroundDrawablePayload ===========================
// ============================================================================

@Serializable
data class FlyaroundDrawablePayload(
    val mesh: String,
    val material: String,
    val origin: FloatArray,
    val axis: FloatArray,
    val anglSpeed: Float
) {}

// ============================================================================
// ======================= NodeData ===========================================
// ============================================================================

@Serializable
open class NodeData(open val type: String) {}

// ============================================================================
// ======================= GroupData ==========================================
// ============================================================================

data class GroupData(override val type: String, val payload: GroupPayload) :
    NodeData(type) {}

// ============================================================================
// ======================= StateGroupData =====================================
// ============================================================================

data class StateGroupData(override val type: String, val payload: StateGroupPayload) :
    NodeData(type) {}

// ============================================================================
// ======================= TransformGroupData =================================
// ============================================================================

data class TransformGroupData(override val type: String, val payload: TransformGroupPayload) :
    NodeData(type) {}

// ============================================================================
// ======================= DrawableData =======================================
// ============================================================================

data class DrawableData(override val type: String, val payload: DrawablePayload) :
    NodeData(type) {}

// ============================================================================
// ======================= FlyaroundDrawableData ==============================
// ============================================================================

data class FlyaroundDrawableData(override val type: String, val payload: FlyaroundDrawablePayload) :
    NodeData(type) {}
