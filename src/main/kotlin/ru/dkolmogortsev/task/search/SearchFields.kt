package ru.dkolmogortsev.task.search

import ru.dkolmogortsev.task.Task

/**
 * Created by dkolmogortsev on 2/18/17.
 */
enum class SearchFields constructor(val mapper: (Task) -> String, private val fieldName: String) {
    TASKNAME({ it.taskName }, "taskName"), DESCRIPTION({ it.description }, "description");

    override fun toString(): String {
        return fieldName
    }
}
