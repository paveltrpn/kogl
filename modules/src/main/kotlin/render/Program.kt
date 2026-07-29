package render

import org.lwjgl.opengl.GL46.*
import algebra.*
import config.Config
import java.nio.CharBuffer
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readText

enum class ShaderStageType {
    VERTEX,
    FRAGMENT,
    TESSELATION_EVAL,
    TESSELATION_CTRL,
    COMPUTE,
    GEOMETRY,
    MESH
}

class Program {
    private var programHandle: GLuint = 0
    private var name: String = ""
    private val uniforms: MutableMap<String, GLint> = mutableMapOf()

    fun init(name: String) {
        if (programHandle != 0u) {
            println("warning: reinitialize already linked program!")
            clean()
        }

        this.name = name

        val stageUnits = mutableListOf<GLuint>()

        val availableShaderFiles = scanForShaderFiles(name)

        for ((stage, type) in availableShaderFiles) {
            val source = readSource(stage)
            val unit = compile(type, source)
            stageUnits.add(unit)
        }

        programHandle = glCreateProgram()

        for (handle in stageUnits) {
            glAttachShader(programHandle, handle)
        }

        glLinkProgram(programHandle)

        val success = glGetProgrami(programHandle, GL_LINK_STATUS)

        if (success == GL_FALSE) {
            val logLength = glGetProgrami(programHandle, GL_INFO_LOG_LENGTH)
            val log = String(CharBuffer(logLength))
            glGetProgramInfoLog(programHandle, logLength, null)
            throw RuntimeException("gl::Program === can't link program with trace:\n$log")
        }
    }

    fun use() {
        glUseProgram(programHandle)
    }

    fun clean() {
        if (programHandle != 0u) {
            glDeleteProgram(programHandle)
            programHandle = 0u
        }
    }

    fun addUniform(id: String) {
        if (uniforms.containsKey(id)) {
            println("warning: program \"$name\" already contains uniform $id")
            return
        }

        val location = glGetUniformLocation(programHandle, id)
        uniforms[id] = location
    }

//    fun addUniform(ids: List<String>) {
//        for (id in ids) {
//            addUniform(id)
//        }
//    }
//
//    fun setScalarUniform(id: String, value: Float) {
//        val location = getUniformLocation(id) ?: return
//        glUniform1f(location, value)
//    }
//
//    fun setScalarUniform(id: String, value: Double) {
//        val location = getUniformLocation(id) ?: return
//        glUniform1d(location, value)
//    }
//
//    fun setScalarUniform(id: String, value: Int) {
//        val location = getUniformLocation(id) ?: return
//        glUniform1i(location, value)
//    }
//
//    fun setScalarUniform(id: String, value: UInt) {
//        val location = getUniformLocation(id) ?: return
//        glUniform1ui(location, value)
//    }
//
//    fun setVectorUniform(id: String, value: Vector2) {
//        val location = getUniformLocation(id) ?: return
//        glUniform2fv(location, floatArrayOf(value.x, value.y))
//    }
//
//    fun setVectorUniform(id: String, value: Vector3) {
//        val location = getUniformLocation(id) ?: return
//        glUniform3fv(location, floatArrayOf(value.x, value.y, value.z))
//    }
//
//    fun setVectorUniform(id: String, value: Vector4) {
//        val location = getUniformLocation(id) ?: return
//        glUniform4fv(location, floatArrayOf(value.x, value.y, value.z, value.w))
//    }
//
//    fun setMatrixUniform(id: String, transpose: Boolean, value: Matrix2) {
//        val location = getUniformLocation(id) ?: return
//        glUniformMatrix2fv(location, transpose, value.data)
//    }
//
//    fun setMatrixUniform(id: String, transpose: Boolean, value: Matrix3) {
//        val location = getUniformLocation(id) ?: return
//        glUniformMatrix3fv(location, transpose, value.data)
//    }
//
//    fun setMatrixUniform(id: String, transpose: Boolean, value: Matrix4) {
//        val location = getUniformLocation(id) ?: return
//        glUniformMatrix4fv(location, transpose, value.data)
//    }
//
//    private fun getUniformLocation(id: String): GLuint? {
//        if (!uniforms.containsKey(id)) {
//            println("warning: program \"$name\" not contains uniform $id")
//            return null
//        }
//        return uniforms[id]!!
//    }

    private fun scanForShaderFiles(name: String): List<Pair<String, GLenum>> {
        val configHandle = Config.instance()
        val basePath = configHandle.workPath()
        val shaderFilesPath = Path("$basePath/assets/shaders/")

        if (!exists(shaderFilesPath)) {
            throw RuntimeException("gl::Program === shaders directory not found: $shaderFilesPath")
        }

        val retItem = mutableListOf<Pair<String, GLenum>>()
        val shaderStageMap = mapOf(
            "VERTEX" to GL_VERTEX_SHADER,
            "FRAGMENT" to GL_FRAGMENT_SHADER,
            "TESSEVAL" to GL_TESS_EVALUATION_SHADER,
            "TESSCTRL" to GL_TESS_CONTROL_SHADER,
            "GEOMETRY" to GL_GEOMETRY_SHADER,
            "COMPUTE" to GL_COMPUTE_SHADER
        )

        val shaderFiles = shaderFilesPath.toFile().listFiles { file ->
            file.name.contains("gl_${name}_") && file.extension == "glsl"
        } ?: emptyArray()

        for (entry in shaderFiles) {
            val fileName = entry.nameWithoutExtension
            val parts = fileName.split("_")
            val suffix = parts.lastOrNull()

            if (suffix != null) {
                val type = shaderStageMap[suffix]
                if (type == null) {
                    throw RuntimeException("gl::Program === shader stage type $suffix not exist!")
                }
                retItem.add(Pair("$fileName.glsl", type))
            }
        }

        if (retItem.isEmpty()) {
            throw RuntimeException("gl::Program === shaders directory not contain files for shader $name")
        }

        return retItem
    }

    private fun readSource(name: String): String {
        val configHandle = Config.instance()
        val basePath = configHandle.workPath()
        val path = Path("$basePath/assets/shaders/$name")

        if (!exists(path)) {
            throw RuntimeException("gl::Program === file not found: ${path.fileName}")
        }

        return path.toFile().readText()
    }

    private fun compile(stage: GLenum, source: String): GLuint {
        val shHandle = glCreateShader(stage)
        glShaderSource(shHandle, source)
        glCompileShader(shHandle)

        val success = glGetShaderi(shHandle, GL_COMPILE_STATUS)

        if (success == GL_FALSE) {
            val logLength = glGetShaderi(shHandle, GL_INFO_LOG_LENGTH)
            val log = glGetShaderInfoLog(shHandle, logLength)
            throw RuntimeException("gl::Program === can't compile program with trace:\n$log")
        }

        return shHandle
    }
}
