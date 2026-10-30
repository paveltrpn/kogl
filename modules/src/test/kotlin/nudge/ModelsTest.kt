package nudge

import algebra.*
import kotlin.test.*

class ModelsTest {
    @Test
    fun bodyValidationChecksCurrentMutableProperties(): Unit {
        val bodies = BodyData(2)
        bodies.properties[1].inertiaInverse.x = Float.POSITIVE_INFINITY
        assertFailsWith<IllegalArgumentException> { bodies.validate() }
        bodies.properties[1].inertiaInverse.x = 0.0f
        bodies.properties[1].massInverse = -1.0f
        assertFailsWith<IllegalArgumentException> { bodies.validate() }
    }

    @Test
    fun colliderValidationChecksCurrentMutableHalfExtents(): Unit {
        val box = BoxCollider(Vector3(1.0f, 1.0f, 1.0f))
        val boxes = BoxColliders(
            mutableListOf(0),
            mutableListOf(box),
            mutableListOf(Transform())
        )
        box.size.x = -1.0f
        assertFailsWith<IllegalArgumentException> { boxes.validate() }
        box.size.x = Float.NaN
        assertFailsWith<IllegalArgumentException> { boxes.validate() }
    }
}
