package render

import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.IntBuffer

import algebra.*

data class UniformInfo(
    val location: Int,
    val type: Int,
    val size: Int
)

// ============================================================================
// ======================= Program ============================================
// ============================================================================

class Program {
    private var _shaderSources: ShaderSource? = null

    private val _defines: MutableList<Pair<Int, String>> = mutableListOf()
    private val _extensions: MutableList<Pair<Int, String>> = mutableListOf()

    private var _programName: String = ""
    private var _programHandle: Int = 0
    private val _uniforms: MutableMap<String, UniformInfo> = mutableMapOf()

    // Permanent buffer. Used to pass vector and matrix float
    // data to shaders.
    private val _floatBuffer = MemoryUtil.memAllocFloat(16)

    private val _versionString = "#version 450 core\n\n"

//    constructor(shaders: ShaderSource) {
//        _programName = shaders.programName
//
//        _programHandle = glCreateProgram()
//
//        for (shader in shaders.shadersList) {
//            val (source, stage) = shader
//            val compiled = compile(stage, source)
//
//            glAttachShader(_programHandle, compiled)
//        }
//
//        glLinkProgram(_programHandle)
//
//        val success = glGetProgrami(_programHandle, GL_LINK_STATUS)
//
//        if (success == GL_FALSE) {
//            val logLength = glGetProgrami(_programHandle, GL_INFO_LOG_LENGTH)
//            val log = glGetProgramInfoLog(_programHandle, logLength)
//            throw RuntimeException("Can't link program with trace:\n$log")
//        }
//
//        reflectUniforms(_programHandle)
//    }

    var source: ShaderSource
        get(): ShaderSource {
            return _shaderSources ?: throw RuntimeException("Trying to obtain undefined shader sources!")
        }
        set(value) {
            if (_shaderSources != null) {
                throw RuntimeException("Shader source reattach is not allowed!")
            }

            _shaderSources = value
        }

    fun source(src: ShaderSource): Unit {
        _shaderSources = src
    }

    fun define(stage: Int, label: String): Unit {
        if (glGetProgrami(_programHandle, GL_LINK_STATUS) == GL_TRUE) {
            println("It meaningless to add defines to already linked program \"$_programName\"!")
            return
        }

        _defines.addLast(Pair(stage, label))
    }

    fun extension(stage: Int, label: String): Unit {
        if (glGetProgrami(_programHandle, GL_LINK_STATUS) == GL_TRUE) {
            println("It meaningless to add defines to already linked program \"$_programName\"!")
            return
        }

        _extensions.addLast(Pair(stage, label))
    }

    private fun parseDefines(defines: List<Pair<Int, String>>): Map<Int, String> {
        val result = mutableMapOf<Int, String>()

        for ((stage, label) in defines) {
            val existing = result[stage]
            if (existing != null) {
                result[stage] = "$existing#define $label\n"
            } else {
                result[stage] = "#define $label\n"
            }
        }

        return result
    }

    private fun parseExtensions(defines: List<Pair<Int, String>>): Map<Int, String> {
        val result = mutableMapOf<Int, String>()

        for ((stage, label) in defines) {
            val existing = result[stage]
            if (existing != null) {
                result[stage] = "$existing#extension $label\n"
            } else {
                result[stage] = "#extension $label\n"
            }
        }

        return result
    }

    fun cleanDefines(): Unit {
        _defines.clear()
    }

    fun cleanExtensions(): Unit {
        _extensions.clear()
    }

    fun build(): Unit {
        val shadersList = _shaderSources?.shadersList ?: throw RuntimeException("Set shader sources before build!")
        _programName = _shaderSources!!.programName

        _programHandle = glCreateProgram()

        for (shader in shadersList) {
            val (source, stage) = shader
            val compiled = compile(stage, source)

            glAttachShader(_programHandle, compiled)
        }

        glLinkProgram(_programHandle)

        val success = glGetProgrami(_programHandle, GL_LINK_STATUS)

        if (success == GL_FALSE) {
            val logLength = glGetProgrami(_programHandle, GL_INFO_LOG_LENGTH)
            val log = glGetProgramInfoLog(_programHandle, logLength)
            throw RuntimeException("Can't link program  \"$_programName\"!\ntrace:\n$log")
        }

        reflectUniforms(_programHandle)
    }

