package ru.dkolmogortsev.messages;

import griffon.core.event.Event;

/**
 * Created by dkolmogortsev on 3/11/17.
 */
public class TimeEntryDeleted extends Event
{

    public TimeEntryDeleted(long entryId)
    {
        super(entryId);
    }

    public long getEntryId()
    {
        return (long)getSource();
    }
}
