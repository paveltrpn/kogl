package tire

import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryUtil.*

import context.*
import render.*

import org.lwjgl.system.Configuration;

class Window {
    private var allocator: GLFWAllocator? = null
    private var window: Long = 0

    private val context: Context
    private val render: Render

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

        // glfwDefaultWindowHints()
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API)

        val WIDTH = 800
        val HEIGHT = 600

        window = glfwCreateWindow(WIDTH, HEIGHT, "another tire", NULL, NULL)
        if (window == NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        context = Context(window)
        render = Render()

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
        render.preLoop()

        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents()
            render.frame()
        }

        render.postLoop()

        destroy()
    }

    fun destroy() {
        // Free structures, allocated in render.
        render.free()

        // Free structures, allocated in context.
        context.free()

        if (window != NULL) {
            glfwDestroyWindow(window)
        }

        glfwTerminate()

//        Objects.requireNonNull<T?>(glfwSetErrorCallback(null)).free()

//        allocator!!.deallocate().free()
//        allocator!!.reallocate().free()
//        allocator!!.allocate().free()
//        allocator!!.free()
    }
}
