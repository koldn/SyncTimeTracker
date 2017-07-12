package ru.dkolmogortsev

import de.jensd.fx.glyphs.GlyphIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.control.ToggleButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Text
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonView
import org.joda.time.Duration
import org.joda.time.LocalDate
import org.reactfx.EventStreams
import ru.dkolmogortsev.controls.TimeEntryButton
import ru.dkolmogortsev.customui.DailyGridContainer
import ru.dkolmogortsev.customui.DayGridPane
import ru.dkolmogortsev.messages.StartTask
import ru.dkolmogortsev.messages.TimeEntryDeleted
import ru.dkolmogortsev.task.Task
import ru.dkolmogortsev.task.TimeEntry
import ru.dkolmogortsev.task.storage.TaskStorage
import ru.dkolmogortsev.task.storage.TimeEntriesStorage
import ru.dkolmogortsev.utils.TimeEntryUiHelper.constraints
import ru.dkolmogortsev.utils.formatToElapsed
import ru.dkolmogortsev.utils.getTimeFromLong
import javax.inject.Inject

/**
 * Created by dkolmogortsev on 2/7/17.
 */
@ArtifactProviderFor(GriffonView::class)
class TaskPanelView : AbstractGriffonView() {

    @MVCMember
    private lateinit var parentView: ControlAndTaskView

    @MVCMember
    private lateinit var model: TaskPanelModel

    @Inject
    private lateinit var storage: TaskStorage

    @Inject
    private lateinit var entriesStorage: TimeEntriesStorage

    private lateinit var entriesPane: DailyGridContainer

    override fun initUI() {
        val scrollPane = ScrollPane()
        scrollPane.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        scrollPane.vbarPolicy = ScrollBarPolicy.NEVER
        scrollPane.prefHeight(400.0)
        scrollPane.prefWidthProperty().bind(parentView.getContainerPane().widthProperty())
        entriesPane = DailyGridContainer()
        entriesPane.prefWidthProperty().bind(scrollPane.widthProperty())
        scrollPane.content = entriesPane
        entriesPane.setOnScroll { event ->
            val deltaY = event.deltaY * 15
            val width = scrollPane.content.boundsInLocal.width
            val vvalue = scrollPane.vvalue
            scrollPane.vvalue = vvalue + -deltaY / width
        }
        parentView.getContainerPane().addRow(1, scrollPane)

        EventStreams.changesOf(model.map).subscribe { change ->
            val date = change.key.toLong()
            val list = change.map[date] as List<TimeEntry>
            val gridIndex = entriesPane.getGridIndex(date)
            if (gridIndex != -1) {
                entriesPane.children[gridIndex] = buildDayGrid(date, list)
            } else {
                entriesPane.children.add(0, buildDayGrid(date, list))
            }
        }
    }

    private fun buildTimeEntryLine(t: Task, timeEntry: TimeEntry): GridPane {
        val header = parentView.pane.children[0] as GridPane//Always header
        val entry = GridPane()

        entry.focusTraversableProperty().set(true)
        entry.columnConstraints.addAll(constraints)

        entry.prefWidthProperty().bind(parentView.pane.widthProperty())
        val timeEntryButton = TimeEntryButton()
        toStartTaskView(timeEntryButton)
        timeEntryButton.prefHeightProperty().bind(header.heightProperty().multiply(0.90))

        val deleteButton = TimeEntryButton()
        toDeleteEntryButton(deleteButton)
        deleteButton.prefHeightProperty().bind(header.heightProperty().multiply(0.90))

        EventStreams.eventsOf(timeEntryButton, MouseEvent.MOUSE_CLICKED)
                .subscribe { _ -> publishTaskStarted(t.id) }

        deleteButton.setOnAction { _ -> publishEntryDeleted(timeEntry) }

        val startStopString = StringBuilder().append(timeEntry.start.getTimeFromLong()).append(" -> ")
                .append(timeEntry.end.getTimeFromLong()).toString()
        val startStopLabel = Label(startStopString)
        startStopLabel.alignment = Pos.CENTER
        startStopLabel.setMaxSize(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE)
        entry.addRow(0, Label(t.description), Label(t.taskName), Label(
                Duration(timeEntry.duration).standardSeconds.formatToElapsed()),
                startStopLabel, timeEntryButton, deleteButton)

        initHover(entry, timeEntryButton, deleteButton)
        return entry
    }

    private fun toDeleteEntryButton(deleteButton: TimeEntryButton) {
        setupIcon(deleteButton, Color.RED, Color.BLACK, MaterialIconView(MaterialIcon.DELETE))
    }

    private fun initHover(entry: GridPane, timeEntryButton: TimeEntryButton, deleteButton: TimeEntryButton) {
        entry.backgroundProperty().bind(Bindings.`when`(entry.hoverProperty())
                .then(Background(BackgroundFill(Color.LIGHTGREY, null, null)))
                .otherwise(Background(BackgroundFill(Color.TRANSPARENT, null, null))))

        deleteButton.visibleProperty().bind(Bindings.`when`(entry.hoverProperty()).then(true).otherwise(false))
        timeEntryButton.visibleProperty().bind(Bindings.`when`(entry.hoverProperty()).then(true).otherwise(false))
    }

    private fun setupIcon(timeEntryButton: TimeEntryButton, hoverColor: Color, normalCover: Color, icon: GlyphIcon<*>) {
        icon.fillProperty()
                .bind(Bindings.`when`(timeEntryButton.hoverProperty()).then(hoverColor).otherwise(normalCover))
        timeEntryButton.layoutBoundsProperty()
                .addListener { _, _, newValue -> icon.setGlyphSize(newValue.height / 2) }
        timeEntryButton.graphic = icon
    }

