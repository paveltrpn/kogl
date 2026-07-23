package tire

// ============================================================================
// ============== EventEmitter ================================================
// ============================================================================

interface EventEmitter {
    abstract fun attach(observer: EventObserver): Unit
    abstract fun detach(observer: EventObserver): Unit
    abstract fun notify(event: EventBase): Unit
}

// ============================================================================
// ============== GlobalEventEmitter ==========================================
// ============================================================================

class GlobalEventEmitter : EventEmitter {
    private val _observers = mutableListOf<EventObserver>()

    override fun attach(observer: EventObserver): Unit {
        _observers.add(observer)
    }

    override fun detach(observer: EventObserver): Unit {
        // TODO
    }

    override fun notify(event: EventBase): Unit {
        for (observer in _observers) {
            observer.handleEvent(event)
        }
    }
};