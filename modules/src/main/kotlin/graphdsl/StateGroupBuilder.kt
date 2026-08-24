package graphdsl

import graph.*

@DslMarker
annotation class StateGroupBuilderDslMarker

@StateGroupBuilderDslMarker
class StateGroupBuilder : NodeBuilder() {
    private var _stateGroup = StateGroup()

    fun stateGroup(block: StateGroup.() -> Unit): Unit {
        _stateGroup.apply(block)
    }

    fun get(): StateGroup {
        return _stateGroup
    }
}

fun buildStateGroup(block: StateGroupBuilder.() -> Unit): StateGroup {
    return StateGroupBuilder().apply(block).get()
}

