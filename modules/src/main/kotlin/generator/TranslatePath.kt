package generator

import algebra.*

// ============================================================================
// ======================= TranslatePath ======================================
// ============================================================================
class TranslatePath(
    path: GeneratorPath,
    delta: Vector3
) : TransformPath(path, _translatePathEval(delta))

private fun _translatePathEval(delta: Vector3): (PathVertex) -> Unit {
    val dx = delta.x
    val dy = delta.y
    val dz = delta.z
    return { vertex: PathVertex ->
        vertex.mPosition = Vector3(
            vertex.mPosition.x + dx,
            vertex.mPosition.y + dy,
            vertex.mPosition.z + dz
        )
    }
}
