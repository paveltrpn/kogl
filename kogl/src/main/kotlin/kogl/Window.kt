package kogl

import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.system.Configuration
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.opengl.GL11.glViewport
import org.lwjgl.system.MemoryUtil

import config.*
import render.*
import events.*
import userinterface.*
import graph.*
import image.*
import map.*

class Window {
    private var mAllocator: GLFWAllocator? = null

    private var _window: Long = 0

    private val _fitSlackPx = 16

    private fun _isMaximized(): Boolean {
        return glfwGetWindowAttrib(_window, GLFW_MAXIMIZED) == GLFW_TRUE
    }

    private fun _isIconified(): Boolean {
        return glfwGetWindowAttrib(_window, GLFW_ICONIFIED) == GLFW_TRUE
    }

    private val mRender: Render
    private val mUserinterface: Userinterface

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

        mAllocator = GLFWAllocator.calloc()
            .allocate(GLFWAllocateCallbackI { size: Long, user: Long -> MemoryUtil.nmemAllocChecked(size) })
            .reallocate(GLFWReallocateCallbackI { block: Long, size: Long, user: Long ->
                MemoryUtil.nmemReallocChecked(
                    block,
                    size
                )
            })
            .deallocate(GLFWDeallocateCallbackI { block: Long, user: Long -> MemoryUtil.nmemFree(block) })

        glfwInitAllocator(mAllocator)

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

        glfwSetKeyCallback(_window, { window, key, scancode, action, mods ->
            if (action == GLFW_PRESS) {
                val keyEvent = WindowKeyPressEvent(key)
                mUserinterface.notify(keyEvent)

                when (key) {
                    GLFW_KEY_G -> {
                        mUserinterface.mHoldMouse = !mUserinterface.mHoldMouse

                        if (mUserinterface.mHoldMouse) {
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
                mUserinterface.notify(keyEvent)
            }
        })

        glfwSetCursorPosCallback(_window, { window, xpos, ypos ->
            if (!mUserinterface.mHoldMouse) {
                val mouseEvent = WindowMouseMoveEvent(xpos.toInt(), ypos.toInt())
                mUserinterface.notify(mouseEvent)
            } else {
                val offsetX = mUserinterface.windowWidth / 2 - xpos.toInt()
                val offsetY = mUserinterface.windowHeight / 2 - ypos.toInt()

                val mouseEvent = WindowMouseOffsetEvent(offsetX, offsetY)
                mUserinterface.notify(mouseEvent)
            }
        })

        glfwSetWindowPosCallback(_window, { window, xpos, ypos ->
            val posEvent = WindowMoveEvent(xpos, ypos)
            mUserinterface.notify(posEvent)

            mUserinterface.windowPosX = xpos
            mUserinterface.mWindowPosY = ypos

            Config.instance().updatePosition(xpos, ypos)
        })

        glfwSetWindowSizeCallback(_window, { window, width, height ->
            if (width > 0 && height > 0) {
                val sizeEvent = WindowResizeEvent(width, height)
                mUserinterface.notify(sizeEvent)

                glViewport(0, 0, width, height)

                mUserinterface.windowWidth = width
                mUserinterface.windowHeight = height

                Config.instance().updateSize(width, height)
            }
        })

        glfwSetMouseButtonCallback(_window, { window, button, action, mods ->
            if (action == GLFW_PRESS) {
                val mouseEvent = WindowMousePress(button)
                mUserinterface.notify(mouseEvent)
            }

            if (action == GLFW_RELEASE) {
                val mouseEvent = WindowMouseRelease(button)
                mUserinterface.notify(mouseEvent)
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
        mUserinterface = Userinterface()
        mUserinterface.windowWidth = Config.instance().konfig.window_width
        mUserinterface.windowHeight = Config.instance().konfig.window_height
        mUserinterface.windowPosX = Config.instance().konfig.window_pos_x
        mUserinterface.mWindowPosY = Config.instance().konfig.window_pos_y

        // Main scene init.
        val mainScene = Scene()

//        with(mainScene) {
//            val file = File("${basePath}/assets/m01.json")
//            val jsonString = file.readText()
//            val mapData = parseMapJson(jsonString)
//            val sg = buildStateGroups(mapData, Storage.instance().bodyStorage)
//
//            val m01Loc = Locale(Vector3(0.0f, 0.0f, 10.0f))
//            m01Loc.addStateGroups(sg)
//            addLocale(m01Loc)
//        }

        with(mainScene) {
            val m = kogl.maps.m04()
            addLocales(m)
        }

//        with(mainScene) {
//            camera = Flycam()
//        }

        // Render init.
        mRender = Render()

        mUserinterface.attach(mRender)

        mRender.mUi = mUserinterface.mUi

        with(mRender) {
            scene = mainScene
        }
    }

    fun run() {
        val cc = Color(Config.instance().konfig.background_color)

        glViewport(0, 0, mUserinterface.windowWidth, mUserinterface.windowHeight)
        glClearColor(cc.rf, cc.gf, cc.bf, 1.0f)

        mRender.preLoop()

        while (!glfwWindowShouldClose(_window)) {
            if (mUserinterface.mHoldMouse) {
                glfwSetCursorPos(_window, mUserinterface.windowWidth / 2.0, mUserinterface.windowHeight / 2.0)
            }

            glfwPollEvents()

            mUserinterface.update()

            mRender.frame()

            glfwSwapBuffers(_window)
        }

        mRender.postLoop()

        // === Destroy ===
        if (_window != MemoryUtil.NULL) {
            Config.instance().updateMaximized(_isMaximized())

            if (!_isMaximized() && !_isIconified()) {
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

        mUserinterface.free()

        if (_window != MemoryUtil.NULL) {
            glfwDestroyWindow(_window)
        }

        glfwTerminate()
    }
}
