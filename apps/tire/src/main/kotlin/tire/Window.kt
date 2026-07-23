package tire

import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.Configuration
import org.lwjgl.system.MemoryUtil.*

class Window {
    private var allocator: GLFWAllocator? = null

    private var _window: Long = 0

    private val _width: Int = 1200
    private val _height: Int = 800

    private val _render: Render

    init {
        _render = Render()

        initGLFW()
    }

    fun run() {
        _render.preLoop()

        while (_render.run) {
            glfwPollEvents()

            _render.frame()

            glfwSwapBuffers(_window)
        }

        _render.postLoop()
    }

    fun destroy() {
        if (_window != NULL) {
            glfwDestroyWindow(_window)
        }

        glfwTerminate()
    }

    private fun initGLFW() {
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

        _window = glfwCreateWindow(_width, _height, "kogl", NULL, NULL)

        if (_window == NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        glfwMakeContextCurrent(_window);

        createCapabilities();

        glfwSetWindowSizeLimits(_window, _width, _height, GLFW_DONT_CARE, GLFW_DONT_CARE)

        //glfwSetWindowAspectRatio(window, 1, 1);
        val monitor = glfwGetPrimaryMonitor()

        val vidmode: GLFWVidMode? = glfwGetVideoMode(monitor)

        glfwSetWindowPos(
            _window,
            (vidmode!!.width() - _width) / 2,
            (vidmode.height() - _height) / 2
        )

        glfwSetKeyCallback(_window, { window, key, scancode, action, mods ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(
                window,
                true
            ) // We will detect this in the rendering loop
        })

        glfwShowWindow(_window)
    }
}
