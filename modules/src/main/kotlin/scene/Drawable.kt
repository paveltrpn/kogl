package scene

import algebra.*
import render.VertexBuffer

// ============================================================================
// ======================= Drawable ===========================================
// ============================================================================

abstract class Drawable(vertices: FloatArray, indices: IntArray) : Node {
    protected val _vertices: FloatArray = vertices
    protected val _indices: IntArray = indices
    protected var _staging: FloatArray
    protected val _buffer = VertexBuffer()

    init {
        _buffer.bindVertexData(_vertices)
        _buffer.bindIndexData(_indices)

        _staging = FloatArray(_vertices.size)
    }

    override fun traverse(): Unit {
        _buffer.updateVertexData(_staging)
        _buffer.drawIndexed()
    }

    abstract fun applyTransform(tr: Matrix4): Unit

    protected fun transform(tr: Matrix4): Unit {
        var i: Int = 0
        while (i < _vertices.size) {
            val vertex = Vector3(_vertices[i], _vertices[i + 1], _vertices[i + 2])

            val transformed = tr.vecMultiply(vertex)

            _staging[i] = transformed.x
            _staging[i + 1] = transformed.y
            _staging[i + 2] = transformed.z

            i += 3
        }
    }
}

// ============================================================================
// ======================= StaticDrawable =====================================
// ============================================================================

class StaticDrawable(vertices: FloatArray, indices: IntArray) : Drawable(vertices, indices) {
    override fun applyTransform(tr: Matrix4): Unit {
        transform(tr)
    }
}

// ============================================================================
// ======================= SpinableDrawable ===================================
// ============================================================================

class SpinableDrawable(vertices: FloatArray, indices: IntArray) : Drawable(vertices, indices) {
    private var _axis = Vector3()
    private var _angl = 0.0f

    var axis: Vector3
        get(): Vector3 {
            return _axis
        }
        set(value: Vector3) {
            _axis = value
        }

    var angl: Float
        get(): Float {
            return _angl
        }
        set(value: Float) {
            _angl = value
        }

    override fun applyTransform(tr: Matrix4): Unit {
        val spin = rotation(_axis, _angl)
        val combinedTransform = tr.multiply(spin)
        transform(combinedTransform)
    }
}