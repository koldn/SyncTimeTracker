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

    private Cache<String,Task> cache;

    @Inject
    TaskStorage(InfinispanCacheManager manager){
        cache = manager.taskStorage();
    }


    public Task save(Task task) {
        cache.put(task.getUUID(), task);
        return task;
    }

    public Task getTask(String taskId) {
        return cache.get(taskId);
    }
}
