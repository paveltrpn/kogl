package nudge

import algebra.*
import kotlin.math.*
import kotlin.test.*

private const val BOX_TEST_PI = 3.14159265f
private const val BOX_TEST_DISTANCE_TOLERANCE = 1e-3f

class BoxBoxCollisionTest {
    @Test
    fun boxBoxTestCase0(): Unit {
        val bodyTransform = Transform(
            Vector3(-10.3300056f, -0.0125209205f, 20.0851059f),
            Quaternion(0.0419846289f, -0.296176672f, -0.954125523f, -0.0126985274f),
            0
        )
        val first = Transform(Vector3(0.0f, -10.0f, 0.0f), Quaternion(), 0)
        val second = composeTransform(bodyTransform, Transform(Vector3(), Quaternion(), 1))
        val contacts = collideBoxBox(
            worldBox(0, 0, first, Vector3(200.0f, 10.0f, 200.0f)),
            worldBox(1, 1, second, Vector3(0.5f, 1.0f, 0.25f))
        )
        assertEquals(4, contacts.size, "Incorrect number of contacts.")
    }

    @Test
    fun boxBoxFaceFaceTags0(): Unit {
        for (iteration: Int in 0 until (1 shl 12)) {
            val ax = (iteration shr 0) and 3
            val ay = (iteration shr 2) and 3
            val az = (iteration shr 4) and 3
            val bx = (iteration shr 6) and 3
            val by = (iteration shr 8) and 3
            val bz = (iteration shr 10) and 3
            val first = Transform(Vector3(0.0f, 1.0f, 0.0f), Quaternion(), 0)
            val second = Transform(Vector3(0.0f, -1.0f, 0.0f), Quaternion(), 1)
            rotateAxisAngle(first, 0.0f, 0.0f, 1.0f, BOX_TEST_PI * 0.5f * az)
            rotateAxisAngle(first, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.5f * ay)
            rotateAxisAngle(first, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 0.5f * ax)
            rotateAxisAngle(first, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.25f)
            rotateAxisAngle(second, 0.0f, 0.0f, 1.0f, BOX_TEST_PI * 0.5f * bz)
            rotateAxisAngle(second, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.5f * by)
            rotateAxisAngle(second, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 0.5f * bx)
            rotateAxisAngle(second, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 1e-4f)
            val contacts0 = collideBoxes(first, second)

            val reversedFirst = Transform(Vector3(0.0f, -1.0f, 0.0f), Quaternion(), 1)
            val reversedSecond = Transform(Vector3(0.0f, 1.0f, 0.0f), Quaternion(), 0)
            rotateAxisAngle(reversedFirst, 0.0f, 0.0f, 1.0f, BOX_TEST_PI * 0.5f * bz)
            rotateAxisAngle(reversedFirst, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.5f * by)
            rotateAxisAngle(reversedFirst, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 0.5f * bx)
            rotateAxisAngle(reversedSecond, 0.0f, 0.0f, 1.0f, BOX_TEST_PI * 0.5f * az)
            rotateAxisAngle(reversedSecond, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.5f * ay)
            rotateAxisAngle(reversedSecond, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 0.5f * ax)
            rotateAxisAngle(reversedSecond, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 1e-4f)
            rotateAxisAngle(reversedSecond, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.25f)
            val contacts1 = collideBoxes(reversedFirst, reversedSecond)

            assertEquals(8, contacts0.size, "Incorrect number of contacts at iteration $iteration.")
            assertEquals(8, contacts1.size, "Incorrect number of reversed contacts at iteration $iteration.")
            assertSwappedContacts(contacts0, contacts1, iteration)
            assertUniqueFeatureTags(contacts0, iteration)
            assertUniqueFeatureTags(contacts1, iteration)
        }
    }

