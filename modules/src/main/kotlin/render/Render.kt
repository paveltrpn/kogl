package render

import org.lwjgl.opengl.GL46.*

import algebra.Matrix4
import algebra.Vector3
import event.*
import scene.*

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
        glViewport(0, 0, 1200, 800)
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f)
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
        val colorShaderSource = ShaderSource("flatshade")
        val colorProgram = Program(colorShaderSource)

        colorProgram.addUniform("view_matrix")
        // colorProgram.addUniform("color")

        val colorStateGroup = StateGroup()
        colorStateGroup.setProgram(colorProgram)

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

        val cubeStatic = StaticDrawable(vertices, indices)
        val cubeSpin = SpinableDrawable(vertices, indices)
        cubeSpin.axis = Vector3(0.2f, 0.5f, 0.5f)
        cubeSpin.anglSpeed = 0.8f

        val offsetOne = Transform()
        offsetOne.transform = algebra.offset(2.0f, 0.0f, 0.0f);

        val scale = Transform()
        scale.transform = algebra.scale(2.0f, 2.0f, 2.0f)

        val offsetTwo = Transform()
        offsetTwo.transform = algebra.offset(-2.0f, 0.0f, 0.0f);

        offsetOne.addChild(scale)
        scale.addChild(cubeStatic)

        offsetTwo.addChild(cubeSpin)

        colorStateGroup.addChild(offsetOne)
        colorStateGroup.addChild(offsetTwo)

        return colorStateGroup
    }
}
