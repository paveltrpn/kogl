package algebra

class Matrix3 {
    private var _data = FloatArray(9) { it -> 0.0f }

    constructor() {
        idtt()
    }

    constructor(other: Matrix3) {
        _data = other._data.copyOf()
    }

    fun copy(): Matrix3 {
        return Matrix3(this)
    }

    val data: FloatArray
        get(): FloatArray {
            return _data
        }

    // Rows
    var row0: Vector3
        get(): Vector3 {
            return Vector3(_data[0], _data[1], _data[2])
        }
        set(value) {
            _data[0] = value.x
            _data[1] = value.y
            _data[2] = value.z
        }

    var row1: Vector3
        get(): Vector3 {
            return Vector3(_data[3], _data[4], _data[5])
        }
        set(value) {
            _data[3] = value.x
            _data[4] = value.y
            _data[5] = value.z
        }

    var row2: Vector3
        get(): Vector3 {
            return Vector3(_data[6], _data[7], _data[8])
        }
        set(value) {
            _data[6] = value.x
            _data[7] = value.y
            _data[8] = value.z
        }

    // Columnes
    var column0: Vector3
        get(): Vector3 {
            return Vector3(_data[0], _data[3], _data[6])
        }
        set(value) {
            _data[0] = value.x
            _data[3] = value.y
            _data[6] = value.z
        }

    var column1: Vector3
        get(): Vector3 {
            return Vector3(_data[1], _data[4], _data[7])
        }
        set(value) {
            _data[1] = value.x
            _data[4] = value.y
            _data[7] = value.z
        }

    var column2: Vector3
        get(): Vector3 {
            return Vector3(_data[2], _data[5], _data[8])
        }
        set(value) {
            _data[2] = value.x
            _data[5] = value.y
            _data[8] = value.z
        }

    fun zero(): Matrix3 {
        _data.fill(0.0f)

        return this
    }

    fun idtt(): Matrix3 {
        _data[0] = 1.0f
        _data[1] = 0.0f
        _data[2] = 0.0f
        _data[3] = 0.0f
        _data[4] = 1.0f
        _data[5] = 0.0f
        _data[6] = 0.0f
        _data[7] = 0.0f
        _data[8] = 1.0f

        return this
    }

    fun multiply(other: Matrix3): Matrix3 {
        val result = Matrix3()
        for (i in 0..2) {
            for (j in 0..2) {
                for (k in 0..2) {
                    result._data[i * 3 + j] += _data[i * 3 + k] * other._data[k * 3 + j]
                }
            }
        }
        return result
    }

    fun vecMultiply(other: Vector3): Vector3 {
        return Vector3(
            _data[0] * other.x + _data[1] * other.y + _data[2] * other.z,
            _data[3] * other.x + _data[4] * other.y + _data[5] * other.z,
            _data[6] * other.x + _data[7] * other.y + _data[8] * other.z
        )
    }
}