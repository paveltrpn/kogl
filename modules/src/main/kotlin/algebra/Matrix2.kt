package algebra

class Matrix2 {
    private var data = FloatArray(4) { it -> 0.0f }

    constructor(other: Matrix2) {
        data = other.data
    }

    constructor(a00: Float = 0.0f, a01: Float = 0.0f, a10: Float = 0.0f, a11: Float = 0.0f) {
        data[0] = a00
        data[1] = a01
        data[2] = a10
        data[3] = a11
    }

    var row0: Vector2
        get(): Vector2 {
            return Vector2(data[0], data[1])
        }
        set(value) {
            data[0] = value.x
            data[1] = value.y
        }

    var row1: Vector2
        get(): Vector2 {
            return Vector2(data[2], data[3])
        }
        set(value) {
            data[2] = value.x
            data[3] = value.y
        }

    var column0: Vector2
        get(): Vector2 {
            return Vector2(data[0], data[2])
        }
        set(value) {
            data[0] = value.x
            data[2] = value.y
        }

    var column1: Vector2
        get() = Vector2(data[1], data[3])
        set(value) {
            data[1] = value.x
            data[3] = value.y
        }
}