package ru.dkolmogortsev.utils.ui

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.binding.Bindings
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ToggleButton
import javafx.scene.input.KeyCode
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.FlowPane
import javafx.scene.layout.GridPane
import javafx.scene.text.Text
import org.joda.time.Duration
import org.joda.time.LocalDate
import ru.dkolmogortsev.controls.TimeEntryButton
import ru.dkolmogortsev.customui.DayGridPane
import ru.dkolmogortsev.events.EventPublisher
import ru.dkolmogortsev.task.TimeEntry
import ru.dkolmogortsev.utils.AppStyles
import ru.dkolmogortsev.utils.formatToElapsed
import tornadofx.*

/**
 * Created by dkolmogortsev on 30.07.2017.
 */
class DayGridBuilderService : UIComponent("dayBuilder"), ScopedInstance {
    override val root = label { }

    companion object {
        val pattern = "dd/MM/yyyy" //TODO make it configurable
        val CHILD_ENTRY_INSETS = Insets(0.0, 0.0, 0.0, 20.0)
    }

    private val eventPublisher: EventPublisher by inject()
    private val uiEntryBuilder = find(TimeEntryUiLineBuilderService::class)

    inner class Builder(private val date: Long, private val taskToEntries: Map<Long, List<TimeEntry>>)
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
            val uiLines = entries.asSequence().map { timeEntry -> uiEntryBuilder.Builder(timeEntry).build() }.toList()
            if (uiLines.size == 1)
            {
                parentPane.children.add(uiLines[0])
                return
            }

            val heightBinding = primaryStage.heightProperty().multiply(0.1)
            uiLines.forEach { gridPane ->
                gridPane.prefHeightProperty().bind(heightBinding)
                (gridPane.children[0] as Label).padding = CHILD_ENTRY_INSETS
            }
            val overAllDuration = entries.asSequence().map { it.duration }.sum()
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
            tg.selectedProperty().onChange { selected ->
                uiLines.forEach {
                    it.isFocusTraversable = selected
                    if (selected) {
                        ents.children.add(1, it)
                    } else {
                        ents.children.remove(it)
                    }
                }
            }
            val task = entries.first().task
            val timeEntryButton = TimeEntryButton()
            timeEntryButton.isFocusTraversable = false
            ButtonStyler.asStartButton(timeEntryButton)
            timeEntryButton.prefHeightProperty().bind(heightBinding)
            timeEntryButton.setOnAction { eventPublisher.publishTaskStarted(task.id) }
            val deleteButton = TimeEntryButton()
            deleteButton.isFocusTraversable = false
            ButtonStyler.asDeleteButton(deleteButton)
            deleteButton.prefHeightProperty().bind(heightBinding)
            deleteButton.setOnAction { entries.forEach { eventPublisher.publishTimeEntryDeleted(it.id) } }
            val groupedEntry = GridPane()
            groupedEntry.columnConstraints.addAll(TimeEntryUiHelper.constraints)
            groupedEntry.prefWidthProperty().bind(primaryStage.widthProperty())
            val desc = label(task.description) {
                padding = Insets(0.0, 0.0, 0.0, 10.0)
            }
            groupedEntry.addRow(0, desc, Label(task.taskName),
                    Label(Duration(overAllDuration).standardSeconds.formatToElapsed()), tg,
                    timeEntryButton, deleteButton)
            ents.children.add(0, groupedEntry)
            groupedEntry.focusedProperty().onChange { focused ->
                if (focused) {
                    groupedEntry.scene.onKeyPressed = EventHandler { it ->
                        when {
                            it.code == KeyCode.DOWN && !tg.isSelected -> tg.fire()
                            it.code == KeyCode.UP && tg.isSelected    -> tg.fire()
                        }
                    }
                }
            }
            ButtonStyler.setupHover(groupedEntry, timeEntryButton, deleteButton)
            parentPane.children.add(ents)
        }

        private fun setupPaneView(pane: DayGridPane)
        {
            pane.prefWidthProperty().bind(primaryStage.widthProperty())
            val dayHeader = GridPane()
            val c1 = ColumnConstraints()
            c1.percentWidth = 100.0

            dayHeader.columnConstraints.addAll(c1)
            dayHeader.prefWidthProperty().bind(primaryStage.widthProperty())
            val localDate = LocalDate(date)
            val isToday = localDate == LocalDate.now()
            val standardSeconds = Duration(taskToEntries.values.flatten().map { it.duration }.sum()).standardSeconds
            val currentDayLabel = label {
                text = "${if (isToday) "Today" else localDate.toString(pattern)}(${standardSeconds.formatToElapsed()})"
                maxHeight = Double.MAX_VALUE
                maxWidth = Double.MAX_VALUE
                alignment = Pos.CENTER_LEFT
                addClass("lbl-info")
                addClass(AppStyles.dayLabel)
                padding = Insets(0.0, 0.0, 0.0, 10.0)
            }
            currentDayLabel.prefHeightProperty().bind(primaryStage.heightProperty().multiply(0.08))
            dayHeader.addRow(0, currentDayLabel)
            var headerContainer = FlowPane()
            headerContainer.prefWidthProperty().bind(primaryStage.widthProperty())

            headerContainer.children.add(dayHeader)

            pane.addRow(0, headerContainer)
        }
    }
}