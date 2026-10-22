package generator

import algebra.*

// ============================================================================
// ======================= RotatePath =========================================
// ============================================================================
class RotatePath : TransformPath {

    constructor(path: GeneratorPath, mAngle: Float, mAxis: Vector3) :
        super(path, _rotatePathEval(mAngle, mAxis))

    constructor(path: GeneratorPath, mAngle: Float, mAxis: Axis) :
        super(path, _rotatePathEval(mAngle, _axisVector(mAxis)))
}

private fun _axisVector(axis: Axis): Vector3 {
    if (axis == Axis.X) {
        return Vector3(1.0f, 0.0f, 0.0f)
    }
    if (axis == Axis.Y) {
        return Vector3(0.0f, 1.0f, 0.0f)
    }
    return Vector3(0.0f, 0.0f, 1.0f)
}

private fun _rotatePathEval(angle: Float, axis: Vector3): (PathVertex) -> Unit {
    val ax = axis.x
    val ay = axis.y
    val az = axis.z
    return { vertex: PathVertex ->
        val rotAxis = Vector3(ax, ay, az)
        vertex.mPosition = rotate(vertex.mPosition, rotAxis, angle)
        vertex.mNormal = rotate(vertex.mNormal, rotAxis, angle)
    }
}
