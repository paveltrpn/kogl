package events

// ============================================================================
// ======================= AppCommand =========================================
// ============================================================================

sealed class AppCommand : Event {
    data class SetVsync(val enabled: Boolean) : AppCommand()
    data object Exit : AppCommand()
}

// ============================================================================
// ======================= UiNotification =====================================
// ============================================================================

sealed class UiNotification : Event {
    data class VsyncStateChanged(val enabled: Boolean) : UiNotification()
    data class FrameDuration(val milliseconds: Float) : UiNotification()
}
