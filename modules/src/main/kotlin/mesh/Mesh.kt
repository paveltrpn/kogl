package mesh

import algebra.Vector3

typealias MeshValueType = Float

// Dual-Indexing (Per-Face Normals/UVs). Each value is an index into
// corresponding face data array. Compatible with .obj, FBX, glTF
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
    private val _name: String = name
    private val _vertices = vertices
    private val _vnormals = vnormals
    private val _txcoords = txcoords
    private val _triangles = triangles

    val name: String
        get() {
            return _name
        }


    val vertices: FloatArray
        get() {
            return _vertices
        }


    val vnormals: FloatArray
        get() {
            return _vnormals
        }


    val txcoords: FloatArray
        get() {
            return _txcoords
        }


    val triangles: List<ObjTriangleIndices>
        get() {
            return _triangles
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

class SeparatedBuffersMesh : Mesh {
    private val _vertices: FloatArray
    private val _vnormals: FloatArray
    private val _txcoords: FloatArray
    private val _name: String

    constructor(other: OBJMesh) {
        _name = other.name

        val vertices: MutableList<Float> = mutableListOf()
        val vnormals: MutableList<Float> = mutableListOf()
        val txcoords: MutableList<Float> = mutableListOf()

        for (indices in other.triangles) {
            val (a_vId, b_vId, c_vId) = indices.vertexIndex

            val a_vx = other.vertices[(a_vId * 3) + 0]
            val a_vy = other.vertices[(a_vId * 3) + 1]
            val a_vz = other.vertices[(a_vId * 3) + 2]

            val b_vx = other.vertices[(b_vId * 3) + 0]
            val b_vy = other.vertices[(b_vId * 3) + 1]
            val b_vz = other.vertices[(b_vId * 3) + 2]

            val c_vx = other.vertices[(c_vId * 3) + 0]
            val c_vy = other.vertices[(c_vId * 3) + 1]
            val c_vz = other.vertices[(c_vId * 3) + 2]

            vertices.addAll(listOf(a_vx, a_vy, a_vz))
            vertices.addAll(listOf(b_vx, b_vy, b_vz))
            vertices.addAll(listOf(c_vx, c_vy, c_vz))

            val (a_nId, b_nId, c_nId) = indices.normalIndex

            val a_nx = other.vnormals[(a_nId * 3) + 0]
            val a_ny = other.vnormals[(a_nId * 3) + 1]
            val a_nz = other.vnormals[(a_nId * 3) + 2]

            val b_nx = other.vnormals[(b_nId * 3) + 0]
            val b_ny = other.vnormals[(b_nId * 3) + 1]
            val b_nz = other.vnormals[(b_nId * 3) + 2]

            val c_nx = other.vnormals[(c_nId * 3) + 0]
            val c_ny = other.vnormals[(c_nId * 3) + 1]
            val c_nz = other.vnormals[(c_nId * 3) + 2]

            vnormals.addAll(listOf(a_nx, a_ny, a_nz))
            vnormals.addAll(listOf(b_nx, b_ny, b_nz))
            vnormals.addAll(listOf(c_nx, c_ny, c_nz))

            val (a_tId, b_tId, c_tId) = indices.texCoordIndex

            val a_tu = other.txcoords[(a_nId * 2) + 0]
            val a_tv = other.txcoords[(a_nId * 2) + 1]

            val b_tu = other.txcoords[(b_nId * 2) + 0]
            val b_tv = other.txcoords[(b_nId * 2) + 1]

            val c_tu = other.txcoords[(c_nId * 2) + 0]
            val c_tv = other.txcoords[(c_nId * 2) + 1]

            txcoords.addAll(listOf(a_tu, a_tv))
            txcoords.addAll(listOf(b_tu, b_tv))
            txcoords.addAll(listOf(c_tu, c_tv))
        }

        _vertices = vertices.toFloatArray()
        _vnormals = vnormals.toFloatArray()
        _txcoords = txcoords.toFloatArray()
    }

    val name: String
        get() {
            return _name
        }

    val vertices: FloatArray
        get() {
            return _vertices
        }

    val vnormals: FloatArray
        get() {
            return _vnormals
        }

    val txcoords: FloatArray
        get() {
            return _txcoords
        }
}

// TODO
class IndexedMesh : Mesh {
    private val _name: String
    private val _vertices: FloatArray
    private val _vnormals: FloatArray
    private val _txcoords: FloatArray
    private val _indices: IntArray

    constructor(other: OBJMesh) {
        _name = other.name

        _vertices = floatArrayOf()
        _vnormals = floatArrayOf()
        _txcoords = floatArrayOf()
        _indices = intArrayOf()
    }

    constructor(
        name: String,
        vertices: FloatArray,
        vnormals: FloatArray,
        txcoords: FloatArray,
        indices: IntArray
    ) {
        _name = name

        _vertices = vertices
        _vnormals = vnormals
        _txcoords = txcoords
        _indices = indices
    }

    val name: String
        get() {
            return _name
        }

    val vertices: FloatArray
        get() {
            return _vertices
        }

    val vnormals: FloatArray
        get() {
            return _vnormals
        }

    val txcoords: FloatArray
        get() {
            return _txcoords
        }

    val indices: IntArray
        get() {
            return _indices
        }
}

class InterleavedMesh : Mesh {
    private val _name: String

    // Stores triples - [vx,vy,vz, vnx, vny, vnz, u, v, ...]
    private val _mesh: FloatArray

    constructor(other: OBJMesh) {
        _name = other.name

        val mesh: MutableList<Float> = mutableListOf()

        for (indices in other.triangles) {
            val (a_vId, b_vId, c_vId) = indices.vertexIndex

            val a_vx = other.vertices[(a_vId * 3) + 0]
            val a_vy = other.vertices[(a_vId * 3) + 1]
            val a_vz = other.vertices[(a_vId * 3) + 2]

            val b_vx = other.vertices[(b_vId * 3) + 0]
            val b_vy = other.vertices[(b_vId * 3) + 1]
            val b_vz = other.vertices[(b_vId * 3) + 2]

            val c_vx = other.vertices[(c_vId * 3) + 0]
            val c_vy = other.vertices[(c_vId * 3) + 1]
            val c_vz = other.vertices[(c_vId * 3) + 2]

            val (a_nId, b_nId, c_nId) = indices.normalIndex

            val a_nx = other.vnormals[(a_nId * 3) + 0]
            val a_ny = other.vnormals[(a_nId * 3) + 1]
            val a_nz = other.vnormals[(a_nId * 3) + 2]

            val b_nx = other.vnormals[(b_nId * 3) + 0]
            val b_ny = other.vnormals[(b_nId * 3) + 1]
            val b_nz = other.vnormals[(b_nId * 3) + 2]

            val c_nx = other.vnormals[(c_nId * 3) + 0]
            val c_ny = other.vnormals[(c_nId * 3) + 1]
            val c_nz = other.vnormals[(c_nId * 3) + 2]

            val (a_tId, b_tId, c_tId) = indices.texCoordIndex

            val a_tu = other.txcoords[(a_nId * 2) + 0]
            val a_tv = other.txcoords[(a_nId * 2) + 1]

            val b_tu = other.txcoords[(b_nId * 2) + 0]
            val b_tv = other.txcoords[(b_nId * 2) + 1]

            val c_tu = other.txcoords[(c_nId * 2) + 0]
            val c_tv = other.txcoords[(c_nId * 2) + 1]

            mesh.addAll(listOf(a_vx, a_vy, a_vz))
            mesh.addAll(listOf(a_nx, a_ny, a_nz))
            mesh.addAll(listOf(a_tu, a_tv))

            mesh.addAll(listOf(b_vx, b_vy, b_vz))
            mesh.addAll(listOf(b_nx, b_ny, b_nz))
            mesh.addAll(listOf(b_tu, b_tv))

            mesh.addAll(listOf(c_vx, c_vy, c_vz))
            mesh.addAll(listOf(c_nx, c_ny, c_nz))
            mesh.addAll(listOf(c_tu, c_tv))
        }

        _mesh = mesh.toFloatArray()
    }

    val name: String
        get() {
            return _name
        }

    val mesh: FloatArray
        get() {
            return _mesh
        }
}



