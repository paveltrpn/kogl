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
        for (component in _componentsList) {
            var nowOffset = 0

            when (component) {
                is Billboard -> {
                }

                is Label -> {
                    _labelBuffer.updateData(component.vertices, component.txcoords)
                    nowOffset = component.quadsCount
                }
            }

        }

        _program.use()

        _labelBuffer.draw()

//        labelBuffer_.primitievsCount_ = 0;
//        billboardBuffer_.primitievsCount_ = 0;

        _componentsList.clear()
    }
}
