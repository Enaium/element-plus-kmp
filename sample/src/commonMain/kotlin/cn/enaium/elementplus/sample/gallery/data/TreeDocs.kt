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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElTree
import cn.enaium.elementplus.components.ElTreeNode
import cn.enaium.elementplus.components.ElTreeNodeDropType
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.sample.gallery.DemoRow
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TreeDocs() {
    val data = remember { treeBasicData() }
    val colors = LocalElTheme.current.colors

    DemoExample("Basic usage", "Click a node and expand branches.") {
        var clicked by remember { mutableStateOf<String?>(null) }
        ElTree(
            data = data,
            onNodeClick = { clicked = it.label },
            modifier = Modifier.width(340.dp),
        )
        DemoHint("Clicked: ${clicked ?: "-"}")
    }

    DemoExample("Selectable", "Highlight the current node and select it on click.") {
        var selected by remember { mutableStateOf<String?>("1-1") }
        ElTree(
            data = data,
            selectedKey = selected,
            onSelectedChange = { selected = it },
            highlightCurrent = true,
            expandOnClickNode = false,
            defaultExpandAll = true,
            modifier = Modifier.width(340.dp),
        )
        DemoHint("Selected: ${selected ?: "-"}")
    }

    DemoExample("Custom leaf node in lazy mode", "A node returned with leaf=true does not show an expand arrow.") {
        val scope = rememberCoroutineScope()
        ElTree(
            data = emptyList(),
            lazy = true,
            showCheckbox = true,
            modifier = Modifier.width(340.dp),
            load = { node, resolve, _ ->
                if (node == null) {
                    resolve(listOf(ElTreeNode("region", "region")))
                } else {
                    scope.launch {
                        delay(350)
                        when (node.key) {
                            "region" -> resolve(
                                listOf(
                                    ElTreeNode("leaf", "leaf", leaf = true),
                                    ElTreeNode("zone", "zone"),
                                ),
                            )
                            else -> resolve(
                                listOf(ElTreeNode("${node.key}-leaf", "${node.key} leaf", leaf = true)),
                            )
                        }
                    }
                }
            },
        )
        DemoHint("Expand region; leaf has no arrow.")
    }

    DemoExample("Lazy loading multiple times", "The load callback rejects three times before succeeding.") {
        var attempts by remember { mutableIntStateOf(0) }
        val scope = rememberCoroutineScope()
        ElTree(
            data = emptyList(),
            lazy = true,
            modifier = Modifier.width(340.dp),
            load = { node, resolve, reject ->
                if (node == null) {
                    resolve(listOf(ElTreeNode("region", "region")))
                } else {
                    scope.launch {
                        delay(450)
                        when (node.key) {
                            "region" -> {
                                attempts++
                                if (attempts <= 3) reject()
                                else resolve(
                                    listOf(
                                        ElTreeNode("zone1", "zone1", leaf = true),
                                        ElTreeNode("zone2", "zone2", leaf = true),
                                        ElTreeNode("zone3", "zone3", leaf = true),
                                    ),
                                )
                            }
                            else -> resolve(emptyList())
                        }
                    }
                }
            },
        )
        DemoHint("Click region again to retry. Attempts: $attempts")
    }

    DemoExample("Disabled checkbox", "Disabled nodes and disabled parents keep their checkbox unavailable.") {
        var checked by remember { mutableStateOf(emptySet<String>()) }
        ElTree(
            data = treeDisabledData(),
            checkedKeys = checked,
            onCheckedChange = { checked = it },
            showCheckbox = true,
            defaultExpandAll = true,
            modifier = Modifier.width(360.dp),
        )
    }

    DemoExample("Default expanded and default checked", "Initialize checked and expanded keys without external state.") {
        ElTree(
            data = treeDefaultData(),
            showCheckbox = true,
            defaultExpandedKeys = setOf("2", "3"),
            defaultCheckedKeys = setOf("5"),
            modifier = Modifier.width(340.dp),
        )
    }

    DemoExample("Checking tree nodes", "Read and replace the checked keys from custom actions.") {
        var checked by remember { mutableStateOf(setOf("5", "9")) }
        var hint by remember { mutableStateOf("") }
        ElTree(
            data = treeDefaultData(),
            checkedKeys = checked,
            onCheckedChange = { checked = it },
            showCheckbox = true,
            defaultExpandAll = true,
            highlightCurrent = true,
            modifier = Modifier.width(340.dp),
        )
        DemoRow {
            ElButton(text = "Get checked", size = ElSize.Small, onClick = {
                hint = checked.sorted().joinToString()
            })
            ElButton(text = "Set node 5", size = ElSize.Small, onClick = {
                checked = setOf("5")
                hint = "checked = [5]"
            })
            ElButton(text = "Set keys 3", size = ElSize.Small, onClick = {
                checked = setOf("3", "7", "8")
                hint = "checked = [3, 7, 8]"
            })
            ElButton(text = "Reset", size = ElSize.Small, onClick = {
                checked = emptySet()
                hint = "checked = []"
            })
        }
        DemoHint(if (hint.isBlank()) "Checked keys: ${checked.sorted().joinToString()}" else hint)
    }

    DemoExample("Custom node content", "Append and delete nodes from a custom node renderer.") {
        var customData by remember { mutableStateOf(treeBasicData()) }
        var nextId by remember { mutableIntStateOf(1000) }
        ElTree(
            data = customData,
            showCheckbox = true,
            defaultExpandAll = true,
            expandOnClickNode = false,
            modifier = Modifier.width(420.dp),
            nodeContent = { node, _ ->
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    BasicText(
                        text = node.label,
                        modifier = Modifier.weight(1f),
                        style = LocalElTheme.current.typography.base.copy(
                            color = LocalElTheme.current.colors.textRegular,
                        ),
                    )
                    ElButton(text = "Append", link = true, size = ElSize.Small, onClick = {
                        val id = "new-${nextId++}"
                        customData = appendNode(customData, node.key, ElTreeNode(id, "new node"))
                    })
                    ElButton(
                        text = "Delete",
                        link = true,
                        type = ElType.Danger,
                        size = ElSize.Small,
                        onClick = { customData = removeNode(customData, node.key) },
                    )
                }
            },
        )
    }

    DemoExample("Custom node class", "Apply a custom modifier to penultimate-level nodes.") {
        ElTree(
            data = treeClassData(),
            showCheckbox = true,
            defaultExpandAll = true,
            expandOnClickNode = false,
            nodeModifier = { node ->
                if (node.key in setOf("2", "3", "4")) {
                    Modifier.background(colors.primary.copy(alpha = 0.10f), RoundedCornerShape(4.dp))
                } else {
                    Modifier
                }
            },
            modifier = Modifier.width(360.dp),
        )
    }

    DemoExample("Tree node filtering", "Filter with a custom predicate.") {
        var filter by remember { mutableStateOf("") }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElInput(
                value = filter,
                onValueChange = { filter = it },
                placeholder = "Filter keyword",
                clearable = true,
                modifier = Modifier.width(260.dp),
            )
            ElTree(
                data = data,
                filterText = filter,
                filterNodeMethod = { query, node -> node.label.contains(query, ignoreCase = true) },
                defaultExpandAll = true,
                modifier = Modifier.width(340.dp),
            )
        }
    }

    DemoExample("Accordion", "Expanding a sibling collapses the previous sibling branch.") {
        ElTree(
            data = data,
            accordion = true,
            modifier = Modifier.width(340.dp),
        )
        DemoHint("Only one sibling branch stays open at each level.")
    }

    DemoExample("Draggable", "Drag a row to reorder it; drag right to drop inside a node.") {
        var dragData by remember { mutableStateOf(treeDraggableData()) }
        var lastDrop by remember { mutableStateOf("") }
        ElTree(
            data = dragData,
            onDataChange = { dragData = it },
            draggable = true,
            defaultExpandAll = true,
            allowDrag = { node -> !node.label.contains("Level three 3-1-1") },
            allowDrop = { _, drop, type ->
                if (drop.label == "Level two 3-1") type != ElTreeNodeDropType.Inner else true
            },
            onNodeDrop = { dragging, drop, type ->
                lastDrop = "${dragging.label} -> ${drop.label} (${type.name})"
            },
            modifier = Modifier.width(400.dp),
        )
        DemoHint(lastDrop.ifBlank { "Drag a node to reorder it." })
    }
}

