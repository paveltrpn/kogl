package graph

import algebra.*

class MatrixStack {
    private var _stack = ArrayDeque<Matrix4>()

    init {
        set(Matrix4().idtt())
    }

    val top: Matrix4
        get(): Matrix4 {
            return _stack.last()
        }

    fun set(m: Matrix4): Unit {
        _stack = ArrayDeque()
        _stack.addLast(m)
    }

    fun push(m: Matrix4): Unit {
        _stack.addLast(m)
    }

    fun push(t: Transform): Unit {
        _stack.addLast(t.transform(top))
    }

    fun pop(): Matrix4 {
        return _stack.removeLast()
    }
}