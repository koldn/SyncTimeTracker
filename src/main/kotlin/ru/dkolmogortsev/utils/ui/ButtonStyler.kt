package ru.dkolmogortsev.utils.ui

import de.jensd.fx.glyphs.GlyphIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.binding.Bindings
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
import javafx.scene.paint.Color
import org.reactfx.EventStreams
import tornadofx.onChange

/**
 * Created by dkolmogortsev on 30.07.2017.
 */
object ButtonStyler
{
    fun asStartButton(button: Button)
    {
        setupIcon(button, Color.DARKGREEN, Color.BLACK, MaterialDesignIconView(MaterialDesignIcon.PLAY))
    }

    fun asDeleteButton(button: Button)
    {
        setupIcon(button, Color.DARKRED, Color.BLACK, MaterialIconView(MaterialIcon.DELETE))
    }

    fun setupHover(entry: GridPane, startButton: Button, deleteButton: Button)
    {
        val focusedOrHover = entry.focusedProperty().or(entry.hoverProperty())
        val cornerRadii = CornerRadii(4.0)
        val bindings = Bindings.`when`(focusedOrHover)
                .then(Background(BackgroundFill(Color.LIGHTGRAY, cornerRadii, null)))
                .otherwise(Background(BackgroundFill(Color.TRANSPARENT, cornerRadii, null)))
        entry.backgroundProperty().bind(bindings)

        entry.isFocusTraversable = true
        focusedOrHover.onChange { boolean ->
            if (boolean) {
                entry.scene.onKeyPressed = EventHandler {
                    when {
                        it.code == KeyCode.DELETE -> deleteButton.fire()
                        it.code == KeyCode.ENTER && it.isControlDown -> startButton.fire()
                    }
                }
            }
        }
        deleteButton.visibleProperty().bind(Bindings.`when`(focusedOrHover).then(true).otherwise(false))
        startButton.visibleProperty().bind(Bindings.`when`(focusedOrHover).then(true).otherwise(false))
    }

    private fun setupIcon(timeEntryButton: Button, hoverColor: Color, normalCover: Color, icon: GlyphIcon<*>)
    {
        icon.fillProperty()
                .bind(Bindings.`when`(timeEntryButton.hoverProperty()).then(hoverColor).otherwise(normalCover))
        EventStreams.changesOf(timeEntryButton.layoutBoundsProperty()).subscribe { icon.setGlyphSize(it.newValue.height / 2) }
        timeEntryButton.graphic = icon
    }
}