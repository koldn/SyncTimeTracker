package ru.dkolmogortsev.task.storage;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.Index;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import ru.dkolmogortsev.task.Task;
import ru.dkolmogortsev.task.TimeEntry;

/**
 * Created by dkolmogortsev on 2/11/17.
 */
@Singleton
public class InfinispanCacheManager
{

    private EmbeddedCacheManager manager;

    @PostConstruct
    public void init()
    {
        manager = new DefaultCacheManager();
        Configuration configuration = new ConfigurationBuilder().persistence().passivation(false).addSingleFileStore()
                .location("/home/dkolmogortsev/syncData/").preload(true).purgeOnStartup(false).indexing()
                .addIndexedEntity(Task.class).index(Index.LOCAL).addProperty("default.directory_provider", "ram").
                        addProperty("lucene_version", "LUCENE_CURRENT").build();
        manager.defineConfiguration("tasks", configuration);

        Configuration entriesConf = new ConfigurationBuilder().persistence().passivation(false).addSingleFileStore()
                .location("/home/dkolmogortsev/syncData/").preload(true).purgeOnStartup(false).build();
        manager.defineConfiguration("timeEntries", entriesConf);

    }

    public Cache<String, Task> taskStorage()
    {
        return manager.getCache("tasks");
    }

    public Cache<String, TimeEntry> timeEntryStorage()
    {
        return manager.getCache("timeEntries", true);
    }
}
