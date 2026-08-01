package render

import org.lwjgl.opengl.GL46.*

import algebra.Matrix4
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
        val colorShader = ShaderSource("color")
        val colorProgram = Program(colorShader)

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
        cubeSpin.spin = Matrix4()

        val offsetOne = Transform()
        val offsetTwo = Transform()

        offsetOne.addChild(cubeStatic)
        offsetTwo.addChild(cubeSpin)

        colorStateGroup.addChild(offsetOne)
        colorStateGroup.addChild(offsetTwo)

        return colorStateGroup
    }
}
