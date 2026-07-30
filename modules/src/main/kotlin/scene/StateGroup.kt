package scene

import render.Program

open class StateGroup : Node {
    private var _children: MutableList<Node> = mutableListOf()
    private var _program: Program? = null

    var children: MutableList<Node>
        get(): MutableList<Node> {
            return _children
        }
        set(value) {
            _children = value
        }

    override fun traverse(): Unit {
        println("Traverse over StateGroup")
    }

    fun addChild(child: Node): Unit {
        _children.addLast(child)
    }

    fun setProgram(program: Program): Unit {
        _program = program
    }
}
