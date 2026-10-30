package algebra

import kotlin.test.*

private const val QUATERNION_TEST_EPSILON = 1e-5f

private fun assertQuaternionClose(expected: Quaternion, actual: Quaternion): Unit {
    assertEquals(expected.x, actual.x, QUATERNION_TEST_EPSILON)
    assertEquals(expected.y, actual.y, QUATERNION_TEST_EPSILON)
    assertEquals(expected.z, actual.z, QUATERNION_TEST_EPSILON)
    assertEquals(expected.w, actual.w, QUATERNION_TEST_EPSILON)
}

private fun assertRotatedVectorClose(expected: Vector3, actual: Vector3): Unit {
    assertEquals(expected.x, actual.x, QUATERNION_TEST_EPSILON)
    assertEquals(expected.y, actual.y, QUATERNION_TEST_EPSILON)
    assertEquals(expected.z, actual.z, QUATERNION_TEST_EPSILON)
}

// ============================================================================
// ======================= QuaternionTest =====================================
// ============================================================================
class QuaternionTest {
    @Test
    fun identityIsDefault(): Unit {
        assertQuaternionClose(Quaternion(0.0f, 0.0f, 0.0f, 1.0f), Quaternion())
    }

    @Test
    fun hamiltonProductUsesNudgeOrdering(): Unit {
        val halfAngle = kotlin.math.sqrt(0.5f)
        val rotateX = Quaternion(halfAngle, 0.0f, 0.0f, halfAngle)
        val rotateY = Quaternion(0.0f, halfAngle, 0.0f, halfAngle)

        assertQuaternionClose(
            Quaternion(0.5f, 0.5f, 0.5f, 0.5f),
            rotateX * rotateY
        )
        assertQuaternionClose(
            Quaternion(0.5f, 0.5f, -0.5f, 0.5f),
            rotateY * rotateX
        )
    }

    @Test
    fun rotatesVectorsAndBuildsColumnBasis(): Unit {
        val halfAngle = kotlin.math.sqrt(0.5f)
        val rotation = Quaternion(0.0f, 0.0f, halfAngle, halfAngle)
        val vector = Vector3(1.0f, 0.0f, 0.0f)

        assertRotatedVectorClose(Vector3(0.0f, 1.0f, 0.0f), rotation.rotate(vector))
        assertRotatedVectorClose(Vector3(1.0f, 0.0f, 0.0f), vector)
        assertRotatedVectorClose(Vector3(0.0f, 1.0f, 0.0f), rotation.toMatrix3().column0)
        assertRotatedVectorClose(Vector3(-1.0f, 0.0f, 0.0f), rotation.toMatrix3().column1)
        assertRotatedVectorClose(Vector3(0.0f, 0.0f, 1.0f), rotation.toMatrix3().column2)

        val generalRotation = Quaternion(1.0f, 2.0f, 3.0f, 4.0f).normalized()
        val generalVector = Vector3(-2.0f, 5.0f, 7.0f)
        assertRotatedVectorClose(
            generalRotation.rotate(generalVector),
            generalRotation.toMatrix3().vecMultiply(generalVector)
        )
    }

    @Test
    fun conjugateInverseAndNormalizationAreConsistent(): Unit {
        val value = Quaternion(1.0f, 2.0f, 3.0f, 4.0f)

        assertQuaternionClose(Quaternion(-1.0f, -2.0f, -3.0f, 4.0f), value.conjugate())
        assertQuaternionClose(Quaternion(), value * value.inverse())

        val normalized = value.normalized()
        assertEquals(1.0f, normalized.x * normalized.x + normalized.y * normalized.y +
                normalized.z * normalized.z + normalized.w * normalized.w, QUATERNION_TEST_EPSILON)
        assertQuaternionClose(Quaternion(1.0f, 2.0f, 3.0f, 4.0f), value)

        val normalizedSelf = Quaternion(1.0f, 2.0f, 3.0f, 4.0f)
        normalizedSelf.normalizeSelf()
        assertQuaternionClose(normalized, normalizedSelf)

        val zero = Quaternion(0.0f, 0.0f, 0.0f, 0.0f)
        assertQuaternionClose(Quaternion(), zero.inverse())
        assertQuaternionClose(Quaternion(), zero.normalized())
        zero.normalizeSelf()
        assertQuaternionClose(Quaternion(), zero)
    }

    @Test
    fun integrationMultipliesAngularVelocityOnLeft(): Unit {
        val halfAngle = kotlin.math.sqrt(0.5f)
        val rotation = Quaternion(0.0f, 0.0f, halfAngle, halfAngle)
        val angularVelocity = Quaternion(1.0f, 0.0f, 0.0f, 0.0f)
        val halfTimeStep = 0.25f
        val delta = angularVelocity * rotation
        val integrated = Quaternion(
            rotation.x + delta.x * halfTimeStep,
            rotation.y + delta.y * halfTimeStep,
            rotation.z + delta.z * halfTimeStep,
            rotation.w + delta.w * halfTimeStep
        ).normalized()

        assertQuaternionClose(
            Quaternion(0.17149858f, -0.17149858f, 0.68599434f, 0.68599434f),
            integrated
        )

        val reversedDelta = rotation * angularVelocity
        val reversed = Quaternion(
            rotation.x + reversedDelta.x * halfTimeStep,
            rotation.y + reversedDelta.y * halfTimeStep,
            rotation.z + reversedDelta.z * halfTimeStep,
            rotation.w + reversedDelta.w * halfTimeStep
        ).normalized()
        assertTrue(kotlin.math.abs(integrated.y - reversed.y) > 0.3f)
    }
}
