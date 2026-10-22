package generator

import mesh.*

fun GeneratorMesh.toObjMesh(name: String): OBJMesh {
    val vertexList = vertices().toList()

    val positions = ArrayList<Float>()
    val normals = ArrayList<Float>()
    val texcoords = ArrayList<Float>()

    val triangles = triangles().map { triangle: Triangle ->
        val a = vertexList[triangle.mVertices[0]]
        val b = vertexList[triangle.mVertices[1]]
        val c = vertexList[triangle.mVertices[2]]

        val faceNormal = normalize(cross(
            sub(b.mPosition, a.mPosition),
            sub(c.mPosition, a.mPosition)
        ))

        val base = positions.size / 3

        for (vertex in listOf(a, b, c)) {
            positions.add(vertex.mPosition.x)
            positions.add(vertex.mPosition.y)
            positions.add(vertex.mPosition.z)

            normals.add(faceNormal.x)
            normals.add(faceNormal.y)
            normals.add(faceNormal.z)

            texcoords.add(vertex.mTexCoord.x)
            texcoords.add(vertex.mTexCoord.y)
        }

        val indices = ObjTriangleIndices()
        indices.vertexIndex = intArrayOf(base + 0, base + 1, base + 2)
        indices.normalIndex = intArrayOf(base + 0, base + 1, base + 2)
        indices.texCoordIndex = intArrayOf(base + 0, base + 1, base + 2)
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
