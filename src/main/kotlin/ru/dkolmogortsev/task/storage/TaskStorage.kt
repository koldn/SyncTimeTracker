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
internal constructor(manager: InfinispanCacheManager) : EventHandler {

    internal var LOG = LoggerFactory.getLogger(TaskStorage::class.java)

    private val cache: Cache<Long, Task>

    init {
        cache = manager.taskStorage()
    }

    fun create(description: String, name: String): Task {

        val newTask = Task(description, name)
        return (cache as java.util.Map<Long, Task>).computeIfAbsent(newTask.id) { integer -> newTask }
    }

    fun getTask(taskId: Long): Task {
        return cache[taskId]!!
    }
}
