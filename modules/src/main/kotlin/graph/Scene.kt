package graph

import org.lwjgl.opengl.GL46.*

import algebra.*
import graphdsl.buildState
import render.*

// ============================================================================
// ======================= DrawCallVisitorBase ================================
// ============================================================================

abstract class DrawCallVisitorBase(delta: Float, viewMatrix: Matrix4) : Visitor() {
    protected val _delta: Float
    protected var _viewMatrix: Matrix4

    protected var _program: Program = Program()
    protected var _modelMatrixStack = MatrixStack()

    protected var _drawCallsCount = 0

    init {
        _delta = delta
        _viewMatrix = viewMatrix
    }

    val drawCallsCount: Int
        get(): Int {
            return _drawCallsCount
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
}

// ============================================================================
// ======================= DrawableVisitor ====================================
// ============================================================================

class DrawableVisitor(delta: Float, viewMatrix: Matrix4) : DrawCallVisitorBase(delta, viewMatrix) {
    override fun apply(node: Leaf): Unit {
        when (node) {
            is Drawable -> {
                when (node) {
                    is SpinableDrawable -> {
                        val local = node.updateLocal(_delta)

                        val top = _modelMatrixStack.top.copy()
                        top.transpose()

                        node.applyTransform(local.multiply(top))
                    }

                    is FlyaroundDrawable -> {
                        val local = node.updateLocal(_delta)

                        val top = _modelMatrixStack.top.copy()
                        top.transpose()

                        node.applyTransform(local.multiply(top))
                    }
                }

                // ...update shader uniform...
                with(_program) {
                    set("view_matrix" to _viewMatrix, false)
                    set("model_matrix" to node.modelMatrix, false)

                    // set("color" to node.color)
                    this assign value("color" to node.color)
                }

                // ...and draw call.
                node.draw()

                _drawCallsCount++
            }
        }
    }
}

// ============================================================================
// ======================= GridDrawVisitor ====================================
// ============================================================================

class GridDrawVisitor(delta: Float, val cameraPos: Vector3, viewMatrix: Matrix4) :
    DrawCallVisitorBase(delta, viewMatrix) {
    override fun apply(node: Leaf): Unit {
        when (node) {
            is Grid -> {
                with(_program) {
                    set("view_matrix" to _viewMatrix, false)
                    set("model_matrix" to Matrix4(), false)

                    set("scale" to 250.0f)
                    set("yOffset" to 0.0f)

                    set("gridSize" to 1.0f)
                    set("lineThickness" to 0.02f)
                    set("maxRange" to 75.0f)

                    set("zoomSensitivity" to 0.05f)

                    this assign value("colorMajor" to Vector3(0.7f, 0.2f, 0.2f))
                    this assign value("colorMinor" to Vector3(0.2f, 0.2f, 0.5f))
                    set("majorDivisor" to 5.0f)

                    this assign value("cameraPos" to cameraPos)
                }

                node.draw()

                _drawCallsCount++
            }
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

    private var _grid: StateGroup
    private var _camera = Flycam()

    private var _drawCallsCount = 0

    val drawCallsCount: Int
        get(): Int {
            return _drawCallsCount
        }

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

        _grid = buildState {
            stateGroup {
                program = Program().apply {
                    source = ShaderSource("grid")
                    extension(GL_VERTEX_SHADER, "GL_KHR_vulkan_glsl")
                    build()
                }
                this attach Grid()
            }
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

        val g = GridDrawVisitor(1.0f, _camera.eye, _camera.viewMatrix)
        _grid.accept(g)

        val r = DrawableVisitor(1.0f, _camera.viewMatrix)
        for (locale in _locales) {
            locale.root.accept(r)
        }

        _drawCallsCount = r.drawCallsCount
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
