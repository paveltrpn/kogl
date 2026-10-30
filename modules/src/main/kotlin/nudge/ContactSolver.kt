package nudge

import algebra.*
import kotlin.math.*

class ContactConstraintData internal constructor(
    internal val constraints: List<ContactConstraint>
)

internal class ContactConstraint(
    val contactIndex: Int,
    val bodyA: Int,
    val bodyB: Int,
    val normal: Vector3,
    val tangentU: Vector3,
    val tangentV: Vector3,
    val offsetA: Vector3,
    val offsetB: Vector3,
    val inverseMassA: Float,
    val inverseMassB: Float,
    val inertiaA: Matrix3,
    val inertiaB: Matrix3,
    val friction: Float,
    val normalFactor: Float,
    val bias: Float,
    val frictionUU: Float,
    val frictionVV: Float,
    val frictionUV: Float
) {
    var normalImpulse: Float = 0.0f
    var frictionImpulseU: Float = 0.0f
    var frictionImpulseV: Float = 0.0f
}

fun setupContactConstraints(
    activeBodies: ActiveBodies,
    contacts: ContactData,
    bodies: BodyData,
    contactImpulses: ContactImpulseData
): ContactConstraintData {
    bodies.validate()
    require(contactImpulses.data.size == contacts.count)
    require(contactImpulses.sortedContactIndices.size == contacts.count)
    val constraints: MutableList<ContactConstraint> = mutableListOf()
    for (contactIndex: Int in contactImpulses.sortedContactIndices) {
        require(contactIndex in contacts.data.indices)
        val contact: Contact = contacts.data[contactIndex]
        val bodyA: Int = contact.bodies.a
        val bodyB: Int = contact.bodies.b
        require(bodyA in bodies.transforms.indices && bodyB in bodies.transforms.indices)
        val transformA: Transform = bodies.transforms[bodyA]
        val transformB: Transform = bodies.transforms[bodyB]
        val inverseMassA: Float = solverInverseMass(bodies, bodyA)
        val inverseMassB: Float = solverInverseMass(bodies, bodyB)
        val inertiaA: Matrix3 = solverInverseInertia(bodies, bodyA)
        val inertiaB: Matrix3 = solverInverseInertia(bodies, bodyB)
        val normal: Vector3 = solverNormal(contact.normal)
        val offsetA: Vector3 = contact.position.subtracted(transformA.position)
        val offsetB: Vector3 = contact.position.subtracted(transformB.position)
        val inverseMassSum: Float = inverseMassA + inverseMassB
        val angularA: Vector3 = inertiaA.vecMultiply(offsetA.cross(normal))
        val angularB: Vector3 = inertiaB.vecMultiply(offsetB.cross(normal))
        val effectiveNormalMass: Float = inverseMassSum + normal.dot(
            angularA.cross(offsetA).added(angularB.cross(offsetB))
        )
        var normalFactor: Float = 0.0f
        if (effectiveNormalMass != 0.0f && effectiveNormalMass.isFinite()) {
            val reciprocal: Float = -1.0f / effectiveNormalMass
            if (reciprocal.isFinite()) {
                normalFactor = reciprocal
            }
        }
        val penetration: Float = maxOf(contact.penetration - 1.0e-3f, 0.0f)
        val computedBias: Float = -2.0f * penetration * normalFactor
        val bias: Float
        if (computedBias.isFinite()) {
            bias = computedBias
        } else {
            bias = 0.0f
        }
        val tangentU: Vector3 = solverTangent(normal)
        val tangentV: Vector3 = tangentU.cross(normal)
        val tangentAU: Vector3 = offsetA.cross(tangentU)
        val tangentAV: Vector3 = offsetA.cross(tangentV)
        val tangentBU: Vector3 = offsetB.cross(tangentU)
        val tangentBV: Vector3 = offsetB.cross(tangentV)
        val frictionUU: Float = inverseMassSum +
                tangentAU.dot(inertiaA.vecMultiply(tangentAU)) +
                tangentBU.dot(inertiaB.vecMultiply(tangentBU))
        val frictionVV: Float = inverseMassSum +
                tangentAV.dot(inertiaA.vecMultiply(tangentAV)) +
                tangentBV.dot(inertiaB.vecMultiply(tangentBV))
        val frictionUV: Float = 2.0f * (tangentAU.dot(inertiaA.vecMultiply(tangentAV)) +
                tangentBU.dot(inertiaB.vecMultiply(tangentBV)))
        val constraint = ContactConstraint(
            contactIndex,
            bodyA,
            bodyB,
            normal,
            tangentU,
            tangentV,
            offsetA,
            offsetB,
            inverseMassA,
            inverseMassB,
            inertiaA,
            inertiaB,
            maxOf(contact.friction, 0.0f),
            normalFactor,
            bias,
            frictionUU,
            frictionVV,
            frictionUV
        )
        initializeWarmStart(constraint, contactImpulses.data[contactIndex].impulse, bodies)
        constraints.add(constraint)
    }
    return ContactConstraintData(constraints)
}

