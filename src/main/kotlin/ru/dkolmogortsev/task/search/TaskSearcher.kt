package ru.dkolmogortsev.task.search

import org.infinispan.query.Search
import org.infinispan.query.dsl.QueryFactory
import org.slf4j.LoggerFactory
import ru.dkolmogortsev.task.Task
import ru.dkolmogortsev.task.storage.InfinispanCacheManager

/**
 * Created by dkolmogortsev on 2/11/17.
 */
object TaskSearcher {
    private val LOG = LoggerFactory.getLogger(this.javaClass)

    private val queryFactory: QueryFactory = Search.getQueryFactory(InfinispanCacheManager.taskStorage())

    fun search(searchString: String, field: SearchFields): List<Task> {
        val query = queryFactory.from(Task::class.java).having(field.toString())
                .like('%' + searchString.toLowerCase() + '%').build()
        LOG.info("Searching : {}", query)
        val foundObjects = query.list<Task>()
        LOG.info("Found tasks size: ${foundObjects.size}")
        return foundObjects
    }

}
