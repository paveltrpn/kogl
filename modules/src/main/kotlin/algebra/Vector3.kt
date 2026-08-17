package algebra

class Vector3 {
    private var _data = FloatArray(3) { it -> 0.0f }

    override fun toString(): String {
        return "x: $x, y: $y, z: $z"
    }

    constructor(other: Vector3) {
        _data = other._data
    }

    constructor(x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f) {
        _data[0] = x
        _data[1] = y
        _data[2] = z
    }

    val data: FloatArray
        get(): FloatArray {
            return _data
        }

    fun set(x: Float, y: Float, z: Float) {
        _data[0] = x
        _data[1] = y
        _data[2] = z
    }

    var x: Float
        get(): Float {
            return _data[0]
        }
        set(value) {
            _data[0] = value
        }

    var y: Float
        get(): Float {
            return _data[1]
        }
        set(value) {
            _data[1] = value
        }

    var z: Float
        get(): Float {
            return _data[2]
        }
        set(value) {
            _data[2] = value
        }

    var xyz: Vector3
        get() {
            return Vector3(this.x, this.y, this.z)
        }
        set(other) {
            _data[0] = other.x
            _data[1] = other.y
            _data[2] = other.z
        }

    var zyx: Vector3
        get() {
            return Vector3(this.z, this.y, this.x)
        }
        set(other) {
            _data[0] = other.z
            _data[1] = other.y
            _data[2] = other.x
        }

    var xyzw: Vector4
        get() {
            return Vector4(this.x, this.y, this.z, 0.0f)
        }
        set(other) {
            _data[0] = other.x
            _data[1] = other.y
            _data[2] = other.z
        }

    operator fun get(id: Int): Float {
        require(id >= 0 && id < 4)

        return _data[id]
    }

    operator fun plus(other: Vector3): Vector3 {
        x += other.x
        y += other.y
        z += other.z

        return this
    }

    operator fun minus(other: Vector3): Vector3 {
        x -= other.x
        y -= other.y
        z -= other.z

        return this
    }

    operator fun plus(other: Vector4): Vector3 {
        x += other.x
        y += other.y
        z += other.z

        return this
    }

    operator fun minus(other: Vector4): Vector3 {
        x -= other.x
        y -= other.y
        z -= other.z

        return this
    }

    fun dot(other: Vector4): Float {
        return _data[0] * other[0] + _data[1] * other[1] +
                _data[2] * other[2]
    }

    fun cross(other: Vector3): Vector3 {
        return Vector3(
            _data[1] * other.z - _data[2] * other.y,
            _data[2] * other.x - _data[0] * other.z,
            _data[0] * other.y - _data[1] * other.x
        )
    }

    fun scale(s: Float): Vector3 {
        return Vector3(_data[0] * s, _data[1] * s, _data[2] * s)
    }

    fun length(): Float {
        return kotlin.math.sqrt(_data[0] * _data[0] + _data[1] * _data[1] + _data[2] * _data[2])
    }

    fun normalize(): Vector3 {
        val len = length()
        return if (len > 0) {
            Vector3(_data[0] / len, _data[1] / len, _data[2] / len)
        } else {
            Vector3()
        }
    }

    fun inversed(): Vector3 {
        return Vector3(-x, -y, -z)
    }

    fun normalizeSelf() {
        val len = length()
        if (len > 0) {
            _data[0] /= len
            _data[1] /= len
            _data[2] /= len
        }
    }
}