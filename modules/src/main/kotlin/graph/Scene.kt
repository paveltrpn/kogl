package graph

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

    override fun apply(node: SwitchGroup): Unit {
        node.traverse(this)
    }

    override fun apply(node: StateGroup): Unit {
        _program = node.program

        _program.use()

        node.traverse(this)
    }

    override fun apply(node: Group): Unit {
        node.traverse(this)
    }

    override fun apply(node: TransformGroup): Unit {
        _modelMatrixStack.push(node)
        node.traverse(this)
        _modelMatrixStack.pop()
    }

    override fun apply(node: Leaf): Unit {
        when (node) {
            is Drawable -> {
                when (node) {
                    is FlyaroundDrawable -> {
                        val local = node.updateLocal(_delta)

                        val top = _modelMatrixStack.top
                        top.transpose()

                        node.applyTransform(local.multiply(top))
                    }

                    is Drawable -> {
                        val top = _modelMatrixStack.top
                        top.transpose()

                        node.applyTransform(top)
                    }
                }

                // ...update shader uniform...
                _program.setMatrixUniform("view_matrix", false, _viewMatrix)
                _program.setMatrixUniform("drawable_matrix", false, node.combined)
                _program.setVectorUniform("color", node.color)

                // ...and draw call.
                node.draw()
            }

//            is -> OtherLeaf {
//
//            }
        }
    }
}

// ============================================================================
// ======================= Scene ==============================================
// ============================================================================

class Scene {
    private var _locales: MutableList<Locale> = mutableListOf()

    private var _camera = Flycam()

    init {
        _camera.apply {
            fov = 45.0f
            aspect = 16.0f / 9.0f
            ncp = 0.1f
            fcp = 100.0f

            eye = Vector3(-18.0f, -13.5f, -14.5f)
            azimuth = 54.0f
            elevation = -31.0f
        }
    }

    val camera: Flycam
        get(): Flycam {
            return _camera
        }

    fun walk(): Unit {
        _camera.traverse()

        val r = GraphRecordVisitor(1.0f, _camera.matrix)

        for (locale in _locales) {
            locale.root.accept(r)
        }
    }

    fun addLocale(locale: Locale): Unit {
        _locales.addLast(locale)
    }
}
