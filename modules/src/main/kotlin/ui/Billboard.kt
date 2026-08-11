package ui

import algebra.*
import kotlin.Float

class Billboard : UiComponent() {
    private val VERTICIES_PER_QUAD = 6
    private val JUST_SINGLE_QUAD = 1

    private val _quadVerticies = Array<Vector3>(JUST_SINGLE_QUAD * VERTICIES_PER_QUAD) { Vector3() };
    private val _quadTexcrds = Array<Vector2>(JUST_SINGLE_QUAD * VERTICIES_PER_QUAD) { Vector2() };
    private val _quadsColors = Array<Vector4>(JUST_SINGLE_QUAD * VERTICIES_PER_QUAD) { Vector4() };

    // Colorf color_{};

    private var _posX = 0.0f
    private var _posY = 0.0f
    private var _z = 0.0f

    private var _width = 1.0f
    private var _height = 1.0f

//    fun setColor( const Colorf &value ) : Unit {
//        //
//        color_ = value;
//    }

    fun setPos(px: Float, py: Float): Unit {
        //
        _posX = px;
        _posY = py;
    }

    fun setSize(width: Float, height: Float): Unit {
        //
        _width = width;
        _height = height;
    }

    fun setZ(z: Float): Unit {
        //
        _z = z;
    }

    fun draw(): Unit {
        val topLeftVt = Vector3(_posX, _posY, _z)
        val topRightVt = Vector3(_posX + _width, _posY, _z)
        val bottomRightVt = Vector3(_posX + _width, _posY - _height, _z)
        val bottomLeftVt = Vector3(_posX, _posY - _height, _z)

        _quadVerticies[0] = topLeftVt;
        _quadVerticies[1] = topRightVt;
        _quadVerticies[2] = bottomRightVt;
        _quadVerticies[3] = topLeftVt;
        _quadVerticies[4] = bottomRightVt;
        _quadVerticies[5] = bottomLeftVt;

        val topLeftTc = Vector2(0.0f, 0.0f)
        val topRightTc = Vector2(1.0f, 0.0f)
        val bottomRightTc = Vector2(1.0f, 1.0f)
        val bottomLeftTc = Vector2(0.0f, 1.0f)

        _quadTexcrds[0] = topLeftTc;
        _quadTexcrds[1] = topRightTc;
        _quadTexcrds[2] = bottomRightTc;
        _quadTexcrds[3] = bottomRightTc;
        _quadTexcrds[4] = bottomLeftTc;
        _quadTexcrds[5] = topLeftTc;

        // val color = color_.asVector4f();
        val color = Vector4(1.0f, 1.0f, 1.0f, 1.0f)

        _quadsColors[0] = color;
        _quadsColors[1] = color;
        _quadsColors[2] = color;
        _quadsColors[3] = color;
        _quadsColors[4] = color;
        _quadsColors[5] = color;
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

    val vertices: Array<Vector3>
        get(): Array<Vector3> {
            return _quadVerticies;
        }

    val txcoords: Array<Vector2>
        get(): Array<Vector2> {
            return _quadTexcrds
        }

    val colors: Array<Vector4>
        get(): Array<Vector4> {
            return _quadsColors
        }
};