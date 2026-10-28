package cn.enaium.elementplus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElAutocomplete
import cn.enaium.elementplus.components.ElCascader
import cn.enaium.elementplus.components.ElCascaderOption
import cn.enaium.elementplus.components.ElForm
import cn.enaium.elementplus.components.ElFormItem
import cn.enaium.elementplus.components.ElFormState
import cn.enaium.elementplus.components.ElInputOtp
import cn.enaium.elementplus.components.ElInputTag
import cn.enaium.elementplus.components.ElRadioGroup
import cn.enaium.elementplus.components.ElRadioOption
import cn.enaium.elementplus.components.ElRate
import cn.enaium.elementplus.components.ElSwitch
import cn.enaium.elementplus.components.ElTimeSelect
import cn.enaium.elementplus.components.ElTransfer
import cn.enaium.elementplus.components.ElTransferItem
import cn.enaium.elementplus.components.ElTreeSelect
import cn.enaium.elementplus.components.ElTreeNode
import cn.enaium.elementplus.components.ElUpload
import cn.enaium.elementplus.components.rememberElFormState
import cn.enaium.elementplus.theme.ElConfigProvider
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Behavioral ports of the upstream form component tests: radio, switch, rate,
 * input-tag, input-otp, autocomplete, cascader, transfer, tree-select, upload,
 * time-select and form validation.
 */
@OptIn(ExperimentalTestApi::class)
class FormComponentsParityTest {
    @Test
    fun radioGroupSelectsAnOption() = runComposeUiTest {
        var selected by mutableStateOf<String?>(null)
        setContent {
            ElConfigProvider {
                ElRadioGroup(
                    options = listOf(ElRadioOption("a", "Option A"), ElRadioOption("b", "Option B")),
                    selected = selected,
                    onSelectedChange = { selected = it },
                )
            }
        }

        onNodeWithText("Option B").performClick()
        assertEquals("b", selected)
    }

    @Test
    fun switchTogglesAndHonorsDisabledState() = runComposeUiTest {
        var checked by mutableStateOf(false)
        setContent {
            ElConfigProvider {
                androidx.compose.foundation.layout.Column {
                    ElSwitch(checked = checked, onCheckedChange = { checked = it }, activeText = "On", inactiveText = "Off")
                    ElSwitch(checked = false, onCheckedChange = {}, activeText = "Locked", disabled = true)
                }
            }
        }

        onNodeWithText("On").performClick()
        assertTrue(checked)
        onNodeWithText("Locked").performClick()
        assertTrue(checked)
    }

    @Test
    fun rateClickSelectsTheClickedStar() = runComposeUiTest {
        var value by mutableStateOf(0.0)
        setContent {
            ElConfigProvider {
                ElRate(value = value, onValueChange = { value = it }, modifier = Modifier.testTag("rate"))
            }
        }

        onNodeWithTag("rate").performTouchInput { click(center) }
        assertEquals(3.0, value)
    }

    @Test
    fun inputTagCreatesTagsFromADelimiter() = runComposeUiTest {
        var values by mutableStateOf(emptyList<String>())
        setContent {
            ElConfigProvider {
                ElInputTag(
                    values = values,
                    onValueChange = { values = it },
                    delimiter = ",",
                    placeholder = "Add tag",
                )
            }
        }

        onNode(hasSetTextAction()).performTextInput("Kotlin,")
        assertEquals(listOf("Kotlin"), values)
        onNodeWithText("Kotlin").assertExists()
    }

    @Test
    fun inputOtpAcceptsDigits() = runComposeUiTest {
        var value by mutableStateOf("")
        setContent {
            ElConfigProvider {
                ElInputOtp(value = value, onValueChange = { value = it }, length = 6)
            }
        }

        onAllNodes(hasSetTextAction())[0].performTextInput("1")
        assertEquals("1", value)
    }

