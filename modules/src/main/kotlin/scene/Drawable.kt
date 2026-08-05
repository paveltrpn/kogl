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

    protected var _combined = Matrix4().idtt()

    override fun traverse(): Unit {
        _buffer.drawIndexed()
    }

    abstract fun applyTransform(tr: Matrix4): Unit

    var combined: Matrix4
        get(): Matrix4 {
            return _combined
        }
        set(value: Matrix4) {
            _combined = value
        }
}

// ============================================================================
// ======================= StaticDrawable =====================================
// ============================================================================

class StaticDrawable(mesh: IndexedMesh) : Drawable(mesh) {
    override fun applyTransform(tr: Matrix4): Unit {
        // Why transpose?
        tr.transpose()

        _combined = tr
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

        // Why transpose?
        tr.transpose()

        _combined = spin.multiply(tr)
    }
}