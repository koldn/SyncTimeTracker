package ru.dkolmogortsev.task.backup;

import java.time.Duration;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.reactfx.util.FxTimer;
import org.reactfx.util.Timer;
import ru.dkolmogortsev.task.TimeEntry;
import ru.dkolmogortsev.task.storage.TimeEntriesStorage;

/**
 * Created by dkolmogortsev on 2/25/17.
 */
@Singleton
public class TimeEntryBackupService
{
    @Inject
    TimeEntriesStorage entriesStorage;

    Timer backUpTimer;

}
