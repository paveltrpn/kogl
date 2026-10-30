package nudge

import algebra.*
import kotlin.math.*
import kotlin.test.*

private const val COLLISION_TEST_TOLERANCE = 1.0e-5f

class CollisionTest {
    @Test
    fun sphereTouchingProducesZeroPenetrationContact(): Unit {
        val first = collisionWorldSphere(0, 0x1234, Transform(Vector3(), Quaternion(), 0), 1.0f)
        val second = collisionWorldSphere(1, 0xabcd, Transform(Vector3(2.0f, 0.0f, 0.0f), Quaternion(), 1), 1.0f)

        val contact = requireNotNull(collideSphereSphere(first, second))

        assertVector(Vector3(1.0f, 0.0f, 0.0f), contact.position)
        assertVector(Vector3(1.0f, 0.0f, 0.0f), contact.normal)
        assertEquals(0.0f, contact.penetration, COLLISION_TEST_TOLERANCE)
        assertEquals(BodyPair(0, 1), contact.bodies)
        assertEquals(0xabcd123400000000uL, contact.tag)
    }

    @Test
    fun separatedSpheresDoNotCollide(): Unit {
        val first = collisionWorldSphere(0, 1, Transform(Vector3(), Quaternion(), 0), 1.0f)
        val second = collisionWorldSphere(1, 2, Transform(Vector3(2.01f, 0.0f, 0.0f), Quaternion(), 1), 1.0f)

        assertNull(collideSphereSphere(first, second))
    }

    @Test
    fun coincidentSpheresUseDeterministicNormal(): Unit {
        val center = Vector3(1.0f, 2.0f, 3.0f)
        val first = collisionWorldSphere(0, 3, Transform(center.copy(), Quaternion(), 0), 1.25f)
        val second = collisionWorldSphere(1, 4, Transform(center.copy(), Quaternion(), 1), 0.75f)

        val contact = requireNotNull(collideSphereSphere(first, second))

        assertVector(Vector3(0.25f, 2.0f, 3.0f), contact.position)
        assertVector(Vector3(1.0f, 0.0f, 0.0f), contact.normal)
        assertEquals(2.0f, contact.penetration, COLLISION_TEST_TOLERANCE)
        assertEquals(BodyPair(0, 1), contact.bodies)
    }

    @Test
    fun boxSphereFaceRegionProducesFaceNormal(): Unit {
        val box = collisionWorldBox(0, 10, Transform(Vector3(), Quaternion(), 0), Vector3(1.0f, 1.0f, 1.0f))
        val sphere = collisionWorldSphere(1, 20, Transform(Vector3(1.25f, 0.0f, 0.0f), Quaternion(), 1), 0.5f)

        val contact = requireNotNull(collideBoxSphere(box, sphere))

        assertVector(Vector3(0.75f, 0.0f, 0.0f), contact.position)
        assertVector(Vector3(1.0f, 0.0f, 0.0f), contact.normal)
        assertEquals(0.25f, contact.penetration, COLLISION_TEST_TOLERANCE)
        assertEquals(BodyPair(0, 1), contact.bodies)
        assertEquals(collisionPairKey(10, 20).toULong() shl 32, contact.tag)
    }

    @Test
    fun boxSphereEdgeRegionProducesEdgeNormal(): Unit {
        val box = collisionWorldBox(0, 10, Transform(Vector3(), Quaternion(), 0), Vector3(1.0f, 1.0f, 1.0f))
        val center = Vector3(1.25f, 1.25f, 0.0f)
        val sphere = collisionWorldSphere(1, 20, Transform(center.copy(), Quaternion(), 1), 0.5f)
        val expectedNormal = Vector3(1.0f, 1.0f, 0.0f).normalize()

        val contact = requireNotNull(collideBoxSphere(box, sphere))

        assertVector(center.subtracted(expectedNormal.scale(0.5f)), contact.position)
        assertVector(expectedNormal, contact.normal)
        assertEquals(0.5f - sqrt(0.125f), contact.penetration, COLLISION_TEST_TOLERANCE)
        assertEquals(BodyPair(0, 1), contact.bodies)
    }

