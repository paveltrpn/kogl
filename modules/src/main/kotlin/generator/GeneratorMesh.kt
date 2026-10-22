package generator

interface GeneratorMesh {
    fun vertices(): Sequence<MeshVertex>
    fun triangles(): Sequence<Triangle>
}

fun countVertices(mesh: GeneratorMesh): Int {
    return mesh.vertices().count()
}
