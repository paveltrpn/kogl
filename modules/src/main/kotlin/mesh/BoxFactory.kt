package mesh

fun boxFactory(): IndexedMesh {
    val vertices = floatArrayOf(
        // 8 cube vertices
        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,
        -0.5f, 0.5f, -0.5f,
        -0.5f, -0.5f, 0.5f,
        0.5f, -0.5f, 0.5f,
        0.5f, 0.5f, 0.5f,
        -0.5f, 0.5f, 0.5f
    )

    val indices = intArrayOf(
        // Front face (z = 0.5) - vertices 4,5,6,7
        4, 6, 5, 4, 7, 6,
        // Back face (z = -0.5) - vertices 0,1,2,3
        0, 2, 1, 0, 3, 2,
        // Top face (y = 0.5) - vertices 3,2,6,7
        3, 6, 7, 3, 2, 6,
        // Bottom face (y = -0.5) - vertices 0,1,5,4
        0, 5, 4, 0, 1, 5,
        // Right face (x = 0.5) - vertices 1,2,6,5
        1, 6, 5, 1, 2, 6,
        // Left face (x = -0.5) - vertices 0,3,7,4
        0, 7, 4, 0, 3, 7
    )

    val vnormals = floatArrayOf(
        // Normal for each vertex according to indices
        // Front face triangles (4,6,5, 4,7,6) - normal (0, 0, 1)
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        // Back face triangles (0,2,1, 0,3,2) - normal (0, 0, -1)
        0.0f, 0.0f, -1.0f,
        0.0f, 0.0f, -1.0f,
        0.0f, 0.0f, -1.0f,
        0.0f, 0.0f, -1.0f,
        0.0f, 0.0f, -1.0f,
        0.0f, 0.0f, -1.0f,
        // Top face triangles (3,6,7, 3,2,6) - normal (0, 1, 0)
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        // Bottom face triangles (0,5,4, 0,1,5) - normal (0, -1, 0)
        0.0f, -1.0f, 0.0f,
        0.0f, -1.0f, 0.0f,
        0.0f, -1.0f, 0.0f,
        0.0f, -1.0f, 0.0f,
        0.0f, -1.0f, 0.0f,
        0.0f, -1.0f, 0.0f,
        // Right face triangles (1,6,5, 1,2,6) - normal (1, 0, 0)
        1.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
        // Left face triangles (0,7,4, 0,3,7) - normal (-1, 0, 0)
        -1.0f, 0.0f, 0.0f,
        -1.0f, 0.0f, 0.0f,
        -1.0f, 0.0f, 0.0f,
        -1.0f, 0.0f, 0.0f,
        -1.0f, 0.0f, 0.0f,
        -1.0f, 0.0f, 0.0f
    )

    val texCoords = floatArrayOf(
        // UV for each vertex according to indices
        // Front face (4,6,5, 4,7,6)
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        1.0f, 1.0f,
        // Back face (0,2,1, 0,3,2)
        0.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,
        // Top face (3,6,7, 3,2,6)
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        1.0f, 1.0f,
        // Bottom face (0,5,4, 0,1,5)
        0.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,
        // Right face (1,6,5, 1,2,6)
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        1.0f, 1.0f,
        // Left face (0,7,4, 0,3,7)
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        1.0f, 1.0f
    )

    return IndexedMesh("Box", vertices, vnormals, texCoords, indices)
}