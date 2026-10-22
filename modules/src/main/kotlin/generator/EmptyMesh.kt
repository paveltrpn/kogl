package generator

class EmptyMesh : GeneratorMesh {
    override fun vertices(): Sequence<MeshVertex> = emptySequence()
    override fun triangles(): Sequence<Triangle> = emptySequence()
}
