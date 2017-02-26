package ru.dkolmogortsev.task.search;


import org.infinispan.query.Search;
import org.infinispan.query.dsl.QueryFactory;
import ru.dkolmogortsev.task.Task;
import ru.dkolmogortsev.task.storage.InfinispanCacheManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by dkolmogortsev on 2/11/17.
 */
@Singleton
public class TaskSearcher {

    private QueryFactory queryFactory;

    @Inject
    public TaskSearcher(InfinispanCacheManager manager){
        queryFactory = Search.getQueryFactory(manager.taskStorage());
    }

    public List<Task> search(String searchString, SearchFields field){
        return queryFactory.from(Task.class).having(field.toString().toLowerCase()).like('%' + searchString.toLowerCase() + '%').toBuilder().build().list();
    }

}
