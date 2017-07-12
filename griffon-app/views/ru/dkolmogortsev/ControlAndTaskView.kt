package ru.dkolmogortsev

import griffon.core.artifact.GriffonView
import griffon.metadata.ArtifactProviderFor
import javafx.scene.Scene
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.RowConstraints
import javafx.stage.Stage
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView
import java.util.Collections.emptyMap

/**
 * Created by dkolmogortsev on 2/7/17.
 */
@ArtifactProviderFor(GriffonView::class)
class ControlAndTaskView : AbstractJavaFXGriffonView() {

    lateinit var pane: GridPane

    override fun mvcGroupInit(
            args: Map<String, Any>) {
        createMVCGroup("controlPanel")
        createMVCGroup("taskPanel")
    }

    override fun initUI() {
        val stage = getApplication().createApplicationContainer(emptyMap<String, Any>()) as Stage
        pane = GridPane()
        pane.vgap = 10.0
        val constr = ColumnConstraints()
        constr.percentWidth = 100.0
        pane.columnConstraints.add(constr)
        val rc = RowConstraints()
        rc.percentHeight = 10.0
        val rc2 = RowConstraints()
        rc2.percentHeight = 90.0
        pane.rowConstraints.addAll(rc, rc2)
        val scene = Scene(pane)
        scene.stylesheets.add("bootstrapfx.css")
        scene.stylesheets.add("/ru/dkolmogortsev/application.css")
        stage.scene = scene

        scene.setOnKeyPressed { event -> println(event) }

        stage.title = getApplication().configuration.getAsString("application.title")
        stage.height = 400.0
        getApplication().getWindowManager<Any>().attach("main", stage)
    }

    fun getContainerPane(): GridPane {
        return pane
    }
}
