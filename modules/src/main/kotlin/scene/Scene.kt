package scene

class Scene {
    var _graph: MutableList<StateGroup> = mutableListOf()

    // Traverse over all available state groups and
    // attached nodes.
    fun walk(): Unit {
        for (node in _graph) {
            iterativeDfs(node)
        }
    }

    fun addStateGroup(stateGroup: StateGroup): Unit {
        _graph.addLast(stateGroup)
    }

    // Must be depth-first traversal.
    private fun iterativeDfs(root: Node): Unit {
        val stack = ArrayDeque<Node>()

        // Push root to the top of the stack.
        stack.addLast(root)

        while (stack.isNotEmpty()) {
            // Pop from the top
            when (val current = stack.removeLast()) {
                is StateGroup -> {
                    // visit(current.value)

                    println("found Transform")
                    
                    for (i in current._children.indices.reversed()) {
                        // Push children to the stack.
                        // We iterate in reverse so the left-most child is processed first.
                        stack.addLast(current._children[i])
                    }
                }

                is Transform -> {
                    println("found Transform")
                }

                is Drawable -> {
                    println("found Drawable")
                }
            }
        }
    }
}
