package algebra

import kotlin.test.*

private const val MATRIX_TEST_EPSILON = 1e-4f

private fun assertMatrixClose(expected: Matrix4, actual: Matrix4): Unit {
    for (index in 0 until 16) {
        assertTrue(
            kotlin.math.abs(expected.data[index] - actual.data[index]) <= MATRIX_TEST_EPSILON,
            "matrix mismatch at $index: expected ${expected.data[index]} but was ${actual.data[index]}"
        )
    }
}

// ============================================================================
// ======================= Matrix4Test ========================================
// ============================================================================

class Matrix4Test {
    @Test
    fun invertsIdentity(): Unit {
        val identity = Matrix4()
        val inverse = identity.inverted()

        assertNotNull(inverse)
        assertMatrixClose(identity, inverse)
    }

    @Test
    fun invertsAffineMatrix(): Unit {
        val matrix = algebra.scale(2.0f, 3.0f, 4.0f).multiply(
            algebra.offset(5.0f, -7.0f, 11.0f)
        ).multiply(algebra.rotation(Vector3(0.0f, 1.0f, 0.0f), 30.0f))
        val inverse = matrix.inverted()

        assertNotNull(inverse)
        assertMatrixClose(Matrix4(), matrix.multiply(inverse))
        assertMatrixClose(Matrix4(), inverse.multiply(matrix))
    }

    @Test
    fun invertsPerspectiveMatrix(): Unit {
        val matrix = Matrix4().perspective(45.0f, 16.0f / 9.0f, 0.1f, 100.0f)
        val inverse = matrix.inverted()

        assertNotNull(inverse)
        assertMatrixClose(Matrix4(), matrix.multiply(inverse))
        assertMatrixClose(Matrix4(), inverse.multiply(matrix))
    }

    @Test
    fun rejectsSingularAndNonFiniteMatrices(): Unit {
        assertNull(Matrix4().zero().inverted())
        assertNull(algebra.scale(1.0f, 1.0f, 0.0f).inverted())

        val nonFinite = Matrix4()
        nonFinite.data[0] = Float.NaN
        assertNull(nonFinite.inverted())
    }

    @Test
    fun doesNotMutateSourceDuringInversion(): Unit {
        val matrix = algebra.scale(3.0f, 4.0f, 5.0f).multiply(
            algebra.offset(2.0f, -3.0f, 4.0f)
        )
        val original = matrix.data.copyOf()

        assertNotNull(matrix.inverted())
        assertContentEquals(original, matrix.data)
    }
}
