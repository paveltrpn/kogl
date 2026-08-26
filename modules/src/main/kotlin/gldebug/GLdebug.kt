package debug

import org.lwjgl.opengl.GL46.*
import org.lwjgl.opengl.GLDebugMessageCallbackI
import org.lwjgl.system.MemoryUtil

class GLDebugCallback : GLDebugMessageCallbackI {
    override fun invoke(
        source: Int,
        type: Int,
        id: Int,
        severity: Int,
        length: Int,
        message: Long,
        userParam: Long
    ) {
        val buffer = MemoryUtil.memByteBuffer(message, length)

        val msg = ByteArray(length).apply {
            buffer.get(this)
        }.decodeToString()

        val sourceStr = when (source) {
            GL_DEBUG_SOURCE_API -> "API"
            GL_DEBUG_SOURCE_WINDOW_SYSTEM -> "Window System"
            GL_DEBUG_SOURCE_SHADER_COMPILER -> "Shader Compiler"
            GL_DEBUG_SOURCE_THIRD_PARTY -> "Third Party"
            GL_DEBUG_SOURCE_APPLICATION -> "Application"
            GL_DEBUG_SOURCE_OTHER -> "Other"
            else -> "Unknown"
        }

        val severityStr = when (severity) {
            GL_DEBUG_SEVERITY_HIGH -> "high"
            GL_DEBUG_SEVERITY_MEDIUM -> "medium"
            GL_DEBUG_SEVERITY_LOW -> "low"
            GL_DEBUG_SEVERITY_NOTIFICATION -> "notification"
            else -> "Unknown"
        }

        val typeStr = when (type) {
            GL_DEBUG_TYPE_ERROR -> "Error"
            GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR -> "Deprecated Behaviour"
            GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR -> "Undefined Behaviour"
            GL_DEBUG_TYPE_PORTABILITY -> "Portability"
            GL_DEBUG_TYPE_PERFORMANCE -> "Performance"
            GL_DEBUG_TYPE_MARKER -> "Marker"
            GL_DEBUG_TYPE_PUSH_GROUP -> "Push Group"
            GL_DEBUG_TYPE_POP_GROUP -> "Pop Group"
            GL_DEBUG_TYPE_OTHER -> "Other"
            else -> "Unknown"
        }

        println("[$sourceStr] [$severityStr] [$typeStr] $msg")

//        when (severity) {
//            GL_DEBUG_SEVERITY_HIGH -> {
//                println("[GL High Severity] $msg")
//
//                // println("Aborting...")
//                // System.exit(1)
//            }
//
//            else -> {
//                println("[GL Debug] $msg")
//            }
//        }
    }
}

fun enableDebugContext(): Unit {
    glEnable(GL_DEBUG_OUTPUT)
    glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS)
    glDebugMessageCallback(GLDebugCallback(), 0L)
    glDebugMessageControl(
        GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, 0, true
    )
}
