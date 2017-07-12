package ru.dkolmogortsev.task

import org.hibernate.search.annotations.Indexed
import org.joda.time.LocalDate
import java.io.Serializable

/**
 * Created by dkolmogortsev on 2/25/17.
 */
@Indexed
class TimeEntry(val start: Long, val taskId: Long) : Comparable<TimeEntry>, Serializable {

    val entryDate: Long = LocalDate(start).toDate().time
    var end: Long = 0
        private set
    var duration: Long = 0
        private set

    fun updateDuration(duration: Long) {
        this.duration = duration * 1000
    }

    fun stop() {
        this.end = this.start + this.duration
    }

    val id: Long
        get() = taskId + start

    override fun toString(): String {
        return "TimeEntry[start:$start,duration:$duration]"
    }

    override fun compareTo(o: TimeEntry): Int {
        return java.lang.Long.compare(this.start, o.start)
    }
}
