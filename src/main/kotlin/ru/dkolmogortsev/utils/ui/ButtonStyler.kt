package ru.dkolmogortsev.utils.ui

import de.jensd.fx.glyphs.GlyphIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.binding.Bindings
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import org.reactfx.EventStreams
import org.reactfx.Subscription

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
        val booleanBinding = entry.focusedProperty().or(entry.hoverProperty())
        val focusedBinding = Bindings.`when`(booleanBinding)
        val cornerRadii = CornerRadii(4.0)
        val bindings = focusedBinding
                .then(Background(BackgroundFill(Color.LIGHTBLUE, cornerRadii, null)))
                .otherwise(Background(BackgroundFill(Color.TRANSPARENT, cornerRadii, null)))
        val defaultBorder = entry.border
        val lightBlueStroke = BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, cornerRadii, BorderWidths(0.5))
        val objectBinding = focusedBinding.then(Border(lightBlueStroke)).otherwise(defaultBorder)
        entry.borderProperty().bind(objectBinding)
        entry.backgroundProperty().bind(bindings)

        entry.isFocusTraversable = true
        var sub: Subscription = Subscription.EMPTY
        EventStreams.changesOf(booleanBinding).subscribe({
            if (it.newValue) {
                sub = EventStreams.eventsOf(entry, KeyEvent.KEY_PRESSED).subscribe({
                    when {
                        it.code.equals(KeyCode.DELETE) -> deleteButton.fire()
                        it.code.equals(KeyCode.ENTER) && it.isControlDown -> startButton.fire()
                    }
                })
            } else {
                sub.unsubscribe()
            }
        })
        deleteButton.visibleProperty().bind(focusedBinding.then(true).otherwise(false))
        startButton.visibleProperty().bind(focusedBinding.then(true).otherwise(false))
    }

    private fun setupIcon(timeEntryButton: Button, hoverColor: Color, normalCover: Color, icon: GlyphIcon<*>)
    {
        icon.fillProperty()
                .bind(Bindings.`when`(timeEntryButton.hoverProperty()).then(hoverColor).otherwise(normalCover))
        EventStreams.changesOf(timeEntryButton.layoutBoundsProperty()).subscribe({ icon.setGlyphSize(it.newValue.height / 2) })
        timeEntryButton.graphic = icon
    }
}