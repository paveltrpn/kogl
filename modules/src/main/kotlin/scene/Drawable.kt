package scene

import algebra.*

interface Drawable : Node {
    abstract fun applyTransform(tr: Matrix4): Unit
    abstract fun drawCall(): Unit
}

class StaticDrawable : Drawable {
    override fun traverse(): Unit {
        println("Traverse over StaticDrawable")
    }

    override fun applyTransform(tr: Matrix4): Unit {
        println("perform StaticDrawable applyTransform")
    }

    override fun drawCall(): Unit {
        println("perform StaticDrawable drawCall")
    }
}

class SpinableDrawable : Drawable {
    override fun traverse(): Unit {
        println("Traverse over SpinableDrawable")
    }

    override fun applyTransform(tr: Matrix4): Unit {
        println("perform SpinableDrawable applyTransform")
    }

    override fun drawCall(): Unit {
        println("perform SpinableDrawable drawCall")
    }
}