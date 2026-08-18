package graph

enum class SwitchMask {
    ENABLED,
    DISABLED
}

class Switch : Node() {
    data class Child(val node: Node, val mask: Int)

    protected var _children: MutableList<Child> = mutableListOf()

    override fun accept(visitor: Visitor): Unit {
        visitor.apply(this)
    }

    override fun traverse(visitor: Visitor): Unit {
        for (child in _children) {
            if (child.mask == 1) {
                child.node.accept(visitor)
            }
        }
    }

    var children: MutableList<Child>
        get(): MutableList<Child> {
            return _children
        }
        set(value) {
            _children = value
        }

    fun addChild(enabled: Boolean, child: Node): Unit {
        _children.addLast(
            Child(
                child, if (enabled) 1 else 0
            )
        )
    }

    fun addChild(mask: Int, child: Node): Unit {
        _children.addLast(Child(child, mask))
    }
}