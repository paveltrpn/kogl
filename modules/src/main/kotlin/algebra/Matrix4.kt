package algebra

class Matrix4 {
    private var _data = FloatArray(16) { it -> 0.0f }

    constructor() {

    }

    constructor(other: Matrix4) {
        _data = other._data
    }

    // Rows
    var row0: Vector4
        get(): Vector4 {
            return Vector4(_data[0], _data[1], _data[2], _data[3])
        }
        set(value) {
            _data[0] = value.x
            _data[1] = value.y
            _data[2] = value.z
            _data[3] = value.w
        }

    var row1: Vector4
        get(): Vector4 {
            return Vector4(_data[4], _data[5], _data[6], _data[7])
        }
        set(value) {
            _data[4] = value.x
            _data[5] = value.y
            _data[6] = value.z
            _data[7] = value.w
        }

    var row2: Vector4
        get(): Vector4 {
            return Vector4(_data[8], _data[9], _data[10], _data[11])
        }
        set(value) {
            _data[8] = value.x
            _data[9] = value.y
            _data[10] = value.z
            _data[11] = value.w
        }

    var row3: Vector4
        get(): Vector4 {
            return Vector4(_data[12], _data[13], _data[14], _data[15])
        }
        set(value) {
            _data[12] = value.x
            _data[13] = value.y
            _data[14] = value.z
            _data[15] = value.w
        }

    // Columnes
    var column0: Vector4
        get(): Vector4 {
            return Vector4(_data[0], _data[4], _data[8], _data[12])
        }
        set(value) {
            _data[0] = value.x
            _data[4] = value.y
            _data[8] = value.z
            _data[12] = value.w
        }

    var column1: Vector4
        get(): Vector4 {
            return Vector4(_data[1], _data[5], _data[9], _data[13])
        }
        set(value) {
            _data[1] = value.x
            _data[5] = value.y
            _data[9] = value.z
            _data[13] = value.w
        }

    var column2: Vector4
        get(): Vector4 {
            return Vector4(_data[2], _data[6], _data[10], _data[14])
        }
        set(value) {
            _data[2] = value.x
            _data[6] = value.y
            _data[10] = value.z
            _data[14] = value.w
        }

    var column3: Vector4
        get(): Vector4 {
            return Vector4(_data[3], _data[7], _data[11], _data[14])
        }
        set(value) {
            _data[3] = value.x
            _data[7] = value.y
            _data[11] = value.z
            _data[14] = value.w
        }

    fun multiply(other: Matrix4): Matrix4 {
        val result = Matrix4()
        for (i in 0..3) {
            for (j in 0..3) {
                for (k in 0..3) {
                    result._data[i * 4 + j] += _data[i * 4 + k] * other._data[k * 4 + j]
                }
            }
        }
        return result
    }

    fun vecMultiply(other: Vector4): Vector4 {
        return Vector4(
            _data[0] * other.x + _data[1] * other.y + _data[2] * other.z + _data[3] * other.w,
            _data[4] * other.x + _data[5] * other.y + _data[6] * other.z + _data[7] * other.w,
            _data[8] * other.x + _data[9] * other.y + _data[10] * other.z + _data[11] * other.w,
            _data[12] * other.x + _data[13] * other.y + _data[14] * other.z + _data[15] * other.w
        )
    }

    fun vecMultiply(other: Vector3): Vector3 {
        val x = _data[0] * other.x + _data[1] * other.y + _data[2] * other.z + _data[3]
        val y = _data[4] * other.x + _data[5] * other.y + _data[6] * other.z + _data[7]
        val z = _data[8] * other.x + _data[9] * other.y + _data[10] * other.z + _data[11]
        return Vector3(x, y, z)
    }

    fun perspective(fov: Float, aspect: Float, near: Float, far: Float): Matrix4 {
        val result = Matrix4()
        val tanHalfFov = kotlin.math.tan(toRadians(fov) / 2.0f)

        result._data[0] = 1.0f / (aspect * tanHalfFov)
        result._data[5] = 1.0f / tanHalfFov
        result._data[10] = -(far + near) / (far - near)
        result._data[11] = -1.0f
        result._data[14] = -(2.0f * far * near) / (far - near)

        return result
    }

