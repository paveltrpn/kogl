package graph

import render.*

open class StateGroup(program: Program) : Group() {
    private var _program: Program

    init {
        _program = program
    }

    var children: MutableList<Node>
        get(): MutableList<Node> {
            return _children
        }
        set(value) {
            _children = value
        }

    override fun accept(visitor: Visitor): Unit {
        visitor.apply(this)
    }

    val program: Program
        get(): Program {
            return _program
        }
}
