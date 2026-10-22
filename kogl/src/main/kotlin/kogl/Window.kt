package kogl

import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.system.Configuration
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.opengl.GL11.glViewport
import org.lwjgl.system.MemoryUtil

import algebra.*
import config.*
import render.*
import events.*
import userinterface.*
import graph.*
import graphdsl.*
import image.*
import map.*

class Window {
    private var mAllocator: GLFWAllocator? = null

    private var _window: Long = 0

    private var _width: Int
    private var _height: Int

    private val mRender: Render
    private val mUserinterface: Userinterface

    init {
        // Place to init all singletons.
        Config.init()

        val basePath = Config.instance().basePath
        Storage.init(basePath)

        println("Base path is ${basePath}.")

        _width = Config.instance().konfig.window_width
        _height = Config.instance().konfig.window_height

        // OpenGL init.
        initGLFW()

        // Enable debug.
        println("Debug output ENABLED! Disable it in ${this::class.constructors.first()}")
        gldebug.enableDebugContext(gldebug.GLDebugDiscardNotification())

        val cc = Color(Config.instance().konfig.background_color)

        glViewport(0, 0, _width, _height)
        glClearColor(cc.rf, cc.gf, cc.bf, 1.0f)

        // Create userinterface object. GL context is current at this point.
        mUserinterface = Userinterface()
        mUserinterface.mWindowWidth = _width
        mUserinterface.mWindowHeight = _height
        mUserinterface.mWindowPosX = Config.instance().konfig.window_pos_x
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

        val actualWidth = IntArray(1)
        val actualHeight = IntArray(1)
        glfwGetWindowSize(_window, actualWidth, actualHeight)

        if (actualWidth[0] > 0 && actualHeight[0] > 0) {
            _width = actualWidth[0]
            _height = actualHeight[0]

            mRender.scene.camera.aspect = _width.toFloat() / _height.toFloat()

            mUserinterface.mWindowWidth = _width
            mUserinterface.mWindowHeight = _height
        }
    }

    fun run() {
        mRender.preLoop()

        while (!glfwWindowShouldClose(_window)) {
            if (mUserinterface.mHoldMouse) {
                glfwSetCursorPos(_window, _width / 2.0, _height / 2.0)
            }

            glfwPollEvents()

            mUserinterface.update()

            mRender.frame()

            glfwSwapBuffers(_window)
        }

        mRender.postLoop()
    }

    fun destroy() {
        if (_window != MemoryUtil.NULL) {
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

        Config.instance().save()

        mUserinterface.free()

        if (_window != MemoryUtil.NULL) {
            glfwDestroyWindow(_window)
        }

        glfwTerminate()
    }

    private fun initGLFW() {
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
            _width,
            _height,
            Config.instance().konfig.application_name,
            MemoryUtil.NULL,
            MemoryUtil.NULL
        )

        if (_window == MemoryUtil.NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        glfwMakeContextCurrent(_window)

        createCapabilities()

        if (Config.instance().konfig.resizeable) {
            glfwSetWindowSizeLimits(_window, 640, 480, GLFW_DONT_CARE, GLFW_DONT_CARE)
        } else {
            glfwSetWindowSizeLimits(_window, _width, _height, GLFW_DONT_CARE, GLFW_DONT_CARE)
        }

        //glfwSetWindowAspectRatio(window, 1, 1)

        glfwSetWindowPos(
            _window,
            Config.instance().konfig.window_pos_x,
            Config.instance().konfig.window_pos_y
        )

        glfwSetKeyCallback(_window, { window, key, scancode, action, mods ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true)
            }

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
                val offsetX = _width / 2 - xpos.toInt()
                val offsetY = _height / 2 - ypos.toInt()

                val mouseEvent = WindowMouseOffsetEvent(offsetX, offsetY)
                mUserinterface.notify(mouseEvent)
            }
        })

        glfwSetWindowPosCallback(_window, { window, xpos, ypos ->
            mUserinterface.mWindowPosX = xpos
            mUserinterface.mWindowPosY = ypos

            Config.instance().updatePosition(xpos, ypos)
        })

        glfwSetWindowSizeCallback(_window, { window, width, height ->
            if (width > 0 && height > 0) {
                _width = width
                _height = height

                glViewport(0, 0, width, height)

                mRender.scene.camera.aspect = width.toFloat() / height.toFloat()

                mUserinterface.mWindowWidth = width
                mUserinterface.mWindowHeight = height

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
    }
}
