package render

import algebra.Vector2
import kotlin.random.Random

import org.lwjgl.opengl.GL46.*

import algebra.Vector3
import config.Config
import event.*
import scene.*
import mesh.*
import ui.*


class Render : EventObserver {
    private var _run: Boolean
    private val _scene: Scene
    private val _ui: UiGL

    init {
        _run = true

        val sparseObjectsGraph = sparseObjectsGraph()
        val testBoxGraph = testCubesGraph()

        _scene = Scene()

        _scene.addStateGroup(sparseObjectsGraph)
        _scene.addStateGroup(testBoxGraph)

        _ui = UiGL()
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

        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LESS)

        _scene.walk()

        glDisable(GL_DEPTH_TEST)

        glEnable(GL_BLEND)
        // glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_COLOR)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC1_ALPHA)

        val TEXT_START_POS_X = -30.0f
        val TEXT_START_POS_Y = 18.0f
        val TEXT_PLANE_Z = 0.5f

        val l1 = Label()
        l1.text = "test string"
        l1.position = Vector2(TEXT_START_POS_X + 0.0f, TEXT_START_POS_Y)
        l1.z = TEXT_PLANE_Z
        l1.letterSpace = 0.1f

        val l2 = Label()
        l2.text = "test string"
        l2.position = Vector2(TEXT_START_POS_X + 0.0f, TEXT_START_POS_Y + 3.5f)
        l2.z = TEXT_PLANE_Z
        l2.letterSpace = 0.2f
        l2.letterScale = 1.8f

        val b1 = Billboard()
        b1.size = Vector2(10.0f, 10.0f)
        b1.position = Vector2(-30.0f, 16.0f)
        b1.z = TEXT_PLANE_Z

        val b2 = Billboard()
        b2.size = Vector2(10.0f, 10.0f)
        b2.position = Vector2(-30.0f, 32.0f)
        b2.z = TEXT_PLANE_Z

        _ui.add(listOf(l1, l2, b1, b2))

        _ui.flush()

        glDisable(GL_BLEND)
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

    private fun sparseObjectsGraph(): StateGroup {
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
        colorProgram.addUniform("color")

        colorProgram.setVectorUniform("color", Vector3(1.0f, 0.0f, 0.0f))

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
            item.color = randomVector3(0.1f, 0.9f)
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
            item.color = randomVector3(0.1f, 0.9f)
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
            item.color = randomVector3(0.1f, 0.9f)
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

        val vnormals = floatArrayOf(
            // Normal for each vertex according to indices
            // Front face triangles (4,6,5, 4,7,6) - normal (0, 0, 1)
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            // Back face triangles (0,2,1, 0,3,2) - normal (0, 0, -1)
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            // Top face triangles (3,6,7, 3,2,6) - normal (0, 1, 0)
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            // Bottom face triangles (0,5,4, 0,1,5) - normal (0, -1, 0)
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            // Right face triangles (1,6,5, 1,2,6) - normal (1, 0, 0)
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            // Left face triangles (0,7,4, 0,3,7) - normal (-1, 0, 0)
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f
        )

        val texCoords = floatArrayOf(
            // UV for each vertex according to indices
            // Front face (4,6,5, 4,7,6)
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            // Back face (0,2,1, 0,3,2)
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            // Top face (3,6,7, 3,2,6)
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            // Bottom face (0,5,4, 0,1,5)
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            // Right face (1,6,5, 1,2,6)
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            // Left face (0,7,4, 0,3,7)
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
        )

        val boxMesh = IndexedMesh("Box", vertices, vnormals, texCoords, indices)
        boxMesh.transform(algebra.rotation(0.0f, 45.0f, 45.0f))

        // =================================================

        val colorShaderSource = ShaderSource("flatshade")
        val colorProgram = Program(colorShaderSource)

        colorProgram.addUniform("view_matrix")
        colorProgram.addUniform("drawable_matrix")
        colorProgram.addUniform("color")

        colorProgram.setVectorUniform("color", Vector3(0.0f, 1.0f, 0.0f))

        val colorStateGroup = StateGroup()
        colorStateGroup.setProgram(colorProgram)

        // =================================================

        val item = StaticDrawable(boxMesh)
        item.color = Vector3(0.0f, 0.0f, 1.0f)

        val scale = Transform()
        scale.matrix = algebra.scale(1.0f, 1.0f, 1.0f)

        val offset = Transform()
        offset.matrix = algebra.offset(0.0f, 0.0f, 0.0f);

        scale.addChild(item)
        offset.addChild(scale)
        colorStateGroup.addChild(offset)

        return colorStateGroup
    }
}
