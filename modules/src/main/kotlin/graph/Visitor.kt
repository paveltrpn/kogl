package  graph

import algebra.rotation

interface Visitor {
    fun apply(node: Node) = Unit
    fun apply(node: Drawable) = Unit
    fun apply(node: StateGroup) = Unit
    fun apply(node: Transform) = Unit
}
