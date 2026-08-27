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
        val uiProgramSource = ShaderSource("ui")
        _program = Program().apply {
            source(uiProgramSource)
            build()
        }

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
                    _billboardBuffer.fill(
                        billboardBufOffset,
                        component.color.asVec4,
                        component.vertices,
                        component.txcoords
                    )
                    billboardBufOffset += component.quadsCount
                }

                is Label -> {
                    _labelBuffer.fill(
                        labelBufOffset,
                        component.color.asVec4,
                        component.vertices,
                        component.txcoords
                    )
                    labelBufOffset += component.quadsCount
                }
            }

        }

//        glEnable(GL_TEXTURE_2D)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        _program.use()

        // val samplerLoc = glGetUniformLocation(_program.handle, "font")
        val samplerLoc = 2

        // val colorLoc = glGetUniformLocation(_program.handle, "color")
        val colorLoc = 3

        // val enableTextureLoc = glGetUniformLocation(_program.handle, "enableTexture")
        val enableTextureLoc = 4

        // Render billboards geometry.

        glUniform1i(enableTextureLoc, 0)
        val bbColor = Color(255, 200, 128, 64).asVec4.data
        glUniform4f(colorLoc, bbColor[0], bbColor[1], bbColor[2], bbColor[3])

        _billboardBuffer.draw()

        // Render labels.

        glUniform1i(samplerLoc, 0)
        glUniform1i(enableTextureLoc, 1)
        val textColor = Color(255, 255, 255, 255).asVec4.data
        glUniform4f(colorLoc, textColor[0], textColor[1], textColor[2], textColor[3])

        _font.bind(0)

        _labelBuffer.draw()

//        NOTE: GL_INVALID_ENUM error generated in this place.
//        glDisable(GL_TEXTURE_2D)

        glDisable(GL_BLEND)

        _componentsList.clear()
    }
}