    fun ortho(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Matrix4 {
        val result = Matrix4()

        result._data[0] = 2.0f / (right - left)
        result._data[5] = 2.0f / (top - bottom)
        result._data[10] = -2.0f / (far - near)

        result._data[12] = -(right + left) / (right - left)
        result._data[13] = -(top + bottom) / (top - bottom)
        result._data[14] = -(far + near) / (far - near)

        return result
    }

    fun fromAxisAngle(axis: Vector3, angle: Float): Matrix4 {
        val x = axis.x
        val y = axis.y
        val z = axis.z
        val cos = kotlin.math.cos(toRadians(angle)).toFloat()
        val sin = kotlin.math.sin(toRadians(angle)).toFloat()
        val oneMinusCos = 1.0f - cos

        _data[0] = cos + x * x * oneMinusCos
        _data[1] = x * y * oneMinusCos - z * sin
        _data[2] = x * z * oneMinusCos + y * sin
        _data[3] = 0.0f

        _data[4] = y * x * oneMinusCos + z * sin
        _data[5] = cos + y * y * oneMinusCos
        _data[6] = y * z * oneMinusCos - x * sin
        _data[7] = 0.0f

        _data[8] = z * x * oneMinusCos - y * sin
        _data[9] = z * y * oneMinusCos + x * sin
        _data[10] = cos + z * z * oneMinusCos
        _data[11] = 0.0f

        _data[12] = 0.0f
        _data[13] = 0.0f
        _data[14] = 0.0f
        _data[15] = 1.0f

        return this
    }

    fun fromEuler(x: Float, y: Float, z: Float): Matrix4 {
        val cx = kotlin.math.cos(toRadians(x)).toFloat()
        val sx = kotlin.math.sin(toRadians(x)).toFloat()
        val cy = kotlin.math.cos(toRadians(y)).toFloat()
        val sy = kotlin.math.sin(toRadians(y)).toFloat()
        val cz = kotlin.math.cos(toRadians(z)).toFloat()
        val sz = kotlin.math.sin(toRadians(z)).toFloat()

        _data[0] = cy * cz
        _data[1] = cy * sz
        _data[2] = -sy
        _data[3] = 0.0f

        _data[4] = sx * sy * cz - cx * sz
        _data[5] = sx * sy * sz + cx * cz
        _data[6] = sx * cy
        _data[7] = 0.0f

        _data[8] = cx * sy * cz + sx * sz
        _data[9] = cx * sy * sz - sx * cz
        _data[10] = cx * cy
        _data[11] = 0.0f

        _data[12] = 0.0f
        _data[13] = 0.0f
        _data[14] = 0.0f
        _data[15] = 1.0f

        return this
    }

    fun offset(x: Float, y: Float, z: Float): Matrix4 {
        _data[0] = 1.0f
        _data[1] = 0.0f
        _data[2] = 0.0f
        _data[3] = 0.0f

        _data[4] = 0.0f
        _data[5] = 1.0f
        _data[6] = 0.0f
        _data[7] = 0.0f

        _data[8] = 0.0f
        _data[9] = 0.0f
        _data[10] = 1.0f
        _data[11] = 0.0f

        _data[12] = x
        _data[13] = y
        _data[14] = z
        _data[15] = 1.0f

        return this
    }

    fun offset(v: Vector3): Matrix4 {
        return offset(v.x, v.y, v.z)
    }

    fun scale(x: Float, y: Float, z: Float): Matrix4 {
        _data[0] = x
        _data[1] = 0.0f
        _data[2] = 0.0f
        _data[3] = 0.0f

        _data[4] = 0.0f
        _data[5] = y
        _data[6] = 0.0f
        _data[7] = 0.0f

        _data[8] = 0.0f
        _data[9] = 0.0f
        _data[10] = z
        _data[11] = 0.0f

        _data[12] = 0.0f
        _data[13] = 0.0f
        _data[14] = 0.0f
        _data[15] = 1.0f

        return this
    }

    fun scale(s: Float): Matrix4 {
        return scale(s, s, s)
    }

    fun scale(v: Vector3): Matrix4 {
        return scale(v.x, v.y, v.z)
    }

    fun transpose() {
        val tmp = _data[1]
        _data[1] = _data[4]
        _data[4] = tmp

        _data[2] = _data[8].also { _data[8] = _data[2] }
        _data[3] = _data[12].also { _data[12] = _data[3] }

        _data[6] = _data[9].also { _data[9] = _data[6] }
        _data[7] = _data[13].also { _data[13] = _data[7] }

        _data[11] = _data[14].also { _data[14] = _data[11] }
    }

    fun translate(x: Float, y: Float, z: Float): Matrix4 {
        _data[0] = 1.0f
        _data[1] = 0.0f
        _data[2] = 0.0f
        _data[3] = 0.0f

        _data[4] = 0.0f
        _data[5] = 1.0f
        _data[6] = 0.0f
        _data[7] = 0.0f

        _data[8] = 0.0f
        _data[9] = 0.0f
        _data[10] = 1.0f
        _data[11] = 0.0f

        _data[12] = x
        _data[13] = y
        _data[14] = z
        _data[15] = 1.0f

        return this
    }

    fun translate(v: Vector3): Matrix4 {
        return translate(v.x, v.y, v.z)
    }

    fun rotate(axis: Vector3, angle: Float): Matrix4 {
        return fromAxisAngle(axis, angle)
    }

    fun rotate(axis: Vector3, angle: Double): Matrix4 {
        return fromAxisAngle(axis, angle.toFloat())
    }

    fun perspective(fov: Double, aspect: Double, near: Double, far: Double): Matrix4 {
        val result = Matrix4()
        val tanHalfFov = kotlin.math.tan(toRadians(fov) / 2.0)

        result._data[0] = 1.0f / (aspect.toFloat() * tanHalfFov.toFloat())
        result._data[5] = 1.0f / tanHalfFov.toFloat()
        result._data[10] = -(far.toFloat() + near.toFloat()) / (far.toFloat() - near.toFloat())
        result._data[11] = -1.0f
        result._data[14] = -(2.0f * far.toFloat() * near.toFloat()) / (far.toFloat() - near.toFloat())

        return result
    }
}

