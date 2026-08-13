package render

import algebra.Vector4
import org.lwjgl.opengl.GL46.*

import ui.*
import image.*
import config.*
import org.lwjgl.system.MemoryStack
import kotlin.use

class UiGL : Ui() {
    private val OUTPUT_QUADS_CAPACITY = 128

    private val _program: Program

    private val _font: Texture

    private val _labelBuffer = QuadsBuffer(OUTPUT_QUADS_CAPACITY)
    private val _billboardBuffer = QuadsBuffer(OUTPUT_QUADS_CAPACITY)

    init {
        val programSource = ShaderSource("screenString")
        _program = Program(programSource)

        val path = "${Config.instance().basePath}/assets/fonts/Consolas-1024-512-32-64-alpha.tga"
        val fontImage = Tga(path)
        _font = Texture(fontImage)
    }

    override fun flush(): Unit {
        var labelBufOffset = 0
        var billboardBufOffset = 0

        for (component in _componentsList) {
            when (component) {
                is Billboard -> {
                    _billboardBuffer.fill(billboardBufOffset, component.vertices, component.txcoords)
                    billboardBufOffset += component.quadsCount
                }

                is Label -> {
                    _labelBuffer.fill(labelBufOffset, component.vertices, component.txcoords)
                    labelBufOffset += component.quadsCount
                }
            }

        }

        glEnable(GL_TEXTURE_2D)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        _program.use()

        // val colorLoc = glGetUniformLocation(_program.handle, "color")
        val colorLoc = 3

        MemoryStack.stackPush().use { stack ->
            val textColorValue = Vector4(1.0f, 1.0f, 1.0f, 1.0f)
            val buf = stack.mallocFloat(4)
            buf.put(textColorValue.data)
            buf.flip()
            glUniform4fv(colorLoc, buf)
        }

        // val samplerLoc = glGetUniformLocation(_program.handle, "font")
        val samplerLoc = 2
        glUniform1i(samplerLoc, 0)

        // val enableTextureLoc = glGetUniformLocation(_program.handle, "enableTexture")
        val enableTextureLoc = 4
        glUniform1i(enableTextureLoc, 1)

        _font.bind(0)

        _labelBuffer.draw()

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glUniform1i(enableTextureLoc, 0)

        MemoryStack.stackPush().use { stack ->
            val textColorValue = Vector4(0.8f, 0.4f, 0.1f, 0.5f)
            val buf = stack.mallocFloat(4)
            buf.put(textColorValue.data)
            buf.flip()
            glUniform4fv(colorLoc, buf)
        }

        _billboardBuffer.draw()

        glDisable(GL_TEXTURE_2D)
        glDisable(GL_BLEND)

        _componentsList.clear()
    }
}
