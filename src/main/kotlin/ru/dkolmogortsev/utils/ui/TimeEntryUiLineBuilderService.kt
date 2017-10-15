package ru.dkolmogortsev.utils.ui

import javafx.beans.binding.DoubleExpression
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import org.joda.time.Duration
import ru.dkolmogortsev.events.EventPublisher
import ru.dkolmogortsev.task.TimeEntry
import ru.dkolmogortsev.utils.formatToElapsed
import ru.dkolmogortsev.utils.getTimeFromLong
import tornadofx.Component

/**
 * Created by dkolmogortsev on 30.07.2017.
 */
class TimeEntryUiLineBuilderService : Component()
{
    private val eventPublisher: EventPublisher by inject()
    private val buttonFactory: ButtonFactory by inject()

    inner class Builder(val timeEntry: TimeEntry, val width: DoubleExpression, val height: DoubleExpression)
    {
        private val task = timeEntry.task
        fun build(): GridPane
        {
            val entry = GridPane()

            entry.focusTraversableProperty().set(true)
            entry.columnConstraints.addAll(TimeEntryUiHelper.constraints)

            entry.prefWidthProperty().bind(width)

            val startButton = buttonFactory.createStartTimeEntryButton(task.id, height)
            val deleteButton = buttonFactory.createDeleteTimeEntryButton(timeEntry.id, height)
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
