package render

import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.IntBuffer

import algebra.*
import config.*

data class UniformInfo(
    val location: Int,
    val type: Int,
    val size: Int
)

class Program {
    private var _shaderSources: ShaderSource? = null

    private val _defines: MutableList<Pair<Int, String>> = mutableListOf()

    private var _programName: String = ""
    private var _programHandle: Int = 0
    private val _uniforms: MutableMap<String, UniformInfo> = mutableMapOf()

    // Permanent buffer. Used to pass vector and matrix float
    // data to shaders.
    private val _floatBuffer = MemoryUtil.memAllocFloat(16)

    val versionString = "#version 450 core\n\n"

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

    fun source(src: ShaderSource): Unit {
        _shaderSources = src
    }

    fun define(stage: Int, label: String): Unit {
        _defines.addLast(Pair(stage, label))
    }

    private fun parseDefines(defines: List<Pair<Int, String>>): Map<Int, String> {
        val result = mutableMapOf<Int, String>()

        for ((stage, label) in defines) {
            val existing = result[stage]
            if (existing != null) {
                result[stage] = "$existing\n#define $label"
            } else {
                result[stage] = "#define $label"
            }
        }

        return result
    }

    fun cleanDefines(): Unit {
        _defines.clear()
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

        val defines = parseDefines(_defines)
        val thisStageDefines = defines[stage] ?: "#pragma"

        glShaderSource(shHandle, versionString, thisStageDefines, source)

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
            ?: throw RuntimeException("Error: program \"$name\" not contains uniform $name.")
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
}

//// 1. Define your shader source segments
//const char* versionSrc = "#version 330 core\n";
//
//const char* definesSrc = "#define USE_LIGHTING 1\n"
//"#define MAX_LIGHTS 4\n";
//
//const char* shaderBodySrc =
//"out vec4 FragColor;\n"
//"void main() {\n"
//"    #if USE_LIGHTING\n"
//"        FragColor = vec4(1.0, 0.5, 0.2, 1.0);\n"
//"    #else\n"
//"        FragColor = vec4(0.5, 0.5, 0.5, 1.0);\n"
//"    #endif\n"
//"}";
//
//// 2. Put the string pointers into an array in the exact order needed
//const char* sourceArray[] = { versionSrc, definesSrc, shaderBodySrc };
//
//// 3. Create the shader object
//GLuint fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
//
//// 4. Pass the array of 3 strings to OpenGL
//glShaderSource(fragmentShader, 3, sourceArray, NULL);
//
//// 5. Compile as usual
//glCompileShader(fragmentShader);


