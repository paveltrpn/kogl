package graph

abstract class Node {
    abstract fun accept(visitor: Visitor): Unit
    abstract fun traverse(visitor: Visitor): Unit
}