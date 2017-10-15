package ru.dkolmogortsev.task.storage.bridge

import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.apache.ignite.Ignition
import org.apache.ignite.cache.CacheMode
import org.apache.ignite.configuration.CacheConfiguration
import org.apache.ignite.configuration.IgniteConfiguration
import ru.dkolmogortsev.task.Task
import ru.dkolmogortsev.task.TimeEntry

/**
 * Created by dkolmogortsev on 10/7/17.
 */
class IgniteManager {

    private lateinit var ignite: Ignite

    init {
        val entriesCache = CacheConfiguration<Long, TimeEntry>("entries")
        entriesCache.cacheMode = CacheMode.LOCAL

        val taskCache = CacheConfiguration<Long, Task>("tasks")
        taskCache.cacheMode = CacheMode.LOCAL

        val igniteConf = IgniteConfiguration()
        igniteConf.setCacheConfiguration(entriesCache, taskCache)

        ignite = Ignition.start(igniteConf)
    }

    fun getTaskStorage(): IgniteCache<Long, Task> {
        return ignite.getOrCreateCache("entries")
    }

    fun getEntriesStorage(): IgniteCache<Long, TimeEntry> {
        return ignite.getOrCreateCache("tasks")
    }
}