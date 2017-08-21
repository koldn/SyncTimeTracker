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
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.control.ToggleButton
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
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
import ru.dkolmogortsev.events.TaskStartedEvent
import ru.dkolmogortsev.events.TimeEntryDeletedEvent
import ru.dkolmogortsev.task.Task
import ru.dkolmogortsev.task.TimeEntry
import ru.dkolmogortsev.task.storage.TaskStorage
import ru.dkolmogortsev.utils.ui.TimeEntryUiHelper.constraints
import ru.dkolmogortsev.utils.formatToElapsed
import ru.dkolmogortsev.utils.getTimeFromLong
import ru.dkolmogortsev.utils.ui.DayGridBuilderService
import java.util.*
import javax.inject.Inject

/**
 * Created by dkolmogortsev on 2/7/17.
 */
@ArtifactProviderFor(GriffonView::class)
class TaskPanelView : AbstractGriffonView()
{
    @MVCMember
    private lateinit var parentView: ControlAndTaskView
    @MVCMember
    private lateinit var model: TaskPanelModel
    @Inject
    private lateinit var dayGridBuilderService: DayGridBuilderService
    private lateinit var entriesPane: DailyGridContainer
    override fun initUI()
    {
        val scrollPane = ScrollPane()
        scrollPane.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        scrollPane.vbarPolicy = ScrollBarPolicy.NEVER
        scrollPane.prefHeight(400.0)
        scrollPane.prefWidthProperty().bind(parentView.getContainerPane().widthProperty())
        scrollPane.isFocusTraversable = false
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
        val node = parentView.pane.children[0] as GridPane

        EventStreams.changesOf(model.map).subscribe { change ->
            val date = change.key.toLong()
            val list = change.map[date] as List<TimeEntry>
            val gridIndex = entriesPane.getGridIndex(date)
            val dayGrid = dayGridBuilderService.Builder(date,
                    node.widthProperty(),
                    node.heightProperty(),
                    list.groupBy { it.task.id }).build()
            entriesPane.children.add(dayGrid)
        }
    }

    companion object
    {
        val pattern = "dd/MM/yyyy" //TODO make it configurable
        val CHILD_ENTRY_INSETS = Insets(0.0, 0.0, 0.0, 20.0)
    }
}
