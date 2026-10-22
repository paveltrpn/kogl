package graph

// ============================================================================
// ======================= Group ==============================================
// ============================================================================

open class Group : Node() {
    protected var mChildren: MutableList<Node> = mutableListOf()

    override fun accept(visitor: Visitor): Unit {
        visitor.apply(this)
    }

    override fun traverse(visitor: Visitor): Unit {
        for (child in mChildren) {
            child.accept(visitor)
        }
    }

    var children: MutableList<Node>
        get(): MutableList<Node> {
            return mChildren
        }
        set(value) {
            mChildren = value
        }

    fun addChild(child: Node): Unit {
        mChildren.addLast(child)
    }

    /**
     * DSL related function.
     */
    infix fun attach(child: Node): Unit {
        addChild(child)
    }

    val cildrenCount: Int
        get(): Int {
            return mChildren.size
        }

    val isEmpty: Boolean
        get(): Boolean {
            return cildrenCount == 0
        }
}

