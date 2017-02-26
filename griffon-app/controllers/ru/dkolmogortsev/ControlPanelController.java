package ru.dkolmogortsev;

import griffon.core.artifact.GriffonController;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import griffon.transform.Threading;
import griffon.transform.Threading.Policy;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;
import org.reactfx.util.FxTimer;
import org.reactfx.util.Timer;
import ru.dkolmogortsev.messages.StartTask;
import ru.dkolmogortsev.messages.TaskStopped;
import ru.dkolmogortsev.task.Task;
import ru.dkolmogortsev.task.TimeEntry;
import ru.dkolmogortsev.task.search.SearchFields;
import ru.dkolmogortsev.task.search.TaskSearcher;
import ru.dkolmogortsev.task.storage.TaskStorage;
import ru.dkolmogortsev.task.storage.TimeEntriesStorage;

@ArtifactProviderFor(GriffonController.class)
public class ControlPanelController extends AbstractGriffonController
{
    @MVCMember
    private ControlPanelModel model;

    @Inject
    private TaskSearcher searcher;

    @Inject
    private TaskStorage storage;

    @Inject
    private TimeEntriesStorage entriesStorage;

    private Timer backUpTimer;

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    public void start()
    {
        if (!model.isTaskStarted())
        {
            String description = model.taskDescriptionProperty().get();
            String nameUI = model.taskNameProperty().get();
            Task saved = storage.save(new Task(description, Task.NO_PROJECT));
            TimeEntry te = new TimeEntry(System.currentTimeMillis(), saved);
            entriesStorage.save(te);
            model.currentTimeEntryIdProperty().set(te.getId());
            initTimeEntryBackup(te);
            model.startTimer();
        }
        else{
            TimeEntry te = entriesStorage.get(model.currentTimeEntryIdProperty().get());
            te.stop();
            getApplication().getEventRouter().publishEvent(new TaskStopped(te.getId()));
            model.stopTimer();
        }
    }

    @Threading(Policy.OUTSIDE_UITHREAD)
    private void initTimeEntryBackup(TimeEntry te)
    {
        backUpTimer = FxTimer.runPeriodically(Duration.ofSeconds(10), () ->
        {
            te.updateDuration(model.getElapsedProperty());
            entriesStorage.save(te);
        });
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    public void search(String searchStr, SearchFields field)
    {
        List<String> search = searcher.search(searchStr, field).stream().map(it -> field.getMapper().apply(it))
                .collect(Collectors.toList());
        model.setList(search);
    }

    public void onStartTask(StartTask task){
        Task taskFromStorage = storage.getTask(task.getTaskId());
        model.taskDescriptionProperty().set(taskFromStorage.getDescription());
        model.taskNameProperty().set(taskFromStorage.getTaskName());
        start();
    }

}