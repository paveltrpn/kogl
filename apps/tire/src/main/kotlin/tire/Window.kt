package tire

import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.Configuration
import org.lwjgl.system.MemoryUtil.*

class Window {
    private var allocator: GLFWAllocator? = null
    private var window: Long = 0

    init {
        // NOTE: set default stack size - 128 kb!!!
        Configuration.STACK_SIZE.set(128 * 1024)

        allocator = GLFWAllocator.calloc()
            .allocate(GLFWAllocateCallbackI { size: Long, user: Long -> nmemAllocChecked(size) })
            .reallocate(GLFWReallocateCallbackI { block: Long, size: Long, user: Long ->
                nmemReallocChecked(
                    block,
                    size
                )
            })
            .deallocate(GLFWDeallocateCallbackI { block: Long, user: Long -> nmemFree(block) })

        glfwInitAllocator(allocator)

        GLFWErrorCallback.createPrint().set()

        if (!glfwInit()) {
            println("Unable to initialize glfw")
        }

        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        val WIDTH = 800
        val HEIGHT = 600

        window = glfwCreateWindow(WIDTH, HEIGHT, "ktire", NULL, NULL)

        if (window == NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        glfwMakeContextCurrent(window);

        createCapabilities();

        glfwSetWindowSizeLimits(window, WIDTH, HEIGHT, GLFW_DONT_CARE, GLFW_DONT_CARE)

        //glfwSetWindowAspectRatio(window, 1, 1);
        val monitor = glfwGetPrimaryMonitor()

        val vidmode: GLFWVidMode? = glfwGetVideoMode(monitor)

        glfwSetWindowPos(
            window,
            (vidmode!!.width() - WIDTH) / 2,
            (vidmode.height() - HEIGHT) / 2
        )

        glfwSetKeyCallback(window, { window, key, scancode, action, mods ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(
                window,
                true
            ) // We will detect this in the rendering loop
        })

        glfwShowWindow(window)
    }

    fun loop() {

        glViewport(0, 0, 800, 600)
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f)

        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents()

            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        }
    }

    fun destroy() {
        if (window != NULL) {
            glfwDestroyWindow(window)
        }

        glfwTerminate()
    }
}
