package ui

import algebra.*
import kotlin.Float

class Label {

    // Size of "Letters buffer" - this is the number of all characters
    // from all draw() calls that can one instance of this class operates
    val VERTICIES_PER_QUAD = 6
    val MAX_LETTERS_COUNT = 64
    val letterQuadsVertecies_ = Array<Vector3>(MAX_LETTERS_COUNT * VERTICIES_PER_QUAD) { Vector3() }
    val letterQuadsTexcrds_ = Array<Vector2>(MAX_LETTERS_COUNT * VERTICIES_PER_QUAD) { Vector2() }
    val letterQuadsColors_ = Array<Vector4>(MAX_LETTERS_COUNT * VERTICIES_PER_QUAD) { Vector4() }

    var glyphScale_ = 1.3f;
    val GLYPH_WIDTH = 0.4f
    val GLYPH_HEIGHT = 1.55f
    var glyphQuadWdt_ = GLYPH_WIDTH * glyphScale_;
    var glyphQuadHgt_ = GLYPH_HEIGHT * glyphScale_;
    val GLYPH_GAP = 0.0f
    var glyphGap_ = GLYPH_GAP;

    var posX_ = 0.0f;
    var posY_ = 0.0f;

    // Формат шрифта - изображение TGA с началом сверху слева,
    // 32 столбца на 8 строк символов, первый символ - 32 ("пробел").
    // Размер ячейки с символом получается делением горизонтального и вертикального
    // размера изображения на количество столбцов и строк соответственно.
    val fontColumnCount_ = 32  // Количество столбцов символов в шрифте
    val fontRowCount_ = 8      // Количество строк символов в шрифте

    var lettersCount_ = 0;

    // Colorf color_{};

    fun setGlyphWidth(value: Float): Unit {
        //
    }

    fun setGlyphHeight(value: Float): Unit {
        //
    }

    fun setGlyphScale(value: Float): Unit {
        //
    }

    fun setGlyphGap(value: Float): Unit {
        //
        glyphGap_ = value;
    }

    fun setTextPosition(x: Float, y: Float): Unit {
        //
    }

//    fun setColor( const Colorf &value ) : Unit{
//        //
//        color_ = value;
//    }

    fun resetStringParameters(): Unit {
        //
    }

    fun setPos(x: Float, y: Float): Unit {
        posX_ = x;
        posY_ = y;
    }


    fun draw(string: String): Unit {
        // Размер ячейки с символом в долях текстурных координат по горизонтали
        val tcGapX = 1.0f / fontColumnCount_.toFloat();
        // Размер ячейки с символом в долях текстурных координат по вертикали
        val tcGapY = 1.0f / fontRowCount_.toFloat();

        for (i in 0..<string.length) {
            // Смещение квада с i-ым символом, зависит от ширины квадов и зазора между ними
            val offset = (glyphQuadWdt_ + glyphGap_) * i.toFloat();

            // Столбец, в котором находится символ
            val glyphX = (string[i].code % fontColumnCount_).toFloat();

            // Строка, в котором находится символ
            val glyphY = ((string[i].code / fontColumnCount_) - 1).toFloat();

            // Build character quad vertecies data.
            val topLeftVt = Vector3((offset + 0.0f) + posX_, 0.0f + posY_, 0.0f)
            val topRightVt = Vector3((offset + glyphQuadWdt_) + posX_, 0.0f + posY_, 0.0f)
            val bottomRightVt = Vector3(
                (offset + glyphQuadWdt_) + posX_, -glyphQuadHgt_ + posY_, 0.0f
            )
            val bottomLeftVt = Vector3((offset + 0.0f) + posX_, -glyphQuadHgt_ + posY_, 0.0f)

            // Build character quad texture coordinates data.
            letterQuadsVertecies_[(i * VERTICIES_PER_QUAD) + 0] = topLeftVt;
            letterQuadsVertecies_[(i * VERTICIES_PER_QUAD) + 1] = topRightVt;
            letterQuadsVertecies_[(i * VERTICIES_PER_QUAD) + 2] = bottomRightVt;
            letterQuadsVertecies_[(i * VERTICIES_PER_QUAD) + 3] = bottomRightVt;
            letterQuadsVertecies_[(i * VERTICIES_PER_QUAD) + 4] = bottomLeftVt;
            letterQuadsVertecies_[(i * VERTICIES_PER_QUAD) + 5] = topLeftVt;

            val topLeftTc = Vector2((tcGapX * glyphX) + 0.0f, (tcGapY * glyphY) + 0.0f)
            val topRightTc = Vector2((tcGapX * glyphX) + tcGapX, (tcGapY * glyphY) + 0.0f)
            val bottomRightTc = Vector2((tcGapX * glyphX) + tcGapX, (tcGapY * glyphY) + tcGapY)
            val bottomLeftTc = Vector2((tcGapX * glyphX) + 0.0f, (tcGapY * glyphY) + tcGapY)

            letterQuadsTexcrds_[(i * VERTICIES_PER_QUAD) + 0] = topLeftTc;
            letterQuadsTexcrds_[(i * VERTICIES_PER_QUAD) + 1] = topRightTc;
            letterQuadsTexcrds_[(i * VERTICIES_PER_QUAD) + 2] = bottomRightTc;
            letterQuadsTexcrds_[(i * VERTICIES_PER_QUAD) + 3] = bottomRightTc;
            letterQuadsTexcrds_[(i * VERTICIES_PER_QUAD) + 4] = bottomLeftTc;
            letterQuadsTexcrds_[(i * VERTICIES_PER_QUAD) + 5] = topLeftTc;

            // Build character quad color data.
            //val color = color_.asVector4f();
            val color = Vector4(1.0f, 1.0f, 1.0f, 1.0f)

            letterQuadsColors_[(i * VERTICIES_PER_QUAD) + 0] = color;
            letterQuadsColors_[(i * VERTICIES_PER_QUAD) + 1] = color;
            letterQuadsColors_[(i * VERTICIES_PER_QUAD) + 2] = color;
            letterQuadsColors_[(i * VERTICIES_PER_QUAD) + 3] = color;
            letterQuadsColors_[(i * VERTICIES_PER_QUAD) + 4] = color;
            letterQuadsColors_[(i * VERTICIES_PER_QUAD) + 5] = color;

            lettersCount_++;
        }
    }

    fun lettersCount(): Int {
        //
        return lettersCount_;
    }

    fun bufferVerticesSize(): Int {
        //
        return lettersCount_ * VERTICIES_PER_QUAD * Float.SIZE_BYTES * 3;
    }

    fun bufferTexcrdsSize(): Int {
        //
        return lettersCount_ * VERTICIES_PER_QUAD * Float.SIZE_BYTES * 2;
    }

    fun bufferVertclrsSize(): Int {
        //
        return lettersCount_ * VERTICIES_PER_QUAD * Float.SIZE_BYTES * 4;
    }


    fun verteciesData(): Array<Vector3> {
        return letterQuadsVertecies_;
    }


    fun texcrdsData(): Array<Vector2> {
        return letterQuadsTexcrds_
    }


    fun clrsData(): Array<Vector4> {
        return letterQuadsColors_
    }


};