package ui

import algebra.*
import kotlin.Float

class Label : UiComponent() {

    // Size of "Letters buffer" - this is the number of all characters
    // from all draw() calls that can one instance of this class operates
    private val VERTICIES_PER_QUAD = 6
    private val MAX_LETTERS_COUNT = 64
    private val _letterQuadsVertices = FloatArray(MAX_LETTERS_COUNT * VERTICIES_PER_QUAD * 3) { 0.0f }
    private val _letterQuadsTxcoords = FloatArray(MAX_LETTERS_COUNT * VERTICIES_PER_QUAD * 2) { 0.0f }

    private var _glyphScale = 1.3f;
    private val GLYPH_WIDTH = 0.4f
    private val GLYPH_HEIGHT = 1.55f
    private var _glyphQuadWdt = GLYPH_WIDTH * _glyphScale;
    private var _glyphQuadHgt = GLYPH_HEIGHT * _glyphScale;
    private val GLYPH_GAP = 0.0f
    private var _glyphGap = GLYPH_GAP;

    private var _posX = 0.0f;
    private var _posY = 0.0f;

    // Формат шрифта - изображение TGA с началом сверху слева,
    // 32 столбца на 8 строк символов, первый символ - 32 ("пробел").
    // Размер ячейки с символом получается делением горизонтального и вертикального
    // размера изображения на количество столбцов и строк соответственно.
    private val _fontColumnCount = 32  // Количество столбцов символов в шрифте
    private val _fontRowCount = 8      // Количество строк символов в шрифте

    private var _lettersCount = 0;

    // Colorf color_{};

    fun setGlyphWidth(value: Float): Unit {
    }

    fun setGlyphHeight(value: Float): Unit {
    }

    fun setGlyphScale(value: Float): Unit {
    }

    fun setGlyphGap(value: Float): Unit {
        _glyphGap = value;
    }

    fun setTextPosition(x: Float, y: Float): Unit {
    }

//    fun setColor( const Colorf &value ) : Unit{
//        color_ = value;
//    }

    fun resetStringParameters(): Unit {
    }

    fun setPos(x: Float, y: Float): Unit {
        _posX = x;
        _posY = y;
    }


