package image

// Canvas is always have underlying RGBA image.
class Canvas(width: Int, height: Int) : Image() {
    init {
        _width = width
        _height = height
        _data = ByteArray(_width * _height * _components)
    }

    fun fillWith(color: Color): Unit {
        for (j in 0..<(width * height)) {
            val base = j * components
            data[base + 0] = color.rb.toByte()
            data[base + 1] = color.gb.toByte()
            data[base + 2] = color.bb.toByte()
            data[base + 3] = color.ab.toByte()
        }
    }
}