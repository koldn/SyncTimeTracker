package ru.dkolmogortsev.events

import griffon.core.GriffonApplication
import griffon.core.event.EventRouter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Юлия Коткова on 30.07.2017.
 */
@Singleton
class EventPublisher @Inject constructor(application: GriffonApplication)
{
    private val eventRouter: EventRouter = application.eventRouter
    fun publishTaskSelected(taskId: Long)
    {
        eventRouter.publishEvent(TaskSelectedEvent(taskId))
    }

    fun publishTaskStarted(taskId: Long)
    {
        eventRouter.publishEvent(TaskStartedEvent(taskId))
    }

    fun publishTaskStopped(taskId: Long)
    {
        eventRouter.publishEvent(TaskStoppedEvent(taskId))
    }

    fun publishTimeEntryDeleted(entryId: Long)
    {
        eventRouter.publishEvent(TimeEntryDeletedEvent(entryId))
    }
}