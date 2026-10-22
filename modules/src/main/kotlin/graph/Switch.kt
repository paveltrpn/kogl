package graph

// ============================================================================
// ======================= SwitchMask =========================================
// ============================================================================

enum class SwitchMask(val value: Int) {
    DISCARD(0),
    SHOW(1)
}

// ============================================================================
// ======================= Switch =============================================
// ============================================================================

class Switch : Node() {
    data class Child(val node: Node, var mask: SwitchMask)

    protected var mChildren: MutableList<Child> = mutableListOf()

    override fun accept(visitor: Visitor): Unit {
        visitor.apply(this)
    }

    override fun traverse(visitor: Visitor): Unit {
        for (child in mChildren) {
            if (child.mask == SwitchMask.SHOW) {
                child.node.accept(visitor)
            }
        }
    }

    var children: MutableList<Child>
        get(): MutableList<Child> {
            return mChildren
        }
        set(value) {
            mChildren = value
        }

    fun addChild(enabled: Boolean, child: Node): Unit {
        mChildren.addLast(
            Child(
                child, if (enabled) SwitchMask.SHOW else SwitchMask.DISCARD
            )
        )
    }

    fun addChild(mask: SwitchMask, child: Node): Unit {
        mChildren.addLast(Child(child, mask))
    }

    fun setChild(enabled: Boolean, index: Int): Unit {
        for ((i, child) in mChildren.withIndex()) {
            if (i == index) {
                child.mask = if (enabled) SwitchMask.SHOW else SwitchMask.DISCARD
            }
        }
    }

    fun setAllChildren(enabled: Boolean): Unit {
        for (child in mChildren) {
            child.mask = if (enabled) SwitchMask.SHOW else SwitchMask.DISCARD
        }
    }

    fun setSingleChildOn(index: Int): Unit {
        for ((i, child) in mChildren.withIndex()) {
            child.mask = if (i == index) SwitchMask.SHOW else SwitchMask.DISCARD
        }
    }
}