package graph

import org.lwjgl.opengl.GL46.*

import algebra.*
import generator.*
import graphdsl.*
import mesh.*
import render.*

// ============================================================================
// ======================= DrawCallVisitorBase ================================
// ============================================================================

abstract class DrawCallVisitorBase(delta: Float, viewMatrix: Matrix4) : TransformVisitor() {
    protected val mDelta: Float
    protected var mViewMatrix: Matrix4
    protected var mProgram: Program = Program()
    protected var mDrawCallsCount = 0

    init {
        mDelta = delta
        mViewMatrix = viewMatrix
    }

    val drawCallsCount: Int
        get(): Int {
            return mDrawCallsCount
        }

    override fun apply(node: Switch): Unit {
        node.traverse(this)
    }

    override fun apply(node: StateGroup): Unit {
        mProgram = node.program

        mProgram.use()

        node.traverse(this)
    }

    override fun apply(node: Group): Unit {
        node.traverse(this)
    }
}

// ============================================================================
// ======================= DrawableVisitor ====================================
// ============================================================================

class DrawableVisitor(
    delta: Float,
    viewMatrix: Matrix4,
    val lights: List<OmniLightData> = emptyList(),
    val lightDummiesVisible: Boolean = true
) : DrawCallVisitorBase(delta, viewMatrix) {
    companion object {
        private const val MAX_LIGHTS = 8

        private val _dummyDrawable: SpinableDrawable by lazy {
            SpinableDrawable().apply {
                mesh = SeparatedArraysMesh(IcoSphereMesh(1.0f, 1).toObjMesh("lightDummy"))
                anglSpeed = 0.0f
            }
        }

        private val _dummyProgram: Program by lazy {
            Program().apply {
                source = ShaderSource("lightDummy")
                build()
            }
        }
    }

    override fun apply(node: StateGroup): Unit {
        mProgram = node.program

        mProgram.use()

        if (mProgram.hasUniform("lightsCount")) {
            val count = minOf(lights.size, MAX_LIGHTS)

            mProgram.set("lightsCount" to count)

            for (i in 0 until count) {
                val light = lights[i]

                mProgram.set("lights[$i].pos" to light.position)
                mProgram.set("lights[$i].color" to light.color)
                mProgram.set("lights[$i].intensity" to light.intensity)
                mProgram.set("lights[$i].radius" to light.radius)
            }
        }

        node.traverse(this)
    }

    override fun apply(node: Leaf): Unit {
        when (node) {
            is OmniLight -> {
                if (lightDummiesVisible) {
                    val position = mModelMatrixStack.top.vecMultiply(Vector3(0.0f, 0.0f, 0.0f))

                    val offset = algebra.offset(position)
                    offset.transpose()

                    val model = algebra.scale(0.3f).multiply(offset)

                    val dummy = _dummyDrawable
                    dummy.modelMatrix = model
                    dummy.color = node.color

                    _dummyProgram.use()

                    with(_dummyProgram) {
                        set("view_matrix" to mViewMatrix, false)
                        set("model_matrix" to dummy.modelMatrix, false)
                        this assign value("color" to dummy.color)
                    }

                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
                    dummy.draw()
                    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)

                    mProgram.use()

                    mDrawCallsCount++
                }
            }

            is Drawable -> {
                when (node) {
                    is SpinableDrawable -> {
                        val local = node.updateLocal(mDelta)

                        val top = mModelMatrixStack.top.copy()
                        top.transpose()

                        node.applyTransform(local.multiply(top))
                    }

                    is FlyaroundDrawable -> {
                        val local = node.updateLocal(mDelta)

                        val top = mModelMatrixStack.top.copy()
                        top.transpose()

                        node.applyTransform(local.multiply(top))
                    }
                }

                // ...update shader uniform...
                with(mProgram) {
                    set("view_matrix" to mViewMatrix, false)
                    this assign value("model_matrix" to node.modelMatrix, false)
                    // set("model_matrix" to node.modelMatrix, false)

                    // set("color" to node.color)
                    this assign value("color" to node.color)
                }

                // ...and draw call.
                node.draw()

                mDrawCallsCount++
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
                with(mProgram) {
                    set("view_matrix" to mViewMatrix, false)
                    set("model_matrix" to Matrix4(), false)

                    set("scale" to 512.0f)
                    set("maxRange" to 256.0f)
                    set("yOffset" to 0.0f)

                    //set("gridSize" to 1.0f)
                    this assign value("gridSize" to 1.0f)

                    set("majorDivisor" to 5.0f)

                    set("lineThickness" to 0.0256f)
                    set("zoomSensitivity" to 0.5f)

                    this assign value("colorMajor" to Vector3(0.7f, 0.1f, 0.1f))
                    this assign value("colorMinor" to Vector3(0.1f, 0.1f, 0.5f))

                    this assign value("cameraPos" to cameraPos)
                }

                glEnable(GL_BLEND)
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
                node.draw()
                glDisable(GL_BLEND)

                mDrawCallsCount++
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
    private var _gridSwitch: Switch
    private var _gridEnabled: Boolean = true

    private var _camera = Flycam()

    private var _drawCallsCount = 0

    var lightDummiesVisible = true

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

            eye = Vector3(-77.0f, -15.5f, -22.5f)
            azimuth = 56.0f
            elevation = -16.0f
        }

        _gridSwitch = Switch()
        _gridSwitch.addChild(true, Grid())

        _grid = buildState {
            stateGroup {
                program = Program().apply {
                    source = ShaderSource("grid")
                    extension(GL_VERTEX_SHADER, "GL_KHR_vulkan_glsl")
                    build()
                }
                this attach _gridSwitch
            }
        }
    }

    fun toggleGridVisibility(): Unit {
        _gridEnabled = !_gridEnabled
        _gridSwitch.setChild(_gridEnabled, 0)
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

        val c = LightCollectorVisitor()
        for (locale in _locales) {
            locale.root.accept(c)
        }

        val r = DrawableVisitor(1.0f, _camera.viewMatrix, c.omniLights, lightDummiesVisible)
        for (locale in _locales) {
            locale.root.accept(r)
        }

        _drawCallsCount = r.drawCallsCount + g.drawCallsCount
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
