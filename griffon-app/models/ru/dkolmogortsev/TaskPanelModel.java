package ru.dkolmogortsev;

import griffon.core.artifact.GriffonModel;
import griffon.metadata.ArtifactProviderFor;
import java.util.List;
import java.util.TreeMap;
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
    private ObservableMap<Long, List<TimeEntry>> entries = FXCollections.observableMap(new TreeMap<>());

    public ObservableMap<Long, List<TimeEntry>> getMap()
    {
        return entries;
    }

}