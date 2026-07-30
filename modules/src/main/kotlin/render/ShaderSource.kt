package render

import org.lwjgl.opengl.GL46.*

import config.Config

class ShaderSource(val programName: String) {
    // Shader files list with corresponding stage types.
    private var _shadersList: MutableList<Pair<String, Int>> = mutableListOf()
    private var _basePath: String = Config.instance().basePath

    init {
        val shaderStageMap = mapOf(
            "VERTEX" to GL_VERTEX_SHADER,
            "FRAGMENT" to GL_FRAGMENT_SHADER,
            "TESSEVAL" to GL_TESS_EVALUATION_SHADER,
            "TESSCTRL" to GL_TESS_CONTROL_SHADER,
            "GEOMETRY" to GL_GEOMETRY_SHADER,
            "COMPUTE" to GL_COMPUTE_SHADER
        )
    }

    val shadersList: MutableList<Pair<String, Int>>
        get(): MutableList<Pair<String, Int>> {
            return _shadersList
        }

    private fun parseDirectory(path: String): Unit {

    }

}