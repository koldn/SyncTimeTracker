package ru.dkolmogortsev.task;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

/**
 * Created by dkolmogortsev on 2/25/17.
 */
@Indexed
public class TimeEntry implements Comparable<TimeEntry>
{
    private long start;
    private long end;
    private long duration;

    @Field(store = Store.YES, analyze = Analyze.NO, indexNullAs = Field.DEFAULT_NULL_TOKEN)
    private String entryDate;
    private String taskId;

    public TimeEntry(long start, Task task)
    {
        this.start = start;
        this.entryDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date(this.start));
        this.taskId = task.getUUID();
    }

    public String getEntryDate()
    {
        return entryDate;
    }

    public String getTaskId()
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

    public void updateDuration(int newDuration)
    {
        this.duration = newDuration;
    }

    public void stop()
    {
        this.end = System.currentTimeMillis();
        this.duration = end - start;
    }

    public String getId()
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
