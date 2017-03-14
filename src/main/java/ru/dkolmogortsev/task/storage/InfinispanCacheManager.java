package ru.dkolmogortsev.task.storage;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
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
                .location(".syncData/").preload(true).purgeOnStartup(false).build();
        manager.defineConfiguration("tasks", configuration);

        Configuration entriesConf = new ConfigurationBuilder().persistence().passivation(false).addSingleFileStore()
                .location(".syncData/").preload(true).purgeOnStartup(false).build();
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
