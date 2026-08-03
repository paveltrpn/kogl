package algebra

class Vector3 {
    private var data = FloatArray(3) { it -> 0.0f }

    override fun toString(): String {
        return "x: $x, y: $y, z: $z"
    }

    constructor(other: Vector3) {
        data = other.data
    }

    constructor(x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f) {
        data[0] = x
        data[1] = y
        data[2] = z
    }

    fun set(x: Float, y: Float, z: Float) {
        data[0] = x
        data[1] = y
        data[2] = z
    }

    var x: Float
        get(): Float {
            return data[0]
        }
        set(value) {
            data[0] = value
        }

    var y: Float
        get(): Float {
            return data[1]
        }
        set(value) {
            data[1] = value
        }

    var z: Float
        get(): Float {
            return data[2]
        }
        set(value) {
            data[2] = value
        }

    var xyz: Vector3
        get() {
            return Vector3(this.x, this.y, this.z)
        }
        set(other) {
            data[0] = other.x
            data[1] = other.y
            data[2] = other.z
        }

    var zyx: Vector3
        get() {
            return Vector3(this.z, this.y, this.x)
        }
        set(other) {
            data[0] = other.z
            data[1] = other.y
            data[2] = other.x
        }

    var xyzw: Vector4
        get() {
            return Vector4(this.x, this.y, this.z, 0.0f)
        }
        set(other) {
            data[0] = other.x
            data[1] = other.y
            data[2] = other.z
        }

    operator fun get(id: Int): Float {
        require(id >= 0 && id < 4)
        return data[id]
    }

    fun dot(other: Vector4): Float {
        return data[0] * other[0] + data[1] * other[1] +
                data[2] * other[2]
    }

    fun cross(other: Vector3): Vector3 {
        return Vector3(
            data[1] * other.z - data[2] * other.y,
            data[2] * other.x - data[0] * other.z,
            data[0] * other.y - data[1] * other.x
        )
    }

    fun scale(s: Float): Vector3 {
        return Vector3(data[0] * s, data[1] * s, data[2] * s)
    }

    fun length(): Float {
        return kotlin.math.sqrt(data[0] * data[0] + data[1] * data[1] + data[2] * data[2])
    }

    fun normalize(): Vector3 {
        val len = length()
        return if (len > 0) {
            Vector3(data[0] / len, data[1] / len, data[2] / len)
        } else {
            Vector3()
        }
    }

    fun normalizeSelf() {
        val len = length()
        if (len > 0) {
            data[0] /= len
            data[1] /= len
            data[2] /= len
        }
    }
}