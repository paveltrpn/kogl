package scene

open class Transform : Node {
    var _child: Node? = null

    override fun traverse(): Unit {

    }

    // NOTE: Must accept Transform or Drawable.
    fun addChild(child: Node): Unit {
        _child = child
    }
}

