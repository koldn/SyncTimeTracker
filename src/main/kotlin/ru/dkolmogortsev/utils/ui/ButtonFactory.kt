package ru.dkolmogortsev.utils.ui

import javafx.beans.binding.DoubleExpression
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

    fun Button.setup(height: DoubleExpression) {
        setMaxSize(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE)
        styleClass.clear()
        alignment = Pos.CENTER
        prefHeightProperty().bind(height.multiply(0.90))
        isFocusTraversable = false
    }

    fun createStartTimeEntryButton(entryId: Long, height: DoubleExpression): Button {
        return button {
            setup(height)
            ButtonStyler.asStartButton(this)
            action {
                eventPublisher.publishTaskStarted(entryId)
            }
        }
    }

    fun createDeleteTimeEntryButton(entryId: Long, height: DoubleExpression): Button {
        return button {
            setup(height)
            ButtonStyler.asDeleteButton(this)
            action {
                eventPublisher.publishTimeEntryDeleted(entryId)

            }
        }
    }

}