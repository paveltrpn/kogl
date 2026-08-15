package graph

open class Group : Node {
    protected var _children: MutableList<Node> = mutableListOf()

    override fun accept(visitor: Visitor): Unit {
        visitor.apply(this)
    }

    override fun ascend(visitor: Visitor): Unit {
    }

    override fun traverse(visitor: Visitor): Unit {
    }

    fun addChild(child: Node): Unit {
        _children.addLast(child)
    }
}
