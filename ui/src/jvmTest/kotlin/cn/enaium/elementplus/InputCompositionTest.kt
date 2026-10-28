package cn.enaium.elementplus

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.theme.ElConfigProvider
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class InputCompositionTest {
    @Test
    fun cjkTextKeepsItsValueAndRemovesThePlaceholder() = runComposeUiTest {
        var value by mutableStateOf("")
        setContent {
            ElConfigProvider {
                ElInput(
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Type here",
                )
            }
        }

        onNodeWithText("Type here").assertExists()
        onNode(hasSetTextAction()).performTextInput("a中")
        assertEquals("a中", value)
        onNodeWithText("Type here").assertDoesNotExist()
    }

    @Test
    fun textareaCjkTextKeepsItsValueAndRemovesThePlaceholder() = runComposeUiTest {
        var value by mutableStateOf("")
        setContent {
            ElConfigProvider {
                ElInput(
                    value = value,
                    onValueChange = { value = it },
                    placeholder = "Type a note",
                    type = cn.enaium.elementplus.components.ElInputType.Textarea,
                    minLines = 2,
                    maxLines = 4,
                )
            }
        }

        onNode(hasSetTextAction()).performTextInput("中文输入")
        assertEquals("中文输入", value)
        onNodeWithText("Type a note").assertDoesNotExist()
    }
}
