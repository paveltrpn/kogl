package generator

import algebra.*

// ============================================================================
// ======================= AxisSwapMesh =======================================
// ============================================================================

class AxisSwapMesh(
    private val _mesh: GeneratorMesh,
    mX: Axis,
    mY: Axis,
    mZ: Axis
) : GeneratorMesh {

    private val _x: Axis = mX
    private val _y: Axis = mY
    private val _z: Axis = mZ
    private val _flip: Boolean

    init {
        var flip = true
        if (mX != Axis.X) {
            flip = !flip
        }
        if (mY != Axis.Y) {
            flip = !flip
        }
        if (mZ != Axis.Z) {
            flip = !flip
        }
        _flip = flip
    }

    override fun vertices(): Sequence<MeshVertex> = _mesh.vertices().map { vertex: MeshVertex ->
        vertex.mPosition = Vector3(
            componentAt(vertex.mPosition, _x),
            componentAt(vertex.mPosition, _y),
            componentAt(vertex.mPosition, _z)
        )
        vertex.mNormal = Vector3(
            componentAt(vertex.mNormal, _x),
            componentAt(vertex.mNormal, _y),
            componentAt(vertex.mNormal, _z)
        )
        vertex
    }

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
