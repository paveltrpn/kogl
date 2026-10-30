package nudge

import algebra.*

fun readCachedImpulses(contactCache: ContactCache, contacts: ContactData): ContactImpulseData {
    val sortedContactIndices: List<Int> = contacts.data.indices.sortedWith { a: Int, b: Int ->
        contacts.data[a].tag.compareTo(contacts.data[b].tag)
    }
    val result = ContactImpulseData(contacts.count, sortedContactIndices)
    val sleepingPairs: Set<UInt> = contacts.sleepingPairs.map { pair: Int -> pair.toUInt() }.toSet()
    for (contactIndex: Int in sortedContactIndices) {
        val cached: CachedContactImpulse? = contactCache.data[contacts.data[contactIndex].tag]
        if (cached != null) {
            result.data[contactIndex].impulse = cached.impulse.copy()
        }
    }
    val sortedCacheTags: List<ULong> = contactCache.data.keys.sorted()
    for (tag: ULong in sortedCacheTags) {
        val pair: UInt = (tag shr 32).toUInt()
        if (sleepingPairs.contains(pair)) {
            val cached: CachedContactImpulse = requireNotNull(contactCache.data[tag])
            result.sleepingData[tag] = CachedContactImpulse(cached.impulse.copy())
        }
    }
    return result
}

fun writeCachedImpulses(
    contactCache: ContactCache,
    contacts: ContactData,
    contactImpulses: ContactImpulseData
): Unit {
    require(contactImpulses.data.size == contacts.count)
    require(contactImpulses.sortedContactIndices.size == contacts.count)
    val merged: MutableMap<ULong, CachedContactImpulse> = mutableMapOf()
    for ((tag: ULong, impulse: CachedContactImpulse) in contactImpulses.sleepingData) {
        merged[tag] = CachedContactImpulse(impulse.impulse.copy())
    }
    for (contactIndex: Int in contactImpulses.sortedContactIndices) {
        require(contactIndex in contacts.data.indices)
        val tag: ULong = contacts.data[contactIndex].tag
        if (!merged.containsKey(tag)) {
            merged[tag] = CachedContactImpulse(contactImpulses.data[contactIndex].impulse.copy())
        }
    }
    contactCache.data.clear()
    for (tag: ULong in merged.keys.sorted()) {
        contactCache.data[tag] = requireNotNull(merged[tag])
    }
}
