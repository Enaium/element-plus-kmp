package cn.enaium.elementplus

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElMessageHost
import cn.enaium.elementplus.components.rememberElMessageService
import cn.enaium.elementplus.theme.ElConfigProvider
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class MessageAnimationTest {
    @Test
    fun messageExitsThroughTheAnimationBeforeBeingRemoved() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                val service = rememberElMessageService()
                ElMessageHost(service) {
                    ElButton(
                        onClick = {
                            service.show("Animated message", duration = 0, showClose = true)
                        },
                        text = "Show animated",
                    )
                }
            }
        }

        onNodeWithText("Show animated").performClick()
        onNodeWithText("Animated message").assertExists()

        mainClock.autoAdvance = false
        onNodeWithContentDescription("Close").performClick()
        mainClock.advanceTimeByFrame()
        onNodeWithText("Animated message").assertExists()

        mainClock.advanceTimeBy(500)
        onNodeWithText("Animated message").assertDoesNotExist()
    }
}
