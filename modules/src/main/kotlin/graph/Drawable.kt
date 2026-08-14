package graph

import algebra.*
import render.*
import mesh.*

// ============================================================================
// ======================= Drawable ===========================================
// ============================================================================

abstract class Drawable(mesh: Mesh) : Node {
    private var _buffer: MeshBuffer? = null

    protected var _color = Vector3(1.0f, 1.0f, 1.0f)
    protected var _combined = Matrix4().idtt()

    init {
        when (mesh) {
            is IndexedMesh -> {
                _buffer = IndexedMeshBuffer(mesh)
            }

            is SeparatedArraysMesh -> {
                _buffer = ArrayMeshBuffer(mesh)
            }

            is InterleavedMesh -> {
                _buffer = InterleavedMeshBuffer(mesh)
            }
        }
    }

    override fun traverse(): Unit {
        _buffer?.draw()
    }

    abstract fun applyTransform(tr: Matrix4): Unit

    var combined: Matrix4
        get(): Matrix4 {
            return _combined
        }
        set(value: Matrix4) {
            _combined = value
        }

    var color: Vector3
        get(): Vector3 {
            return _color
        }
        set(value: Vector3) {
            _color = value
        }
}

// ============================================================================
// ======================= StaticDrawable =====================================
// ============================================================================

class StaticDrawable(mesh: Mesh) : Drawable(mesh) {
    override fun applyTransform(tr: Matrix4): Unit {
        // Why transpose?
        tr.transpose()

        _combined = tr
    }
}

// ============================================================================
// ======================= SpinableDrawable ===================================
// ============================================================================

class SpinableDrawable(mesh: Mesh) : Drawable(mesh) {
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