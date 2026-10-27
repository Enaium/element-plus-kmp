package cn.enaium.elementplus.sample.gallery.data

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.sample.gallery.DemoRow
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElTextAlign
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.launch

private data class V2Row(
    val id: Int,
    val name: String,
    val age: Int,
    val city: String,
    val status: String,
    val date: String,
    val description: String,
)

private data class V2TreeRow(
    val id: String,
    val name: String,
    val age: Int,
    val city: String,
    val status: String,
    val children: List<V2TreeRow> = emptyList(),
)

private fun v2Rows(count: Int = 1000): List<V2Row> {
    val names = listOf("Tom", "Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace")
    val cities = listOf("Chengdu", "Shanghai", "Beijing", "Shenzhen", "Hangzhou", "Wuhan")
    val statuses = listOf("Active", "Pending", "Inactive")
    val descriptions = listOf(
        "Short note.",
        "A medium description that wraps onto a second line when the column is narrow.",
        "A longer description used to demonstrate dynamic row measurement. It contains enough text to wrap across several lines and make the row taller than the estimated height.",
    )
    return (1..count).map { index ->
        V2Row(
            id = index,
            name = "${names[index % names.size]} $index",
            age = 18 + index % 50,
            city = cities[index % cities.size],
            status = statuses[index % statuses.size],
            date = "2026-${(index % 12 + 1).toString().padStart(2, '0')}-${(index % 28 + 1).toString().padStart(2, '0')}",
            description = descriptions[index % descriptions.size],
        )
    }
}

private fun v2Columns(): List<ElTableColumn<V2Row>> = listOf(
    ElTableColumn("ID", key = "id", width = 70.dp, property = { it.id }),
    ElTableColumn("Name", key = "name", width = 150.dp, property = { it.name }, sortable = true),
    ElTableColumn("Age", key = "age", width = 80.dp, property = { it.age }, sortable = true, align = ElTextAlign.End),
    ElTableColumn("City", key = "city", width = 130.dp, property = { it.city }),
    ElTableColumn("Status", key = "status", width = 100.dp, property = { it.status }),
    ElTableColumn("Date", key = "date", width = 130.dp, property = { it.date }),
)

private fun v2StatusCell(row: V2Row): @Composable () -> Unit = {
    ElTag(
        text = row.status,
        type = when (row.status) {
            "Active" -> ElType.Success
            "Pending" -> ElType.Warning
            else -> ElType.Info
        },
        size = ElSize.Small,
    )
}

private fun v2Tree(): List<V2TreeRow> = (1..30).map { index ->
    V2TreeRow(
        id = "root-$index",
        name = "Department $index",
        age = 20 + index,
        city = listOf("Chengdu", "Shanghai", "Beijing")[index % 3],
        status = if (index % 3 == 0) "Pending" else "Active",
        children = (1..3).map { child ->
            V2TreeRow(
                id = "root-$index-$child",
                name = "Member $index-$child",
                age = 22 + child,
                city = "Chengdu",
                status = "Active",
            )
        },
    )
}

