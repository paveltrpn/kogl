package generator

import algebra.Vector2

class AxisSwapShape(
    shape: GeneratorShape
) : TransformShape(shape, axisSwapEval())

private fun axisSwapEval(): (ShapeVertex) -> Unit {
    return { vertex ->
        vertex.mPosition = Vector2(vertex.mPosition.y, vertex.mPosition.x)
        vertex.mTangent = Vector2(vertex.mTangent.y, vertex.mTangent.x)
    }
}