    @Test
    fun autocompleteSelectsAFetchedSuggestion() = runComposeUiTest {
        var value by mutableStateOf("")
        setContent {
            ElConfigProvider {
                ElAutocomplete(
                    value = value,
                    onValueChange = { value = it },
                    fetchSuggestions = { query -> if (query.startsWith("Ap")) listOf("Apple", "Apricot") else emptyList() },
                    placeholder = "Search fruit",
                    debounce = 0,
                )
            }
        }

        onNode(hasSetTextAction()).performTextInput("Ap")
        waitForIdle()
        onNodeWithText("Apple").performClick()
        assertEquals("Apple", value)
    }

    @Test
    fun cascaderSelectsAPath() = runComposeUiTest {
        var value by mutableStateOf(emptyList<String>())
        val options = listOf(
            ElCascaderOption(
                value = "zj",
                label = "Zhejiang",
                children = listOf(ElCascaderOption("hz", "Hangzhou")),
            ),
        )
        setContent {
            ElConfigProvider {
                ElCascader(
                    value = value,
                    onValueChange = { value = it },
                    options = options,
                    placeholder = "Select region",
                )
            }
        }

        onNodeWithText("Select region").performClick()
        onNodeWithText("Zhejiang").performClick()
        onNodeWithText("Hangzhou").performClick()
        assertEquals(listOf("zj", "hz"), value)
    }

    @Test
    fun transferMovesCheckedItemsToTheRight() = runComposeUiTest {
        var value by mutableStateOf(emptyList<String>())
        setContent {
            ElConfigProvider {
                ElTransfer(
                    data = listOf(ElTransferItem("a", "Alpha"), ElTransferItem("b", "Beta")),
                    value = value,
                    onValueChange = { value = it },
                    leftDefaultChecked = listOf("a"),
                    buttonTexts = listOf("Move right", "Move left"),
                )
            }
        }

        onNodeWithText("Move right").performClick()
        assertEquals(listOf("a"), value)
    }

    @Test
    fun treeSelectSelectsANode() = runComposeUiTest {
        var selected by mutableStateOf<String?>(null)
        val data = listOf(ElTreeNode("parent", "Parent", listOf(ElTreeNode("child", "Child"))))
        setContent {
            ElConfigProvider {
                ElTreeSelect(
                    selectedKey = selected,
                    onSelectedChange = { selected = it },
                    data = data,
                    defaultExpandAll = true,
                    placeholder = "Select node",
                )
            }
        }

        onNodeWithText("Select node").performClick()
        onNodeWithText("Child").performClick()
        assertEquals("child", selected)
    }

    @Test
    fun uploadDelegatesFilePickingToTheHost() = runComposeUiTest {
        var picks = 0
        setContent {
            ElConfigProvider {
                ElUpload(
                    files = emptyList(),
                    onFilesChange = {},
                    onPickFiles = { picks++ },
                    buttonText = "Choose file",
                )
            }
        }

        onNodeWithText("Choose file").performClick()
        assertEquals(1, picks)
    }

    @Test
    fun timeSelectPicksAnOption() = runComposeUiTest {
        var value by mutableStateOf<LocalTime?>(null)
        setContent {
            ElConfigProvider {
                ElTimeSelect(
                    value = value,
                    onValueChange = { value = it },
                    start = LocalTime(9, 0),
                    end = LocalTime(10, 0),
                    step = 30,
                    placeholder = "Pick time",
                )
            }
        }

        onNodeWithText("Pick time").performClick()
        onNodeWithText("09:30").performClick()
        assertEquals(LocalTime(9, 30), value)
    }

    @Test
    fun formRejectsMissingRequiredValues() = runComposeUiTest {
        lateinit var form: ElFormState
        setContent {
            ElConfigProvider {
                form = rememberElFormState()
                ElForm(state = form) {
                    ElFormItem(label = "Name", value = "", required = true) {
                        BasicText("Name field")
                    }
                }
            }
        }

        runOnIdle { assertFalse(form.validate()) }
        assertFalse(form.isValid)
    }
}
