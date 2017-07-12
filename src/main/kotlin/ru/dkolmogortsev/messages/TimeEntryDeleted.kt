package ru.dkolmogortsev.messages

import griffon.core.event.Event

/**
 * Created by dkolmogortsev on 3/11/17.
 */
class TimeEntryDeleted(entryId: Long) : Event(entryId) {

    val entryId: Long
        get() = getSource() as Long
}
