package generator

import algebra.*

// ============================================================================
// ======================= ScaleShape =========================================
// ============================================================================
class ScaleShape(
    shape: GeneratorShape,
    scale: Vector2
) : TransformShape(shape, _scaleEval(scale))

private fun _scaleEval(scale: Vector2): (ShapeVertex) -> Unit {
    val scaleX = scale.x
    val scaleY = scale.y
    return { vertex: ShapeVertex ->
        vertex.mPosition = Vector2(
            vertex.mPosition.x * scaleX,
            vertex.mPosition.y * scaleY
        )
        vertex.mTangent = normalize(Vector2(
            vertex.mTangent.x * scaleX,
            vertex.mTangent.y * scaleY
        ))
    }
}
