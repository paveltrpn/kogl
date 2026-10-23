package graph

import org.lwjgl.opengl.GL46.*

import algebra.*
import graphdsl.*
import render.*

// ============================================================================
// ======================= Scene ==============================================
// ============================================================================

class Scene {
    private var _locales: MutableList<Locale> = mutableListOf()

    private var _grid: StateGroup
    private var _gridSwitch: Switch
    private var _gridEnabled: Boolean = false

    private var _aabbEnabled: Boolean = false

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

            eye = Vector3(-17.0f, -13.5f, -29.5f)
            azimuth = -26.0f
            elevation = -22.0f
        }

        _gridSwitch = Switch()
        _gridSwitch.addChild(_gridEnabled, Grid())

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

    fun toggleAABBVisibility(): Unit {
        _aabbEnabled = !_aabbEnabled
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

        var aabbDrawCallsCount = 0

        if (_aabbEnabled) {
            val a = AABBDrawVisitor(1.0f, _camera.viewMatrix)
            for (locale in _locales) {
                locale.root.accept(a)
            }

            aabbDrawCallsCount = a.drawCallsCount
        }

        _drawCallsCount = r.drawCallsCount + g.drawCallsCount + aabbDrawCallsCount
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
