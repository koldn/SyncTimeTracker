package ru.dkolmogortsev.events

import tornadofx.FXEvent

/**
 * Created by dkolmogortsev on 20.07.2017.
 */
class TaskSelectedEvent(taskId: Long) : FXEvent()
{
    val taskId: Long = taskId
}