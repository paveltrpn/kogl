package graph

import render.*

open class StateGroup(program: Program) : Node {
    private var _children: MutableList<Node> = mutableListOf()
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

    override fun ascend(visitor: Visitor): Unit {
    }

    override fun traverse(visitor: Visitor): Unit {

    }

    fun callProgram(): Unit {
        _program.use()
    }

    fun addChild(child: Node): Unit {
        _children.addLast(child)
    }

    val program: Program
        get(): Program {
            return _program
        }
}
