package nudge

import algebra.*
import kotlin.math.*
import kotlin.test.*

class ContactSolverTest {
    @Test
    fun accumulatedNormalAndFrictionImpulsesAreProjected(): Unit {
        val bodies: BodyData = dynamicOverStatic()
        bodies.momentum[1].velocity = Vector3(1.0f, -1.0f, 0.0f)
        val contacts = ContactData(mutableListOf(contact(friction = 0.5f)))
        val impulses: ContactImpulseData = readCachedImpulses(ContactCache(), contacts)
        val constraints: ContactConstraintData = setupContactConstraints(ActiveBodies(), contacts, bodies, impulses)

        repeat(4) { iteration: Int ->
            applyImpulses(constraints, bodies)
        }
        updateCachedImpulses(constraints, impulses)
        val cached: Vector3 = impulses.data[0].impulse

        assertTrue(bodies.momentum[1].velocity.y >= -1.0e-5f)
        assertEquals(0.0f, bodies.momentum[0].velocity.length())
        val constraint: ContactConstraint = constraints.constraints.single()
        assertTrue(constraint.normalImpulse >= 0.0f)
        val frictionLength: Float = sqrt(
            constraint.frictionImpulseU * constraint.frictionImpulseU +
                    constraint.frictionImpulseV * constraint.frictionImpulseV
        )
        assertTrue(frictionLength <= 0.5f * constraint.normalImpulse + 1.0e-5f)
        assertEquals(constraint.normalImpulse, cached.dot(constraint.normal), 1.0e-5f)
        assertEquals(constraint.frictionImpulseU, cached.dot(constraint.tangentU), 1.0e-5f)
        assertEquals(constraint.frictionImpulseV, cached.dot(constraint.tangentV), 1.0e-5f)
    }

    @Test
    fun warmStartIsAppliedDuringSetup(): Unit {
        val bodies: BodyData = dynamicOverStatic()
        val contacts = ContactData(mutableListOf(contact(friction = 0.0f)))
        val cache = ContactCache()
        cache.data[1uL] = CachedContactImpulse(Vector3(0.0f, 2.0f, 0.0f))
        contacts.data[0].tag = 1uL
        val impulses: ContactImpulseData = readCachedImpulses(cache, contacts)

        setupContactConstraints(ActiveBodies(), contacts, bodies, impulses)

        assertEquals(2.0f, bodies.momentum[1].velocity.y, 1.0e-5f)
        assertEquals(0.0f, bodies.momentum[0].velocity.length())
    }

    @Test
    fun staticAndDegenerateContactsRemainFinite(): Unit {
        val bodies = BodyData(2)
        val contacts = ContactData(
            mutableListOf(
                Contact(
                    normal = Vector3(),
                    penetration = 0.0f,
                    friction = 0.0f,
                    bodies = BodyPair(0, 0)
                )
            )
        )
        val impulses: ContactImpulseData = readCachedImpulses(ContactCache(), contacts)
        val constraints: ContactConstraintData = setupContactConstraints(ActiveBodies(), contacts, bodies, impulses)

        applyImpulses(constraints, bodies)
        updateCachedImpulses(constraints, impulses)

        assertFinite(bodies.momentum[0].velocity)
        assertFinite(bodies.momentum[0].angularVelocity)
        assertFinite(impulses.data[0].impulse)
    }

    @Test
    fun repeatedRunsAreDeterministic(): Unit {
        val first: Vector3 = solvedVelocity()
        val second: Vector3 = solvedVelocity()
        assertEquals(first.x, second.x, 0.0f)
        assertEquals(first.y, second.y, 0.0f)
        assertEquals(first.z, second.z, 0.0f)
    }

    @Test
    fun dynamicBoxOnStaticGroundRunsAllSevenStages(): Unit {
        val bodies = BodyData(2)
        bodies.properties[1].massInverse = 1.0f
        bodies.properties[1].inertiaInverse = Vector3(1.0f, 1.0f, 1.0f)
        bodies.transforms[0].position = Vector3(0.0f, -0.5f, 0.0f)
        bodies.transforms[1].position = Vector3(0.0f, 0.45f, 0.0f)
        bodies.momentum[1].velocity = Vector3(0.0f, -1.0f, 0.0f)
        val colliders = ColliderData(
            boxes = BoxColliders(
                mutableListOf(0, 1),
                mutableListOf(
                    BoxCollider(Vector3(10.0f, 0.5f, 10.0f)),
                    BoxCollider(Vector3(0.5f, 0.5f, 0.5f))
                ),
                mutableListOf(Transform(body = 0), Transform(body = 1))
            )
        )
        val active = ActiveBodies()
        val contacts = ContactData()
        val cache = ContactCache()

        collide(active, contacts, bodies, colliders)
        val impulses: ContactImpulseData = readCachedImpulses(cache, contacts)
        val constraints: ContactConstraintData = setupContactConstraints(active, contacts, bodies, impulses)
        repeat(10) { iteration: Int ->
            applyImpulses(constraints, bodies)
        }
        updateCachedImpulses(constraints, impulses)
        writeCachedImpulses(cache, contacts, impulses)
        val oldPosition: Vector3 = bodies.transforms[1].position.copy()
        advance(active, bodies, 0.01f)

        assertEquals(4, contacts.count)
        assertEquals(listOf(1), active.indices)
        assertEquals(contacts.count, cache.count)
        assertTrue(bodies.momentum[1].velocity.y > -1.0f)
        assertFalse(oldPosition == bodies.transforms[1].position)
        assertEquals(-0.5f, bodies.transforms[0].position.y, 0.0f)
        assertEquals(0.0f, bodies.transforms[0].position.x, 0.0f)
        assertEquals(0.0f, bodies.transforms[0].position.z, 0.0f)
        assertFinite(bodies.transforms[1].position)
        assertFinite(bodies.momentum[1].velocity)
    }

