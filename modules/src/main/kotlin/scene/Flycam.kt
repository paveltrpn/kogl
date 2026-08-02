package scene

import algebra.*

enum class FlycamMoveBits {
    NONE, FORWARD, BACKWARD, LEFT, RIGHT, UP, DOWN
}

class Flycam {
    private var _fov: Double = 45.0
    private var _aspect: Double = 16.0 / 9.0
    private var _ncp: Double = 0.1
    private var _fcp: Double = 100.0

    private var _eye: Vector3 = Vector3(0.0f, 0.0f, 0.0f)
    private var _zenith: Vector3 = Vector3(0.0f, 1.0f, 0.0f)
    private var _right: Vector3 = Vector3(1.0f, 0.0f, 0.0f)
    private var _look: Vector3 = Vector3(0.0f, 0.0f, 1.0f)

    private var _azimuth: Double = 0.0
    private var _elevation: Double = 0.0
    private var _roll: Double = 0.0

    private var _moveMask: Int = 0

    private var _name: String = ""

    var fov: Double
        get() = _fov
        set(value) { _fov = value }

    var aspect: Double
        get() = _aspect
        set(value) { _aspect = value }

    var ncp: Double
        get() = _ncp
        set(value) { _ncp = value }

    var fcp: Double
        get() = _fcp
        set(value) { _fcp = value }

    var eye: Vector3
        get() = _eye
        set(value) { _eye = value }

    var zenith: Vector3
        get() = _zenith
        set(value) { _zenith = value }

    var right: Vector3
        get() = _right
        set(value) { _right = value }

    var look: Vector3
        get() = _look
        set(value) { _look = value }

    var azimuth: Double
        get() = _azimuth
        set(value) { _azimuth = value }

    var elevation: Double
        get() = _elevation
        set(value) { _elevation = value }

    var roll: Double
        get() = _roll
        set(value) { _roll = value }

    var name: String
        get() = _name
        set(value) { _name = value }

    constructor(eye: Vector3, azimuth: Double, elevation: Double) {
        this._eye = eye
        this._azimuth = azimuth
        this._elevation = elevation
        this._roll = 0.0
    }

    constructor() : this(Vector3(0.0f, 0.0f, 0.0f), 0.0, 0.0)

    fun setMoveBit(bit: FlycamMoveBits) {
        _moveMask = _moveMask or (1 shl bit.ordinal)
    }

    fun unsetMoveBit(bit: FlycamMoveBits) {
        _moveMask = _moveMask and (1 shl bit.ordinal).inv()
    }

    fun unsetMoveAll() {
        _moveMask = 0
    }

    fun rotate(azimuthOffset: Double, elevationOffset: Double) {
        _azimuth += azimuthOffset

        if (_azimuth > 360.0 || _azimuth < -360.0) _azimuth = 0.0

        _elevation += elevationOffset

        val elevationBound = 80.0
        if (_elevation > elevationBound) _elevation = elevationBound
        if (_elevation < -elevationBound) _elevation = -elevationBound
    }

    fun setPosition(pos: Vector3) {
        _eye = pos
    }

    fun setAngles(azimuth: Double, elevation: Double, roll: Double) {
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

    fun matrix(): Matrix4 {
        val projection = Matrix4().perspective(_fov, _aspect, _ncp, _fcp)

        val ar = Matrix4().rotate(_zenith, _azimuth.toFloat())
        _right = ar.vecMultiply(Vector3(1.0f, 0.0f, 0.0f))

        val er = Matrix4().rotate(_right, _elevation.toFloat())
        _look = er.vecMultiply(_right.cross(_zenith))

        val offset = Matrix4().translate(_eye)
        offset.transpose()

        return offset.multiply(er).multiply(ar).multiply(projection)
    }
}
