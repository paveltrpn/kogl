package context

import org.lwjgl.vulkan.VkInstance
import org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface
import org.lwjgl.system.MemoryStack
import kotlin.use

class Surface(instance: VkInstance, window: Long) : ResourceBase() {
    val mSurface: Long

    init {
        mStack.push().use { stack ->
            // Create a WSI surface for the window.
            val surfaceBuf = stack.mallocLong(1)
            glfwCreateWindowSurface(instance, window, null, surfaceBuf);
            mSurface = surfaceBuf.get(0);
        }
    }
}
