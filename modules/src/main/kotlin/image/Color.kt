package image

import algebra.*

fun Char.isHexDigit(): Boolean = this in '0'..'9' || this in 'a'..'f' || this in 'A'..'F'

class Color() {
    var data = ByteArray(4)

    constructor(r: Int, g: Int, b: Int, a: Int) : this() {
        data[0] = r.toByte()
        data[1] = g.toByte()
        data[2] = b.toByte()
        data[3] = a.toByte()
    }

    constructor(color: String) : this() {
        fromHexString(color)
    }

    var rb: Byte
        get() : Byte {
            return data[0]
        }
        set(value) {
            data[0] = value
        }

    var gb: Byte
        get() : Byte {
            return data[1]
        }
        set(value) {
            data[1] = value
        }

    var bb: Byte
        get() : Byte {
            return data[2]
        }
        set(value) {
            data[2] = value
        }

    var ab: Byte
        get() : Byte {
            return data[3]
        }
        set(value) {
            data[3] = value
        }

    val rf: Float
        get() : Float {
            return data[0].toFloat() / 255.0f
        }


    val gf: Float
        get() : Float {
            return data[1].toFloat() / 255.0f
        }


    val bf: Float
        get() : Float {
            return data[2].toFloat() / 255.0f
        }


    val af: Float
        get() : Float {
            return data[3].toFloat() / 255.0f
        }

    val asVec4: Vector4
        get(): Vector4 {
            return Vector4(rf, gf, bf, af)
        }

    val asVec3: Vector4
        get(): Vector4 {
            return Vector4(rf, gf, bf)
        }

    fun setColor(r: Int, g: Int, b: Int, a: Int): Unit {
        data[0] = r.toByte()
        data[1] = g.toByte()
        data[2] = b.toByte()
        data[3] = a.toByte()
    }

    fun fromHexString(color: String) {
        if (color.length != 9 || !color.startsWith("#")) {
            throw RuntimeException("Color string must be exactly 9 characters long and start with '#', got: $color")
        }

        val hex = color.substring(1) // Remove '#'
        if (hex.length != 8 || !hex.all { it.isHexDigit() }) {
            throw RuntimeException("Color string must contain exactly 8 valid hexadecimal digits after '#', got: $hex")
        }

        data = ByteArray(4) { i ->
            val start = i * 2
            val hexPair = hex.substring(start, start + 2)
            hexPair.toInt(radix = 16).toByte()
        }
    }
}