    @Test
    fun boxBoxFaceFaceTags1(): Unit {
        for (iteration: Int in 0 until (1 shl 12)) {
            val ax = (iteration shr 0) and 3
            val ay = (iteration shr 2) and 3
            val az = (iteration shr 4) and 3
            val bx = (iteration shr 6) and 3
            val by = (iteration shr 8) and 3
            val bz = (iteration shr 10) and 3
            val first = Transform(Vector3(0.0f, 1.0f, 2.0f), Quaternion(), 0)
            val second = Transform(Vector3(0.0f, -1.0f, 0.0f), Quaternion(), 1)
            rotateAxisAngle(first, 0.0f, 0.0f, 1.0f, BOX_TEST_PI * 0.5f * az)
            rotateAxisAngle(first, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.5f * ay)
            rotateAxisAngle(first, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 0.5f * ax)
            rotateAxisAngle(first, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.25f)
            rotateAxisAngle(second, 0.0f, 0.0f, 1.0f, BOX_TEST_PI * 0.5f * bz)
            rotateAxisAngle(second, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.5f * by)
            rotateAxisAngle(second, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 0.5f * bx)
            rotateAxisAngle(second, 1.0f, 0.0f, 0.0f, -BOX_TEST_PI * 1e-5f)
            val contacts0 = collideBoxes(first, second)

            val reversedFirst = Transform(Vector3(0.0f, -1.0f, 0.0f), Quaternion(), 1)
            val reversedSecond = Transform(Vector3(0.0f, 1.0f, 2.0f), Quaternion(), 0)
            rotateAxisAngle(reversedFirst, 0.0f, 0.0f, 1.0f, BOX_TEST_PI * 0.5f * bz)
            rotateAxisAngle(reversedFirst, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.5f * by)
            rotateAxisAngle(reversedFirst, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 0.5f * bx)
            rotateAxisAngle(reversedFirst, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 1e-5f)
            rotateAxisAngle(reversedSecond, 0.0f, 0.0f, 1.0f, BOX_TEST_PI * 0.5f * az)
            rotateAxisAngle(reversedSecond, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.5f * ay)
            rotateAxisAngle(reversedSecond, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 0.5f * ax)
            rotateAxisAngle(reversedSecond, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.25f)
            val contacts1 = collideBoxes(reversedFirst, reversedSecond)

            assertEquals(3, contacts0.size, "Incorrect number of contacts at iteration $iteration.")
            assertEquals(3, contacts1.size, "Incorrect number of reversed contacts at iteration $iteration.")
            assertSwappedContacts(contacts0, contacts1, iteration)
            assertUniqueFeatureTags(contacts0, iteration)
            assertUniqueFeatureTags(contacts1, iteration)
        }
    }

    @Test
    fun boxBoxEdgeEdgeTags(): Unit {
        for (iteration: Int in 0 until (1 shl 12)) {
            val ax = (iteration shr 0) and 3
            val ay = (iteration shr 2) and 3
            val az = (iteration shr 4) and 3
            val bx = (iteration shr 6) and 3
            val by = (iteration shr 8) and 3
            val bz = (iteration shr 10) and 3
            val first = Transform(Vector3(2.2f, 2.2f, 0.0f), Quaternion(), 0)
            val second = Transform(Vector3(), Quaternion(), 1)
            rotateAxisAngle(first, 0.0f, 0.0f, 1.0f, BOX_TEST_PI * 0.5f * az)
            rotateAxisAngle(first, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.5f * ay)
            rotateAxisAngle(first, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 0.5f * ax)
            rotateAxisAngle(first, 1.0f, 0.0f, 0.0f, -BOX_TEST_PI * 1e-4f)
            rotateAxisAngle(first, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.25f)
            rotateAxisAngle(first, 0.0f, 0.0f, 1.0f, BOX_TEST_PI * 0.25f)
            rotateAxisAngle(second, 0.0f, 0.0f, 1.0f, BOX_TEST_PI * 0.5f * bz)
            rotateAxisAngle(second, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.5f * by)
            rotateAxisAngle(second, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 0.5f * bx)
            val contacts0 = collideBoxes(first, second)

            val reversedFirst = Transform(Vector3(), Quaternion(), 1)
            val reversedSecond = Transform(Vector3(2.2f, 2.2f, 0.0f), Quaternion(), 0)
            rotateAxisAngle(reversedFirst, 0.0f, 0.0f, 1.0f, BOX_TEST_PI * 0.5f * bz)
            rotateAxisAngle(reversedFirst, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.5f * by)
            rotateAxisAngle(reversedFirst, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 0.5f * bx)
            rotateAxisAngle(reversedFirst, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 1e-4f)
            rotateAxisAngle(reversedSecond, 0.0f, 0.0f, 1.0f, BOX_TEST_PI * 0.5f * az)
            rotateAxisAngle(reversedSecond, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.5f * ay)
            rotateAxisAngle(reversedSecond, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 0.5f * ax)
            rotateAxisAngle(reversedSecond, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.25f)
            rotateAxisAngle(reversedSecond, 0.0f, 0.0f, 1.0f, BOX_TEST_PI * 0.25f)
            val contacts1 = collideBoxes(reversedFirst, reversedSecond)

            assertEquals(1, contacts0.size, "Incorrect number of contacts at iteration $iteration.")
            assertEquals(1, contacts1.size, "Incorrect number of reversed contacts at iteration $iteration.")
            assertSwappedContacts(contacts0, contacts1, iteration)
        }
    }

