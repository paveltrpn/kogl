package scene

import graph.*
import algebra.*
import render.*

// ============================================================================
// ======================= Scene ==============================================
// ============================================================================

class Scene {
    private var _graph: MutableList<StateGroup> = mutableListOf()
    private var _camera = Flycam()

    // Each StateGroup branch data - store current state group shader program
    // and accumulate transformations from every transform on the path.
    //
    // This data reset on every next StateGroup traversal begin.
    private var _transformAccumulator = TransformAccumulateVisitor()
    private var _stateProgram = Program()

    init {
        _camera.fov = 45.0f
        _camera.aspect = 16.0f / 9.0f
        _camera.ncp = 0.1f
        _camera.fcp = 100.0f

        _camera.eye = Vector3(0.0f, 0.0f, -4.0f)
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
            when (val next = stack.removeLast()) {
                is StateGroup -> {
                    // Reset current state group shader program...
                    _stateProgram = next.program

                    // ...and bind it.
                    _stateProgram.use()

                    for (i in next.children.indices.reversed()) {
                        // Push children to the stack.
                        // We iterate in reverse so the left-most child is processed first.
                        stack.addLast(next.children[i])
                    }
                }

                is Group -> {
                    for (i in next.children.indices.reversed()) {
                        // Push children to the stack.
                        // We iterate in reverse so the left-most child is processed first.
                        stack.addLast(next.children[i])
                    }
                }

                is Transform -> {
                    // Reset transform accumulator.
                    _transformAccumulator = TransformAccumulateVisitor()

                    // Recursive traverse over transforms list until
                    // reach some Drawable.
                    digIntoTransform(next)
                }
            }
        }
    }

    // Recursive descend through Transforms list until
    // Drawable leaf node reached.
    private fun digIntoTransform(node: Transform): Unit {
        // Apply transformation matrix from this transform.
        node.accept(_transformAccumulator)

        when (val next = node.child!!) {
            is Transform -> {
                // Recursive dig into Transform chain.
                digIntoTransform(next)
            }

            is Group -> {
                // Traverse Group children.
                for (child in next.children) {
                    child.accept(_transformAccumulator)
                    when (child) {
                        is Transform -> {
                            digIntoTransform(child)
                        }

                        is Drawable -> {
                            val modelMatrix = _transformAccumulator.matrix
                            val viewMatrix = _camera.matrix
                            
                            val update = DrawableTransformVisitor(1.0f, modelMatrix, viewMatrix, _stateProgram)
                            child.accept(update)
                        }
                    }
                }
            }

            // Reach Drawable leaf node...
            is Drawable -> {
                val modelMatrix = _transformAccumulator.matrix
                val viewMatrix = _camera.matrix

                // Perform transformation and draw.
                val update = DrawableTransformVisitor(1.0f, modelMatrix, viewMatrix, _stateProgram)
                next.accept(update)
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