@Composable
fun VirtualizedTableDocs() {
    val rows = remember { v2Rows(1000) }
    val colors = LocalElTheme.current.colors
    val theme = LocalElTheme.current

    DemoExample("Basic usage", "1000 rows, 6 columns, only visible rows are composed.") {
        ElVirtualizedTable(
            data = rows,
            columns = v2Columns(),
            width = 700.dp,
            height = 400.dp,
        )
    }

    DemoExample("Auto resizer", "Measure the parent and pass its size to the table.") {
        Box(Modifier.fillMaxWidth().height(400.dp)) {
            ElAutoResizer { width, height ->
                ElVirtualizedTable(
                    data = rows.take(200),
                    columns = v2Columns(),
                    width = width,
                    height = height,
                )
            }
        }
    }

    DemoExample("Customize Cell Renderer", "Use cell lambdas for dates, tags and action buttons.") {
        ElVirtualizedTable(
            data = rows.take(200),
            columns = listOf(
                ElTableColumn(
                    "Date",
                    width = 160.dp,
                    fixed = ElTableFixed.Left,
                    cell = { row, _ ->
                        ElTooltip(text = row.date) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ElIcon(ElIcons.Timer, size = 14.dp, tint = theme.colors.textSecondary)
                                Spacer(Modifier.width(6.dp))
                                BasicText(row.date, style = theme.typography.small)
                            }
                        }
                    },
                ),
                ElTableColumn(
                    "Name",
                    width = 150.dp,
                    align = ElTextAlign.Center,
                    cell = { row, _ -> ElTag(row.name) },
                ),
                ElTableColumn(
                    "Operations",
                    width = 200.dp,
                    align = ElTextAlign.Center,
                    cell = { _, _ ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ElButton(text = "Edit", size = ElSize.Small, onClick = {})
                            ElButton(text = "Delete", size = ElSize.Small, type = ElType.Danger, onClick = {})
                        }
                    },
                ),
            ),
            width = 700.dp,
            height = 400.dp,
            border = true,
        )
    }

    DemoExample("Table with selections", "Select rows with a built-in selection column.") {
        var selected by remember { mutableStateOf(setOf<Any>()) }
        ElVirtualizedTable(
            data = rows.take(200),
            columns = v2Columns(),
            rowKey = { it.id },
            width = 700.dp,
            height = 400.dp,
            selection = true,
            selectedKeys = selected,
            onSelectionChange = { selected = it },
            border = true,
        )
        DemoHint("Selected rows: ${selected.size}")
    }

    DemoExample("Inline editing", "Click a name to edit it; press Enter to finish.") {
        var editableRows by remember { mutableStateOf(v2Rows(200)) }
        var editingId by remember { mutableStateOf<Int?>(null) }
        ElVirtualizedTable(
            data = editableRows,
            columns = listOf(
                ElTableColumn("ID", width = 70.dp, property = { it.id }),
                ElTableColumn(
                    "Editable name",
                    width = 220.dp,
                    cell = { row, _ ->
                        if (editingId == row.id) {
                            ElInput(
                                value = row.name,
                                onValueChange = { value ->
                                    editableRows = editableRows.map {
                                        if (it.id == row.id) it.copy(name = value) else it
                                    }
                                },
                                onKeyboardAction = { editingId = null },
                                size = ElSize.Small,
                            )
                        } else {
                            Box(
                                Modifier.fillMaxWidth().clickable { editingId = row.id }.padding(4.dp),
                            ) {
                                BasicText(
                                    text = row.name,
                                    style = theme.typography.small.copy(color = theme.colors.textRegular),
                                )
                            }
                        }
                    },
                ),
                ElTableColumn("Age", width = 80.dp, property = { it.age }, align = ElTextAlign.End),
                ElTableColumn("Status", width = 100.dp, cell = { row, _ -> v2StatusCell(row)() }),
            ),
            rowKey = { it.id },
            width = 700.dp,
            height = 400.dp,
            border = true,
        )
    }

    DemoExample("Table with status", "Render tags for state values.") {
        ElVirtualizedTable(
            data = rows.take(200),
            columns = listOf(
                ElTableColumn("ID", width = 80.dp, property = { it.id }),
                ElTableColumn("Name", width = 200.dp, property = { it.name }),
                ElTableColumn("Status", width = 120.dp, cell = { row, _ -> v2StatusCell(row)() }),
            ),
            width = 700.dp,
            height = 400.dp,
            border = true,
        )
    }

    DemoExample("Table with sticky rows", "A fixed row follows the scroll position.") {
        val stickyRows = remember { v2Rows(200) }
        val listState = rememberLazyListState()
        var stickyIndex by remember { mutableStateOf(0) }
        LaunchedEffect(listState) {
            snapshotFlow { listState.firstVisibleItemIndex }.collect { index ->
                stickyIndex = ((index / 5) * 5).coerceIn(0, stickyRows.lastIndex)
            }
        }
        ElVirtualizedTable(
            data = stickyRows.drop(1),
            columns = v2Columns(),
            rowKey = { it.id },
            width = 700.dp,
            height = 400.dp,
            border = true,
            listState = listState,
            fixedData = listOf(stickyRows[stickyIndex]),
            fixedRowBackground = colors.primary.copy(alpha = 0.14f),
        )
    }

    DemoExample("Table with fixed columns", "Left and right columns stay pinned while the middle scrolls.") {
        ElVirtualizedTable(
            data = rows.take(200),
            columns = listOf(
                ElTableColumn("ID", width = 70.dp, fixed = ElTableFixed.Left, property = { it.id }),
                ElTableColumn("Name", width = 130.dp, fixed = ElTableFixed.Left, property = { it.name }),
                ElTableColumn("City", width = 130.dp, property = { it.city }),
                ElTableColumn("Age", width = 80.dp, property = { it.age }),
                ElTableColumn("Date", width = 130.dp, property = { it.date }),
                ElTableColumn("Description", width = 220.dp, property = { it.description }),
                ElTableColumn("Status", width = 100.dp, fixed = ElTableFixed.Right, cell = { row, _ -> v2StatusCell(row)() }),
            ),
            width = 700.dp,
            height = 400.dp,
            border = true,
        )
    }

    DemoExample("Grouping header", "Group columns with a two-level header.") {
        ElVirtualizedTable(
            data = rows.take(200),
            columns = listOf(
                ElTableColumn("ID", width = 70.dp, fixed = ElTableFixed.Left, property = { it.id }),
                ElTableColumn(
                    "User",
                    children = listOf(
                        ElTableColumn("Name", width = 180.dp, property = { it.name }),
                        ElTableColumn("City", width = 140.dp, property = { it.city }),
                    ),
                ),
                ElTableColumn(
                    "Work",
                    children = listOf(
                        ElTableColumn("Age", width = 80.dp, property = { it.age }),
                        ElTableColumn("Status", width = 100.dp, property = { it.status }),
                        ElTableColumn("Date", width = 130.dp, property = { it.date }),
                    ),
                ),
            ),
            width = 700.dp,
            height = 400.dp,
            border = true,
        )
    }

    DemoExample("Filter", "Filter the status column with the built-in filter popover.") {
        val tableState = rememberElTableState()
        ElVirtualizedTable(
            data = rows.take(200),
            columns = listOf(
                ElTableColumn("ID", width = 70.dp, property = { it.id }),
                ElTableColumn("Name", width = 160.dp, property = { it.name }),
                ElTableColumn(
                    "Status",
                    key = "status",
                    width = 120.dp,
                    cell = { row, _ -> v2StatusCell(row)() },
                    filters = listOf(
                        ElTableFilter("Active", "Active"),
                        ElTableFilter("Pending", "Pending"),
                        ElTableFilter("Inactive", "Inactive"),
                    ),
                    filterMethod = { value, row -> row.status == value },
                ),
                ElTableColumn("City", width = 140.dp, property = { it.city }),
            ),
            state = tableState,
            width = 700.dp,
            height = 400.dp,
            border = true,
        )
        DemoRow { ElButton(text = "Clear filters", onClick = { tableState.clearFilter() }) }
    }

    DemoExample("Sortable", "Click a sortable header to cycle sort order.") {
        ElVirtualizedTable(
            data = rows.take(200),
            columns = listOf(
                ElTableColumn("Name", key = "name", width = 180.dp, property = { it.name }, sortable = true),
                ElTableColumn("Age", key = "age", width = 100.dp, property = { it.age }, sortable = true, align = ElTextAlign.End),
                ElTableColumn("City", width = 160.dp, property = { it.city }),
            ),
            width = 700.dp,
            height = 400.dp,
            border = true,
        )
    }

    DemoExample("Controlled Sort", "The sort state is hoisted and supports multiple sorted columns.") {
        var sortState by remember {
            mutableStateOf(
                mapOf(
                    "name" to ElTableSortOrder.Ascending,
                    "age" to ElTableSortOrder.Descending,
                ),
            )
        }
        ElVirtualizedTable(
            data = rows.take(200),
            columns = listOf(
                ElTableColumn("Name", key = "name", width = 180.dp, property = { it.name }, sortable = true),
                ElTableColumn("Age", key = "age", width = 100.dp, property = { it.age }, sortable = true, align = ElTextAlign.End),
                ElTableColumn("City", width = 160.dp, property = { it.city }),
            ),
            sortState = sortState,
            onSortStateChange = { sortState = it },
            width = 700.dp,
            height = 400.dp,
            border = true,
        )
        DemoHint(sortState.entries.joinToString { "${it.key}: ${it.value}" })
    }

    DemoExample("Cross hovering", "Highlight every cell in the hovered column.") {
        ElVirtualizedTable(
            data = rows.take(200),
            columns = listOf(
                ElTableColumn("Row No.", width = 70.dp, fixed = ElTableFixed.Left, type = ElTableColumnType.Index),
                ElTableColumn("Name", width = 170.dp, property = { it.name }),
                ElTableColumn("Age", width = 90.dp, property = { it.age }),
                ElTableColumn("City", width = 160.dp, property = { it.city }),
                ElTableColumn("Status", width = 110.dp, property = { it.status }),
            ),
            width = 700.dp,
            height = 400.dp,
            crossHovering = true,
            border = true,
        )
    }

    DemoExample("Colspan", "Cells span multiple columns.") {
        val spanRows = remember { v2Rows(40) }
        ElVirtualizedTable(
            data = spanRows,
            columns = listOf(
                ElTableColumn("ID", width = 70.dp, property = { it.id }),
                ElTableColumn(
                    "Name",
                    width = 160.dp,
                    align = ElTextAlign.Center,
                    property = { it.name },
                    colSpan = { _, rowIndex, _ -> (rowIndex % 4) + 1 },
                ),
                ElTableColumn("Age", width = 80.dp, property = { it.age }),
                ElTableColumn("City", width = 140.dp, property = { it.city }),
                ElTableColumn("Status", width = 110.dp, property = { it.status }),
            ),
            width = 700.dp,
            height = 400.dp,
            border = true,
        )
    }

    DemoExample("Rowspan", "Cells span two rows at a time.") {
        val spanRows = remember { v2Rows(40) }
        ElVirtualizedTable(
            data = spanRows,
            columns = listOf(
                ElTableColumn(
                    "ID",
                    width = 80.dp,
                    property = { it.id },
                    rowSpan = { _, rowIndex, _ ->
                        if (rowIndex % 2 == 0 && rowIndex <= spanRows.lastIndex - 1) 2 else 1
                    },
                ),
                ElTableColumn("Name", width = 180.dp, property = { it.name }),
                ElTableColumn("Age", width = 90.dp, property = { it.age }),
                ElTableColumn("City", width = 160.dp, property = { it.city }),
                ElTableColumn("Status", width = 120.dp, property = { it.status }),
            ),
            width = 700.dp,
            height = 400.dp,
            border = true,
        )
    }

    DemoExample("Rowspan and Colspan together", "Merge cells across rows and columns.") {
        val spanRows = remember { v2Rows(40) }
        ElVirtualizedTable(
            data = spanRows,
            columns = listOf(
                ElTableColumn(
                    "ID",
                    width = 80.dp,
                    property = { it.id },
                    rowSpan = { _, rowIndex, _ ->
                        if (rowIndex % 2 == 0 && rowIndex <= spanRows.lastIndex - 1) 2 else 1
                    },
                ),
                ElTableColumn(
                    "Name",
                    width = 180.dp,
                    align = ElTextAlign.Center,
                    property = { it.name },
                    colSpan = { _, rowIndex, _ -> (rowIndex % 3) + 1 },
                ),
                ElTableColumn("Age", width = 90.dp, property = { it.age }),
                ElTableColumn("City", width = 160.dp, property = { it.city }),
                ElTableColumn("Status", width = 120.dp, property = { it.status }),
            ),
            width = 700.dp,
            height = 400.dp,
            border = true,
        )
    }

    DemoExample("Tree data", "Expandable rows from nested data.") {
        val tree = remember { v2Tree() }
        var expanded by remember { mutableStateOf(setOf<Any>("root-1")) }
        ElVirtualizedTable(
            data = tree,
            columns = listOf(
                ElTableColumn("Name", key = "name", width = 220.dp, property = { it.name }),
                ElTableColumn("Age", width = 90.dp, property = { it.age }, align = ElTextAlign.End),
                ElTableColumn("City", width = 150.dp, property = { it.city }),
                ElTableColumn("Status", width = 110.dp, property = { it.status }),
            ),
            rowKey = { it.id },
            children = { it.children },
            expandColumnKey = "name",
            expandedRowKeys = expanded,
            onExpandedChange = { expanded = it },
            width = 700.dp,
            height = 400.dp,
            border = true,
        )
    }

    DemoExample("Dynamic height rows", "Rows measure their content instead of using a fixed height.") {
        ElVirtualizedTable(
            data = rows.take(200),
            columns = listOf(
                ElTableColumn("ID", width = 70.dp, property = { it.id }),
                ElTableColumn("Name", width = 120.dp, property = { it.name }),
                ElTableColumn(
                    "Description",
                    width = 300.dp,
                    cell = { row, _ ->
                        BasicText(
                            text = row.description,
                            modifier = Modifier.padding(vertical = 10.dp),
                            style = theme.typography.small.copy(color = theme.colors.textRegular),
                        )
                    },
                ),
                ElTableColumn("Status", width = 100.dp, cell = { row, _ -> v2StatusCell(row)() }),
            ),
            estimatedRowHeight = 40.dp,
            width = 700.dp,
            height = 400.dp,
            border = true,
        )
    }

    DemoExample("Detail view", "Expand a row to show a custom detail panel.") {
        ElVirtualizedTable(
            data = rows.take(200),
            columns = listOf(
                ElTableColumn("ID", width = 70.dp, property = { it.id }),
                ElTableColumn("Name", width = 170.dp, property = { it.name }),
                ElTableColumn("City", width = 140.dp, property = { it.city }),
                ElTableColumn("Status", width = 100.dp, property = { it.status }),
            ),
            rowKey = { it.id },
            defaultExpandedRowKeys = setOf(1),
            detailContent = { row, _ ->
                BasicText(
                    text = "${row.name} lives in ${row.city}. ${row.description}",
                    modifier = Modifier.padding(18.dp),
                    style = theme.typography.small.copy(color = theme.colors.textRegular),
                )
            },
            width = 700.dp,
            height = 400.dp,
            border = true,
        )
    }

    DemoExample("Customized Footer", "Provide a footer slot with its own height.") {
        ElVirtualizedTable(
            data = rows.take(200),
            columns = v2Columns(),
            width = 700.dp,
            height = 400.dp,
            border = true,
            footerHeight = 50.dp,
            footer = {
                Box(
                    Modifier.fillMaxSize().background(colors.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        text = "Display a message in the footer",
                        style = theme.typography.small.copy(color = theme.colors.textRegular),
                    )
                }
            },
        )
    }

    DemoExample("Customized Empty Renderer", "Replace the default empty state.") {
        ElVirtualizedTable(
            data = emptyList<V2Row>(),
            columns = v2Columns(),
            width = 700.dp,
            height = 400.dp,
            border = true,
            empty = { ElEmpty(description = "No records") },
        )
    }

    DemoExample("Overlay", "Compose an overlay over the table body.") {
        ElVirtualizedTable(
            data = rows.take(200),
            columns = v2Columns(),
            width = 700.dp,
            height = 400.dp,
            border = true,
            overlay = {
                Box(
                    Modifier.fillMaxSize().background(colors.background.copy(alpha = 0.72f)),
                    contentAlignment = Alignment.Center,
                ) {
                    ElIcon(
                        ElIcons.Loading,
                        size = 28.dp,
                        tint = colors.primary,
                        spin = true,
                    )
                }
            },
        )
    }

    DemoExample("Manual scrolling", "Scroll by pixels or by row index through the list state.") {
        val listState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        var pixels by remember { mutableStateOf("200") }
        var rowIndex by remember { mutableStateOf("10") }
        DemoRow {
            ElInput(value = pixels, onValueChange = { pixels = it }, modifier = Modifier.width(100.dp), size = ElSize.Small)
            ElButton(text = "Scroll by pixels", onClick = {
                scope.launch { listState.scrollBy(pixels.toFloatOrNull() ?: 0f) }
            })
            ElInput(value = rowIndex, onValueChange = { rowIndex = it }, modifier = Modifier.width(100.dp), size = ElSize.Small)
            ElButton(text = "Scroll by rows", onClick = {
                scope.launch {
                    listState.scrollToItem((rowIndex.toIntOrNull() ?: 0).coerceIn(0, rows.lastIndex))
                }
            })
        }
        ElVirtualizedTable(
            data = rows,
            columns = v2Columns(),
            width = 700.dp,
            height = 400.dp,
            border = true,
            listState = listState,
        )
    }
}
