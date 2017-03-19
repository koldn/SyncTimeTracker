package ru.dkolmogortsev.task.storage;

import griffon.core.event.EventHandler;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.infinispan.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.dkolmogortsev.task.Task;

/**
 * Created by dkolmogortsev on 2/11/17.
 */
@Singleton
public class TaskStorage implements EventHandler {

    Logger LOG = LoggerFactory.getLogger(TaskStorage.class);

    private Cache<Long, Task> cache;

    @Inject
    TaskStorage(InfinispanCacheManager manager){
        cache = manager.taskStorage();
    }

    public Task create(String description, String name)
    {

        Task newTask = new Task(description, name);
        return cache.computeIfAbsent(newTask.getId(), integer -> newTask);
    }

    public Task getTask(long taskId)
    {
        return cache.get(taskId);
    }
}
