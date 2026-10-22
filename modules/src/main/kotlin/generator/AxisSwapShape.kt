package generator

import algebra.*

// ============================================================================
// ======================= AxisSwapShape ======================================
// ============================================================================

class AxisSwapShape(
    shape: GeneratorShape
) : TransformShape(shape, _axisSwapEval())

private fun _axisSwapEval(): (ShapeVertex) -> Unit {
    return { vertex: ShapeVertex ->
        vertex.mPosition = Vector2(vertex.mPosition.y, vertex.mPosition.x)
        vertex.mTangent = Vector2(vertex.mTangent.y, vertex.mTangent.x)
    }
}
