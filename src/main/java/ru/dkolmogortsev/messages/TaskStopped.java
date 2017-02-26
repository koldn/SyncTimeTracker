package ru.dkolmogortsev.messages;

import griffon.core.event.Event;

/**
 * Created by dkolmogortsev on 2/25/17.
 */
public class TaskStopped extends Event
{
    public TaskStopped(String timeEntryId){
        super(timeEntryId);
    }

    public String getId(){
        return (String)super.getSource();
    }
}
