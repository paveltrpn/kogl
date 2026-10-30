package algebra

import kotlin.test.*

private const val VECTOR_TEST_EPSILON = 1e-5f

private fun assertVector2Close(expected: Vector2, actual: Vector2): Unit {
    assertEquals(expected.x, actual.x, VECTOR_TEST_EPSILON)
    assertEquals(expected.y, actual.y, VECTOR_TEST_EPSILON)
}

private fun assertVector3Close(expected: Vector3, actual: Vector3): Unit {
    assertEquals(expected.x, actual.x, VECTOR_TEST_EPSILON)
    assertEquals(expected.y, actual.y, VECTOR_TEST_EPSILON)
    assertEquals(expected.z, actual.z, VECTOR_TEST_EPSILON)
}

// ============================================================================
// ======================= VectorTest =========================================
// ============================================================================
class VectorTest {
    @Test
    fun vector2ArithmeticIsNonMutating(): Unit {
        val value = Vector2(3.0f, -4.0f)
        val other = Vector2(-2.0f, 5.0f)

        assertVector2Close(Vector2(1.0f, 1.0f), value.added(other))
        assertVector2Close(Vector2(5.0f, -9.0f), value.subtracted(other))
        assertVector2Close(Vector2(6.0f, -8.0f), value.scale(2.0f))
        assertEquals(-26.0f, value.dot(other), VECTOR_TEST_EPSILON)
        assertEquals(25.0f, value.lengthSquared(), VECTOR_TEST_EPSILON)
        assertEquals(5.0f, value.length(), VECTOR_TEST_EPSILON)
        assertVector2Close(Vector2(3.0f, -4.0f), value)
        assertVector2Close(Vector2(-2.0f, 5.0f), other)
    }

    @Test
    fun vector3ArithmeticIsNonMutating(): Unit {
        val value = Vector3(1.0f, 2.0f, 3.0f)
        val other = Vector3(-4.0f, 5.0f, -6.0f)

        assertVector3Close(Vector3(-3.0f, 7.0f, -3.0f), value.added(other))
        assertVector3Close(Vector3(5.0f, -3.0f, 9.0f), value.subtracted(other))
        assertVector3Close(Vector3(-2.0f, -4.0f, -6.0f), value.scale(-2.0f))
        assertEquals(-12.0f, value.dot(other), VECTOR_TEST_EPSILON)
        assertVector3Close(Vector3(-27.0f, -6.0f, 13.0f), value.cross(other))
        assertEquals(14.0f, value.lengthSquared(), VECTOR_TEST_EPSILON)
        assertEquals(kotlin.math.sqrt(14.0f), value.length(), VECTOR_TEST_EPSILON)
        assertVector3Close(Vector3(1.0f, 2.0f, 3.0f), value)
        assertVector3Close(Vector3(-4.0f, 5.0f, -6.0f), other)
    }

    @Test
    fun vector3CopyIsIndependent(): Unit {
        val original = Vector3(1.0f, 2.0f, 3.0f)
        val copy = original.copy()

        copy.set(7.0f, 8.0f, 9.0f)

        assertVector3Close(Vector3(1.0f, 2.0f, 3.0f), original)
        assertVector3Close(Vector3(7.0f, 8.0f, 9.0f), copy)
    }

    @Test
    fun vector3PlusAndMinusRemainMutating(): Unit {
        val value = Vector3(1.0f, 2.0f, 3.0f)

        val plusResult = value + Vector3(3.0f, 2.0f, 1.0f)
        assertSame(value, plusResult)
        assertVector3Close(Vector3(4.0f, 4.0f, 4.0f), value)

        val minusResult = value - Vector3(1.0f, 1.0f, 1.0f)
        assertSame(value, minusResult)
        assertVector3Close(Vector3(3.0f, 3.0f, 3.0f), value)
    }
}
