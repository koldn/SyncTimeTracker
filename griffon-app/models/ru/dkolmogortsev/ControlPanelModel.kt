package ru.dkolmogortsev

import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import javafx.beans.property.*
import javafx.collections.FXCollections
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel
import org.reactfx.util.FxTimer
import org.reactfx.util.Timer
import ru.dkolmogortsev.task.Task
import ru.dkolmogortsev.utils.formatToElapsed
import java.time.Duration

@ArtifactProviderFor(GriffonModel::class)
class ControlPanelModel : AbstractGriffonModel() {
    private val elapsedProperty = SimpleLongProperty(0)
    private val currentTimeEntryIdProperty = SimpleLongProperty()
    private val taskStarted = SimpleBooleanProperty(false)
    val timerTextProp = SimpleStringProperty(elapsedProperty.get().formatToElapsed())
    private val taskNameProperty = SimpleStringProperty("Name")
    private val taskDescriptionProperty = SimpleStringProperty("Description")
    private val tasks = SimpleListProperty<Task>()
    private lateinit var timer: Timer

    var timeEntryId
        get() = currentTimeEntryIdProperty.get()
        set(value) = currentTimeEntryIdProperty.set(value)

    var elapsed
        get() = elapsedProperty.get()
        set(value) = elapsedProperty.set(value)

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

    fun tasksProperty(): ListProperty<Task> {
        return tasks
    }

    fun setList(tasks: List<Task>) {
        this.tasks.value = FXCollections.observableArrayList(tasks)
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