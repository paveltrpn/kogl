package algebra

class Vector4 {
    private var _data = FloatArray(4) { it -> 0.0f }

    override fun toString(): String {
        return "x: $x, y: $y, z: $z, w: $w"
    }

    constructor(other: Vector4) {
        _data = other._data
    }

    constructor(x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f, w: Float = 0.0f) {
        _data[0] = x
        _data[1] = y
        _data[2] = z
        _data[3] = w
    }

    val data: FloatArray
        get(): FloatArray {
            return _data
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

    var w: Float
        get(): Float {
            return _data[3]
        }
        set(value) {
            _data[3] = value
        }

    var xyzw: Vector4
        get() {
            return Vector4(this.x, this.y, this.z, this.w)
        }
        set(other) {
            _data[0] = other.x
            _data[1] = other.y
            _data[2] = other.z
            _data[3] = other.w
        }

    var wzyx: Vector4
        get() {
            return Vector4(this.w, this.z, this.y, this.x)
        }
        set(other) {
            _data[0] = other.w
            _data[1] = other.z
            _data[2] = other.y
            _data[3] = other.x
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

    operator fun get(id: Int): Float {
        require(id >= 0 && id < 5)

        return _data[id]
    }

    operator fun plus(other: Vector2): Vector4 {
        x += other.x
        y += other.y

        return this
    }

    operator fun minus(other: Vector2): Vector4 {
        x -= other.x
        y -= other.y

        return this
    }

    operator fun plus(other: Vector3): Vector4 {
        x += other.x
        y += other.y
        z += other.z

        return this
    }

    operator fun minus(other: Vector3): Vector4 {
        x -= other.x
        y -= other.y
        z -= other.z

        return this
    }

    operator fun plus(other: Vector4): Vector4 {
        x += other.x
        y += other.y
        z += other.z
        w += other.w

        return this
    }

    operator fun minus(other: Vector4): Vector4 {
        x -= other.x
        y -= other.y
        z -= other.z
        w -= other.w

        return this
    }


    fun dot(other: Vector4): Float {
        return _data[0] * other[0] + _data[1] * other[1] +
                _data[2] * other[2] + _data[3] * other[3]
    }
}