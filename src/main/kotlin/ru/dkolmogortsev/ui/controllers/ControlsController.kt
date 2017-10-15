package ru.dkolmogortsev.ui.controllers

import org.reactfx.util.FxTimer
import org.reactfx.util.Timer
import ru.dkolmogortsev.events.EventPublisher
import ru.dkolmogortsev.events.TaskSelectedEvent
import ru.dkolmogortsev.events.TaskStartedEvent
import ru.dkolmogortsev.task.Task
import ru.dkolmogortsev.task.TimeEntry
import ru.dkolmogortsev.task.search.SearchFields
import ru.dkolmogortsev.task.search.TaskSearcher
import ru.dkolmogortsev.task.storage.TaskStorage
import ru.dkolmogortsev.task.storage.TimeEntriesStorage
import ru.dkolmogortsev.ui.models.ControlModel
import tornadofx.Controller
import java.time.Duration

/**
 * Created by dkolmogortsev on 10/13/17.
 */
class ControlsController : Controller() {
    private val model: ControlModel by inject()

    private lateinit var searcher: TaskSearcher
    private val storage: TaskStorage by inject()
    private val entriesStorage: TimeEntriesStorage by inject()
    private val eventPublisher: EventPublisher by inject()
    private lateinit var backUpTimer: Timer

    init {
        subscribe<TaskStartedEvent> { onStartTask(it) }
        subscribe<TaskSelectedEvent> { onTaskSelected(it) }
    }


    fun start() {
        if (!model.isTaskStarted) {
            val description = model.taskDescriptionProperty().get()
            val nameUI = model.taskNameProperty().get()
            val task = Task(description, nameUI)
            storage.save(task)
            val te = TimeEntry(System.currentTimeMillis(), task)
            entriesStorage.save(te)
            model.timeEntryId = te.id
            initTimeEntryBackup(te)
            model.startTimer()
        } else {
            stopBackgroundBackup()
            val te = entriesStorage.get(model.timeEntryId)
            te.updateDuration(model.getElapsedProperty())
            te.stop()
            entriesStorage.save(te)
            model.stopTimer()
            eventPublisher.publishTaskStopped(te.id)

        }
    }

    private fun initTimeEntryBackup(te: TimeEntry) {
        backUpTimer = FxTimer.runPeriodically(Duration.ofSeconds(10)) {
            te.updateDuration(model.getElapsedProperty())
            entriesStorage.save(te)
        }
    }

    private fun stopBackgroundBackup() {
        backUpTimer.stop()
    }

    fun search(searchStr: String, field: SearchFields) {
        model.setSearchResults(TaskSearcher.search(searchStr, field))
    }

    fun onStartTask(taskStartedEvent: TaskStartedEvent) {
        val taskFromStorage = storage.getTask(taskStartedEvent.taskId)
        model.taskDescriptionProperty().set(taskFromStorage.description)
        model.taskNameProperty().set(taskFromStorage.taskName)
        start()
    }

    fun onTaskSelected(taskSelectedEvent: TaskSelectedEvent) {
        val task = storage[taskSelectedEvent.taskId]
        model.taskDescriptionProperty().value = task.description
        model.taskNameProperty().value = task.taskName
        model.taskSelectedProperty.value = true
        model.taskSelectedProperty.value = false
    }
}