    @Test
    fun boxSphereCornerRegionProducesCornerNormal(): Unit {
        val box = collisionWorldBox(0, 10, Transform(Vector3(), Quaternion(), 0), Vector3(1.0f, 1.0f, 1.0f))
        val center = Vector3(1.25f, 1.25f, 1.25f)
        val sphere = collisionWorldSphere(1, 20, Transform(center.copy(), Quaternion(), 1), 0.5f)
        val expectedNormal = Vector3(1.0f, 1.0f, 1.0f).normalize()

        val contact = requireNotNull(collideBoxSphere(box, sphere))

        assertVector(center.subtracted(expectedNormal.scale(0.5f)), contact.position)
        assertVector(expectedNormal, contact.normal)
        assertEquals(0.5f - sqrt(0.1875f), contact.penetration, COLLISION_TEST_TOLERANCE)
        assertEquals(BodyPair(0, 1), contact.bodies)
    }

    @Test
    fun mixedCollidersUseAllDispatchPathsInDeterministicOrder(): Unit {
        val bodies = BodyData(4)
        val unitBox = BoxCollider(Vector3(1.0f, 1.0f, 1.0f))
        val colliders = ColliderData(
            BoxColliders(
                mutableListOf(10, 11, 12),
                mutableListOf(unitBox, unitBox, unitBox),
                mutableListOf(
                    Transform(Vector3(0.0f, 0.0f, 0.0f), Quaternion(), 0),
                    Transform(Vector3(1.5f, 0.0f, 0.0f), Quaternion(), 1),
                    Transform(Vector3(10.0f, 0.0f, 0.0f), Quaternion(), 0)
                )
            ),
            SphereColliders(
                mutableListOf(20, 21, 22),
                mutableListOf(SphereCollider(0.5f), SphereCollider(1.0f), SphereCollider(1.0f)),
                mutableListOf(
                    Transform(Vector3(11.25f, 0.0f, 0.0f), Quaternion(), 2),
                    Transform(Vector3(20.0f, 0.0f, 0.0f), Quaternion(), 0),
                    Transform(Vector3(21.5f, 0.0f, 0.0f), Quaternion(), 3)
                )
            )
        )

        val result = runCollision(bodies, colliders)
        val pairKeys = result.second.data.map { contact: Contact -> collisionContactPairKey(contact) }

        assertEquals(10, result.second.count)
        assertEquals(
            listOf(collisionPairKey(11, 10), collisionPairKey(12, 20), collisionPairKey(21, 22)),
            pairKeys.distinct()
        )
        assertEquals(8, pairKeys.count { key: Int -> key == collisionPairKey(11, 10) })
        assertEquals(1, pairKeys.count { key: Int -> key == collisionPairKey(12, 20) })
        assertEquals(1, pairKeys.count { key: Int -> key == collisionPairKey(21, 22) })
        assertEquals(listOf(1, 2, 3), result.first.indices)
        assertTrue(result.second.sleepingPairs.isEmpty())
    }

    @Test
    fun colliderPairsOnTheSameBodyAreRemoved(): Unit {
        val bodies = BodyData(2)
        val colliders = ColliderData(
            spheres = SphereColliders(
                mutableListOf(30, 31),
                mutableListOf(SphereCollider(1.0f), SphereCollider(1.0f)),
                mutableListOf(
                    Transform(Vector3(), Quaternion(), 1),
                    Transform(Vector3(0.5f, 0.0f, 0.0f), Quaternion(), 1)
                )
            )
        )

        val result = runCollision(bodies, colliders)

        assertTrue(result.second.data.isEmpty())
        assertTrue(result.second.sleepingPairs.isEmpty())
        assertEquals(listOf(1), result.first.indices)
    }

