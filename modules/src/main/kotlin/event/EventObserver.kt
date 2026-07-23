package tire

interface EventObserver {
    abstract fun handleEvent(event: EventBase) : Unit
};