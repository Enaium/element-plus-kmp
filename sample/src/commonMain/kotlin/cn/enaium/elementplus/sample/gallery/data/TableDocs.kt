package cn.enaium.elementplus.sample.gallery.data

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElTable
import cn.enaium.elementplus.components.ElTableCellSpan
import cn.enaium.elementplus.components.ElTableColumn
import cn.enaium.elementplus.components.ElTableLayout
import cn.enaium.elementplus.components.ElTableFilter
import cn.enaium.elementplus.components.ElTableFixed
import cn.enaium.elementplus.components.ElTableSort
import cn.enaium.elementplus.components.ElTableSortOrder
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.ElTag
import cn.enaium.elementplus.components.rememberElTableState
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.sample.gallery.DemoRow
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElTextAlign
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TableDocs() {
    val rows = remember {
        listOf(
            PersonRow("Alice", 28, "Chengdu", "Active"),
            PersonRow("Bob", 34, "Shanghai", "Active"),
            PersonRow("Charlie", 22, "Beijing", "Pending"),
            PersonRow("Diana", 41, "Shenzhen", "Active"),
        )
    }
    DemoExample("Basic usage", "A data table with property-based columns.") {
        ElTable(
            data = rows,
            columns = listOf(
                ElTableColumn("Name", width = 140.dp, property = { it.name }),
                ElTableColumn("Age", width = 80.dp, property = { it.age }),
                ElTableColumn("City", width = 140.dp, property = { it.city }),
            ),
        )
    }
    DemoExample("Striped and border", "Add row stripes and cell borders.") {
        ElTable(
            data = rows,
            columns = listOf(
                ElTableColumn("Name", property = { it.name }),
                ElTableColumn("Age", property = { it.age }),
                ElTableColumn("City", property = { it.city }),
                ElTableColumn("Status", property = { it.status }),
            ),
            stripe = true,
            border = true,
        )
    }
    DemoExample("Sortable", "Click a sortable header to cycle ascending and descending.") {
        ElTable(
            data = rows,
            columns = listOf(
                ElTableColumn("Name", key = "name", property = { it.name }, sortable = true),
                ElTableColumn("Age", key = "age", property = { it.age }, sortable = true, align = ElTextAlign.End),
                ElTableColumn("City", property = { it.city }),
            ),
        )
    }
    DemoExample(
        "Default sort",
        "Initialize table state with a descending sort without a callback.",
    ) {
        ElTable(
            data = rows,
            columns = listOf(
                ElTableColumn("Name", property = { it.name }, sortable = true),
                ElTableColumn("Age", key = "age", property = { it.age }, sortable = true),
                ElTableColumn("City", property = { it.city }),
            ),
            state = rememberElTableState(ElTableSort("age", ElTableSortOrder.Descending)),
        )
    }
    DemoExample("Selection", "Select one or more rows with checkboxes.") {
        var selected by remember { mutableStateOf(setOf<Any>()) }
        ElTable(
            data = rows,
            columns = listOf(
                ElTableColumn("Name", property = { it.name }),
                ElTableColumn("Age", property = { it.age }),
                ElTableColumn("City", property = { it.city }),
            ),
            selection = true,
            selectedKeys = selected,
            onSelectionChange = { selected = it },
            rowKey = { it.name },
        )
        DemoRow {
            ElButton(text = "Clear selection", onClick = { selected = emptySet() })
            DemoHint("Selected: ${selected.size}")
        }
    }
    DemoExample("Single selection", "Highlight the current row through onRowClick.") {
        var current by remember { mutableStateOf<Any?>("Bob") }
        ElTable(
            data = rows,
            columns = listOf(
                ElTableColumn("Name", property = { it.name }),
                ElTableColumn("Age", property = { it.age }),
                ElTableColumn("City", property = { it.city }),
            ),
            rowKey = { it.name },
            highlightCurrentRow = true,
            selectedRowKey = current,
            onRowClick = { row, _ -> current = row.name },
        )
        DemoHint("Current row: ${current ?: "none"}")
    }
    DemoExample("Custom column", "Render a tag or any composable inside a cell.") {
        ElTable(
            data = rows,
            columns = listOf(
                ElTableColumn("Name", width = 140.dp, property = { it.name }),
                ElTableColumn("Status", width = 120.dp, cell = { row, _ ->
                    ElTag(
                        text = row.status,
                        type = if (row.status == "Active") ElType.Success else ElType.Warning,
                    )
                }),
            ),
        )
    }
    DemoExample("Fixed height", "A fixed-height table scrolls the body under a sticky header.") {
        ElTable(
            data = rows + rows + rows,
            columns = listOf(
                ElTableColumn("Name", property = { it.name }),
                ElTableColumn("Age", property = { it.age }),
                ElTableColumn("City", property = { it.city }),
            ),
            height = 180.dp,
            stripe = true,
        )
    }
    DemoExample("Empty and loading", "Show the empty state or a loading indicator.") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElTable(
                data = emptyList<PersonRow>(),
                columns = listOf(ElTableColumn("Name", property = { it.name })),
            )
            ElTable(
                data = rows,
                columns = listOf(ElTableColumn("Name", property = { it.name })),
                loading = true,
            )
        }
    }

    DemoExample("Fixed columns", "Pin the id and actions columns while the middle scrolls.") {
        data class Ticket(val id: Int, val title: String, val owner: String, val area: String, val state: String)
        val tickets = remember {
            (1..8).map { index ->
                Ticket(index, "Ticket $index", "Owner ${index % 3 + 1}", "Area ${index % 4 + 1}", if (index % 2 == 0) "Open" else "Closed")
            }
        }
        ElTable(
            data = tickets,
            columns = listOf(
                ElTableColumn("ID", width = 60.dp, fixed = ElTableFixed.Left, property = { it.id }),
                ElTableColumn("Title", width = 200.dp, property = { it.title }),
                ElTableColumn("Owner", width = 200.dp, property = { it.owner }),
                ElTableColumn("Area", width = 200.dp, property = { it.area }),
                ElTableColumn(
                    "State",
                    width = 100.dp,
                    fixed = ElTableFixed.Right,
                    cell = { row, _ ->
                        ElTag(row.state, type = if (row.state == "Open") ElType.Danger else ElType.Info, size = ElSize.Small)
                    },
                ),
            ),
            height = 260.dp,
            border = true,
            modifier = Modifier.width(640.dp),
        )
    }

    DemoExample("Grouped header", "Group columns under descriptive headers.") {
        data class Delivery(val id: Int, val name: String, val address: String, val city: String, val zip: String)
        val deliveries = listOf(
            Delivery(1, "Alice", "No. 1 Road", "Chengdu", "610000"),
            Delivery(2, "Bob", "No. 2 Road", "Shanghai", "200000"),
            Delivery(3, "Charlie", "No. 3 Road", "Beijing", "100000"),
        )
        ElTable(
            data = deliveries,
            columns = listOf(
                ElTableColumn("ID", width = 80.dp, fixed = ElTableFixed.Left, property = { it.id }),
                ElTableColumn(
                    "Delivery",
                    children = listOf(
                        ElTableColumn("Name", property = { it.name }),
                        ElTableColumn("Address", property = { it.address }),
                    ),
                ),
                ElTableColumn(
                    "Location",
                    children = listOf(
                        ElTableColumn("City", property = { it.city }),
                        ElTableColumn("Zip", property = { it.zip }),
                    ),
                ),
            ),
            border = true,
        )
    }

    DemoExample("Filter", "Filter rows by date and tag. Filter state lives in ElTableState.") {
        data class Order(val id: Int, val name: String, val date: String, val tag: String)
        val orders = listOf(
            Order(1, "Order 1", "2024-01-01", "Home"),
            Order(2, "Order 2", "2024-01-02", "Office"),
            Order(3, "Order 3", "2024-01-03", "Home"),
            Order(4, "Order 4", "2024-01-04", "Shop"),
        )
        val tableState = rememberElTableState()
        ElTable(
            data = orders,
            columns = listOf(
                ElTableColumn("ID", width = 80.dp, key = "id", property = { it.id }),
                ElTableColumn(
                    "Date",
                    key = "date",
                    property = { it.date },
                    filters = listOf(
                        ElTableFilter("January 1", "2024-01-01"),
                        ElTableFilter("January 2", "2024-01-02"),
                        ElTableFilter("January 3", "2024-01-03"),
                    ),
                    filterMethod = { value, row -> row.date == value },
                ),
                ElTableColumn(
                    "Tag",
                    key = "tag",
                    property = { it.tag },
                    filters = listOf(
                        ElTableFilter("Home", "Home"),
                        ElTableFilter("Office", "Office"),
                        ElTableFilter("Shop", "Shop"),
                    ),
                    filterMethod = { value, row -> row.tag == value },
                ),
                ElTableColumn("Name", property = { it.name }),
            ),
            state = tableState,
            border = true,
        )
        DemoRow {
            ElButton(text = "Clear date", onClick = { tableState.clearFilter("date") })
            ElButton(text = "Clear all", onClick = { tableState.clearFilter() })
        }
    }

    DemoExample("Rowspan and colspan", "Span cells with the table-level spanMethod.") {
        data class Stat(val name: String, val value: Int, val region: String, val note: String)
        val stats = listOf(
            Stat("Alice", 20, "East", "Team A"),
            Stat("Bob", 30, "East", "Team A"),
            Stat("Charlie", 40, "West", "Team B"),
            Stat("Diana", 50, "West", "Team B"),
        )
        ElTable(
            data = stats,
            columns = listOf(
                ElTableColumn("Name", property = { it.name }),
                ElTableColumn("Value", width = 100.dp, property = { it.value }, align = ElTextAlign.End),
                ElTableColumn("Region", property = { it.region }),
                ElTableColumn("Note", property = { it.note }),
            ),
            spanMethod = { _, rowIndex, columnIndex, _ ->
                if (rowIndex == 0 && columnIndex == 0) ElTableCellSpan(rowSpan = 2, colSpan = 2) else null
            },
            border = true,
        )
    }

    DemoExample("Tree data", "Nested rows with expandable branches and parent/child selection.") {
        data class Node(val id: String, val label: String, val size: Int, val children: List<Node> = emptyList())
        val tree = listOf(
            Node(
                "1", "Root", 100, listOf(
                    Node("1-1", "Child 1", 40),
                    Node("1-2", "Child 2", 60),
                ),
            ),
            Node("2", "Other", 80),
        )
        var selected by remember { mutableStateOf(setOf<Any>()) }
        ElTable(
            data = tree,
            columns = listOf(
                ElTableColumn("Name", property = { it.label }),
                ElTableColumn("Size", width = 100.dp, property = { it.size }, align = ElTextAlign.End),
            ),
            children = { it.children },
            rowKey = { it.id },
            defaultExpandAll = true,
            selection = true,
            selectedKeys = selected,
            onSelectionChange = { selected = it },
        )
        DemoHint("Checked nodes: ${selected.joinToString()}")
    }

    DemoExample("Lazy tree", "Load children on demand through the load callback.") {
        data class LazyNode(val id: String, val label: String)
        val roots = listOf(LazyNode("root", "Server"), LazyNode("leaf", "Local"))
        val scope = rememberCoroutineScope()
        ElTable(
            data = roots,
            columns = listOf(ElTableColumn("Name", property = { it.label })),
            rowKey = { it.id },
            hasChildren = { it.id == "root" },
            lazy = true,
            load = { _, resolve ->
                scope.launch {
                    delay(300)
                    resolve(listOf(LazyNode("child-1", "Database"), LazyNode("child-2", "Cache")))
                }
            },
        )
        DemoHint("Expand Server to load its children.")
    }

    DemoExample("Expandable row", "Show arbitrary detail content under a row.") {
        var expanded by remember { mutableStateOf(setOf<Any>("Alice")) }
        ElTable(
            data = rows,
            columns = listOf(
                ElTableColumn("Name", property = { it.name }),
                ElTableColumn("City", property = { it.city }),
                ElTableColumn("Status", property = { it.status }),
            ),
            rowKey = { it.name },
            expandedRowKeys = expanded,
            onExpandedChange = { expanded = it },
            expandContent = { row, _ ->
                Box(Modifier.fillMaxWidth().padding(16.dp)) {
                    BasicText(
                        text = "${row.name} lives in ${row.city} and is currently ${row.status.lowercase()}.",
                        style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textRegular),
                    )
                }
            },
        )
    }

    DemoExample("Custom index", "Use a custom index function for the index column.") {
        ElTable(
            data = rows,
            columns = listOf(
                ElTableColumn("Index", type = cn.enaium.elementplus.components.ElTableColumnType.Index, index = { it * 2 }, width = 90.dp),
                ElTableColumn("Name", property = { it.name }),
                ElTableColumn("Age", property = { it.age }),
            ),
            border = true,
        )
    }

    DemoExample("Summary row", "Sum a numeric column with a custom summary method.") {
        ElTable(
            data = rows,
            columns = listOf(
                ElTableColumn("Name", property = { it.name }),
                ElTableColumn("Age", property = { it.age }, align = ElTextAlign.End),
                ElTableColumn("City", property = { it.city }),
            ),
            showSummary = true,
            summaryMethod = { _, data -> listOf("Total", data.sumOf { it.age }.toString(), "") },
        )
    }

    DemoExample("Selectable rows", "Disable selection for some rows with the selectable predicate.") {
        var selected by remember { mutableStateOf(setOf<Any>()) }
        ElTable(
            data = rows,
            columns = listOf(
                ElTableColumn("Name", property = { it.name }),
                ElTableColumn("Age", property = { it.age }),
                ElTableColumn("City", property = { it.city }),
            ),
            rowKey = { it.name },
            selection = true,
            selectable = { row, _ -> row.age >= 30 },
            selectedKeys = selected,
            onSelectionChange = { selected = it },
        )
        DemoHint("Only rows aged 30 and above are selectable.")
    }

    DemoExample("Tooltip", "Show overflow tooltips with a custom formatter.") {
        ElTable(
            data = rows,
            columns = listOf(
                ElTableColumn(
                    "Description",
                    width = 260.dp,
                    property = { "${it.name} is a resident of ${it.city}. This description is intentionally long to demonstrate overflow tooltips." },
                    showOverflowTooltip = true,
                    tooltipFormatter = { data -> "Row ${data.rowIndex + 1}: ${data.cellValue}" },
                ),
                ElTableColumn("Age", width = 80.dp, property = { it.age }),
            ),
            showOverflowTooltip = false,
        )
    }

    DemoExample("Table with status", "Customize cells to render status tags.") {
        data class Order(val id: Int, val name: String, val status: String)
        val orders = listOf(
            Order(1, "Order A", "Success"),
            Order(2, "Order B", "Pending"),
            Order(3, "Order C", "Failed"),
        )
        ElTable(
            data = orders,
            columns = listOf(
                ElTableColumn("ID", width = 80.dp, property = { it.id }),
                ElTableColumn("Name", width = 180.dp, property = { it.name }),
                ElTableColumn(
                    "Status",
                    width = 120.dp,
                    cell = { row, _ ->
                        ElTag(
                            text = row.status,
                            type = when (row.status) {
                                "Success" -> ElType.Success
                                "Pending" -> ElType.Warning
                                else -> ElType.Danger
                            },
                            size = ElSize.Small,
                        )
                    },
                ),
            ),
        )
    }
    DemoExample("Check strictly", "Compare independent selection with cascading tree selection.") {
        data class StrictNode(val id: Int, val label: String, val children: List<StrictNode> = emptyList())
        val tree = listOf(
            StrictNode(1, "Root 1", listOf(StrictNode(11, "Child 1-1"), StrictNode(12, "Child 1-2"))),
            StrictNode(2, "Root 2"),
        )
        var strict by remember { mutableStateOf(false) }
        var selected by remember { mutableStateOf(setOf<Any>()) }
        DemoRow {
            ElButton(text = if (strict) "check-strictly = true" else "check-strictly = false", onClick = { strict = !strict })
            DemoHint("Selected: ${selected.size}")
        }
        ElTable(
            data = tree,
            columns = listOf(
                ElTableColumn("Name", property = { it.label }),
            ),
            children = { it.children },
            rowKey = { it.id },
            defaultExpandAll = true,
            selection = true,
            checkStrictly = strict,
            selectedKeys = selected,
            onSelectionChange = { selected = it },
        )
    }

    DemoExample("Custom header", "Put a search input in a column header and filter the data.") {
        data class HeaderUser(val date: String, val name: String, val address: String)
        val users = remember {
            listOf(
                HeaderUser("2016-05-03", "Tom", "No. 189, Grove St, Los Angeles"),
                HeaderUser("2016-05-02", "John", "No. 189, Grove St, Los Angeles"),
                HeaderUser("2016-05-04", "Morgan", "No. 189, Grove St, Los Angeles"),
                HeaderUser("2016-05-01", "Jessy", "No. 189, Grove St, Los Angeles"),
            )
        }
        var search by remember { mutableStateOf("") }
        var edited by remember { mutableStateOf<String?>(null) }
        val filtered = users.filter { search.isBlank() || it.name.contains(search, ignoreCase = true) }
        ElTable(
            data = filtered,
            columns = listOf(
                ElTableColumn("Date", width = 130.dp, property = { it.date }),
                ElTableColumn("Name", width = 130.dp, property = { it.name }),
                ElTableColumn(
                    "",
                    minWidth = 280.dp,
                    align = ElTextAlign.End,
                    header = {
                        ElInput(
                            value = search,
                            onValueChange = { search = it },
                            size = ElSize.Small,
                            placeholder = "Type to search",
                        )
                    },
                    cell = { row, _ ->
                        DemoRow {
                            ElButton(text = "Edit", size = ElSize.Small, onClick = { edited = row.name })
                            ElButton(text = "Delete", size = ElSize.Small, type = ElType.Danger, onClick = {
                                edited = "Deleted ${row.name}"
                            })
                        }
                    },
                ),
            ),
        )
        DemoHint(edited?.let { "Action: $it" } ?: "Filtering by name")
    }

    DemoExample("Fixed column and group header", "Pin a grouped column to the right.") {
        data class Delivery(val date: String, val name: String, val zip: String, val state: String, val city: String, val address: String)
        val rows = remember {
            (1..7).map {
                Delivery("2016-05-0${(it % 8) + 1}", "Tom", "CA 90036", "California", "Los Angeles", "No. 189, Grove St, Los Angeles")
            }
        }
        ElTable(
            data = rows,
            columns = listOf(
                ElTableColumn("Date", property = { it.date }),
                ElTableColumn("Name", width = 100.dp, property = { it.name }),
                ElTableColumn("Zip", width = 120.dp, property = { it.zip }),
                ElTableColumn(
                    "Address Info",
                    fixed = ElTableFixed.Right,
                    children = listOf(
                        ElTableColumn("State", width = 120.dp, property = { it.state }),
                        ElTableColumn("City", width = 120.dp, property = { it.city }),
                        ElTableColumn("Address", minWidth = 200.dp, property = { it.address }),
                    ),
                ),
            ),
            height = 250.dp,
            border = true,
        )
    }

    DemoExample("Fixed column and header", "Keep both edges and the header pinned while scrolling.") {
        ElTable(
            data = rows + rows,
            columns = listOf(
                ElTableColumn("Date", width = 150.dp, fixed = ElTableFixed.Left, property = { it.name }),
                ElTableColumn("Name", width = 120.dp, property = { it.name }),
                ElTableColumn("City", width = 120.dp, property = { it.city }),
                ElTableColumn("Address", width = 420.dp, property = { it.city }),
                ElTableColumn(
                    "Operations",
                    width = 120.dp,
                    fixed = ElTableFixed.Right,
                    cell = { row, _ -> ElText("Edit ${row.name}", type = ElType.Primary) },
                ),
            ),
            height = 240.dp,
            border = true,
        )
    }

    DemoExample("Fixed header with fluid header", "Use maxHeight and add or remove rows above a fluid table.") {
        data class FluidRow(val date: String, val name: String, val state: String, val city: String, val address: String, val zip: String)
        var rows by remember {
            mutableStateOf(
                listOf(
                    FluidRow("2016-05-01", "Tom", "California", "Los Angeles", "No. 189, Grove St", "CA 90036"),
                    FluidRow("2016-05-02", "Tom", "California", "Los Angeles", "No. 189, Grove St", "CA 90036"),
                    FluidRow("2016-05-03", "Tom", "California", "Los Angeles", "No. 189, Grove St", "CA 90036"),
                ),
            )
        }
        ElTable(
            data = rows,
            columns = listOf(
                ElTableColumn("Date", width = 150.dp, fixed = ElTableFixed.Left, property = { it.date }),
                ElTableColumn("Name", width = 120.dp, property = { it.name }),
                ElTableColumn("State", width = 120.dp, property = { it.state }),
                ElTableColumn("City", width = 120.dp, property = { it.city }),
                ElTableColumn("Address", width = 300.dp, property = { it.address }),
                ElTableColumn("Zip", width = 120.dp, property = { it.zip }),
                ElTableColumn(
                    "Operations",
                    width = 120.dp,
                    fixed = ElTableFixed.Right,
                    cell = { row, _ ->
                        ElButton(text = "Remove", textButton = true, size = ElSize.Small, onClick = {
                            rows = rows - row
                        })
                    },
                ),
            ),
            maxHeight = 250.dp,
            border = true,
        )
        ElButton(
            text = "Add Item",
            onClick = {
                rows = rows + FluidRow("2016-05-04", "Tom", "California", "Los Angeles", "No. 189, Grove St", "CA 90036")
            },
        )
    }

    DemoExample("Table layout", "Switch between fixed and auto table layout.") {
        var layout by remember { mutableStateOf(ElTableLayout.Fixed) }
        DemoRow {
            ElButton(text = if (layout == ElTableLayout.Fixed) "layout: fixed" else "layout: auto", onClick = {
                layout = if (layout == ElTableLayout.Fixed) ElTableLayout.Auto else ElTableLayout.Fixed
            })
        }
        ElTable(
            data = rows,
            columns = listOf(
                ElTableColumn("Date", property = { it.name }),
                ElTableColumn("Name", property = { it.name }),
                ElTableColumn("City", property = { it.city }),
            ),
            tableLayout = layout,
            border = true,
        )
    }

}