    private fun buildDayGrid(date: Long, entries: List<TimeEntry>): DayGridPane {
        val pane = DayGridPane(date)
        pane.prefWidthProperty().bind((parentView.pane.children[0] as GridPane).widthProperty())
        val dayHeader = GridPane()
        val headerHeight = (parentView.pane.children[0] as GridPane).heightProperty()

        val c1 = ColumnConstraints()
        c1.percentWidth = 50.0

        val c2 = ColumnConstraints()
        c2.percentWidth = 50.0

        dayHeader.columnConstraints.addAll(c1, c2)
        dayHeader.prefWidthProperty().bind((parentView.pane.children[0] as GridPane).widthProperty())
        val localDate = LocalDate(date)
        val isToday = localDate == LocalDate.now()
        val currentDayLabel = Label(if (isToday) "Today" else localDate.toString(pattern))
        currentDayLabel.alignment = Pos.CENTER_LEFT
        currentDayLabel.setMaxSize(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE)
        currentDayLabel.styleClass.addAll("lbl-info", "daygrid-lbl")
        currentDayLabel.prefHeightProperty().bind(headerHeight.multiply(0.60))
        val dayDuration = Label(Duration(entries.map { it.duration }.sum()).standardSeconds.formatToElapsed())

        dayDuration.alignment = Pos.CENTER_RIGHT
        dayDuration.setMaxSize(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE)
        dayDuration.styleClass.addAll("lbl-info", "daygrid-lbl")
        dayHeader.addRow(0, currentDayLabel, dayDuration)
        pane.addRow(0, dayHeader)
        val e = FlowPane()

        entries.groupBy { it.taskId }.forEach { aLong, entries1 -> buildEntriesNew(e, aLong, entries1) }
        pane.addRow(1, e)
        return pane
    }

    private fun buildEntriesNew(parentPane: FlowPane, taskId: Long, entries: List<TimeEntry>) {
        val task = storage.getTask(taskId)
        val uiLines = entries.map { timeEntry -> buildTimeEntryLine(task, timeEntry) }.toList()


        if (uiLines.size == 1) {
            parentPane.children.add(uiLines[0])
            return
        }

        val controlPane = parentView.pane.children[0] as GridPane//Always header

        uiLines.forEach { gridPane ->
            gridPane.prefHeightProperty().bind(controlPane.heightProperty().multiply(0.80))
            (gridPane.children[0] as Label).padding = CHILD_ENTRY_INSETS
        }
        val overAllDuration = entries.map { it.duration }.sum()

        val ents = FlowPane()

        ents.prefWidthProperty().bind(parentPane.widthProperty())
        val tg = ToggleButton()
        tg.setMaxSize(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE)
        tg.styleClass.clear()

        val icon = MaterialDesignIconView(MaterialDesignIcon.ARROW_UP_DROP_CIRCLE_OUTLINE)
        tg.heightProperty()
                .addListener { _, _, newValue -> icon.glyphSize = newValue.toDouble() / 2 }
        val text = Text(entries.size.toString())
        tg.graphicProperty().bind(Bindings.`when`(tg.selectedProperty()).then<Text>(icon).otherwise(text))

        EventStreams.changesOf(tg.selectedProperty()).subscribe { booleanChange ->
            val newVal = booleanChange.newValue
            if (newVal) {
                ents.children.addAll(1, uiLines)
            } else {
                ents.children.removeAll(uiLines)
            }
        }

        val timeEntryButton = TimeEntryButton()
        toStartTaskView(timeEntryButton)
        timeEntryButton.prefHeightProperty().bind(controlPane.heightProperty().multiply(0.90))

        val deleteButton = TimeEntryButton()
        toDeleteEntryButton(deleteButton)
        deleteButton.prefHeightProperty().bind(controlPane.heightProperty().multiply(0.90))

        EventStreams.eventsOf(deleteButton, MouseEvent.MOUSE_CLICKED)
                .subscribe { _ -> entries.forEach { timeEntry -> publishEntryDeleted(timeEntry) } }

        EventStreams.eventsOf(timeEntryButton, MouseEvent.MOUSE_CLICKED)
                .subscribe { _ -> publishTaskStarted(taskId) }

        val groupedEntry = GridPane()
        groupedEntry.columnConstraints.addAll(constraints)
        groupedEntry.prefWidthProperty().bind((parentView.pane.children[0] as GridPane).widthProperty())
        groupedEntry.addRow(0, Label(task.description), Label(task.taskName),
                Label(Duration(overAllDuration).standardSeconds.formatToElapsed()), tg,
                timeEntryButton, deleteButton)
        ents.children.add(0, groupedEntry)

        initHover(groupedEntry, timeEntryButton, deleteButton)

        parentPane.children.add(ents)
    }

    private fun toStartTaskView(timeEntryButton: TimeEntryButton) {
        setupIcon(timeEntryButton, Color.LIGHTGREEN, Color.BLACK, MaterialDesignIconView(MaterialDesignIcon.PLAY))
    }

    private fun publishTaskStarted(taskId: Long) {
        getApplication().eventRouter.publishEvent(StartTask(taskId))
    }

    private fun publishEntryDeleted(timeEntry: TimeEntry) {
        getApplication().eventRouter.publishEvent(TimeEntryDeleted(timeEntry.id))
    }

    companion object {
        private val pattern = "dd/MM/yyyy" //TODO make it configurable

        private val CHILD_ENTRY_INSETS = Insets(0.0, 0.0, 0.0, 20.0)
    }
}
