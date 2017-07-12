package ru.dkolmogortsev.customui

import javafx.scene.layout.FlowPane

/**
 * Created by dkolmogortsev on 3/11/17.
 */
class DailyGridContainer : FlowPane() {
    init {
        vgap = 10.0
    }

    fun getGridIndex(date: Long): Int {
        for (i in 0..children.size - 1) {
            val dayGrid = children[i] as DayGridPane
            if (dayGrid.date == date) {
                return i
            }
        }
        return -1
    }

}