    @Test
    fun boxBoxFacesShareTags(): Unit {
        for (iteration: Int in 0 until (1 shl 12)) {
            val ax = (iteration shr 0) and 3
            val ay = (iteration shr 2) and 3
            val az = (iteration shr 4) and 3
            val bx = (iteration shr 6) and 3
            val by = (iteration shr 8) and 3
            val bz = (iteration shr 10) and 3
            val first0 = Transform(Vector3(2.0f, 0.1f, 0.0f), Quaternion(), 0)
            val second0 = Transform(Vector3(), Quaternion(), 1)
            applyFaceShareFirstRotation(first0, ax, ay, az, BOX_TEST_PI * 1e-3f)
            applyBaseRotations(second0, bx, by, bz)
            val contacts0 = collideBoxes(first0, second0)

            val first1 = Transform(Vector3(2.0f, 0.1f, 0.0f), Quaternion(), 0)
            val second1 = Transform(Vector3(), Quaternion(), 1)
            applyFaceShareFirstRotation(first1, ax, ay, az, -BOX_TEST_PI * 1e-3f)
            applyBaseRotations(second1, bx, by, bz)
            val contacts1 = collideBoxes(first1, second1)

            assertEquals(2, contacts0.size, "Incorrect number of contacts at iteration $iteration.")
            assertEquals(2, contacts1.size, "Incorrect number of contacts at iteration $iteration.")
            val labels0 = faceAndVertexLabels(contacts0, iteration)
            val labels1 = faceAndVertexLabels(contacts1, iteration)
            assertEquals(labels0.second, labels1.second, "Vertex not shared between faces at iteration $iteration.")
            assertEquals(labels0.first, labels1.first, "Edge not shared between faces at iteration $iteration.")
        }
    }

    @Test
    fun boxBoxConsistentFaceEdgeTags(): Unit {
        for (iteration: Int in 0 until (1 shl 12)) {
            val ax = (iteration shr 0) and 3
            val ay = (iteration shr 2) and 3
            val az = (iteration shr 4) and 3
            val bx = (iteration shr 6) and 3
            val by = (iteration shr 8) and 3
            val bz = (iteration shr 10) and 3
            val edgeFirst = Transform(Vector3(2.2f, 2.2f, 0.0f), Quaternion(), 0)
            val edgeSecond = Transform(Vector3(), Quaternion(), 1)
            rotateAxisAngle(edgeFirst, 0.0f, 0.0f, 1.0f, BOX_TEST_PI * 0.5f * az)
            rotateAxisAngle(edgeFirst, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.5f * ay)
            rotateAxisAngle(edgeFirst, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 0.5f * ax)
            rotateAxisAngle(edgeFirst, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.25f)
            rotateAxisAngle(edgeFirst, 0.0f, 0.0f, 1.0f, BOX_TEST_PI * 0.25f)
            applyBaseRotations(edgeSecond, bx, by, bz)
            val contacts0 = collideBoxes(edgeFirst, edgeSecond)

            val faceFirst = Transform(Vector3(2.0f, 0.1f, 0.0f), Quaternion(), 0)
            val faceSecond = Transform(Vector3(), Quaternion(), 1)
            rotateAxisAngle(faceFirst, 0.0f, 0.0f, 1.0f, BOX_TEST_PI * 0.5f * az)
            rotateAxisAngle(faceFirst, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.5f * ay)
            rotateAxisAngle(faceFirst, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 0.5f * ax)
            rotateAxisAngle(faceFirst, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.25f)
            applyBaseRotations(faceSecond, bx, by, bz)
            val contacts1 = collideBoxes(faceFirst, faceSecond)

            assertEquals(1, contacts0.size, "Incorrect number of edge contacts at iteration $iteration.")
            assertEquals(2, contacts1.size, "Incorrect number of face contacts at iteration $iteration.")
            val labels = faceAndVertexLabels(contacts1, iteration)
            val vertex = labels.second and 0xffu
            val edge = labels.first
            assertTrue(
                vertex == ((edge shr 0) and 0xffu) ||
                        vertex == ((edge shr 8) and 0xffu) ||
                        vertex == ((edge shr 16) and 0xffu) ||
                        vertex == ((edge shr 24) and 0xffu),
                "Edge does not contain the vertex at iteration $iteration."
            )
            var matches = 0
            for (i: Int in 0..1) {
                if (contacts0[0].bodies.a == contacts1[i].bodies.a &&
                    contacts0[0].bodies.b == contacts1[i].bodies.b
                ) {
                    if (contacts0[0].tag == contacts1[i].tag) {
                        matches++
                    }
                } else {
                    if (contacts0[0].bodies.b == contacts1[i].bodies.a &&
                        contacts0[0].bodies.a == contacts1[i].bodies.b
                    ) {
                        val shifted = (contacts0[0].tag shl 16) or (contacts0[0].tag shr 16)
                        if (shifted == contacts1[i].tag) {
                            matches++
                        }
                    }
                }
            }
            assertEquals(1, matches, "Incorrect number of matches at iteration $iteration.")
        }
    }
}

