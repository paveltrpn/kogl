package graph

import algebra.*

open class TransformGroup() : Group() {
    private var _matrix = Matrix4().idtt()

    constructor(other: FloatArray) : this() {
        matrix = Matrix4(other)
    }

    override fun accept(visitor: Visitor): Unit {
        visitor.apply(this)
    }

    var matrix: Matrix4
        get(): Matrix4 {
            return _matrix
        }
        set(value) {
            _matrix = value
        }

    fun transform(m: Matrix4): Matrix4 {
        return m.multiply(matrix)
    }
}


