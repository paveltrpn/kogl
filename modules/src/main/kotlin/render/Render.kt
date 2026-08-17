package render

import java.io.File
import java.text.DecimalFormat
import java.math.RoundingMode

import org.lwjgl.opengl.GL46.*

import algebra.*
import config.Config
import event.*
import scene.*
import ui.*
import image.*
import map.*

class Render : EventObserver {
    private var _run: Boolean
    private val _scene: Scene
    private val _ui: UiGL

    init {
        _run = true

        val basePath = Config.instance().basePath

        val file = File("${basePath}/assets/m01.json")
        val jsonString = file.readText()
        val mapData = parseMapJson(jsonString)

//        printMapStructure(mapData)

        val sg = buildStateGroups(mapData, Storage.instance().bodyStorage)

        _scene = Scene().apply {
            addStateGroup(testgraph.sparseObjectsGraph())
//            addStateGroup(testgraph.testCubesGraph())
            // addStateGroup(testgraph.testFlyaroundsGraph())
            addStateGroup(sg[0])
            // addStateGroup(sg[1])
        }

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


        _ui = UiGL()
    }

    var run: Boolean
        get(): Boolean {
            return _run
        }
        set(value) {
            _run = value
        }

    fun preLoop(): Unit {

    }

    fun frame(): Unit {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LESS)

        _scene.walk2()

        glDisable(GL_DEPTH_TEST)

        val TEXT_START_POS_X = -30.0f
        val TEXT_START_POS_Y = 30.0f
        val TEXT_PLANE_Z = 0.4f

        val l1 = Label().apply {
            val df = DecimalFormat("0.000").apply {
                // Truncates instead of rounding up.
                roundingMode = RoundingMode.DOWN
            }

            val cx = df.format(_scene.camera.eye.x)
            val cy = df.format(_scene.camera.eye.y)
            val cz = df.format(_scene.camera.eye.z)
            val az = df.format(_scene.camera.azimuth)
            val el = df.format(_scene.camera.elevation)

            text = "pos: ${cx} ${cy} ${cz} ${az} ${el}"

            position = Vector2(TEXT_START_POS_X + 0.0f, TEXT_START_POS_Y)
            z = TEXT_PLANE_Z - 0.1f
            letterSpace = 0.0f
            letterScale = 1.0f
            color = Color(255, 255, 255, 255)
        }

        val b1 = Billboard().apply {
            size = Vector2(18.0f, 2.5f)
            position = Vector2(-30.5f, 30.5f)
            z = TEXT_PLANE_Z
            color = Color(255, 200, 128, 128)
        }

        _ui.add(listOf(l1, b1))

        _ui.flush()
    }

    fun postLoop(): Unit {

    }

    override fun handleEvent(event: EventBase) {
        if (event is EventKey) {
            if (event.keyAction == KeyAction.PRESS) {
//                println("=== ${event.key}")
                when (event.key) {
                    256 -> {
                        run = false
                    }

                    // w
                    87 -> {
                        _scene.camera.setMoveBit(FlycamMoveBits.FORWARD)
                    }

                    // a
                    65 -> {
                        _scene.camera.setMoveBit(FlycamMoveBits.LEFT)
                    }

                    // s
                    83 -> {
                        _scene.camera.setMoveBit(FlycamMoveBits.BACKWARD)
                    }

                    // d
                    68 -> {
                        _scene.camera.setMoveBit(FlycamMoveBits.RIGHT)
                    }

                    // c
                    67 -> {

                    }

                    // g
                    71 -> {

                    }
                }
            }

            if (event.keyAction == KeyAction.RELEASE) {
                when (event.key) {
                    87 -> {
                        _scene.camera.unsetMoveBit(FlycamMoveBits.FORWARD)
                    }

                    65 -> {
                        _scene.camera.unsetMoveBit(FlycamMoveBits.LEFT)
                    }

                    83 -> {
                        _scene.camera.unsetMoveBit(FlycamMoveBits.BACKWARD)
                    }

                    68 -> {
                        _scene.camera.unsetMoveBit(FlycamMoveBits.RIGHT)
                    }
                }
            }
        }

        if (event is EventMouse) {
            val SENSIVITY = 0.005f
            _scene.camera.rotate(event.xoffst.toFloat() * SENSIVITY, event.yoffst.toFloat() * SENSIVITY)
        }
    }
}
