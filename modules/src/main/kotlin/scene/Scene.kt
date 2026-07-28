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
            // Pop from the top.
            when (val current = stack.removeLast()) {
                is StateGroup -> {
                    // visit(current.value)

                    println("found StateGroup")

                    for (i in current._children.indices.reversed()) {
                        // Push children to the stack.
                        // We iterate in reverse so the left-most child is processed first.
                        stack.addLast(current._children[i])
                    }
                }

                is Transform -> {
                    // Recursive traverse over transforms list until
                    // reach some Drawable.
                    digIntoTransform(current)
                }
            }
        }
    }

    private fun digIntoTransform(root: Node): Unit {
        when (root) {
            is Transform -> {
                println("found Transform")

                when (val next = root._child!!) {
                    is Transform -> {
                        // Recursive dig in into Transform chain.
                        digIntoTransform(next)
                    }

                    is Drawable -> {
                        println("found Drawable")
                    }
                }
            }

            else -> {
                println("nothing attached to this transform")
            }
        }
    }
}
