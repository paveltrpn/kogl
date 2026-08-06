package mesh

import java.io.File
import java.io.FileNotFoundException

private enum class ObjFormatToken {
    GEOMETRIC_VERTICES,
    TEXTURE_VERTICES,
    VERTEX_NORMALS,
    PARAMETER_SPACE_VERTICES,
    CURVE_OR_SURFACE,
    DEGREE,
    BASIS_MATRIX,
    STEP_SIZE,
    POINT,
    LINE,
    FACE,
    CURVE,
    CURVE_2D,
    SURFACE,
    PARAMETER_VALUES,
    OUTER_TRIMMING_LOOP,
    INNER_TRIMMING_LOOP,
    SPECIAL_CURVE,
    SPECIAL_POINT,
    END_STATEMENT,
    CONNECT,
    GROUP_NAME,
    SMOOTHING_GROUP,
    MERGING_GROUP,
    OBJECT_NAME,
    BEVEL_INTERPOLATION,
    COLOR_INTERPOLATION,
    DISSOLVE_INTERPOLATION,
    LEVEL_OF_DETAIL,
    MATERIAL_NAME,
    MATERIAL_LIBRARY,
    SHADOW_CASTING,
    RAY_TRACING,
    CURVE_APPROXIMATION_TECHNIQUE,
    SURFACE_APPROXIMATION_TECHNIQUE,
    COMMENT,
}

private val tokensMap: Map<ObjFormatToken, String> = mapOf(
    ObjFormatToken.GEOMETRIC_VERTICES to "v ",
    ObjFormatToken.TEXTURE_VERTICES to "vt ",
    ObjFormatToken.VERTEX_NORMALS to "vn ",
    ObjFormatToken.PARAMETER_SPACE_VERTICES to "vp ",
    ObjFormatToken.CURVE_OR_SURFACE to "cstype ",
    ObjFormatToken.DEGREE to "deg ",
    ObjFormatToken.BASIS_MATRIX to "bmat ",
    ObjFormatToken.STEP_SIZE to "step ",
    ObjFormatToken.POINT to "p ",
    ObjFormatToken.LINE to "l ",
    ObjFormatToken.FACE to "f ",
    ObjFormatToken.CURVE to "curv ",
    ObjFormatToken.CURVE_2D to "curv2 ",
    ObjFormatToken.SURFACE to "surf ",
    ObjFormatToken.PARAMETER_VALUES to "parm ",
    ObjFormatToken.OUTER_TRIMMING_LOOP to "trim ",
    ObjFormatToken.INNER_TRIMMING_LOOP to "hole ",
    ObjFormatToken.SPECIAL_CURVE to "scrv ",
    ObjFormatToken.SPECIAL_POINT to "sp ",
    ObjFormatToken.END_STATEMENT to "end ",
    ObjFormatToken.CONNECT to "con ",
    ObjFormatToken.GROUP_NAME to "g ",
    ObjFormatToken.SMOOTHING_GROUP to "s ",
    ObjFormatToken.MERGING_GROUP to "mg ",
    ObjFormatToken.OBJECT_NAME to "o ",
    ObjFormatToken.BEVEL_INTERPOLATION to "bevel ",
    ObjFormatToken.COLOR_INTERPOLATION to "c_interp ",
    ObjFormatToken.DISSOLVE_INTERPOLATION to "d_interp ",
    ObjFormatToken.LEVEL_OF_DETAIL to "lod ",
    ObjFormatToken.MATERIAL_NAME to "usemtl ",
    ObjFormatToken.MATERIAL_LIBRARY to "mtllib ",
    ObjFormatToken.SHADOW_CASTING to "shadow_obj ",
    ObjFormatToken.RAY_TRACING to "trace_obj ",
    ObjFormatToken.CURVE_APPROXIMATION_TECHNIQUE to "ctech ",
    ObjFormatToken.SURFACE_APPROXIMATION_TECHNIQUE to "stech ",
    ObjFormatToken.COMMENT to "# "
)

private fun split(str: String, delim: Char): List<String> {
    val result = mutableListOf<String>()
    var left = 0
    for (i in str.indices) {
        if (str[i] == delim) {
            result.add(str.substring(left, i))
            left = i + 1
        }
    }
    if (left < str.length) {
        result.add(str.substring(left))
    }
    return result
}

