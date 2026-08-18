package scene

import graph.*
import algebra.*
import render.*

class GraphRecordVisitor(delta: Float, viewMatrix: Matrix4) : Visitor {
    private val _delta: Float
    private var _viewMatrix: Matrix4

    private var _program: Program = Program()
    private var _modelMatrixStack = MatrixStack()

    init {
        _delta = delta
        _viewMatrix = viewMatrix
    }

    override fun apply(node: Switch): Unit {
        node.traverse(this)
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
        _modelMatrixStack.push(node)
        node.traverse(this)
        _modelMatrixStack.pop()
    }

    override fun apply(node: Drawable): Unit {
        when (node) {
            is SpinableDrawable -> {
                node.applyTransform(_modelMatrixStack.top)
                node.update(_delta)
            }

            is FlyaroundDrawable -> {
                node.applyTransform(_modelMatrixStack.top)
                node.update(_delta)
            }

            is Drawable -> {
                node.applyTransform(_modelMatrixStack.top)
            }
        }

        // ...update shader uniform...
        _program.setMatrixUniform("view_matrix", false, _viewMatrix)
        _program.setMatrixUniform("drawable_matrix", false, node.combined)
        _program.setVectorUniform("color", node.color)

        // ...and draw call.
        node.draw()
    }
}

// ============================================================================
// ======================= Scene ==============================================
// ============================================================================

class Scene {
    private var _graph: Group = Group()
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

    fun addStateGroup(stateGroup: StateGroup): Unit {
        _graph.addChild(stateGroup)
    }
}
