package ui

import algebra.*
import kotlin.Float

class Label : UiComponent() {
    private var _text: String = ""

    // Size of "Letters buffer" - this is the number of all characters
    // from all draw() calls that can one instance of this class operates
    private val VERTICIES_PER_QUAD = 6
    private val MAX_LETTERS_COUNT = 64

    private val _letterQuadsVertices = FloatArray(MAX_LETTERS_COUNT * VERTICIES_PER_QUAD * 3) { 0.0f }
    private val _letterQuadsTxcoords = FloatArray(MAX_LETTERS_COUNT * VERTICIES_PER_QUAD * 2) { 0.0f }

    private var _letterScale = 1.0f
    private var _letterSpace = 0.0f
    private var _position: Vector3 = Vector3()

    // Формат шрифта - изображение с нулём сверху слева,
    // 32 столбца на 8 строк символов, первый символ - 32 ("пробел").
    // Размер ячейки с символом получается делением горизонтального и вертикального
    // размера изображения на количество столбцов и строк соответственно.
    private val _fontColumnCount = 32  // Количество столбцов символов в шрифте
    private val _fontRowCount = 8      // Количество строк символов в шрифте

    var text: String
        get():String {
            return _text
        }
        set(value) {
            _text = value
        }

    // Colorf color_{}

    var letterScale: Float
        get():Float {
            return _letterScale
        }
        set(value) {
            _letterScale = value
        }

    var letterSpace: Float
        get():Float {
            return _letterSpace
        }
        set(value) {
            _letterSpace = value
        }

//    fun setColor( const Colorf &value ) : Unit{
//        color_ = value
//    }

    var position: Vector3
        get() :Vector3 {
            return _position
        }
        set(value: Vector3) {
            _position = value
        }

    override fun draw(): Unit {
        val GLYPH_WIDTH = 0.4f
        val GLYPH_HEIGHT = 1.55f

        val letterWidth = GLYPH_WIDTH * _letterScale
        val letterHeight = GLYPH_HEIGHT * _letterScale

        // Размер ячейки с символом в долях текстурных координат по горизонтали
        val tcGapX = 1.0f / _fontColumnCount.toFloat()
        // Размер ячейки с символом в долях текстурных координат по вертикали
        val tcGapY = 1.0f / _fontRowCount.toFloat()

        for ((i, element) in _text.withIndex()) {
            // Смещение квада с i-ым символом, зависит от ширины квадов и зазора между ними
            val offset = (letterWidth + _letterSpace) * i.toFloat()

            // Столбец, в котором находится символ
            val glyphX = (element.code % _fontColumnCount).toFloat()

            // Строка, в котором находится символ
            val glyphY = ((element.code / _fontColumnCount) - 1).toFloat()

            // Build character quad vertices data.
            val topLeftVt = Vector3((offset + 0.0f) + position.x, 0.0f + position.y, position.z)
            val topRightVt = Vector3((offset + letterWidth) + position.x, 0.0f + position.y, position.z)
            val bottomRightVt = Vector3(
                (offset + letterWidth) + position.x, -letterHeight + position.y, position.z
            )
            val bottomLeftVt = Vector3((offset + 0.0f) + position.x, -letterHeight + position.y, position.z)

            // Fill vertices array.
            val vertexOffset = i * VERTICIES_PER_QUAD * 3

            _letterQuadsVertices[vertexOffset + 0] = topLeftVt.x
            _letterQuadsVertices[vertexOffset + 1] = topLeftVt.y
            _letterQuadsVertices[vertexOffset + 2] = topLeftVt.z

            _letterQuadsVertices[vertexOffset + 3] = topRightVt.x
            _letterQuadsVertices[vertexOffset + 4] = topRightVt.y
            _letterQuadsVertices[vertexOffset + 5] = topRightVt.z

            _letterQuadsVertices[vertexOffset + 6] = bottomRightVt.x
            _letterQuadsVertices[vertexOffset + 7] = bottomRightVt.y
            _letterQuadsVertices[vertexOffset + 8] = bottomRightVt.z

            _letterQuadsVertices[vertexOffset + 9] = bottomRightVt.x
            _letterQuadsVertices[vertexOffset + 10] = bottomRightVt.y
            _letterQuadsVertices[vertexOffset + 11] = bottomRightVt.z

            _letterQuadsVertices[vertexOffset + 12] = bottomLeftVt.x
            _letterQuadsVertices[vertexOffset + 13] = bottomLeftVt.y
            _letterQuadsVertices[vertexOffset + 14] = bottomLeftVt.z

            _letterQuadsVertices[vertexOffset + 15] = topLeftVt.x
            _letterQuadsVertices[vertexOffset + 16] = topLeftVt.y
            _letterQuadsVertices[vertexOffset + 17] = topLeftVt.z

            // Build character quad texture coordinates data.
            val topLeftTc = Vector2((tcGapX * glyphX) + 0.0f, (tcGapY * glyphY) + 0.0f)
            val topRightTc = Vector2((tcGapX * glyphX) + tcGapX, (tcGapY * glyphY) + 0.0f)
            val bottomRightTc = Vector2((tcGapX * glyphX) + tcGapX, (tcGapY * glyphY) + tcGapY)
            val bottomLeftTc = Vector2((tcGapX * glyphX) + 0.0f, (tcGapY * glyphY) + tcGapY)

            // Fill texture coordinates array.
            val txOffset = i * VERTICIES_PER_QUAD * 2

            _letterQuadsTxcoords[txOffset + 0] = topLeftTc.x
            _letterQuadsTxcoords[txOffset + 1] = topLeftTc.y

            _letterQuadsTxcoords[txOffset + 2] = topRightTc.x
            _letterQuadsTxcoords[txOffset + 3] = topRightTc.y

            _letterQuadsTxcoords[txOffset + 4] = bottomRightTc.x
            _letterQuadsTxcoords[txOffset + 5] = bottomRightTc.y

            _letterQuadsTxcoords[txOffset + 6] = bottomRightTc.x
            _letterQuadsTxcoords[txOffset + 7] = bottomRightTc.y

            _letterQuadsTxcoords[txOffset + 8] = bottomLeftTc.x
            _letterQuadsTxcoords[txOffset + 9] = bottomLeftTc.y

            _letterQuadsTxcoords[txOffset + 10] = topLeftTc.x
            _letterQuadsTxcoords[txOffset + 11] = topLeftTc.y

            // Increment outputt quads (letters) count
            _quadsCount++
        }
    }

    val quadsCount: Int
        get(): Int {
            return _quadsCount
        }

    val vertices: FloatArray
        get():FloatArray {
            return _letterQuadsVertices
        }

    val txcoords: FloatArray
        get(): FloatArray {
            return _letterQuadsTxcoords
        }
};