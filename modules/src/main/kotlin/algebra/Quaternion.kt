package algebra

// ============================================================================
// ======================= Quaternion =========================================
// ============================================================================
class Quaternion(
    x: Float = 0.0f,
    y: Float = 0.0f,
    z: Float = 0.0f,
    w: Float = 1.0f
) {
    private var _x = x
    private var _y = y
    private var _z = z
    private var _w = w

    var x: Float
        get(): Float {
            return _x
        }
        set(value: Float) {
            _x = value
        }

    var y: Float
        get(): Float {
            return _y
        }
        set(value: Float) {
            _y = value
        }

    var z: Float
        get(): Float {
            return _z
        }
        set(value: Float) {
            _z = value
        }

    var w: Float
        get(): Float {
            return _w
        }
        set(value: Float) {
            _w = value
        }

    operator fun times(other: Quaternion): Quaternion {
        return Quaternion(
            _w * other._x + _x * other._w + _y * other._z - _z * other._y,
            _w * other._y - _x * other._z + _y * other._w + _z * other._x,
            _w * other._z + _x * other._y - _y * other._x + _z * other._w,
            _w * other._w - _x * other._x - _y * other._y - _z * other._z
        )
    }

    fun rotate(other: Vector3): Vector3 {
        val vector = Vector3(_x, _y, _z)
        val t = vector.cross(other).scale(2.0f)
        return other.added(t.scale(_w)).added(vector.cross(t))
    }

    fun conjugate(): Quaternion {
        return Quaternion(-_x, -_y, -_z, _w)
    }

    fun inverse(): Quaternion {
        val lengthSquared = _x * _x + _y * _y + _z * _z + _w * _w
        if (lengthSquared == 0.0f) {
            return Quaternion()
        }
        return Quaternion(
            -_x / lengthSquared,
            -_y / lengthSquared,
            -_z / lengthSquared,
            _w / lengthSquared
        )
    }

    fun normalized(): Quaternion {
        val length = kotlin.math.sqrt(_x * _x + _y * _y + _z * _z + _w * _w)
        if (length == 0.0f) {
            return Quaternion()
        }
        return Quaternion(_x / length, _y / length, _z / length, _w / length)
    }

    fun normalizeSelf(): Unit {
        val length = kotlin.math.sqrt(_x * _x + _y * _y + _z * _z + _w * _w)
        if (length == 0.0f) {
            _x = 0.0f
            _y = 0.0f
            _z = 0.0f
            _w = 1.0f
            return
        }
        _x /= length
        _y /= length
        _z /= length
        _w /= length
    }

    fun toMatrix3(): Matrix3 {
        val kx = _x + _x
        val ky = _y + _y
        val kz = _z + _z
        val xx = kx * _x
        val yy = ky * _y
        val zz = kz * _z
        val xy = kx * _y
        val xz = kx * _z
        val yz = ky * _z
        val sx = kx * _w
        val sy = ky * _w
        val sz = kz * _w

        return Matrix3(
            Vector3(1.0f - yy - zz, xy + sz, xz - sy),
            Vector3(xy - sz, 1.0f - xx - zz, yz + sx),
            Vector3(xz + sy, yz - sx, 1.0f - xx - yy)
        )
    }
}
