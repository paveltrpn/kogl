package kogl

import java.io.File

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
import event.*
import graph.*
import graphdsl.*
import image.*
import map.*

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

        val basePath = Config.instance().basePath
        Storage.init(basePath)

        println("base path is ${basePath}")

        _width = Config.instance().konfig.window_width
        _height = Config.instance().konfig.window_height

        //
        initGLFW()

        val cc = Color(Config.instance().konfig.background_color)

        //
        glViewport(0, 0, _width, _height)
        glClearColor(cc.rf, cc.gf, cc.bf, 1.0f)

        //
        _render = Render()
        GlobalEventEmitter.instance().attach(_render)

        val file = File("${basePath}/assets/m01.json")
        val jsonString = file.readText()
        val mapData = parseMapJson(jsonString)

//        printMapStructure(mapData)

        val sg = buildStateGroups(mapData, Storage.instance().bodyStorage)


//        class PrintTypeVisitor : Visitor {
//            override fun apply(node: StateGroup): Unit {
//                println("my type is ${node::class}")
//                node.traverse(this)
//            }
//
//            override fun apply(node: Transform): Unit {
//                println("my type is ${node::class}")
//                node.traverse(this)
//            }
//
//            override fun apply(node: Drawable): Unit {
//                println("my type is ${node::class}")
//                node.traverse(this)
//            }
//        }

//        val printtype = PrintTypeVisitor()
//        sparseObjectsGraph.accept(printtype)


        with(_render) {
            scene = Scene().apply {
                val (dimonds, frames, arches) = testgraph.sparseObjectsGraph()

                val dimondsLoc = buildLocale {
                    origin = Vector3(-8.0f, 0.0f, 0.0f)
                    this attach dimonds
                }

                val framesLoc = Locale(Vector3(0.0f, 0.0f, 0.0f)).apply {
                    addStateGroup(frames)
                }

                val archesLoc = Locale(Vector3(8.0f, 0.0f, 0.0f)).apply {
                    addStateGroup(arches)
                }

//            addStateGroup(testgraph.testCubesGraph())
//            addStateGroup(testgraph.testFlyaroundsGraph())

                addLocales(listOf(dimondsLoc, framesLoc, archesLoc))

                val m01Loc = Locale(Vector3(0.0f, 0.0f, 10.0f))
                m01Loc.addStateGroups(sg)
                addLocale(m01Loc)
            }
        }
    }

    fun run() {
        _render.preLoop()

        while (_render.run) {
            glfwSetCursorPos(_window, 500.0, 500.0)

            glfwPollEvents()

            _render.frame()

            glfwSwapBuffers(_window)
        }

        _render.postLoop()
    }

    fun destroy() {
        if (_window != MemoryUtil.NULL) {
            glfwDestroyWindow(_window)
        }

        glfwTerminate()
    }

    private fun initGLFW() {
        // NOTE: set default stack size - 128 kb!!!
        Configuration.STACK_SIZE.set(128 * 1024)

        allocator = GLFWAllocator.calloc()
            .allocate(GLFWAllocateCallbackI { size: Long, user: Long -> MemoryUtil.nmemAllocChecked(size) })
            .reallocate(GLFWReallocateCallbackI { block: Long, size: Long, user: Long ->
                MemoryUtil.nmemReallocChecked(
                    block,
                    size
                )
            })
            .deallocate(GLFWDeallocateCallbackI { block: Long, user: Long -> MemoryUtil.nmemFree(block) })

        glfwInitAllocator(allocator)

        GLFWErrorCallback.createPrint().set()

        if (!glfwInit()) {
            throw RuntimeException("Unable to initialize GLFW.")
        }

        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
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

        glfwSetWindowSizeLimits(_window, _width, _height, GLFW_DONT_CARE, GLFW_DONT_CARE)

        //glfwSetWindowAspectRatio(window, 1, 1)
        val monitor = glfwGetPrimaryMonitor()

        val vidmode: GLFWVidMode = glfwGetVideoMode(monitor) ?: throw RuntimeException("VideoMode is undefined!")

        // val posx = Config.instance().konfig.window_pos_x
        // val posy = Config.instance().konfig.window_pos_y

        glfwSetWindowPos(
            _window,
            (vidmode.width() - _width) / 2,
            (vidmode.height() - _height) / 2
        )

        glfwSetWindowPos(
            _window,
            100, 100
        )

        glfwSetInputMode(_window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN)

        glfwSetKeyCallback(_window, { window, key, scancode, action, mods ->
            if (action == GLFW_PRESS) {
                val event = EventKey(KeyAction.PRESS, key)
                GlobalEventEmitter.instance().notify(event)
            }

            if (action == GLFW_RELEASE) {
                val event = EventKey(KeyAction.RELEASE, key)
                GlobalEventEmitter.instance().notify(event)
            }
        })

        glfwSetCursorPosCallback(_window, { window, xpos, ypos ->
            val xoffset = 500.0 - xpos
            val yoffset = 500.0 - ypos

            val event = EventMouse(0.0, 0.0, xoffset, yoffset, KeyAction.RELEASE, 0)
            GlobalEventEmitter.instance().notify(event)
        })

        glfwShowWindow(_window)
    }
}
