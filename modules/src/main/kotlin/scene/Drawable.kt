package scene

import algebra.*
import render.*
import mesh.*

// ============================================================================
// ======================= Drawable ===========================================
// ============================================================================

abstract class Drawable(mesh: IndexedMesh) : Node {
    private val _mesh = mesh
    private val _buffer = MeshBuffer(_mesh)

    override fun traverse(): Unit {
        // _buffer.updateVertexData(_staging)
        _buffer.drawIndexed()
    }

    abstract fun applyTransform(tr: Matrix4): Unit

//    protected fun transform(tr: Matrix4): Unit {
//        var i: Int = 0
//        while (i < _vertices.size) {
//            val vertex = Vector3(_vertices[i + 0], _vertices[i + 1], _vertices[i + 2])
//
//            val transformed = tr.vecMultiply(vertex)
//
//            _staging[i + 0] = transformed.x
//            _staging[i + 1] = transformed.y
//            _staging[i + 2] = transformed.z
//
//            i += 3
//        }
//    }
}

// ============================================================================
// ======================= StaticDrawable =====================================
// ============================================================================

class StaticDrawable(mesh: IndexedMesh) : Drawable(mesh) {
    override fun applyTransform(tr: Matrix4): Unit {
        // transform(tr)
    }
}

// ============================================================================
// ======================= SpinableDrawable ===================================
// ============================================================================

class SpinableDrawable(mesh: IndexedMesh) : Drawable(mesh) {
    private var _axis = Vector3()
    private var _anglSpeed = 0.0f
    private var _angl = 0.0f

    var axis: Vector3
        get(): Vector3 {
            return _axis
        }
        set(value: Vector3) {
            _axis = value
            _axis.normalizeSelf()
        }

    var anglSpeed: Float
        get(): Float {
            return _anglSpeed
        }
        set(value: Float) {
            _anglSpeed = value
        }

    override fun applyTransform(tr: Matrix4): Unit {
        _angl += _anglSpeed

        if (_angl > 360.0f || _angl < -360.0f) _angl = 0.0f

        val spin = rotation(_axis, _angl)

        val combined = tr.multiply(spin)

        // transform(combined)
    }
}