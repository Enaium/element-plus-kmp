package cn.enaium.elementplus.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElTree
import cn.enaium.elementplus.components.ElTreeNode
import cn.enaium.elementplus.components.ElVirtualizedTree
import cn.enaium.elementplus.theme.ElConfigProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class TreeInteractionTest {

    private data class Node(val id: String, val label: String, val children: List<Node> = emptyList())

    @Test
    fun lazyTreeLoadsRootAndCustomLeaf() = runComposeUiTest {
        var rootLoads = 0
        setContent {
            ElConfigProvider {
                ElTree(
                    data = emptyList(),
                    lazy = true,
                    load = { node, resolve, _ ->
                        if (node == null) {
                            rootLoads++
                            resolve(listOf(ElTreeNode("root", "Root")))
                        } else {
                            resolve(listOf(ElTreeNode("leaf", "Leaf", leaf = true)))
                        }
                    },
                )
            }
        }
        waitForIdle()
        onNodeWithText("Root").assertExists()
        assertEquals(1, rootLoads)
        onNodeWithContentDescription("Expand node").performClick()
        onNodeWithText("Leaf").assertExists()
        onAllNodesWithContentDescription("Expand node").assertCountEquals(0)
    }

    @Test
    fun lazyTreeRetriesAfterReject() = runComposeUiTest {
        var attempts = 0
        setContent {
            ElConfigProvider {
                ElTree(
                    data = emptyList(),
                    lazy = true,
                    load = { node, resolve, reject ->
                        if (node == null) {
                            resolve(listOf(ElTreeNode("root", "Root")))
                        } else {
                            attempts++
                            if (attempts == 1) reject()
                            else resolve(listOf(ElTreeNode("leaf", "Leaf")))
                        }
                    },
                )
            }
        }
        waitForIdle()
        onNodeWithContentDescription("Expand node").performClick()
        waitForIdle()
        onNodeWithText("Leaf").assertDoesNotExist()
        assertEquals(1, attempts)
        onNodeWithContentDescription("Retry loading node").performClick()
        waitForIdle()
        onNodeWithText("Leaf").assertExists()
        assertEquals(2, attempts)
    }

    @Test
    fun disabledCheckboxIsNotSelectedByParentCascade() = runComposeUiTest {
        val tree = listOf(
            ElTreeNode(
                "root", "Root", listOf(
                    ElTreeNode("enabled", "Enabled"),
                    ElTreeNode("disabled", "Disabled", disabled = true),
                ),
            ),
        )
        var checked by mutableStateOf(emptySet<String>())
        setContent {
            ElConfigProvider {
                Column {
                    ElTree(
                        data = tree,
                        checkedKeys = checked,
                        onCheckedChange = { checked = it },
                        showCheckbox = true,
                        defaultExpandAll = true,
                    )
                    BasicText("checked-${checked.sorted().joinToString()}")
                }
            }
        }
        onNodeWithContentDescription("Select Root").performClick()
        onNodeWithText("checked-enabled, root").assertExists()
        onNodeWithContentDescription("Select Disabled").assertIsNotEnabled()
    }

    @Test
    fun virtualizedTreeFiltersAndShowsDefaultChecked() = runComposeUiTest {
        val tree = listOf(
            ElTreeNode(
                "root", "Root", listOf(
                    ElTreeNode("one", "One"),
                    ElTreeNode("two", "Two"),
                ),
            ),
            ElTreeNode("other", "Other"),
        )
        setContent {
            ElConfigProvider {
                Column {
                    ElVirtualizedTree(
                        data = tree,
                        showCheckbox = true,
                        defaultExpandedKeys = setOf("root"),
                        defaultCheckedKeys = setOf("two"),
                        filterText = "two",
                        filterNodeMethod = { query, node -> node.label.contains(query, ignoreCase = true) },
                        height = 200.dp,
                        modifier = Modifier.width(260.dp),
                    )
                }
            }
        }
        onNodeWithText("Two").assertExists()
        onNodeWithText("One").assertDoesNotExist()
        onNodeWithText("Other").assertDoesNotExist()
        onNodeWithContentDescription("Select Two").assertIsOn()
    }

    @Test
    fun draggableTreeReordersRoots() = runComposeUiTest {
        var tree by mutableStateOf(listOf(ElTreeNode("a", "A"), ElTreeNode("b", "B")))
        setContent {
            ElConfigProvider {
                Column {
                    ElTree(
                        data = tree,
                        onDataChange = { tree = it },
                        draggable = true,
                    )
                    BasicText("order-${tree.joinToString { it.label }}")
                }
            }
        }
        onNodeWithText("A").performTouchInput {
            down(center)
            moveBy(Offset(0f, 45f))
            up()
        }
        waitForIdle()
        onNodeWithText("order-B, A").assertExists()
    }

    @Test
    fun accordionCollapsesSiblingBranches() = runComposeUiTest {
        val tree = listOf(
            ElTreeNode(
                "root", "Root", listOf(
                    ElTreeNode("a", "A", listOf(ElTreeNode("a1", "A1"))),
                    ElTreeNode("b", "B", listOf(ElTreeNode("b1", "B1"))),
                ),
            ),
        )
        setContent {
            ElConfigProvider {
                ElTree(
                    data = tree,
                    accordion = true,
                    defaultExpandedKeys = setOf("root"),
                )
            }
        }
        onAllNodesWithContentDescription("Expand node").onFirst().performClick()
        onNodeWithText("A1").assertExists()
        onNodeWithContentDescription("Expand node").performClick()
        onNodeWithText("B1").assertExists()
        onNodeWithText("A1").assertDoesNotExist()
    }
}
