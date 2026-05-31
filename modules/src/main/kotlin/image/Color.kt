package image

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

    var r: Byte
        get() : Byte {
            return data[0]
        }
        set(value) {
            data[0] = value
        }

    var g: Byte
        get() : Byte {
            return data[1]
        }
        set(value) {
            data[1] = value
        }

    var b: Byte
        get() : Byte {
            return data[2]
        }
        set(value) {
            data[2] = value
        }

    var a: Byte
        get() : Byte {
            return data[3]
        }
        set(value) {
            data[3] = value
        }

    fun setColor(r: Int, g: Int, b: Int, a: Int): Unit {
        data[0] = r.toByte()
        data[1] = g.toByte()
        data[2] = b.toByte()
        data[3] = a.toByte()
    }

    fun fromHexString(color: String) {
        if (color.length != 9 || !color.startsWith("#")) {
            throw IllegalArgumentException("Color string must be exactly 9 characters long and start with '#', got: $this")
        }

        val hex = color.substring(1) // Remove '#'
        if (hex.length != 8 || !hex.all { it.isHexDigit() }) {
            throw IllegalArgumentException("Color string must contain exactly 8 valid hexadecimal digits after '#', got: $hex")
        }

        data = ByteArray(4) { i ->
            val start = i * 2
            val hexPair = hex.substring(start, start + 2)
            hexPair.toInt(radix = 16).toByte()
        }
    }
}