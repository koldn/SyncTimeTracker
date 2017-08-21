package ru.dkolmogortsev.task.storage

import griffon.core.event.EventHandler
import org.infinispan.Cache
import org.slf4j.LoggerFactory
import ru.dkolmogortsev.task.Task
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dkolmogortsev on 2/11/17.
 */
@Singleton
class TaskStorage @Inject
internal constructor(manager: InfinispanCacheManager) : Storage<Task>
{
    override operator fun get(id: Long): Task
    {
        return cache[id]!!
    }

    override fun save(toSave: Task)
    {
        cache.computeIfAbsent(toSave.id, { _ -> toSave })
    }

    internal var LOG = LoggerFactory.getLogger(TaskStorage::class.java)
    private val cache: Cache<Long, Task> = manager.taskStorage()
    fun getTask(taskId: Long): Task
    {
        return cache[taskId]!!
    }
}
