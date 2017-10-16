package ru.dkolmogortsev.task.storage

import org.infinispan.Cache
import ru.dkolmogortsev.task.TimeEntry
import tornadofx.Component
import tornadofx.ScopedInstance

/**
 * Created by dkolmogortsev on 2/25/17.
 */
class TimeEntriesStorage : Component(), Storage<TimeEntry>, ScopedInstance {
    internal var infStorage: Cache<Long, TimeEntry> = InfinispanCacheManager.timeEntryStorage()
    override fun save(entry: TimeEntry) {
        infStorage.put(entry.id, entry)
    }

    override operator fun get(timeEntryId: Long): TimeEntry {
        return infStorage[timeEntryId]!!
    }

    fun delete(timeEntryId: Long): TimeEntry {
        return infStorage.remove(timeEntryId)!!
    }

    fun getByEntryDate(entryDate: Long): List<TimeEntry> {
        //TODO implement by lucene
        return listOf()
    }

    //TODO implement by lucene
    val entriesGroupedByDay: Map<Long, List<TimeEntry>>
        get() {
            return mapOf()
        }

    init {
//		factory = Search.getQueryFactory(infStorage)
//		val brokenEntries = factory.from(TimeEntry::class.java).having("end").equal(0).build().list<TimeEntry>()
//		brokenEntries.forEach { timeEntry ->
//			timeEntry.stop()
//			infStorage.put(timeEntry.id, timeEntry)
//		}
    }
}
