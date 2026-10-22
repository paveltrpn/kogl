package generator

open class TransformMesh(
    private val _mesh: GeneratorMesh,
    private val _mutate: (MeshVertex) -> Unit
) : GeneratorMesh {

    override fun vertices(): Sequence<MeshVertex> = _mesh.vertices().map { vertex ->
        _mutate(vertex)
        vertex
    }

    override fun triangles(): Sequence<Triangle> = _mesh.triangles()
}
