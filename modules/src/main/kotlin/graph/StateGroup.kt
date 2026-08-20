package graph

import render.*

/**
 * StateGroup sits on top of scene subgraph, connects direct to locale object
 * and holds shader program that bind on every traversal begin.
 */
open class StateGroup(program: Program) : Group() {
    private var _program: Program

    init {
        _program = program
    }

    override fun accept(visitor: Visitor): Unit {
        visitor.apply(this)
    }

    val program: Program
        get(): Program {
            return _program
        }
}
