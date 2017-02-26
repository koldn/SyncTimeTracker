package ru.dkolmogortsev.task;

/**
 * Created by dkolmogortsev on 2/25/17.
 */
public class TimeEntry implements Comparable<TimeEntry>
{
    private long start;
    private long end;
    private long duration;
    private String taskId;

    public TimeEntry(long start, Task task)
    {
        this.start = start;
        this.taskId = task.getUUID();
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
