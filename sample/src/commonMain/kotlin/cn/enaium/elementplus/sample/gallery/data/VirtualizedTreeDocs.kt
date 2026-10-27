package cn.enaium.elementplus.sample.gallery.data

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElVirtualizedTree
import cn.enaium.elementplus.components.ElTreeNode
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun VirtualizedTreeDocs() {
    val data = remember { virtualTreeData() }
    val colors = LocalElTheme.current.colors
    val theme = LocalElTheme.current

    DemoExample("Basic usage", "A large tree rendered with LazyColumn.") {
        ElVirtualizedTree(
            data = data,
            height = 300.dp,
            modifier = Modifier.width(380.dp),
        )
    }

    DemoExample("Selectable", "Highlight and select a node.") {
        var selected by remember { mutableStateOf<String?>("g-1-1") }
        ElVirtualizedTree(
            data = data,
            selectedKey = selected,
            onSelectedChange = { selected = it },
            highlightCurrent = true,
            expandOnClickNode = false,
            defaultExpandedKeys = setOf("g-1"),
            height = 300.dp,
            modifier = Modifier.width(380.dp),
        )
        DemoHint("Selected: ${selected ?: "-"}")
    }

    DemoExample("Disabled checkbox", "Disabled nodes cannot be checked.") {
        var checked by remember { mutableStateOf(emptySet<String>()) }
        val disabledData = remember(data) {
            data.map { group ->
                group.copy(children = group.children.map { item ->
                    if (item.key.endsWith("-2")) item.copy(disabled = true) else item
                })
            }
        }
        ElVirtualizedTree(
            data = disabledData,
            checkedKeys = checked,
            onCheckedChange = { checked = it },
            showCheckbox = true,
            defaultExpandedKeys = setOf("g-1"),
            height = 300.dp,
            modifier = Modifier.width(380.dp),
        )
    }

    DemoExample("Default expanded and default checked", "Initialize checked and expanded keys.") {
        ElVirtualizedTree(
            data = data,
            showCheckbox = true,
            defaultExpandedKeys = setOf("g-1"),
            defaultCheckedKeys = setOf("g-1-1"),
            height = 300.dp,
            modifier = Modifier.width(380.dp),
        )
    }

    DemoExample("Custom node content", "Render a custom prefix and leaf styling.") {
        ElVirtualizedTree(
            data = data,
            defaultExpandedKeys = setOf("g-1"),
            nodeContent = { node, isSelected ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicText(
                        text = "[ElementPlus] ",
                        style = theme.typography.base.copy(
                            color = if (node.isLeaf) colors.success else colors.primary,
                        ),
                    )
                    BasicText(
                        text = node.label,
                        style = theme.typography.base.copy(
                            color = if (isSelected) colors.primary else colors.textRegular,
                        ),
                    )
                }
            },
            height = 300.dp,
            modifier = Modifier.width(380.dp),
        )
    }

    DemoExample("Custom node class", "Apply a custom modifier to penultimate-level nodes.") {
        ElVirtualizedTree(
            data = data,
            showCheckbox = true,
            defaultExpandedKeys = setOf("g-1"),
            expandOnClickNode = false,
            nodeModifier = { node ->
                if (node.key.endsWith("-3")) {
                    Modifier.background(colors.primary.copy(alpha = 0.10f), RoundedCornerShape(4.dp))
                } else {
                    Modifier
                }
            },
            height = 300.dp,
            modifier = Modifier.width(380.dp),
        )
    }

    DemoExample("Custom node icon", "Choose folder/document icons based on node type and expansion.") {
        ElVirtualizedTree(
            data = data,
            defaultExpandedKeys = setOf("g-1"),
            nodeIcon = { node, expanded -> customIcon(node.isLeaf, expanded) },
            height = 300.dp,
            modifier = Modifier.width(380.dp),
        )
    }

    DemoExample("Tree node filtering", "Filter nodes with a caller-provided predicate.") {
        var filter by remember { mutableStateOf("") }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElInput(
                value = filter,
                onValueChange = { filter = it },
                placeholder = "Filter keyword",
                clearable = true,
                modifier = Modifier.width(260.dp),
            )
            ElVirtualizedTree(
                data = data,
                filterText = filter,
                filterNodeMethod = { query, node -> node.label.contains(query, ignoreCase = true) },
                height = 300.dp,
                modifier = Modifier.width(380.dp),
            )
        }
    }
}

private fun customIcon(isLeaf: Boolean, expanded: Boolean): ImageVector =
    if (isLeaf) ElIcons.Document else ElIcons.Folder

private fun virtualTreeData(): List<ElTreeNode> = (1..30).map { group ->
    ElTreeNode(
        "g-$group",
        "Group $group",
        children = (1..8).map { item ->
            ElTreeNode(
                "g-$group-$item",
                "Item $group-$item",
                children = (1..2).map { leaf ->
                    ElTreeNode("g-$group-$item-$leaf", "Leaf $group-$item-$leaf")
                },
            )
        },
    )
}
