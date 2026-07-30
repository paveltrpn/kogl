package render

import org.lwjgl.opengl.GL46.*
import java.io.File

import config.Config

class ShaderSource(val programName: String) {
    // Shader files list with corresponding stage types.
    private var _shadersList: MutableList<Pair<String, Int>> = mutableListOf()

    init {
        val shaderStageMap = mapOf(
            "VERTEX" to GL_VERTEX_SHADER,
            "FRAGMENT" to GL_FRAGMENT_SHADER,
            "TESSEVAL" to GL_TESS_EVALUATION_SHADER,
            "TESSCTRL" to GL_TESS_CONTROL_SHADER,
            "GEOMETRY" to GL_GEOMETRY_SHADER,
            "COMPUTE" to GL_COMPUTE_SHADER
        )

        val bp = Config.instance().basePath
        val shadersPath = "$bp/assets/shaders"

        val allSahdersList = parseDirectory(shadersPath)


    }

    val shadersList: MutableList<Pair<String, Int>>
        get(): MutableList<Pair<String, Int>> {
            return _shadersList
        }

    private fun parseDirectory(path: String): List<String> {
        val directory = File(path)
        val filesList = directory.listFiles()

        val onlyGlsl = filesList?.filter { it.isFile && it.extension.equals(".glsl", ignoreCase = true) }
            ?.map { it.name } ?: emptyList()


        return onlyGlsl
    }

    private fun extractProgramShaders(programName: String): List<String> {
        
    }

}