    @Test
    fun anisotropicFrictionUsesDoubledCrossCoupling(): Unit {
        val bodies: BodyData = dynamicOverStatic()
        bodies.properties[1].inertiaInverse = Vector3(0.2f, 0.3f, 0.4f)
        bodies.transforms[1].rotation = Quaternion(0.1f, 0.2f, 0.3f, 0.9f).normalized()
        bodies.momentum[1].velocity = Vector3(0.5f, -1.0f, 0.25f)
        val contacts = ContactData(mutableListOf(contact(10.0f)))
        contacts.data[0].position = Vector3(0.2f, -0.5f, 0.1f)
        val impulses: ContactImpulseData = readCachedImpulses(ContactCache(), contacts)
        val constraints: ContactConstraintData = setupContactConstraints(ActiveBodies(), contacts, bodies, impulses)
        val constraint: ContactConstraint = constraints.constraints.single()
        val tangentU: Vector3 = constraint.offsetB.cross(constraint.tangentU)
        val tangentV: Vector3 = constraint.offsetB.cross(constraint.tangentV)
        val expectedUV: Float = 2.0f * tangentU.dot(constraint.inertiaB.vecMultiply(tangentV))
        assertEquals(expectedUV, constraint.frictionUV, 1.0e-6f)

        val initialVelocity: Vector3 = bodies.momentum[1].velocity
        val velocityU: Float = constraint.tangentU.dot(initialVelocity)
        val velocityV: Float = constraint.tangentV.dot(initialVelocity)
        val speedSquared: Float = velocityU * velocityU + velocityV * velocityV
        val denominator: Float = velocityU * velocityU * constraint.frictionUU +
                velocityV * velocityV * constraint.frictionVV +
                velocityU * velocityV * expectedUV
        val factor: Float = minOf(1.0e6f, 1.0f / denominator)
        val expectedU: Float = -velocityU * speedSquared * factor
        val expectedV: Float = -velocityV * speedSquared * factor

        applyImpulses(constraints, bodies)

        assertEquals(expectedU, constraint.frictionImpulseU, 1.0e-5f)
        assertEquals(expectedV, constraint.frictionImpulseV, 1.0e-5f)
    }

    @Test
    fun tinyEffectiveMassDoesNotCreateNonFiniteImpulses(): Unit {
        val bodies: BodyData = dynamicOverStatic()
        bodies.properties[1].massInverse = Float.MIN_VALUE
        val contacts = ContactData(mutableListOf(contact(0.5f)))
        val impulses: ContactImpulseData = readCachedImpulses(ContactCache(), contacts)
        val constraints: ContactConstraintData = setupContactConstraints(ActiveBodies(), contacts, bodies, impulses)

        applyImpulses(constraints, bodies)
        updateCachedImpulses(constraints, impulses)

        assertTrue(constraints.constraints.single().normalFactor.isFinite())
        assertTrue(constraints.constraints.single().bias.isFinite())
        assertFinite(bodies.momentum[1].velocity)
        assertFinite(impulses.data[0].impulse)
    }

    private fun dynamicOverStatic(): BodyData {
        val bodies = BodyData(2)
        bodies.properties[1].massInverse = 1.0f
        return bodies
    }

    private fun contact(friction: Float): Contact {
        return Contact(
            position = Vector3(),
            normal = Vector3(0.0f, 1.0f, 0.0f),
            penetration = 0.0f,
            friction = friction,
            bodies = BodyPair(0, 1),
            tag = 1uL
        )
    }

    private fun solvedVelocity(): Vector3 {
        val bodies: BodyData = dynamicOverStatic()
        bodies.properties[1].inertiaInverse = Vector3(0.2f, 0.3f, 0.4f)
        bodies.transforms[1].rotation = Quaternion(0.1f, 0.2f, 0.3f, 0.9f).normalized()
        bodies.momentum[1].velocity = Vector3(0.5f, -2.0f, 0.25f)
        bodies.momentum[1].angularVelocity = Vector3(0.1f, -0.2f, 0.3f)
        val contacts = ContactData(mutableListOf(contact(0.5f)))
        contacts.data[0].position = Vector3(0.2f, -0.5f, 0.1f)
        contacts.data[0].penetration = 0.02f
        val impulses: ContactImpulseData = readCachedImpulses(ContactCache(), contacts)
        val constraints: ContactConstraintData = setupContactConstraints(ActiveBodies(), contacts, bodies, impulses)
        repeat(8) { iteration: Int ->
            applyImpulses(constraints, bodies)
        }
        return bodies.momentum[1].velocity
    }

    private fun assertFinite(vector: Vector3): Unit {
        assertTrue(vector.x.isFinite())
        assertTrue(vector.y.isFinite())
        assertTrue(vector.z.isFinite())
    }
}
