package ui

import algebra.*
import kotlin.Float

class Billboard : UiComponent() {
    val VERTICIES_PER_QUAD = 6
    val JUST_SINGLE_QUAD = 1

    val quadVerticies_ = Array<Vector3>(JUST_SINGLE_QUAD * VERTICIES_PER_QUAD) { Vector3() };
    val quadTexcrds_ = Array<Vector2>(JUST_SINGLE_QUAD * VERTICIES_PER_QUAD) { Vector2() };
    val quadsColors_ = Array<Vector4>(JUST_SINGLE_QUAD * VERTICIES_PER_QUAD) { Vector4() };

    // Colorf color_{};

    var posX_ = 0.0f
    var posY_ = 0.0f
    var z_ = 0.0f

    var width_ = 1.0f
    var height_ = 1.0f

//    fun setColor( const Colorf &value ) : Unit {
//        //
//        color_ = value;
//    }

    fun setPos(px: Float, py: Float): Unit {
        //
        posX_ = px;
        posY_ = py;
    }

    fun setSize(width: Float, height: Float): Unit {
        //
        width_ = width;
        height_ = height;
    }

    fun setZ(z: Float): Unit {
        //
        z_ = z;
    }

    fun draw(): Unit {
        val topLeftVt = Vector3(posX_, posY_, z_)
        val topRightVt = Vector3(posX_ + width_, posY_, z_)
        val bottomRightVt = Vector3(posX_ + width_, posY_ - height_, z_)
        val bottomLeftVt = Vector3(posX_, posY_ - height_, z_)

        quadVerticies_[0] = topLeftVt;
        quadVerticies_[1] = topRightVt;
        quadVerticies_[2] = bottomRightVt;
        quadVerticies_[3] = topLeftVt;
        quadVerticies_[4] = bottomRightVt;
        quadVerticies_[5] = bottomLeftVt;

        val topLeftTc = Vector2(0.0f, 0.0f)
        val topRightTc = Vector2(1.0f, 0.0f)
        val bottomRightTc = Vector2(1.0f, 1.0f)
        val bottomLeftTc = Vector2(0.0f, 1.0f)

        quadTexcrds_[0] = topLeftTc;
        quadTexcrds_[1] = topRightTc;
        quadTexcrds_[2] = bottomRightTc;
        quadTexcrds_[3] = bottomRightTc;
        quadTexcrds_[4] = bottomLeftTc;
        quadTexcrds_[5] = topLeftTc;

        // val color = color_.asVector4f();
        val color = Vector4(1.0f, 1.0f, 1.0f, 1.0f)

        quadsColors_[0] = color;
        quadsColors_[1] = color;
        quadsColors_[2] = color;
        quadsColors_[3] = color;
        quadsColors_[4] = color;
        quadsColors_[5] = color;
    }


    fun lettersCount(): Int {
        //
        return JUST_SINGLE_QUAD;
    }

    fun bufferVerticesSize(): Int {
        //
        return JUST_SINGLE_QUAD * VERTICIES_PER_QUAD * Float.SIZE_BYTES * 3
    }

    fun bufferTexcrdsSize(): Int {
        //
        return JUST_SINGLE_QUAD * VERTICIES_PER_QUAD * Float.SIZE_BYTES * 2
    }

    fun bufferVertclrsSize(): Int {
        //
        return JUST_SINGLE_QUAD * VERTICIES_PER_QUAD * Float.SIZE_BYTES * 4
    }


    fun verteciesData(): Array<Vector3> {
        return quadVerticies_
    }


    fun texcrdsData(): Array<Vector2> {
        return quadTexcrds_
    }


    fun clrsData(): Array<Vector4> {
        return quadsColors_
    }
};