    @Test
    fun emptyScenesClearOutputs(): Unit {
        for (bodyCount: Int in listOf(0, 1)) {
            val activeBodies = ActiveBodies(mutableListOf(7))
            val contacts = ContactData(mutableListOf(Contact()), mutableListOf(123))

            collide(activeBodies, contacts, BodyData(bodyCount), ColliderData())

            assertTrue(activeBodies.indices.isEmpty())
            assertTrue(contacts.data.isEmpty())
            assertTrue(contacts.sleepingPairs.isEmpty())
        }
    }

    @Test
    fun singleColliderScenesProduceNoPairs(): Unit {
        val dynamicResult = runCollision(
            BodyData(2),
            ColliderData(
                spheres = SphereColliders(
                    mutableListOf(40),
                    mutableListOf(SphereCollider(1.0f)),
                    mutableListOf(Transform(Vector3(1.0f, 2.0f, 3.0f), Quaternion(), 1))
                )
            )
        )
        val staticResult = runCollision(
            BodyData(1),
            ColliderData(
                boxes = BoxColliders(
                    mutableListOf(41),
                    mutableListOf(BoxCollider(Vector3(1.0f, 1.0f, 1.0f))),
                    mutableListOf(Transform(Vector3(), Quaternion(), 0))
                )
            )
        )

        assertTrue(dynamicResult.second.data.isEmpty())
        assertTrue(dynamicResult.second.sleepingPairs.isEmpty())
        assertEquals(listOf(1), dynamicResult.first.indices)
        assertTrue(staticResult.second.data.isEmpty())
        assertTrue(staticResult.second.sleepingPairs.isEmpty())
        assertTrue(staticResult.first.indices.isEmpty())
    }

    @Test
    fun strictAabbOverlapRejectsTouchingSphereCandidates(): Unit {
        val bodies = BodyData(2)
        val colliders = ColliderData(
            spheres = SphereColliders(
                mutableListOf(50, 51),
                mutableListOf(SphereCollider(1.0f), SphereCollider(1.0f)),
                mutableListOf(
                    Transform(Vector3(), Quaternion(), 0),
                    Transform(Vector3(2.0f, 0.0f, 0.0f), Quaternion(), 1)
                )
            )
        )

        val result = runCollision(bodies, colliders)

        assertTrue(result.second.data.isEmpty())
        assertTrue(result.second.sleepingPairs.isEmpty())
        assertEquals(listOf(1), result.first.indices)
    }

    @Test
    fun activeIslandIncludesCollidingSleepingBody(): Unit {
        val bodies = BodyData(3)
        bodies.idleCounters[2] = 255
        val colliders = ColliderData(
            spheres = SphereColliders(
                mutableListOf(60, 61),
                mutableListOf(SphereCollider(1.0f), SphereCollider(1.0f)),
                mutableListOf(
                    Transform(Vector3(), Quaternion(), 1),
                    Transform(Vector3(1.5f, 0.0f, 0.0f), Quaternion(), 2)
                )
            )
        )

        val result = runCollision(bodies, colliders)

        assertEquals(1, result.second.count)
        assertEquals(BodyPair(1, 2), result.second.data[0].bodies)
        assertEquals(0.5f, result.second.data[0].penetration, COLLISION_TEST_TOLERANCE)
        assertEquals(listOf(1, 2), result.first.indices)
        assertTrue(result.second.sleepingPairs.isEmpty())
    }

    @Test
    fun sleepingIslandProducesPairWithoutContactOrActiveBodies(): Unit {
        val bodies = BodyData(3)
        bodies.idleCounters[1] = 255
        bodies.idleCounters[2] = 255
        val colliders = ColliderData(
            spheres = SphereColliders(
                mutableListOf(70, 71),
                mutableListOf(SphereCollider(1.0f), SphereCollider(1.0f)),
                mutableListOf(
                    Transform(Vector3(), Quaternion(), 1),
                    Transform(Vector3(1.5f, 0.0f, 0.0f), Quaternion(), 2)
                )
            )
        )

        val result = runCollision(bodies, colliders)

        assertTrue(result.second.data.isEmpty())
        assertEquals(listOf(71 or (70 shl 16)), result.second.sleepingPairs)
        assertTrue(result.first.indices.isEmpty())
    }

