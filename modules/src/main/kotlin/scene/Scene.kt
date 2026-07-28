package scene

import algebra.*

class Scene {
    private var _graph: MutableList<StateGroup> = mutableListOf()

    private var _transformAccumulator = Matrix4()

    // Iterative traverse over all available state groups and
    // attached nodes. Must be depth-first traversal.
    fun walk(): Unit {
        val stack = ArrayDeque<Node>()

        // Push all state groups to the stack.
        for (node in _graph) {
            stack.addLast(node)
        }

        while (stack.isNotEmpty()) {
            // Pop from the top.
            when (val current = stack.removeLast()) {
                is StateGroup -> {
                    current.traverse()

                    for (i in current.children.indices.reversed()) {
                        // Push children to the stack.
                        // We iterate in reverse so the left-most child is processed first.
                        stack.addLast(current.children[i])
                    }
                }

                is Transform -> {
                    // Reset this Transforms branch state.
                    _transformAccumulator = Matrix4()

                    // Recursive traverse over transforms list until
                    // reach some Drawable.
                    digIntoTransform(current)
                }
            }
        }
    }

    // Recursive descend through Transforms list until
    // Drawable leaf node reached.
    private fun digIntoTransform(root: Transform): Unit {
        root.traverse()

        // Accumulate transformations from every Transform
        // on the path.
        _transformAccumulator = _transformAccumulator.multiply(root.transform)

        when (val next = root.child!!) {
            is Transform -> {
                // Recursive dig into Transform chain.
                digIntoTransform(next)
            }

            is Drawable -> {
                next.traverse()

                // Reach Drawable leaf node, perform transformation
                // applying and draw call.
                next.applyTransform(_transformAccumulator)
                next.drawCall()
            }

            else -> {
                println("nothing attached to this transform")
            }
        }
    }

    fun addStateGroup(stateGroup: StateGroup): Unit {
        _graph.addLast(stateGroup)
    }
}
