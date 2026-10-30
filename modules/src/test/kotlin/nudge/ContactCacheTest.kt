package nudge

import algebra.*
import kotlin.test.*

class ContactCacheTest {
    @Test
    fun warmStartLookupIsTagSortedAndDoesNotAliasCache(): Unit {
        val contacts = ContactData()
        contacts.data.add(Contact(tag = 20u))
        contacts.data.add(Contact(tag = 10u))
        val cache = ContactCache()
        cache.data[10u] = CachedContactImpulse(Vector3(1.0f, 2.0f, 3.0f))

        val impulses: ContactImpulseData = readCachedImpulses(cache, contacts)

        assertEquals(listOf(1, 0), impulses.sortedContactIndices)
        assertVector(Vector3(1.0f, 2.0f, 3.0f), impulses.data[1].impulse)
        assertVector(Vector3(), impulses.data[0].impulse)
        impulses.data[1].impulse.x = 9.0f
        assertEquals(1.0f, cache.data[10u]?.impulse?.x)
    }

    @Test
    fun writeRetainsSleepingEntriesAndEvictsStaleEntries(): Unit {
        val sleepingPair: ULong = 0x00020001uL
        val sleepingTag: ULong = (sleepingPair shl 32) or 7uL
        val staleTag: ULong = (0x00030001uL shl 32) or 8uL
        val contacts = ContactData(
            mutableListOf(Contact(tag = 10u), Contact(tag = 20u)),
            mutableListOf(sleepingPair.toInt())
        )
        val cache = ContactCache()
        cache.data[10u] = CachedContactImpulse(Vector3(1.0f, 0.0f, 0.0f))
        cache.data[sleepingTag] = CachedContactImpulse(Vector3(2.0f, 0.0f, 0.0f))
        cache.data[staleTag] = CachedContactImpulse(Vector3(3.0f, 0.0f, 0.0f))
        val impulses: ContactImpulseData = readCachedImpulses(cache, contacts)
        impulses.data[0].impulse = Vector3(4.0f, 0.0f, 0.0f)
        impulses.data[1].impulse = Vector3(5.0f, 0.0f, 0.0f)

        writeCachedImpulses(cache, contacts, impulses)

        assertEquals(setOf(10uL, 20uL, sleepingTag), cache.data.keys)
        assertVector(Vector3(2.0f, 0.0f, 0.0f), cache.data[sleepingTag]?.impulse)
        assertFalse(cache.data.containsKey(staleTag))
        assertEquals(listOf(10uL, 20uL, sleepingTag), cache.data.keys.toList())
    }

    private fun assertVector(expected: Vector3, actual: Vector3?): Unit {
        assertNotNull(actual)
        assertEquals(expected.x, actual.x, 1.0e-6f)
        assertEquals(expected.y, actual.y, 1.0e-6f)
        assertEquals(expected.z, actual.z, 1.0e-6f)
    }
}
