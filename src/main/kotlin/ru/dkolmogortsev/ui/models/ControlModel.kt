package ru.dkolmogortsev.ui.models

import javafx.beans.property.*
import javafx.collections.FXCollections
import org.reactfx.util.FxTimer
import org.reactfx.util.Timer
import ru.dkolmogortsev.task.Task
import ru.dkolmogortsev.utils.formatToElapsed
import tornadofx.Component
import tornadofx.ScopedInstance
import java.time.Duration

/**
 * Created by dkolmogortsev on 10/13/17.
 */
class ControlModel : Component(), ScopedInstance {
    private val elapsedProperty = SimpleLongProperty(0)
    private val currentTimeEntryIdProperty = SimpleLongProperty()
    private val taskStarted = SimpleBooleanProperty(false)
    val timerTextProp = SimpleStringProperty(elapsedProperty.get().formatToElapsed())
    private val taskNameProperty = SimpleStringProperty("Name")
    private val taskDescriptionProperty = SimpleStringProperty("Description")
    private val searchResultProperty = SimpleListProperty<Task>()
    var taskSelectedProperty: SimpleBooleanProperty = SimpleBooleanProperty(false)
    private lateinit var timer: Timer
    var timeEntryId
        get() = currentTimeEntryIdProperty.get()
        set(value) = currentTimeEntryIdProperty.set(value)

    init {
        elapsedProperty.addListener { _, _, newValue -> timerTextProp.set(newValue.toLong().formatToElapsed()) }
    }

    fun getElapsedProperty(): Long {
        return elapsedProperty.get()
    }

    fun taskStartedProperty(): SimpleBooleanProperty {
        return taskStarted
    }

    fun taskNameProperty(): SimpleStringProperty {
        return taskNameProperty
    }

    fun taskDescriptionProperty(): SimpleStringProperty {
        return taskDescriptionProperty
    }

    val isTaskStarted: Boolean
        get() = taskStarted.get()

    fun searchResultsProperty(): ListProperty<Task> {
        return searchResultProperty
    }

    fun setSearchResults(tasks: List<Task>) {
        this.searchResultProperty.value = FXCollections.observableArrayList(tasks)
    }

    fun startTimer() {
        timer = FxTimer
                .runPeriodically(Duration.ofSeconds(1L)) { elapsedProperty.value = getElapsedProperty() + 1 }
        taskStarted.set(true)
    }

    fun stopTimer() {
        elapsedProperty.set(0)
        timer.stop()
        taskStarted.set(false)
    }
}