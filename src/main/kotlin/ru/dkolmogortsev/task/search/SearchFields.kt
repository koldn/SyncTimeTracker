package ru.dkolmogortsev.task.search

import ru.dkolmogortsev.task.Task

/**
 * Created by dkolmogortsev on 2/18/17.
 */
enum class SearchFields
{
    TASK_NAME({ "${it.taskName}(${it.description})" }, "taskName"),
    DESCRIPTION({ "${it.description}:(${it.taskName})" }, "description");

    val mapper: (Task) -> String
    private val fieldName: String

    constructor(mapper: (Task) -> String, fieldName: String)
    {
        this.mapper = mapper
        this.fieldName = fieldName
    }

    override fun toString(): String
    {
        return fieldName
    }
}
