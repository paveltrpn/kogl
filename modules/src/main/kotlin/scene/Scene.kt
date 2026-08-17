package scene

import graph.*
import algebra.*
import render.*

class GraphRecordVisitor(delta: Float, viewMatrix: Matrix4) : Visitor {
    private val _delta: Float
    private var _viewMatrix: Matrix4

    private var _modelMatrix: Matrix4 = Matrix4().idtt()
    private var _program: Program = Program()

    init {
        _delta = delta
        _viewMatrix = viewMatrix
    }

    override fun apply(node: StateGroup): Unit {
        _program = node.program

        // ...and bind it.
        _program.use()

        node.traverse(this)
    }

    override fun apply(node: Group): Unit {
        node.traverse(this)
    }

    override fun apply(node: Transform): Unit {
        _modelMatrix = _modelMatrix.multiply(node.matrix)
        node.traverse(this)
    }

    override fun apply(node: Drawable): Unit {
        when (node) {
            is SpinableDrawable -> {
                node.applyTransform(_modelMatrix)
                node.update(_delta)
            }

            is FlyaroundDrawable -> {
                node.applyTransform(_modelMatrix)
                node.update(_delta)
            }

            is Drawable -> {
                node.applyTransform(_modelMatrix)
            }
        }

        // ...update shader uniform...
        _program.setMatrixUniform("view_matrix", false, _viewMatrix)
        _program.setMatrixUniform("drawable_matrix", false, node.combined)
        _program.setVectorUniform("color", node.color)

        // ...and draw call.
        node.draw()

        _modelMatrix = Matrix4().idtt()
    }
}

// ============================================================================
// ======================= Scene ==============================================
// ============================================================================

class Scene {
    // private var _graph: MutableList<StateGroup> = mutableListOf()

    private var _graph: Group = Group()

    // Each StateGroup branch data - store current state group shader program
    // and accumulate transformations from every transform on the path.
    //
    // This data reset on every next StateGroup traversal begin.
    private var _transformAccumulator = TransformAccumulateVisitor()

    private var _stateProgram = Program()

    private var _camera = Flycam()

    init {
        _camera.apply {
            fov = 45.0f
            aspect = 16.0f / 9.0f
            ncp = 0.1f
            fcp = 100.0f

            eye = Vector3(-4.0f, -6.2f, -7.4f)
            azimuth = 32.0f
            elevation = -36.3f
        }
    }

    val camera: Flycam
        get(): Flycam {
            return _camera
        }

    fun walk2(): Unit {
        _camera.traverse()

        val r = GraphRecordVisitor(1.0f, _camera.matrix)
        _graph.accept(r)
    }

    // Iterative traverse over all available state groups and
    // attached nodes. Must be depth-first traversal.
//    fun walk(): Unit {
//        _camera.traverse()
//
//        val stack = ArrayDeque<Node>()
//
//        // Push all state groups to the stack.
//        for (node in _graph) {
//            stack.addLast(node)
//        }
//
//        while (stack.isNotEmpty()) {
//            // Pop from the top.
//            when (val next = stack.removeLast()) {
//                is StateGroup -> {
//                    // Reset current state group shader program...
//                    _stateProgram = next.program
//
//                    // ...and bind it.
//                    _stateProgram.use()
//
//                    for (i in next.children.indices.reversed()) {
//                        // Push children to the stack.
//                        // We iterate in reverse so the left-most child is processed first.
//                        stack.addLast(next.children[i])
//                    }
//                }
//
//                is Group -> {
//                    for (i in next.children.indices.reversed()) {
//                        // Push children to the stack.
//                        // We iterate in reverse so the left-most child is processed first.
//                        stack.addLast(next.children[i])
//                    }
//                }
//
//                is Transform -> {
//                    // Reset transform accumulator.
//                    _transformAccumulator = TransformAccumulateVisitor()
//
//                    // Recursive traverse over transforms list until
//                    // reach some Drawable.
//                    digIntoTransform(next)
//                }
//            }
//        }
//    }
//
//    // Recursive descend through Transforms list until
//    // Drawable leaf node reached.
//    private fun digIntoTransform(node: Transform): Unit {
//        // Apply transformation matrix from this transform.
//        node.accept(_transformAccumulator)
//
//        when (val next = node.child!!) {
//            is Transform -> {
//                // Recursive dig into Transform chain.
//                digIntoTransform(next)
//            }
//
//            is Group -> {
//                // Traverse Group children.
//                for (child in next.children) {
//                    child.accept(_transformAccumulator)
//                    when (child) {
//                        is Transform -> {
//                            digIntoTransform(child)
//                        }
//
//                        is Drawable -> {
//                            val modelMatrix = _transformAccumulator.matrix
//                            val viewMatrix = _camera.matrix
//
//                            val update = DrawableTransformVisitor(1.0f, modelMatrix, viewMatrix, _stateProgram)
//                            child.accept(update)
//                        }
//                    }
//                }
//            }
//
//            // Reach Drawable leaf node...
//            is Drawable -> {
//                val modelMatrix = _transformAccumulator.matrix
//                val viewMatrix = _camera.matrix
//
//                // Perform transformation and draw.
//                val update = DrawableTransformVisitor(1.0f, modelMatrix, viewMatrix, _stateProgram)
//                next.accept(update)
//            }
//
//            else -> {
//                println("nothing attached to this transform")
//            }
//        }
//    }

    fun addStateGroup(stateGroup: StateGroup): Unit {
        _graph.addChild(stateGroup)
    }
}
