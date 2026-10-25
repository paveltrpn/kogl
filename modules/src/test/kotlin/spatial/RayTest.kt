package spatial

import algebra.*
import graph.*
import mesh.*
import kotlin.test.*

private const val RAY_TEST_EPSILON = 1e-4f

private fun assertRayClose(expected: Float, actual: Float): Unit {
    assertTrue(
        kotlin.math.abs(expected - actual) <= RAY_TEST_EPSILON,
        "expected $expected but was $actual"
    )
}

private fun rayBounds(
    minX: Float,
    minY: Float,
    minZ: Float,
    maxX: Float,
    maxY: Float,
    maxZ: Float
): AABB {
    return AABB(Vector3(minX, minY, minZ), Vector3(maxX, maxY, maxZ))
}

private fun screenCamera(): Flycam {
    val camera = Flycam()
    camera.fov = 45.0f
    camera.aspect = 1.0f
    camera.ncp = 0.1f
    camera.fcp = 100.0f
    return camera
}

// ============================================================================
// ======================= RayTest ============================================
// ============================================================================

class RayTest {
    @Test
    fun copiesOriginAndNormalizesDirection(): Unit {
        val eye = Vector3(1.0f, 2.0f, 3.0f)
        val direction = Vector3(0.0f, 0.0f, 12.0f)
        val ray = Ray(eye, direction)

        eye.set(8.0f, 9.0f, 10.0f)
        direction.set(1.0f, 0.0f, 0.0f)

        assertRayClose(1.0f, ray.eye.x)
        assertRayClose(2.0f, ray.eye.y)
        assertRayClose(3.0f, ray.eye.z)
        assertRayClose(0.0f, ray.direction.x)
        assertRayClose(0.0f, ray.direction.y)
        assertRayClose(1.0f, ray.direction.z)
    }

    @Test
    fun buildsCenterAndOffCenterScreenRaysWithYFlip(): Unit {
        val camera = screenCamera()
        val center = Ray.fromScreen(camera, 50, 50, 100, 100)
        val right = Ray.fromScreen(camera, 75, 50, 100, 100)
        val left = Ray.fromScreen(camera, 25, 50, 100, 100)
        val top = Ray.fromScreen(camera, 50, 25, 100, 100)
        val bottom = Ray.fromScreen(camera, 50, 75, 100, 100)

        assertNotNull(center)
        assertNotNull(right)
        assertNotNull(left)
        assertNotNull(top)
        assertNotNull(bottom)

        assertRayClose(0.0f, center.direction.x)
        assertRayClose(0.0f, center.direction.y)
        assertTrue(center.direction.z < 0.0f)
        assertTrue(right.direction.x > 0.0f)
        assertTrue(left.direction.x < 0.0f)
        assertTrue(top.direction.y > 0.0f)
        assertTrue(bottom.direction.y < 0.0f)
        assertTrue(center.near < center.far)
        assertRayClose(1.0f, center.direction.length())
    }

    @Test
    fun screenRayUsesACopiedCameraEye(): Unit {
        val camera = screenCamera()
        camera.eye = Vector3(2.0f, 3.0f, 4.0f)
        val ray = Ray.fromScreen(camera, 50, 50, 100, 100)

        assertNotNull(ray)
        camera.eye.set(7.0f, 8.0f, 9.0f)
        assertRayClose(2.0f, ray.eye.x)
        assertRayClose(3.0f, ray.eye.y)
        assertRayClose(4.0f, ray.eye.z)
    }

    @Test
    fun rejectsInvalidScreenInputsAndSingularCameraMatrices(): Unit {
        val camera = screenCamera()

        assertNull(Ray.fromScreen(camera, 0, 0, 0, 100))
        assertNull(Ray.fromScreen(camera, 0, 0, 100, -1))
        assertNull(Ray.fromScreen(camera, -1, 0, 100, 100))
        assertNull(Ray.fromScreen(camera, 0, -1, 100, 100))
        assertNull(Ray.fromScreen(camera, 100, 0, 100, 100))
        assertNull(Ray.fromScreen(camera, 0, 100, 100, 100))

        camera.fcp = camera.ncp
        assertNull(Ray.fromScreen(camera, 50, 50, 100, 100))
    }

