package algebra

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Matrix4 {
    private var _data = FloatArray(16) { it -> 0.0f }

    constructor() {
        idtt()
    }

    constructor(other: Matrix4) {
        _data = other._data
    }

    constructor(other: FloatArray) {
        _data = other
    }

    val data: FloatArray
        get(): FloatArray {
            return _data
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

    fun zero(): Matrix4 {
        _data.fill(0.0f)
        return this
    }

    fun idtt(): Matrix4 {
        _data[0] = 1.0f;
        _data[1] = 0.0f;
        _data[2] = 0.0f;
        _data[3] = 0.0f;
        _data[4] = 0.0f;
        _data[5] = 1.0f
        _data[6] = 0.0f;
        _data[7] = 0.0f;
        _data[8] = 0.0f;
        _data[9] = 0.0f;
        _data[10] = 1.0f
        _data[11] = 0.0f;
        _data[12] = 0.0f;
        _data[13] = 0.0f;
        _data[14] = 0.0f;
        _data[15] = 1.0f

        return this
    }

    fun multiply(other: Matrix4): Matrix4 {
        val result = Matrix4().zero()

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
        var rx = other.x * _data[0] + other.y * _data[1] + other.z * _data[2] + _data[3];
        var ry = other.x * _data[4] + other.y * _data[5] + other.z * _data[6] + _data[7];
        var rz = other.x * _data[8] + other.y * _data[9] + other.z * _data[10] + _data[11];
        val w = other.x * _data[12] + other.y * _data[13] + other.z * _data[14] + _data[15];

        // Normalize if w is different from 1.0 (convert from homogeneous to Cartesian
        // coordinates).
        if (w != 1.0f) {
            rx /= w;
            ry /= w;
            rz /= w;
        }

        return Vector3(rx, ry, rz)
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

    fun fromAxisAngle(ax: Vector3, phi: Float): Matrix4 {
        val cosphi = kotlin.math.cos(toRadians(phi));
        val sinphi = kotlin.math.sin(toRadians(phi));
        val vxvy = ax.x * ax.y;
        val vxvz = ax.x * ax.z;
        val vyvz = ax.y * ax.z;
        val vx = ax.x;
        val vy = ax.y;
        val vz = ax.z;

        _data[0] = cosphi + (1.0f - cosphi) * vx * vx;
        _data[1] = (1.0f - cosphi) * vxvy - sinphi * vz;
        _data[2] = (1.0f - cosphi) * vxvz + sinphi * vy;
        _data[3] = 0.0f;

        _data[4] = (1.0f - cosphi) * vxvy + sinphi * vz;
        _data[5] = cosphi + (1.0f - cosphi) * vy * vy;
        _data[6] = (1.0f - cosphi) * vyvz - sinphi * vx;
        _data[7] = 0.0f;

        _data[8] = (1.0f - cosphi) * vxvz - sinphi * vy;
        _data[9] = (1.0f - cosphi) * vyvz + sinphi * vx;
        _data[10] = cosphi + (1.0f - cosphi) * vz * vz;
        _data[11] = 0.0f;

        _data[12] = 0.0f;
        _data[13] = 0.0f;
        _data[14] = 0.0f;
        _data[15] = 1.0f;

        return this
    }

    fun fromEuler(x: Float, y: Float, z: Float): Matrix4 {
        val cx = kotlin.math.cos(toRadians(x))
        val sx = kotlin.math.sin(toRadians(x))
        val cy = kotlin.math.cos(toRadians(y))
        val sy = kotlin.math.sin(toRadians(y))
        val cz = kotlin.math.cos(toRadians(z))
        val sz = kotlin.math.sin(toRadians(z))

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
        _data[3] = x

        _data[4] = 0.0f
        _data[5] = 1.0f
        _data[6] = 0.0f
        _data[7] = y

        _data[8] = 0.0f
        _data[9] = 0.0f
        _data[10] = 1.0f
        _data[11] = z

        _data[12] = 0.0f
        _data[13] = 0.0f
        _data[14] = 0.0f
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

    fun rotate(axis: Vector3, angle: Float): Matrix4 {
        return fromAxisAngle(axis, angle)
    }

    fun rotate(axis: Vector3, angle: Double): Matrix4 {
        return fromAxisAngle(axis, angle.toFloat())
    }
}

