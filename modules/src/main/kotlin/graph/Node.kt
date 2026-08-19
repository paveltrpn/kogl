package graph

abstract class Node {
    open fun accept(visitor: Visitor): Unit {
        visitor.apply(this)
    }

    abstract fun traverse(visitor: Visitor): Unit
}