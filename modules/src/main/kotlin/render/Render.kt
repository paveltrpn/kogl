package render

import java.text.*
import java.math.*

import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.*

import algebra.*
import events.*
import graph.*
import userinterface.*
import image.*

// ============================================================================
// ======================= Render =============================================
// ============================================================================

class Render(var viewportWidth: Int, var viewportHeight: Int) : WindowEventObserver {
    private val _ui: UiGL = UiGL()

    private var _scene: Scene? = null

    private val _mouseSensitivity = 0.005f

    private var _framebuffer: Framebuffer
    private var _quadBuffer: QuadsBuffer
    private var _frameProgram: Program

    private var _ssaoFramebuffer: Framebuffer
    private var _ssaoBlurFramebuffer: Framebuffer
    private var _ssaoProgram: Program
    private var _ssaoBlurProgram: Program
    private var _ssaoNoiseTexture: Int
    private var _ssaoKernel: List<Vector3>

    private var _effect = 2

    private var _ssaoEnabled = true

    var mUi: UiDrawable? = null

    var scene: Scene
        get(): Scene {
            return _scene ?: throw RuntimeException("Scene is null! Nothing to return!")
        }
        set(value) {
            _scene = value
        }

    init {
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
            set("ssaoTexture" to 1)
            set("mouseFocusPoint" to Vector2(0.5f, 0.5f))
        }

        _ssaoProgram = Program().apply {
            source = ShaderSource("ssao")
            build()
            use()
            set("depthTexture" to 0)
            set("noiseTexture" to 1)
            set("radius" to 0.3f)
            set("bias" to 0.025f)
            set("kernelSize" to 128)
        }

        _ssaoBlurProgram = Program().apply {
            source = ShaderSource("ssaoBlur")
            build()
            use()
            set("ssaoInput" to 0)
        }

        _framebuffer = Framebuffer(viewportWidth, viewportHeight)
        _ssaoFramebuffer = Framebuffer(viewportWidth, viewportHeight, withDepth = false, colorFormat = GL_RED)
        _ssaoBlurFramebuffer = Framebuffer(viewportWidth, viewportHeight, withDepth = false, colorFormat = GL_RED)

