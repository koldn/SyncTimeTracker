package ru.dkolmogortsev.task

import com.google.common.base.Objects
import java.io.Serializable

/**
 * Created by dkolmogortsev on 2/11/17.
 */
class Task(val description: String, val taskName: String) : Serializable {
    val id: Long = Objects.hashCode(taskName, description).toLong()
    override fun toString(): String {
        return String.format("Task [d:%s]", description)
    }

    companion object {
        val NO_PROJECT = "Without project"
    }
}