private fun treeBasicData(): List<ElTreeNode> = listOf(
    ElTreeNode(
        "1", "Level one 1", listOf(
            ElTreeNode(
                "1-1", "Level two 1-1", listOf(
                    ElTreeNode("1-1-1", "Level three 1-1-1"),
                ),
            ),
        ),
    ),
    ElTreeNode(
        "2", "Level one 2", listOf(
            ElTreeNode("2-1", "Level two 2-1", listOf(ElTreeNode("2-1-1", "Level three 2-1-1"))),
            ElTreeNode("2-2", "Level two 2-2", listOf(ElTreeNode("2-2-1", "Level three 2-2-1"))),
        ),
    ),
    ElTreeNode(
        "3", "Level one 3", listOf(
            ElTreeNode("3-1", "Level two 3-1", listOf(ElTreeNode("3-1-1", "Level three 3-1-1"))),
            ElTreeNode("3-2", "Level two 3-2", listOf(ElTreeNode("3-2-1", "Level three 3-2-1"))),
        ),
    ),
)

private fun treeDefaultData(): List<ElTreeNode> = listOf(
    ElTreeNode("1", "Level one 1", listOf(ElTreeNode("4", "Level two 1-1", listOf(
        ElTreeNode("9", "Level three 1-1-1"),
        ElTreeNode("10", "Level three 1-1-2"),
    )))),
    ElTreeNode("2", "Level one 2", listOf(
        ElTreeNode("5", "Level two 2-1"),
        ElTreeNode("6", "Level two 2-2"),
    )),
    ElTreeNode("3", "Level one 3", listOf(
        ElTreeNode("7", "Level two 3-1"),
        ElTreeNode("8", "Level two 3-2"),
    )),
)

