package ru.dkolmogortsev;

import griffon.core.artifact.GriffonController;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import javafx.collections.ObservableList;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;
import ru.dkolmogortsev.messages.TaskStopped;
import ru.dkolmogortsev.task.TimeEntry;
import ru.dkolmogortsev.task.search.TaskSearcher;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Map;
import ru.dkolmogortsev.task.storage.TimeEntriesStorage;

/**
 * Created by dkolmogortsev on 2/7/17.
 */
@ArtifactProviderFor(GriffonController.class)
public class TaskPanelController extends AbstractGriffonController {

    @MVCMember
    private TaskPanelModel model;

    @Inject
    private TimeEntriesStorage timeEntriesStorage;

    public void onTaskStopped(TaskStopped taskStopped){
        TimeEntry timeEntry = timeEntriesStorage.get(taskStopped.getId());
        model.newTimeEntryProperty().set(timeEntry);
    }

}
