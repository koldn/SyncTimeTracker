package ru.dkolmogortsev

import griffon.core.artifact.GriffonController
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading
import griffon.transform.Threading.Policy
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import org.reactfx.util.FxTimer
import org.reactfx.util.Timer
import ru.dkolmogortsev.messages.StartTask
import ru.dkolmogortsev.messages.TaskStopped
import ru.dkolmogortsev.task.TimeEntry
import ru.dkolmogortsev.task.search.SearchFields
import ru.dkolmogortsev.task.search.TaskSearcher
import ru.dkolmogortsev.task.storage.TaskStorage
import ru.dkolmogortsev.task.storage.TimeEntriesStorage
import java.time.Duration
import javax.inject.Inject

@ArtifactProviderFor(GriffonController::class)
class ControlPanelController : AbstractGriffonController() {
    @MVCMember
    lateinit var model: ControlPanelModel

    @Inject
    private lateinit var searcher: TaskSearcher

    @Inject
    private lateinit var storage: TaskStorage

    @Inject
    private lateinit var entriesStorage: TimeEntriesStorage

    private lateinit var backUpTimer: Timer

    override fun mvcGroupInit(args: MutableMap<String, Any>) {
        getApplication().eventRouter.addEventListener(StartTask::class.java, { onStartTask(it!![0] as StartTask) })
        super.mvcGroupInit(args)
    }


    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    fun start() {
        if (!model.isTaskStarted) {
            val description = model.taskDescriptionProperty().get()
            val nameUI = model.taskNameProperty().get()
            val task = storage.create(description, nameUI)
            val te = TimeEntry(System.currentTimeMillis(), task.id)
            entriesStorage.save(te)
            model.timeEntryId = te.id
            initTimeEntryBackup(te)
            model.startTimer()
        } else {
            stopBackgroundBackup()
            val te = entriesStorage[model.timeEntryId]
            te.updateDuration(model.getElapsedProperty())
            te.stop()
            entriesStorage.save(te)
            getApplication().eventRouter.publishEvent(TaskStopped(te.id))
            model.stopTimer()
        }
    }

    @Threading(Policy.OUTSIDE_UITHREAD)
    private fun initTimeEntryBackup(te: TimeEntry) {
        backUpTimer = FxTimer.runPeriodically(Duration.ofSeconds(10)) {
            te.updateDuration(model.getElapsedProperty())
            entriesStorage.save(te)
        }
    }

    @Threading(Policy.OUTSIDE_UITHREAD)
    private fun stopBackgroundBackup() {
        backUpTimer.stop()
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    fun search(searchStr: String, field: SearchFields) {
        model.setList(searcher.search(searchStr, field))
    }

    fun onStartTask(task: StartTask) {
        val taskFromStorage = storage.getTask(task.taskId)
        model.taskDescriptionProperty().set(taskFromStorage.description)
        model.taskNameProperty().set(taskFromStorage.taskName)
        start()
    }
}