package generator

import algebra.Vector2

class ScaleShape(
    shape: GeneratorShape,
    scale: Vector2
) : TransformShape(shape, scaleEval(scale))

private fun scaleEval(scale: Vector2): (ShapeVertex) -> Unit {
    val scaleX = scale.x
    val scaleY = scale.y
    return { vertex ->
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
