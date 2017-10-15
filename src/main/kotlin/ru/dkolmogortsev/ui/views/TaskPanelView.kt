package ru.dkolmogortsev.ui.views

import org.reactfx.EventStreams
import ru.dkolmogortsev.customui.DailyGridContainer
import ru.dkolmogortsev.task.TimeEntry
import ru.dkolmogortsev.ui.controllers.TaskPanelController
import ru.dkolmogortsev.ui.models.TaskPanelModel
import ru.dkolmogortsev.utils.ui.DayGridBuilderService
import tornadofx.View
import tornadofx.scrollpane

/**
 * Created by dkolmogortsev on 10/15/17.
 */
class TaskPanelView : View("My View") {

    private val model: TaskPanelModel by inject()
    private val dayGridBuilderService: DayGridBuilderService by inject()

    private val controlsView: ControlsView by inject()

    private val controller: TaskPanelController by inject()

    private lateinit var entriesPane: DailyGridContainer


    lateinit var parentView: MainView

    override val root = scrollpane {
        prefHeight = 400.00
        isFocusTraversable = false
        entriesPane = DailyGridContainer()
        entriesPane.prefWidthProperty().bind(this.widthProperty())
        entriesPane.setOnScroll { event ->
            val deltaY = event.deltaY * 15
            val width = this.content.boundsInLocal.width
            val vvalue = this.vvalue
            this.vvalue = vvalue + -deltaY / width
        }
        content = entriesPane
    }

    override fun onDock() {
        root.prefWidthProperty().bind(parentView.root.widthProperty())
        EventStreams.changesOf(model.map).subscribe { change ->
            val date = change.key.toLong()
            val list = change.map[date] as List<TimeEntry>
            val gridIndex = entriesPane.getGridIndex(date)
            val dayGrid = dayGridBuilderService.Builder(date,
                    controlsView.root.widthProperty(),
                    controlsView.root.heightProperty(),
                    list.groupBy { it.task.id }).build()
            if (gridIndex == -1) {
                entriesPane.children.add(dayGrid)
            } else {
                entriesPane.children.set(gridIndex, dayGrid)
            }
        }
        controller.init()
    }
}
