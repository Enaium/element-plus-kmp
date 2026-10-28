package cn.enaium.elementplus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElAvatar
import cn.enaium.elementplus.components.ElAvatarGroup
import cn.enaium.elementplus.components.ElBadge
import cn.enaium.elementplus.components.ElCard
import cn.enaium.elementplus.components.ElCollapse
import cn.enaium.elementplus.components.ElCollapseItem
import cn.enaium.elementplus.components.ElDescriptionItem
import cn.enaium.elementplus.components.ElDescriptions
import cn.enaium.elementplus.components.ElProgress
import cn.enaium.elementplus.components.ElResult
import cn.enaium.elementplus.components.ElSegmented
import cn.enaium.elementplus.components.ElSegmentedOption
import cn.enaium.elementplus.components.ElSkeleton
import cn.enaium.elementplus.components.ElStatistic
import cn.enaium.elementplus.components.ElTable
import cn.enaium.elementplus.components.ElTableColumn
import cn.enaium.elementplus.components.ElTableSortOrder
import cn.enaium.elementplus.components.ElTag
import cn.enaium.elementplus.components.ElTimeline
import cn.enaium.elementplus.components.ElTimelineItem
import cn.enaium.elementplus.theme.ElConfigProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Behavioral ports of the upstream data component tests: avatar, badge, card,
 * collapse, descriptions, progress, result, skeleton, table, tag, timeline,
 * statistic and segmented.
 */
@OptIn(ExperimentalTestApi::class)
class DataComponentsParityTest {
    @Test
    fun avatarGroupCollapsesExtraItems() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElAvatarGroup(count = 4, max = 2, extra = "+2") { index ->
                    ElAvatar(text = "U$index")
                }
            }
        }

        onNodeWithText("U0").assertExists()
        onNodeWithText("U1").assertExists()
        onNodeWithText("U2").assertDoesNotExist()
        onNodeWithText("+2").assertExists()
    }

    @Test
    fun badgeFormatsValuesAboveMaxAndCanBeHidden() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                Column {
                    ElBadge(value = "120", max = 99) { BasicText("Inbox") }
                    ElBadge(value = "8", hidden = true) { BasicText("Hidden badge") }
                }
            }
        }

        onNodeWithText("99+").assertExists()
        onNodeWithText("8").assertDoesNotExist()
    }

    @Test
    fun cardRendersHeaderBodyAndFooter() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElCard(
                    header = { BasicText("Card header") },
                    footer = { BasicText("Card footer") },
                ) { BasicText("Card body") }
            }
        }

        onNodeWithText("Card header").assertExists()
        onNodeWithText("Card body").assertExists()
        onNodeWithText("Card footer").assertExists()
    }

    @Test
    fun collapseOpensAndClosesItsPanel() = runComposeUiTest {
        var active by mutableStateOf<List<String>>(emptyList())
        setContent {
            ElConfigProvider {
                ElCollapse(activeNames = active, onActiveChange = { active = it }) {
                    ElCollapseItem(name = "one", title = "Panel title") {
                        BasicText("Panel body")
                    }
                }
            }
        }

        onNodeWithText("Panel title").performClick()
        assertEquals(listOf("one"), active)
        onNodeWithText("Panel body").assertExists()
        onNodeWithText("Panel title").performClick()
        assertTrue(active.isEmpty())
    }

    @Test
    fun descriptionsRenderLabelsAndValues() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElDescriptions(
                    title = "Profile",
                    items = listOf(
                        ElDescriptionItem("Name") { BasicText("Alice") },
                        ElDescriptionItem("Role") { BasicText("Engineer") },
                    ),
                )
            }
        }

        onNodeWithText("Profile").assertExists()
        onNodeWithText("Name").assertExists()
        onNodeWithText("Alice").assertExists()
        onNodeWithText("Engineer").assertExists()
    }

    @Test
    fun progressUsesTheConfiguredFormatter() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElProgress(percentage = 40.0)
            }
        }
        onNodeWithText("40%").assertExists()
    }

    @Test
    fun resultShowsTitleSubtitleAndActions() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElResult(
                    title = "Success",
                    subTitle = "Everything is ready",
                    extra = { BasicText("Continue") },
                )
            }
        }

        onNodeWithText("Success").assertExists()
        onNodeWithText("Everything is ready").assertExists()
        onNodeWithText("Continue").assertExists()
    }

    @Test
    fun skeletonShowsContentOnlyWhenLoaded() = runComposeUiTest {
        var loading by mutableStateOf(true)
        setContent {
            ElConfigProvider {
                ElSkeleton(loading = loading) { BasicText("Loaded content") }
            }
        }

        onNodeWithText("Loaded content").assertDoesNotExist()
        runOnIdle { loading = false }
        onNodeWithText("Loaded content").assertExists()
    }

    @Test
    fun tagCloseCallsTheCloseHandler() = runComposeUiTest {
        var closed = false
        setContent {
            ElConfigProvider {
                ElTag("Release", closable = true, onClose = { closed = true })
            }
        }

        onNodeWithContentDescription("Close this tag").performClick()
        assertTrue(closed)
    }

    @Test
    fun timelineRendersContentAndTimestamps() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElTimeline(
                    items = listOf(
                        ElTimelineItem({ BasicText("Created project") }, timestamp = "09:00"),
                        ElTimelineItem({ BasicText("Shipped release") }, timestamp = "10:30"),
                    ),
                )
            }
        }

        onNodeWithText("Created project").assertExists()
        onNodeWithText("09:00").assertExists()
        onNodeWithText("Shipped release").assertExists()
    }

    @Test
    fun statisticFormatsNumbersAndTitle() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElStatistic(value = 1234.5, title = "Revenue", precision = 1)
            }
        }

        onNodeWithText("Revenue").assertExists()
        onNodeWithText("1,234.5").assertExists()
    }

    @Test
    fun segmentedChangesSelection() = runComposeUiTest {
        var value by mutableStateOf("medium")
        setContent {
            ElConfigProvider {
                ElSegmented(
                    options = listOf(
                        ElSegmentedOption("small", "Small"),
                        ElSegmentedOption("medium", "Medium"),
                        ElSegmentedOption("large", "Large"),
                    ),
                    value = value,
                    onValueChange = { value = it },
                )
            }
        }

        onNodeWithText("Large").performClick()
        assertEquals("large", value)
    }

    @Test
    fun tableSortsByAColumnHeader() = runComposeUiTest {
        data class Row(val id: Int, val name: String)
        var sort: Pair<String, Boolean?>? = null
        val rows = listOf(Row(1, "Beta"), Row(2, "Alpha"))
        setContent {
            ElConfigProvider {
                Box(Modifier.width(500.dp).height(300.dp)) {
                    ElTable(
                        data = rows,
                        columns = listOf(
                            ElTableColumn<Row>(
                                label = "Name",
                                key = "name",
                                property = { it.name },
                                sortable = true,
                            ),
                        ),
                        onSortChange = { column, ascending -> sort = column.label to ascending },
                    )
                }
            }
        }

        onNodeWithText("Name").performClick()
        assertEquals("Name" to true, sort)
    }
}
