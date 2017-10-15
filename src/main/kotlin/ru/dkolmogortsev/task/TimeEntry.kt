package ru.dkolmogortsev.task

import org.hibernate.search.annotations.Analyze
import org.hibernate.search.annotations.Field
import org.hibernate.search.annotations.Indexed
import org.hibernate.search.annotations.Store
import org.joda.time.LocalDate
import java.io.Serializable

/**
 * Created by dkolmogortsev on 2/25/17.
 */
@Indexed
class TimeEntry : Comparable<TimeEntry>, Serializable
{
    val start: Long
    val task: Task
    @Field(analyze = Analyze.NO, store = Store.YES)
    val entryDate: Long
    var end: Long = 0
        private set
    var duration: Long = 0
        private set
    val id: Long
        get() = task.id + start

    constructor(start: Long, task: Task)
    {
        this.start = start
        this.task = task
        this.entryDate = LocalDate(start).toDate().time
    }

    fun updateDuration(duration: Long)
    {
        this.duration = duration * 1000
    }

    fun stop()
    {
        this.end = this.start + this.duration
    }

    override fun toString(): String
    {
        return "TimeEntry[start:$start,duration:$duration]"
    }

    override fun compareTo(o: TimeEntry): Int
    {
        return java.lang.Long.compare(this.start, o.start)
    }
}
