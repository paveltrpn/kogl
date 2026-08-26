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
        val msg = MemoryUtil.memByteBuffer(message, length).toString()
        if (severity == GL_DEBUG_SEVERITY_HIGH) {
            println("[GL High Severity] $msg")

            // println("Aborting...")
            // System.exit(1)
        } else {
            println("[GL Debug] $msg")
        }
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
