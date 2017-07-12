package ru.dkolmogortsev.messages

import griffon.core.event.Event

/**
 * Created by dkolmogortsev on 2/25/17.
 */
class TaskStopped(timeEntryId: Long) : Event(timeEntryId) {

    val id: Long
        get() = super.getSource() as Long
}
