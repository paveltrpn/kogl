package render

import java.text.DecimalFormat
import java.math.RoundingMode

import org.lwjgl.opengl.GL46.*

import algebra.*
import event.*
import graph.*
import ui.*
import image.*

class Render : EventObserver {
    private var _run: Boolean = true
    private val _ui: UiGL = UiGL()

    private var _scene: Scene? = null

    var scene: Scene
        get(): Scene {
            return _scene ?: throw RuntimeException("Scene is null! Nothing to return!")
        }
        set(value) {
            _scene = value
        }

    var run: Boolean
        get(): Boolean {
            return _run
        }
        set(value) {
            _run = value
        }

    fun preLoop(): Unit {
        if (_scene == null) {
            throw RuntimeException("Scene is null! Nothing to render!")
        }
    }

    fun frame(): Unit {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LESS)

        scene.walk()

        glDisable(GL_DEPTH_TEST)

        val TEXT_START_POS_X = -30.0f
        val TEXT_START_POS_Y = 30.0f
        val TEXT_PLANE_Z = 0.4f

        val l1 = Label().apply {
            val df = DecimalFormat("0.000").apply {
                // Truncates instead of rounding up.
                roundingMode = RoundingMode.DOWN
            }

            val cx = df.format(scene.camera.eye.x)
            val cy = df.format(scene.camera.eye.y)
            val cz = df.format(scene.camera.eye.z)
            val az = df.format(scene.camera.azimuth)
            val el = df.format(scene.camera.elevation)

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
                        scene.camera.setMoveBit(FlycamMoveBits.FORWARD)
                    }

                    // a
                    65 -> {
                        scene.camera.setMoveBit(FlycamMoveBits.LEFT)
                    }

                    // s
                    83 -> {
                        scene.camera.setMoveBit(FlycamMoveBits.BACKWARD)
                    }

                    // d
                    68 -> {
                        scene.camera.setMoveBit(FlycamMoveBits.RIGHT)
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
                        scene.camera.unsetMoveBit(FlycamMoveBits.FORWARD)
                    }

                    65 -> {
                        scene.camera.unsetMoveBit(FlycamMoveBits.LEFT)
                    }

                    83 -> {
                        scene.camera.unsetMoveBit(FlycamMoveBits.BACKWARD)
                    }

                    68 -> {
                        scene.camera.unsetMoveBit(FlycamMoveBits.RIGHT)
                    }
                }
            }
        }

        if (event is EventMouse) {
            val SENSIVITY = 0.005f
            scene.camera.rotate(event.xoffst.toFloat() * SENSIVITY, event.yoffst.toFloat() * SENSIVITY)
        }
    }
}
