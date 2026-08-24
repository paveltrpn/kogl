package graphdsl

import graph.*

abstract class NodeBuilder {
    abstract fun attach(node: Node): Unit
}

