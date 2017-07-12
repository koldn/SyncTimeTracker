package ru.dkolmogortsev

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView
import org.reactfx.EventStreams
import org.reactfx.Subscription
import ru.dkolmogortsev.messages.StartTask
import ru.dkolmogortsev.task.Task
import ru.dkolmogortsev.task.search.SearchFields
import ru.dkolmogortsev.task.search.SearchFields.TASKNAME

@ArtifactProviderFor(GriffonView::class)
class ControlPanelView : AbstractJavaFXGriffonView() {
    @FXML
    private lateinit var startButton: Button
    @FXML
    private lateinit var timerLabel: Label
    @FXML
    private lateinit var taskDescription: TextField
    @FXML
    private lateinit var taskName: TextField
    @FXML
    private lateinit var menuButton: Button
    @MVCMember
    private lateinit var controller: ControlPanelController
    @MVCMember
    private lateinit var model: ControlPanelModel
    @MVCMember
    private lateinit var parentView: ControlAndTaskView
    private val started = SimpleBooleanProperty(false)
    private val tasksToShow = SimpleListProperty<Task>()
    private val menu = ContextMenu()
    private lateinit var eventSub: Subscription
    private lateinit var popupSubscription: Subscription

    override fun initUI() {
        val node = init()
        timerLabel.textProperty().bind(model.timerTextProp)
        started.bind(model.taskStartedProperty())
        tasksToShow.bind(model.tasksProperty())

        taskDescription.textProperty().bindBidirectional(model.taskDescriptionProperty())
        taskName.textProperty().bindBidirectional(model.taskNameProperty())

        startButton.styleClass.clear()
        menuButton.styleClass.clear()
        val menuIcon = MaterialDesignIconView(MaterialDesignIcon.MENU)
        menuButton.heightProperty().addListener { _, _, newValue -> menuIcon.glyphSize = newValue.toDouble() / 2 }
        menuButton.graphic = menuIcon


        toStartButton()
        parentView.getContainerPane().addRow(0, node)
        started.addListener { _, _, newValue ->
            if (newValue) {
                toStopButton()
            } else {
                toStartButton()
            }
        }
        node.requestFocus()
        onFocus(taskDescription, SearchFields.DESCRIPTION)
        onFocus(taskName, TASKNAME)

    }

    private fun buildPopupElement(menu: ContextMenu, s: Task, mapper: (Task) -> String) {
        val pane = GridPane()

        val c = ColumnConstraints()
        c.percentWidth = 100.0
        pane.columnConstraints.add(c)
        val innerButton = Label(mapper.invoke(s))
        innerButton.padding = Insets(5.0, 0.0, 0.0, 5.0)
        innerButton.maxWidth = java.lang.Double.MAX_VALUE
        innerButton.maxHeight = java.lang.Double.MAX_VALUE
        innerButton.alignment = Pos.CENTER_LEFT

        pane.addRow(0, innerButton)
        pane.prefWidth = taskDescription.width
        pane.prefHeight = taskDescription.height * 0.75
        val customMenuItem = CustomMenuItem(pane)

        customMenuItem.setOnAction { event -> application.eventRouter.publishEvent(StartTask(s.id)) }

        menu.items.add(customMenuItem)
    }

    // build the UI
    private fun init(): Node {
        var node = loadFromFXML()!!
        connectActions(node, controller)
        connectMessageSource(node)
        return node
    }

    private fun toStartButton() {
        startButton.styleClass.setAll("btn", "btn-success")
        startButton.text = "Start"
    }

    private fun toStopButton() {
        startButton.styleClass.setAll("btn", "btn-danger")
        startButton.text = "Stop"
    }

    private fun onFocus(focusedField: TextField, searchField: SearchFields) {
        menu.maxWidthProperty().bind(focusedField.widthProperty())
        menu.isAutoFix = true

        EventStreams.changesOf(focusedField.focusedProperty()).subscribe { booleanChange ->
            if (booleanChange.newValue) {
                menu.show(focusedField, Side.BOTTOM, 0.0, 0.0)
                eventSub = EventStreams.valuesOf(focusedField.textProperty())
                        .subscribe { s -> controller.search(s, searchField) }
                if (focusedField.text == searchField.toString()) {
                    focusedField.text = ""
                }


                popupSubscription = EventStreams.valuesOf<ObservableList<Task>>(tasksToShow).subscribe { tasks ->
                    if (tasks == null) {
                    }
                    menu.items.clear()
                    tasks.stream().limit(10).forEach { s -> buildPopupElement(menu, s, searchField.mapper) }
                    if (tasks.size > 10) {
                        buildPopupElement(menu, Task("Found more than 10 elements", "Found more than 10 elements"), { task -> task.description })
                    }
                    menu.show(focusedField, Side.BOTTOM, 0.0, 0.0)
                }

            } else {
                popupSubscription.unsubscribe()
                eventSub.unsubscribe()
                menu.hide()
            }
        }


    }

}
