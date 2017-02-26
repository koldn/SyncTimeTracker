package ru.dkolmogortsev.task.storage;

import griffon.core.event.EventHandler;
import org.infinispan.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.dkolmogortsev.messages.TaskCreated;
import ru.dkolmogortsev.task.Task;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

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

    public List<Task> getAll() {
        return null;
    }

    public Task save(Task task) {
        cache.put(task.getUUID(), task);
        return task;
    }

    public Task getTask(String taskId) {
        return cache.get(taskId);
    }
}
