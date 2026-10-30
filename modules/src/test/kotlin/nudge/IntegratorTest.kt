package nudge

import algebra.*
import kotlin.math.*
import kotlin.test.*

class IntegratorTest {
    @Test
    fun advanceUsesAngularVelocityTimesOldRotation(): Unit {
        val bodies = BodyData(3)
        bodies.momentum[1].velocity = Vector3(1.0f, 0.0f, 0.0f)
        bodies.momentum[1].angularVelocity = Vector3(0.0f, 0.0f, 1.0f)
        bodies.momentum[2].velocity = Vector3(5.0f, 0.0f, 0.0f)

        advance(ActiveBodies(mutableListOf(0, 1)), bodies, 0.5f)

        assertEquals(0.5f, bodies.transforms[1].position.x, 1.0e-6f)
        val expectedLength: Float = sqrt(1.0f + 0.25f * 0.25f)
        assertEquals(0.25f / expectedLength, bodies.transforms[1].rotation.z, 1.0e-6f)
        assertEquals(1.0f / expectedLength, bodies.transforms[1].rotation.w, 1.0e-6f)
        assertEquals(0, bodies.idleCounters[1])
        assertEquals(0.0f, bodies.transforms[0].position.length())
        assertEquals(0.0f, bodies.transforms[2].position.length())
    }

    @Test
    fun idleCounterSaturatesAtSleepSentinel(): Unit {
        val bodies = BodyData(2)
        bodies.idleCounters[1] = 254
        val active = ActiveBodies(mutableListOf(1))

        advance(active, bodies, 0.01f)
        assertEquals(255, bodies.idleCounters[1])
        advance(active, bodies, 0.01f)
        assertEquals(255, bodies.idleCounters[1])

        bodies.momentum[1].angularVelocity = Vector3(1.0f, 0.0f, 0.0f)
        advance(active, bodies, 0.01f)
        assertEquals(0, bodies.idleCounters[1])
    }
}
