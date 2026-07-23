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

class GlobalEventEmitter private constructor() : EventEmitter {
    private val _observers = mutableListOf<EventObserver>()

    companion object {
        @Volatile
        private var instance: GlobalEventEmitter? = null

        fun init(): GlobalEventEmitter {
            return instance ?: synchronized(this) {
                instance ?: GlobalEventEmitter().also { instance = it }
            }
        }

        fun instance(): GlobalEventEmitter {
            return instance ?: throw IllegalStateException(
                "GlobalEventEmitter must be initialized by calling initialize() first."
            )
        }
    }

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