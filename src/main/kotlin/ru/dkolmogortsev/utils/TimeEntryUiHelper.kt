package ru.dkolmogortsev.utils

import com.google.common.collect.Lists
import javafx.scene.layout.ColumnConstraints

/**
 * Created by dkolmogortsev on 2/26/17.
 */
object TimeEntryUiHelper {
    //Task description, task name, start-stop, duration, start, delete
    val constraints: List<ColumnConstraints>
        get() {
            val result = Lists.newArrayList<ColumnConstraints>()
            Lists.newArrayList(35.0, 15.0, 15.0, 18.0, 7.5, 7.5).forEach { p ->
                val c = ColumnConstraints()
                c.percentWidth = p!!
                result.add(c)
            }
            return result
        }
}
