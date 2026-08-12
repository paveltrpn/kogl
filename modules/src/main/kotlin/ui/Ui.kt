package ui

import algebra.*

sealed class UiComponent {
    protected var _quadsCount = 0;
}

abstract class Ui {
    protected val _componentsList: MutableList<UiComponent> = mutableListOf()

    fun label(px: Float, py: Float, msg: String): Unit {
        val l = Label()

        // l.setColor( { "white" } )
        l.setGlyphGap(0.1f)
        l.position = Vector3(px, py, 0.5f)

        l.draw(msg)

        _componentsList.addLast(l)
    }

    fun billboard(px: Float, py: Float, sx: Float, sy: Float, z: Float): Unit {
        val b = Billboard()

        b.setPos(px, py)
        b.setSize(sx, sy)
        b.setZ(z)
        // b.setColor( { "#b852ac33" } )
        b.draw()

        _componentsList.addLast(b)
    }

    abstract fun flush(): Unit
}