package event

enum class KeyAction {
    PRESS, RELEASE
}

interface EventBase {}

data class EventKey(val keyAction: KeyAction, val key: Int) : EventBase {}

class EventMouse(
    val xpos: Double,
    val ypos: Double,
    val xoffst: Double,
    val yoffst: Double,
    val keyAction: KeyAction,
    val key: Int
) :
    EventBase {}

