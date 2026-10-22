package generator

import algebra.*

// ============================================================================
// ======================= RepeatMesh =========================================
// ============================================================================
class RepeatMesh(
    private val _mesh: GeneratorMesh,
    private val _instances: Int,
    private val _delta: Vector3
) : GeneratorMesh {

    private val _vertexCount: Int = _mesh.vertices().count()

    override fun vertices(): Sequence<MeshVertex> = sequence {
        if (_vertexCount > 0) {
            var dx = 0.0f
            var dy = 0.0f
            var dz = 0.0f
            for (i in 0 until _instances) {
                for (vertex in _mesh.vertices()) {
                    vertex.mPosition = Vector3(
                        vertex.mPosition.x + dx,
                        vertex.mPosition.y + dy,
                        vertex.mPosition.z + dz
                    )
                    yield(vertex)
                }
                dx += _delta.x
                dy += _delta.y
                dz += _delta.z
            }
        }
    }

    override fun triangles(): Sequence<Triangle> = sequence {
        if (_vertexCount > 0) {
            var delta = 0
            for (i in 0 until _instances) {
                for (triangle in _mesh.triangles()) {
                    yield(Triangle(
                        triangle.mVertices[0] + delta,
                        triangle.mVertices[1] + delta,
                        triangle.mVertices[2] + delta
                    ))
                }
                delta += _vertexCount
            }
        }
    }
}
