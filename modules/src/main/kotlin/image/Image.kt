package image

import java.io.File

fun toUnsignedValue(value: Byte): Int {
    return value.toInt() and 0xFF
}

fun toSignedByte(value: Int): Byte {
    require(value in 0..255) {
        throw IllegalArgumentException("Value must be in range [0, 255], got: $value")
    }

    return value.toByte()
}

// 32 bit per pixel RGBA image.
abstract class Image {
    protected var _width: Int
    protected var _height: Int
    protected var _data: ByteArray

    protected val _components = 4
    protected val _bits = 32

    init {
        _width = 0
        _height = 0
        _data = byteArrayOf()
    }

    val width: Int
        get() {
            return _width
        }

    val height: Int
        get() {
            return _height
        }

    val components: Int
        get() {
            return _components
        }

    val bits: Int
        get() {
            return _bits
        }

    val data: ByteArray
        get() {
            return _data
        }

    fun asPPM(): String {
        var ppmImage = StringBuilder()

        ppmImage.append("P3\n $width $height\n255\n")

        // Write body.
        for (j in 0..<(width * height)) {
            val base = j * components
            val ir = toUnsignedValue(_data[base + 0])
            val ig = toUnsignedValue(_data[base + 1])
            val ib = toUnsignedValue(_data[base + 2])

            // NOTE: no alpha in PPM.
            //val ia = toUnsignedValue(data[base + 3])

            ppmImage.append("$ir $ig $ib\n")
        }

        return ppmImage.toString()
    }

    fun save(path: String): Unit {
        try {
            val content = asPPM()
            File(path).writeText(content)
            println("Successfully wrote to $path")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}