package scene

import algebra.*

enum class FlycamMoveBits {
    NONE, FORWARD, BACKWARD, LEFT, RIGHT, UP, DOWN
}

class Flycam {
    private var _fov: Float = 45.0f
    private var _aspect: Float = 16.0f / 9.0f
    private var _ncp: Float = 0.1f
    private var _fcp: Float = 100.0f

    private var _eye: Vector3 = Vector3(0.0f, 0.0f, 0.0f)
    private var _zenith: Vector3 = Vector3(0.0f, 1.0f, 0.0f)
    private var _right: Vector3 = Vector3(1.0f, 0.0f, 0.0f)
    private var _look: Vector3 = Vector3(0.0f, 0.0f, 1.0f)

    private var _azimuth: Float = 0.0f
    private var _elevation: Float = 0.0f
    private var _roll: Float = 0.0f

    private var _moveMask: Int = 0

    private var _name: String = ""

    var fov: Float
        get() = _fov
        set(value) {
            _fov = value
        }

    var aspect: Float
        get() = _aspect
        set(value) {
            _aspect = value
        }

    var ncp: Float
        get() = _ncp
        set(value) {
            _ncp = value
        }

    var fcp: Float
        get() = _fcp
        set(value) {
            _fcp = value
        }

    var eye: Vector3
        get() = _eye
        set(value) {
            _eye = value
        }

    var zenith: Vector3
        get() = _zenith
        set(value) {
            _zenith = value
        }

    var right: Vector3
        get() = _right
        set(value) {
            _right = value
        }

    var look: Vector3
        get() = _look
        set(value) {
            _look = value
        }

    var azimuth: Float
        get() = _azimuth
        set(value) {
            _azimuth = value
        }

    var elevation: Float
        get() = _elevation
        set(value) {
            _elevation = value
        }

    var roll: Float
        get() = _roll
        set(value) {
            _roll = value
        }

    var name: String
        get() = _name
        set(value) {
            _name = value
        }

    constructor(eye: Vector3, azimuth: Float, elevation: Float) {
        this._eye = eye
        this._azimuth = azimuth
        this._elevation = elevation
        this._roll = 0.0f
    }

    constructor() : this(Vector3(0.0f, 0.0f, 0.0f), 0.0f, 0.0f)

    fun setMoveBit(bit: FlycamMoveBits) {
        _moveMask = _moveMask or (1 shl bit.ordinal)
    }

    fun unsetMoveBit(bit: FlycamMoveBits) {
        _moveMask = _moveMask and (1 shl bit.ordinal).inv()
    }

    fun unsetMoveAll() {
        _moveMask = 0
    }

    fun rotate(azimuthOffset: Float, elevationOffset: Float) {
        _azimuth += azimuthOffset

        if (_azimuth > 360.0f || _azimuth < -360.0f) _azimuth = 0.0f

        _elevation += elevationOffset

        val elevationBound = 80.0f
        if (_elevation > elevationBound) _elevation = elevationBound
        if (_elevation < -elevationBound) _elevation = -elevationBound
    }

    fun setPosition(pos: Vector3) {
        _eye = pos
    }

    fun setAngles(azimuth: Float, elevation: Float, roll: Float) {
        this._azimuth = azimuth
        this._elevation = elevation
        this._roll = roll
    }

    fun traverse() {
        val velocity = Vector3(0.0f, 0.0f, 0.0f)

        if ((_moveMask shr FlycamMoveBits.FORWARD.ordinal) and 1 == 1)
            velocity.x += _look.x
        if ((_moveMask shr FlycamMoveBits.BACKWARD.ordinal) and 1 == 1)
            velocity.x -= _look.x
        if ((_moveMask shr FlycamMoveBits.RIGHT.ordinal) and 1 == 1)
            velocity.x -= _right.x
        if ((_moveMask shr FlycamMoveBits.LEFT.ordinal) and 1 == 1)
            velocity.x += _right.x

        _eye.x += velocity.x * 0.5f
        _eye.y += velocity.y * 0.5f
        _eye.z += velocity.z * 0.5f
    }

    val matrix: Matrix4
        get(): Matrix4 {
            val projection = Matrix4().perspective(_fov, _aspect, _ncp, _fcp)

            val ar = Matrix4().rotate(_zenith, _azimuth)
            _right = ar.vecMultiply(Vector3(1.0f, 0.0f, 0.0f))

            val er = Matrix4().rotate(_right, _elevation)
            _look = er.vecMultiply(_right.cross(_zenith))

            val offset = Matrix4().offset(_eye)
            offset.transpose()

            return offset.multiply(er).multiply(ar).multiply(projection)
        }
}
