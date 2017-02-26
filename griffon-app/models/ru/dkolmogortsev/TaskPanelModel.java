package ru.dkolmogortsev;

import griffon.core.artifact.GriffonModel;
import griffon.metadata.ArtifactProviderFor;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import ru.dkolmogortsev.task.TimeEntry;

/**
 * Created by dkolmogortsev on 2/7/17.
 */
@ArtifactProviderFor(GriffonModel.class)
public class TaskPanelModel extends AbstractGriffonModel {

    private SimpleObjectProperty<TimeEntry> newTimeEntry = new SimpleObjectProperty<>();

    public SimpleObjectProperty<TimeEntry> newTimeEntryProperty()
    {
        return newTimeEntry;
    }
}
