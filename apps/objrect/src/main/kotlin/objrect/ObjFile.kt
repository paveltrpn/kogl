package objrect

import java.io.File
import java.io.FileNotFoundException
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken

class Vec2 {
    private var data = FloatArray(2)

    init {
        data[0] = 0.0f
        data[1] = 0.0f
    }

    var x: Float
        get(): Float {
            return data[0]
        }
        set(value) {
            data[0] = value
        }

    var y: Float
        get(): Float {
            return data[1]
        }
        set(value) {
            data[1] = value
        }

    // 2-element string list
    constructor(str: List<String>) {
        for ((i, numStr) in str.withIndex()) {
            val num = numStr.toFloat()
            data[i] = num
        }
    }

    override fun toString(): String {
        val iMax: Int = data.size - 1
        val b = StringBuilder()
        b.append('{')
        var i = 0
        while (true) {
            b.append(data[i])
            if (i == iMax) return b.append('}').toString()
            b.append(", ")
            i++
        }
    }
}

class Vec3 {
    private var data = FloatArray(3)

    init {
        data[0] = 0.0f
        data[1] = 0.0f
        data[2] = 0.0f
    }

    constructor(x: Float, y: Float, z: Float) {
        data[0] = x
        data[1] = y
        data[2] = z
    }

    var x: Float
        get(): Float {
            return data[0]
        }
        set(value) {
            data[0] = value
        }

    var y: Float
        get(): Float {
            return data[1]
        }
        set(value) {
            data[1] = value
        }

    var z: Float
        get(): Float {
            return data[2]
        }
        set(value) {
            data[2] = value
        }

    // 3-element string list
    constructor(str: List<String>) {
        for ((i, numStr) in str.withIndex()) {
            val num = numStr.toFloat()
            data[i] = num
        }
    }

    override fun toString(): String {
        val iMax: Int = data.size - 1
        val b = StringBuilder()
        b.append('{')
        var i = 0
        while (true) {
            b.append(data[i])
            if (i == iMax) return b.append('}').toString()
            b.append(", ")
            i++
        }
    }
}

class VertexPropIndexes() {
    private var data = IntArray(3)

    init {
        data[0] = 0
        data[1] = 0
        data[2] = 0
    }

    operator fun set(id: Int, value: Int) {
        data[id] = value
    }

    operator fun get(id: Int): Int {
        return data[id]
    }

    // 3-element string list
    constructor(str: List<String>) : this() {
        for ((i, numStr) in str.withIndex()) {
            val num = numStr.toInt()
            data[i] = num
        }
    }

    override fun toString(): String {
        val iMax: Int = data.size - 1
        val b = StringBuilder()
        b.append('{')
        var i = 0
        while (true) {
            b.append(data[i])
            if (i == iMax) return b.append('}').toString()
            b.append(", ")
            i++
        }
    }
}

data class IndexedVertecies(
    val name: String, val face_count: Int, val vertecies: List<Vec3>, val normals: List<Vec3>,
    val texturec: List<Vec2>, val verteciesid: List<VertexPropIndexes>, val normalsid: List<VertexPropIndexes>,
    val texturecid: List<VertexPropIndexes>
)

data class PlainVertecies(
    val name: String, val face_count: Int, val vertecies: List<Vec3>, val normals: List<Vec3>,
    val texturec: List<Vec2>
)

class ObjFile(filePath: String) {
    private val lines = mutableListOf<String>()

    private var objectName = ""

    // Geometry data
    private var vertecies = mutableListOf<Vec3>()
    private var norlmals = mutableListOf<Vec3>()
    private var texCoords = mutableListOf<Vec2>()

    // Indecies data
    private var vertIds = mutableListOf<VertexPropIndexes>()
    private var texcIds = mutableListOf<VertexPropIndexes>()
    private var nrmlIds = mutableListOf<VertexPropIndexes>()

    private var trianglesCount = 0

    val gson = GsonBuilder().setPrettyPrinting().create()

