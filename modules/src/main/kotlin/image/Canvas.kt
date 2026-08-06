package image

// Canvas is always have underlying RGBA image.
class Canvas(width: Int, height: Int) : Image(width, height) {

    fun fillWith(color: Color): Unit {
        for (j in 0..<(width * height)) {
            val base = j * components
            data[base + 0] = color.rb
            data[base + 1] = color.gb
            data[base + 2] = color.bb
            data[base + 3] = color.ab
        }
    }
}