package ru.dkolmogortsev.messages;

import griffon.core.event.Event;

/**
 * Created by dkolmogortsev on 2/26/17.
 */
public class StartTask extends Event
{
    public StartTask(long taskId)
    {
        super(taskId);
    }

    public long getTaskId()
    {
        return (long)getSource();
    }
}
