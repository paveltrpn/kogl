package image

// Canvas is always have underlying RGBA image.
// ============================================================================
// ======================= Canvas =============================================
// ============================================================================
class Canvas(width: Int, height: Int) : Image() {
    init {
        mWidth = width
        mHeight = height
        mData = ByteArray(mWidth * mHeight * mComponents)
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