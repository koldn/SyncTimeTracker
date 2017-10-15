package ru.dkolmogortsev.events

import tornadofx.FXEvent

/**
 * Created by dkolmogortsev on 2/26/17.
 */
class TaskStartedEvent(taskId: Long) : FXEvent()
{
    val taskId: Long = taskId
}
