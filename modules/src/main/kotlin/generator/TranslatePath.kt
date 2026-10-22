package generator

import algebra.Vector3

class TranslatePath(
    path: GeneratorPath,
    delta: Vector3
) : TransformPath(path, translatePathEval(delta))

private fun translatePathEval(delta: Vector3): (PathVertex) -> Unit {
    val dx = delta.x
    val dy = delta.y
    val dz = delta.z
    return { vertex ->
        vertex.mPosition = Vector3(
            vertex.mPosition.x + dx,
            vertex.mPosition.y + dy,
            vertex.mPosition.z + dz
        )
    }
}
