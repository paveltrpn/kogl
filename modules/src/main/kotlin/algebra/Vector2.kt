package algebra

class Vector2 {
    private var _data = FloatArray(2) { it -> 0.0f }

    override fun toString(): String {
        return "x: $x, y: $y"
    }

    constructor(other: Vector2) {
        _data = other._data
    }

    constructor(x: Float = 0.0f, y: Float = 0.0f) {
        _data[0] = x
        _data[1] = y
    }

    val data: FloatArray
        get(): FloatArray {
            return _data
        }

    fun set(x: Float, y: Float) {
        _data[0] = x
        _data[1] = y
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

    operator fun get(id: Int): Float {
        require(id >= 0 && id < 3)
        return _data[id]
    }

    fun dot(other: Vector4): Float {
        return _data[0] * other[0] + _data[1] * other[1]
    }
}