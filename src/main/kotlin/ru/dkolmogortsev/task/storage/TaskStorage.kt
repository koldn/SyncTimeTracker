package ru.dkolmogortsev.task.storage

import org.infinispan.Cache
import org.slf4j.LoggerFactory
import ru.dkolmogortsev.task.Task
import tornadofx.Component
import tornadofx.ScopedInstance

/**
 * Created by dkolmogortsev on 2/11/17.
 */
class TaskStorage : Component(), Storage<Task>, ScopedInstance
{

    override operator fun get(id: Long): Task
    {
        return cache[id]!!
    }

    override fun save(toSave: Task)
    {
        cache.putIfAbsent(toSave.id, toSave)
    }

    internal var LOG = LoggerFactory.getLogger(TaskStorage::class.java)
    private val cache: Cache<Long, Task> = InfinispanCacheManager.taskStorage()
    fun getTask(taskId: Long): Task
    {
        return cache[taskId]!!
    }
}