private fun parseVertexString(str: String): Triple<Float, Float, Float> {
    val vertexValuesString = split(str, ' ')
    val x = vertexValuesString[0].toFloat()
    val y = vertexValuesString[1].toFloat()
    val z = vertexValuesString[2].toFloat()
    return Triple(x, y, z)
}

private fun parseNormalString(str: String): Triple<Float, Float, Float> {
    val normalValuesString = split(str, ' ')
    val x = normalValuesString[0].toFloat()
    val y = normalValuesString[1].toFloat()
    val z = normalValuesString[2].toFloat()
    return Triple(x, y, z)
}

private fun parseTexCoordString(str: String): Pair<Float, Float> {
    val texCoordValuesString = split(str, ' ')
    val u = texCoordValuesString[0].toFloat()
    val v = texCoordValuesString[1].toFloat()
    return Pair(u, v)
}

private fun parseTriangleString(str: String): ObjTriangleIndices {
    val indicesString = split(str, ' ')

    val triangle = ObjTriangleIndices()

    var i = 0
    for (indexString in indicesString) {
        val indices = split(indexString, '/')
        val v = indices[0].toInt()
        val t = indices[1].toInt()
        val n = indices[2].toInt()

        // In OBJ format indices starts with 1.
        triangle.vertexIndex[i] = v - 1
        triangle.normalIndex[i] = n - 1
        triangle.texCoordIndex[i] = t - 1

        i++
    }

    return triangle
}

fun readWavefrontObjFile(filePath: String): OBJMesh {
    val file = File(filePath)

    if (!file.exists()) {
        throw FileNotFoundException("File \"$filePath\" not exist!")
    }

    val fileHandle = file.readText()

    var name: String = ""
    val vertices: MutableList<Float> = mutableListOf()
    val vnormals: MutableList<Float> = mutableListOf()
    val txcoords: MutableList<Float> = mutableListOf()
    val triangles: MutableList<ObjTriangleIndices> = mutableListOf()

    for (line in fileHandle.lineSequence()) {
        val str = line.trim()
        if (str.isEmpty()) continue

        when {
            str.startsWith(tokensMap[ObjFormatToken.COMMENT]!!) -> {
                continue
            }

            str.startsWith(tokensMap[ObjFormatToken.OBJECT_NAME]!!) -> {
                name = str.substring(tokensMap[ObjFormatToken.OBJECT_NAME]!!.length).trim()
                continue
            }

            str.startsWith(tokensMap[ObjFormatToken.GEOMETRIC_VERTICES]!!) -> {
                val vertexString = str.substring(tokensMap[ObjFormatToken.GEOMETRIC_VERTICES]!!.length)
                val (x, y, z) = parseVertexString(vertexString)
                vertices.addLast(x)
                vertices.addLast(y)
                vertices.addLast(z)
                continue
            }

            str.startsWith(tokensMap[ObjFormatToken.VERTEX_NORMALS]!!) -> {
                val normalString = str.substring(tokensMap[ObjFormatToken.VERTEX_NORMALS]!!.length)
                val (nx, ny, nz) = parseNormalString(normalString)
                vnormals.addLast(nx)
                vnormals.addLast(ny)
                vnormals.addLast(nz)
                continue
            }

            str.startsWith(tokensMap[ObjFormatToken.TEXTURE_VERTICES]!!) -> {
                val texcrdString = str.substring(tokensMap[ObjFormatToken.TEXTURE_VERTICES]!!.length)
                val (u, v) = parseTexCoordString(texcrdString)
                txcoords.addLast(u)
                txcoords.addLast(v)
                continue
            }

            str.startsWith(tokensMap[ObjFormatToken.FACE]!!) -> {
                val triangleString = str.substring(tokensMap[ObjFormatToken.FACE]!!.length)
                val triangle = parseTriangleString(triangleString)
                triangles.addLast(triangle)
                continue
            }
        }
    }

    return OBJMesh(name, vertices.toFloatArray(), vnormals.toFloatArray(), txcoords.toFloatArray(), triangles)
}
