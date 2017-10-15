package ru.dkolmogortsev.events

import tornadofx.Component
import tornadofx.ScopedInstance

/**
 * Created by Юлия Коткова on 30.07.2017.
 */
class EventPublisher : Component(), ScopedInstance
{
    fun publishTaskSelected(taskId: Long)
    {
        fire(TaskSelectedEvent(taskId))
    }

    fun publishTaskStarted(taskId: Long)
    {
        fire(TaskStartedEvent(taskId))
    }

    fun publishTaskStopped(taskId: Long)
    {
        fire(TaskStoppedEvent(taskId))
    }

    fun publishTimeEntryDeleted(entryId: Long)
    {
        fire(TimeEntryDeletedEvent(entryId))
    }
}