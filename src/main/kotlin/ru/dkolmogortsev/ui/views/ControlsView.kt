package ru.dkolmogortsev.ui.views

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import org.reactfx.EventStreams
import org.reactfx.Subscription
import ru.dkolmogortsev.events.EventPublisher
import ru.dkolmogortsev.task.Task
import ru.dkolmogortsev.task.search.SearchFields
import ru.dkolmogortsev.ui.controllers.ControlsController
import ru.dkolmogortsev.ui.models.ControlModel
import ru.dkolmogortsev.utils.AppStyles
import ru.dkolmogortsev.utils.clearBorder
import ru.dkolmogortsev.utils.newBorderColor
import ru.dkolmogortsev.utils.setControlPanelProps
import tornadofx.*

/**
 * Created by dkolmogortsev on 10/12/17.
 */
class ControlsView : View("My View") {

    var startButton: Button by singleAssign()
    var taskDescription: TextField by singleAssign()
    var projectButton: Button by singleAssign()
    private val model: ControlModel by inject()
    val controller: ControlsController by inject()
    private val eventPublisher: EventPublisher by inject()

    private val menu = ContextMenu()
    private lateinit var eventSub: Subscription
    private lateinit var popupSubscription: Subscription
    lateinit var parentView: MainView

    override val root = gridpane {
        prefWidth = 700.00
        row {
            button {
                setControlPanelProps()
                val materialDesignIconView = MaterialDesignIconView(MaterialDesignIcon.MENU)
                graphic = materialDesignIconView
                heightProperty().addListener { _, _, newValue ->
                    materialDesignIconView.glyphSize = newValue.toDouble() / 2
                }
                styleClass.clear()
            }
            anchorpane {
                vgrow = Priority.ALWAYS
                taskDescription = textfield {
                    setControlPanelProps()
                    styleClass.clear()
                    prefWidth = 200.0
                    id = "taskDescription"
                    textProperty().bindBidirectional(model.taskDescriptionProperty())
                    anchorpaneConstraints {
                        rightAnchor = 0
                        leftAnchor = 0
                        topAnchor = 0
                        bottomAnchor = 0
                    }
                    focusedProperty().onChange {

                        if (it) this@anchorpane.newBorderColor(Color.BLUE) else this@anchorpane.clearBorder()
                    }
                    hoverProperty().onChange {
                        if (!focusedProperty().value) {
                            if (it) {
                                this@anchorpane.newBorderColor(Color.LIGHTGRAY)
                            } else {
                                this@anchorpane.clearBorder()
                            }
                        }
                    }
                }
                projectButton = button {
                    alignment = Pos.CENTER_RIGHT
                    anchorpaneConstraints {
                        rightAnchor = 0
                        bottomAnchor = 0
                        topAnchor = 0
                    }
                    vgrow = Priority.ALWAYS
                    maxHeight = Double.MAX_VALUE
                    styleClass.clear()
                    val materialDesignIconView = MaterialDesignIconView(MaterialDesignIcon.LINK)
                    graphic = materialDesignIconView
                    heightProperty().addListener { _, _, newValue ->
                        materialDesignIconView.glyphSize = newValue.toDouble() / 2
                    }
                }
            }
            label {
                setControlPanelProps()
                textAlignment = TextAlignment.CENTER
                alignment = Pos.CENTER
                text = "00:00"
                textProperty().bind(model.timerTextProp)
            }
            startButton = button {
                setControlPanelProps()
                styleClass.clear()
                action {
                    controller.start()
                }
            }

        }

        var heightConstraint = RowConstraints()
        heightConstraint.vgrow = Priority.ALWAYS
        heightConstraint.percentHeight = -1.0
        rowConstraints.add(heightConstraint)

        listOf(10.00, 60.00, 15.00, 15.00).forEach {
            var constraint = ColumnConstraints()
            constraint.hgrow = Priority.SOMETIMES
            constraint.percentWidth = it
            this@gridpane.columnConstraints.add(constraint)
        }

    }

    init {
        toStartButton()
        model.taskStartedProperty().addListener({ _, _, newValue ->
            run {
                if (newValue) toStopButton() else toStartButton()
                startButton.requestFocus()
            }
        })
        onFocus(taskDescription, SearchFields.DESCRIPTION)
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

                popupSubscription = EventStreams.valuesOf<ObservableList<Task>>(model.searchResultsProperty()).subscribe { tasks ->
                    if (tasks == null) {
                    }
                    menu.hide()
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

    private fun buildPopupElement(menu: ContextMenu, s: Task, mapper: (Task) -> String) {
        val p = gridpane {
            row {
                label(mapper.invoke(s)) {
                    textOverrun = OverrunStyle.CLIP
                    padding = Insets(5.0, 0.0, 0.0, 5.0)
                    maxWidth = Double.MAX_VALUE
                    maxHeight = Double.MAX_VALUE
                }
            }
            prefWidth = taskDescription.width
            prefHeight = taskDescription.height * 0.75
            constraintsForColumn(0).percentWidth = 100.00
        }

        val customMenuItem = CustomMenuItem(p)
        customMenuItem.action { eventPublisher.publishTaskSelected(s.id) }
        menu.items.add(customMenuItem)
    }
}