private fun worldBox(index: Int, tag: Int, transform: Transform, size: Vector3): WorldCollider {
    return WorldCollider(index, tag, transform, BoxCollider(size), null, Vector3(), Vector3())
}

private fun collideBoxes(first: Transform, second: Transform): List<Contact> {
    val size = Vector3(1.125f, 1.125f, 1.125f)
    return collideBoxBox(worldBox(0, 0, first, size), worldBox(1, 1, second, size))
}

private fun rotateAxisAngle(
    transform: Transform,
    axisX: Float,
    axisY: Float,
    axisZ: Float,
    angle: Float
): Unit {
    val sine = sin(angle * 0.5f)
    val cosine = cos(angle * 0.5f)
    val rotation = Quaternion(axisX * sine, axisY * sine, axisZ * sine, cosine).normalized()
    val current = transform.rotation
    transform.rotation = Quaternion(
        current.x * rotation.w + rotation.x * current.w + rotation.y * current.z - rotation.z * current.y,
        current.y * rotation.w + rotation.y * current.w + rotation.z * current.x - rotation.x * current.z,
        current.z * rotation.w + rotation.z * current.w + rotation.x * current.y - rotation.y * current.x,
        rotation.w * current.w - rotation.x * current.x - rotation.y * current.y - rotation.z * current.z
    )
}

private fun applyBaseRotations(transform: Transform, x: Int, y: Int, z: Int): Unit {
    rotateAxisAngle(transform, 0.0f, 0.0f, 1.0f, BOX_TEST_PI * 0.5f * z)
    rotateAxisAngle(transform, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.5f * y)
    rotateAxisAngle(transform, 1.0f, 0.0f, 0.0f, BOX_TEST_PI * 0.5f * x)
}

private fun applyFaceShareFirstRotation(
    transform: Transform,
    x: Int,
    y: Int,
    z: Int,
    epsilonAngle: Float
): Unit {
    applyBaseRotations(transform, x, y, z)
    rotateAxisAngle(transform, 0.0f, 1.0f, 0.0f, BOX_TEST_PI * 0.25f)
    rotateAxisAngle(transform, 0.0f, 1.0f, 0.0f, epsilonAngle)
}

private fun vectorDistanceSquared(a: Vector3, b: Vector3): Float {
    val delta = a.subtracted(b)
    return delta.dot(delta)
}

private fun swapTagHalves(tag: UInt): UInt {
    return (tag shr 16) or (tag shl 16)
}

private fun assertSwappedContacts(contacts0: List<Contact>, contacts1: List<Contact>, iteration: Int): Unit {
    for (i: Int in contacts0.indices) {
        val position0 = contacts0[i].position
        var distance = vectorDistanceSquared(contacts1[0].position, position0)
        var index1 = 0
        for (j: Int in contacts1.indices) {
            assertEquals(contacts0[i].bodies.a, contacts1[j].bodies.b, "Inconsistent bodies at iteration $iteration.")
            assertEquals(contacts0[i].bodies.b, contacts1[j].bodies.a, "Inconsistent bodies at iteration $iteration.")
            val currentDistance = vectorDistanceSquared(contacts1[j].position, position0)
            if (currentDistance < distance) {
                distance = currentDistance
                index1 = j
            }
        }
        assertTrue(distance < BOX_TEST_DISTANCE_TOLERANCE, "No contact match at iteration $iteration: $distance.")
        assertEquals(
            contacts0[i].tag.toUInt(),
            swapTagHalves(contacts1[index1].tag.toUInt()),
            "Inconsistent tags at iteration $iteration."
        )
    }
}

private fun assertUniqueFeatureTags(contacts: List<Contact>, iteration: Int): Unit {
    for (i: Int in contacts.indices) {
        for (j: Int in i + 1 until contacts.size) {
            assertNotEquals(contacts[i].tag.toUInt(), contacts[j].tag.toUInt(), "Duplicate tag at iteration $iteration.")
        }
    }
}

private fun faceAndVertexLabels(contacts: List<Contact>, iteration: Int): Pair<UInt, UInt> {
    var labelEdge = contacts[0].tag.toUInt()
    var labelVertex = contacts[1].tag.toUInt()
    if (contacts[0].bodies.a != 0) {
        labelEdge = swapTagHalves(labelEdge)
    }
    if (contacts[1].bodies.a != 0) {
        labelVertex = swapTagHalves(labelVertex)
    }
    if ((labelVertex shr 16) != 0xffffu) {
        val temporary = labelVertex
        labelVertex = labelEdge
        labelEdge = temporary
    }
    assertEquals(0xffffu, labelVertex shr 16, "Invalid vertex label at iteration $iteration.")
    assertNotEquals(0xffffu, labelEdge shr 16, "Invalid edge label at iteration $iteration.")
    return Pair(labelEdge, labelVertex)
}
