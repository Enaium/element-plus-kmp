package cn.enaium.elementplus

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runComposeUiTest
import cn.enaium.elementplus.components.ElInputOtp
import cn.enaium.elementplus.theme.ElConfigProvider
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class InputOtpKeyTest {
    @Test
    fun backspaceClearsTheCurrentCellAndFocusesThePreviousOne() = runComposeUiTest {
        var value by mutableStateOf("123456")
        setContent {
            ElConfigProvider {
                ElInputOtp(value = value, onValueChange = { value = it })
            }
        }

        onNodeWithContentDescription("Please enter OTP character 2").performClick()
        onNodeWithContentDescription("Please enter OTP character 2").performKeyInput {
            pressKey(Key.Backspace)
        }

        assertEquals("13456", value)
        onNodeWithContentDescription("Please enter OTP character 1").assertIsFocused()
    }

    @Test
    fun deleteClearsTheCurrentCellAndKeepsFocus() = runComposeUiTest {
        var value by mutableStateOf("123456")
        setContent {
            ElConfigProvider {
                ElInputOtp(value = value, onValueChange = { value = it })
            }
        }

        onNodeWithContentDescription("Please enter OTP character 2").performClick()
        onNodeWithContentDescription("Please enter OTP character 2").performKeyInput {
            pressKey(Key.Delete)
        }

        assertEquals("13456", value)
        onNodeWithContentDescription("Please enter OTP character 2").assertIsFocused()
    }
}
