package ru.dkolmogortsev.utils

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class AppStyles : Stylesheet() {
    companion object {
        val dayLabel by cssclass()
        val descriptionField by cssclass()
    }

    init {
        dayLabel {
            fontWeight = FontWeight.BOLD
            textFill = Color.WHITE
            println(this)
        }

        descriptionField {
            backgroundColor += Color.WHITE
            text {
                insets(left = 20.0)
            }
            and(focused) {
                borderColor += box(Color.LIGHTBLUE)
                borderWidth += box(3.px, 0.px, 3.px, 3.px)
            }
        }
    }
}