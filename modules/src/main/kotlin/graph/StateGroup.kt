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

    override fun traverse(): Unit {
        _program.use()
    }

    override fun accept(visitor: Visitor): Unit {
        visitor.apply(this)
    }
    
    fun addChild(child: Node): Unit {
        _children.addLast(child)
    }

    fun setProgram(program: Program): Unit {
        _program = program
    }

    val program: Program
        get(): Program {
            return _program
        }
}