fun applyImpulses(data: ContactConstraintData, bodies: BodyData): Unit {
    bodies.validate()
    for (constraint: ContactConstraint in data.constraints) {
        val momentumA: BodyMomentum = bodies.momentum[constraint.bodyA]
        val momentumB: BodyMomentum = bodies.momentum[constraint.bodyB]
        val pointVelocityA: Vector3 = momentumA.velocity.added(
            momentumA.angularVelocity.cross(constraint.offsetA)
        )
        val pointVelocityB: Vector3 = momentumB.velocity.added(
            momentumB.angularVelocity.cross(constraint.offsetB)
        )
        val relativeVelocity: Vector3 = pointVelocityB.subtracted(pointVelocityA)
        val normalVelocity: Float = constraint.normal.dot(relativeVelocity)
        val oldNormalImpulse: Float = constraint.normalImpulse
        val newNormalImpulse: Float = maxOf(
            oldNormalImpulse + constraint.bias + constraint.normalFactor * normalVelocity,
            0.0f
        )
        constraint.normalImpulse = newNormalImpulse

        val velocityU: Float = constraint.tangentU.dot(relativeVelocity)
        val velocityV: Float = constraint.tangentV.dot(relativeVelocity)
        val speedSquared: Float = velocityU * velocityU + velocityV * velocityV
        val denominator: Float = velocityU * velocityU * constraint.frictionUU +
                velocityV * velocityV * constraint.frictionVV +
                velocityU * velocityV * constraint.frictionUV
        val factor: Float = frictionFactor(denominator)
        val oldFrictionU: Float = constraint.frictionImpulseU
        val oldFrictionV: Float = constraint.frictionImpulseV
        var newFrictionU: Float = oldFrictionU - velocityU * speedSquared * factor
        var newFrictionV: Float = oldFrictionV - velocityV * speedSquared * factor
        val maximumFriction: Float = constraint.friction * newNormalImpulse
        val frictionLengthSquared: Float = newFrictionU * newFrictionU + newFrictionV * newFrictionV
        if (maximumFriction <= 0.0f) {
            newFrictionU = 0.0f
            newFrictionV = 0.0f
        } else {
            if (frictionLengthSquared > maximumFriction * maximumFriction && frictionLengthSquared > 0.0f) {
                val scale: Float = maximumFriction / sqrt(frictionLengthSquared)
                newFrictionU *= scale
                newFrictionV *= scale
            }
        }
        constraint.frictionImpulseU = newFrictionU
        constraint.frictionImpulseV = newFrictionV

        val deltaNormal: Float = newNormalImpulse - oldNormalImpulse
        val deltaFrictionU: Float = newFrictionU - oldFrictionU
        val deltaFrictionV: Float = newFrictionV - oldFrictionV
        val impulse: Vector3 = constraint.normal.scale(deltaNormal)
            .added(constraint.tangentU.scale(deltaFrictionU))
            .added(constraint.tangentV.scale(deltaFrictionV))
        applyContactImpulse(constraint, impulse, bodies)
    }
}

fun updateCachedImpulses(data: ContactConstraintData, contactImpulses: ContactImpulseData): Unit {
    for (constraint: ContactConstraint in data.constraints) {
        contactImpulses.data[constraint.contactIndex].impulse = constraint.normal.scale(constraint.normalImpulse)
            .added(constraint.tangentU.scale(constraint.frictionImpulseU))
            .added(constraint.tangentV.scale(constraint.frictionImpulseV))
    }
}

