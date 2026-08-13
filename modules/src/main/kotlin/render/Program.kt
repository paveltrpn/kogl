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

        reflectUniform(_programHandle)
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

    fun reflectUniform(programHandle: Int): Unit {
        val uniformMap = mutableMapOf<String, UniformInfo>()

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

                println(" === $cleanName")

                val size = sizeBuf.get(0)
                val type = typeBuf.get(0)
                
                val location = glGetUniformLocation(_programHandle, cleanName)

                uniformMap[cleanName] = UniformInfo(location, type, size)
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
            floatBuffer.put(value.data)
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
            floatBuffer.put(value.data)
            floatBuffer.flip()
            glUniformMatrix4fv(location, false, floatBuffer)
        }
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

// ==========================================================================================================================

//#include <GL/glew.h> // Or any OpenGL loading library like glad/glad.h
//#include <iostream>
//#include <vector>
//#include <string>
//#include <unordered_map>
//
//// Structure to hold introspected uniform metadata

//
//std::unordered_map<std::string, UniformInfo> ReflectUniforms(GLuint programID) {
//    std::unordered_map<std::string, UniformInfo> uniformMap;
//

//
//    return uniformMap;
//}
