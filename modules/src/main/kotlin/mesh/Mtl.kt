package mesh

import java.io.File
import java.io.FileNotFoundException

private enum class MtlFormatToken {
    NEW_MATERIAL,
    AMBIENT_COLOR,
    DIFFUSE_COLOR,
    SPECULAR_COLOR,
    SPECULAR_EXPONENT,
    SPECULAR_HIGHLIGHTS,
    TRANSPARENCY,
    TRANSMISSION_FILTER,
    ILLUMINATION_MODEL,
    SOLID_DENSITY,
    OPTICAL_DENSITY,
    BUMP_MAP,
    DIFFUSE_TEXTURE,
    SPECULAR_TEXTURE,
    ALPHA_TEXTURE,
    BUMP_MAP_OPTIONS,
    REFLECTION_MAP,
    REFLECTION_MAP_OPTIONS,
    DISPLACEMENT_MAP,
    AMBIENT_TEXTURE,
    GLOW_TEXTURE,
    COMMENT,
}

private val tokensMap: Map<MtlFormatToken, String> = mapOf(
    MtlFormatToken.NEW_MATERIAL to "newmtl ",
    MtlFormatToken.AMBIENT_COLOR to "Ka ",
    MtlFormatToken.DIFFUSE_COLOR to "Kd ",
    MtlFormatToken.SPECULAR_COLOR to "Ks ",
    MtlFormatToken.SPECULAR_EXPONENT to "Ns ",
    MtlFormatToken.SPECULAR_HIGHLIGHTS to "Se ",
    MtlFormatToken.TRANSPARENCY to "d ",
    MtlFormatToken.TRANSMISSION_FILTER to "Tr ",
    MtlFormatToken.ILLUMINATION_MODEL to "illum ",
    MtlFormatToken.SOLID_DENSITY to "Pd ",
    MtlFormatToken.OPTICAL_DENSITY to "Ni ",
    MtlFormatToken.BUMP_MAP to "map_bump ",
    MtlFormatToken.DIFFUSE_TEXTURE to "map_Kd ",
    MtlFormatToken.SPECULAR_TEXTURE to "map_Ks ",
    MtlFormatToken.ALPHA_TEXTURE to "map_d ",
    MtlFormatToken.BUMP_MAP_OPTIONS to "bm ",
    MtlFormatToken.REFLECTION_MAP to "map_reflect ",
    MtlFormatToken.REFLECTION_MAP_OPTIONS to "reflect ",
    MtlFormatToken.DISPLACEMENT_MAP to "disp ",
    MtlFormatToken.AMBIENT_TEXTURE to "map_Ka ",
    MtlFormatToken.GLOW_TEXTURE to "glow ",
    MtlFormatToken.COMMENT to "# "
)

data class Material(
    val name: String,
    val ka: Triple<Float, Float, Float> = Triple(0.0f, 0.0f, 0.0f),
    val kd: Triple<Float, Float, Float> = Triple(0.8f, 0.8f, 0.8f),
    val ks: Triple<Float, Float, Float> = Triple(0.0f, 0.0f, 0.0f),
    val ns: Float = 0.0f,
    val d: Float = 1.0f,
    val illum: Int = 1,
    val mapKd: String? = null
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

private fun parseColorString(str: String): Triple<Float, Float, Float> {
    val valuesString = split(str, ' ')
    val r = valuesString[0].toFloat()
    val g = valuesString[1].toFloat()
    val b = valuesString[2].toFloat()
    return Triple(r, g, b)
}

fun readWavefrontMtlFile(filePath: String): Map<String, Material> {
    val file = File(filePath)

    if (!file.exists()) {
        throw FileNotFoundException("File \"$filePath\" not exist!")
    }

    val fileHandle = file.readText()
    val materials = mutableMapOf<String, Material>()

    var currentMaterial: Material? = null
    var currentName: String = ""
    var ka = Triple(0.0f, 0.0f, 0.0f)
    var kd = Triple(0.8f, 0.8f, 0.8f)
    var ks = Triple(0.0f, 0.0f, 0.0f)
    var ns = 0.0f
    var d = 1.0f
    var illum = 1
    var mapKd: String? = null

    fun saveCurrentMaterial() {
        if (currentName.isNotEmpty()) {
            materials[currentName] = Material(currentName, ka, kd, ks, ns, d, illum, mapKd)
        }
    }

    for (line in fileHandle.lineSequence()) {
        val str = line.trim()
        if (str.isEmpty()) continue

        when {
            str.startsWith(tokensMap[MtlFormatToken.COMMENT]!!) -> {
                continue
            }

            str.startsWith(tokensMap[MtlFormatToken.NEW_MATERIAL]!!) -> {
                saveCurrentMaterial()
                currentName = str.substring(tokensMap[MtlFormatToken.NEW_MATERIAL]!!.length).trim()
                ka = Triple(0.0f, 0.0f, 0.0f)
                kd = Triple(0.8f, 0.8f, 0.8f)
                ks = Triple(0.0f, 0.0f, 0.0f)
                ns = 0.0f
                d = 1.0f
                illum = 1
                mapKd = null
                continue
            }

            str.startsWith(tokensMap[MtlFormatToken.AMBIENT_COLOR]!!) -> {
                val colorString = str.substring(tokensMap[MtlFormatToken.AMBIENT_COLOR]!!.length)
                ka = parseColorString(colorString)
                continue
            }

            str.startsWith(tokensMap[MtlFormatToken.DIFFUSE_COLOR]!!) -> {
                val colorString = str.substring(tokensMap[MtlFormatToken.DIFFUSE_COLOR]!!.length)
                kd = parseColorString(colorString)
                continue
            }

            str.startsWith(tokensMap[MtlFormatToken.SPECULAR_COLOR]!!) -> {
                val colorString = str.substring(tokensMap[MtlFormatToken.SPECULAR_COLOR]!!.length)
                ks = parseColorString(colorString)
                continue
            }

            str.startsWith(tokensMap[MtlFormatToken.SPECULAR_EXPONENT]!!) -> {
                val valueString = str.substring(tokensMap[MtlFormatToken.SPECULAR_EXPONENT]!!.length)
                ns = valueString.trim().toFloat()
                continue
            }

            str.startsWith(tokensMap[MtlFormatToken.TRANSPARENCY]!!) -> {
                val valueString = str.substring(tokensMap[MtlFormatToken.TRANSPARENCY]!!.length)
                d = valueString.trim().toFloat()
                continue
            }

            str.startsWith(tokensMap[MtlFormatToken.ILLUMINATION_MODEL]!!) -> {
                val valueString = str.substring(tokensMap[MtlFormatToken.ILLUMINATION_MODEL]!!.length)
                illum = valueString.trim().toInt()
                continue
            }

            str.startsWith(tokensMap[MtlFormatToken.DIFFUSE_TEXTURE]!!) -> {
                val textureString = str.substring(tokensMap[MtlFormatToken.DIFFUSE_TEXTURE]!!.length)
                mapKd = textureString.trim()
                continue
            }
        }
    }

    saveCurrentMaterial()

    return materials
}
