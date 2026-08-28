package gldebug

import org.lwjgl.opengl.GL46.*
import org.lwjgl.opengl.GLDebugMessageCallbackI
import org.lwjgl.system.MemoryUtil
import java.nio.IntBuffer

fun mapGLDebugMsgInfo(
    source: Int,
    type: Int, severity: Int
): Triple<String, String, String> {
    val sourceStr = when (source) {
        GL_DEBUG_SOURCE_API -> "API"
        GL_DEBUG_SOURCE_WINDOW_SYSTEM -> "Window System"
        GL_DEBUG_SOURCE_SHADER_COMPILER -> "Shader Compiler"
        GL_DEBUG_SOURCE_THIRD_PARTY -> "Third Party"
        GL_DEBUG_SOURCE_APPLICATION -> "Application"
        GL_DEBUG_SOURCE_OTHER -> "Other"
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

    val severityStr = when (severity) {
        GL_DEBUG_SEVERITY_HIGH -> "high"
        GL_DEBUG_SEVERITY_MEDIUM -> "medium"
        GL_DEBUG_SEVERITY_LOW -> "low"
        GL_DEBUG_SEVERITY_NOTIFICATION -> "notification"
        else -> "Unknown"
    }

    return Triple(sourceStr, typeStr, severityStr)
}

abstract class GLDebugCallbackBase : GLDebugMessageCallbackI {
    fun message(length: Int, message: Long): String {
        val buffer = MemoryUtil.memByteBuffer(message, length)
        return ByteArray(length).apply {
            buffer.get(this)
        }.decodeToString()
    }
}

class GLDebugCallbackAll : GLDebugCallbackBase() {
    override fun invoke(
        source: Int,
        type: Int,
        id: Int,
        severity: Int,
        length: Int,
        message: Long,
        userParam: Long
    ) {
        val msg = message(length, message)

        val (sourceStr, typeStr, severityStr) = mapGLDebugMsgInfo(
            source, type, severity
        )

        println("[$sourceStr] [$typeStr] [$severityStr] $msg")
    }
}

class GLDebugApiErrorHight : GLDebugCallbackBase() {
    override fun invoke(
        source: Int,
        type: Int,
        id: Int,
        severity: Int,
        length: Int,
        message: Long,
        userParam: Long
    ) {
        val mustShown =
            (source == GL_DEBUG_SOURCE_API) and (type == GL_DEBUG_TYPE_ERROR) and (severity == GL_DEBUG_SEVERITY_HIGH)

        if (mustShown) {
            val msg = message(length, message)

            val (sourceStr, typeStr, severityStr) = mapGLDebugMsgInfo(
                source, type, severity
            )

            println("[$sourceStr] [$typeStr] [$severityStr] $msg")
        }
    }
}

class GLDebugDiscardNotification : GLDebugCallbackBase() {
    override fun invoke(
        source: Int,
        type: Int,
        id: Int,
        severity: Int,
        length: Int,
        message: Long,
        userParam: Long
    ) {
        val mustShown = (severity != GL_DEBUG_SEVERITY_NOTIFICATION)

        if (mustShown) {
            val msg = message(length, message)

            val (sourceStr, typeStr, severityStr) = mapGLDebugMsgInfo(
                source, type, severity
            )

            println("[$sourceStr] [$typeStr] [$severityStr] $msg")
        }
    }
}

fun <T : GLDebugMessageCallbackI> enableDebugContext(cb: T): Unit {
    glEnable(GL_DEBUG_OUTPUT)
    glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS)

//    NOTE: glDebugMessageControl not working at all! No messages filtered with any
//    of this calls!
//    glDebugMessageControl(
//        GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, 0, true
//    )

//    glDebugMessageControl(
//        GL_DEBUG_SOURCE_API, GL_DEBUG_TYPE_OTHER, GL_DEBUG_SEVERITY_NOTIFICATION, 0, true
//    )

//    glDebugMessageControl(
//        GL_DEBUG_SOURCE_API, GL_DEBUG_TYPE_ERROR, GL_DEBUG_SEVERITY_HIGH, 0, true
//    )

    glDebugMessageCallback(cb, 0L)
}
