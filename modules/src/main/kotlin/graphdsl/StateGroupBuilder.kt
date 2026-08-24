package graphdsl

import graph.*

@DslMarker
annotation class StateGroupBuilderDslMarker

@StateGroupBuilderDslMarker
class StateGroupBuilder : NodeBuilder() {
    private var _stateGroup: StateGroup? = null

    fun stateGroup(block: StateGroup.() -> Unit): Unit {
        _stateGroup = StateGroup().apply {
            block()
        }
    }

    fun get(): StateGroup {
        return _stateGroup ?: throw RuntimeException("StateGroup not initialized!")
    }
}

fun buildStateGroup(block: StateGroupBuilder.() -> Unit): StateGroup {
    val r = StateGroupBuilder().apply {
        block()
    }.get()

    if (r.isEmpty) {
        throw RuntimeException("Do not create empty state groups!")
    }

    return r
}

