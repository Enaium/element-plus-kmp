package cn.enaium.elementplus

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.internal.rememberElInteractionState
import cn.enaium.elementplus.theme.ElConfigProvider
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class PressFeedbackTest {
    @Test
    fun quickClickKeepsPressedFeedbackForAtLeastOneFrame() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                val interaction = remember { MutableInteractionSource() }
                val state = rememberElInteractionState(interaction)
                Box(
                    Modifier
                        .size(48.dp)
                        .testTag("press-target")
                        .clickable(
                            interactionSource = interaction,
                            indication = null,
                        ) {},
                ) {
                    BasicText(if (state.pressed) "Pressed" else "Idle")
                }
            }
        }

        mainClock.autoAdvance = false
        onNodeWithTag("press-target").performClick()
        mainClock.advanceTimeByFrame()
        onNodeWithText("Pressed").assertExists()

        mainClock.advanceTimeBy(80)
        onNodeWithText("Idle").assertExists()
    }

    @Test
    fun continuousClicksDoNotStayPressedForever() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                val interaction = remember { MutableInteractionSource() }
                val state = rememberElInteractionState(interaction)
                Box(
                    Modifier
                        .size(48.dp)
                        .testTag("press-target")
                        .clickable(
                            interactionSource = interaction,
                            indication = null,
                        ) {},
                ) {
                    BasicText(if (state.pressed) "Pressed" else "Idle")
                }
            }
        }

        mainClock.autoAdvance = false
        repeat(6) {
            onNodeWithTag("press-target").performClick()
            mainClock.advanceTimeBy(20)
        }
        mainClock.advanceTimeBy(150)
        onNodeWithText("Idle").assertExists()
    }
}
