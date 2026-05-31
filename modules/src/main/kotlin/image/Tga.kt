package image

import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class TgaHeader(
    val identsize: UByte,
    val colorMapType: UByte,
    val imageType: UByte,
    val colorMapStart: UShort,
    val colorMapLength: UShort,
    val colorMapBits: UByte,
    val xstart: UShort,
    val ystart: UShort,
    val width: UShort,
    val height: UShort,
    val bits: UByte,
    val descriptor: UByte
)

fun byteArrayToInt(byteArray: ByteArray): Int {
    var result = 0
    for (i in byteArray.indices) {
        result = result or (byteArray[i].toInt() and 0xFF shl (8 * (byteArray.size - 1 - i)))
    }
    return result
}

fun toInt32(bytes: ByteArray, index: Int): Int {
    require(bytes.size == 4) { "length must be 4, got: ${bytes.size}" }
    return ByteBuffer.wrap(bytes, index, 4).order(ByteOrder.LITTLE_ENDIAN).int
}

class Tga(val path: String) {
    private var data: ByteArray? = null
    private var width: Int = 0
    private var height: Int = 0
    private var bits: Int = 0
    private var imageType: Int = 0

    init {
        try {
            //
            val file = File(path)

            //
            data = file.readBytes()

            println("${data?.size} bytes read")

            if (data != null) {
                // Get image parameters
                imageType = data!![2].toInt()
                width = ByteBuffer.wrap(data, 12, 2).order(ByteOrder.LITTLE_ENDIAN).short.toInt()
                height = ByteBuffer.wrap(data, 14, 2).order(ByteOrder.LITTLE_ENDIAN).short.toInt()
                bits = data!![16].toInt()
            }

            println("image type: $imageType; width: $width; height: $height; bits: $bits")

        } catch (e: Exception) {
            println("exception is: ${e.message}")
        }
    }
}
