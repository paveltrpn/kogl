package scene

import algebra.*

abstract class Drawable(vertices: FloatArray, indices: IntArray) : Node {
    private val _vertices: FloatArray = vertices
    private val _indices: IntArray = indices

    override fun traverse(): Unit {
        println("Traverse over Drawable")
    }

    fun drawCall(): Unit {
        println("perform Drawable drawCall")
    }

    abstract fun applyTransform(tr: Matrix4): Unit
}

class StaticDrawable(vertices: FloatArray, indices: IntArray) : Drawable(vertices, indices) {
    override fun applyTransform(tr: Matrix4): Unit {
        println("perform StaticDrawable applyTransform")
    }
}

class SpinableDrawable(vertices: FloatArray, indices: IntArray) : Drawable(vertices, indices) {
    override fun applyTransform(tr: Matrix4): Unit {
        println("perform SpinableDrawable applyTransform")
    }
}