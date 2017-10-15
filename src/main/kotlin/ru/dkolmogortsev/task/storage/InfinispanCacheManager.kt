package ru.dkolmogortsev.task.storage

import org.infinispan.Cache
import org.infinispan.configuration.cache.ConfigurationBuilder
import org.infinispan.manager.DefaultCacheManager
import org.infinispan.manager.EmbeddedCacheManager
import ru.dkolmogortsev.task.Task
import ru.dkolmogortsev.task.TimeEntry

/**
 * Created by dkolmogortsev on 2/11/17.
 */
object InfinispanCacheManager {

    private var manager: EmbeddedCacheManager

    init {
        manager = DefaultCacheManager()
        val configuration = ConfigurationBuilder().persistence().passivation(false).addSingleFileStore()
                .location(".syncData/").preload(true).purgeOnStartup(false)
                .build()
        manager.defineConfiguration("tasks", configuration)
        manager.defineConfiguration("timeEntries", configuration)

    }

    fun taskStorage(): Cache<Long, Task> {
        return manager.getCache<Long, Task>("tasks")
    }

    fun timeEntryStorage(): Cache<Long, TimeEntry> {
        return manager.getCache<Long, TimeEntry>("timeEntries")
    }
}
