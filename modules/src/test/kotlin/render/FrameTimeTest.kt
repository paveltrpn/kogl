package render

import kotlin.test.*

// ============================================================================
// ======================= FrameTimeTest ======================================
// ============================================================================

class FrameTimeTest {
    @Test
    fun measuresSuccessiveFramesWithoutMutatingEarlierSnapshots(): Unit {
        var timestampNanos = 10_000_000_000L
        val frameTime = FrameTime({ -> timestampNanos })

        frameTime.reset()

        timestampNanos += 250_000_000L
        val firstFrame = frameTime.nextFrame()

        timestampNanos += 500_000_000L
        val secondFrame = frameTime.nextFrame()

        assertEquals(0.5f, secondFrame.deltaSeconds, 1e-6f)
        assertEquals(0.75, secondFrame.elapsedSeconds)
        assertEquals(0.25f, firstFrame.deltaSeconds, 1e-6f)
        assertEquals(0.25, firstFrame.elapsedSeconds)
    }

    @Test
    fun resetRestartsElapsedAndPreviousFrameBaselines(): Unit {
        var timestampNanos = 5_000_000_000L
        val frameTime = FrameTime({ -> timestampNanos })

        frameTime.reset()

        timestampNanos += 2_000_000_000L
        val beforeReset = frameTime.nextFrame()

        assertEquals(2.0f, beforeReset.deltaSeconds, 1e-6f)
        assertEquals(2.0, beforeReset.elapsedSeconds)

        timestampNanos = 40_000_000_000L
        frameTime.reset()

        timestampNanos += 125_000_000L
        val afterReset = frameTime.nextFrame()

        assertEquals(0.125f, afterReset.deltaSeconds, 1e-6f)
        assertEquals(0.125, afterReset.elapsedSeconds)
    }
}
