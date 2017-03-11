package ru.dkolmogortsev;

import griffon.core.artifact.GriffonModel;
import griffon.metadata.ArtifactProviderFor;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;
import ru.dkolmogortsev.task.TimeEntry;

/**
 * Created by dkolmogortsev on 2/7/17.
 */
@ArtifactProviderFor(GriffonModel.class)
public class TaskPanelModel extends AbstractGriffonModel
{

    private SimpleObjectProperty<TimeEntry> newTimeEntry = new SimpleObjectProperty<>();

    private ObservableMap<String, List<TimeEntry>> entries = FXCollections.observableHashMap();

    public ObservableMap<String, List<TimeEntry>> getMap()
    {
        return entries;
    }

    public SimpleObjectProperty<TimeEntry> newTimeEntryProperty()
    {
        return newTimeEntry;
    }

}