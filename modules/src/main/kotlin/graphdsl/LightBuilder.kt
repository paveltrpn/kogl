package graphdsl

import graph.*

@DslMarker
annotation class LightBuilderDslMarker

@LightBuilderDslMarker
class LightBuilder : NodeBuilder() {
    private var _light: Light? = null

    fun omniLight(block: OmniLight.() -> Unit): Unit {
        _light = OmniLight().apply {
            block()
        }
    }

    fun spotLight(block: SpotLight.() -> Unit): Unit {
        _light = SpotLight().apply {
            block()
        }
    }

    fun get(): Light {
        return _light ?: throw RuntimeException("Light not initialized!")
    }
}

fun buildLight(block: LightBuilder.() -> Unit): Light {
    return LightBuilder().apply {
        block()
    }.get()
}

infix fun Group.attachLight(block: LightBuilder.() -> Unit): Unit {
    this attach buildLight {
        block()
    }
}

