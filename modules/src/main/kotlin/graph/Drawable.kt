package graph

import algebra.Vector3
import algebra.Matrix4
import render.*
import mesh.*

// ============================================================================
// ======================= Drawable ===========================================
// ============================================================================

abstract class Drawable : Leaf {
    private var _mesh: Mesh? = null
    private var _buffer: MeshBuffer? = null

    protected var _color = Vector3(1.0f, 1.0f, 1.0f)
    protected var _modelMatrix = Matrix4().idtt()

    protected var _origin = Vector3(0.0f, 0.0f, 0.0f)
    protected var _axis = Vector3(0.0f, 1.0f, 0.0f)
    protected var _anglSpeed = 0.0f
    protected var _angl = 0.0f

    constructor() : super() {}

    constructor(mesh: Mesh) : super() {
        _mesh = mesh
        when (_mesh) {
            is IndexedMesh -> {
                _buffer = IndexedMeshBuffer(_mesh as IndexedMesh)
            }

            is SeparatedArraysMesh -> {
                _buffer = ArrayMeshBuffer(_mesh as SeparatedArraysMesh)
            }

            is InterleavedMesh -> {
                _buffer = InterleavedMeshBuffer(_mesh as InterleavedMesh)
            }
        }
    }

    var mesh: Mesh
        get(): Mesh {
            return _mesh!!
        }
        set(value) {
            _mesh = value
            when (_mesh) {
                is IndexedMesh -> {
                    _buffer = IndexedMeshBuffer(_mesh as IndexedMesh)
                }

                is SeparatedArraysMesh -> {
                    _buffer = ArrayMeshBuffer(_mesh as SeparatedArraysMesh)
                }

                is InterleavedMesh -> {
                    _buffer = InterleavedMeshBuffer(_mesh as InterleavedMesh)
                }
            }
        }

    var origin: Vector3
        get(): Vector3 {
            return _origin
        }
        set(value) {
            _origin = value
        }

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
        set(value) {
            _anglSpeed = value
        }

    open fun draw(): Unit {
        _buffer?.draw()
    }

    fun applyTransform(tr: Matrix4): Unit {
        _modelMatrix = tr
    }

    var modelMatrix: Matrix4
        get(): Matrix4 {
            return _modelMatrix
        }
        set(value) {
            _modelMatrix = value
        }

    var color: Vector3
        get(): Vector3 {
            return _color
        }
        set(value) {
            _color = value
        }

    protected val offsetMatrix: Matrix4
        get(): Matrix4 {
            val offset = algebra.offset(_origin)
            offset.transpose()
            return offset
        }

    protected fun updateAnglAndGetSpinMatrix(dt: Float): Matrix4 {
        _angl += anglSpeed * dt

        if (_angl > 360.0f || _angl < -360.0f) _angl = 0.0f

        return algebra.rotation(_axis, _angl)
    }
}

// ============================================================================
// ======================= SpinableDrawable ===================================
// ============================================================================

class SpinableDrawable : Drawable {
    constructor() : super() {}

    constructor(mesh: Mesh) : super(mesh) {}

    fun updateLocal(dt: Float): Matrix4 {
        val spinMatrix = updateAnglAndGetSpinMatrix(dt)
        return spinMatrix.multiply(offsetMatrix)
    }
}

// ============================================================================
// ======================= FlyaroundDrawable ==================================
// ============================================================================

class FlyaroundDrawable : Drawable {
    constructor() : super() {}

    constructor(mesh: Mesh) : super(mesh) {}

    fun updateLocal(dt: Float): Matrix4 {
        val spinMatrix = updateAnglAndGetSpinMatrix(dt)
        return offsetMatrix.multiply(spinMatrix)
    }
}
