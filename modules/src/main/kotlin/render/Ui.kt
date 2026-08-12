package render

import ui.*

class UiGL : Ui() {
    private val OUTPUT_QUADS_COUNT = 128

    private var _program: Program

    private val _labelBuffer = QuadsBuffer(OUTPUT_QUADS_COUNT * 6)
    private val _billboardBuffer = QuadsBuffer(OUTPUT_QUADS_COUNT * 6)

    init {
        val programSource = ShaderSource("screenString")
        _program = Program(programSource)
    }

    fun draw(): Unit {

    }

    override fun flush(): Unit {
        for (component in _componentsList) {
            when (component) {
                is Billboard -> {
                    // println(" === ${component.quadsCount}")
                }

                is Label -> {
                    val v = floatArrayOf(
                        -1.0f, 1.0f, 10.0f,
                        1.0f, 1.0f, 10.0f,
                        1.0f, -1.0f, 10.0f,
                        1.0f, -1.0f, 10.0f,
                        -1.0f, -1.0f, 10.0f,
                        -1.0f, 1.0f, 10.0f
                    )

                    _labelBuffer.updateData(v, component.txcoords)
                    // println(" === ${component.quadsCount}")
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
