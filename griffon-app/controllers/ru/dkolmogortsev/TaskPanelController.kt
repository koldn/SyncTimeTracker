package ru.dkolmogortsev

import com.google.common.collect.Lists
import griffon.core.artifact.GriffonController
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import org.joda.time.LocalDate
import ru.dkolmogortsev.messages.TaskStopped
import ru.dkolmogortsev.messages.TimeEntryDeleted
import ru.dkolmogortsev.task.TimeEntry
import ru.dkolmogortsev.task.storage.TimeEntriesStorage
import javax.inject.Inject

/**
 * Created by dkolmogortsev on 2/7/17.
 */
@ArtifactProviderFor(GriffonController::class)
class TaskPanelController : AbstractGriffonController() {

    @MVCMember
    lateinit var model: TaskPanelModel

    @Inject
    lateinit var timeEntriesStorage: TimeEntriesStorage

    fun onTaskStopped(taskStopped: TaskStopped) {
        val timeEntry = timeEntriesStorage.get(taskStopped.id)

        (model.map as java.util.Map<Long, List<TimeEntry>>).compute(timeEntry.entryDate) { s, timeEntries -> timeEntriesStorage.getByEntryDate(s) }
    }

    fun onTimeEntryDeleted(deleted: TimeEntryDeleted) {
        val timeEntry = timeEntriesStorage.delete(deleted.entryId)
        (model.map as java.util.Map<Long, List<TimeEntry>>).compute(timeEntry.entryDate) { s, timeEntries ->
            val es = timeEntriesStorage.getByEntryDate(s)
            es
        }
    }

    override fun mvcGroupInit(
            args: Map<String, Any>) {
        super.mvcGroupInit(args)
        initData()
    }

    fun initData() {
        val modelMap = model.map
        timeEntriesStorage.entriesGroupedByDay.entries.stream()
                .forEach { stringListEntry -> modelMap.put(stringListEntry.key, stringListEntry.value) }
        if (modelMap.isEmpty()) {
            modelMap.put(LocalDate.now().toDate().time, Lists.newArrayList<TimeEntry>())
        }
    }

}
