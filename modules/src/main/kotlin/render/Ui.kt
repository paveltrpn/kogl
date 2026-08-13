package render

import org.lwjgl.opengl.GL46.*

import ui.*
import image.*
import config.*

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

        _program.use()

        val loc = glGetUniformLocation(_program.handle, "font")
        glUniform1i(loc, 0)

        // _font.bind(0)

        _labelBuffer.draw()

        glDisable(GL_TEXTURE_2D)

        _billboardBuffer.draw()

        _componentsList.clear()
    }
}
