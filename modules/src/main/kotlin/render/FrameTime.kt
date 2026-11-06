package render

// ============================================================================
// ======================= FrameTime ==========================================
// ============================================================================

class FrameTime(
    private val _nanoTimeSource: () -> Long = System::nanoTime
) {
    private var _appStartNanos: Long = 0L
    private var _previousFrameStartNanos: Long = 0L

    fun reset(): Unit {
        val loopStartNanos = _nanoTimeSource()
        _appStartNanos = loopStartNanos
        _previousFrameStartNanos = loopStartNanos
    }

    fun nextFrame(): FrameTimeData {
        val frameStartNanos = _nanoTimeSource()

        val deltaSeconds = ((frameStartNanos - _previousFrameStartNanos) / 1_000_000_000.0).toFloat()
        val elapsedSeconds = (frameStartNanos - _appStartNanos) / 1_000_000_000.0

        _previousFrameStartNanos = frameStartNanos

        return FrameTimeData(deltaSeconds, elapsedSeconds)
    }
}

// ============================================================================
// ======================= FrameTimeData ======================================
// ============================================================================

data class FrameTimeData(
    val deltaSeconds: Float,
    val elapsedSeconds: Double
)