        _ssaoKernel = buildSsaoKernel()
        _ssaoNoiseTexture = buildSsaoNoiseTexture()
    }

    fun preLoop(): Unit {
        if (_scene == null) {
            throw RuntimeException("Scene is null! Nothing to render!")
        }
    }

    private fun buildSsaoKernel(): List<Vector3> {
        val random = kotlin.random.Random(42)

        val _KERNEL_SIZE = 128
        return List(_KERNEL_SIZE) { index: Int ->
            val sample = Vector3(
                random.nextFloat() * 2.0f - 1.0f,
                random.nextFloat() * 2.0f - 1.0f,
                random.nextFloat()
            )

            sample.normalizeSelf()

            var scale = index.toFloat() / _KERNEL_SIZE.toFloat()
            scale = 0.1f + scale * scale * 0.9f

            sample.scale(scale * random.nextFloat())
        }
    }

    private fun buildSsaoNoiseTexture(): Int {
        val _TEXTURE_SIZE = 8

        val random = kotlin.random.Random(1337)
        val noise = FloatArray(_TEXTURE_SIZE * _TEXTURE_SIZE * 3)

        for (i in 0 until _TEXTURE_SIZE * _TEXTURE_SIZE) {
            noise[i * 3 + 0] = random.nextFloat() * 2.0f - 1.0f
            noise[i * 3 + 1] = random.nextFloat() * 2.0f - 1.0f
            noise[i * 3 + 2] = 0.0f
        }

        val texture = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, texture)

        MemoryStack.stackPush().use { stack: MemoryStack ->
            val buffer = stack.mallocFloat(noise.size)
            buffer.put(noise)
            buffer.flip()
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB16F, _TEXTURE_SIZE, _TEXTURE_SIZE, 0, GL_RGB, GL_FLOAT, buffer)
        }

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

        glBindTexture(GL_TEXTURE_2D, 0)

        return texture
    }

    fun frame(): Unit {
        // === Render into framebuffer === //
        _framebuffer.bind()

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LESS)

        scene.walk()

        glDisable(GL_DEPTH_TEST)

        _framebuffer.unbind()
        // === End === //

        // === SSAO pass === //
        _ssaoFramebuffer.bind()

        glClear(GL_COLOR_BUFFER_BIT)

        _ssaoProgram.use()
        _ssaoProgram.set("nearPlane" to scene.camera.ncp)
        _ssaoProgram.set("farPlane" to scene.camera.fcp)
        _ssaoProgram.set("fov" to scene.camera.fov)
        _ssaoProgram.set("aspect" to scene.camera.aspect)
        _ssaoProgram.set("noiseScale" to Vector2(viewportWidth / 4.0f, viewportHeight / 4.0f))

        for (index in _ssaoKernel.indices) {
            _ssaoProgram.set("samples[$index]" to _ssaoKernel[index])
        }

        _framebuffer.bindDepthTexture(0)

        glActiveTexture(GL_TEXTURE1)
        glBindTexture(GL_TEXTURE_2D, _ssaoNoiseTexture)

        _quadBuffer.draw()

        _ssaoFramebuffer.unbind()

        _ssaoBlurFramebuffer.bind()

        glClear(GL_COLOR_BUFFER_BIT)

        _ssaoBlurProgram.use()

        _ssaoFramebuffer.bindTexture(0)

        _quadBuffer.draw()

        _ssaoBlurFramebuffer.unbind()

        glClear(GL_COLOR_BUFFER_BIT)
        // === End === //

        // === Draw framebuffer into the screen quad === //
        _frameProgram.use()
        _frameProgram.set("effect" to _effect)

        if (_ssaoEnabled) {
            _frameProgram.set("ssaoEnabled" to 1)
        } else {
            _frameProgram.set("ssaoEnabled" to 0)
        }

        _framebuffer.bindTexture(0)
        _ssaoBlurFramebuffer.bindTexture(1)

        _quadBuffer.draw()
        // === End === //

        // === Draw screen ui === //
        val TEXT_START_POS_X = 19.0f
        val TEXT_START_POS_Y = 31.0f
        val TEXT_LINE_WIDTH = 0.8f
        val TEXT_LETTER_SPACE = 0.0f
        val TEXT_LETTER_SCALE = 0.6f
        var textLineCount = 0
        val TEXT_PLANE_Z = 0.4f
        val TEXT_PADDING = 0.4f

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

        val aabbToggleLable = Label().apply {
            text = "\"b\" - toggle debug draw"

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

        val ssaoToggleLable = Label().apply {
            text = "\"o\" - toggle ssao"

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
            size = Vector2(13.0f, TEXT_LINE_WIDTH * textLineCount + TEXT_PADDING * 2.0f)
            position = Vector2(TEXT_START_POS_X - TEXT_PADDING, TEXT_START_POS_Y + TEXT_PADDING)
            z = TEXT_PLANE_Z
            color = Color(255, 200, 128, 64)
        }

        _ui.add(
            listOf(
                cameraPosLabel,
                drawCallsLabel,
                gridToggleLable,
                aabbToggleLable,
                postEffectLable,
                ssaoToggleLable,
                holdCameraToggleLable,
                b1
            )
        )

        _ui.flushAndDraw()

        mUi?.draw()
        // === End === //
    }

    fun postLoop(): Unit {
        _quadBuffer.clean()
        _framebuffer.clean()

        _frameProgram.clean()
        _ssaoProgram.clean()
        _ssaoBlurProgram.clean()

        _ssaoFramebuffer.clean()
        _ssaoBlurFramebuffer.clean()

        glDeleteTextures(_ssaoNoiseTexture)
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

                    // b
                    66 -> {
                        scene.toggleAABBVisibility()
                    }

                    // p
                    80 -> {
                        _effect = (_effect + 1) % 3
                    }

                    // o
                    79 -> {
                        _ssaoEnabled = !_ssaoEnabled
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
                _ssaoFramebuffer.resize(event.width, event.height)
                _ssaoBlurFramebuffer.resize(event.width, event.height)

                viewportWidth = event.width
                viewportHeight = event.height
            }

            else -> {
            }
        }
    }
}
