package image

import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TgaHeader(imageData: ByteArray) {
    val identsize: UByte
    val colorMapType: UByte
    val imageType: UByte
    val colorMapStart: UShort
    val colorMapLength: UShort
    val colorMapBits: UByte
    val xstart: UShort
    val ystart: UShort
    val width: UShort
    val height: UShort
    val bits: UByte
    val descriptor: UByte

    init {
        val endianness = ByteOrder.LITTLE_ENDIAN

        identsize = imageData[0].toUByte()
        colorMapType = imageData[1].toUByte()
        imageType = imageData[2].toUByte()
        colorMapStart = ByteBuffer.wrap(imageData, 3, 2).order(endianness).short.toUShort()
        colorMapLength = ByteBuffer.wrap(imageData, 5, 2).order(endianness).short.toUShort()
        colorMapBits = imageData[7].toUByte()
        xstart = ByteBuffer.wrap(imageData, 8, 2).order(endianness).short.toUShort()
        ystart = ByteBuffer.wrap(imageData, 10, 2).order(endianness).short.toUShort()
        width = ByteBuffer.wrap(imageData, 12, 2).order(endianness).short.toUShort()
        height = ByteBuffer.wrap(imageData, 14, 2).order(endianness).short.toUShort()
        bits = imageData[16].toUByte()
        descriptor = imageData[17].toUByte()
    }
}

class Tga(path: String) : Image() {
    private var header: TgaHeader? = null

    init {
        val file = File(path)

        val imageData = file.readBytes()

        // println("${data.size} bytes read")

        header = TgaHeader(imageData)

        // println("TGA Header: type=$header!!.imageType, width=$width, height=$height, bits=$header!!.bits")

        if (header!!.bits.toInt() != 32) {
            throw RuntimeException("Unsupported TGA image type, bits is $header!!.bits.toInt()")
        }

        _width = header!!.width.toInt()
        _height = header!!.height.toInt()

        _data = ByteArray(width * height * components)

        val offset = 18 + header!!.identsize.toInt()

        // Load color map if present
        if (header!!.colorMapType.toInt() != 0) {
            val cmapBytes = (header!!.colorMapBits.toInt() + 7) / 8
            val colorMapData = ByteArray(header!!.colorMapLength.toInt() * cmapBytes)
            System.arraycopy(data, offset, colorMapData, 0, colorMapData.size)
        }

        val imageDataOffset =
            if (header!!.colorMapType.toInt() != 0) {
                offset + header!!.colorMapLength.toInt() * ((header!!.colorMapBits.toInt() + 7) / 8)
            } else {
                offset
            }

        when (header!!.imageType.toInt()) {
            1 -> {
                decodeRLE(imageData, imageDataOffset, true)
            }

            2 -> {
                decodeUncompressed(imageData, imageDataOffset, false)
            }

            3 -> {
                decodeGrayscaleRLE(imageData, imageDataOffset)
            }

            9, 10 -> {
                decodeRLE(imageData, imageDataOffset, false)
            }

            else -> {
                throw RuntimeException("Unsupported TGA image type: $header!!.imageType")
            }
        }
    }

    private fun decodeUncompressed(data: ByteArray, offset: Int, flipped: Boolean) {
        val pixelSize = when (header!!.bits.toInt()) {
            24 -> 3
            32 -> 4
            else -> {
                throw RuntimeException("Unsupported bit depth: ${header!!.bits}")
            }
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

                _data[destIdx + 2] = r.toByte()
                _data[destIdx + 1] = g.toByte()
                _data[destIdx + 0] = b.toByte()

                if (components == 4) {
                    _data[destIdx + 3] = if (pixelSize == 4) dest[srcIdx + 3].toByte() else -1
                }
            }
        }
    }

    private fun decodeRLE(data: ByteArray, offset: Int, flipped: Boolean) {
        val pixelSize = when (header!!.bits.toInt()) {
            24 -> 3
            32 -> 4
            else -> throw RuntimeException("Unsupported bit depth: ${header!!.bits}")
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
                        _data[destIdx + 2] = r.toByte()
                        _data[destIdx + 1] = g.toByte()
                        _data[destIdx + 0] = b.toByte()
                        if (components == 4) _data[destIdx + 3] = a.toByte()
                    } else {
                        _data[destIdx + 2] = r.toByte()
                        _data[destIdx + 1] = g.toByte()
                        _data[destIdx + 0] = b.toByte()
                        if (components == 4) _data[destIdx + 3] = a.toByte()
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

                    _data[destIdx + 2] = r.toByte()
                    _data[destIdx + 1] = g.toByte()
                    _data[destIdx + 0] = b.toByte()
                    if (components == 4) _data[destIdx + 3] = a.toByte()

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

                    _data[destIdx + 0] = gray.toByte()
                    _data[destIdx + 1] = gray.toByte()
                    _data[destIdx + 2] = gray.toByte()
                    _data[destIdx + 3] = -1

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

                    _data[destIdx + 0] = gray.toByte()
                    _data[destIdx + 1] = gray.toByte()
                    _data[destIdx + 2] = gray.toByte()
                    _data[destIdx + 3] = -1

                    pixelIdx++
                }
            }
        }
    }
}
