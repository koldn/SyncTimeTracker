package ru.dkolmogortsev.utils.ui

import javafx.beans.binding.DoubleExpression
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import org.joda.time.Duration
import ru.dkolmogortsev.controls.TimeEntryButton
import ru.dkolmogortsev.events.EventPublisher
import ru.dkolmogortsev.task.TimeEntry
import ru.dkolmogortsev.utils.formatToElapsed
import ru.dkolmogortsev.utils.getTimeFromLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dkolmogortsev on 30.07.2017.
 */
@Singleton
class TimeEntryUiLineBuilderService
{
    @Inject
    private lateinit var eventPublisher: EventPublisher

    inner class Builder(val timeEntry: TimeEntry, val width: DoubleExpression, val height: DoubleExpression)
    {
        private val task = timeEntry.task
        fun build(): GridPane
        {
            val entry = GridPane()

            entry.focusTraversableProperty().set(true)
            entry.columnConstraints.addAll(TimeEntryUiHelper.constraints)

            entry.prefWidthProperty().bind(width)
            val startButton = TimeEntryButton()
            ButtonStyler.asStartButton(startButton)
            startButton.prefHeightProperty().bind(height.multiply(0.90))
            val deleteButton = TimeEntryButton()
            ButtonStyler.asDeleteButton(deleteButton)
            deleteButton.prefHeightProperty().bind(height.multiply(0.90))

            startButton.setOnAction { _ -> eventPublisher.publishTaskStarted(task.id) }
            deleteButton.setOnAction { _ -> eventPublisher.publishTimeEntryDeleted(timeEntry.id) }
            val startStopString = StringBuilder().append(timeEntry.start.getTimeFromLong()).append(" -> ")
                    .append(timeEntry.end.getTimeFromLong()).toString()
            val startStopLabel = Label(startStopString)
            startStopLabel.alignment = Pos.CENTER
            startStopLabel.setMaxSize(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE)
            entry.addRow(0, Label(task.description), Label(task.taskName), Label(
                    Duration(timeEntry.duration).standardSeconds.formatToElapsed()),
                    startStopLabel, startButton, deleteButton)

            ButtonStyler.setupHover(entry, startButton, deleteButton)
            entry.focusTraversableProperty().set(true)
            return entry
        }
    }
}
