package render

import java.text.*
import java.math.*

import org.lwjgl.opengl.GL46.*

import algebra.*
import events.*
import graph.*
import userinterface.*
import image.*

// ============================================================================
// ======================= Render =============================================
// ============================================================================

class Render : WindowEventObserver {
    private val _ui: UiGL = UiGL()

    private var _scene: Scene? = null

    private val _mouseSensitivity = 0.005f

    private lateinit var _framebuffer: Framebuffer
    private lateinit var _quadBuffer: QuadsBuffer
    private lateinit var _frameProgram: Program

    private var _effect = 0

    var mUi: UiDrawable? = null

    var scene: Scene
        get(): Scene {
            return _scene ?: throw RuntimeException("Scene is null! Nothing to return!")
        }
        set(value) {
            _scene = value
        }

    fun preLoop(): Unit {
        if (_scene == null) {
            throw RuntimeException("Scene is null! Nothing to render!")
        }

        val viewport = IntArray(4)
        glGetIntegerv(GL_VIEWPORT, viewport)

        _framebuffer = Framebuffer(viewport[2], viewport[3])

        _quadBuffer = QuadsBuffer(1)
        _quadBuffer.fill(
            0,
            Vector4(1.0f, 1.0f, 1.0f, 1.0f),
            floatArrayOf(
                -1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                -1.0f, 1.0f, 0.0f
            ),
            floatArrayOf(
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
            )
        )

        _frameProgram = Program().apply {
            source = ShaderSource("frameProgram")
            build()
            use()
            set("frameTexture" to 0)
            set("mouseFocusPoint" to Vector2(0.5f, 0.5f))
        }
    }

    fun frame(): Unit {
        _framebuffer.bind()

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LESS)

        scene.walk()

        glDisable(GL_DEPTH_TEST)

        _framebuffer.unbind()

        glClear(GL_COLOR_BUFFER_BIT)

        _frameProgram.use()
        _frameProgram.set("effect" to _effect)

        _framebuffer.bindTexture()

        _quadBuffer.draw()

        val TEXT_START_POS_X = 14.2f
        val TEXT_START_POS_Y = 31.0f
        val TEXT_LINE_WIDTH = 1.6f
        val TEXT_LETTER_SPACE = 0.0f
        val TEXT_LETTER_SCALE = 1.0f
        var textLineCount = 0
        val TEXT_PLANE_Z = 0.4f
        val TEXT_PADDING = 0.5f

        val cameraPosLabel = Label().apply {
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

            position = Vector2(TEXT_START_POS_X + 0.0f, TEXT_START_POS_Y - TEXT_LINE_WIDTH * textLineCount)
            z = TEXT_PLANE_Z - 0.1f
            letterSpace = TEXT_LETTER_SPACE
            letterScale = TEXT_LETTER_SCALE
            color = Color(255, 255, 255, 255)

            textLineCount++
        }

        val drawCallsLabel = Label().apply {
            text = "draw calls: ${scene.drawCallsCount}"

            position = Vector2(TEXT_START_POS_X + 0.0f, TEXT_START_POS_Y - TEXT_LINE_WIDTH * textLineCount)
            z = TEXT_PLANE_Z + 0.1f
            letterSpace = TEXT_LETTER_SPACE
            letterScale = TEXT_LETTER_SCALE
            color = Color(255, 255, 255, 255)

            textLineCount++
        }

        val gridToggleLable = Label().apply {
            text = "\"t\" - toggle grid visibility"

            position = Vector2(TEXT_START_POS_X + 0.0f, TEXT_START_POS_Y - TEXT_LINE_WIDTH * textLineCount)
            z = TEXT_PLANE_Z + 0.1f
            letterSpace = TEXT_LETTER_SPACE
            letterScale = TEXT_LETTER_SCALE
            color = Color(255, 255, 255, 255)

            textLineCount++
        }

        val postEffectLable = Label().apply {
            text = "\"p\" - cycle post effect"

            position = Vector2(TEXT_START_POS_X + 0.0f, TEXT_START_POS_Y - TEXT_LINE_WIDTH * textLineCount)
            z = TEXT_PLANE_Z + 0.1f
            letterSpace = TEXT_LETTER_SPACE
            letterScale = TEXT_LETTER_SCALE
            color = Color(255, 255, 255, 255)

            textLineCount++
        }

        val holdCameraToggleLable = Label().apply {
            text = "\"g\" - toggle hold camera"

            position = Vector2(TEXT_START_POS_X + 0.0f, TEXT_START_POS_Y - TEXT_LINE_WIDTH * textLineCount)
            z = TEXT_PLANE_Z + 0.1f
            letterSpace = TEXT_LETTER_SPACE
            letterScale = TEXT_LETTER_SCALE
            color = Color(255, 255, 255, 255)

            textLineCount++
        }

        val b1 = Billboard().apply {
            size = Vector2(18.0f, TEXT_LINE_WIDTH * textLineCount + TEXT_PADDING * 2.0f)
            position = Vector2(TEXT_START_POS_X - TEXT_PADDING, TEXT_START_POS_Y + TEXT_PADDING)
            z = TEXT_PLANE_Z
            color = Color(255, 200, 128, 64)
        }

        _ui.add(listOf(cameraPosLabel, drawCallsLabel, gridToggleLable, postEffectLable, holdCameraToggleLable, b1))

        _ui.flush()

        mUi?.draw()
    }

    fun postLoop(): Unit {
        _quadBuffer.clean()
        _framebuffer.clean()
        _frameProgram.clean()
    }

    override fun onWindowEvent(event: WindowEvent) {
        when (event) {
            is WindowKeyPressEvent -> {
                when (event.keyCode) {
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

                    // t
                    84 -> {
                        scene.toggleGridVisibility()
                    }

                    // p
                    80 -> {
                        _effect = (_effect + 1) % 3
                    }
                }
            }

            is WindowKeyReleaseEvent -> {
                when (event.keyCode) {
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

            is WindowMouseOffsetEvent -> {
                scene.camera.rotate(
                    event.offsetX.toFloat() * _mouseSensitivity,
                    event.offsetY.toFloat() * _mouseSensitivity
                )
            }

            is WindowResizeEvent -> {
                scene.camera.aspect = event.width.toFloat() / event.height.toFloat()
                _framebuffer.resize(event.width, event.height)
            }

            else -> {
            }
        }
    }
}
