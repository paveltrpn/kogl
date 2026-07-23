package tire

enum class KeyAction {
    PRESS, RELEASE
}

interface EventBase {

}

data class EventKey(val keyAction: KeyAction, val key: Int) : EventBase {
}

class EventMouse : EventBase {

}

