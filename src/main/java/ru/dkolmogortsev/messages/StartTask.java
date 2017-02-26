package ru.dkolmogortsev.messages;

import griffon.core.event.Event;

/**
 * Created by dkolmogortsev on 2/26/17.
 */
public class StartTask extends Event
{
    public StartTask(String taskId){
        super(taskId);
    }

    public String getTaskId(){
        return (String)getSource();
    }
}
