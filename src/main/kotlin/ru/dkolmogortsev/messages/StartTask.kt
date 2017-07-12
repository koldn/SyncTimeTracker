package ru.dkolmogortsev.messages

import griffon.core.event.Event

/**
 * Created by dkolmogortsev on 2/26/17.
 */
class StartTask(taskId: Long) : Event(taskId) {

    val taskId: Long
        get() = getSource() as Long
}
