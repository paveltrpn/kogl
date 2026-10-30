package nudge

import algebra.*

fun advance(activeBodies: ActiveBodies, bodies: BodyData, timeStep: Float): Unit {
    require(timeStep.isFinite())
    bodies.validate()
    for (bodyIndex: Int in activeBodies.indices) {
        require(bodyIndex in bodies.transforms.indices)
        if (bodyIndex == 0) {
            continue
        }
        val momentum: BodyMomentum = bodies.momentum[bodyIndex]
        val idleCounter: Int = bodies.idleCounters[bodyIndex]
        if (momentum.velocity.lengthSquared() < 1.0e-2f &&
            momentum.angularVelocity.lengthSquared() < 1.0e-1f
        ) {
            if (idleCounter != 255) {
                bodies.idleCounters[bodyIndex] = minOf(idleCounter + 1, 255)
            }
        } else {
            bodies.idleCounters[bodyIndex] = 0
        }

        val transform: Transform = bodies.transforms[bodyIndex]
        val oldRotation: Quaternion = transform.rotation
        val angular: Vector3 = momentum.angularVelocity
        val deltaRotation: Quaternion = Quaternion(angular.x, angular.y, angular.z, 0.0f) * oldRotation
        val halfStep: Float = 0.5f * timeStep
        transform.position = transform.position.added(momentum.velocity.scale(timeStep))
        transform.rotation = Quaternion(
            oldRotation.x + deltaRotation.x * halfStep,
            oldRotation.y + deltaRotation.y * halfStep,
            oldRotation.z + deltaRotation.z * halfStep,
            oldRotation.w + deltaRotation.w * halfStep
        ).normalized()
    }
}
