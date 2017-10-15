package ru.dkolmogortsev.ui.views

import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.RowConstraints
import tornadofx.View

/**
 * Created by dkolmogortsev on 10/12/17.
 */
class MainView : View("Main") {


    val controls: ControlsView by inject()
    val taskPanelView: TaskPanelView by inject()
    override val root = GridPane()


    init {
        controls.parentView = this
        taskPanelView.parentView = this
        with(root) {
            vgap = 10.0
            primaryStage.height = 400.00
            var rc1 = RowConstraints()
            rc1.percentHeight = 10.0
            var rc2 = RowConstraints()
            rc2.percentHeight = 90.00
            rowConstraints.addAll(rc1, rc2)
            var widthConstraint = ColumnConstraints()
            widthConstraint.percentWidth = 100.00
            columnConstraints.add(widthConstraint)
            addRow(0, controls.root)
            addRow(1, taskPanelView.root)
            isGridLinesVisible = true
        }


    }

    override fun onDock() {
        with(root) {
            root.scene.stylesheets.add("bootstrapfx.css")
            root.scene.stylesheets.add("application.css")
        }
    }
}
