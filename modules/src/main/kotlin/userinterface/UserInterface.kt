package userinterface

import events.*
import render.*

// ============================================================================
// ======================= Userinterface ======================================
// ============================================================================
class Userinterface : WindowEventEmitter() {
    private val _nuklearBackend: NuklearGlBackend = NuklearGlBackend()
    private val _nuklearInterface: NuklearUi = NuklearUi(_nuklearBackend)

    private var _windowWidth: Int = 0
    private var _windowHeight: Int = 0

    private var _windowPosX: Int = 0
    private var _windowPosY: Int = 0

    private var _mousePosX: Int = 0
    private var _mousePosY: Int = 0

    private var _holdMouse: Boolean = false

    var windowWidth: Int
        get() = _windowWidth
        set(value) {
            _windowWidth = value
        }

    var windowHeight: Int
        get() = _windowHeight
        set(value) {
            _windowHeight = value
        }

    var windowPosX: Int
        get() = _windowPosX
        set(value) {
            _windowPosX = value
        }

    var mWindowPosY: Int
        get() = _windowPosY
        set(value) {
            _windowPosY = value
        }

    var mMousePosX: Int
        get() = _mousePosX
        set(value) {
            _mousePosX = value
        }

    var mMousePosY: Int
        get() = _mousePosY
        set(value) {
            _mousePosY = value
        }

    var mHoldMouse: Boolean
        get() = _holdMouse
        set(value) {
            _holdMouse = value
        }

    val mUi: UiDrawable
        get(): UiDrawable {
            return _nuklearInterface
        }

    init {
        attach(_nuklearInterface)
    }

    fun update() {
        _nuklearInterface.update()
    }

    fun free() {
        _nuklearInterface.free()
        _nuklearBackend.free()
    }
}
