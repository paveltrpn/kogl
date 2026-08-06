package render

import org.lwjgl.opengl.GL46.*

import algebra.Vector3
import config.Config
import event.*
import scene.*
import mesh.*
import kotlin.random.Random

class Render : EventObserver {
    private var _run: Boolean
    private var _scene = Scene()

    init {
        _run = true

        val testCubes = testCubesGraph()

        _scene.addStateGroup(testCubes)
    }

    var run: Boolean
        get(): Boolean {
            return _run
        }
        set(value) {
            _run = value
        }

    fun preLoop(): Unit {

    }

    fun frame(): Unit {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        _scene.walk()
    }

    fun postLoop(): Unit {

    }

    override fun handleEvent(event: EventBase) {
        if (event is EventKey) {
            if (event.keyAction == KeyAction.PRESS) {
                when (event.key) {
                    256 -> {
                        run = false
                    }
                }
            }
        }
    }

    private fun testCubesGraph(): StateGroup {
        val vertices = floatArrayOf(
            // 8 cube vertices
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f
        )

        val indices = intArrayOf(
            // Front face (z = 0.5) - vertices 4,5,6,7
            4, 6, 5, 4, 7, 6,
            // Back face (z = -0.5) - vertices 0,1,2,3
            0, 2, 1, 0, 3, 2,
            // Top face (y = 0.5) - vertices 3,2,6,7
            3, 6, 7, 3, 2, 6,
            // Bottom face (y = -0.5) - vertices 0,1,5,4
            0, 5, 4, 0, 1, 5,
            // Right face (x = 0.5) - vertices 1,2,6,5
            1, 6, 5, 1, 2, 6,
            // Left face (x = -0.5) - vertices 0,3,7,4
            0, 7, 4, 0, 3, 7
        )

        val boxMesh = IndexedMesh("Box", vertices, floatArrayOf(), floatArrayOf(), indices)

        // =================================================

        val pathPrefix = Config.instance().basePath

        val frameObj = readWavefrontObjFile("${pathPrefix}/assets/bodystorage/frame.obj")
        val frameMesh = InterleavedMesh(frameObj)

        val diamondObj = readWavefrontObjFile("${pathPrefix}/assets/bodystorage/diamond.obj")
        val diamondMesh = SeparatedArraysMesh(diamondObj)

        val arch01dObj = readWavefrontObjFile("${pathPrefix}/assets/bodystorage/arch01.obj")
        val arch01dMesh = SeparatedArraysMesh(arch01dObj)

        // =================================================

        val colorShaderSource = ShaderSource("flatshade")
        val colorProgram = Program(colorShaderSource)

        colorProgram.addUniform("view_matrix")
        colorProgram.addUniform("drawable_matrix")

        // colorProgram.addUniform("color")

        val colorStateGroup = StateGroup()
        colorStateGroup.setProgram(colorProgram)

        val sum: (Int, Int) -> Int = { a: Int, b: Int ->
            val result = a + b
            result // The last expression acts as the return value
        }

        val randomFloat: (Float, Float) -> Float = { from: Float, to: Float ->
            from + Random.nextFloat() * (to - from)
        }

        val randomVector3: (Float, Float) -> Vector3 = { from: Float, to: Float ->
            val list = List(3) { from + Random.nextFloat() * (to - from) }
            Vector3(list[0], list[1], list[2])
        }

        for (i in 0..64) {
            val item = SpinableDrawable(diamondMesh)
            item.axis = randomVector3(-0.6f, 0.6f).normalize()
            item.anglSpeed = randomFloat(-1.0f, 1.0f)

            val scale = Transform()
            val sf = randomFloat(0.2f, 0.6f)
            scale.matrix = algebra.scale(sf, sf, sf)

            val offset = Transform()
            val rz = randomFloat(-1.0f, -6.0f)
            val rtv = randomVector3(-4.0f, 4.0f)
            offset.matrix = algebra.offset(rtv.x, rtv.y, rz);

            scale.addChild(item)
            offset.addChild(scale)
            colorStateGroup.addChild(offset)
        }

        for (i in 0..16) {
            val item = SpinableDrawable(frameMesh)
            item.axis = randomVector3(-0.6f, 0.6f).normalize()
            item.anglSpeed = randomFloat(0.4f, 1.2f)

            val scale = Transform()
            val sf = randomFloat(0.8f, 1.8f)
            scale.matrix = algebra.scale(sf, sf, sf)

            val offset = Transform()
            val rz = randomFloat(-1.0f, -6.0f)
            val rtv = randomVector3(-5.0f, 5.0f)
            offset.matrix = algebra.offset(rtv.x, rtv.y, rz);

            scale.addChild(item)
            offset.addChild(scale)
            colorStateGroup.addChild(offset)
        }

        for (i in 0..16) {
            val item = SpinableDrawable(arch01dMesh)
            item.axis = randomVector3(-0.6f, 0.6f).normalize()
            item.anglSpeed = randomFloat(0.4f, 1.2f)

            val scale = Transform()
            val sf = randomFloat(0.8f, 1.4f)
            scale.matrix = algebra.scale(sf, sf, sf)

            val offset = Transform()
            val rz = randomFloat(-1.0f, -6.0f)
            val rtv = randomVector3(-6.0f, 6.0f)
            offset.matrix = algebra.offset(rtv.x, rtv.y, rz);

            scale.addChild(item)
            offset.addChild(scale)
            colorStateGroup.addChild(offset)
        }

        return colorStateGroup
    }
}
