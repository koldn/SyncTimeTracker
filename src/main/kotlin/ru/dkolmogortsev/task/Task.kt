package ru.dkolmogortsev.task

import com.google.common.base.Objects
import org.hibernate.search.annotations.Analyze
import org.hibernate.search.annotations.Field
import org.hibernate.search.annotations.Indexed
import org.hibernate.search.annotations.Store
import java.io.Serializable

/**
 * Created by dkolmogortsev on 2/11/17.
 */
@Indexed
class Task(@Field(store = Store.YES, analyze = Analyze.NO, indexNullAs = Field.DEFAULT_NULL_TOKEN)
           val description: String, @Field(store = Store.YES, analyze = Analyze.NO, indexNullAs = Field.DEFAULT_NULL_TOKEN)
           val taskName: String) : Serializable {
    val id: Long

    init {
        this.id = Objects.hashCode(taskName, description).toLong()
    }

    override fun toString(): String {
        return String.format("Task [d:%s]", description)
    }

    companion object {
        val NO_PROJECT = "Without project"
    }
}
