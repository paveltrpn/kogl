package scene

import graph.*
import algebra.*
import render.*

private class AccumulateTransformVisitor : Visitor {
    private var _accumulatedMatrix: Matrix4

    init {
        _accumulatedMatrix = Matrix4()
        _accumulatedMatrix.idtt()
    }

    val matrix: Matrix4
        get(): Matrix4 {
            return _accumulatedMatrix
        }

    override fun apply(node: Transform): Unit {
        _accumulatedMatrix = _accumulatedMatrix.multiply(node.matrix)
    }
}

private class SpinableVisitor : Visitor {
    override fun apply(node: Drawable): Unit {
        when (node) {
            is SpinableDrawable -> {
                node.update(1.0f)
            }
        }
    }
}

// ============================================================================
// ======================= Scene ==============================================
// ============================================================================

class Scene {
    private var _graph: MutableList<StateGroup> = mutableListOf()
    private var _currentProgram: Program? = null
    private var _camera = Flycam()

    // Accumulate transformations from every Transform
    // on the path.
    private var _matrixAccumulator = AccumulateTransformVisitor()

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
            when (val current = stack.removeLast()) {
                is StateGroup -> {
                    _currentProgram = current.program

                    current.callProgram()

                    for (i in current.children.indices.reversed()) {
                        // Push children to the stack.
                        // We iterate in reverse so the left-most child is processed first.
                        stack.addLast(current.children[i])
                    }
                }

                is Transform -> {
                    _matrixAccumulator = AccumulateTransformVisitor()

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
        node.accept(_matrixAccumulator)

        when (val next = node.child!!) {
            is Transform -> {
                // Recursive dig into Transform chain.
                digIntoTransform(next)
            }

            // Reach Drawable leaf node...
            is Drawable -> {
                val spin = SpinableVisitor()
                next.accept(spin)

                // ...perform transformation...
                next.applyTransform(_matrixAccumulator.matrix)

                // ...update shader uniform...
                _currentProgram?.setMatrixUniform("view_matrix", false, _camera.matrix())
                _currentProgram?.setMatrixUniform("drawable_matrix", false, next.combined)
                _currentProgram?.setVectorUniform("color", next.color)

                // ...applying and draw call.
                next.draw()
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
