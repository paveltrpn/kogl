package graphdsl

import algebra.*
import graph.*

@DslMarker
annotation class LocaleBuilderDslMarker

@LocaleBuilderDslMarker
class LocaleBuilder : NodeBuilder() {
    private var _origin = Vector3()

    private var _stateGroups: MutableList<StateGroup> = mutableListOf()

    var origin: Vector3
        get(): Vector3 {
            return _origin
        }
        set(value) {
            _origin = value
        }

    fun attachStateGroup(sg: StateGroup): Unit {
        _stateGroups.addLast(sg)
    }

    infix fun attach(sg: StateGroup): Unit {
        attachStateGroup(sg)
    }

    fun get(): Locale {
        return Locale().apply {
            origin = _origin
            addStateGroups(_stateGroups)
        }
    }
}

fun buildLocale(block: LocaleBuilder.() -> Unit): Locale {
    val r = LocaleBuilder().apply {
        block()
    }.get()

    if (r.isEmpty) {
        throw RuntimeException("Do not create empty locales!")
    }

    return r
}
