package graph

abstract class Node {
    open fun accept(visitor: Visitor): Unit {
        visitor.apply(this)
    }

    open fun traverse(visitor: Visitor): Unit {
        // NOTE: noop.
    }
}