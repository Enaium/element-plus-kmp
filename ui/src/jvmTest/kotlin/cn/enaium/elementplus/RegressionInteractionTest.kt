package cn.enaium.elementplus

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElMention
import cn.enaium.elementplus.components.ElMentionOption
import cn.enaium.elementplus.components.ElPopconfirm
import cn.enaium.elementplus.components.ElPopover
import cn.enaium.elementplus.components.ElPopoverTrigger
import cn.enaium.elementplus.components.ElSwitch
import cn.enaium.elementplus.theme.ElConfigProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class RegressionInteractionTest {
    @Test
    fun popoverClickTriggerWorksWhenContentIsClickable() = runComposeUiTest {
        var visible by mutableStateOf(false)
        setContent {
            ElConfigProvider {
                ElPopover(
                    trigger = ElPopoverTrigger.Click,
                    visible = visible,
                    onVisibleChange = { visible = it },
                    triggerContent = { ElButton(onClick = {}, text = "Open popover") },
                ) {
                    BasicText("Popover body")
                }
            }
        }

        onNodeWithText("Open popover").performClick()
        assertTrue(visible)
        onNodeWithText("Popover body").assertExists()
    }

    @Test
    fun popconfirmClickTriggerWorksWhenContentIsClickable() = runComposeUiTest {
        var visible by mutableStateOf(false)
        setContent {
            ElConfigProvider {
                ElPopconfirm(
                    title = "Delete?",
                    visible = visible,
                    onVisibleChange = { visible = it },
                    onConfirm = {},
                    triggerContent = { ElButton(onClick = {}, text = "Delete item") },
                )
            }
        }

        onNodeWithText("Delete item").performClick()
        assertTrue(visible)
        onNodeWithText("Delete?").assertExists()
    }

    @Test
    fun mentionClosesAfterOneTrailingSpaceAndKeepsCursorAtEnd() = runComposeUiTest {
        var value by mutableStateOf("")
        val options = listOf(ElMentionOption(value = "John"))
        setContent {
            ElConfigProvider {
                ElMention(
                    value = value,
                    onValueChange = { value = it },
                    options = options,
                )
            }
        }

        onNode(hasSetTextAction()).performTextInput("@jo")
        onNodeWithText("John").performClick()

        assertEquals("@John ", value)
        onNodeWithText("John").assertDoesNotExist()

        onNode(hasSetTextAction()).performTextInput("x")
        assertEquals("@John x", value)
    }

    @Test
    fun switchShowsBothSideLabelsInEveryState() = runComposeUiTest {
        var checked by mutableStateOf(false)
        setContent {
            ElConfigProvider {
                ElSwitch(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    activeText = "On",
                    inactiveText = "Off",
                )
            }
        }

        onNodeWithText("On").assertExists()
        onNodeWithText("Off").assertExists()

        onNodeWithText("On").performClick()
        assertTrue(checked)
        onNodeWithText("On").assertExists()
        onNodeWithText("Off").assertExists()
    }
}
