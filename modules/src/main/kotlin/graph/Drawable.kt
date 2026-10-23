package graph

import algebra.*
import render.*
import mesh.*

// ============================================================================
// ======================= Drawable ===========================================
// ============================================================================

abstract class Drawable : Leaf {
    private var _mesh: Mesh? = null
    private var _buffer: MeshBuffer? = null

    protected var mColor = Vector3(1.0f, 1.0f, 1.0f)
    protected var mModelMatrix = Matrix4().idtt()

    protected var mOrigin = Vector3(0.0f, 0.0f, 0.0f)
    protected var mScale = Vector3(1.0f, 1.0f, 1.0f)
    protected var mAxis = Vector3(0.0f, 1.0f, 0.0f)
    protected var mAngl = 0.0f
    protected var mAnglSpeed = 0.0f

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

    val hasMesh: Boolean
        get(): Boolean {
            return _mesh != null
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
            return mOrigin
        }
        set(value) {
            mOrigin = value
        }

    var scale: Vector3
        get(): Vector3 {
            return mScale
        }
        set(value) {
            mScale = value
        }

    var axis: Vector3
        get(): Vector3 {
            return mAxis
        }
        set(value: Vector3) {
            // TODO: assert zero length vector.
            mAxis = value
            mAxis.normalizeSelf()
        }

    var angl: Float
        get(): Float {
            return mAngl
        }
        set(value) {
            mAngl = value
        }

    var anglSpeed: Float
        get(): Float {
            return mAnglSpeed
        }
        set(value) {
            mAnglSpeed = value
        }

    open fun draw(): Unit {
        _buffer?.draw()
    }

    fun applyTransform(tr: Matrix4): Unit {
        mModelMatrix = tr
    }

    var modelMatrix: Matrix4
        get(): Matrix4 {
            return mModelMatrix
        }
        set(value) {
            mModelMatrix = value
        }

    var color: Vector3
        get(): Vector3 {
            return mColor
        }
        set(value) {
            mColor = value
        }

    protected val mOffsetMatrix: Matrix4
        get(): Matrix4 {
            val offset = algebra.offset(mOrigin)
            offset.transpose()
            return offset
        }

    protected val mScaleMatrix: Matrix4
        get(): Matrix4 {
            return algebra.scale(mScale)
        }

    protected fun mUpdateAnglAndGetSpinMatrix(dt: Float): Matrix4 {
        mAngl += anglSpeed * dt

        if (mAngl > 360.0f || mAngl < -360.0f) {
            mAngl = 0.0f
        }

        return algebra.rotation(mAxis, mAngl)
    }
}

// ============================================================================
// ======================= SpinableDrawable ===================================
// ============================================================================

class SpinableDrawable : Drawable {
    constructor() : super() {}

    constructor(mesh: Mesh) : super(mesh) {}

    fun updateLocal(dt: Float): Matrix4 {
        val spinMatrix = mUpdateAnglAndGetSpinMatrix(dt)
        val t = mScaleMatrix.multiply(spinMatrix)
        return t.multiply(mOffsetMatrix)
    }
}

// ============================================================================
// ======================= FlyaroundDrawable ==================================
// ============================================================================

class FlyaroundDrawable : Drawable {
    constructor() : super() {}

    constructor(mesh: Mesh) : super(mesh) {}

    fun updateLocal(dt: Float): Matrix4 {
        val spinMatrix = mUpdateAnglAndGetSpinMatrix(dt)
        val t = mOffsetMatrix.multiply(mScaleMatrix)
        return t.multiply(spinMatrix)
    }
}
