package generator

import algebra.*

// ============================================================================
// ======================= TranslateShape =====================================
// ============================================================================
class TranslateShape(
    shape: GeneratorShape,
    delta: Vector2
) : TransformShape(shape, _translateEval(delta))

private fun _translateEval(delta: Vector2): (ShapeVertex) -> Unit {
    val dx = delta.x
    val dy = delta.y
    return { vertex: ShapeVertex ->
        vertex.mPosition = Vector2(
            vertex.mPosition.x + dx,
            vertex.mPosition.y + dy
        )
    }
}
