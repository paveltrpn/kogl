package graph

import algebra.*
import render.*

// ============================================================================
// ======================= GraphRecordVisitor =================================
// ============================================================================

class GraphRecordVisitor(delta: Float, viewMatrix: Matrix4) : Visitor() {
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
                with(_program) {
                    set("view_matrix" to _viewMatrix, false)
                    set("model_matrix" to node.modelMatrix, false)
                    set("color" to node.color)
                }

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

/**
 * This object serve as the largest unit of scenegraph representation,
 * and can also be thought of as database. Scene can be very large, both
 * in physical space units and in content.
 */
class Scene {
    private var _locales: MutableList<Locale> = mutableListOf()

    private var _camera = Flycam()

    init {
        with(_camera) {
            fov = 45.0f
            aspect = 16.0f / 9.0f
            ncp = 0.1f
            fcp = 100.0f

            eye = Vector3(-18.0f, -13.5f, -14.5f)
            azimuth = 54.0f
            elevation = -31.0f
        }
    }

    var camera: Flycam
        get(): Flycam {
            return _camera
        }
        set(value) {
            _camera = value
        }

    fun walk(): Unit {
        _camera.traverse()

        val r = GraphRecordVisitor(1.0f, _camera.viewMatrix)

        for (locale in _locales) {
            locale.root.accept(r)
        }
    }

    fun addLocale(locale: Locale): Unit {
        _locales.addLast(locale)
    }

    fun addLocales(locales: List<Locale>): Unit {
        for (locale in locales) {
            _locales.addLast(locale)
        }
    }
}
