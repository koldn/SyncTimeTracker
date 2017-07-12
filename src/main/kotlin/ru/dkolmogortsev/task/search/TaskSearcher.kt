package ru.dkolmogortsev.task.search

import org.infinispan.query.Search
import org.infinispan.query.dsl.QueryFactory
import ru.dkolmogortsev.task.Task
import ru.dkolmogortsev.task.storage.InfinispanCacheManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dkolmogortsev on 2/11/17.
 */
@Singleton
class TaskSearcher @Inject
constructor(manager: InfinispanCacheManager) {

    private val queryFactory: QueryFactory = Search.getQueryFactory(manager.taskStorage())

    fun search(searchString: String, field: SearchFields): List<Task> {
        return queryFactory.from(Task::class.java).having(field.toString())
                .like('%' + searchString.toLowerCase() + '%').build().list<Task>()
    }

}
