package render

import ui.*

class UiGL : Ui() {
    private val OUTPUT_QUADS_CAPACITY = 128

    private var _program: Program

    private val _labelBuffer = QuadsBuffer(OUTPUT_QUADS_CAPACITY)
    private val _billboardBuffer = QuadsBuffer(OUTPUT_QUADS_CAPACITY)

    init {
        val programSource = ShaderSource("screenString")
        _program = Program(programSource)
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

        _program.use()

        _labelBuffer.draw()

        _billboardBuffer.draw()

        _componentsList.clear()
    }
}
