package ru.dkolmogortsev.events

import griffon.core.event.Event

/**
 * Created by dkolmogortsev on 20.07.2017.
 */
class TaskSelectedEvent(taskId: Long) : Event(taskId)
{
    val taskId: Long
        get() = getSource() as Long
}