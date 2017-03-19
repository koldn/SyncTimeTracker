package ru.dkolmogortsev.task.storage;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.infinispan.Cache;
import org.infinispan.query.Search;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.dsl.SortOrder;
import ru.dkolmogortsev.task.TimeEntry;

/**
 * Created by dkolmogortsev on 2/25/17.
 */
@Singleton
public class TimeEntriesStorage
{
    Cache<Long, TimeEntry> infStorage;

    private QueryFactory factory;

    @Inject
    public TimeEntriesStorage(InfinispanCacheManager manager)
    {
        infStorage = manager.timeEntryStorage();
        factory = Search.getQueryFactory(infStorage);
    }

    public TimeEntry save(TimeEntry entry)
    {
        return infStorage.put(entry.getId(), entry);
    }

    public TimeEntry get(long timeEntryId)
    {
        return infStorage.get(timeEntryId);
    }

    public TimeEntry delete(long timeEntryId)
    {
        return infStorage.remove(timeEntryId);
    }

    public List<TimeEntry> getByEntryDate(long entryDate)
    {
        List<TimeEntry> entries = factory.from(TimeEntry.class).having("entryDate").eq(entryDate)
                .orderBy("start", SortOrder.ASC).build().list();
        return entries;
    }

    public Map<Long, List<TimeEntry>> getEntriesGroupedByDay()
    {
        List<Object[]> resultSet = factory.from(TimeEntry.class).select("entryDate").groupBy("entryDate")
                .orderBy("entryDate", SortOrder.ASC).build().list();
        Map<Long, List<TimeEntry>> longListMap = resultSet.stream().map(longs -> (long)longs[0]).collect(Collectors
                .toMap(Function.identity(), this::getByEntryDate, (entries, entries2) -> entries, TreeMap::new));
        return longListMap;

    }
}