    private val stageToString: (Int) -> String = { stage ->
        when (stage) {
            GL_VERTEX_SHADER -> "VERTEX"
            GL_FRAGMENT_SHADER -> "FRAGMENT"
            GL_GEOMETRY_SHADER -> "GEOMETRY"
            GL_TESS_CONTROL_SHADER -> "TESS_CONTROL"
            GL_TESS_EVALUATION_SHADER -> "TESS_EVALUATION"
            GL_COMPUTE_SHADER -> "COMPUTE"
            else -> "UNKNOWN"
        }
    }

    private fun compile(stage: Int, source: String): Int {
        val shHandle = glCreateShader(stage)

        val extension = parseExtensions(_extensions)
        val thisStageExtensions = extension[stage] ?: ""

        val defines = parseDefines(_defines)
        val thisStageDefines = defines[stage] ?: ""

//        val fullSource = _versionString + thisStageExtensions + thisStageDefines + source
//        println("$fullSource")

        glShaderSource(shHandle, _versionString, thisStageExtensions, thisStageDefines, source)

        glCompileShader(shHandle)

        val success = glGetShaderi(shHandle, GL_COMPILE_STATUS)

        if (success == GL_FALSE) {
            val logLength = glGetShaderi(shHandle, GL_INFO_LOG_LENGTH)
            val log = glGetShaderInfoLog(shHandle, logLength)
            throw RuntimeException("Can't compile program \"$_programName\" stage \"${stageToString(stage)}\"!\ntrace:\n$log")
        }

        return shHandle
    }

    private fun reflectUniforms(programHandle: Int): Unit {
        val numUniforms = glGetProgrami(programHandle, GL_ACTIVE_UNIFORMS)
        val maxLength = glGetProgrami(programHandle, GL_ACTIVE_UNIFORM_MAX_LENGTH)

        MemoryStack.stackPush().use { stack ->
            val lengthBuf: IntBuffer = stack.mallocInt(1)
            val sizeBuf: IntBuffer = stack.mallocInt(1)
            val typeBuf: IntBuffer = stack.mallocInt(1)
            val nameBuf: ByteBuffer = stack.malloc(maxLength)

            for (i in 0 until numUniforms) {
                glGetActiveUniform(programHandle, i, lengthBuf, sizeBuf, typeBuf, nameBuf)

                val uniformName = MemoryUtil.memUTF8(nameBuf, lengthBuf.get(0))

                val cleanName = if (uniformName.endsWith("]")) {
                    val bracketIdx = uniformName.lastIndexOf('[')
                    if (bracketIdx > 0) uniformName.substring(0, bracketIdx) else uniformName
                } else {
                    uniformName
                }

                val size = sizeBuf.get(0)
                val type = typeBuf.get(0)

                val location = glGetUniformLocation(_programHandle, cleanName)

                _uniforms[cleanName] = UniformInfo(location, type, size)
            }
        }
    }

    val handle: Int
        get(): Int {
            return _programHandle
        }

    fun use() {
        glUseProgram(_programHandle)
    }

    fun clean() {
        MemoryUtil.memFree(_floatBuffer)

        if (_programHandle != 0) {
            glDeleteProgram(_programHandle)
            _programHandle = 0
        }
    }

    private fun uniformLocation(name: String): Int {
        return _uniforms[name]?.location
            ?: throw RuntimeException("Error: program \"$_programName\" not contains uniform \"$name\".")
    }

    fun setScalarUniform(name: String, value: Float) {
        val location = uniformLocation(name)
        glUniform1f(location, value)
    }

    fun setScalarUniform(name: String, value: Double) {
        val location = uniformLocation(name)
        glUniform1d(location, value)
    }

    fun setScalarUniform(name: String, value: Int) {
        val location = uniformLocation(name)
        glUniform1i(location, value)
    }

    fun setScalarUniform(name: String, value: UInt) {
        val location = uniformLocation(name)
        glUniform1ui(location, value.toInt())
    }

    fun setVectorUniform(id: String, value: Vector2) {
        val location = uniformLocation(id)
        _floatBuffer.clear().put(0, value.data, 0, 2).flip()
        _floatBuffer.position(0).limit(2)
        glUniform3fv(location, _floatBuffer)
    }

