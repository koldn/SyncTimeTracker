package ru.dkolmogortsev;

import com.google.common.collect.Lists;
import griffon.core.artifact.GriffonController;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableMap;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;
import org.joda.time.LocalDate;
import ru.dkolmogortsev.messages.TaskStopped;
import ru.dkolmogortsev.messages.TimeEntryDeleted;
import ru.dkolmogortsev.task.TimeEntry;
import ru.dkolmogortsev.task.storage.TimeEntriesStorage;

/**
 * Created by dkolmogortsev on 2/7/17.
 */
@ArtifactProviderFor(GriffonController.class)
public class TaskPanelController extends AbstractGriffonController
{

    @MVCMember
    private TaskPanelModel model;

    @Inject
    private TimeEntriesStorage timeEntriesStorage;

    public void onTaskStopped(TaskStopped taskStopped)
    {
        TimeEntry timeEntry = timeEntriesStorage.get(taskStopped.getId());

        model.getMap().compute(timeEntry.getEntryDate(), (s, timeEntries) -> timeEntriesStorage.getByEntryDate(s));
    }

    public void onTimeEntryDeleted(TimeEntryDeleted deleted)
    {
        TimeEntry timeEntry = timeEntriesStorage.delete(deleted.getEntryId());
        model.getMap().compute(timeEntry.getEntryDate(), (s, timeEntries) ->
        {
            List<TimeEntry> es = timeEntriesStorage.getByEntryDate(s);
            return es;
        });
    }

    @Override
    public void mvcGroupInit(
            @Nonnull
                    Map<String, Object> args)
    {
        super.mvcGroupInit(args);
        initData();
    }

    public void initData()
    {
        ObservableMap<Long, List<TimeEntry>> modelMap = model.getMap();
        timeEntriesStorage.getEntriesGroupedByDay().entrySet().stream().forEachOrdered(stringListEntry ->
        {
            modelMap.put(stringListEntry.getKey(), stringListEntry.getValue());
        });
        if (modelMap.isEmpty())
        {
            modelMap.put(LocalDate.now().toDate().getTime(), Lists.newArrayList());
        }
    }

}
