package ru.dkolmogortsev;

import java.util.List;
import java.util.Map;

import javafx.beans.property.SimpleObjectProperty;

import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

import griffon.core.artifact.GriffonModel;
import griffon.metadata.ArtifactProviderFor;
import ru.dkolmogortsev.task.TimeEntry;

/**
 * Created by dkolmogortsev on 2/7/17.
 */
@ArtifactProviderFor(GriffonModel.class)
public class TaskPanelModel extends AbstractGriffonModel
{

    private SimpleObjectProperty<TimeEntry> newTimeEntry = new SimpleObjectProperty<>();

    private SimpleObjectProperty<Map<String, List<TimeEntry>>> groupedTimeEntries = new SimpleObjectProperty<>();

    public SimpleObjectProperty<Map<String, List<TimeEntry>>> groupedTimeEntriesProperty()
    {
        return groupedTimeEntries;
    }

    public SimpleObjectProperty<TimeEntry> newTimeEntryProperty()
    {
        return newTimeEntry;
    }
}
