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

class Tga(path: String) : Image(0, 0) {
    private var header: TgaHeader? = null
    private var idExtensionLength: Int = 0
    private var colorMapData: ByteArray? = null

    init {
        val file = File(path)

        val data = file.readBytes()

        // println("${data.size} bytes read")

        // Parse header.
        val identsize = data[0].toUByte()
        val colorMapType = data[1].toUByte()
        val imageType = data[2].toUByte()
        val colorMapStart = ByteBuffer.wrap(data, 3, 2).order(ByteOrder.LITTLE_ENDIAN).short.toUShort()
        val colorMapLength = ByteBuffer.wrap(data, 5, 2).order(ByteOrder.LITTLE_ENDIAN).short.toUShort()
        val colorMapBits = data[7].toUByte()
        val xstart = ByteBuffer.wrap(data, 8, 2).order(ByteOrder.LITTLE_ENDIAN).short.toUShort()
        val ystart = ByteBuffer.wrap(data, 10, 2).order(ByteOrder.LITTLE_ENDIAN).short.toUShort()

        val width = ByteBuffer.wrap(data, 12, 2).order(ByteOrder.LITTLE_ENDIAN).short.toUShort()
        val height = ByteBuffer.wrap(data, 14, 2).order(ByteOrder.LITTLE_ENDIAN).short.toUShort()

        val bits = data[16].toUByte()
        val descriptor = data[17].toUByte()

        header = TgaHeader(
            identsize,
            colorMapType,
            imageType,
            colorMapStart,
            colorMapLength,
            colorMapBits,
            xstart,
            ystart,
            width,
            height,
            bits,
            descriptor
        )
        idExtensionLength = identsize.toInt()

        println("TGA Header: type=$imageType, width=$width, height=$height, bits=$bits")

        val components = when (bits.toInt()) {
            24 -> 3
            32 -> 4
            else -> 4
        }
        this.data = ByteArray(this.width * this.height * this.components)

        val offset = 18 + identsize.toInt()

        // Load color map if present
        if (colorMapType.toInt() != 0) {
            val cmapBytes = (colorMapBits.toInt() + 7) / 8
            colorMapData = ByteArray(colorMapLength.toInt() * cmapBytes)
            System.arraycopy(data, offset, colorMapData, 0, colorMapData.size)
        }

        val imageDataOffset =
            if (colorMapType.toInt() != 0) offset + colorMapLength.toInt() * ((colorMapBits.toInt() + 7) / 8) else offset

        when (imageType.toInt()) {
            1 -> decodeRunLengthEncoded(data, imageDataOffset, true)
            2 -> decodeUncompressed(data, imageDataOffset, false)
            3 -> decodeGrayscaleRLE(data, imageDataOffset)
            9, 10 -> decodeRunLengthEncoded(data, imageDataOffset, false)
            else -> throw IllegalArgumentException("Unsupported TGA image type: $imageType")
        }
    }

    private fun load(path: String) {

    }

    private fun decodeUncompressed(data: ByteArray, offset: Int, flipped: Boolean) {
        val pixelSize = when (header!!.bits.toInt()) {
            24 -> 3
            32 -> 4
            else -> throw IllegalArgumentException("Unsupported bit depth: ${header!!.bits}")
        }

        val dest = if (flipped) ByteArray(data.size) else data

        for (y in 0 until height) {
            val srcY = if (flipped) height - 1 - y else y
            val srcRow = offset + srcY * width * pixelSize

            for (x in 0 until width) {
                val srcIdx = srcRow + x * pixelSize
                val destIdx = (y * width + x) * components

                val b = dest[srcIdx].toInt() and 0xFF
                val g = dest[srcIdx + 1].toInt() and 0xFF
                val r = dest[srcIdx + 2].toInt() and 0xFF

                this.data[destIdx + 2] = r.toByte()
                this.data[destIdx + 1] = g.toByte()
                this.data[destIdx + 0] = b.toByte()

                if (components == 4) {
                    this.data[destIdx + 3] = if (pixelSize == 4) dest[srcIdx + 3].toByte() else -1
                }
            }
        }
    }

