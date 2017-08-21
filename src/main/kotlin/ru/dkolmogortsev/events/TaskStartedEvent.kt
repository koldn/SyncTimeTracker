package ru.dkolmogortsev.events

import griffon.core.event.Event

/**
 * Created by dkolmogortsev on 2/26/17.
 */
class TaskStartedEvent(taskId: Long) : Event(taskId)
{

    val taskId: Long
        get() = getSource() as Long
}
