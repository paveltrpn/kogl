package graph

interface Node {
    abstract fun traverse(): Unit
    abstract fun accept(visitor: Visitor): Unit
}