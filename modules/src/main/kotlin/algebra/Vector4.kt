package algebra

class Vector4 {
    private var data = FloatArray(4) { it -> 0.0f }

    override fun toString(): String {
        return "x: $x, y: $y, z: $z, w: $w"
    }

    constructor(other: Vector4) {
        data = other.data
    }

    constructor(x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f, w: Float = 0.0f) {
        data[0] = x
        data[1] = y
        data[2] = z
        data[3] = w
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

    var w: Float
        get(): Float {
            return data[3]
        }
        set(value) {
            data[3] = value
        }

    var xyzw: Vector4
        get() {
            return Vector4(this.x, this.y, this.z, this.w)
        }
        set(other) {
            data[0] = other.x
            data[1] = other.y
            data[2] = other.z
            data[3] = other.w
        }

    var wzyx: Vector4
        get() {
            return Vector4(this.w, this.z, this.y, this.x)
        }
        set(other) {
            data[0] = other.w
            data[1] = other.z
            data[2] = other.y
            data[3] = other.x
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

    operator fun get(id: Int): Float {
        require(id >= 0 && id < 5)
        return data[id]
    }

    fun dot(other: Vector4): Float {
        return data[0] * other[0] + data[1] * other[1] +
                data[2] * other[2] + data[3] * other[3]
    }
}