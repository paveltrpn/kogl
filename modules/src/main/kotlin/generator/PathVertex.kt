package generator

import algebra.*

// ============================================================================
// ======================= PathVertex =========================================
// ============================================================================
class PathVertex {
    var mNormal: Vector3 = Vector3()
    var mPosition: Vector3 = Vector3()
    var mTangent: Vector3 = Vector3()
    var mTexCoord: Float = 0.0f

    // tangent x normal
    fun binormal(): Vector3 {
        return cross(mTangent, mNormal)
    }
}
