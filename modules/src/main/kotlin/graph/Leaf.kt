package graph

abstract class Leaf : Node() {
    override fun accept(visitor: Visitor): Unit {
        visitor.apply(this)
    }

    override fun traverse(visitor: Visitor): Unit {
        // NOTE: noop, this is leaf node, no children to traverse.
    }
}
