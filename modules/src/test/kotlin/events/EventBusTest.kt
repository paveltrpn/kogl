package events

import kotlin.test.*

// ============================================================================
// ======================= RecordingNode ======================================
// ============================================================================

private class RecordingNode(
    private val _handler: (Event) -> Unit = { event: Event -> }
) : EventBusNode {
    val receivedEvents: MutableList<Event> = mutableListOf()

    override fun onEvent(event: Event): Unit {
        receivedEvents.add(event)
        _handler(event)
    }
}

// ============================================================================
// ======================= EventBusTest =======================================
// ============================================================================

class EventBusTest {
    @Test
    fun dispatchesEventsInFifoAndSubscriptionOrder(): Unit {
        val eventBus = EventBus()
        val deliveryOrder = mutableListOf<String>()
        val firstNode = RecordingNode { event: Event ->
            val notification = event as UiNotification.FrameDuration
            deliveryOrder.add("first:${notification.milliseconds}")
        }
        val secondNode = RecordingNode { event: Event ->
            val notification = event as UiNotification.FrameDuration
            deliveryOrder.add("second:${notification.milliseconds}")
        }
        val firstEvent = UiNotification.FrameDuration(1.0f)
        val secondEvent = UiNotification.FrameDuration(2.0f)

        eventBus.subscribe<UiNotification>(firstNode)
        eventBus.subscribe<UiNotification>(secondNode)
        eventBus.publish(firstEvent)
        eventBus.publish(secondEvent)
        eventBus.dispatch()

        assertEquals(
            listOf("first:1.0", "second:1.0", "first:2.0", "second:2.0"),
            deliveryOrder
        )
        assertEquals(listOf<Event>(firstEvent, secondEvent), firstNode.receivedEvents)
        assertEquals(listOf<Event>(firstEvent, secondEvent), secondNode.receivedEvents)
    }

    @Test
    fun deliversOnlyMatchingTypesAndIncludesSubtypesForBaseSubscriptions(): Unit {
        val eventBus = EventBus()
        val allEventsNode = RecordingNode()
        val windowEventsNode = RecordingNode()
        val setVsyncNode = RecordingNode()
        val notificationsNode = RecordingNode()
        val setVsyncEvent = AppCommand.SetVsync(true)
        val windowEvent = WindowKeyPressEvent(87)
        val notificationEvent = UiNotification.FrameDuration(16.0f)
        val publishedEvents = listOf<Event>(setVsyncEvent, windowEvent, notificationEvent)

        eventBus.subscribe<Event>(allEventsNode)
        eventBus.subscribe<WindowEvent>(windowEventsNode)
        eventBus.subscribe(AppCommand.SetVsync::class.java, setVsyncNode)
        eventBus.subscribe<UiNotification>(notificationsNode)
        publishedEvents.forEach { event: Event ->
            eventBus.publish(event)
        }
        eventBus.dispatch()

        assertEquals(publishedEvents, allEventsNode.receivedEvents)
        assertEquals(listOf<Event>(windowEvent), windowEventsNode.receivedEvents)
        assertEquals(listOf<Event>(setVsyncEvent), setVsyncNode.receivedEvents)
        assertEquals(listOf<Event>(notificationEvent), notificationsNode.receivedEvents)
    }

    @Test
    fun publishDoesNotDeliverUntilDispatch(): Unit {
        val eventBus = EventBus()
        val node = RecordingNode()
        val event = AppCommand.SetVsync(true)

        eventBus.subscribe<AppCommand.SetVsync>(node)
        eventBus.publish(event)

        assertTrue(node.receivedEvents.isEmpty())

        eventBus.dispatch()

        assertEquals(listOf<Event>(event), node.receivedEvents)
    }

    @Test
    fun defersHandlerPublicationsAndSupportsBidirectionalExchange(): Unit {
        val eventBus = EventBus()
        val commandNode = RecordingNode { event: Event ->
            if (event is AppCommand.SetVsync) {
                eventBus.publish(UiNotification.VsyncStateChanged(event.enabled))
            }
        }
        val notificationNode = RecordingNode { event: Event ->
            if (event is UiNotification.VsyncStateChanged) {
                eventBus.publish(AppCommand.SetVsync(!event.enabled))
            }
        }
        val initialCommand = AppCommand.SetVsync(true)
        val initialNotification = UiNotification.VsyncStateChanged(true)
        val replyCommand = AppCommand.SetVsync(false)

        eventBus.subscribe<AppCommand.SetVsync>(commandNode)
        eventBus.subscribe<UiNotification.VsyncStateChanged>(notificationNode)
        eventBus.publish(initialCommand)
        eventBus.dispatch()

        assertEquals(listOf<Event>(initialCommand), commandNode.receivedEvents)
        assertTrue(notificationNode.receivedEvents.isEmpty())

        eventBus.dispatch()

        assertEquals(listOf<Event>(initialCommand), commandNode.receivedEvents)
        assertEquals(listOf<Event>(initialNotification), notificationNode.receivedEvents)

        eventBus.dispatch()

        assertEquals(listOf<Event>(initialCommand, replyCommand), commandNode.receivedEvents)
        assertEquals(listOf<Event>(initialNotification), notificationNode.receivedEvents)
    }

    @Test
    fun treatsMatchingRegistrationsAsIndependentSubscriptions(): Unit {
        val eventBus = EventBus()
        val node = RecordingNode()
        val event = WindowMoveEvent(10, 20)

        eventBus.subscribe<Event>(node)
        eventBus.subscribe<WindowEvent>(node)
        eventBus.publish(event)
        eventBus.dispatch()

        assertEquals(listOf<Event>(event, event), node.receivedEvents)
    }
}
