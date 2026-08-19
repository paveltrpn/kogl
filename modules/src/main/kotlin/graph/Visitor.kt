package  graph

interface Visitor {
    fun apply(node: Node) = Unit
    fun apply(node: Leaf) = Unit
    fun apply(node: Group) = Unit
    fun apply(node: StateGroup) = Unit
    fun apply(node: TransformGroup) = Unit
    fun apply(node: Drawable) = Unit
    fun apply(node: SwitchGroup) = Unit
}
