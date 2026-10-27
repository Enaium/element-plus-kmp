package cn.enaium.elementplus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElContainer
import cn.enaium.elementplus.components.ElHeader
import cn.enaium.elementplus.components.ElDateTimePicker
import cn.enaium.elementplus.components.ElDateRangePicker
import cn.enaium.elementplus.components.ElDateShortcut
import kotlinx.datetime.LocalDate
import cn.enaium.elementplus.components.ElMain
import cn.enaium.elementplus.theme.ElConfigProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ContainerLayoutTest {
    @Test
    fun mainFillsTheContainerWidth() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                Box(Modifier.width(400.dp).height(200.dp)) {
                    ElContainer(
                        header = {
                            ElHeader(Modifier.padding(horizontal = 12.dp)) {
                                BasicText("Header")
                            }
                        },
                    ) {
                        ElMain(Modifier.testTag("container-main")) {
                            BasicText("Main")
                        }
                    }
                }
            }
        }

        val bounds = onNodeWithTag("container-main").getUnclippedBoundsInRoot()
        assertEquals(400.dp, bounds.right - bounds.left)
    }
}

@OptIn(ExperimentalTestApi::class)
class DateTimePickerPopupTest {
    @Test
    fun popupKeepsItsPanelWidth() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                Box(Modifier.width(1000.dp).height(800.dp)) {
                    ElDateTimePicker(
                        value = null,
                        onValueChange = {},
                        placeholder = "Pick date time",
                    )
                }
            }
        }

        onNodeWithText("Pick date time").performClick()
        val now = onNodeWithText("Now").getUnclippedBoundsInRoot()
        assertTrue(now.right < 500.dp)
    }
}

@OptIn(ExperimentalTestApi::class)
class DateRangeQuickOptionsTest {
    @Test
    fun quickOptionsKeepBothCalendarsVisible() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                Box(Modifier.width(1000.dp).height(800.dp)) {
                    ElDateRangePicker(
                        start = LocalDate(2026, 10, 1),
                        end = LocalDate(2026, 11, 15),
                        onValueChange = { _, _ -> },
                        startPlaceholder = "Start date",
                        endPlaceholder = "End date",
                        shortcuts = listOf(
                            ElDateShortcut("Last week", start = LocalDate(2026, 10, 1), end = LocalDate(2026, 10, 8)),
                        ),
                    )
                }
            }
        }

        onNodeWithText("2026-10-01").performClick()
        onNodeWithText("Last week").assertExists()
        val left = onNodeWithText("October 2026").getUnclippedBoundsInRoot()
        val right = onNodeWithText("November 2026").getUnclippedBoundsInRoot()
        assertTrue(right.left > left.left)
        assertTrue(right.left < 900.dp)
    }
}
