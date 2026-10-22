package generator

import algebra.Vector2

class RotateShape(
    shape: GeneratorShape,
    angle: Float
) : TransformShape(shape, rotateEval(angle))

private fun rotateEval(angle: Float): (ShapeVertex) -> Unit {
    val cosine = kotlin.math.cos(angle)
    val sine = kotlin.math.sin(angle)
    return { vertex ->
        vertex.mPosition = Vector2(
            vertex.mPosition.x * cosine - vertex.mPosition.y * sine,
            vertex.mPosition.x * sine + vertex.mPosition.y * cosine
        )
        vertex.mTangent = Vector2(
            vertex.mTangent.x * cosine - vertex.mTangent.y * sine,
            vertex.mTangent.x * sine + vertex.mTangent.y * cosine
        )
    }
}
