package ru.dkolmogortsev.task.storage;

import com.google.inject.Provides;
import javafx.stage.Stage;
import org.infinispan.Cache;
import org.infinispan.cache.impl.CacheImpl;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.Index;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.reactfx.util.Timer;
import ru.dkolmogortsev.task.Task;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.inject.Singleton;
import ru.dkolmogortsev.task.TimeEntry;

/**
 * Created by dkolmogortsev on 2/11/17.
 */
@Singleton
public class InfinispanCacheManager {

    private EmbeddedCacheManager manager;
    @PostConstruct
    public void init(){
        manager = new DefaultCacheManager();
        Configuration configuration = new ConfigurationBuilder().indexing().addIndexedEntity(Task.class).index(Index.LOCAL).addProperty("default.directory_provider", "ram").
                addProperty("lucene_version", "LUCENE_CURRENT").build();
        manager.defineConfiguration("tasks", configuration);
    }

    public Cache<String, Task> taskStorage(){
        return manager.getCache("tasks");
    }

    public Cache<String, TimeEntry> timeEntryStorage(){
        return manager.getCache("timeEntries", true);
    }
}
