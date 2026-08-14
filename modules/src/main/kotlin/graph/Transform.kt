package graph

import algebra.*

open class Transform : Node {
    private var _child: Node? = null

    private var _matrix = Matrix4()

    override fun accept(visitor: Visitor): Unit {
        visitor.apply(this)
    }

    override fun ascend(visitor: Visitor): Unit {
    }

    override fun traverse(visitor: Visitor): Unit {
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

    // NOTE: Must accept Transform or Drawable.
    fun addChild(child: Node): Unit {
        _child = child
    }
}

