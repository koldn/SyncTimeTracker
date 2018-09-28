package ru.dkolmogortsev

import ru.dkolmogortsev.ui.views.MainView
import ru.dkolmogortsev.utils.AppStyles
import tornadofx.App
import tornadofx.InternalWindow
import tornadofx.reloadStylesheetsOnFocus

/**
 * Created by dkolmogortsev on 10/12/17.
 */
class TimeTracker : App(MainView::class, AppStyles::class) {
    init {
        reloadStylesheetsOnFocus()
    }
}