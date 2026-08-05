package mesh

import algebra.Vector2
import algebra.Vector3
import java.io.File
import java.io.FileNotFoundException

enum class ObjTokens {
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
    SOURFACE,
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

val tokens_: Map<ObjTokens, String> = mapOf(
    ObjTokens.GEOMETRIC_VERTICES to "v ",
    ObjTokens.TEXTURE_VERTICES to "vt ",
    ObjTokens.VERTEX_NORMALS to "vn ",
    ObjTokens.PARAMETER_SPACE_VERTICES to "vp ",
    ObjTokens.CURVE_OR_SURFACE to "cstype ",
    ObjTokens.DEGREE to "deg ",
    ObjTokens.BASIS_MATRIX to "bmat ",
    ObjTokens.STEP_SIZE to "step ",
    ObjTokens.POINT to "p ",
    ObjTokens.LINE to "l ",
    ObjTokens.FACE to "f ",
    ObjTokens.CURVE to "curv ",
    ObjTokens.CURVE_2D to "curv2 ",
    ObjTokens.SOURFACE to "surf ",
    ObjTokens.PARAMETER_VALUES to "parm ",
    ObjTokens.OUTER_TRIMMING_LOOP to "trim ",
    ObjTokens.INNER_TRIMMING_LOOP to "hole ",
    ObjTokens.SPECIAL_CURVE to "scrv ",
    ObjTokens.SPECIAL_POINT to "sp ",
    ObjTokens.END_STATEMENT to "end ",
    ObjTokens.CONNECT to "con ",
    ObjTokens.GROUP_NAME to "g ",
    ObjTokens.SMOOTHING_GROUP to "s ",
    ObjTokens.MERGING_GROUP to "mg ",
    ObjTokens.OBJECT_NAME to "o ",
    ObjTokens.BEVEL_INTERPOLATION to "bevel ",
    ObjTokens.COLOR_INTERPOLATION to "c_interp ",
    ObjTokens.DISSOLVE_INTERPOLATION to "d_interp ",
    ObjTokens.LEVEL_OF_DETAIL to "lod ",
    ObjTokens.MATERIAL_NAME to "usemtl ",
    ObjTokens.MATERIAL_LIBRARY to "mtllib ",
    ObjTokens.SHADOW_CASTING to "shadow_obj ",
    ObjTokens.RAY_TRACING to "trace_obj ",
    ObjTokens.CURVE_APPROXIMATION_TECHNIQUE to "ctech ",
    ObjTokens.SURFACE_APPROXIMATION_TECHNIQUE to "stech ",
    ObjTokens.COMMENT to "# "
)

fun split(str: String, delim: Char): List<String> {
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

fun parseVertexString(str: String): Triple<Float, Float, Float> {
    val vertexValuesString = split(str, ' ')
    val x = vertexValuesString[0].toFloat()
    val y = vertexValuesString[1].toFloat()
    val z = vertexValuesString[2].toFloat()
    return Triple(x, y, z)
}

fun parseNormalString(str: String): Triple<Float, Float, Float> {
    val normalValuesString = split(str, ' ')
    val x = normalValuesString[0].toFloat()
    val y = normalValuesString[1].toFloat()
    val z = normalValuesString[2].toFloat()
    return Triple(x, y, z)
}

fun parseTexCoordString(str: String): Pair<Float, Float> {
    val texCoordValuesString = split(str, ' ')
    val u = texCoordValuesString[0].toFloat()
    val v = texCoordValuesString[1].toFloat()
    return Pair(u, v)
}

fun parseTriangleString(str: String): ObjTriangleIndices {
    val indicesString = split(str, ' ')

    val triangle = ObjTriangleIndices()

    var i = 0
    for (indexString in indicesString) {
        val indicies = split(indexString, '/')
        val v = indicies[0].toInt()
        val t = indicies[1].toInt()
        val n = indicies[2].toInt()

        triangle.vertexIndex[i] = v - 1
        triangle.normalIndex[i] = n - 1
        triangle.texCoordIndex[i] = t - 1

        i++
    }

    return triangle
}

fun readWavefrontObjFile(filePath: String): SeparatedBuffersMesh {
    val file = File(filePath)

    if (!file.exists()) {
        throw FileNotFoundException("File \"$filePath\" not exist!")
    }

    val fileHandle = file.readText()

    val objMesh = ObjMesh()

    for (line in fileHandle.lineSequence()) {
        val str = line.trim()
        if (str.isEmpty()) continue

        when {
            str.startsWith(tokens_[ObjTokens.COMMENT]!!) -> {
                continue
            }

            str.startsWith(tokens_[ObjTokens.OBJECT_NAME]!!) -> {
                objMesh._name = str.substring(tokens_[ObjTokens.OBJECT_NAME]!!.length).trim()
                continue
            }

            str.startsWith(tokens_[ObjTokens.GEOMETRIC_VERTICES]!!) -> {
                val vertexString = str.substring(tokens_[ObjTokens.GEOMETRIC_VERTICES]!!.length)
                val (x, y, z) = parseVertexString(vertexString)
                objMesh._vertices.add(Vector3(x, y, z))
                continue
            }

            str.startsWith(tokens_[ObjTokens.VERTEX_NORMALS]!!) -> {
                val normalString = str.substring(tokens_[ObjTokens.VERTEX_NORMALS]!!.length)
                val (nx, ny, nz) = parseNormalString(normalString)
                objMesh._normals.add(Vector3(nx, ny, nz))
                continue
            }

            str.startsWith(tokens_[ObjTokens.TEXTURE_VERTICES]!!) -> {
                val texcrdString = str.substring(tokens_[ObjTokens.TEXTURE_VERTICES]!!.length)
                val (u, v) = parseTexCoordString(texcrdString)
                objMesh._texcrds.add(Vector2(u, v))
                continue
            }

            str.startsWith(tokens_[ObjTokens.FACE]!!) -> {
                val triangleString = str.substring(tokens_[ObjTokens.FACE]!!.length)
                val triangle = parseTriangleString(triangleString)
                objMesh._triangles.add(triangle)
                continue
            }
        }
    }

    return SeparatedBuffersMesh(objMesh)
}
