package ru.dkolmogortsev.task.storage

import com.google.common.collect.Maps
import org.infinispan.Cache
import org.infinispan.query.Search
import org.infinispan.query.dsl.QueryFactory
import org.infinispan.query.dsl.SortOrder
import ru.dkolmogortsev.task.TimeEntry
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dkolmogortsev on 2/25/17.
 */
@Singleton
class TimeEntriesStorage @Inject constructor(manager: InfinispanCacheManager) : Storage<TimeEntry>
{
	internal var infStorage: Cache<Long, TimeEntry> = manager.timeEntryStorage()
	private val factory: QueryFactory
	override fun save(entry: TimeEntry)
	{
		infStorage.put(entry.id, entry)
	}

	override operator fun get(timeEntryId: Long): TimeEntry
	{
		return infStorage[timeEntryId]!!
	}

	fun delete(timeEntryId: Long): TimeEntry
	{
		return infStorage.remove(timeEntryId)!!
	}

	fun getByEntryDate(entryDate: Long): List<TimeEntry>
	{
		val entries = factory.from(TimeEntry::class.java).having("entryDate").equal(entryDate).orderBy("start", SortOrder.DESC).build().list<TimeEntry>()
		return entries
	}

	val entriesGroupedByDay: Map<Long, List<TimeEntry>>
		get()
		{
			val resultSet = factory.from(TimeEntry::class.java).select("entryDate").groupBy("entryDate")
					.orderBy("entryDate", SortOrder.ASC).build().list<Array<Any>>()
			val longListMap = TreeMap<Long, List<TimeEntry>>({ v1: Long, v2: Long -> v2.compareTo(v1) })
			resultSet.onEach { arrayOfAnys -> longListMap.put(arrayOfAnys[0] as Long, getByEntryDate(arrayOfAnys[0] as Long)) }
			return longListMap
		}

	init
	{
		factory = Search.getQueryFactory(infStorage)
		val brokenEntries = factory.from(TimeEntry::class.java).having("end").equal(0).build().list<TimeEntry>()
		brokenEntries.forEach { timeEntry ->
			timeEntry.stop()
			infStorage.put(timeEntry.id, timeEntry)
		}
	}
}
