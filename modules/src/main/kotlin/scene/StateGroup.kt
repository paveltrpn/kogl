package scene

open class StateGroup : Node {
    var _children: MutableList<Node> = mutableListOf()

    override fun traverse(): Unit {

    }

    fun addChild(child: Node): Unit {
        _children.addLast(child)
    }
}
