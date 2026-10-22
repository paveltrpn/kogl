package graphdsl

import graph.*

// ============================================================================
// ======================= GroupBuilderDslMarker ==============================
// ============================================================================
@DslMarker
annotation class GroupBuilderDslMarker

// ============================================================================
// ======================= GroupBuilder =======================================
// ============================================================================
@GroupBuilderDslMarker
class GroupBuilder : NodeBuilder() {
    private var _group: Group? = null

    fun group(block: Group.() -> Unit): Unit {
        _group = Group().apply {
            block()
        }
    }

    fun get(): Group {
        return _group ?: throw RuntimeException("Group not initialized!")
    }
}

fun buildGroup(block: GroupBuilder.() -> Unit): Group {
    val r = GroupBuilder().apply {
        block()
    }.get()

    return r
}

infix fun Group.attachGroup(block: GroupBuilder.() -> Unit): Unit {
    this attach buildGroup {
        block()
    }
}