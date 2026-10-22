package generator

import mesh.OBJMesh
import mesh.ObjTriangleIndices

fun GeneratorMesh.toObjMesh(name: String): OBJMesh {
    val positions = ArrayList<Float>()
    val normals = ArrayList<Float>()
    val texcoords = ArrayList<Float>()

    for (vertex in vertices()) {
        positions.add(vertex.mPosition.x)
        positions.add(vertex.mPosition.y)
        positions.add(vertex.mPosition.z)

        normals.add(vertex.mNormal.x)
        normals.add(vertex.mNormal.y)
        normals.add(vertex.mNormal.z)

        texcoords.add(vertex.mTexCoord.x)
        texcoords.add(vertex.mTexCoord.y)
    }

    val triangles = triangles().map { triangle ->
        val indices = ObjTriangleIndices()
        indices.vertexIndex = triangle.mVertices.copyOf()
        indices.normalIndex = triangle.mVertices.copyOf()
        indices.texCoordIndex = triangle.mVertices.copyOf()
        indices
    }.toList()

    return OBJMesh(
        name,
        positions.toFloatArray(),
        normals.toFloatArray(),
        texcoords.toFloatArray(),
        triangles
    )
}
