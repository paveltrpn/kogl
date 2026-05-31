package image

// Canvas is always have underlying RGBA image.
class Canvas(width: Int, height: Int) : Image(width, height) {

    fun fillWith(color: Color): Unit {
        for (j in 0..<(width * height)) {
            val base = j * components
            data[base + 0] = color.r
            data[base + 1] = color.g
            data[base + 2] = color.b
            data[base + 3] = color.a
        }
    }
}