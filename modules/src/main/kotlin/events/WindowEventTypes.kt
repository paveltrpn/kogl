package events

interface WindowEvent {

}

data class WindowMoveEvent(val posX: Int, val posY: Int) : WindowEvent {}

data class WindowResizeEvent(val width: Int, val height: Int) : WindowEvent {}

data class WindowMousePress(val keyCode: Int) : WindowEvent {}

data class WindowMouseRelease(val keyCode: Int) : WindowEvent {}

data class WindowMouseMoveEvent(val posX: Int, val posY: Int) : WindowEvent {}

data class WindowMouseOffsetEvent(val offsetX: Int, val offsetY: Int) : WindowEvent {}

data class WindowKeyPressEvent(val keyCode: Int) : WindowEvent {}

data class WindowKeyReleaseEvent(val keyCode: Int) : WindowEvent {}
