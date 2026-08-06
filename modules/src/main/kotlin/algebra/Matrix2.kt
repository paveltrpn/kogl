package algebra

class Matrix2 {
    private var _data = FloatArray(4) { it -> 0.0f }

    constructor() {

    }

    constructor(other: Matrix2) {
        _data = other._data
    }

    constructor(a00: Float = 0.0f, a01: Float = 0.0f, a10: Float = 0.0f, a11: Float = 0.0f) {
        _data[0] = a00
        _data[1] = a01
        _data[2] = a10
        _data[3] = a11
    }

    val data: FloatArray
        get(): FloatArray {
            return _data
        }
    
    var row0: Vector2
        get(): Vector2 {
            return Vector2(_data[0], _data[1])
        }
        set(value) {
            _data[0] = value.x
            _data[1] = value.y
        }

    var row1: Vector2
        get(): Vector2 {
            return Vector2(_data[2], _data[3])
        }
        set(value) {
            _data[2] = value.x
            _data[3] = value.y
        }

    var column0: Vector2
        get(): Vector2 {
            return Vector2(_data[0], _data[2])
        }
        set(value) {
            _data[0] = value.x
            _data[2] = value.y
        }

    var column1: Vector2
        get() = Vector2(_data[1], _data[3])
        set(value) {
            _data[1] = value.x
            _data[3] = value.y
        }

    fun multiply(other: Matrix2): Matrix2 {
        val result = Matrix2()
        for (i in 0..1) {
            for (j in 0..1) {
                for (k in 0..1) {
                    result._data[i * 2 + j] += _data[i * 2 + k] * other._data[k * 2 + j]
                }
            }
        }
        return result
    }

    fun vecMultiply(other: Vector2): Vector2 {
        return Vector2(
            _data[0] * other.x + _data[1] * other.y,
            _data[2] * other.x + _data[3] * other.y
        )
    }
}