    @Test
    fun aabbTestHandlesParallelInsideBehindAndLimitedRays(): Unit {
        val ray = Ray(Vector3(), Vector3(0.0f, 0.0f, 1.0f), 1.0f, 5.0f)
        val box = rayBounds(-1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 5.0f)

        assertTrue(ray.intersects(box))
        assertFalse(ray.intersects(rayBounds(-1.0f, -1.0f, -3.0f, 1.0f, 1.0f, -2.0f)))
        assertFalse(ray.intersects(rayBounds(-1.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.5f)))
        assertFalse(ray.intersects(rayBounds(-1.0f, -1.0f, 6.0f, 1.0f, 1.0f, 7.0f)))

        val inside = Ray(Vector3(), Vector3(0.0f, 0.0f, 1.0f), 0.0f, 10.0f)
        assertTrue(inside.intersects(rayBounds(-1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f)))

        val parallelInside = Ray(Vector3(0.5f, 0.0f, 0.0f), Vector3(0.0f, 0.0f, 1.0f))
        val parallelOutside = Ray(Vector3(2.0f, 0.0f, 0.0f), Vector3(0.0f, 0.0f, 1.0f))
        assertTrue(parallelInside.intersects(box))
        assertFalse(parallelOutside.intersects(box))
    }

    @Test
    fun triangleTestAcceptsBothFacesAndRespectsNearAndFar(): Unit {
        val ray = Ray(Vector3(), Vector3(0.0f, 0.0f, 1.0f), 1.0f, 4.0f)
        val a = Vector3(-1.0f, -1.0f, 3.0f)
        val b = Vector3(1.0f, -1.0f, 3.0f)
        val c = Vector3(0.0f, 1.0f, 3.0f)

        val front = ray.intersectTriangle(a, b, c)
        val back = ray.intersectTriangle(a, c, b)
        assertNotNull(front)
        assertNotNull(back)
        assertRayClose(3.0f, front)
        assertRayClose(3.0f, back)

        assertNull(
            ray.intersectTriangle(
                Vector3(-1.0f, -1.0f, 0.5f),
                Vector3(1.0f, -1.0f, 0.5f),
                Vector3(0.0f, 1.0f, 0.5f)
            )
        )
        assertNull(
            ray.intersectTriangle(
                Vector3(-1.0f, -1.0f, 5.0f),
                Vector3(1.0f, -1.0f, 5.0f),
                Vector3(0.0f, 1.0f, 5.0f)
            )
        )
    }

    @Test
    fun triangleTestRejectsBehindOutsideAndDegenerateGeometry(): Unit {
        val ray = Ray(Vector3(), Vector3(0.0f, 0.0f, 1.0f), 0.0f, 10.0f)
        val a = Vector3(-1.0f, -1.0f, 3.0f)
        val b = Vector3(1.0f, -1.0f, 3.0f)
        val c = Vector3(0.0f, 1.0f, 3.0f)
        val outside = Ray(Vector3(2.0f, 2.0f, 0.0f), Vector3(0.0f, 0.0f, 1.0f))
        val degenerateRay = Ray(Vector3(), Vector3())

        assertNull(ray.intersectTriangle(a, a, c))
        assertNull(outside.intersectTriangle(a, b, c))
        assertNull(degenerateRay.intersectTriangle(a, b, c))
        assertFalse(degenerateRay.intersects(rayBounds(-1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f)))
        assertNull(
            ray.intersectTriangle(
                Vector3(-1.0f, -1.0f, -3.0f),
                Vector3(1.0f, -1.0f, -3.0f),
                Vector3(0.0f, 1.0f, -3.0f)
            )
        )
    }

    @Test
    fun intersectionTestsDoNotMutateInputs(): Unit {
        val ray = Ray(Vector3(), Vector3(0.0f, 0.0f, 3.0f), 0.0f, 10.0f)
        val bounds = rayBounds(-1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 4.0f)
        val a = Vector3(-1.0f, -1.0f, 3.0f)
        val b = Vector3(1.0f, -1.0f, 3.0f)
        val c = Vector3(0.0f, 1.0f, 3.0f)
        val originalBoundsMin = bounds.min.data.copyOf()
        val originalBoundsMax = bounds.max.data.copyOf()
        val originalA = a.data.copyOf()
        val originalB = b.data.copyOf()
        val originalC = c.data.copyOf()
        val originalEye = ray.eye.data.copyOf()
        val originalDirection = ray.direction.data.copyOf()

        assertTrue(ray.intersects(bounds))
        assertNotNull(ray.intersectTriangle(a, b, c))

        assertContentEquals(originalBoundsMin, bounds.min.data)
        assertContentEquals(originalBoundsMax, bounds.max.data)
        assertContentEquals(originalA, a.data)
        assertContentEquals(originalB, b.data)
        assertContentEquals(originalC, c.data)
        assertContentEquals(originalEye, ray.eye.data)
        assertContentEquals(originalDirection, ray.direction.data)
    }
}
