package events

interface WindowEventObserver {
    fun onWindowEvent(event: WindowEvent)
}

open class WindowEventEmitter {
    private val _observers: MutableList<WindowEventObserver> = mutableListOf()

    fun attach(observer: WindowEventObserver) {
        _observers.add(observer)
    }

    fun detach(observer: WindowEventObserver) {
        _observers.remove(observer)
    }

    fun notify(event: WindowEvent) {
        _observers.forEach { observer: WindowEventObserver ->
            observer.onWindowEvent(event)
        }
    }
}