    @Test
    fun customBodyConnectionActivatesSleepingContactIsland(): Unit {
        val bodies = BodyData(3)
        bodies.idleCounters[1] = 255
        val connections = BodyConnections(mutableListOf(BodyPair(1, 2)))
        val colliders = ColliderData(
            spheres = SphereColliders(
                mutableListOf(80, 81),
                mutableListOf(SphereCollider(1.0f), SphereCollider(1.0f)),
                mutableListOf(
                    Transform(Vector3(), Quaternion(), 0),
                    Transform(Vector3(1.5f, 0.0f, 0.0f), Quaternion(), 1)
                )
            )
        )

        val result = runCollision(bodies, colliders, connections)

        assertEquals(1, result.second.count)
        assertEquals(BodyPair(0, 1), result.second.data[0].bodies)
        assertEquals(listOf(1, 2), result.first.indices)
        assertTrue(result.second.sleepingPairs.isEmpty())
    }

    @Test
    fun sleepingPairsUseNormalizedKeysAndUnsignedSorting(): Unit {
        val bodies = BodyData(5)
        for (body: Int in 1 until bodies.count) {
            bodies.idleCounters[body] = 255
        }
        val colliders = ColliderData(
            spheres = SphereColliders(
                mutableListOf(0x8001, 0x8000, 2, 1),
                mutableListOf(SphereCollider(1.0f), SphereCollider(1.0f), SphereCollider(1.0f), SphereCollider(1.0f)),
                mutableListOf(
                    Transform(Vector3(), Quaternion(), 1),
                    Transform(Vector3(1.5f, 0.0f, 0.0f), Quaternion(), 2),
                    Transform(Vector3(10.0f, 0.0f, 0.0f), Quaternion(), 3),
                    Transform(Vector3(11.5f, 0.0f, 0.0f), Quaternion(), 4)
                )
            )
        )

        val result = runCollision(bodies, colliders)

        assertTrue(result.second.data.isEmpty())
        assertEquals(listOf(0x00010002, 0x80008001u.toInt()), result.second.sleepingPairs)
        assertTrue(result.first.indices.isEmpty())
    }

    @Test
    fun collisionDoesNotMutateLocalColliderTransforms(): Unit {
        val bodies = BodyData(2)
        bodies.transforms[0].position = Vector3(1.0f, 2.0f, 3.0f)
        bodies.transforms[0].rotation = Quaternion(0.1f, 0.2f, 0.3f, 0.9f).normalized()
        bodies.transforms[1].position = Vector3(1.5f, 2.0f, 3.0f)
        bodies.transforms[1].rotation = Quaternion(-0.2f, 0.1f, 0.4f, 0.8f).normalized()
        val colliders = ColliderData(
            BoxColliders(
                mutableListOf(90),
                mutableListOf(BoxCollider(Vector3(2.0f, 2.0f, 2.0f))),
                mutableListOf(Transform(Vector3(0.2f, 0.1f, 0.0f), Quaternion(0.2f, 0.0f, 0.1f, 1.0f).normalized(), 0))
            ),
            SphereColliders(
                mutableListOf(91),
                mutableListOf(SphereCollider(2.0f)),
                mutableListOf(Transform(Vector3(-0.2f, 0.0f, 0.1f), Quaternion(0.0f, 0.3f, 0.2f, 1.0f).normalized(), 1))
            )
        )
        val originalTransforms = listOf(colliders.boxes.transforms[0], colliders.spheres.transforms[0]).map { transform: Transform ->
            collisionCopyTransform(transform)
        }

        val result = runCollision(bodies, colliders)

        assertEquals(1, result.second.count)
        val actualTransforms = listOf(colliders.boxes.transforms[0], colliders.spheres.transforms[0])
        for (index: Int in originalTransforms.indices) {
            assertTransform(originalTransforms[index], actualTransforms[index])
        }
    }

