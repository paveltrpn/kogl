package graphdsl

import graph.*
import mesh.*

class DrawableBuilder : NodeBuilder() {
    private var _drawable: Drawable? = null

    fun staticDrawable(mesh: Mesh, block: Drawable.() -> Unit): Unit {
        _drawable = Drawable(mesh)
        (_drawable as Drawable).apply(block)
    }

    fun flyaroundDrawable(mesh: Mesh, block: FlyaroundDrawable.() -> Unit): Unit {
        _drawable = FlyaroundDrawable(mesh)
        (_drawable as FlyaroundDrawable).apply(block)
    }

    fun get(): Drawable {
        return _drawable ?: throw RuntimeException("Drawable not initialized!")
    }
}

fun buildDrawable(block: DrawableBuilder.() -> Unit): Drawable {
    return DrawableBuilder().apply(block).get()
}