package render

import config.Config

class ShaderHalper(val programName: String) {
    // Shader files list with corresponding stage types.
    private var _shadersList: MutableList<Pair<String, ShaderStageType>> = mutableListOf()

    init {

    }
    
    var shadersList: MutableList<Pair<String, ShaderStageType>> = mutableListOf()
    fun get(): MutableList<Pair<String, ShaderStageType>> {
        return _shadersList
    }
}