private fun treeDisabledData(): List<ElTreeNode> = listOf(
    ElTreeNode(
        "1", "Level one 1", listOf(
            ElTreeNode(
                "3", "Level two 2-1", listOf(
                    ElTreeNode("4", "Level three 3-1-1"),
                    ElTreeNode("5", "Level three 3-1-2", disabled = true),
                ),
            ),
            ElTreeNode(
                "2", "Level two 2-2", listOf(
                    ElTreeNode("6", "Level three 3-2-1"),
                    ElTreeNode("7", "Level three 3-2-2", disabled = true),
                ), disabled = true,
            ),
        ),
    ),
)

private fun treeClassData(): List<ElTreeNode> = listOf(
    ElTreeNode("1", "Level one 1", listOf(
        ElTreeNode("4", "Level two 1-1", listOf(
            ElTreeNode("9", "Level three 1-1-1"),
            ElTreeNode("10", "Level three 1-1-2"),
        )),
    )),
    ElTreeNode("2", "Level one 2", listOf(
        ElTreeNode("5", "Level two 2-1"),
        ElTreeNode("6", "Level two 2-2"),
    )),
    ElTreeNode("3", "Level one 3", listOf(
        ElTreeNode("7", "Level two 3-1"),
        ElTreeNode("8", "Level two 3-2"),
    )),
)

private fun treeDraggableData(): List<ElTreeNode> = treeBasicData()

private fun appendNode(nodes: List<ElTreeNode>, parentKey: String, child: ElTreeNode): List<ElTreeNode> =
    nodes.map { node ->
        if (node.key == parentKey) {
            node.copy(children = node.children + child)
        } else {
            node.copy(children = appendNode(node.children, parentKey, child))
        }
    }

private fun removeNode(nodes: List<ElTreeNode>, key: String): List<ElTreeNode> =
    nodes.filterNot { it.key == key }.map { it.copy(children = removeNode(it.children, key)) }
