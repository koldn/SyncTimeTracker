package ru.dkolmogortsev.task;

import java.io.Serializable;
import org.hibernate.search.annotations.Indexed;
import org.joda.time.LocalDate;

/**
 * Created by dkolmogortsev on 2/25/17.
 */
@Indexed
public class TimeEntry implements Comparable<TimeEntry>, Serializable
{

    private long start;

    private long entryDate;
    private long end;
    private long duration;

    private long taskId;

    public TimeEntry(long start, long taskId)
    {
        this.start = start;
        this.entryDate = new LocalDate(start).toDate().getTime();
        this.taskId = taskId;
    }

    public long getEntryDate()
    {
        return entryDate;
    }

    public long getTaskId()
    {
        return taskId;
    }

    public long getStart()
    {
        return start;
    }

    public long getEnd()
    {
        return end;
    }

    public long getDuration()
    {
        return duration;
    }

    public void updateDuration(long duration)
    {
        this.duration = duration * 1000;
    }

    public void stop()
    {
        this.end = this.start + this.duration;
    }

    public long getId()
    {
        return taskId + start;
    }

    @Override
    public String toString()
    {
        return "TimeEntry[start:" + start + ",duration:" + duration + ']';
    }

    @Override
    public int compareTo(TimeEntry o)
    {
        return Long.compare(this.start, o.start);
    }
}
