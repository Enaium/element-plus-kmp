package cn.enaium.elementplus.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElTable
import cn.enaium.elementplus.components.ElTableColumn
import cn.enaium.elementplus.components.ElTableFilter
import cn.enaium.elementplus.components.ElTableSortOrder
import cn.enaium.elementplus.components.ElTableState
import cn.enaium.elementplus.components.ElVirtualizedTable
import cn.enaium.elementplus.theme.ElConfigProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.launch

@OptIn(ExperimentalTestApi::class)
class TableInteractionTest {

    private data class Row(val name: String, val age: Int)
    private data class Order(val id: Int, val date: String)

    @Test
    fun sortHeaderCyclesAscendingAndDescending() = runComposeUiTest {
        var changes = mutableListOf<Boolean?>()
        setContent {
            ElConfigProvider {
                ElTable(
                    data = listOf(Row("Alice", 28), Row("Bob", 34)),
                    columns = listOf(
                        ElTableColumn("Name", property = { it.name }),
                        ElTableColumn("Age", key = "age", property = { it.age }, sortable = true),
                    ),
                    onSortChange = { _, ascending -> changes += ascending },
                )
            }
        }
        onNodeWithContentDescription("Sort by Age").performClick()
        assertEquals(listOf<Boolean?>(true), changes)
        onNodeWithContentDescription("Sort by Age").performClick()
        assertEquals(listOf<Boolean?>(true, false), changes)
        onNodeWithContentDescription("Sort by Age").performClick()
        assertEquals(listOf<Boolean?>(true, false, null), changes)
    }

    @Test
    fun selectionHeaderAndRowsAreInteractive() = runComposeUiTest {
        var selected by mutableStateOf(setOf<Any>())
        setContent {
            ElConfigProvider {
                Column {
                    ElTable(
                        data = listOf(Row("Alice", 28), Row("Bob", 34)),
                        columns = listOf(
                            ElTableColumn("Name", property = { it.name }),
                            ElTableColumn("Age", property = { it.age }),
                        ),
                        rowKey = { it.name },
                        selection = true,
                        selectedKeys = selected,
                        onSelectionChange = { selected = it },
                    )
                    BasicText("selected-${selected.size}")
                }
            }
        }
        onNodeWithContentDescription("Select all rows").performClick()
        onNodeWithText("selected-2").assertExists()
        onAllNodesWithContentDescription("Select this row").onFirst().performClick()
        onNodeWithText("selected-1").assertExists()
    }

    @Test
    fun expandableRowShowsDetailContent() = runComposeUiTest {
        var expanded by mutableStateOf(setOf<Any>())
        setContent {
            ElConfigProvider {
                ElTable(
                    data = listOf(Row("Alice", 28), Row("Bob", 34)),
                    columns = listOf(ElTableColumn("Name", property = { it.name })),
                    rowKey = { it.name },
                    expandedRowKeys = expanded,
                    onExpandedChange = { expanded = it },
                    expandContent = { row, _ -> BasicText("detail-${row.name}") },
                )
            }
        }
        onAllNodesWithContentDescription("Expand this row").onFirst().performClick()
        onNodeWithText("detail-Alice").assertExists()
    }

    @Test
    fun lazyTreeLoadsChildrenOnExpand() = runComposeUiTest {
        data class Node(val id: String, val label: String)
        var loadCalled = false
        setContent {
            ElConfigProvider {
                ElTable(
                    data = listOf(Node("root", "Server")),
                    columns = listOf(ElTableColumn("Name", property = { it.label })),
                    rowKey = { it.id },
                    hasChildren = { it.id == "root" },
                    lazy = true,
                    load = { _, resolve ->
                        loadCalled = true
                        resolve(listOf(Node("child", "Database")))
                    },
                )
            }
        }
        onAllNodesWithContentDescription("Expand this row").onFirst().performClick()
        assertTrue(loadCalled)
        onNodeWithText("Database").assertExists()
    }

