package nudge

import algebra.*

data class Contact(
    var position: Vector3 = Vector3(),
    var normal: Vector3 = Vector3(1.0f, 0.0f, 0.0f),
    var penetration: Float = 0.0f,
    var friction: Float = 0.5f,
    var bodies: BodyPair = BodyPair(0, 0),
    var tag: ULong = 0u
)

class ContactData(
    val data: MutableList<Contact> = mutableListOf(),
    val sleepingPairs: MutableList<Int> = mutableListOf()
) {
    val count: Int
        get(): Int {
            return data.size
        }

    val sleepingCount: Int
        get(): Int {
            return sleepingPairs.size
        }
}

data class CachedContactImpulse(
    var impulse: Vector3 = Vector3()
)

class ContactCache(
    val data: MutableMap<ULong, CachedContactImpulse> = mutableMapOf()
) {
    val count: Int
        get(): Int {
            return data.size
        }
}

class ContactImpulseData internal constructor(
    contactCount: Int,
    val sortedContactIndices: List<Int>
) {
    val data: MutableList<CachedContactImpulse> = MutableList(contactCount) { index: Int ->
        CachedContactImpulse()
    }

    internal val sleepingData: MutableMap<ULong, CachedContactImpulse> = mutableMapOf()
}
