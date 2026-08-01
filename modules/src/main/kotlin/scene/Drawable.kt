package scene

import algebra.*
import render.VertexBuffer

abstract class Drawable(vertices: FloatArray, indices: IntArray) : Node {
    protected val _vertices: FloatArray = vertices
    protected val _indices: IntArray = indices
    protected val _buffer = VertexBuffer()

    init {
        _buffer.bindVertexData(_vertices)
    }

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
    private var _spin = Matrix4()

    var spin: Matrix4
        get(): Matrix4 {
            return _spin
        }
        set(value: Matrix4) {
            _spin = value
        }

    override fun applyTransform(tr: Matrix4): Unit {
        val combinedTransform = tr.multiply(spin)

        // TODO
        val transformedVertices = FloatArray(_vertices.size)

        var i: Int = 0

        while (i < _vertices.size) {
            val vertex = Vector3(_vertices[i], _vertices[i + 1], _vertices[i + 2])

            val transformed = combinedTransform.vecMultiply(vertex)

            transformedVertices[i] = transformed.x
            transformedVertices[i + 1] = transformed.y
            transformedVertices[i + 2] = transformed.z

            i += 3
        }
    }

}