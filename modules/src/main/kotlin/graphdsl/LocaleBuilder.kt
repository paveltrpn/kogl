package graphdsl

import algebra.*
import graph.*
import render.*

class LocaleBuilder : NodeBuilder() {
    private var _stateGroups: MutableList<StateGroup> = mutableListOf()

    fun stateGroup(p: Program): StateGroup {
        return StateGroup(p)
    }
}

fun buildLocale(builder: LocaleBuilder.(origin: Vector3) -> Unit): Locale {
    return Locale().apply { origin = Vector3() }
}
