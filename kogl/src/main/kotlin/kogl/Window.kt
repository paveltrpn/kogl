package kogl

import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.*

import config.*
import render.*
import events.*
import userinterface.*
import graph.*
import image.*
import map.*

// ============================================================================
// ======================= Window =============================================
// ============================================================================
class Window {
    private var _allocator: GLFWAllocator? = null

    private var _window: Long = 0

    private val _render: Render
    private val _userinterface: Userinterface

    val isMaximized: Boolean
        get() {
            return glfwGetWindowAttrib(_window, GLFW_MAXIMIZED) == GLFW_TRUE
        }

    val isIconified: Boolean
        get() {
            return glfwGetWindowAttrib(_window, GLFW_ICONIFIED) == GLFW_TRUE
        }

    init {
        // Place to init all singletons.
        Config.init()

        val inititalWindowWidth = Config.instance().konfig.window_width
        val inititalWindowHeight = Config.instance().konfig.window_height

        val basePath = Config.instance().basePath
        Storage.init(basePath)

        println("Base path is ${basePath}.")

        // OpenGL init.
        // NOTE: set default stack size - 128 kb!!!
        Configuration.STACK_SIZE.set(128 * 1024)

        _allocator = GLFWAllocator.calloc()
            .allocate(GLFWAllocateCallbackI { size: Long, user: Long -> MemoryUtil.nmemAllocChecked(size) })
            .reallocate(GLFWReallocateCallbackI { block: Long, size: Long, user: Long ->
                MemoryUtil.nmemReallocChecked(
                    block,
                    size
                )
            })
            .deallocate(GLFWDeallocateCallbackI { block: Long, user: Long -> MemoryUtil.nmemFree(block) })

        glfwInitAllocator(_allocator)

        GLFWErrorCallback.createPrint().set()

        if (!glfwInit()) {
            throw RuntimeException("Unable to initialize GLFW.")
        }

        if (Config.instance().konfig.resizeable) {
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        } else {
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
        }

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

        _window = glfwCreateWindow(
            inititalWindowWidth,
            inititalWindowHeight,
            Config.instance().konfig.application_name,
            MemoryUtil.NULL,
            MemoryUtil.NULL
        )

        if (_window == MemoryUtil.NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        glfwMakeContextCurrent(_window)

        createCapabilities()

        //glfwSetWindowAspectRatio(window, 1, 1)

        glfwSetWindowPos(
            _window,
            Config.instance().konfig.window_pos_x,
            Config.instance().konfig.window_pos_y
        )

        // Force window resize at startup.
        glfwSetWindowSize(
            _window, inititalWindowWidth,
            inititalWindowHeight
        )

        glfwSetKeyCallback(_window, { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            if (action == GLFW_PRESS) {
                val keyEvent = WindowKeyPressEvent(key)
                _userinterface.notify(keyEvent)

                when (key) {
                    GLFW_KEY_G -> {
                        _userinterface.mHoldMouse = !_userinterface.mHoldMouse

                        if (_userinterface.mHoldMouse) {
                            glfwSetInputMode(_window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN)
                        } else {
                            glfwSetInputMode(_window, GLFW_CURSOR, GLFW_CURSOR_NORMAL)
                        }
                    }

                    GLFW_KEY_ESCAPE -> {
                        glfwSetWindowShouldClose(window, true)
                    }
                }
            }

            if (action == GLFW_RELEASE) {
                val keyEvent = WindowKeyReleaseEvent(key)
                _userinterface.notify(keyEvent)
            }
        })

        glfwSetCursorPosCallback(_window, { window: Long, xpos: Double, ypos: Double ->
            if (!_userinterface.mHoldMouse) {
                val mouseEvent = WindowMouseMoveEvent(xpos.toInt(), ypos.toInt())
                _userinterface.notify(mouseEvent)
            } else {
                val offsetX = _userinterface.windowWidth / 2 - xpos.toInt()
                val offsetY = _userinterface.windowHeight / 2 - ypos.toInt()

                val mouseEvent = WindowMouseOffsetEvent(offsetX, offsetY)
                _userinterface.notify(mouseEvent)
            }
        })

        glfwSetWindowPosCallback(_window, { window: Long, xpos: Int, ypos: Int ->
            val posEvent = WindowMoveEvent(xpos, ypos)
            _userinterface.notify(posEvent)

            _userinterface.windowPosX = xpos
            _userinterface.mWindowPosY = ypos

            Config.instance().updatePosition(xpos, ypos)
        })

        glfwSetWindowSizeCallback(_window, { window: Long, width: Int, height: Int ->
            if (width > 0 && height > 0) {
                val sizeEvent = WindowResizeEvent(width, height)
                _userinterface.notify(sizeEvent)

                glViewport(0, 0, width, height)

                _userinterface.windowWidth = width
                _userinterface.windowHeight = height

                Config.instance().updateSize(width, height)
            }
        })

        glfwSetMouseButtonCallback(_window, { window: Long, button: Int, action: Int, mods: Int ->
            if (action == GLFW_PRESS) {
                val mouseEvent = WindowMousePress(button)
                _userinterface.notify(mouseEvent)
            }

            if (action == GLFW_RELEASE) {
                val mouseEvent = WindowMouseRelease(button)
                _userinterface.notify(mouseEvent)
            }
        })

        glfwShowWindow(_window)

        if (Config.instance().konfig.maximized) {
            glfwMaximizeWindow(_window)
        }

        // Enable debug.
        println("Debug output ENABLED! Disable it in ${this::class.constructors.first()}")
        gldebug.enableDebugContext(gldebug.GLDebugDiscardNotification())

        // Create userinterface object. GL context is current at this point.
        _userinterface = Userinterface()
        _userinterface.windowWidth = inititalWindowWidth
        _userinterface.windowHeight = inititalWindowHeight
        _userinterface.windowPosX = Config.instance().konfig.window_pos_x
        _userinterface.mWindowPosY = Config.instance().konfig.window_pos_y

        // Render init.
        _render = Render(_userinterface.windowWidth, _userinterface.windowHeight)

        _userinterface.attach(_render)

        with(_render) {
            mUi = _userinterface.mUi

            val mainScene = Scene()
            with(mainScene) {
                val m = kogl.maps.m04()
                addLocales(m)
            }

            scene = mainScene
        }
    }

    fun run() {
        val cc = Color(Config.instance().konfig.background_color)

        glViewport(0, 0, _userinterface.windowWidth, _userinterface.windowHeight)
        glClearColor(cc.rf, cc.gf, cc.bf, 1.0f)

        _render.preLoop()

        while (!glfwWindowShouldClose(_window)) {
            if (_userinterface.mHoldMouse) {
                glfwSetCursorPos(_window, _userinterface.windowWidth / 2.0, _userinterface.windowHeight / 2.0)
            }

            glfwPollEvents()

            _userinterface.update()

            _render.frame()

            glfwSwapBuffers(_window)
        }

        _render.postLoop()

        // === Destroy === //
        if (_window != MemoryUtil.NULL) {
            Config.instance().updateMaximized(isMaximized)

            if (!isMaximized && !isIconified) {
                val width = IntArray(1)
                val height = IntArray(1)
                glfwGetWindowSize(_window, width, height)

                if (width[0] > 0 && height[0] > 0) {
                    Config.instance().updateSize(width[0], height[0])
                }

                val posX = IntArray(1)
                val posY = IntArray(1)
                glfwGetWindowPos(_window, posX, posY)

                Config.instance().updatePosition(posX[0], posY[0])
            }
        }

        Config.instance().save()

        _userinterface.free()

        glfwDestroyWindow(_window)

        glfwTerminate()
    }
}
