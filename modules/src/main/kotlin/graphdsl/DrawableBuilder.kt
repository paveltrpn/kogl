package graphdsl

import graph.*
import mesh.*

@DslMarker
annotation class DrawableBuilderDslMarker

@DrawableBuilderDslMarker
class DrawableBuilder : NodeBuilder() {
    private var _drawable: Drawable? = null

    infix fun set(other: Drawable): Unit {
        _drawable = other
    }

    fun spinableDrawable(block: SpinableDrawable.() -> Unit): Unit {
        _drawable = SpinableDrawable().apply {
            block()
        }
    }

    fun flyaroundDrawable(block: FlyaroundDrawable.() -> Unit): Unit {
        _drawable = FlyaroundDrawable().apply {
            block()
        }
    }

    fun get(): Drawable {
        return _drawable ?: throw RuntimeException("Drawable not initialized!")
    }
}

fun buildDrawable(block: DrawableBuilder.() -> Unit): Drawable {
    return DrawableBuilder().apply {
        block()
    }.get()
}

infix fun Group.attachDrawable(block: DrawableBuilder.() -> Unit): Unit {
    this attach buildDrawable {
        block()
    }
}