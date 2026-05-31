package render

//import java.nio.file.Path
//import kotlin.io.path.Path
//import kotlin.io.path.exists
//
//import org.lwjgl.opengl.GL46.*
//import java.nio.IntBuffer
//import kotlin.io.path.extension
//import kotlin.io.path.isRegularFile
//import kotlin.io.path.listDirectoryEntries
//import kotlin.io.path.nameWithoutExtension
//import kotlin.io.path.pathString
//import kotlin.io.path.readText
//
//enum class ShaderStageType {
//    VERTEX,
//    FRAGMENT,
//    TESSELATION_EVAL,
//    TESSELATION_CTRL,
//    COMPUTE,
//    GEOMETRY,
//    MESH
//}
//
//// Shader filename stage type suffix.
//const val vertex_stage_suffix = "VERTEX"
//const val tessctrl_stage_suffix = "TESSCTRL"
//const val tesseval_stage_suffix = "TESSEVAL"
//const val geometry_stage_suffix = "GEOMETRY"
//const val fragment_stage_suffix = "FRAGMENT"
//const val compute_stage_suffix = "COMPUTE"
//const val mesh_stage_suffix = "MESH"
//
//
//val StagesSuffixMap = mapOf(
//    vertex_stage_suffix to GL_VERTEX_SHADER,
//    fragment_stage_suffix to GL_FRAGMENT_SHADER,
//    tesseval_stage_suffix to GL_TESS_EVALUATION_SHADER,
//    tessctrl_stage_suffix to GL_TESS_CONTROL_SHADER,
//    geometry_stage_suffix to GL_GEOMETRY_SHADER,
//    compute_stage_suffix to GL_COMPUTE_SHADER,
//    // { mesh_stage_suffix, GL_MESH_SHADER },
//)
//
//// OpenGL shader program.
//// Constructor - where and which source files to find.
//// "path" - path to directory with sahders.
//// "name" - shader program name, satisfies
////  shader filename template, i.e. "gl_{name}_STAGETYPE.glsl"
//class Program(private val path: String, val name: String) {
//    var program: Int = 0
//    val uniforms: MutableMap<String, Int> = mutableMapOf()
//
//    init {
//        val pathToShaders = Path(path)
//
//        // Check shader path exist.
//        if (!pathToShaders.exists()) {
//            throw RuntimeException("Path to shader ${path} not exist! ")
//        }
//
//        // Get list of shader source file to given shader program.
//        val shaderSources = scanForSources(pathToShaders)
//
//        program = glCreateProgram()
//
//        // Read and compile each shader source file.
//        for (shader in shaderSources) {
//            val sourceString = readSource(shader.first)
//            val stage = StagesSuffixMap[shader.second]!!
//            val shaderHandle = compile(stage, sourceString)
//            glAttachShader(program, shaderHandle)
//        }
//
//        // Link OpenGL shader program.
//        glLinkProgram(program)
//
//        // Check program linking log.
//        val success = glGetProgrami(program, GL_LINK_STATUS)
//
//        val logLength = glGetProgrami(program, GL_INFO_LOG_LENGTH)
//        val log = glGetProgramInfoLog(program, logLength)
//
//        if (success == GL_FALSE) {
//            throw RuntimeException("Error while shader \"${name}\" linking, log: $log")
//        } else {
//            if (!log.isEmpty()) {
//                println("shader \"${name}\" link log: $log")
//            }
//        }
//
//        println("program \"${name}\" link success!")
//    }
//
//    fun use(): Unit {
//        glUseProgram(program)
//    }
//
//    fun delete(): Unit {
//        uniforms.clear()
//        glDeleteProgram(program)
//    }
//
//    // Returns list with full path to shader file source and shader type.
//    private fun scanForSources(pathToShaders: Path): MutableList<Pair<String, String>> {
//        var shaderFilesList: MutableList<Pair<String, String>> = mutableListOf()
//
//        val files = pathToShaders.listDirectoryEntries()
//
//        for (file in files) {
//            if (file.isRegularFile() && (file.extension == "glsl")) {
//                val name = file.nameWithoutExtension.split("_")
//                val shaderName = name[1]
//                if (shaderName == this.name) {
//                    shaderFilesList.add(Pair(file.pathString, name[2]))
//                }
//
//            }
//        }
//
//        // Shader program must be linked at least from
//        // two shader files - vertex and fragment.
//        if (shaderFilesList.size < 2) {
//            throw RuntimeException("Can't initialize shader program ${this.name} from only one shader source file!")
//        }
//
//        return shaderFilesList
//    }
//
//    private fun readSource(sourcePath: String): String {
//        val filePath = Path(sourcePath)
//
//        try {
//            val content = filePath.readText()
//            return content
//        } catch (e: Exception) {
//            println("Error reading file: ${e.message}")
//            return ""
//        }
//    }
//
//    private fun compile(stage: Int, source: String): Int {
//        val shaderHandle = glCreateShader(stage)
//
//        glShaderSource(shaderHandle, source)
//
//        glCompileShader(shaderHandle)
//
//        val success = glGetShaderi(shaderHandle, GL_COMPILE_STATUS)
//
//        val logLength = glGetShaderi(shaderHandle, GL_INFO_LOG_LENGTH)
//        val log = glGetShaderInfoLog(shaderHandle, logLength)
//
//        if (success == GL_FALSE) {
//            throw RuntimeException("Error while shader \"${name}\" compile, log: $log")
//        } else {
//            if (!log.isEmpty()) {
//                println("shader \"${name}\" compilation log: $log")
//            }
//        }
//
//        return shaderHandle
//    }
//
//    fun getUniformLocation(id: String): Int {
//        val location = glGetUniformLocation(program, id)
//
//        if (location == GL_INVALID_VALUE) {
//            println(
//                "gl::Program === uniform location error - invalid value"
//            )
//        }
//
//        if (location == GL_INVALID_OPERATION) {
//            println(
//                "gl::Program ===  uniform location error - invalid operation"
//            )
//        }
//
//        return location
//    }
//
//    fun addUniform(id: String): Unit {
//        if (uniforms.contains(id)) {
//            println(
//                "gl::Program === program \"{$name}\" already contains uniform {$id}"
//            )
//            return
//        }
//
//        val location = glGetUniformLocation(program, id);
//
//        uniforms[id] = location
//    }
//
//    fun addUniform(ids: List<String>): Unit {
//        for (item in ids) {
//            addUniform(item)
//        }
//    }
//}
