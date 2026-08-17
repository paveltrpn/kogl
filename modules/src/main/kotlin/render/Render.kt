package render

import java.io.File

import org.lwjgl.opengl.GL46.*

import algebra.*
import config.*
import event.*
import graph.*
import scene.*
import mesh.*
import ui.*
import image.*
import map.*
import testgraph.*

class Render : EventObserver {
    private var _run: Boolean
    private val _scene: Scene
    private val _ui: UiGL

    init {
        _run = true

        val file = File("/mnt/main/code/kogl/kogl/assets/m01.json")
        val jsonString = file.readText()
        val mapData = parseMapJson(jsonString)

//        printMapStructure(mapData)

        val sg = buildStateGroups(mapData, Storage.instance().bodyStorage)

        _scene = Scene().apply {
//            addStateGroup(testgraph.sparseObjectsGraph())
//            addStateGroup(testgraph.testCubesGraph())
//            addStateGroup(testgraph.testFlyaroundsGraph())
            addStateGroup(sg[0])
            addStateGroup(sg[1])
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

        _scene.walk()

        glDisable(GL_DEPTH_TEST)

        val TEXT_START_POS_X = -30.0f
        val TEXT_START_POS_Y = 26.0f
        val TEXT_PLANE_Z = 0.4f

        val l1 = Label().apply {
            text = "test string"
            position = Vector2(TEXT_START_POS_X + 0.0f, TEXT_START_POS_Y)
            z = TEXT_PLANE_Z - 0.1f
            letterSpace = 0.0f
            color = Color(255, 255, 255, 255)
        }

        val l2 = Label().apply {
            text = "test string"
            position = Vector2(TEXT_START_POS_X + 0.0f, TEXT_START_POS_Y + 3.5f)
            z = TEXT_PLANE_Z - 0.1f
            letterSpace = 0.0f
            letterScale = 1.5f
            color = Color(128, 255, 128, 255)
        }

        val b1 = Billboard().apply {
            size = Vector2(10.0f, 10.0f)
            position = Vector2(-31.0f, 16.0f)
            z = TEXT_PLANE_Z
            color = Color(255, 200, 128, 128)
        }

        val b2 = Billboard().apply {
            size = Vector2(10.0f, 10.0f)
            position = Vector2(-31.0f, 30.0f)
            z = TEXT_PLANE_Z
            color = Color(128, 200, 255, 128)
        }

        _ui.add(listOf(l1, l2, b1, b2))

        _ui.flush()
    }

    fun postLoop(): Unit {

    }

    override fun handleEvent(event: EventBase) {
        if (event is EventKey) {
            if (event.keyAction == KeyAction.PRESS) {
                //println("=== ${event.key}")
                when (event.key) {
                    256 -> {
                        run = false
                    }

                    // w
                    87 -> {
                        _scene._camera.setMoveBit(FlycamMoveBits.FORWARD)
                    }

                    // a
                    65 -> {
                        _scene._camera.setMoveBit(FlycamMoveBits.LEFT)
                    }

                    // s
                    83 -> {
                        _scene._camera.setMoveBit(FlycamMoveBits.BACKWARD)
                    }

                    // d
                    68 -> {
                        _scene._camera.setMoveBit(FlycamMoveBits.RIGHT)
                    }
                }
            }

            if (event.keyAction == KeyAction.RELEASE) {
                //println("=== ${event.key}")
                when (event.key) {
                    87 -> {
                        _scene._camera.unsetMoveBit(FlycamMoveBits.FORWARD)
                    }

                    65 -> {
                        _scene._camera.unsetMoveBit(FlycamMoveBits.LEFT)
                    }

                    83 -> {
                        _scene._camera.unsetMoveBit(FlycamMoveBits.BACKWARD)
                    }

                    68 -> {
                        _scene._camera.unsetMoveBit(FlycamMoveBits.RIGHT)
                    }
                }
            }
        }
    }
}
