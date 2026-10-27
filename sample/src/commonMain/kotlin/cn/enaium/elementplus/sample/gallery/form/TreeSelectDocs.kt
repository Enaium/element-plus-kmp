package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.ElTag
import cn.enaium.elementplus.components.ElTagEffect
import cn.enaium.elementplus.components.ElTreeNode
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.ElMessageType
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber

@Composable
fun TreeSelectDocs() {
    val treeData = listOf(
        ElTreeNode("1", "Guidelines", listOf(ElTreeNode("1-1", "Principles"), ElTreeNode("1-2", "Colors"))),
        ElTreeNode("2", "Components", listOf(ElTreeNode("2-1", "Button"), ElTreeNode("2-2", "Input"))),
        ElTreeNode("3", "Resources", listOf(ElTreeNode("3-1", "Icons"), ElTreeNode("3-2", "Fonts"))),
    )
    val nestedData = listOf(
        ElTreeNode(
            "1",
            "Level one 1",
            listOf(
                ElTreeNode("1-1", "Level two 1-1", listOf(ElTreeNode("1-1-1", "Level three 1-1-1"))),
            ),
        ),
        ElTreeNode(
            "2",
            "Level one 2",
            listOf(
                ElTreeNode("2-1", "Level two 2-1", listOf(ElTreeNode("2-1-1", "Level three 2-1-1"))),
                ElTreeNode("2-2", "Level two 2-2", listOf(ElTreeNode("2-2-1", "Level three 2-2-1"))),
            ),
        ),
        ElTreeNode(
            "3",
            "Level one 3",
            listOf(
                ElTreeNode("3-1", "Level two 3-1", listOf(ElTreeNode("3-1-1", "Level three 3-1-1"))),
                ElTreeNode("3-2", "Level two 3-2", listOf(ElTreeNode("3-2-1", "Level three 3-2-1"))),
            ),
        ),
    )
    var selected by remember { mutableStateOf<String?>("1-1") }
    var filtered by remember { mutableStateOf<String?>(null) }
    DemoExample("Basic usage", "Select a node from a tree dropdown.") {
        ElTreeSelect(
            selectedKey = selected,
            onSelectedChange = { selected = it },
            data = treeData,
            placeholder = "Select a node",
            defaultExpandAll = true,
            modifier = Modifier.width(260.dp),
        )
        DemoHint("Selected: ${selected ?: "-"}")
    }
    DemoExample("Filterable", "Search the tree by label.") {
        ElTreeSelect(
            selectedKey = filtered,
            onSelectedChange = { filtered = it },
            data = treeData,
            placeholder = "Type to filter",
            filterable = true,
            modifier = Modifier.width(260.dp),
        )
    }
    DemoExample("Disabled", "Disable the tree select.") {
        ElTreeSelect(
            selectedKey = "2-2",
            onSelectedChange = {},
            data = treeData,
            disabled = true,
            modifier = Modifier.width(260.dp),
        )
    }

    DemoExample(
        "Multiple selection",
        "Select several nodes; the selection is rendered as removable tags.",
    ) {
        var multipleValue by remember { mutableStateOf(listOf("2-1")) }
        ElTreeSelect(
            selectedKey = null,
            onSelectedChange = {},
            data = nestedData,
            multiple = true,
            selectedKeys = multipleValue,
            onSelectedKeysChange = { multipleValue = it },
            defaultExpandAll = true,
            placeholder = "Select nodes",
            modifier = Modifier.width(280.dp),
        )
        DemoHint("Selected keys: ${multipleValue.joinToString(", ").ifEmpty { "-" }}")
    }

    DemoExample(
        "Multiple with checkboxes and collapsible tags",
        "Show checkboxes plus parent/child checking, and collapse the tags beyond two items.",
    ) {
        var checkedValue by remember { mutableStateOf(emptyList<String>()) }
        ElTreeSelect(
            selectedKey = null,
            onSelectedChange = {},
            data = nestedData,
            multiple = true,
            showCheckbox = true,
            selectedKeys = checkedValue,
            onSelectedKeysChange = { checkedValue = it },
            maxCollapseTags = 2,
            defaultExpandAll = true,
            placeholder = "Select nodes",
            modifier = Modifier.width(280.dp),
        )
        DemoHint("${checkedValue.size} node(s) checked")
    }

    DemoExample(
        "Check strictly and check on click node",
        "check-strictly disables parent/child linking, and clicking a row toggles its checkbox.",
    ) {
        var strictValue by remember { mutableStateOf(emptyList<String>()) }
        ElTreeSelect(
            selectedKey = null,
            onSelectedChange = {},
            data = nestedData,
            multiple = true,
            showCheckbox = true,
            checkStrictly = true,
            checkOnClickNode = true,
            selectedKeys = strictValue,
            onSelectedKeysChange = { strictValue = it },
            defaultExpandAll = true,
            placeholder = "Select nodes",
            modifier = Modifier.width(280.dp),
        )
        DemoHint("Selected keys: ${strictValue.joinToString(", ").ifEmpty { "-" }}")
    }

    DemoExample(
        "Lazy load",
        "Load children on expand instead of providing the full tree up front.",
    ) {
        var lazyValue by remember { mutableStateOf<String?>(null) }
        val lazyData = remember {
            listOf(
                ElTreeNode("lazy-1", "Level one 1"),
                ElTreeNode("lazy-2", "Level one 2"),
            )
        }
        ElTreeSelect(
            selectedKey = lazyValue,
            onSelectedChange = { lazyValue = it },
            data = lazyData,
            lazy = true,
            load = { node ->
                delay(400)
                listOf(
                    ElTreeNode("${node.key}-1", "${node.label} child"),
                    ElTreeNode("${node.key}-2", "${node.label} leaf", leaf = true),
                )
            },
            placeholder = "Expand to load",
            modifier = Modifier.width(260.dp),
        )
        DemoHint("Selected: ${lazyValue ?: "-"}")
    }

    DemoExample(
        "Accordion tree",
        "Only one branch stays expanded at a time.",
    ) {
        var accordionValue by remember { mutableStateOf<String?>(null) }
        ElTreeSelect(
            selectedKey = accordionValue,
            onSelectedChange = { accordionValue = it },
            data = nestedData,
            accordion = true,
            defaultExpandedKeys = setOf("1"),
            placeholder = "Single expanded branch",
            modifier = Modifier.width(260.dp),
        )
    }

    DemoExample(
        "Custom node content (default slot)",
        "Render the node label through the nodeContent slot.",
    ) {
        var slotValue by remember { mutableStateOf<String?>(null) }
        ElTreeSelect(
            selectedKey = slotValue,
            onSelectedChange = { slotValue = it },
            data = nestedData,
            defaultExpandAll = true,
            nodeContent = { node ->
                BasicText(
                    text = "${node.label} (suffix)",
                    style = LocalElTheme.current.typography.base.copy(
                        color = LocalElTheme.current.colors.textSecondary,
                    ),
                )
            },
            placeholder = "Select a node",
            modifier = Modifier.width(280.dp),
        )
    }

    DemoExample(
        "Prop alias and node-key",
        "Use ElTreeSelectProps to alias key, label and children fields of a raw model.",
    ) {
        var aliasValue by remember { mutableStateOf(listOf("2")) }
        ElTreeSelect(
            selectedKey = null,
            onSelectedChange = {},
            data = treeAliasNodes.toTreeNodes(
                ElTreeSelectProps(
                    key = { it.id.toString() },
                    label = { it.desc },
                    children = { it.childNodes },
                ),
            ),
            multiple = true,
            showCheckbox = true,
            selectedKeys = aliasValue,
            onSelectedKeysChange = { aliasValue = it },
            defaultExpandAll = true,
            modifier = Modifier.width(280.dp),
        )
        DemoHint("Selected ids: ${aliasValue.joinToString(", ").ifEmpty { "-" }}")
    }
}

private data class TreeSelectAliasNode(
    val id: Int,
    val desc: String,
    val childNodes: List<TreeSelectAliasNode> = emptyList(),
)

private val treeAliasNodes = listOf(
    TreeSelectAliasNode(
        id = 1,
        desc = "Level one 1",
        childNodes = listOf(
            TreeSelectAliasNode(2, "Level two 1-1"),
            TreeSelectAliasNode(3, "Level two 1-2"),
        ),
    ),
    TreeSelectAliasNode(
        id = 4,
        desc = "Level one 2",
        childNodes = listOf(TreeSelectAliasNode(5, "Level two 2-1")),
    ),
)
