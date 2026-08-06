package kogl

import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.system.Configuration
import org.lwjgl.system.MemoryUtil.*

import config.*
import render.*
import event.*
import mesh.*
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.opengl.GL11.glViewport

class Window {
    private var allocator: GLFWAllocator? = null

    private var _window: Long = 0

    private val _width: Int
    private val _height: Int

    private val _render: Render

    init {
        // Place to init all singletons.
        Config.init()
        GlobalEventEmitter.init()
        BodyStorage.init("")

        println("Base path is ${Config.instance().basePath}")

        _width = Config.instance().konfig.window_width
        _height = Config.instance().konfig.window_height

        //
        initGLFW()

        //
        glViewport(0, 0, _width, _height)
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f)

        //
        _render = Render()
        GlobalEventEmitter.instance().attach(_render)
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
            throw RuntimeException("Unable to initialize GLFW.")
        }

        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        _window = glfwCreateWindow(_width, _height, Config.instance().konfig.application_name, NULL, NULL)

        if (_window == NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        glfwMakeContextCurrent(_window);

        createCapabilities();

        glfwSetWindowSizeLimits(_window, _width, _height, GLFW_DONT_CARE, GLFW_DONT_CARE)

        //glfwSetWindowAspectRatio(window, 1, 1);
        val monitor = glfwGetPrimaryMonitor()

        val vidmode: GLFWVidMode = glfwGetVideoMode(monitor) ?: throw RuntimeException("VideoMode is undefined!")

        // val posx = Config.instance().konfig.window_pos_x
        // val posy = Config.instance().konfig.window_pos_y

        glfwSetWindowPos(
            _window,
            (vidmode.width() - _width) / 2,
            (vidmode.height() - _height) / 2
        )

        glfwSetKeyCallback(_window, { window, key, scancode, action, mods ->
            if (key == GLFW_KEY_ESCAPE) {
                val event = EventKey(KeyAction.PRESS, key)
                GlobalEventEmitter.instance().notify(event)
            }

            if (key == GLFW_RELEASE) {
                val event = EventKey(KeyAction.RELEASE, key)
                GlobalEventEmitter.instance().notify(event)
            }
        })

        glfwShowWindow(_window)
    }
}
