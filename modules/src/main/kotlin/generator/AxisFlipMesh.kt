package generator

import algebra.*

// ============================================================================
// ======================= AxisFlipMesh =======================================
// ============================================================================

class AxisFlipMesh(
    mMesh: GeneratorMesh,
    mX: Boolean,
    mY: Boolean,
    mZ: Boolean
) : GeneratorMesh {

    private val _mesh: TransformMesh = TransformMesh(mMesh, _axisFlipMutator(mX, mY, mZ))
    private val _flip: Boolean

    init {
        var flip = false
        if (mX) {
            flip = !flip
        }
        if (mY) {
            flip = !flip
        }
        if (mZ) {
            flip = !flip
        }
        _flip = flip
    }

    override fun vertices(): Sequence<MeshVertex> = _mesh.vertices()

    override fun triangles(): Sequence<Triangle> = _mesh.triangles().map { triangle: Triangle ->
        if (_flip) {
            Triangle(
                triangle.mVertices[2],
                triangle.mVertices[1],
                triangle.mVertices[0]
            )
        } else {
            triangle
        }
    }
}

private fun _axisFlipMutator(x: Boolean, y: Boolean, z: Boolean): (MeshVertex) -> Unit {
    return { vertex: MeshVertex ->
        if (x) {
            vertex.mPosition = Vector3(
                -vertex.mPosition.x,
                vertex.mPosition.y,
                vertex.mPosition.z
            )
            vertex.mNormal = Vector3(
                -vertex.mNormal.x,
                vertex.mNormal.y,
                vertex.mNormal.z
            )
        }
        if (y) {
            vertex.mPosition = Vector3(
                vertex.mPosition.x,
                -vertex.mPosition.y,
                vertex.mPosition.z
            )
            vertex.mNormal = Vector3(
                vertex.mNormal.x,
                -vertex.mNormal.y,
                vertex.mNormal.z
            )
        }
        if (z) {
            vertex.mPosition = Vector3(
                vertex.mPosition.x,
                vertex.mPosition.y,
                -vertex.mPosition.z
            )
            vertex.mNormal = Vector3(
                vertex.mNormal.x,
                vertex.mNormal.y,
                -vertex.mNormal.z
            )
        }
    }
}
