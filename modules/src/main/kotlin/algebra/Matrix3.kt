package algebra

class Matrix3 {
    private var data = FloatArray(9) { it -> 0.0f }

    constructor(other: Matrix3) {
        data = other.data
    }

    // Rows
    var row0: Vector3
        get(): Vector3 {
            return Vector3(data[0], data[1], data[2])
        }
        set(value) {
            data[0] = value.x
            data[1] = value.y
            data[2] = value.z
        }

    var row1: Vector3
        get(): Vector3 {
            return Vector3(data[3], data[4], data[5])
        }
        set(value) {
            data[3] = value.x
            data[4] = value.y
            data[5] = value.z
        }

    var row2: Vector3
        get(): Vector3 {
            return Vector3(data[6], data[7], data[8])
        }
        set(value) {
            data[6] = value.x
            data[7] = value.y
            data[8] = value.z
        }

    // Columnes
    var column0: Vector3
        get(): Vector3 {
            return Vector3(data[0], data[3], data[6])
        }
        set(value) {
            data[0] = value.x
            data[3] = value.y
            data[6] = value.z
        }

    var column1: Vector3
        get(): Vector3 {
            return Vector3(data[1], data[4], data[7])
        }
        set(value) {
            data[1] = value.x
            data[4] = value.y
            data[7] = value.z
        }

    var column2: Vector3
        get(): Vector3 {
            return Vector3(data[2], data[5], data[8])
        }
        set(value) {
            data[2] = value.x
            data[5] = value.y
            data[8] = value.z
        }
}