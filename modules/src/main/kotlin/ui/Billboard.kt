package ui

import kotlin.Float

import algebra.*

class Billboard : UiComponent() {
    private val VERTICIES_PER_QUAD = 6

    private val _quadVerticies = FloatArray(1 * VERTICIES_PER_QUAD * 3) { 0.0f }
    private val _quadTexcrds = FloatArray(1 * VERTICIES_PER_QUAD * 2) { 0.0f }

    // Colorf color_{}

    private var _position: Vector3 = Vector3()

    private var _width = 1.0f
    private var _height = 1.0f

//    fun setColor( const Colorf &value ) : Unit {
//        color_ = value;
//    }

    var size: Vector2
        get(): Vector2 {
            return Vector2(_width, height)
        }
        set(value) {
            _width = value.x
            _height = value.y
        }

    var width: Float
        get(): Float {
            return _width
        }
        set(value) {
            _width = value
        }

    var height: Float
        get(): Float {
            return _height
        }
        set(value) {
            _height = value
        }

    var position: Vector2
        get() :Vector2 {
            return Vector2(_position.x, _position.y)
        }
        set(value) {
            _position.x = value.x
            _position.y = value.y
        }

    var z: Float
        get(): Float {
            return _position.z
        }
        set(value) {
            _position.z = value
        }

    override fun draw(): Unit {
        // Build quad vertices data.
        val topLeftVt = Vector3(_position.x, _position.y, _position.z)
        val topRightVt = Vector3(_position.x + _width, _position.y, _position.z)
        val bottomRightVt = Vector3(_position.x + _width, _position.y - _height, _position.z)
        val bottomLeftVt = Vector3(_position.x, _position.y - _height, _position.z)

        // Fill vertices array.
        _quadVerticies[0] = topLeftVt.x
        _quadVerticies[1] = topLeftVt.y
        _quadVerticies[2] = topLeftVt.z

        _quadVerticies[3] = topRightVt.x
        _quadVerticies[4] = topRightVt.y
        _quadVerticies[5] = topRightVt.z

        _quadVerticies[6] = bottomRightVt.x
        _quadVerticies[7] = bottomRightVt.y
        _quadVerticies[8] = bottomRightVt.z

        _quadVerticies[9] = bottomRightVt.x
        _quadVerticies[10] = bottomRightVt.y
        _quadVerticies[11] = bottomRightVt.z

        _quadVerticies[12] = bottomLeftVt.x
        _quadVerticies[13] = bottomLeftVt.y
        _quadVerticies[14] = bottomLeftVt.z

        _quadVerticies[15] = topLeftVt.x
        _quadVerticies[16] = topLeftVt.y
        _quadVerticies[17] = topLeftVt.z

        // Build quad texture coordinates data.
        val topLeftTc = Vector2(0.0f, 0.0f)
        val topRightTc = Vector2(1.0f, 0.0f)
        val bottomRightTc = Vector2(1.0f, 1.0f)
        val bottomLeftTc = Vector2(0.0f, 1.0f)

        // Fill texture coordinates array.
        _quadTexcrds[0] = topLeftTc.x
        _quadTexcrds[1] = topLeftTc.y

        _quadTexcrds[2] = topRightTc.x
        _quadTexcrds[3] = topRightTc.y

        _quadTexcrds[4] = bottomRightTc.x
        _quadTexcrds[5] = bottomRightTc.y

        _quadTexcrds[6] = bottomRightTc.x
        _quadTexcrds[7] = bottomRightTc.y

        _quadTexcrds[8] = bottomLeftTc.x
        _quadTexcrds[9] = bottomLeftTc.y

        _quadTexcrds[10] = topLeftTc.x
        _quadTexcrds[11] = topLeftTc.y

        _quadsCount++
    }


    val quadsCount: Int
        get(): Int {
            return _quadsCount
        }

    val vertices: FloatArray
        get(): FloatArray {
            return _quadVerticies
        }

    val txcoords: FloatArray
        get():FloatArray {
            return _quadTexcrds
        }
};