    fun setVectorUniform(id: String, value: Vector3) {
        val location = uniformLocation(id)
        _floatBuffer.clear().put(0, value.data, 0, 3).flip()
        _floatBuffer.position(0).limit(3)
        glUniform3fv(location, _floatBuffer)
    }

    fun setVectorUniform(id: String, value: Vector4) {
        val location = uniformLocation(id)
        _floatBuffer.clear().put(0, value.data, 0, 4).flip()
        _floatBuffer.position(0).limit(4)
        glUniform3fv(location, _floatBuffer)
    }

    fun setMatrixUniform(id: String, transpose: Boolean, value: Matrix2) {
        val location = uniformLocation(id)
        _floatBuffer.clear().put(0, value.data, 0, 4).flip()
        _floatBuffer.position(0).limit(4)
        glUniformMatrix2fv(location, transpose, _floatBuffer)

    }

    fun setMatrixUniform(id: String, transpose: Boolean, value: Matrix3) {
        val location = uniformLocation(id)
        _floatBuffer.clear().put(0, value.data, 0, 9).flip()
        _floatBuffer.position(0).limit(9)
        glUniformMatrix3fv(location, transpose, _floatBuffer)

//        MemoryStack.stackPush().use { stack ->
//            val floatBuffer = stack.mallocFloat(9)
//            floatBuffer.put(value.data)
//            floatBuffer.flip()
//            glUniformMatrix3fv(location, transpose, floatBuffer)
//        }
    }

    fun setMatrixUniform(id: String, transpose: Boolean, value: Matrix4) {
        val location = uniformLocation(id)
        _floatBuffer.clear().put(value.data).flip()
        glUniformMatrix4fv(location, transpose, _floatBuffer)
    }

    @JvmName("setNumberUniform")
    fun <T : Number> set(p: Pair<String, T>): Unit {
        val (id, value) = p
        when (value) {
            is Int -> {
                setScalarUniform(id, value)
            }

            is Float -> {
                setScalarUniform(id, value)
            }

            is Double -> {
                setScalarUniform(id, value)
            }
        }
    }

    @JvmName("setMatrix2Uniform")
    fun set(p: Pair<String, Matrix2>, transpose: Boolean): Unit {
        val (id, value) = p
        setMatrixUniform(id, transpose, value)
    }

    @JvmName("setMatrix3Uniform")
    fun set(p: Pair<String, Matrix3>, transpose: Boolean): Unit {
        val (id, value) = p
        setMatrixUniform(id, transpose, value)
    }

    @JvmName("setMatrix4Uniform")
    fun set(p: Pair<String, Matrix4>, transpose: Boolean): Unit {
        val (id, value) = p
        setMatrixUniform(id, transpose, value)
    }

    @JvmName("setVector2Uniform")
    fun set(p: Pair<String, Vector2>): Unit {
        val (id, value) = p
        setVectorUniform(id, value)
    }

    @JvmName("setVector3Uniform")
    fun set(p: Pair<String, Vector3>): Unit {
        val (id, value) = p
        setVectorUniform(id, value)
    }

    @JvmName("setVector4Uniform")
    fun set(p: Pair<String, Vector4>): Unit {
        val (id, value) = p
        setVectorUniform(id, value)
    }
}

// ============================================================================

interface UniformSetter<T> {
    fun set(p: Program)
}

fun <T> value(pair: Pair<String, T>): UniformSetter<T> {
    when (pair.second) {
        is Vector2 -> {
            return object : UniformSetter<T> {
                override fun set(p: Program) {
                    val (id, value) = pair
                    p.setVectorUniform(id, value as Vector2)
                }
            }
        }

        is Vector3 -> {
            return object : UniformSetter<T> {
                override fun set(p: Program) {
                    val (id, value) = pair
                    p.setVectorUniform(id, value as Vector3)
                }
            }
        }

        is Vector4 -> {
            return object : UniformSetter<T> {
                override fun set(p: Program) {
                    val (id, value) = pair
                    p.setVectorUniform(id, value as Vector4)
                }
            }
        }

        else -> {
            throw RuntimeException("Unknown UniformSetter<T> type!")
        }
    }
}

/**
 * Weirdo uniform assignment.
 * Can be used as ```program assign value("some_unifirm" to some_value)```
 */
infix fun <T> Program.assign(setter: UniformSetter<T>): Unit {
    setter.set(this)
}
