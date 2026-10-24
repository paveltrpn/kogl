package graph

import algebra.*
import mesh.*
import kotlin.test.*

private fun _bounds(
    minX: Float,
    minY: Float,
    minZ: Float,
    maxX: Float,
    maxY: Float,
    maxZ: Float
): AABB {
    return AABB(
        Vector3(minX, minY, minZ),
        Vector3(maxX, maxY, maxZ)
    )
}

private fun _point(x: Float, y: Float, z: Float): AABB {
    return _bounds(x, y, z, x, y, z)
}

private fun _shaderOffset(x: Float, y: Float, z: Float): Matrix4 {
    val result = algebra.offset(x, y, z)
    result.transpose()
    return result
}

// ============================================================================
// ======================= FrustumTest ========================================
// ============================================================================
class FrustumTest {
    private val _unitBox = _bounds(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f)

    @Test
    fun reportsBoxInsideClipVolume(): Unit {
        val frustum = Frustum(Matrix4())

        assertTrue(frustum.intersects(_unitBox, Matrix4()))
    }

    @Test
    fun cullsBoxesOutsideEveryClipPlane(): Unit {
        val frustum = Frustum(Matrix4())
        val outsideBoxes = listOf(
            "left" to _bounds(-3.0f, -0.5f, -0.5f, -2.0f, 0.5f, 0.5f),
            "right" to _bounds(2.0f, -0.5f, -0.5f, 3.0f, 0.5f, 0.5f),
            "bottom" to _bounds(-0.5f, -3.0f, -0.5f, 0.5f, -2.0f, 0.5f),
            "top" to _bounds(-0.5f, 2.0f, -0.5f, 0.5f, 3.0f, 0.5f),
            "near" to _bounds(-0.5f, -0.5f, -3.0f, 0.5f, 0.5f, -2.0f),
            "far" to _bounds(-0.5f, -0.5f, 2.0f, 0.5f, 0.5f, 3.0f)
        )

        for ((plane, bounds) in outsideBoxes) {
            assertFalse(
                frustum.intersects(bounds, Matrix4()),
                "Box outside the $plane plane should not intersect"
            )
        }
    }

    @Test
    fun reportsBoxesTouchingEveryClipPlane(): Unit {
        val frustum = Frustum(Matrix4())
        val boundaryBoxes = listOf(
            _bounds(-2.0f, -0.5f, -0.5f, -1.0f, 0.5f, 0.5f),
            _bounds(1.0f, -0.5f, -0.5f, 2.0f, 0.5f, 0.5f),
            _bounds(-0.5f, -2.0f, -0.5f, 0.5f, -1.0f, 0.5f),
            _bounds(-0.5f, 1.0f, -0.5f, 0.5f, 2.0f, 0.5f),
            _bounds(-0.5f, -0.5f, -2.0f, 0.5f, 0.5f, -1.0f),
            _bounds(-0.5f, -0.5f, 1.0f, 0.5f, 0.5f, 2.0f)
        )

        for (bounds in boundaryBoxes) {
            assertTrue(frustum.intersects(bounds, Matrix4()))
        }
    }

    @Test
    fun reportsBoxEnclosingClipVolume(): Unit {
        val frustum = Frustum(Matrix4())
        val enclosingBox = _bounds(-2.0f, -2.0f, -2.0f, 2.0f, 2.0f, 2.0f)

        assertTrue(frustum.intersects(enclosingBox, Matrix4()))
    }

    @Test
    fun handlesDegenerateBoxes(): Unit {
        val frustum = Frustum(Matrix4())
        val intersectingPlane = _bounds(0.0f, -2.0f, -2.0f, 0.0f, 2.0f, 2.0f)
        val outsidePlane = _bounds(-2.0f, -0.5f, -0.5f, -2.0f, 0.5f, 0.5f)

        assertTrue(frustum.intersects(_point(0.0f, 0.0f, 0.0f), Matrix4()))
        assertFalse(frustum.intersects(_point(2.0f, 0.0f, 0.0f), Matrix4()))
        assertTrue(frustum.intersects(intersectingPlane, Matrix4()))
        assertFalse(frustum.intersects(outsidePlane, Matrix4()))
    }

    @Test
    fun usesScaledAndTranslatedModelMatrices(): Unit {
        val frustum = Frustum(Matrix4())
        val scalableBox = _bounds(0.75f, -0.5f, -0.5f, 1.25f, 0.5f, 0.5f)

        assertTrue(frustum.intersects(scalableBox, Matrix4()))
        assertFalse(frustum.intersects(scalableBox, algebra.scale(2.0f)))
        assertFalse(frustum.intersects(_unitBox, _shaderOffset(3.0f, 0.0f, 0.0f)))
    }

    @Test
    fun usesTranslatedViewMatrix(): Unit {
        val frustum = Frustum(_shaderOffset(-3.0f, 0.0f, 0.0f))
        val translatedBox = _bounds(2.5f, -0.5f, -0.5f, 3.5f, 0.5f, 0.5f)

        assertFalse(frustum.intersects(_unitBox, Matrix4()))
        assertTrue(frustum.intersects(translatedBox, Matrix4()))
    }

    @Test
    fun transposesModelMatrixLikeShaderUpload(): Unit {
        val frustum = Frustum(Matrix4())
        val model = _shaderOffset(2.0f, 0.0f, 0.0f)
        val localPoint = _point(-1.5f, 0.0f, 0.0f)

        assertTrue(frustum.intersects(localPoint, model))
    }

    @Test
    fun doesNotMutateInputMatrices(): Unit {
        val view = _shaderOffset(1.0f, 2.0f, 3.0f)
        val model = algebra.scale(2.0f).multiply(_shaderOffset(4.0f, 5.0f, 6.0f))
        val originalView = view.data.copyOf()
        val originalModel = model.data.copyOf()

        Frustum(view).intersects(_unitBox, model)

        assertContentEquals(originalView, view.data)
        assertContentEquals(originalModel, model.data)
    }
}