    init {
        if (filePath.isEmpty()) {
            throw IllegalArgumentException()
        }

        val file = File(filePath)
        if (!file.exists()) {
            throw FileNotFoundException()
        }

        file.inputStream().bufferedReader().forEachLine { line: String -> lines.add(line) }

        for (line in lines) {
            when {
                line.startsWith("o ") -> {
                    if (!objectName.isEmpty()) {
                        throw Exception("error while reading object file - object name already occured, file must contain only one object!")
                    }

                    objectName = line.removePrefix("o ")
                }

                // Read vertex string
                line.startsWith("v ") -> {
                    val vertexString = line.removePrefix("v ").split(" ")
                    val v = Vec3(vertexString)
                    vertecies.add(v)
                }

                // Read vertex normal string
                line.startsWith("vn ") -> {
                    val normalString = line.removePrefix("vn ").split(" ")
                    val n = Vec3(normalString)
                    norlmals.add(n)
                }

                // Read vertex texture coordinate string
                line.startsWith("vt ") -> {
                    val texCoordString = line.removePrefix("vt ").split(" ")
                    val tc = Vec2(texCoordString)
                    texCoords.add(tc)
                }

                // Read face id`s string string which have a format:
                // "f v1{vId/tId/nId} v2{vId/tId/nId} v3{vId/tId/nId}"
                line.startsWith("f ") -> {
                    // Return string list with length of 3:
                    // ["vert/texc/nrml", "vert/texc/nrml", "vert/texc/nrml"]
                    val faceString = line.removePrefix("f ").split(" ")

                    val veIds = VertexPropIndexes()
                    val tcIds = VertexPropIndexes()
                    val nmIds = VertexPropIndexes()

                    for ((i, face) in faceString.withIndex()) {
                        // Split each element of string list into
                        // ["vert", "texc", "nrml"]
                        val vertPropsStr = face.split("/")

                        // Wavefront obj counts vertecies from "1" instead of "0".
                        veIds[i] = vertPropsStr[0].toInt() - 1
                        tcIds[i] = vertPropsStr[1].toInt() - 1
                        nmIds[i] = vertPropsStr[2].toInt() - 1
                    }

                    // Store each index (vertex, texture coord and normal in
                    // seperate array
                    vertIds.add(veIds)
                    texcIds.add(tcIds)
                    nrmlIds.add(nmIds)

                    trianglesCount++
                }
            }
        }
    }

    fun outPlainVertecies(): String {
        val va = mutableListOf<Vec3>()
        val na = mutableListOf<Vec3>()
        val ta = mutableListOf<Vec2>()

        for (i in 0..<trianglesCount) {
            va.add(vertecies[vertIds[i][0]])
            va.add(vertecies[vertIds[i][1]])
            va.add(vertecies[vertIds[i][2]])

            na.add(norlmals[nrmlIds[i][0]])
            na.add(norlmals[nrmlIds[i][1]])
            na.add(norlmals[nrmlIds[i][2]])

            ta.add(texCoords[texcIds[i][0]])
            ta.add(texCoords[texcIds[i][1]])
            ta.add(texCoords[texcIds[i][2]])
        }

        val out = PlainVertecies(objectName, trianglesCount, va, na, ta)
        return gson.toJson(out)
    }

    fun outIndexed(): String {
        val out =
            IndexedVertecies(objectName, trianglesCount, vertecies, norlmals, texCoords, vertIds, nrmlIds, texcIds)
        return gson.toJson(out)
    }

    fun outCsvPlainVertecies(): String {
        val va = mutableListOf<Vec3>()
        val na = mutableListOf<Vec3>()
        val ta = mutableListOf<Vec2>()

        val vertString = StringBuilder()

        vertString.append("triangles_count\n$trianglesCount\n")
        vertString.append("vertices\n")
        for (i in 0..<trianglesCount) {
            va.add(vertecies[vertIds[i][0]])
            va.add(vertecies[vertIds[i][1]])
            va.add(vertecies[vertIds[i][2]])

            vertString.append(
                "${vertecies[vertIds[i][0]].x}, ${vertecies[vertIds[i][0]].y}, ${vertecies[vertIds[i][0]].z}\n" +
                        "${vertecies[vertIds[i][1]].x}, ${vertecies[vertIds[i][1]].y}, ${vertecies[vertIds[i][1]].z}\n" +
                        "${vertecies[vertIds[i][2]].x}, ${vertecies[vertIds[i][2]].y}, ${vertecies[vertIds[i][2]].z}\n"
            )
        }

        vertString.append("normals\n")
        for (i in 0..<trianglesCount) {
            na.add(norlmals[nrmlIds[i][0]])
            na.add(norlmals[nrmlIds[i][1]])
            na.add(norlmals[nrmlIds[i][2]])

            vertString.append(
                "${norlmals[nrmlIds[i][0]].x}, ${norlmals[nrmlIds[i][0]].y}, ${norlmals[nrmlIds[i][0]].z}\n" +
                        "${norlmals[nrmlIds[i][1]].x}, ${norlmals[nrmlIds[i][1]].y}, ${norlmals[nrmlIds[i][1]].z}\n" +
                        "${norlmals[nrmlIds[i][2]].x}, ${norlmals[nrmlIds[i][2]].y}, ${norlmals[nrmlIds[i][2]].z}\n"
            )
        }

        vertString.append("texcrds\n")
        for (i in 0..<trianglesCount) {
            ta.add(texCoords[texcIds[i][0]])
            ta.add(texCoords[texcIds[i][1]])

            vertString.append(
                "${texCoords[texcIds[i][0]].x}, ${texCoords[texcIds[i][0]].y}\n" +
                        "${texCoords[texcIds[i][1]].x}, ${texCoords[texcIds[i][1]].y}\n" +
                        "${texCoords[texcIds[i][2]].x}, ${texCoords[texcIds[i][2]].y}\n"
            )
        }

        vertString.append("bounding_volume_min\n")
        vertString.append("\n")
        vertString.append("bounding_volume_max\n")

        return vertString.toString()
    }

    fun outCsvIndexed(): String {
        return ""
    }
}