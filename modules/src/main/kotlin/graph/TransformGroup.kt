package graph

import algebra.*

open class TransformGroup() : Node() {
    private var _child: Node? = null

    private var _matrix = Matrix4()

    constructor(other: FloatArray) : this() {
        matrix = Matrix4(other)
    }

    override fun accept(visitor: Visitor): Unit {
        visitor.apply(this)
    }

    override fun traverse(visitor: Visitor): Unit {
        _child?.accept(visitor)
    }

    var child: Node?
        get(): Node? {
            return _child
        }
        set(value) {
            _child = value
        }

    var matrix: Matrix4
        get(): Matrix4 {
            return _matrix
        }
        set(value) {
            _matrix = value
        }

    // NOTE: Must accept Transform, Group or Drawable.
    fun addChild(child: Node): Unit {
        _child = child
    }

    fun transform(m: Matrix4): Matrix4 {
        return m.multiply(matrix)
    }
}


