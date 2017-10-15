package ru.dkolmogortsev.ui.models

import javafx.collections.FXCollections
import ru.dkolmogortsev.task.TimeEntry
import tornadofx.Component
import tornadofx.ScopedInstance
import java.util.*

/**
 * Created by dkolmogortsev on 10/15/17.
 */
class TaskPanelModel : Component(), ScopedInstance {
    val map = FXCollections.observableMap(TreeMap<Long, List<TimeEntry>>({ l1: Long, l2: Long -> l1.compareTo(l2) }))
}