    @Test
    fun stateFilterHidesNonMatchingRows() = runComposeUiTest {
        val state = ElTableState()
        setContent {
            ElConfigProvider {
                Box(Modifier.fillMaxSize()) {
                    ElTable(
                        data = listOf(Order(1, "2024-01-01"), Order(2, "2024-01-02")),
                        columns = listOf(
                            ElTableColumn("Order", property = { "Order ${it.id}" }),
                            ElTableColumn(
                                "Date",
                                key = "date",
                                property = { it.date },
                                filters = listOf(
                                    ElTableFilter("January 1", "2024-01-01"),
                                    ElTableFilter("January 2", "2024-01-02"),
                                ),
                                filterMethod = { value, row -> row.date == value },
                            ),
                        ),
                        state = state,
                    )
                }
            }
        }
        onNodeWithText("Order 2").assertExists()
        state.setFilter("date", listOf("2024-01-01"))
        waitForIdle()
        onNodeWithText("Order 1").assertExists()
        onNodeWithText("Order 2").assertDoesNotExist()
    }

    @Test
    fun filterPopupAppliesAndClearsColumnFilters() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElTable(
                    data = listOf(Order(1, "2024-01-01"), Order(2, "2024-01-02")),
                    columns = listOf(
                        ElTableColumn("Order", property = { "Order ${it.id}" }),
                        ElTableColumn(
                            "Date",
                            key = "date",
                            property = { it.date },
                            filters = listOf(
                                ElTableFilter("January 1", "2024-01-01"),
                                ElTableFilter("January 2", "2024-01-02"),
                            ),
                            filterMethod = { value, row -> row.date == value },
                        ),
                    ),
                )
            }
        }
        onNodeWithText("Order 2").assertExists()
        onNodeWithContentDescription("Filter by Date").performClick()
        onNodeWithContentDescription("Filter by Date: January 1").performClick()
        waitForIdle()
        onNodeWithText("Order 1").assertExists()
        onNodeWithText("Order 2").assertDoesNotExist()
        onNodeWithText("Reset").performClick()
        waitForIdle()
        onNodeWithText("Order 2").assertExists()
    }

    @Test
    fun treeSelectionCascadesToChildrenAndAncestors() = runComposeUiTest {
        data class Node(val id: String, val label: String, val children: List<Node> = emptyList())
        val tree = listOf(
            Node("1", "Root", listOf(Node("1-1", "Child 1"), Node("1-2", "Child 2"))),
        )
        var selected by mutableStateOf(setOf<Any>())
        setContent {
            ElConfigProvider {
                Column {
                    ElTable(
                        data = tree,
                        columns = listOf(ElTableColumn("Name", property = { it.label })),
                        children = { it.children },
                        rowKey = { it.id },
                        defaultExpandAll = true,
                        selection = true,
                        selectedKeys = selected,
                        onSelectionChange = { selected = it },
                    )
                    BasicText("tree-selected-${selected.size}")
                }
            }
        }
        onAllNodesWithContentDescription("Select this row").onFirst().performClick()
        onNodeWithText("tree-selected-3").assertExists()
        onAllNodesWithContentDescription("Select this row")[1].performClick()
        onNodeWithText("tree-selected-1").assertExists()
    }

    @Test
    fun virtualizedTableSupportsSelectionAndSorting() = runComposeUiTest {
        var selected by mutableStateOf(setOf<Any>())
        var lastSort: Boolean? = null
        setContent {
            ElConfigProvider {
                ElVirtualizedTable(
                    data = listOf(Row("Alice", 28), Row("Bob", 34)),
                    columns = listOf(
                        ElTableColumn("Name", property = { it.name }),
                        ElTableColumn("Age", key = "age", property = { it.age }, sortable = true),
                    ),
                    rowKey = { it.name },
                    height = 160.dp,
                    selection = true,
                    selectedKeys = selected,
                    onSelectionChange = { selected = it },
                    onSortChange = { _, ascending -> lastSort = ascending },
                )
            }
        }
        onNodeWithContentDescription("Sort by Age").performClick()
        assertEquals(true, lastSort)
        onNodeWithContentDescription("Select all rows").performClick()
        assertEquals(setOf<Any>("Alice", "Bob"), selected)
    }

    @Test
    fun virtualizedTreeExpandsChildren() = runComposeUiTest {
        data class Node(val id: String, val name: String, val children: List<Node> = emptyList())
        var expanded by mutableStateOf(setOf<Any>())
        setContent {
            ElConfigProvider {
                ElVirtualizedTable(
                    data = listOf(Node("1", "Root", listOf(Node("1-1", "Child A")))),
                    columns = listOf(ElTableColumn("Name", key = "name", width = 220.dp, property = { it.name })),
                    height = 200.dp,
                    rowKey = { it.id },
                    children = { it.children },
                    expandColumnKey = "name",
                    expandedRowKeys = expanded,
                    onExpandedChange = { expanded = it },
                )
            }
        }
        onAllNodesWithContentDescription("Expand this row").onFirst().performClick()
        onNodeWithText("Child A").assertExists()
    }

    @Test
    fun virtualizedDetailViewIsExpandedByDefault() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                ElVirtualizedTable(
                    data = listOf(Row("Alice", 28), Row("Bob", 34)),
                    columns = listOf(
                        ElTableColumn("Name", width = 160.dp, property = { it.name }),
                        ElTableColumn("Age", width = 80.dp, property = { it.age }),
                    ),
                    height = 240.dp,
                    rowKey = { it.name },
                    defaultExpandedRowKeys = setOf("Alice"),
                    detailContent = { row, _ -> BasicText("detail-${row.name}") },
                )
            }
        }
        onNodeWithText("detail-Alice").assertExists()
    }

    @Test
    fun virtualizedControlledSortReportsTheHoistedMap() = runComposeUiTest {
        var sort by mutableStateOf(mapOf("name" to ElTableSortOrder.Ascending))
        setContent {
            ElConfigProvider {
                ElVirtualizedTable(
                    data = listOf(Row("Alice", 28), Row("Bob", 34)),
                    columns = listOf(
                        ElTableColumn("Name", key = "name", width = 160.dp, property = { it.name }, sortable = true),
                        ElTableColumn("Age", key = "age", width = 80.dp, property = { it.age }, sortable = true),
                    ),
                    height = 200.dp,
                    sortState = sort,
                    onSortStateChange = { sort = it },
                )
            }
        }
        onNodeWithContentDescription("Sort by Name").performClick()
        assertEquals(mapOf("name" to ElTableSortOrder.Descending), sort)
        onNodeWithContentDescription("Sort by Age").performClick()
        assertEquals(
            mapOf(
                "name" to ElTableSortOrder.Descending,
                "age" to ElTableSortOrder.Ascending,
            ),
            sort,
        )
    }

    @Test
    fun virtualizedFilterPopupAndFooterEmptyOverlaySlotsWork() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                Column {
                    ElVirtualizedTable(
                        data = listOf(Order(1, "2024-01-01"), Order(2, "2024-01-02")),
                        columns = listOf(
                            ElTableColumn("Order", width = 140.dp, property = { "Order ${it.id}" }),
                            ElTableColumn(
                                "Date",
                                key = "date",
                                width = 160.dp,
                                property = { it.date },
                                filters = listOf(
                                    ElTableFilter("January 1", "2024-01-01"),
                                    ElTableFilter("January 2", "2024-01-02"),
                                ),
                                filterMethod = { value, row -> row.date == value },
                            ),
                        ),
                        height = 200.dp,
                    )
                    ElVirtualizedTable(
                        data = emptyList<Row>(),
                        columns = listOf(ElTableColumn("Name", width = 160.dp, property = { it.name })),
                        height = 180.dp,
                        footerHeight = 32.dp,
                        footer = { BasicText("custom-footer") },
                        empty = { BasicText("custom-empty") },
                        overlay = { BasicText("custom-overlay") },
                    )
                }
            }
        }
        onNodeWithContentDescription("Filter by Date").performClick()
        onNodeWithContentDescription("Filter by Date: January 1").performClick()
        waitForIdle()
        onNodeWithText("Order 1").assertExists()
        onNodeWithText("Order 2").assertDoesNotExist()
        onNodeWithText("custom-footer").assertExists()
        onNodeWithText("custom-empty").assertExists()
        onNodeWithText("custom-overlay").assertExists()
    }

    @Test
    fun virtualizedTableScrollsToARowThroughListState() = runComposeUiTest {
        setContent {
            ElConfigProvider {
                val listState = rememberLazyListState()
                val scope = rememberCoroutineScope()
                Column {
                    ElVirtualizedTable(
                        data = (1..100).map { "Row $it" },
                        columns = listOf(ElTableColumn("Name", width = 200.dp, property = { it })),
                        height = 160.dp,
                        listState = listState,
                    )
                    ElButton(
                        text = "jump",
                        onClick = { scope.launch { listState.scrollToItem(20) } },
                    )
                }
            }
        }
        onNodeWithText("Row 1").assertExists()
        onNodeWithText("jump").performClick()
        waitForIdle()
        onNodeWithText("Row 21").assertExists()
        onNodeWithText("Row 1").assertDoesNotExist()
    }
}
