package algebra

class Matrix4 {
    private var data = FloatArray(16) { it -> 0.0f }

    constructor(other: Matrix4) {
        data = other.data
    }

    // Rows
    var row0: Vector4
        get(): Vector4 {
            return Vector4(data[0], data[1], data[2], data[3])
        }
        set(value) {
            data[0] = value.x
            data[1] = value.y
            data[2] = value.z
            data[3] = value.w
        }

    var row1: Vector4
        get(): Vector4 {
            return Vector4(data[4], data[5], data[6], data[7])
        }
        set(value) {
            data[4] = value.x
            data[5] = value.y
            data[6] = value.z
            data[7] = value.w
        }

    var row2: Vector4
        get(): Vector4 {
            return Vector4(data[8], data[9], data[10], data[11])
        }
        set(value) {
            data[8] = value.x
            data[9] = value.y
            data[10] = value.z
            data[11] = value.w
        }

    var row3: Vector4
        get(): Vector4 {
            return Vector4(data[12], data[13], data[14], data[15])
        }
        set(value) {
            data[12] = value.x
            data[13] = value.y
            data[14] = value.z
            data[15] = value.w
        }

    // Columnes
    var column0: Vector4
        get(): Vector4 {
            return Vector4(data[0], data[4], data[8], data[12])
        }
        set(value) {
            data[0] = value.x
            data[4] = value.y
            data[8] = value.z
            data[12] = value.w
        }

    var column1: Vector4
        get(): Vector4 {
            return Vector4(data[1], data[5], data[9], data[13])
        }
        set(value) {
            data[1] = value.x
            data[5] = value.y
            data[9] = value.z
            data[13] = value.w
        }

    var column2: Vector4
        get(): Vector4 {
            return Vector4(data[2], data[6], data[10], data[14])
        }
        set(value) {
            data[2] = value.x
            data[6] = value.y
            data[10] = value.z
            data[14] = value.w
        }

    var column3: Vector4
        get(): Vector4 {
            return Vector4(data[3], data[7], data[11], data[14])
        }
        set(value) {
            data[3] = value.x
            data[7] = value.y
            data[11] = value.z
            data[14] = value.w
        }
}