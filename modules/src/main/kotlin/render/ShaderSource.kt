package render

import org.lwjgl.opengl.GL46.*
import java.io.File

import config.Config

class ShaderSource(val programName: String) {
    // Shader files list with corresponding stage types.
    private val _shadersPath: String
    private val _shadersList: List<Pair<String, Int>>

    init {
        val bp = Config.instance().basePath
        _shadersPath = "$bp/assets/shaders"

        val allShadersList = parseDirectory(_shadersPath)
        val programShaders = extractProgramShaders(allShadersList, programName)

        // Must exist at least 2 shaders to form some program
        if (programShaders.size < 2) {
            throw IllegalStateException("Program '$programName' requires at least 2 shaders, found ${programShaders.size}")
        }

        _shadersList = readShaders(programShaders)
    }

    val shadersList: List<Pair<String, Int>>
        get(): List<Pair<String, Int>> {
            return _shadersList
        }

    private fun parseDirectory(path: String): List<String> {
        val directory = File(path)
        val filesList = directory.listFiles()

        val onlyGlsl = filesList?.filter { it.isFile && it.extension.equals("glsl", ignoreCase = true) }
            ?.map { it.name } ?: emptyList()


        return onlyGlsl
    }

    private fun extractProgramShaders(allShaders: List<String>, programName: String): List<String> {
        val pattern = "^gl_${programName}_.*\\.glsl$".toRegex()
        return allShaders.filter { it.matches(pattern) }
    }

    private fun readShaders(programShaders: List<String>): List<Pair<String, Int>> {
        val shaderStageMap = mapOf(
            "VERTEX" to GL_VERTEX_SHADER,
            "FRAGMENT" to GL_FRAGMENT_SHADER,
            "TESSEVAL" to GL_TESS_EVALUATION_SHADER,
            "TESSCTRL" to GL_TESS_CONTROL_SHADER,
            "GEOMETRY" to GL_GEOMETRY_SHADER,
            "COMPUTE" to GL_COMPUTE_SHADER
        )

        val bp = Config.instance().basePath

        return programShaders.map { fileName ->
            val filePath = "$_shadersPath/$fileName"
            val source = File(filePath).readText()
            val stageName = fileName.substringAfter("gl_${programName}_").substringBeforeLast(".glsl").uppercase()
            val shaderType =
                shaderStageMap[stageName] ?: throw IllegalArgumentException("Unknown shader stage: $stageName")
            source to shaderType
        }
    }


}