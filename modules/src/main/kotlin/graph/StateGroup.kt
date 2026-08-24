package graph

import render.*

/**
 * StateGroup sits on top of scene subgraph, connects direct to locale object
 * and holds shader program that bind on every traversal begin.
 */
open class StateGroup : Group {
    private var _program: Program

    constructor() {
        _program = Program()
    }

    constructor(program: Program) : super() {
        _program = program
    }

    var program: Program
        get(): Program {
            return _program
        }
        set(value) {
            _program = value
        }

    override fun accept(visitor: Visitor): Unit {
        visitor.apply(this)
    }
}