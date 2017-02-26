package ru.dkolmogortsev.task.storage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.infinispan.Cache;
import org.infinispan.query.Search;
import org.infinispan.query.dsl.Expression;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.dsl.SortOrder;
import ru.dkolmogortsev.task.TimeEntry;

/**
 * Created by dkolmogortsev on 2/25/17.
 */
@Singleton
public class TimeEntriesStorage
{
    Cache<String, TimeEntry> infStorage;

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

    public TimeEntry get(String timeEntryId)
    {
        return infStorage.get(timeEntryId);
    }

    public void getEntriesGroupedByDay()
    {
        List<String> entryDate = factory.from(TimeEntry.class).select(Expression.property("entryDate")).build().list();
        Map<String, List<TimeEntry>> listMap = entryDate.stream().collect(Collectors.toMap(Object::toString,
                s -> factory.having("entryDate").eq(s).toBuilder().orderBy("entryDate", SortOrder.DESC).build()
                        .list()));
    }
}
