package ui

import algebra.*
import kotlin.Float

class Billboard : UiComponent() {
    private val VERTICIES_PER_QUAD = 6

    private val _quadVerticies = FloatArray(1 * VERTICIES_PER_QUAD * 3) { 0.0f };
    private val _quadTexcrds = FloatArray(1 * VERTICIES_PER_QUAD * 2) { 0.0f };

    // Colorf color_{};

    private var _posX = 0.0f
    private var _posY = 0.0f
    private var _z = 0.0f

    private var _width = 1.0f
    private var _height = 1.0f

//    fun setColor( const Colorf &value ) : Unit {
//        color_ = value;
//    }

    fun setPos(px: Float, py: Float): Unit {
        _posX = px;
        _posY = py;
    }

    fun setSize(width: Float, height: Float): Unit {
        _width = width;
        _height = height;
    }

    fun setZ(z: Float): Unit {
        _z = z;
    }

    fun draw(): Unit {
        val topLeftVt = Vector3(_posX, _posY, _z)
        val topRightVt = Vector3(_posX + _width, _posY, _z)
        val bottomRightVt = Vector3(_posX + _width, _posY - _height, _z)
        val bottomLeftVt = Vector3(_posX, _posY - _height, _z)

        for (i in 0..<3) {
            _quadVerticies[0 + ((i * 3) + 0)] = topLeftVt.x
            _quadVerticies[0 + ((i * 3) + 1)] = topLeftVt.y
            _quadVerticies[0 + ((i * 3) + 2)] = topLeftVt.z

            _quadVerticies[3 + ((i * 3) + 0)] = topRightVt.x
            _quadVerticies[3 + ((i * 3) + 1)] = topRightVt.y
            _quadVerticies[3 + ((i * 3) + 2)] = topRightVt.z

            _quadVerticies[6 + ((i * 3) + 0)] = bottomRightVt.x
            _quadVerticies[6 + ((i * 3) + 1)] = bottomRightVt.y
            _quadVerticies[6 + ((i * 3) + 2)] = bottomRightVt.z

            _quadVerticies[9 + ((i * 3) + 0)] = topLeftVt.x
            _quadVerticies[9 + ((i * 3) + 1)] = topLeftVt.y
            _quadVerticies[9 + ((i * 3) + 2)] = topLeftVt.z

            _quadVerticies[12 + ((i * 3) + 0)] = bottomRightVt.x
            _quadVerticies[12 + ((i * 3) + 1)] = bottomRightVt.y
            _quadVerticies[12 + ((i * 3) + 2)] = bottomRightVt.z

            _quadVerticies[15 + ((i * 3) + 0)] = bottomLeftVt.x
            _quadVerticies[15 + ((i * 3) + 1)] = bottomLeftVt.y
            _quadVerticies[15 + ((i * 3) + 2)] = bottomLeftVt.z
        }

//        _quadVerticies[0] = topLeftVt
//        _quadVerticies[1] = topRightVt;
//        _quadVerticies[2] = bottomRightVt;
//        _quadVerticies[3] = topLeftVt;
//        _quadVerticies[4] = bottomRightVt;
//        _quadVerticies[5] = bottomLeftVt;

        val topLeftTc = Vector2(0.0f, 0.0f)
        val topRightTc = Vector2(1.0f, 0.0f)
        val bottomRightTc = Vector2(1.0f, 1.0f)
        val bottomLeftTc = Vector2(0.0f, 1.0f)

        for (i in 0..<2) {
            _quadVerticies[0 + ((i * 2) + 0)] = topLeftTc.x
            _quadVerticies[0 + ((i * 2) + 1)] = topLeftTc.y

            _quadVerticies[2 + ((i * 2) + 0)] = topRightTc.x
            _quadVerticies[2 + ((i * 2) + 1)] = topRightTc.y

            _quadVerticies[4 + ((i * 2) + 0)] = bottomRightTc.x
            _quadVerticies[4 + ((i * 2) + 1)] = bottomRightTc.y

            _quadVerticies[6 + ((i * 2) + 0)] = bottomRightTc.x
            _quadVerticies[6 + ((i * 2) + 1)] = bottomRightTc.y

            _quadVerticies[8 + ((i * 2) + 0)] = bottomLeftTc.x
            _quadVerticies[8 + ((i * 2) + 1)] = bottomLeftTc.y

            _quadVerticies[10 + ((i * 2) + 0)] = topLeftTc.x
            _quadVerticies[10 + ((i * 2) + 1)] = topLeftTc.y
        }

//        _quadTexcrds[0] = topLeftTc;
//        _quadTexcrds[1] = topRightTc;
//        _quadTexcrds[2] = bottomRightTc;
//        _quadTexcrds[3] = bottomRightTc;
//        _quadTexcrds[4] = bottomLeftTc;
//        _quadTexcrds[5] = topLeftTc;

        _quadsCount++;
    }


    val quadsCount: Int
        get(): Int {
            return _quadsCount;
        }

    fun bufferVerticesSize(): Int {
        return quadsCount * VERTICIES_PER_QUAD * Float.SIZE_BYTES * 3
    }

    fun bufferTexcrdsSize(): Int {
        return quadsCount * VERTICIES_PER_QUAD * Float.SIZE_BYTES * 2
    }

    fun bufferVertclrsSize(): Int {
        return quadsCount * VERTICIES_PER_QUAD * Float.SIZE_BYTES * 4
    }

    val vertices: FloatArray
        get(): FloatArray {
            return _quadVerticies;
        }

    val txcoords: FloatArray
        get():FloatArray {
            return _quadTexcrds
        }
};