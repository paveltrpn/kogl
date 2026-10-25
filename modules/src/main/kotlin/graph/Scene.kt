package graph

import org.lwjgl.opengl.GL46.*

import algebra.*
import graphdsl.*
import render.*
import spatial.*

// ============================================================================
// ======================= Scene ==============================================
// ============================================================================

class Scene {
    private var _locales: MutableList<Locale> = mutableListOf()

    private var _grid: StateGroup
    private var _gridSwitch: Switch
    private var _gridEnabled: Boolean = false

    private var _debugDrawEnabled: Boolean = false
    private var _debugDrawStateGroup: StateGroup

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

        val debugProgram = Program().apply {
            source = ShaderSource("lightDummy")
            build()
        }

        _debugDrawStateGroup = StateGroup(debugProgram)
        _debugDrawStateGroup.addChild(DebugLightDrawable())
        _debugDrawStateGroup.addChild(DebugAABBDrawable())
    }

    fun toggleGridVisibility(): Unit {
        _gridEnabled = !_gridEnabled
        _gridSwitch.setChild(_gridEnabled, 0)
    }

    fun toggleAABBVisibility(): Unit {
        _debugDrawEnabled = !_debugDrawEnabled
    }

    var camera: Flycam
        get(): Flycam {
            return _camera
        }
        set(value) {
            _camera = value
        }

    fun traverse(): Unit {
        _camera.update()

        val viewMatrix = _camera.viewMatrix

        val g = GridDrawVisitor(1.0f, _camera.eye, viewMatrix)
        _grid.accept(g)

        val lightsData = LightCollectorVisitor()
        for (locale in _locales) {
            locale.root.accept(lightsData)
        }

        val a = AnimationVisitor(1.0f)
        for (locale in _locales) {
            locale.root.accept(a)
        }

        val c = FrustumCullingVisitor(viewMatrix)
        for (locale in _locales) {
            locale.root.accept(c)
        }

        val r = DrawableVisitor(1.0f, viewMatrix, lightsData.omniLights)
        for (locale in _locales) {
            locale.root.accept(r)
        }

        var debugDrawCallsCount = 0

        if (_debugDrawEnabled) {
            val collector = DebugDrawInfoCollectorVisitor()
            for (locale in _locales) {
                locale.root.accept(collector)
            }

            val d = DebugDrawVisitor(1.0f, viewMatrix, collector.info, _debugDrawStateGroup.program)
            _debugDrawStateGroup.accept(d)

            debugDrawCallsCount = d.drawCallsCount
        }

        _drawCallsCount = r.drawCallsCount + g.drawCallsCount + debugDrawCallsCount
    }

    fun intersect(
        posX: Int,
        posY: Int,
        viewportWidth: Int,
        viewportHeight: Int
    ): IntersectionResult? {
        val ray = Ray.fromScreen(_camera, posX, posY, viewportWidth, viewportHeight) ?: return null

        val visitor = IntersectionVisitor(ray)

        for (locale in _locales) {
            locale.root.accept(visitor)
        }

        return visitor.result
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