private fun initializeWarmStart(
    constraint: ContactConstraint,
    cachedImpulse: Vector3,
    bodies: BodyData
): Unit {
    val normalImpulse: Float = maxOf(constraint.normal.dot(cachedImpulse), 0.0f)
    var frictionU: Float = constraint.tangentU.dot(cachedImpulse)
    var frictionV: Float = constraint.tangentV.dot(cachedImpulse)
    val maximumFriction: Float = constraint.friction * normalImpulse
    val frictionLengthSquared: Float = frictionU * frictionU + frictionV * frictionV
    if (maximumFriction <= 0.0f) {
        frictionU = 0.0f
        frictionV = 0.0f
    } else {
        if (frictionLengthSquared > maximumFriction * maximumFriction && frictionLengthSquared > 0.0f) {
            val scale: Float = maximumFriction / sqrt(frictionLengthSquared)
            frictionU *= scale
            frictionV *= scale
        }
    }
    constraint.normalImpulse = normalImpulse
    constraint.frictionImpulseU = frictionU
    constraint.frictionImpulseV = frictionV
    val impulse: Vector3 = constraint.normal.scale(normalImpulse)
        .added(constraint.tangentU.scale(frictionU))
        .added(constraint.tangentV.scale(frictionV))
    applyContactImpulse(constraint, impulse, bodies)
}

private fun applyContactImpulse(constraint: ContactConstraint, impulse: Vector3, bodies: BodyData): Unit {
    val momentumA: BodyMomentum = bodies.momentum[constraint.bodyA]
    val momentumB: BodyMomentum = bodies.momentum[constraint.bodyB]
    momentumA.velocity = momentumA.velocity.subtracted(impulse.scale(constraint.inverseMassA))
    momentumB.velocity = momentumB.velocity.added(impulse.scale(constraint.inverseMassB))
    val angularDeltaA: Vector3 = constraint.inertiaA.vecMultiply(constraint.offsetA.cross(impulse)).inversed()
    val angularDeltaB: Vector3 = constraint.inertiaB.vecMultiply(constraint.offsetB.cross(impulse))
    momentumA.angularVelocity = momentumA.angularVelocity.added(angularDeltaA)
    momentumB.angularVelocity = momentumB.angularVelocity.added(angularDeltaB)
}

private fun solverInverseMass(bodies: BodyData, body: Int): Float {
    if (body == 0) {
        return 0.0f
    }
    return bodies.properties[body].massInverse
}

private fun solverInverseInertia(bodies: BodyData, body: Int): Matrix3 {
    if (body == 0) {
        return Matrix3().zero()
    }
    val properties: BodyProperties = bodies.properties[body]
    val basis: Matrix3 = bodies.transforms[body].rotation.toMatrix3()
    val scaled = Matrix3(
        basis.column0.scale(properties.inertiaInverse.x),
        basis.column1.scale(properties.inertiaInverse.y),
        basis.column2.scale(properties.inertiaInverse.z)
    )
    return scaled.multiply(basis.transposed())
}

private fun solverNormal(normal: Vector3): Vector3 {
    val lengthSquared: Float = normal.lengthSquared()
    if (lengthSquared > 0.0f && lengthSquared.isFinite() &&
        normal.x.isFinite() && normal.y.isFinite() && normal.z.isFinite()
    ) {
        return normal.copy()
    }
    return Vector3(1.0f, 0.0f, 0.0f)
}

private fun solverTangent(normal: Vector3): Vector3 {
    val signScale: Float = abs(normal.x)
    val candidate = Vector3(
        -normal.z * signScale,
        normal.z * signScale - normal.z,
        (normal.x - normal.y) * signScale + normal.y
    )
    val lengthSquared: Float = candidate.lengthSquared()
    if (lengthSquared > 1.0e-20f && lengthSquared.isFinite()) {
        return candidate.scale(1.0f / sqrt(lengthSquared))
    }
    val axis: Vector3
    if (abs(normal.x) <= abs(normal.y) && abs(normal.x) <= abs(normal.z)) {
        axis = Vector3(1.0f, 0.0f, 0.0f)
    } else {
        if (abs(normal.y) <= abs(normal.z)) {
            axis = Vector3(0.0f, 1.0f, 0.0f)
        } else {
            axis = Vector3(0.0f, 0.0f, 1.0f)
        }
    }
    val fallback: Vector3 = axis.cross(normal)
    if (fallback.lengthSquared() > 0.0f) {
        return fallback.normalize()
    }
    return Vector3(0.0f, 1.0f, 0.0f)
}

private fun frictionFactor(denominator: Float): Float {
    if (denominator == 0.0f) {
        return 1.0e6f
    }
    if (denominator > 0.0f && denominator.isFinite()) {
        return minOf(1.0e6f, 1.0f / denominator)
    }
    return 0.0f
}
