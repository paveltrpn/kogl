package image

import java.io.*

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
// ============================================================================
// ======================= Image ==============================================
// ============================================================================
abstract class Image {
    protected var mWidth: Int
    protected var mHeight: Int
    protected var mData: ByteArray

    protected val mComponents = 4
    protected val mBits = 32

    init {
        mWidth = 0
        mHeight = 0
        mData = byteArrayOf()
    }

    val width: Int
        get() {
            return mWidth
        }

    val height: Int
        get() {
            return mHeight
        }

    val components: Int
        get() {
            return mComponents
        }

    val bits: Int
        get() {
            return mBits
        }

    val data: ByteArray
        get() {
            return mData
        }

    fun asPPM(): String {
        var ppmImage = StringBuilder()

        ppmImage.append("P3\n $width $height\n255\n")

        // Write body.
        for (j in 0..<(width * height)) {
            val base = j * components
            val ir = toUnsignedValue(mData[base + 0])
            val ig = toUnsignedValue(mData[base + 1])
            val ib = toUnsignedValue(mData[base + 2])

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