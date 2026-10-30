package nudge

import algebra.*
import kotlin.math.*

internal fun collideSphereSphere(a: WorldCollider, b: WorldCollider): Contact? {
    val sphereA: SphereCollider = requireNotNull(a.sphere)
    val sphereB: SphereCollider = requireNotNull(b.sphere)
    val radius: Float = sphereA.radius + sphereB.radius
    val delta: Vector3 = b.transform.position.subtracted(a.transform.position)
    val distanceSquared: Float = delta.lengthSquared()
    if (distanceSquared > radius * radius) {
        return null
    }
    val distance: Float = sqrt(distanceSquared)
    val normal: Vector3
    if (distanceSquared > 1.0e-4f) {
        normal = delta.scale(1.0f / distance)
    } else {
        normal = Vector3(1.0f, 0.0f, 0.0f)
    }
    val position: Vector3 = a.transform.position.added(normal.scale(distance - sphereB.radius))
    return Contact(
        position = position,
        normal = normal,
        penetration = radius - distance,
        bodies = BodyPair(a.body, b.body),
        tag = colliderOnlyTag(a.tag, b.tag)
    )
}

internal fun collideBoxSphere(box: WorldCollider, sphere: WorldCollider): Contact? {
    val boxShape: BoxCollider = requireNotNull(box.box)
    val sphereShape: SphereCollider = requireNotNull(sphere.sphere)
    val rotation: Quaternion = box.transform.rotation
    val offset: Vector3 = rotation.conjugate().rotate(
        sphere.transform.position.subtracted(box.transform.position)
    )
    val absoluteX: Float = abs(offset.x)
    val absoluteY: Float = abs(offset.y)
    val absoluteZ: Float = abs(offset.z)
    val width: Float = boxShape.size.x + sphereShape.radius
    val height: Float = boxShape.size.y + sphereShape.radius
    val depth: Float = boxShape.size.z + sphereShape.radius
    if (absoluteX >= width || absoluteY >= height || absoluteZ >= depth) {
        return null
    }

    val outsideX: Boolean = absoluteX > boxShape.size.x
    val outsideY: Boolean = absoluteY > boxShape.size.y
    val outsideZ: Boolean = absoluteZ > boxShape.size.z
    val outsideCount: Int = listOf(outsideX, outsideY, outsideZ).count { outside: Boolean -> outside }
    val localNormal: Vector3
    val penetration: Float

    if (outsideCount >= 2) {
        val corner = Vector3(
            clampedFaceCoordinate(offset.x, boxShape.size.x, outsideX),
            clampedFaceCoordinate(offset.y, boxShape.size.y, outsideY),
            clampedFaceCoordinate(offset.z, boxShape.size.z, outsideZ)
        )
        val delta: Vector3 = offset.subtracted(corner)
        val distanceSquared: Float = delta.lengthSquared()
        if (distanceSquared > sphereShape.radius * sphereShape.radius) {
            return null
        }
        val distance: Float = sqrt(distanceSquared)
        if (distance > 0.0f && distance.isFinite()) {
            localNormal = delta.scale(1.0f / distance)
        } else {
            localNormal = Vector3(1.0f, 0.0f, 0.0f)
        }
        penetration = sphereShape.radius - distance
    } else {
        val penetrationX: Float = width - absoluteX
        val penetrationY: Float = height - absoluteY
        val penetrationZ: Float = depth - absoluteZ
        if (penetrationX < penetrationY && penetrationX < penetrationZ) {
            localNormal = Vector3(signedDirection(offset.x), 0.0f, 0.0f)
            penetration = penetrationX
        } else {
            if (penetrationY < penetrationZ) {
                localNormal = Vector3(0.0f, signedDirection(offset.y), 0.0f)
                penetration = penetrationY
            } else {
                localNormal = Vector3(0.0f, 0.0f, signedDirection(offset.z))
                penetration = penetrationZ
            }
        }
    }

    val localPosition: Vector3 = offset.subtracted(localNormal.scale(sphereShape.radius))
    return Contact(
        position = rotation.rotate(localPosition).added(box.transform.position),
        normal = rotation.rotate(localNormal),
        penetration = penetration,
        bodies = BodyPair(box.body, sphere.body),
        tag = colliderOnlyTag(box.tag, sphere.tag)
    )
}

private fun clampedFaceCoordinate(value: Float, extent: Float, outside: Boolean): Float {
    if (outside) {
        return signedDirection(value) * extent
    }
    return value
}

private fun signedDirection(value: Float): Float {
    if (value > 0.0f) {
        return 1.0f
    }
    return -1.0f
}

private fun colliderOnlyTag(a: Int, b: Int): ULong {
    val pair: ULong = (a.toULong() and 0xffffu) or ((b.toULong() and 0xffffu) shl 16)
    return pair shl 32
}
