package generator

import algebra.*

// ============================================================================
// ======================= AxisSwapPath =======================================
// ============================================================================

class AxisSwapPath(
    path: GeneratorPath,
    mX: Axis,
    mY: Axis,
    mZ: Axis
) : TransformPath(path, _axisSwapPathEval(mX, mY, mZ))

private fun _axisSwapPathEval(x: Axis, y: Axis, z: Axis): (PathVertex) -> Unit {
    return { vertex: PathVertex ->
        vertex.mPosition = Vector3(
            componentAt(vertex.mPosition, x),
            componentAt(vertex.mPosition, y),
            componentAt(vertex.mPosition, z)
        )
        vertex.mTangent = Vector3(
            componentAt(vertex.mTangent, x),
            componentAt(vertex.mTangent, y),
            componentAt(vertex.mTangent, z)
        )
        vertex.mNormal = Vector3(
            componentAt(vertex.mNormal, x),
            componentAt(vertex.mNormal, y),
            componentAt(vertex.mNormal, z)
        )
    }
}
