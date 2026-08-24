package graphdsl

import algebra.*
import graph.*
import render.*

class StateGroupBuilder : NodeBuilder() {
    private var _program = Program()
    private var _stateGroup = StateGroup()

    var program: Program
        get(): Program {
            return _program
        }
        set(value) {
            _program = value
        }

    fun stateGroup(block: StateGroup.() -> Unit): Unit {
        _stateGroup.program = _program
        _stateGroup.apply(block)
    }

    fun get(): StateGroup {
        return _stateGroup
    }
}

fun buildStateGroup(block: StateGroupBuilder.() -> Unit): StateGroup {
    return StateGroupBuilder().apply(block).get()
}

