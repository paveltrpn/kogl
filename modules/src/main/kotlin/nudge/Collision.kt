package nudge

import algebra.*
import kotlin.math.*

fun collide(
    activeBodies: ActiveBodies,
    contacts: ContactData,
    bodies: BodyData,
    colliders: ColliderData,
    bodyConnections: BodyConnections = BodyConnections()
): Unit {
    bodies.validate()
    colliders.validate()
    contacts.data.clear()
    contacts.sleepingPairs.clear()
    activeBodies.indices.clear()
    if (bodies.count == 0) {
        require(colliders.boxes.count == 0 && colliders.spheres.count == 0)
        require(bodyConnections.data.isEmpty())
        return
    }

    val worldColliders: List<WorldCollider> = createWorldColliders(bodies, colliders)
    val candidates: MutableList<BodyPair> = mutableListOf()
    for (a: Int in worldColliders.indices) {
        for (b: Int in a + 1 until worldColliders.size) {
            if (overlaps(worldColliders[a], worldColliders[b])) {
                candidates.add(BodyPair(a, b))
            }
        }
    }

    val coarseBodyPairs: List<BodyPair> = candidates.map { pair: BodyPair ->
        BodyPair(worldColliders[pair.a].body, worldColliders[pair.b].body)
    }
    val coarseIslands: IslandData = buildIslands(bodies, bodyConnections, coarseBodyPairs)
    val activeCandidates: MutableList<BodyPair> = mutableListOf()
    for (pair: BodyPair in candidates) {
        val a: WorldCollider = worldColliders[pair.a]
        val b: WorldCollider = worldColliders[pair.b]
        if (a.body == b.body) {
            continue
        }
        if (coarseIslands.isActive(a.body, b.body)) {
            activeCandidates.add(pair)
        } else {
            contacts.sleepingPairs.add(sleepingPairKey(a.tag, b.tag))
        }
    }

    val generatedContacts: MutableList<Contact> = mutableListOf()
    for (pair: BodyPair in activeCandidates) {
        val a: WorldCollider = worldColliders[pair.a]
        val b: WorldCollider = worldColliders[pair.b]
        if (a.box != null && b.box != null) {
            generatedContacts.addAll(collideBoxBox(a, b))
        } else {
            if (a.box != null && b.sphere != null) {
                val contact: Contact? = collideBoxSphere(a, b)
                if (contact != null) {
                    generatedContacts.add(contact)
                }
            } else {
                if (a.sphere != null && b.box != null) {
                    val contact: Contact? = collideBoxSphere(b, a)
                    if (contact != null) {
                        generatedContacts.add(contact)
                    }
                } else {
                    val contact: Contact? = collideSphereSphere(a, b)
                    if (contact != null) {
                        generatedContacts.add(contact)
                    }
                }
            }
        }
    }

    val fineIslands: IslandData = buildIslands(
        bodies,
        bodyConnections,
        generatedContacts.map { contact: Contact -> contact.bodies }
    )
    var contactIndex: Int = 0
    while (contactIndex < generatedContacts.size) {
        val first: Contact = generatedContacts[contactIndex]
        val pairKey: Int = (first.tag shr 32).toInt()
        var end: Int = contactIndex + 1
        while (end < generatedContacts.size && (generatedContacts[end].tag shr 32).toInt() == pairKey) {
            end++
        }
        if (fineIslands.isActive(first.bodies.a, first.bodies.b)) {
            for (index: Int in contactIndex until end) {
                contacts.data.add(generatedContacts[index])
            }
        } else {
            contacts.sleepingPairs.add(pairKey)
        }
        contactIndex = end
    }

    for (body: Int in 1 until bodies.count) {
        if (fineIslands.activeSets[fineIslands.sets[body]]) {
            activeBodies.indices.add(body)
        }
    }
    contacts.sleepingPairs.sortWith { a: Int, b: Int ->
        a.toUInt().compareTo(b.toUInt())
    }
}

private fun createWorldColliders(bodies: BodyData, colliders: ColliderData): List<WorldCollider> {
    val result: MutableList<WorldCollider> = mutableListOf()
    for (index: Int in colliders.boxes.data.indices) {
        val local: Transform = colliders.boxes.transforms[index]
        require(local.body in bodies.transforms.indices)
        val world: Transform = composeTransform(bodies.transforms[local.body], local)
        val box: BoxCollider = colliders.boxes.data[index]
        val basis: Matrix3 = world.rotation.toMatrix3()
        val size: Vector3 = box.size
        val extent = Vector3(
            abs(basis.column0.x) * size.x + abs(basis.column1.x) * size.y + abs(basis.column2.x) * size.z,
            abs(basis.column0.y) * size.x + abs(basis.column1.y) * size.y + abs(basis.column2.y) * size.z,
            abs(basis.column0.z) * size.x + abs(basis.column1.z) * size.y + abs(basis.column2.z) * size.z
        )
        result.add(
            WorldCollider(
                result.size,
                colliders.boxes.tags[index],
                world,
                box,
                null,
                world.position.subtracted(extent),
                world.position.added(extent)
            )
        )
    }
    for (index: Int in colliders.spheres.data.indices) {
        val local: Transform = colliders.spheres.transforms[index]
        require(local.body in bodies.transforms.indices)
        val world: Transform = composeTransform(bodies.transforms[local.body], local)
        val sphere: SphereCollider = colliders.spheres.data[index]
        val extent = Vector3(sphere.radius, sphere.radius, sphere.radius)
        result.add(
            WorldCollider(
                result.size,
                colliders.spheres.tags[index],
                world,
                null,
                sphere,
                world.position.subtracted(extent),
                world.position.added(extent)
            )
        )
    }
    return result
}

private fun overlaps(a: WorldCollider, b: WorldCollider): Boolean {
    return a.maximum.x > b.minimum.x && b.maximum.x > a.minimum.x &&
            a.maximum.y > b.minimum.y && b.maximum.y > a.minimum.y &&
            a.maximum.z > b.minimum.z && b.maximum.z > a.minimum.z
}
