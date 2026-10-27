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
fun TreeDocs() {
    val data = treeData()
    DemoExample("Basic usage", "Select and expand tree nodes.") {
        var selected by remember { mutableStateOf<String?>("1-1") }
        ElTree(
            data = data,
            selectedKey = selected,
            onSelectedChange = { selected = it },
            defaultExpandAll = true,
            modifier = Modifier.width(280.dp),
        )
        DemoHint("Selected: ${selected ?: "-"}")
    }
    DemoExample("Checkbox", "Check nodes with automatic parent/child state.") {
        var checked by remember { mutableStateOf(setOf("1-1")) }
        ElTree(
            data = data,
            checkedKeys = checked,
            onCheckedChange = { checked = it },
            showCheckbox = true,
            defaultExpandAll = true,
            modifier = Modifier.width(300.dp),
        )
    }
    DemoExample("Filter", "Filter nodes by label.") {
        var filter by remember { mutableStateOf("") }
        var selected by remember { mutableStateOf<String?>("1-1") }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElInput(value = filter, onValueChange = { filter = it }, placeholder = "Filter nodes", clearable = true, modifier = Modifier.width(260.dp))
            ElTree(
                data = data,
                filterText = filter,
                selectedKey = selected,
                onSelectedChange = { selected = it },
                modifier = Modifier.width(300.dp),
            )
        }
    }
    DemoExample("Disabled nodes", "Disabled nodes cannot be selected or checked.") {
        ElTree(
            data = data,
            defaultExpandAll = true,
            selectedKey = "3-1",
            modifier = Modifier.width(300.dp),
        )
    }

    DemoExample("Custom node", "Render custom content for each tree node.") {
        var selected by remember { mutableStateOf<String?>("1-1") }
        ElTree(
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

    DemoExample("Default expanded and checked", "Initialize expanded and checked keys.") {
        var checked by remember { mutableStateOf(setOf("1-1", "1-2")) }
        ElTree(
            data = data,
            checkedKeys = checked,
            onCheckedChange = { checked = it },
            showCheckbox = true,
            defaultExpandedKeys = setOf("1"),
            modifier = Modifier.width(300.dp),
        )
    }
    DemoExample("Disabled checkbox", "Disabled nodes cannot be checked.") {
        ElTree(
            data = data.map { node ->
                if (node.key == "1-2") node.copy(disabled = true) else node
            },
            selectedKey = null,
            showCheckbox = true,
            defaultExpandAll = true,
            modifier = Modifier.width(300.dp),
        )
    }

    DemoExample("Accordion", "Only one branch stays expanded at a time.") {
        var selected by remember { mutableStateOf<String?>(null) }
        ElTree(
            data = data,
            selectedKey = selected,
            onSelectedChange = { selected = it },
            accordion = true,
            modifier = Modifier.width(300.dp),
        )
    }
}

@Composable
private fun treeData(): List<ElTreeNode> = listOf(
    ElTreeNode(
        "1", "Guidelines", listOf(
            ElTreeNode("1-1", "Design principles"),
            ElTreeNode("1-2", "Colors"),
            ElTreeNode("1-3", "Typography"),
        ),
    ),
    ElTreeNode(
        "2", "Components", listOf(
            ElTreeNode("2-1", "Buttons"),
            ElTreeNode("2-2", "Forms"),
            ElTreeNode("2-3", "Data display"),
        ),
    ),
    ElTreeNode(
        "3", "Resources", listOf(
            ElTreeNode("3-1", "Icons", disabled = true),
            ElTreeNode("3-2", "Fonts"),
        ),
    ),
)
