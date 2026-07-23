package tire

// ============================================================================
// ============== EventEmitter ================================================
// ============================================================================

interface EventEmitter {
    abstract fun attach(observer: EventObserver): Unit
    abstract fun detach(observer: EventObserver): Unit
    abstract fun notify(event: EventBase): Unit
}