package ru.dkolmogortsev.utils.ui

import javafx.geometry.Pos
import javafx.scene.control.Button
import ru.dkolmogortsev.events.EventPublisher
import tornadofx.*

/**
 * Created by dkolmogortsev on 10/15/17.
 */
class ButtonFactory : UIComponent("ButtonFactory"), ScopedInstance {
    override val root = label { }

    private val eventPublisher: EventPublisher by inject()

    fun Button.setup() {
        setMaxSize(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE)
        styleClass.clear()
        alignment = Pos.CENTER
        val heightProperty = primaryStage.heightProperty()
        prefHeightProperty().bind(heightProperty.multiply(0.1))
        isFocusTraversable = false
    }

    fun createStartTimeEntryButton(entryId: Long): Button {
        return button {
            setup()
            ButtonStyler.asStartButton(this)
            action {
                eventPublisher.publishTaskStarted(entryId)
            }
        }
    }

    fun createDeleteTimeEntryButton(entryId: Long): Button {
        return button {
            setup()
            ButtonStyler.asDeleteButton(this)
            action {
                eventPublisher.publishTimeEntryDeleted(entryId)
            }
        }
    }

}