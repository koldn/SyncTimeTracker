package ru.dkolmogortsev.messages;

import griffon.core.event.Event;

/**
 * Created by dkolmogortsev on 2/25/17.
 */
public class TaskStopped extends Event
{
    public TaskStopped(long timeEntryId)
    {
        super(timeEntryId);
    }

    public long getId()
    {
        return (long)super.getSource();
    }
}
