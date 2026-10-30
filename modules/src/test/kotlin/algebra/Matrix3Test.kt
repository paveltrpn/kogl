package algebra

import kotlin.test.*

private fun assertMatrix3VectorClose(expected: Vector3, actual: Vector3): Unit {
    assertEquals(expected.x, actual.x, 1e-5f)
    assertEquals(expected.y, actual.y, 1e-5f)
    assertEquals(expected.z, actual.z, 1e-5f)
}

// ============================================================================
// ======================= Matrix3Test ========================================
// ============================================================================
class Matrix3Test {
    @Test
    fun constructsRowMajorStorageFromColumns(): Unit {
        val matrix = Matrix3(
            Vector3(1.0f, 2.0f, 3.0f),
            Vector3(4.0f, 5.0f, 6.0f),
            Vector3(7.0f, 8.0f, 9.0f)
        )

        assertContentEquals(
            floatArrayOf(1.0f, 4.0f, 7.0f, 2.0f, 5.0f, 8.0f, 3.0f, 6.0f, 9.0f),
            matrix.data
        )
        assertMatrix3VectorClose(Vector3(1.0f, 2.0f, 3.0f), matrix.vecMultiply(Vector3(1.0f, 0.0f, 0.0f)))
        assertMatrix3VectorClose(Vector3(4.0f, 5.0f, 6.0f), matrix.vecMultiply(Vector3(0.0f, 1.0f, 0.0f)))
        assertMatrix3VectorClose(Vector3(7.0f, 8.0f, 9.0f), matrix.vecMultiply(Vector3(0.0f, 0.0f, 1.0f)))
    }

    @Test
    fun transposedTransformUsesRowsAsColumns(): Unit {
        val matrix = Matrix3(
            Vector3(1.0f, 2.0f, 3.0f),
            Vector3(4.0f, 5.0f, 6.0f),
            Vector3(7.0f, 8.0f, 9.0f)
        )
        val vector = Vector3(2.0f, -3.0f, 5.0f)
        val original = matrix.data.copyOf()

        val transposed = matrix.transposed()
        assertContentEquals(
            floatArrayOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f),
            transposed.data
        )
        assertMatrix3VectorClose(Vector3(25.0f, 29.0f, 33.0f), matrix.vecMultiply(vector))
        assertMatrix3VectorClose(Vector3(11.0f, 23.0f, 35.0f), matrix.vecMultiplyTransposed(vector))
        assertMatrix3VectorClose(matrix.vecMultiplyTransposed(vector), transposed.vecMultiply(vector))
        assertContentEquals(original, matrix.data)
    }

    @Test
    fun multiplyAccumulatesFromZero(): Unit {
        val left = Matrix3(
            Vector3(1.0f, 2.0f, 3.0f),
            Vector3(4.0f, 5.0f, 6.0f),
            Vector3(7.0f, 8.0f, 9.0f)
        )
        val right = Matrix3(
            Vector3(9.0f, 8.0f, 7.0f),
            Vector3(6.0f, 5.0f, 4.0f),
            Vector3(3.0f, 2.0f, 1.0f)
        )
        val expected = Matrix3(
            Vector3(90.0f, 114.0f, 138.0f),
            Vector3(54.0f, 69.0f, 84.0f),
            Vector3(18.0f, 24.0f, 30.0f)
        )

        assertContentEquals(expected.data, left.multiply(right).data)
        assertContentEquals(left.data, Matrix3().multiply(left).data)
        assertContentEquals(left.data, left.multiply(Matrix3()).data)
        assertContentEquals(Matrix3().data, Matrix3().multiply(Matrix3()).data)
    }
}
