package cn.enaium.elementplus.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElCheckbox
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElSelect
import cn.enaium.elementplus.components.ElSelectMultiple
import cn.enaium.elementplus.components.ElSelectOption
import cn.enaium.elementplus.components.ElVirtualizedSelect
import cn.enaium.elementplus.theme.ElConfigProvider
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class SelectInteractionTest {

    @Test
    fun remoteSearchLoadsAndSelectsOptions() = runComposeUiTest {
        var value by mutableStateOf<String?>(null)
        var options by mutableStateOf(emptyList<ElSelectOption<String>>())
        setContent {
            ElConfigProvider {
                Column {
                    ElSelect(
                        value = value,
                        onValueChange = { value = it },
                        options = options,
                        filterable = true,
                        remote = true,
                        remoteMethod = { query ->
                            options = listOf("Alabama", "Alaska", "Arizona")
                                .filter { it.contains(query, ignoreCase = true) }
                                .map { ElSelectOption("value:$it", "label:$it") }
                        },
                        placeholder = "Pick",
                        modifier = Modifier.width(240.dp),
                    )
                    BasicText("picked-${value ?: "-"}")
                }
            }
        }
        onNodeWithText("Pick").performClick()
        onNode(hasSetTextAction()).performTextInput("Ala")
        onNodeWithText("label:Alabama").assertExists()
        onNodeWithText("label:Alabama").performClick()
        onNodeWithText("picked-value:Alabama").assertExists()
    }

    @Test
    fun allowCreateCreatesAndSelectsANewValue() = runComposeUiTest {
        var values by mutableStateOf(emptyList<String>())
        setContent {
            ElConfigProvider {
                Column {
                    ElSelectMultiple(
                        values = values,
                        onValueChange = { values = it },
                        options = listOf(ElSelectOption("HTML", "HTML"), ElSelectOption("CSS", "CSS")),
                        filterable = true,
                        allowCreate = true,
                        onCreateOption = { ElSelectOption(it, it) },
                        placeholder = "Pick",
                        modifier = Modifier.width(260.dp),
                    )
                    BasicText("created-${values.joinToString()}")
                }
            }
        }
        onNodeWithText("Pick").performClick()
        onNode(hasSetTextAction()).performTextInput("Vue")
        onNodeWithText("Create \"Vue\"").performClick()
        onNodeWithText("created-Vue").assertExists()
    }

    @Test
    fun valueKeyComparesObjectValuesById() = runComposeUiTest {
        data class City(val id: Int, val label: String, val description: String)
        val a = City(1, "Option A", "first A")
        val b = City(2, "Option B", "second B")
        var value by mutableStateOf<City?>(a)
        setContent {
            ElConfigProvider {
                Column {
                    ElSelect(
                        value = value,
                        onValueChange = { value = it },
                        options = listOf(ElSelectOption(a, a.label), ElSelectOption(b, b.label)),
                        valueKey = { it.id },
                        placeholder = "Pick",
                        modifier = Modifier.width(240.dp),
                    )
                    BasicText("description-${value?.description ?: "-"}")
                }
            }
        }
        onNodeWithText("Option A").performClick()
        onNodeWithText("Option B").performClick()
        onNodeWithText("description-second B").assertExists()
    }

    @Test
    fun customLabelContentIsRendered() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElSelect(
                    value = "Option1",
                    onValueChange = {},
                    options = listOf(ElSelectOption("Option1", "Label1")),
                    labelContent = { label, value, _ -> BasicText("$label: $value") },
                    modifier = Modifier.width(240.dp),
                )
            }
        }
        onNodeWithText("Label1: Option1").assertExists()
    }

    @Test
    fun groupedVirtualizedOptionsRenderGroupHeaders() = runComposeUiTest {
        var value by mutableStateOf<String?>(null)
        val options = listOf(
            ElSelectOption("a", "A", group = "Group 1"),
            ElSelectOption("b", "B", group = "Group 1"),
            ElSelectOption("c", "C", group = "Group 2"),
        )
        setContent {
            ElConfigProvider {
                Column {
                    ElVirtualizedSelect(
                        value = value,
                        onValueChange = { value = it },
                        options = options,
                        placeholder = "Pick",
                        modifier = Modifier.width(240.dp),
                    )
                    BasicText("grouped-${value ?: "-"}")
                }
            }
        }
        onNodeWithText("Pick").performClick()
        onNodeWithText("Group 1").assertExists()
        onNodeWithText("Group 2").assertExists()
        onNodeWithText("B").performClick()
        onNodeWithText("grouped-b").assertExists()
    }

    @Test
    fun headerCanSelectAllOptions() = runComposeUiTest {
        val options = (1..5).map { ElSelectOption("$it", "Option $it") }
        var values by mutableStateOf(emptyList<String>())
        setContent {
            ElConfigProvider {
                Column {
                    ElSelectMultiple(
                        values = values,
                        onValueChange = { values = it },
                        options = options,
                        collapseTags = true,
                        maxCollapseTags = 1,
                        header = {
                            ElCheckbox(
                                checked = values.size == options.size,
                                indeterminate = values.isNotEmpty() && values.size != options.size,
                                onCheckedChange = { checked ->
                                    values = if (checked) options.map { it.value } else emptyList()
                                },
                                label = "All",
                            )
                        },
                        placeholder = "Pick",
                        modifier = Modifier.width(280.dp),
                    )
                    BasicText("count-${values.size}")
                }
            }
        }
        onNodeWithText("Pick").performClick()
        onNodeWithText("All").performClick()
        onNodeWithText("count-5").assertExists()
        onNodeWithText("+4").assertExists()
    }

    @Test
    fun footerCanAddAnOption() = runComposeUiTest {
        var options by mutableStateOf((1..3).map { ElSelectOption("$it", "Option $it") })
        var value by mutableStateOf<String?>(null)
        var adding by mutableStateOf(false)
        var optionName by mutableStateOf("")
        setContent {
            ElConfigProvider {
                Column {
                    ElSelect(
                        value = value,
                        onValueChange = { value = it },
                        options = options,
                        footer = {
                            if (!adding) {
                                ElButton(text = "Add an option", onClick = { adding = true }, size = ElSize.Small)
                            } else {
                                Column {
                                    ElInput(value = optionName, onValueChange = { optionName = it }, placeholder = "option name")
                                    ElButton(
                                        text = "confirm",
                                        type = ElType.Primary,
                                        size = ElSize.Small,
                                        onClick = {
                                            options = options + ElSelectOption(optionName, optionName)
                                            adding = false
                                        },
                                    )
                                }
                            }
                        },
                        placeholder = "Pick",
                        modifier = Modifier.width(280.dp),
                    )
                    BasicText("count-${options.size}")
                }
            }
        }
        onNodeWithText("Pick").performClick()
        onNodeWithText("Add an option").performClick()
        onNode(hasSetTextAction()).performTextInput("Custom")
        onNodeWithText("confirm").performClick()
        onNodeWithText("count-4").assertExists()
        onNodeWithText("Custom").assertExists()
        assertEquals(4, options.size)
    }

    @Test
    fun customLoadingAndEmptyValuesAreSupported() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                Column {
                    ElSelect(
                        value = null,
                        onValueChange = {},
                        options = emptyList(),
                        loading = true,
                        loadingContent = { BasicText("custom-loading") },
                        placeholder = "Pick",
                        modifier = Modifier.width(240.dp),
                    )
                    ElSelect(
                        value = "",
                        onValueChange = {},
                        options = listOf(ElSelectOption("", "All")),
                        emptyValues = listOf(null),
                        placeholder = "Empty value",
                        modifier = Modifier.width(240.dp),
                    )
                }
            }
        }
        onNodeWithText("Pick").performClick()
        onNodeWithText("custom-loading").assertExists()
        onNodeWithText("All").assertExists()
    }
}
