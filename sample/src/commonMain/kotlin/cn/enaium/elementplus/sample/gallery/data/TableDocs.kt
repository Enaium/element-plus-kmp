package cn.enaium.elementplus.sample.gallery.data

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.ElMessageType
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElSwitch
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElTextAlign
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

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
                ElTableColumn("Name", property = { it.name }, sortable = true),
                ElTableColumn("Age", property = { it.age }, sortable = true, align = ElTextAlign.End),
                ElTableColumn("City", property = { it.city }),
            ),
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
        DemoHint("Selected: ${selected.joinToString()}")
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
}
