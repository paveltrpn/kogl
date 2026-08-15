package graph

open class Group : Node() {
    protected var _children: MutableList<Node> = mutableListOf()

    override fun accept(visitor: Visitor): Unit {
        visitor.apply(this)
    }

    override fun traverse(visitor: Visitor): Unit {
        for (child in _children) {
            child.accept(visitor)
        }
    }

    fun addChild(child: Node): Unit {
        _children.addLast(child)
    }
}