    @Test
    fun colliderTagsMustBeGloballyUniqueAcrossShapeTypes(): Unit {
        val colliders = ColliderData(
            BoxColliders(
                mutableListOf(42),
                mutableListOf(BoxCollider(Vector3(1.0f, 1.0f, 1.0f))),
                mutableListOf(Transform(body = 0))
            ),
            SphereColliders(
                mutableListOf(42),
                mutableListOf(SphereCollider(1.0f)),
                mutableListOf(Transform(body = 0))
            )
        )

        val failure = assertFailsWith<IllegalArgumentException> { colliders.validate() }

        assertEquals("Collider tags must be globally unique", failure.message)
    }

    @Test
    fun colliderTagsMustFitUnsignedSixteenBits(): Unit {
        for (tag: Int in listOf(-1, 0x10000)) {
            val colliders = ColliderData(
                spheres = SphereColliders(
                    mutableListOf(tag),
                    mutableListOf(SphereCollider(1.0f)),
                    mutableListOf(Transform(body = 0))
                )
            )

            val failure = assertFailsWith<IllegalArgumentException> { colliders.validate() }

            assertEquals("Collider tags must fit unsigned 16 bits", failure.message)
        }
        ColliderData(
            boxes = BoxColliders(
                mutableListOf(0, 0xffff),
                mutableListOf(
                    BoxCollider(Vector3(1.0f, 1.0f, 1.0f)),
                    BoxCollider(Vector3(1.0f, 1.0f, 1.0f))
                ),
                mutableListOf(Transform(body = 0), Transform(body = 0))
            )
        ).validate()
    }
}

private fun collisionWorldSphere(index: Int, tag: Int, transform: Transform, radius: Float): WorldCollider {
    return WorldCollider(index, tag, transform, null, SphereCollider(radius), Vector3(), Vector3())
}

private fun collisionWorldBox(index: Int, tag: Int, transform: Transform, size: Vector3): WorldCollider {
    return WorldCollider(index, tag, transform, BoxCollider(size), null, Vector3(), Vector3())
}

private fun collisionPairKey(firstTag: Int, secondTag: Int): Int {
    return (firstTag and 0xffff) or ((secondTag and 0xffff) shl 16)
}

private fun collisionContactPairKey(contact: Contact): Int {
    return (contact.tag shr 32).toInt()
}

private fun runCollision(
    bodies: BodyData,
    colliders: ColliderData,
    connections: BodyConnections = BodyConnections()
): Pair<ActiveBodies, ContactData> {
    val activeBodies = ActiveBodies()
    val contacts = ContactData()
    collide(activeBodies, contacts, bodies, colliders, connections)
    return Pair(activeBodies, contacts)
}

private fun assertVector(expected: Vector3, actual: Vector3): Unit {
    assertEquals(expected.x, actual.x, COLLISION_TEST_TOLERANCE)
    assertEquals(expected.y, actual.y, COLLISION_TEST_TOLERANCE)
    assertEquals(expected.z, actual.z, COLLISION_TEST_TOLERANCE)
}

private fun collisionCopyTransform(transform: Transform): Transform {
    return Transform(
        transform.position.copy(),
        Quaternion(transform.rotation.x, transform.rotation.y, transform.rotation.z, transform.rotation.w),
        transform.body
    )
}

private fun assertTransform(expected: Transform, actual: Transform): Unit {
    assertEquals(expected.position.x, actual.position.x)
    assertEquals(expected.position.y, actual.position.y)
    assertEquals(expected.position.z, actual.position.z)
    assertEquals(expected.rotation.x, actual.rotation.x)
    assertEquals(expected.rotation.y, actual.rotation.y)
    assertEquals(expected.rotation.z, actual.rotation.z)
    assertEquals(expected.rotation.w, actual.rotation.w)
    assertEquals(expected.body, actual.body)
}
