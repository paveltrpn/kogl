package scene

import algebra.*

class Scene {
    private var _graph: MutableList<StateGroup> = mutableListOf()
    private var _transformAccumulator = Matrix4()
    private var _camera = Flycam()

    init {
        _camera.fov = 50.0f
        _camera.aspect = 16.0f / 9.0f
        _camera.ncp = 0.1f
        _camera.fcp = 100.0f

        _camera.eye = Vector3(0.0f, 0.0f, -18.0f)
    }

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
                    current.program?.setMatrixUniform("view_matrix", false, _camera.matrix())

                    current.traverse()

                    for (i in current.children.indices.reversed()) {
                        // Push children to the stack.
                        // We iterate in reverse so the left-most child is processed first.
                        stack.addLast(current.children[i])
                    }
                }

                is Transform -> {
                    // Reset this Transforms branch state.
                    _transformAccumulator = algebra.offset(0.0f, 0.0f, 0.0f)

                    // Recursive traverse over transforms list until
                    // reach some Drawable.
                    digIntoTransform(current)
                }
            }
        }
    }

    // Recursive descend through Transforms list until
    // Drawable leaf node reached.
    private fun digIntoTransform(node: Transform): Unit {
        node.traverse()

        // Accumulate transformations from every Transform
        // on the path.
        _transformAccumulator = _transformAccumulator.multiply(node.transform)

        when (val next = node.child!!) {
            is Transform -> {
                // Recursive dig into Transform chain.
                digIntoTransform(next)
            }

            is Drawable -> {
                next.applyTransform(_transformAccumulator)

                // Reach Drawable leaf node, perform transformation
                // applying and draw call.
                next.traverse()
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
