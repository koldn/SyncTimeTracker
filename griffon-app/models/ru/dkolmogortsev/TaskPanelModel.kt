package ru.dkolmogortsev

import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import javafx.collections.FXCollections
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel
import ru.dkolmogortsev.task.TimeEntry
import java.util.*

/**
 * Created by dkolmogortsev on 2/7/17.
 */
@ArtifactProviderFor(GriffonModel::class)
class TaskPanelModel : AbstractGriffonModel() {
    val map = FXCollections.observableMap(TreeMap<Long, List<TimeEntry>>())

}