package ru.dkolmogortsev.events

import tornadofx.FXEvent

/**
 * Created by dkolmogortsev on 2/25/17.
 */
class TaskStoppedEvent(timeEntryId: Long) : FXEvent()
{

    val id: Long = timeEntryId
}
