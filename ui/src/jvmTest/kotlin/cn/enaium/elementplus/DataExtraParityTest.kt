package cn.enaium.elementplus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElCalendar
import cn.enaium.elementplus.components.ElCarousel
import cn.enaium.elementplus.components.ElDatePicker
import cn.enaium.elementplus.components.ElTour
import cn.enaium.elementplus.components.ElTourStep
import cn.enaium.elementplus.components.ElTableColumn
import cn.enaium.elementplus.components.ElTreeNode
import cn.enaium.elementplus.components.ElVirtualizedTable
import cn.enaium.elementplus.components.ElVirtualizedTree
import cn.enaium.elementplus.theme.ElConfigProvider
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Additional ports of upstream calendar/carousel/date-picker/tour and
 * virtualized table/tree tests.
 */
@OptIn(ExperimentalTestApi::class)
class DataExtraParityTest {
    @Test
    fun calendarSelectsAClickedDay() = runComposeUiTest {
        var value by mutableStateOf(LocalDate(2026, 5, 10))
        setContent {
            ElConfigProvider {
                ElCalendar(value = value, onValueChange = { value = it })
            }
        }

        onNodeWithText("15").performClick()
        assertEquals(LocalDate(2026, 5, 15), value)
    }

    @Test
    fun carouselRendersTheInitialSlide() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElCarousel(itemCount = 2, autoplay = false) { index ->
                    BasicText("Slide ${index + 1}")
                }
            }
        }

        onNodeWithText("Slide 1").assertExists()
    }

    @Test
    fun datePickerSelectsADayFromThePopup() = runComposeUiTest {
        var value by mutableStateOf<LocalDate?>(null)
        setContent {
            ElConfigProvider {
                ElDatePicker(
                    value = value,
                    onValueChange = { value = it },
                    defaultValue = LocalDate(2026, 5, 1),
                    placeholder = "Pick date",
                )
            }
        }

        onNodeWithText("Pick date").performClick()
        onNodeWithText("15").performClick()
        assertEquals(LocalDate(2026, 5, 15), value)
    }

    @Test
    fun virtualizedTableRendersRows() = runComposeUiTest {
        data class Row(val id: Int, val name: String)
        setContent {
            ElConfigProvider {
                Box(Modifier.width(500.dp).height(240.dp)) {
                    ElVirtualizedTable(
                        data = listOf(Row(1, "Alpha"), Row(2, "Beta")),
                        columns = listOf(ElTableColumn<Row>("Name", property = { it.name })),
                        height = 200.dp,
                    )
                }
            }
        }

        onNodeWithText("Alpha").assertExists()
        onNodeWithText("Beta").assertExists()
    }

    @Test
    fun virtualizedTreeRendersAndSelectsNodes() = runComposeUiTest {
        var selected by mutableStateOf<String?>(null)
        setContent {
            ElConfigProvider {
                ElVirtualizedTree(
                    data = listOf(ElTreeNode("parent", "Parent", listOf(ElTreeNode("child", "Child")))),
                    selectedKey = selected,
                    onSelectedChange = { selected = it },
                    defaultExpandAll = true,
                    height = 200.dp,
                    width = 300.dp,
                )
            }
        }

        onNodeWithText("Child").performClick()
        assertEquals("child", selected)
    }

    @Test
    fun tourNextButtonAdvancesTheStep() = runComposeUiTest {
        var current by mutableStateOf(0)
        var closed = false
        setContent {
            ElConfigProvider {
                ElTour(
                    visible = true,
                    steps = listOf(
                        ElTourStep("Welcome", "First step", targetBounds = Rect(10f, 10f, 100f, 40f), nextText = "Continue"),
                        ElTourStep("Finished", "Last step", targetBounds = Rect(10f, 60f, 100f, 90f)),
                    ),
                    current = current,
                    onCurrentChange = { current = it },
                    onClose = { closed = true },
                )
            }
        }

        onNodeWithText("Welcome").assertExists()
        onNodeWithText("Continue").performClick()
        assertEquals(1, current)
        onNodeWithText("Finished").assertExists()
        assertTrue(!closed)
    }
}
