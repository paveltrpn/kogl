package algebra

class Matrix4 {
    private var data = FloatArray(16) { it -> 0.0f }

    constructor() {

    }

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

    fun multiply(other: Matrix4): Matrix4 {
        val result = Matrix4()
        for (i in 0..3) {
            for (j in 0..3) {
                for (k in 0..3) {
                    result.data[i * 4 + j] += data[i * 4 + k] * other.data[k * 4 + j]
                }
            }
        }
        return result
    }

    fun vecMultiply(other: Vector4): Vector4 {
        return Vector4(
            data[0] * other.x + data[1] * other.y + data[2] * other.z + data[3] * other.w,
            data[4] * other.x + data[5] * other.y + data[6] * other.z + data[7] * other.w,
            data[8] * other.x + data[9] * other.y + data[10] * other.z + data[11] * other.w,
            data[12] * other.x + data[13] * other.y + data[14] * other.z + data[15] * other.w
        )
    }

    fun perspective(fov: Float, aspect: Float, near: Float, far: Float): Matrix4 {
        val result = Matrix4()
        val tanHalfFov = kotlin.math.tan(toRadians(fov) / 2.0f)

        result.data[0] = 1.0f / (aspect * tanHalfFov)
        result.data[5] = 1.0f / tanHalfFov
        result.data[10] = -(far + near) / (far - near)
        result.data[11] = -1.0f
        result.data[14] = -(2.0f * far * near) / (far - near)

        return result
    }

    fun ortho(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Matrix4 {
        val result = Matrix4()

        result.data[0] = 2.0f / (right - left)
        result.data[5] = 2.0f / (top - bottom)
        result.data[10] = -2.0f / (far - near)

        result.data[12] = -(right + left) / (right - left)
        result.data[13] = -(top + bottom) / (top - bottom)
        result.data[14] = -(far + near) / (far - near)

        return result
    }
}