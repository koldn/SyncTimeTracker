package ru.dkolmogortsev.utils

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class AppStyles : Stylesheet() {
    companion object {
        val dayLabel by cssclass()
        val anchorPane by cssclass()
        val textField by cssclass()
    }

    init {
        dayLabel {
            fontWeight = FontWeight.BOLD
            textFill = Color.WHITE
            println(this)
        }

        anchorPane {
            backgroundColor += Color.WHITE
            text {
                insets(left = 20.0)
            }
            borderRadius += box(0.px)
//            and(hover){
//                borderColor += box(Color.GREEN)
//                borderWidth += box(3.px, 3.px, 3.px, 3.px)
//                and(focused){
//                    borderColor += box(Color.LIGHTBLUE)
//                    borderWidth += box(3.px, 3.px, 3.px, 3.px)
//                }
//            }
//            and(focused) {
//                borderColor += box(Color.LIGHTBLUE)
//                borderWidth += box(3.px, 0.px, 3.px, 3.px)
//            }
        }
        textField {
            and(hover) {
                s(anchorPane) {
                    borderColor += box(Color.GREEN)
                    borderWidth += box(3.px, 3.px, 3.px, 3.px)
                }
                and(focused) {
                    s(anchorPane) {
                        borderColor += box(Color.LIGHTBLUE)
                        borderWidth += box(3.px, 3.px, 3.px, 3.px)
                    }
                }
            }
        }
    }
}