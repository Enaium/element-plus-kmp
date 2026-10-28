package cn.enaium.elementplus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElAffix
import cn.enaium.elementplus.components.ElAnchor
import cn.enaium.elementplus.components.ElAnchorLink
import cn.enaium.elementplus.components.ElBacktop
import cn.enaium.elementplus.components.ElDivider
import cn.enaium.elementplus.components.ElLink
import cn.enaium.elementplus.components.ElRow
import cn.enaium.elementplus.components.ElRowCol
import cn.enaium.elementplus.components.ElScrollbar
import cn.enaium.elementplus.components.ElScrollbarState
import cn.enaium.elementplus.components.ElSpace
import cn.enaium.elementplus.components.ElSplitter
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.ElTypography
import cn.enaium.elementplus.components.ElWatermark
import cn.enaium.elementplus.components.rememberElScrollbarState
import cn.enaium.elementplus.theme.ElConfigProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Behavioral ports of the upstream tests for the basic/layout components:
 * link, text, space, splitter, scrollbar, affix, anchor, backtop, divider,
 * watermark and layout.
 */
@OptIn(ExperimentalTestApi::class)
class BasicComponentsParityTest {
    @Test
    fun linkEmitsClickOnlyWhenEnabled() = runComposeUiTest {
        var clicks = 0
        setContent {
            ElConfigProvider {
                Column {
                    ElLink("Read more", onClick = { clicks++ })
                    ElLink("Disabled link", disabled = true, onClick = { clicks++ })
                }
            }
        }

        onNodeWithText("Read more").performClick()
        assertEquals(1, clicks)
        onNodeWithText("Disabled link").performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun textAndTypographyRenderContentAndClicks() = runComposeUiTest {
        var clicks = 0
        setContent {
            ElConfigProvider {
                Column {
                    ElText("Plain text")
                    ElText("Clickable text", onClick = { clicks++ })
                    ElTypography("Heading", level = 2)
                    ElText(content = { _ -> BasicText("Inline") })
                }
            }
        }

        onNodeWithText("Clickable text").performClick()
        assertEquals(1, clicks)
        onNodeWithText("Plain text").assertExists()
        onNodeWithText("Heading").assertExists()
        onNodeWithText("Inline").assertExists()
    }

    @Test
    fun spaceInsertsASpacerBetweenItemsOnly() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElSpace(spacer = { BasicText("|") }) {
                    BasicText("First")
                    BasicText("Second")
                    BasicText("Third")
                }
            }
        }

        onAllNodesWithText("|").assertCountEquals(2)
        onNodeWithText("First").assertExists()
        onNodeWithText("Third").assertExists()
    }

    @Test
    fun splitterRendersEveryPanel() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                Box(Modifier.width(400.dp).height(200.dp)) {
                    ElSplitter(panelCount = 3, panelContent = { index ->
                        BasicText("Panel ${index + 1}")
                    })
                }
            }
        }

        onNodeWithText("Panel 1").assertExists()
        onNodeWithText("Panel 2").assertExists()
        onNodeWithText("Panel 3").assertExists()
    }

    @Test
    fun scrollbarStateScrollsItsViewport() = runComposeUiTest {
        lateinit var state: ElScrollbarState
        setContent {
            ElConfigProvider {
                state = rememberElScrollbarState()
                ElScrollbar(height = 80.dp, state = state) {
                    Column {
                        repeat(20) { BasicText("Scroll row $it", Modifier.height(20.dp)) }
                    }
                }
            }
        }

        runOnIdle { state.setScrollTop(120f) }
        waitForIdle()
        assertTrue(state.scrollTop > 0f, "scrollTop was ${state.scrollTop}")
    }

    @Test
    fun affixRendersSlottedContentWithoutScrollState() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElAffix { affixed ->
                    BasicText("Affix content ${if (affixed) "fixed" else "normal"}")
                }
            }
        }
        onNodeWithText("Affix content normal").assertExists()
    }

    @Test
    fun anchorInvokesLinkClick() = runComposeUiTest {
        var clicked: String? = null
        setContent {
            ElConfigProvider {
                ElAnchor(
                    links = listOf(ElAnchorLink("#intro", "Introduction")),
                    activeHref = "#intro",
                    onLinkClick = { clicked = it },
                )
            }
        }

        onNodeWithText("Introduction").performClick()
        assertEquals("#intro", clicked)
    }

    @Test
    fun backtopCanBeForcedVisibleAndClickable() = runComposeUiTest {
        var clicks = 0
        setContent {
            ElConfigProvider {
                Box(Modifier.width(300.dp).height(200.dp)) {
                    ElBacktop(visible = true, right = 0.dp, bottom = 0.dp, onClick = { clicks++ }) { BasicText("Top") }
                }
            }
        }

        onNodeWithText("Top").performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun dividerAndWatermarkKeepTheirContent() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                Box(Modifier.width(300.dp).height(200.dp)) {
                    ElWatermark(text = "Draft") {
                        Column(Modifier.fillMaxSize().background(Color.White)) {
                            ElDivider { BasicText("Section") }
                            BasicText("Watermarked content")
                        }
                    }
                }
            }
        }

        onNodeWithText("Section").assertExists()
        onNodeWithText("Watermarked content").assertExists()
    }

    @Test
    fun rowColumnsShareTheAvailableWidth() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElRow {
                    ElRowCol(span = 12) { BasicText("Half A") }
                    ElRowCol(span = 12) { BasicText("Half B") }
                }
            }
        }

        val a = onNodeWithText("Half A").getUnclippedBoundsInRoot()
        val b = onNodeWithText("Half B").getUnclippedBoundsInRoot()
        assertTrue(b.left > a.left)
        assertTrue(a.right <= b.left + 1.dp)
    }
}
