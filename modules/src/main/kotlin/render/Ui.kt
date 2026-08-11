package render

import ui.*

class UiGL : Ui() {
    private val OUTPUT_QUADS_COUNT = 128

    private val _labelBuffer = VertexBuffer(OUTPUT_QUADS_COUNT * 6)
    private val _billboardBuffer = VertexBuffer(OUTPUT_QUADS_COUNT * 6)

    fun draw(): Unit {

    }

    override fun flush(): Unit {
//        labelBuffer_.primitievsCount_ = 0;
//        billboardBuffer_.primitievsCount_ = 0;

        _componentsList.clear()
    }
}