    private fun decodeRunLengthEncoded(data: ByteArray, offset: Int, flipped: Boolean) {
        val pixelSize = when (header!!.bits.toInt()) {
            24 -> 3
            32 -> 4
            else -> throw IllegalArgumentException("Unsupported bit depth: ${header!!.bits}")
        }

        var pixelIdx = 0
        var dataOffset = offset
        val totalPixels = width * height

        while (pixelIdx < totalPixels) {
            val packetHeader = data[dataOffset].toInt() and 0xFF
            dataOffset++

            val isRLE = (packetHeader and 0x80) != 0
            val count = (packetHeader and 0x7F) + 1

            val packetPixelSize = if (isRLE) pixelSize else count * pixelSize

            if (isRLE) {
                val b = data[dataOffset].toInt() and 0xFF
                val g = data[dataOffset + 1].toInt() and 0xFF
                val r = data[dataOffset + 2].toInt() and 0xFF
                val a = if (pixelSize == 4) data[dataOffset + 3].toInt() and 0xFF else 255
                dataOffset += pixelSize

                for (i in 0 until count) {
                    if (pixelIdx >= totalPixels) break

                    val y = pixelIdx / width
                    val x = pixelIdx % width
                    val destIdx = (y * width + x) * components

                    if (flipped) {
                        this.data[destIdx + 2] = r.toByte()
                        this.data[destIdx + 1] = g.toByte()
                        this.data[destIdx + 0] = b.toByte()
                        if (components == 4) this.data[destIdx + 3] = a.toByte()
                    } else {
                        this.data[destIdx + 2] = r.toByte()
                        this.data[destIdx + 1] = g.toByte()
                        this.data[destIdx + 0] = b.toByte()
                        if (components == 4) this.data[destIdx + 3] = a.toByte()
                    }
                    pixelIdx++
                }
            } else {
                for (i in 0 until count) {
                    if (pixelIdx >= totalPixels) break

                    val b = data[dataOffset].toInt() and 0xFF
                    val g = data[dataOffset + 1].toInt() and 0xFF
                    val r = data[dataOffset + 2].toInt() and 0xFF
                    val a = if (pixelSize == 4) data[dataOffset + 3].toInt() and 0xFF else 255
                    dataOffset += pixelSize

                    val y = pixelIdx / width
                    val x = pixelIdx % width
                    val destIdx = (y * width + x) * components

                    this.data[destIdx + 2] = r.toByte()
                    this.data[destIdx + 1] = g.toByte()
                    this.data[destIdx + 0] = b.toByte()
                    if (components == 4) this.data[destIdx + 3] = a.toByte()

                    pixelIdx++
                }
            }
        }
    }

    private fun decodeGrayscaleRLE(data: ByteArray, offset: Int) {
        var pixelIdx = 0
        var dataOffset = offset
        val totalPixels = width * height

        while (pixelIdx < totalPixels) {
            val packetHeader = data[dataOffset].toInt() and 0xFF
            dataOffset++

            val isRLE = (packetHeader and 0x80) != 0
            val count = (packetHeader and 0x7F) + 1

            if (isRLE) {
                val gray = data[dataOffset].toInt() and 0xFF
                dataOffset++

                for (i in 0 until count) {
                    if (pixelIdx >= totalPixels) break

                    val y = pixelIdx / width
                    val x = pixelIdx % width
                    val destIdx = (y * width + x) * components

                    this.data[destIdx + 0] = gray.toByte()
                    this.data[destIdx + 1] = gray.toByte()
                    this.data[destIdx + 2] = gray.toByte()
                    this.data[destIdx + 3] = -1

                    pixelIdx++
                }
            } else {
                for (i in 0 until count) {
                    if (pixelIdx >= totalPixels) break

                    val gray = data[dataOffset].toInt() and 0xFF
                    dataOffset++

                    val y = pixelIdx / width
                    val x = pixelIdx % width
                    val destIdx = (y * width + x) * components

                    this.data[destIdx + 0] = gray.toByte()
                    this.data[destIdx + 1] = gray.toByte()
                    this.data[destIdx + 2] = gray.toByte()
                    this.data[destIdx + 3] = -1

                    pixelIdx++
                }
            }
        }
    }
}
