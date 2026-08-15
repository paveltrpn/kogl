package  graph

interface Visitor {
    fun apply(node: Node) = Unit
    fun apply(node: Group) = Unit
    fun apply(node: StateGroup) = Unit
    fun apply(node: Transform) = Unit
    fun apply(node: Drawable) = Unit
}
