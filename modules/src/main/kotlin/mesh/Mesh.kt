package mesh

import algebra.Vector2
import algebra.Vector3

typealias MeshValueType = Float

class ObjTriangleIndices {
    var vertexIndex = IntArray(3) { 0 }
    var normalIndex = IntArray(3) { 0 }
    var texCoordIndex = IntArray(3) { 0 }
}

interface Mesh {
}

class ObjMesh : Mesh {
    var _vertices: MutableList<Vector3> = mutableListOf()
    var _normals: MutableList<Vector3> = mutableListOf()
    var _texcrds: MutableList<Vector2> = mutableListOf()
    var _vertclr: MutableList<Vector3> = mutableListOf()
    var _triangles: MutableList<ObjTriangleIndices> = mutableListOf()
    var _name: String = ""

    fun verticesData(): List<Vector3> = _vertices
    fun indicesData(): List<ObjTriangleIndices> = _triangles
    fun normalsData(): List<Vector3> = _normals
    fun texcrdsData(): List<Vector2> = _texcrds
    fun vertclrData(): List<Vector3> = _vertclr

    fun vertices(): List<Vector3> = _vertices
    fun indices(): List<ObjTriangleIndices> = _triangles
    fun normals(): List<Vector3> = _normals
    fun texcrds(): List<Vector2> = _texcrds
    fun vertclr(): List<Vector3> = _vertclr

    fun setVertices(vertices: List<Vector3>) {
        _vertices = vertices.toMutableList()
    }

    fun setTriangles(triangles: List<ObjTriangleIndices>) {
        _triangles = triangles.toMutableList()
    }

    fun setNormals(normals: List<Vector3>) {
        _normals = normals.toMutableList()
    }

    fun setTexCoords(texCoords: List<Vector2>) {
        _texcrds = texCoords.toMutableList()
    }

    fun setVertexColors(vertexColors: List<Vector3>) {
        _vertclr = vertexColors.toMutableList()
    }

    fun name(): String = _name

    fun setName(name: String) {
        _name = name
    }
}

//class InterleavedMesh : Mesh {
//    data class Vertex(
//        var pos: Vector3,
//        var norm: Vector3,
//        var uv: Vector2
//    )
//
//    var _vertices: MutableList<Vertex> = mutableListOf()
//    var _name: String = ""
//
//    constructor() {}
//
//    constructor(other: ObjMesh) {
//        _name = other._name
//        for (indecies in other._triangles) {
//            val v1 = other._vertices[indecies.vertexIndex[0]]
//            val v2 = other._vertices[indecies.vertexIndex[1]]
//            val v3 = other._vertices[indecies.vertexIndex[2]]
//
//            val n1 = other._normals[indecies.normalIndex[0]]
//            val n2 = other._normals[indecies.normalIndex[1]]
//            val n3 = other._normals[indecies.normalIndex[2]]
//
//            val tx1 = other._texcrds[indecies.texCoordIndex[0]]
//            val tx2 = other._texcrds[indecies.texCoordIndex[1]]
//            val tx3 = other._texcrds[indecies.texCoordIndex[2]]
//
//            val vertex1 = Vertex(v1, n1, tx1)
//            val vertex2 = Vertex(v2, n2, tx2)
//            val vertex3 = Vertex(v3, n3, tx3)
//
//            _vertices.add(vertex1)
//            _vertices.add(vertex2)
//            _vertices.add(vertex3)
//        }
//    }
//
//    fun name(): String = _name
//
//    fun setName(name: String) {
//        _name = name
//    }
//
//    override fun verticesCount(): Long = _vertices.size.toLong()
//    override fun trianglesCount(): Long = _vertices.size / 3L
//}
//
//class SeparatedBuffersMesh : Mesh {
//    var _vertices: MutableList<Vector3> = mutableListOf()
//    var _normals: MutableList<Vector3> = mutableListOf()
//    var _texcrds: MutableList<Vector2> = mutableListOf()
//    var _name: String = ""
//
//    constructor() {}
//
//    constructor(other: ObjMesh) {
//        _name = other._name
//        for (indecies in other._triangles) {
//            val v1 = other._vertices[indecies.vertexIndex[0]]
//            val v2 = other._vertices[indecies.vertexIndex[1]]
//            val v3 = other._vertices[indecies.vertexIndex[2]]
//
//            val n1 = other._normals[indecies.normalIndex[0]]
//            val n2 = other._normals[indecies.normalIndex[1]]
//            val n3 = other._normals[indecies.normalIndex[2]]
//
//            val tx1 = other._texcrds[indecies.texCoordIndex[0]]
//            val tx2 = other._texcrds[indecies.texCoordIndex[1]]
//            val tx3 = other._texcrds[indecies.texCoordIndex[2]]
//
//            _vertices.add(v1)
//            _vertices.add(v2)
//            _vertices.add(v3)
//
//            _normals.add(n1)
//            _normals.add(n2)
//            _normals.add(n3)
//
//            _texcrds.add(tx1)
//            _texcrds.add(tx2)
//            _texcrds.add(tx3)
//        }
//    }
//
//    fun name(): String = _name
//
//    fun setName(name: String) {
//        _name = name
//    }
//
//    override fun verticesCount(): Long = _vertices.size.toLong()
//    override fun trianglesCount(): Long = _vertices.size / 3L
//}

