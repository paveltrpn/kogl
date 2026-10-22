package generator

import algebra.Vector3

class ScalePath(
    path: GeneratorPath,
    mScale: Vector3
) : TransformPath(path, scalePathEval(mScale))

private fun scalePathEval(scaleFactor: Vector3): (PathVertex) -> Unit {
    val sx = scaleFactor.x
    val sy = scaleFactor.y
    val sz = scaleFactor.z
    return { vertex ->
        val factor = Vector3(sx, sy, sz)
        vertex.mPosition = mul(vertex.mPosition, factor)
        vertex.mTangent = normalize(mul(factor, vertex.mTangent))
        vertex.mNormal = normalize(mul(factor, vertex.mNormal))
    }
}
