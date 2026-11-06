package events

// ============================================================================
// ======================= Event ==============================================
// ============================================================================

interface Event

// ============================================================================
// ======================= EventBusNode =======================================
// ============================================================================

interface EventBusNode {
    fun onEvent(event: Event): Unit
}

// ============================================================================
// ======================= EventBus ===========================================
// ============================================================================

class EventBus {
    private val _events: ArrayDeque<Event> = ArrayDeque()
    private val _subscriptions: MutableList<Pair<Class<out Event>, EventBusNode>> = mutableListOf()

    fun publish(event: Event): Unit {
        _events.addLast(event)
    }

    fun <T : Event> subscribe(eventType: Class<T>, node: EventBusNode): Unit {
        _subscriptions.add(eventType to node)
    }

    inline fun <reified T : Event> subscribe(node: EventBusNode): Unit {
        subscribe(T::class.java, node)
    }

    fun dispatch(): Unit {
        val pendingEventCount = _events.size

        repeat(pendingEventCount) { _: Int ->
            val event = _events.removeFirst()

            for (subscription in _subscriptions) {
                if (subscription.first.isInstance(event)) {
                    subscription.second.onEvent(event)
                }
            }
        }
    }
}
