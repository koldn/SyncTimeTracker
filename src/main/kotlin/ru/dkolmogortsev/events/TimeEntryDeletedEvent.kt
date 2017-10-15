package ru.dkolmogortsev.events

import tornadofx.FXEvent

/**
 * Created by dkolmogortsev on 3/11/17.
 */
class TimeEntryDeletedEvent(entryId: Long) : FXEvent()
{
    val entryId: Long = entryId
}
