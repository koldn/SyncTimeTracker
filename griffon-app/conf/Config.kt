import griffon.util.AbstractMapResourceBundle

class Config : AbstractMapResourceBundle() {
    override fun initialize(entries: MutableMap<String, Any>) {
        entries.put("application", hashMapOf(
                "title" to "time-tracker",
                "startupGroups" to listOf("mainView"),
                "autoShutdown" to true
        ))
        entries.put("mvcGroups", hashMapOf(
                "controlPanel" to hashMapOf(
                        "model" to "ru.dkolmogortsev.ControlPanelModel",
                        "view" to "ru.dkolmogortsev.ControlPanelView",
                        "controller" to "ru.dkolmogortsev.ControlPanelController"),
                "taskPanel" to hashMapOf(
                        "model" to "ru.dkolmogortsev.TaskPanelModel",
                        "view" to "ru.dkolmogortsev.TaskPanelView",
                        "controller" to "ru.dkolmogortsev.TaskPanelController"
                ),
                "mainView" to hashMapOf("view" to "ru.dkolmogortsev.ControlAndTaskView")
        ))
    }
}