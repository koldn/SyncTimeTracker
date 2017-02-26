package ru.dkolmogortsev.task.storage;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.infinispan.Cache;
import ru.dkolmogortsev.task.TimeEntry;

/**
 * Created by dkolmogortsev on 2/25/17.
 */
@Singleton
public class TimeEntriesStorage
{
    Cache<String, TimeEntry> infStorage;

    @Inject
    public TimeEntriesStorage(InfinispanCacheManager manager){
        infStorage = manager.timeEntryStorage();
    }

    public TimeEntry save(TimeEntry entry){
        return infStorage.put(entry.getId(),entry);
    }

    public TimeEntry get(String timeEntryId){
        return infStorage.get(timeEntryId);
    }
}
