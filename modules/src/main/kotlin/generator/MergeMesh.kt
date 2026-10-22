package generator

class MergeMesh(
    vararg val mMeshes: GeneratorMesh
) : GeneratorMesh {

    override fun vertices(): Sequence<MeshVertex> = sequence {
        for (mesh in mMeshes) {
            for (vertex in mesh.vertices()) {
                yield(vertex)
            }
        }
    }

    override fun triangles(): Sequence<Triangle> = sequence {
        var offset = 0
        for (mesh in mMeshes) {
            for (triangle in mesh.triangles()) {
                yield(Triangle(
                    triangle.mVertices[0] + offset,
                    triangle.mVertices[1] + offset,
                    triangle.mVertices[2] + offset
                ))
            }
            offset += mesh.vertices().count()
        }
    }
}
