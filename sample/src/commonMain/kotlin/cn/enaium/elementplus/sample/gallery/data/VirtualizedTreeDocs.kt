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
fun VirtualizedTreeDocs() {
    val data = remember {
        (1..20).map { group ->
            ElTreeNode(
                "group-$group",
                "Group $group",
                (1..20).map { item -> ElTreeNode("group-$group-item-$item", "Item $group-$item") },
            )
        }
    }
    var selected by remember { mutableStateOf<String?>(null) }
    DemoExample("Basic usage", "A large tree is rendered lazily.") {
        ElVirtualizedTree(
            data = data,
            selectedKey = selected,
            onSelectedChange = { selected = it },
            defaultExpandAll = true,
            height = 280.dp,
            modifier = Modifier.width(320.dp),
        )
        DemoHint("Selected: ${selected ?: "-"}")
    }
    DemoExample("Checkbox and filter", "Virtualized trees support checkboxes and filtering.") {
        var checked by remember { mutableStateOf(emptySet<String>()) }
        var filter by remember { mutableStateOf("") }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElInput(value = filter, onValueChange = { filter = it }, placeholder = "Filter", modifier = Modifier.width(260.dp))
            ElVirtualizedTree(
                data = data,
                checkedKeys = checked,
                onCheckedChange = { checked = it },
                showCheckbox = true,
                filterText = filter,
                defaultExpandAll = true,
                height = 280.dp,
                modifier = Modifier.width(320.dp),
            )
        }
    }

    DemoExample("Custom node content", "Render custom labels in the virtualized tree.") {
        var selected by remember { mutableStateOf<String?>("1-1") }
        ElVirtualizedTree(
            data = data,
            selectedKey = selected,
            onSelectedChange = { selected = it },
            defaultExpandAll = true,
            nodeContent = { node, isSelected ->
                BasicText(
                    text = "• ${node.label}",
                    style = LocalElTheme.current.typography.base.copy(
                        color = if (isSelected) LocalElTheme.current.colors.primary else LocalElTheme.current.colors.textRegular,
                    ),
                )
            },
            modifier = Modifier.width(300.dp),
        )
    }

    DemoExample("Disabled checkbox", "Disabled nodes cannot be checked.") {
        var checked by remember { mutableStateOf(emptySet<String>()) }
        ElVirtualizedTree(
            data = data.map { group ->
                group.copy(children = group.children.mapIndexed { index, node ->
                    if (index == 0) node.copy(disabled = true) else node
                })
            },
            checkedKeys = checked,
            onCheckedChange = { checked = it },
            showCheckbox = true,
            defaultExpandAll = true,
            height = 280.dp,
            modifier = Modifier.width(320.dp),
        )
    }
    DemoExample("Default expanded and checked", "Initialize expanded and checked keys.") {
        var checked by remember { mutableStateOf(setOf("group-1-item-1", "group-1-item-2")) }
        ElVirtualizedTree(
            data = data,
            checkedKeys = checked,
            onCheckedChange = { checked = it },
            showCheckbox = true,
            defaultExpandedKeys = setOf("group-1"),
            height = 280.dp,
            modifier = Modifier.width(320.dp),
        )
    }
}
