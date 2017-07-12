package ru.dkolmogortsev.controls

import javafx.geometry.Pos
import javafx.scene.control.Button

/**
 * Created by dkolmogortsev on 2/26/17.
 */
class TimeEntryButton : Button() {
    init {
        setMaxSize(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE)
        styleClass.clear()
        alignment = Pos.CENTER
    }
}
