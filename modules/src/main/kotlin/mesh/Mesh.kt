package mesh

import algebra.Vector2
import algebra.Vector3

typealias MeshValueType = Float

// Dual-Indexing (Per-Face Normals/UVs). Each value is an index into
// corresponding face data array\.
// Compatible with .obj, FBX, glTF
class ObjTriangleIndices {
    var vertexIndex = IntArray(3) { 0 }
    var normalIndex = IntArray(3) { 0 }
    var texCoordIndex = IntArray(3) { 0 }
}

interface Mesh {
}

class OBJMesh(
    name: String,
    vertices: FloatArray,
    vnormals: FloatArray,
    txcoords: FloatArray,
    triangles: List<ObjTriangleIndices>
) : Mesh {
    private var _name: String = name
    private var _vertices = vertices
    private var _vnormals = vnormals
    private var _txcoords = txcoords
    private var _triangles = triangles

    var name: String
        get() {
            return _name
        }
        set(value) {
            _name = value
        }

    var vertices: FloatArray
        get() {
            return _vertices
        }
        set(value) {
            _vertices = value
        }

    var normals: FloatArray
        get() {
            return _vnormals
        }
        set(value) {
            _vnormals = value
        }

    var txcoords: FloatArray
        get() {
            return _txcoords
        }
        set(value) {
            _txcoords = value
        }

    var triangles: List<ObjTriangleIndices>
        get() {
            return _triangles
        }
        set(value) {
            _triangles = value
        }


    val verticesCount: Int
        get(): Int {
            return _vertices.size.div(3) ?: 0
        }

    val vnormalsCount: Int
        get(): Int {
            return _vnormals.size.div(3) ?: 0
        }

    val txcoordsCount: Int
        get(): Int {
            return _txcoords.size.div(2) ?: 0
        }

    val trianglesCount: Int
        get(): Int {
            return _triangles.size.div(9) ?: 0
        }

    // Return i-th triangle vertices as Vector3
    operator fun get(index: Int): Vector3? {
        val i = index * 3

        if (i + 2 >= _vertices.size) {
            return null
        }

        return Vector3(_vertices[i], _vertices[i + 1], _vertices[i + 2])
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

