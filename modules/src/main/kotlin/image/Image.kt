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

open class Image(protected val width: Int, protected val height: Int) {
    // 32 bit per pixel RGBA image.
    protected val components = 4
    protected val byteSize = 8
    protected val depth = components * byteSize

    var data = ByteArray(width * height * components)

    fun width(): Int {
        return width
    }

    fun height(): Int {
        return height
    }

    fun depth(): Int {
        return depth
    }

    fun components(): Int {
        return components
    }

    fun data(): ByteArray {
        return data
    }

    fun asPPM(): String {
        var ppmImage = StringBuilder()

        ppmImage.append("P3\n $width $height\n255\n")

        // Write body.
        for (j in 0..<(width * height)) {
            val base = j * components
            val ir = toUnsignedValue(data[base + 0])
            val ig = toUnsignedValue(data[base + 1])
            val ib = toUnsignedValue(data[base + 2])

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