package generator

import algebra.*

// ============================================================================
// ======================= RotateMesh =========================================
// ============================================================================
class RotateMesh : GeneratorMesh {

    private val _mesh: TransformMesh

    constructor(mMesh: GeneratorMesh, mAngle: Float, mAxis: Vector3) {
        _mesh = TransformMesh(mMesh, _rotateMutator(mAngle, mAxis))
    }

    constructor(mMesh: GeneratorMesh, mAngle: Float, mAxis: Axis) : this(
        mMesh,
        mAngle,
        _axisVector(mAxis)
    )

    override fun vertices(): Sequence<MeshVertex> = _mesh.vertices()

    override fun triangles(): Sequence<Triangle> = _mesh.triangles()
}

private fun _axisVector(axis: Axis): Vector3 {
    if (axis == Axis.X) {
        return Vector3(1.0f, 0.0f, 0.0f)
    }
    if (axis == Axis.Y) {
        return Vector3(0.0f, 1.0f, 0.0f)
    }
    return Vector3(0.0f, 0.0f, 1.0f)
}

private fun _rotateMutator(angle: Float, axis: Vector3): (MeshVertex) -> Unit {
    val ax = Vector3(axis.x, axis.y, axis.z)
    return { vertex: MeshVertex ->
        vertex.mPosition = rotate(vertex.mPosition, ax, angle)
        vertex.mNormal = rotate(vertex.mNormal, ax, angle)
    }
}
