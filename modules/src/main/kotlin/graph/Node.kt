package graph

interface Node {
    abstract fun accept(visitor: Visitor): Unit
    abstract fun ascend(visitor: Visitor): Unit
    abstract fun traverse(visitor: Visitor): Unit
}