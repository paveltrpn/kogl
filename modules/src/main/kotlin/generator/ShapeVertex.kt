package generator

import algebra.Vector2

class ShapeVertex {
    var mPosition: Vector2 = Vector2()
    var mTangent: Vector2 = Vector2()
    var mTexCoord: Float = 0.0f

    // Tangent rotated 90 degrees clockwise.
    fun normal(): Vector2 {
        return Vector2(mTangent.y, -mTangent.x)
    }
}
