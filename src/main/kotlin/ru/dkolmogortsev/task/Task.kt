package ru.dkolmogortsev.task

import com.google.common.base.Objects
import org.hibernate.search.annotations.*
import ru.dkolmogortsev.task.storage.bridge.LowerCaseStringBrigde
import java.io.Serializable

/**
 * Created by dkolmogortsev on 2/11/17.
 */
@Indexed
class Task(@Field(store = Store.YES, analyze = Analyze.NO, indexNullAs = Field.DEFAULT_NULL_TOKEN) val description: String, @Field(store = Store.YES, analyze =
Analyze.NO, indexNullAs = Field.DEFAULT_NULL_TOKEN) val taskName: String) : Serializable
{
    val id: Long = Objects.hashCode(taskName, description).toLong()
    override fun toString(): String
    {
        return String.format("Task [d:%s]", description)
    }

    companion object
    {
        val NO_PROJECT = "Without project"
    }
}
