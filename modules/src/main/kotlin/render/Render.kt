package render

import org.lwjgl.opengl.GL46.*
import event.*
import scene.*

class Render : EventObserver {
    private var _run: Boolean
    private var _scene = Scene()

    init {
        _run = true

        val testCubes = testCubesGraph()

        _scene.addStateGroup(testCubes)

        _scene.walk()
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

        // _scene.walk()
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
        
        val cubeStatic = StaticDrawable()
        val cubeSpin = SpinableDrawable()

        val offsetOne = Transform()
        val offsetTwo = Transform()

        offsetOne.addChild(cubeStatic)
        offsetTwo.addChild(cubeSpin)

        colorStateGroup.addChild(offsetOne)
        colorStateGroup.addChild(offsetTwo)

        return colorStateGroup
    }
}
