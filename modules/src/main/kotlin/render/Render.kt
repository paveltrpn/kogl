package render

import org.lwjgl.opengl.GL46.*
import event.*
import scene.*

class Render : EventObserver {
    private var _run: Boolean
    private var _scene = Scene()

    init {
        _run = true

        // TEST
        var sgOne = StateGroup()
        var sgTwo = StateGroup()

        var daOne = Drawable()
        var daTwo = Drawable()

        var trOne = Transform()
        var trTwo = Transform()

        var trThree = Transform()

        trTwo.addChild(trThree)

        trOne.addChild(daOne)
        trThree.addChild(daTwo)

        sgOne.addChild(trOne)
        sgOne.addChild(trTwo)

        sgTwo.addChild(trOne)
        sgTwo.addChild(trTwo)

        _scene.addStateGroup(sgOne)
        _scene.addStateGroup(sgTwo)

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
}
