package scene

import algebra.*

open class Drawable : Node {
    override fun traverse(): Unit {
        println("Traverse over Drawable")
    }

    fun applyTransform(tr: Matrix4): Unit {
        println("perform Drawable applyTransform")
    }

    fun drawCall(): Unit {
        println("perform Drawable drawCall")
    }
}

