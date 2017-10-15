package ru.dkolmogortsev.utils.ui

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.binding.Bindings
import javafx.beans.binding.DoubleExpression
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ToggleButton
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.FlowPane
import javafx.scene.layout.GridPane
import javafx.scene.text.Text
import org.joda.time.Duration
import org.joda.time.LocalDate
import org.reactfx.EventStreams
import org.reactfx.Subscription
import ru.dkolmogortsev.controls.TimeEntryButton
import ru.dkolmogortsev.customui.DayGridPane
import ru.dkolmogortsev.events.EventPublisher
import ru.dkolmogortsev.task.TimeEntry
import ru.dkolmogortsev.utils.formatToElapsed
import tornadofx.Component
import tornadofx.ScopedInstance

/**
 * Created by dkolmogortsev on 30.07.2017.
 */
class DayGridBuilderService : Component(), ScopedInstance
{
    companion object {
        val pattern = "dd/MM/yyyy" //TODO make it configurable
        val CHILD_ENTRY_INSETS = Insets(0.0, 0.0, 0.0, 20.0)
    }

    private val eventPublisher: EventPublisher by inject()
    private val uiEntryBuilder = find(TimeEntryUiLineBuilderService::class)

    inner class Builder(val date: Long, val paneWidth: DoubleExpression, val headerHeight: DoubleExpression, val taskToEntries: Map<Long, List<TimeEntry>>)
    {
        fun build(): DayGridPane
        {
            val dayGridPane = DayGridPane(date)
            setupPaneView(dayGridPane)
            val entriesPane = FlowPane()

            taskToEntries.forEach { _, timeEntries -> buildEntries(entriesPane, timeEntries) }
            dayGridPane.addRow(1, entriesPane)
            return dayGridPane
        }

        private fun buildEntries(parentPane: FlowPane, entries: List<TimeEntry>)
        {
            val uiLines = entries.map { timeEntry -> uiEntryBuilder.Builder(timeEntry, paneWidth, headerHeight).build() }.toList()
            if (uiLines.size == 1)
            {
                parentPane.children.add(uiLines[0])
                return
            }

            uiLines.forEach { gridPane ->
                gridPane.prefHeightProperty().bind(headerHeight.multiply(0.80))
                (gridPane.children[0] as Label).padding = CHILD_ENTRY_INSETS
            }
            val overAllDuration = entries.map { it.duration }.sum()
            val ents = FlowPane()

            ents.prefWidthProperty().bind(parentPane.widthProperty())
            val tg = ToggleButton()
            tg.isFocusTraversable = false
            tg.setMaxSize(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE)
            tg.styleClass.clear()
            val icon = MaterialDesignIconView(MaterialDesignIcon.ARROW_UP_DROP_CIRCLE_OUTLINE)
            tg.heightProperty()
                    .addListener { _, _, newValue -> icon.glyphSize = newValue.toDouble() / 2 }
            val text = Text(entries.size.toString())
            tg.graphicProperty().bind(Bindings.`when`(tg.selectedProperty()).then<Text>(icon).otherwise(text))

            EventStreams.changesOf(tg.selectedProperty()).subscribe { booleanChange ->
                val newVal = booleanChange.newValue
                if (newVal)
                {
                    uiLines.forEach { it.isFocusTraversable = true }
                    ents.children.addAll(1, uiLines)
                }
                else
                {
                    uiLines.forEach { it.isFocusTraversable = false }
                    ents.children.removeAll(uiLines)
                }
            }
            val task = entries.first().task
            val timeEntryButton = TimeEntryButton()
            timeEntryButton.isFocusTraversable = false
            ButtonStyler.asStartButton(timeEntryButton)
            timeEntryButton.prefHeightProperty().bind(headerHeight.multiply(0.90))
            timeEntryButton.setOnAction { eventPublisher.publishTaskStarted(task.id) }
            val deleteButton = TimeEntryButton()
            deleteButton.isFocusTraversable = false
            ButtonStyler.asDeleteButton(deleteButton)
            deleteButton.prefHeightProperty().bind(headerHeight.multiply(0.90))
            deleteButton.setOnAction { entries.forEach({ eventPublisher.publishTimeEntryDeleted(it.id) }) }
            val groupedEntry = GridPane()
            groupedEntry.columnConstraints.addAll(TimeEntryUiHelper.constraints)
            groupedEntry.prefWidthProperty().bind(paneWidth)
            groupedEntry.addRow(0, Label(task.description), Label(task.taskName),
                    Label(Duration(overAllDuration).standardSeconds.formatToElapsed()), tg,
                    timeEntryButton, deleteButton)
            ents.children.add(0, groupedEntry)
            var sub: Subscription = Subscription.EMPTY

            EventStreams.changesOf(groupedEntry.focusedProperty()).subscribe({ prop ->
                if (prop.newValue)
                {
                    sub = EventStreams.eventsOf(groupedEntry, KeyEvent.KEY_PRESSED).subscribe({
                        when
                        {
                            it.code == KeyCode.DOWN && !tg.isSelected -> tg.fire()
                            it.code == KeyCode.UP && tg.isSelected    -> tg.fire()
                        }
                    })
                }
                else
                {
                    sub.unsubscribe()
                }
            })

            ButtonStyler.setupHover(groupedEntry, timeEntryButton, deleteButton)
            parentPane.children.add(ents)
        }

        private fun setupPaneView(pane: DayGridPane)
        {
            pane.prefWidthProperty().bind(paneWidth)
            val dayHeader = GridPane()
            val c1 = ColumnConstraints()
            c1.percentWidth = 50.0
            val c2 = ColumnConstraints()
            c2.percentWidth = 50.0

            dayHeader.columnConstraints.addAll(c1, c2)
            dayHeader.prefWidthProperty().bind(paneWidth)
            val localDate = LocalDate(date)
            val isToday = localDate == LocalDate.now()
            val currentDayLabel = Label(if (isToday) "Today" else localDate.toString(pattern))
            currentDayLabel.alignment = Pos.CENTER_LEFT
            currentDayLabel.setMaxSize(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE)
            currentDayLabel.styleClass.addAll("lbl-info", "daygrid-lbl")
            currentDayLabel.prefHeightProperty().bind(headerHeight.multiply(0.60))
            val standardSeconds = Duration(taskToEntries.values.flatten().map { it.duration }.sum()).standardSeconds
            val dayDuration = Label(standardSeconds.formatToElapsed())

            dayDuration.alignment = Pos.CENTER_RIGHT
            dayDuration.setMaxSize(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE)
            dayDuration.styleClass.addAll("lbl-info", "daygrid-lbl")

            dayHeader.addRow(0, currentDayLabel, dayDuration)
            var headerContainer = FlowPane()
            headerContainer.prefWidthProperty().bind(paneWidth)

            headerContainer.children.add(dayHeader)

            pane.addRow(0, headerContainer)
        }
    }
}