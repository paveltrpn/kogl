package algebra

class Vector2 {
    private var data = FloatArray(2) { it -> 0.0f }

    override fun toString(): String {
        return "x: $x, y: $y"
    }

    constructor(other: Vector2) {
        data = other.data
    }

    constructor(x: Float = 0.0f, y: Float = 0.0f) {
        data[0] = x
        data[1] = y
    }

    fun set(x: Float, y: Float) {
        data[0] = x
        data[1] = y
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

    operator fun get(id: Int): Float {
        require(id >= 0 && id < 3)
        return data[id]
    }

    fun dot(other: Vector4): Float {
        return data[0] * other[0] + data[1] * other[1]
    }
}