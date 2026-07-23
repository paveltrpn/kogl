package event

interface EventObserver {
    abstract fun handleEvent(event: EventBase): Unit
};