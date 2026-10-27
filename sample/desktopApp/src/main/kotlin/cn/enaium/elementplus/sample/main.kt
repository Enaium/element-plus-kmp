package cn.enaium.elementplus.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "element-plus-kmp",
    ) {
        App()
    }
}