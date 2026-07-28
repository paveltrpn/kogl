package scene

import algebra.*

open class Drawable : Node {
    override fun traverse(): Unit {

    }

    fun applyTransform(tr: Matrix4): Unit {

    }

    fun drawCall(): Unit {
        println("perform Drawable drawCall")
    }
}