    fun draw(string: String): Unit {
        // Размер ячейки с символом в долях текстурных координат по горизонтали
        val tcGapX = 1.0f / _fontColumnCount.toFloat();
        // Размер ячейки с символом в долях текстурных координат по вертикали
        val tcGapY = 1.0f / _fontRowCount.toFloat();

        for (i in 0..<string.length) {
            // Смещение квада с i-ым символом, зависит от ширины квадов и зазора между ними
            val offset = (_glyphQuadWdt + _glyphGap) * i.toFloat();

            // Столбец, в котором находится символ
            val glyphX = (string[i].code % _fontColumnCount).toFloat();

            // Строка, в котором находится символ
            val glyphY = ((string[i].code / _fontColumnCount) - 1).toFloat();

            // Build character quad vertecies data.
            val topLeftVt = Vector3((offset + 0.0f) + _posX, 0.0f + _posY, 0.0f)
            val topRightVt = Vector3((offset + _glyphQuadWdt) + _posX, 0.0f + _posY, 0.0f)
            val bottomRightVt = Vector3(
                (offset + _glyphQuadWdt) + _posX, -_glyphQuadHgt + _posY, 0.0f
            )
            val bottomLeftVt = Vector3((offset + 0.0f) + _posX, -_glyphQuadHgt + _posY, 0.0f)


            for (j in 0..<3) {
                _letterQuadsVertices[((i * VERTICIES_PER_QUAD) + 0) + ((j * 3) + 0)] = topLeftVt.x
                _letterQuadsVertices[((i * VERTICIES_PER_QUAD) + 0) + ((j * 3) + 1)] = topLeftVt.y
                _letterQuadsVertices[((i * VERTICIES_PER_QUAD) + 0) + ((j * 3) + 2)] = topLeftVt.z

                _letterQuadsVertices[((i * VERTICIES_PER_QUAD) + 3) + ((j * 3) + 0)] = topRightVt.x
                _letterQuadsVertices[((i * VERTICIES_PER_QUAD) + 3) + ((j * 3) + 1)] = topRightVt.y
                _letterQuadsVertices[((i * VERTICIES_PER_QUAD) + 3) + ((j * 3) + 2)] = topRightVt.z

                _letterQuadsVertices[((i * VERTICIES_PER_QUAD) + 6) + ((j * 3) + 0)] = bottomRightVt.x
                _letterQuadsVertices[((i * VERTICIES_PER_QUAD) + 6) + ((j * 3) + 1)] = bottomRightVt.y
                _letterQuadsVertices[((i * VERTICIES_PER_QUAD) + 6) + ((j * 3) + 2)] = bottomRightVt.z

                _letterQuadsVertices[((i * VERTICIES_PER_QUAD) + 9) + ((j * 3) + 0)] = bottomRightVt.x
                _letterQuadsVertices[((i * VERTICIES_PER_QUAD) + 9) + ((j * 3) + 1)] = bottomRightVt.y
                _letterQuadsVertices[((i * VERTICIES_PER_QUAD) + 9) + ((j * 3) + 2)] = bottomRightVt.z

                _letterQuadsVertices[((i * VERTICIES_PER_QUAD) + 12) + ((j * 3) + 0)] = bottomLeftVt.x
                _letterQuadsVertices[((i * VERTICIES_PER_QUAD) + 12) + ((j * 3) + 1)] = bottomLeftVt.y
                _letterQuadsVertices[((i * VERTICIES_PER_QUAD) + 12) + ((j * 3) + 2)] = bottomLeftVt.z

                _letterQuadsVertices[((i * VERTICIES_PER_QUAD) + 15) + ((j * 3) + 0)] = topLeftVt.x
                _letterQuadsVertices[((i * VERTICIES_PER_QUAD) + 15) + ((j * 3) + 1)] = topLeftVt.y
                _letterQuadsVertices[((i * VERTICIES_PER_QUAD) + 15) + ((j * 3) + 2)] = topLeftVt.z
            }

//            _letterQuadsVertices[(i * VERTICIES_PER_QUAD) + 0] = topLeftVt;
//            _letterQuadsVertices[(i * VERTICIES_PER_QUAD) + 1] = topRightVt;
//            _letterQuadsVertices[(i * VERTICIES_PER_QUAD) + 2] = bottomRightVt;
//            _letterQuadsVertices[(i * VERTICIES_PER_QUAD) + 3] = bottomRightVt;
//            _letterQuadsVertices[(i * VERTICIES_PER_QUAD) + 4] = bottomLeftVt;
//            _letterQuadsVertices[(i * VERTICIES_PER_QUAD) + 5] = topLeftVt;

            val topLeftTc = Vector2((tcGapX * glyphX) + 0.0f, (tcGapY * glyphY) + 0.0f)
            val topRightTc = Vector2((tcGapX * glyphX) + tcGapX, (tcGapY * glyphY) + 0.0f)
            val bottomRightTc = Vector2((tcGapX * glyphX) + tcGapX, (tcGapY * glyphY) + tcGapY)
            val bottomLeftTc = Vector2((tcGapX * glyphX) + 0.0f, (tcGapY * glyphY) + tcGapY)

            for (j in 0..<3) {
                _letterQuadsTxcoords[((i * VERTICIES_PER_QUAD) + 0) + ((j * 2) + 0)] = topLeftTc.x
                _letterQuadsTxcoords[((i * VERTICIES_PER_QUAD) + 0) + ((j * 2) + 1)] = topLeftTc.y

                _letterQuadsTxcoords[((i * VERTICIES_PER_QUAD) + 2) + ((j * 2) + 0)] = topRightTc.x
                _letterQuadsTxcoords[((i * VERTICIES_PER_QUAD) + 2) + ((j * 2) + 1)] = topRightTc.y

                _letterQuadsTxcoords[((i * VERTICIES_PER_QUAD) + 4) + ((j * 2) + 0)] = bottomRightTc.x
                _letterQuadsTxcoords[((i * VERTICIES_PER_QUAD) + 4) + ((j * 2) + 1)] = bottomRightTc.y

                _letterQuadsTxcoords[((i * VERTICIES_PER_QUAD) + 6) + ((j * 2) + 0)] = bottomRightTc.x
                _letterQuadsTxcoords[((i * VERTICIES_PER_QUAD) + 6) + ((j * 2) + 1)] = bottomRightTc.y

                _letterQuadsTxcoords[((i * VERTICIES_PER_QUAD) + 8) + ((j * 2) + 0)] = bottomLeftTc.x
                _letterQuadsTxcoords[((i * VERTICIES_PER_QUAD) + 8) + ((j * 2) + 1)] = bottomLeftTc.y

                _letterQuadsTxcoords[((i * VERTICIES_PER_QUAD) + 10) + ((j * 2) + 0)] = topLeftTc.x
                _letterQuadsTxcoords[((i * VERTICIES_PER_QUAD) + 10) + ((j * 2) + 1)] = topLeftTc.y
            }

//            _letterQuadsTxcoords[(i * VERTICIES_PER_QUAD) + 0] = topLeftTc;
//            _letterQuadsTxcoords[(i * VERTICIES_PER_QUAD) + 1] = topRightTc;
//            _letterQuadsTxcoords[(i * VERTICIES_PER_QUAD) + 2] = bottomRightTc;
//            _letterQuadsTxcoords[(i * VERTICIES_PER_QUAD) + 3] = bottomRightTc;
//            _letterQuadsTxcoords[(i * VERTICIES_PER_QUAD) + 4] = bottomLeftTc;
//            _letterQuadsTxcoords[(i * VERTICIES_PER_QUAD) + 5] = topLeftTc;

            _lettersCount++;
        }
    }

    val lettersCount: Int
        get(): Int {
            return _lettersCount
        }

    fun bufferVerticesSize(): Int {
        return lettersCount * VERTICIES_PER_QUAD * Float.SIZE_BYTES * 3
    }

    fun bufferTexcrdsSize(): Int {
        return lettersCount * VERTICIES_PER_QUAD * Float.SIZE_BYTES * 2
    }

    val vertices: FloatArray
        get():FloatArray {
            return _letterQuadsVertices;
        }

    val txcoords: FloatArray
        get(): FloatArray {
            return _letterQuadsTxcoords
        }
};