package ru.dkolmogortsev.utils

import javafx.scene.layout.*
import javafx.scene.paint.Paint
import tornadofx.hgrow
import tornadofx.vgrow
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by dkolmogortsev on 6/11/17.
 */

private val SECONDS_IN_MINUTE = 60
private val MINUTES_IN_HOUR = 60
private val SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR

fun Long.getTimeFromLong(): String {
    return SimpleDateFormat("HH:mm").format(Date(this))
}

fun Long.formatDate(): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(this))
}

fun Long.formatToElapsed(): String {
    val hours = this / SECONDS_IN_HOUR
    val minutes = this % SECONDS_IN_HOUR / SECONDS_IN_MINUTE
    val secs = this % SECONDS_IN_MINUTE
    return String.format("%d:%02d:%02d", hours, minutes, secs)
}

fun Region.setControlPanelProps() {
    this.vgrow = Priority.ALWAYS
    this.hgrow = Priority.ALWAYS
    this.maxHeight = Double.MAX_VALUE
    this.maxWidth = Double.MAX_VALUE
}

fun AnchorPane.newBorderColor(color: Paint) {
    this.border = Border(BorderStroke(color, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT))
}

fun AnchorPane.clearBorder() {
    this.border = Border.EMPTY
}