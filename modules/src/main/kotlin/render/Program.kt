package render

import org.lwjgl.opengl.GL46.*
import algebra.*
import config.Config
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.use

enum class ShaderStageType {
    UNKNOWN,
    VERTEX,
    FRAGMENT,
    TESSELATION_EVAL,
    TESSELATION_CTRL,
    COMPUTE,
    GEOMETRY,
    MESH
}

class Program {
    private val _programName: String
    private var _programHandle: Int = 0
    private val _uniforms: MutableMap<String, Int> = mutableMapOf()

    constructor(name: String, shaders: List<Pair<String, Int>>) {
        _programName = name

        // TODO
    }

    constructor(shaders: ShaderSource) {
        _programName = shaders.programName

        _programHandle = glCreateProgram()

        for (shader in shaders.shadersList) {
            val (source, stage) = shader
            val compiled = compile(stage, source)

            glAttachShader(_programHandle, compiled)
        }

        glLinkProgram(_programHandle)

        val success = glGetProgrami(_programHandle, GL_LINK_STATUS)

        if (success == GL_FALSE) {
            val logLength = glGetProgrami(_programHandle, GL_INFO_LOG_LENGTH)
            val log = glGetProgramInfoLog(_programHandle, logLength)
            throw RuntimeException("Can't link program with trace:\n$log")
        }
    }

    private fun compile(stage: Int, source: String): Int {
        val shHandle = glCreateShader(stage)
        glShaderSource(shHandle, source)
        glCompileShader(shHandle)

        val success = glGetShaderi(shHandle, GL_COMPILE_STATUS)

        if (success == GL_FALSE) {
            val logLength = glGetShaderi(shHandle, GL_INFO_LOG_LENGTH)
            val log = glGetShaderInfoLog(shHandle, logLength)
            throw RuntimeException("Can't compile program with trace:\n$log")
        }

        return shHandle
    }

    fun use() {
        glUseProgram(_programHandle)
    }

    fun clean() {
        if (_programHandle != 0) {
            glDeleteProgram(_programHandle)
            _programHandle = 0
        }
    }

    private fun getUniformLocation(name: String): Int? {
        if (!_uniforms.containsKey(name)) {
            println("Warning: program \"$name\" not contains uniform $name.")
            return null
        }
        return _uniforms[name]!!
    }

    fun addUniform(name: String) {
        if (_uniforms.containsKey(name)) {
            println("Warning: program \"$name\" already contains uniform $name.")
            return
        }

        val location = glGetUniformLocation(_programHandle, name)
        _uniforms[name] = location
    }

    fun addUniform(names: List<String>) {
        for (name in names) {
            addUniform(name)
        }
    }

    fun setScalarUniform(name: String, value: Float) {
        val location = getUniformLocation(name) ?: return
        glUniform1f(location, value)
    }

    fun setScalarUniform(name: String, value: Double) {
        val location = getUniformLocation(name) ?: return
        glUniform1d(location, value)
    }

    fun setScalarUniform(name: String, value: Int) {
        val location = getUniformLocation(name) ?: return
        glUniform1i(location, value)
    }

    fun setScalarUniform(name: String, value: UInt) {
        val location = getUniformLocation(name) ?: return
        glUniform1ui(location, value.toInt())
    }

    fun setVectorUniform(id: String, value: Vector3) {
        val location = getUniformLocation(id) ?: return
        MemoryStack.stackPush().use { stack ->
            val floatBuffer = stack.mallocFloat(3)
            floatBuffer.put(value.array)
            floatBuffer.flip()
            glUniform3fv(location, floatBuffer)
        }

    }

//    fun setVectorUniform(id: String, value: Vector4) {
//        val location = getUniformLocation(id) ?: return fun setVectorUniform(id: String, value: Vector2) {
//            val location = getUniformLocation(id) ?: return
//            glUniform2fv(location, floatArrayOf(value.x, value.y))
//        }
//        glUniform4fv(location, floatArrayOf(value.x, value.y, value.z, value.w))
//    }

//    fun setMatrixUniform(id: String, transpose: Boolean, value: Matrix2) {
//        val location = getUniformLocation(id) ?: return
//        glUniformMatrix2fv(location, transpose, value.data)
//    }
//
//    fun setMatrixUniform(id: String, transpose: Boolean, value: Matrix3) {
//        val location = getUniformLocation(id) ?: return
//        glUniformMatrix3fv(location, transpose, value.data)
//    }

    fun setMatrixUniform(id: String, transpose: Boolean, value: Matrix4) {
        val location = getUniformLocation(id) ?: return
        MemoryStack.stackPush().use { stack ->
            val floatBuffer = stack.mallocFloat(16)
            floatBuffer.put(value.toFloatBuffer())
            floatBuffer.flip()
            glUniformMatrix4fv(location, false, floatBuffer)
        }
    }


}
