package generator

import algebra.Vector2

class TranslateShape(
    shape: GeneratorShape,
    delta: Vector2
) : TransformShape(shape, translateEval(delta))

private fun translateEval(delta: Vector2): (ShapeVertex) -> Unit {
    val dx = delta.x
    val dy = delta.y
    return { vertex ->
        vertex.mPosition = Vector2(
            vertex.mPosition.x + dx,
            vertex.mPosition.y + dy
        )
    }
}
