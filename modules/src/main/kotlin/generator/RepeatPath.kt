package generator

import algebra.Vector3

class RepeatPath(
    private val _path: GeneratorPath,
    val mInstances: Int,
    mDelta: Vector3
) : GeneratorPath {

    private val _dx: Float = mDelta.x
    private val _dy: Float = mDelta.y
    private val _dz: Float = mDelta.z

    override fun vertices(): Sequence<PathVertex> = sequence {
        val vertexCount = _path.vertices().count()
        if (vertexCount > 0) {
            for (i in 0 until mInstances) {
                val offset = Vector3(_dx * i, _dy * i, _dz * i)
                for (vertex in _path.vertices()) {
                    vertex.mPosition = add(vertex.mPosition, offset)
                    yield(vertex)
                }
            }
        }
    }

    override fun edges(): Sequence<Edge> = sequence {
        val vertexCount = _path.vertices().count()
        if (vertexCount > 0) {
            for (i in 0 until mInstances) {
                val delta = i * vertexCount
                for (edge in _path.edges()) {
                    yield(Edge(
                        edge.mVertices[0] + delta,
                        edge.mVertices[1] + delta
                    ))
                }
            }
        }
    }
}
