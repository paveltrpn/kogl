package graphdsl

import graph.*
import mesh.*

@DslMarker
annotation class DrawableBuilderDslMarker

@DrawableBuilderDslMarker
class DrawableBuilder : NodeBuilder() {
    private var _mesh: Mesh? = null
    private var _drawable: Drawable? = null

    var mesh: Mesh
        get(): Mesh {
            return _mesh!!
        }
        set(value) {
            _mesh = value
        }

    fun staticDrawable(block: Drawable.() -> Unit): Unit {
        _drawable = Drawable().apply { block() }
        // (_drawable as Drawable).apply(block)
    }

    fun flyaroundDrawable(block: FlyaroundDrawable.() -> Unit): Unit {
        _drawable = FlyaroundDrawable().apply { block() }
//        (_drawable as FlyaroundDrawable).apply(block)
    }

    fun get(): Drawable {
        return _drawable ?: throw RuntimeException("Drawable not initialized!")
    }

    override fun attach(node: Node) {
        TODO("Not yet implemented")
    }
}

fun buildDrawable(block: DrawableBuilder.() -> Unit): Drawable {
    return DrawableBuilder().apply(block).get()
}