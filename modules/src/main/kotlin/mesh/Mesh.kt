package mesh

// Dual-Indexing (Per-Face Normals/UVs).
// Compatible with .obj, FBX, glTF.
class ObjTriangleIndices {
    var vertexIndex = IntArray(3) { it -> 0 }
    var normalIndex = IntArray(3) { it -> 0 }
    var texcrdIndex = IntArray(3) { it -> 0 }
};

interface Mesh {

}

class ObjMesh : Mesh {

}

class InterleavedMesh : Mesh {

}

class SeparatedBuffersMesh : Mesh {

}