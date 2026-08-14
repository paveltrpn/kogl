package graph

import algebra.*
import render.*
import mesh.*

// ============================================================================
// ======================= Drawable ===========================================
// ============================================================================

open class Drawable(mesh: Mesh) : Node {
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

    override fun accept(visitor: Visitor): Unit {
        visitor.apply(this)
    }

    override fun ascend(visitor: Visitor): Unit {
    }

    override fun traverse(visitor: Visitor): Unit {

    }

    fun draw(): Unit {
        _buffer?.draw()
    }

    open fun applyTransform(tr: Matrix4): Unit {
        // Why transpose?
        tr.transpose()

        _combined = tr
    }

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