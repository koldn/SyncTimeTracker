package ru.dkolmogortsev.ui.controllers

import com.google.common.collect.Lists
import org.joda.time.LocalDate
import ru.dkolmogortsev.events.TaskStoppedEvent
import ru.dkolmogortsev.events.TimeEntryDeletedEvent
import ru.dkolmogortsev.task.TimeEntry
import ru.dkolmogortsev.task.storage.TimeEntriesStorage
import tornadofx.Controller

/**
 * Created by dkolmogortsev on 10/15/17.
 */
class TaskPanelController : Controller() {

    private val model: ru.dkolmogortsev.ui.models.TaskPanelModel by inject()
    private val timeEntriesStorage: TimeEntriesStorage by inject()

    fun init() {
        val modelMap = model.map
        timeEntriesStorage.entriesGroupedByDay.entries.stream()
                .forEach { stringListEntry -> modelMap.put(stringListEntry.key, stringListEntry.value) }
        if (modelMap.isEmpty()) {
            modelMap.put(LocalDate.now().toDate().time, Lists.newArrayList<TimeEntry>())
        }
    }

    private fun onTaskStopped(taskStoppedEvent: TaskStoppedEvent) {
        val timeEntry = timeEntriesStorage.get(taskStoppedEvent.id)

        (model.map as java.util.Map<Long, List<TimeEntry>>).compute(timeEntry.entryDate) { s, _ -> timeEntriesStorage.getByEntryDate(s) }
    }

    private fun onTimeEntryDeleted(deletedEvent: TimeEntryDeletedEvent) {
        val timeEntry = timeEntriesStorage.delete(deletedEvent.entryId)
        (model.map as java.util.Map<Long, List<TimeEntry>>).compute(timeEntry.entryDate) { s, timeEntries ->
            timeEntriesStorage.getByEntryDate(s)
        }
    }

    init {
        subscribe<TimeEntryDeletedEvent> { onTimeEntryDeleted(it) }
        subscribe<TaskStoppedEvent> { onTaskStopped(it) }
    }
}