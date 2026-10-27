package cn.enaium.elementplus.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

/** Node model shared by [ElTree], [ElVirtualizedTree] and TreeSelect. */
data class ElTreeNode(
    val key: String,
    val label: String,
    val children: List<ElTreeNode> = emptyList(),
    val disabled: Boolean = false,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
    val isLeaf: Boolean get() = children.isEmpty()
}

internal data class FlatTreeNode(
    val node: ElTreeNode,
    val depth: Int,
    val parentKey: String?,
)

internal fun flattenTree(
    nodes: List<ElTreeNode>,
    expandedKeys: Set<String>,
    parentKey: String? = null,
    depth: Int = 0,
): List<FlatTreeNode> {
    val result = mutableListOf<FlatTreeNode>()
    nodes.forEach { node ->
        result += FlatTreeNode(node, depth, parentKey)
        if (!node.isLeaf && node.key in expandedKeys) {
            result += flattenTree(node.children, expandedKeys, node.key, depth + 1)
        }
    }
    return result
}

internal fun flattenTreeAll(nodes: List<ElTreeNode>, depth: Int = 0): List<FlatTreeNode> {
    val result = mutableListOf<FlatTreeNode>()
    nodes.forEach { node ->
        result += FlatTreeNode(node, depth, null)
        if (!node.isLeaf) result += flattenTreeAll(node.children, depth + 1)
    }
    return result
}

internal fun findTreeNode(nodes: List<ElTreeNode>, key: String): ElTreeNode? {
    nodes.forEach { node ->
        if (node.key == key) return node
        findTreeNode(node.children, key)?.let { return it }
    }
    return null
}

internal fun treeMatches(node: ElTreeNode, query: String): Boolean =
    node.label.contains(query, ignoreCase = true) ||
        node.children.any { treeMatches(it, query) }

internal fun filteredTree(nodes: List<ElTreeNode>, query: String): List<FlatTreeNode> {
    if (query.isBlank()) return emptyList()
    val result = mutableListOf<FlatTreeNode>()
    fun walk(node: ElTreeNode, depth: Int) {
        if (!treeMatches(node, query)) return
        result += FlatTreeNode(node, depth, null)
        node.children.forEach { walk(it, depth + 1) }
    }
    nodes.forEach { walk(it, 0) }
    return result
}

internal fun descendantKeys(node: ElTreeNode): Set<String> {
    val result = mutableSetOf<String>()
    fun walk(n: ElTreeNode) {
        result += n.key
        n.children.forEach { walk(it) }
    }
    walk(node)
    return result
}

internal fun parentKeysOf(nodes: List<ElTreeNode>, targetKey: String): List<String> {
    fun walk(node: ElTreeNode, trail: List<String>): List<String>? {
        if (node.key == targetKey) return trail
        node.children.forEach { child ->
            walk(child, trail + node.key)?.let { return it }
        }
        return null
    }
    nodes.forEach { node -> walk(node, emptyList())?.let { return it } }
    return emptyList()
}

/**
 * `<el-tree>`: expandable, selectable and checkable tree.
 *
 * [checkedKeys] is the set of fully checked nodes; half-checked parents are
 * derived automatically when [checkStrictly] is false.
 */
@Composable
fun ElTree(
    data: List<ElTreeNode>,
    modifier: Modifier = Modifier,
    selectedKey: String? = null,
    onSelectedChange: ((String) -> Unit)? = null,
    checkedKeys: Set<String> = emptySet(),
    onCheckedChange: ((Set<String>) -> Unit)? = null,
    showCheckbox: Boolean = false,
    checkStrictly: Boolean = false,
    defaultExpandedKeys: Set<String> = emptySet(),
    defaultExpandAll: Boolean = false,
    accordion: Boolean = false,
    expandOnClickNode: Boolean = true,
    filterText: String = "",
    highlightCurrent: Boolean = false,
    emptyText: String? = null,
    indent: Dp = 16.dp,
    nodeContent: (@Composable (ElTreeNode, Boolean) -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val initialExpanded = remember(data) {
        if (defaultExpandAll) flattenTreeAll(data).map { it.node.key }.toSet() else defaultExpandedKeys
    }
    var expandedKeys by remember(data) { mutableStateOf(initialExpanded) }
    fun nextExpanded(key: String): Set<String> {
        val expanded = key in expandedKeys
        return when {
            expanded -> expandedKeys - key
            accordion -> setOf(key)
            else -> expandedKeys + key
        }
    }
    val filterActive = filterText.isNotBlank()
    val nodes = remember(data, expandedKeys, filterText) {
        if (filterActive) filteredTree(data, filterText) else flattenTree(data, expandedKeys)
    }
    if (nodes.isEmpty()) {
        Box(modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
            BasicText(
                text = emptyText ?: locale.tree.emptyText,
                style = theme.typography.small.copy(color = colors.textSecondary),
            )
        }
        return
    }
    Column(modifier.fillMaxWidth()) {
        nodes.forEach { flat ->
            TreeRow(
                flat = flat,
                expanded = flat.node.key in expandedKeys,
                selected = selectedKey == flat.node.key,
                checked = flat.node.key in checkedKeys,
                halfChecked = showCheckbox && !checkStrictly && isHalfChecked(flat.node, checkedKeys),
                showCheckbox = showCheckbox,
                highlightCurrent = highlightCurrent,
                indent = indent,
                nodeContent = nodeContent,
                onToggleExpand = {
                    expandedKeys = nextExpanded(flat.node.key)
                },
                onSelect = {
                    if (!flat.node.disabled) {
                        onSelectedChange?.invoke(flat.node.key)
                        if (expandOnClickNode && !flat.node.isLeaf) {
                            expandedKeys = nextExpanded(flat.node.key)
                        }
                    }
                },
                onToggleCheck = {
                    if (!flat.node.disabled) {
                        val next = if (checkStrictly) {
                            if (flat.node.key in checkedKeys) checkedKeys - flat.node.key else checkedKeys + flat.node.key
                        } else {
                            computeCascadeCheck(data, checkedKeys, flat.node)
                        }
                        onCheckedChange?.invoke(next)
                    }
                },
            )
        }
    }
}

/** Virtualized variant of [ElTree] using a lazy list. */
@Composable
fun ElVirtualizedTree(
    data: List<ElTreeNode>,
    modifier: Modifier = Modifier,
    selectedKey: String? = null,
    onSelectedChange: ((String) -> Unit)? = null,
    checkedKeys: Set<String> = emptySet(),
    onCheckedChange: ((Set<String>) -> Unit)? = null,
    showCheckbox: Boolean = false,
    checkStrictly: Boolean = false,
    defaultExpandedKeys: Set<String> = emptySet(),
    defaultExpandAll: Boolean = false,
    accordion: Boolean = false,
    expandOnClickNode: Boolean = true,
    filterText: String = "",
    highlightCurrent: Boolean = false,
    emptyText: String? = null,
    indent: Dp = 16.dp,
    height: Dp = 300.dp,
    nodeContent: (@Composable (ElTreeNode, Boolean) -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val initialExpanded = remember(data) {
        if (defaultExpandAll) flattenTreeAll(data).map { it.node.key }.toSet() else defaultExpandedKeys
    }
    var expandedKeys by remember(data) { mutableStateOf(initialExpanded) }
    fun nextExpanded(key: String): Set<String> {
        val expanded = key in expandedKeys
        return when {
            expanded -> expandedKeys - key
            accordion -> setOf(key)
            else -> expandedKeys + key
        }
    }
    val filterActive = filterText.isNotBlank()
    val nodes = remember(data, expandedKeys, filterText) {
        if (filterActive) filteredTree(data, filterText) else flattenTree(data, expandedKeys)
    }
    if (nodes.isEmpty()) {
        Box(modifier.fillMaxWidth().height(height).padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
            BasicText(text = emptyText ?: locale.tree.emptyText, style = theme.typography.small.copy(color = colors.textSecondary))
        }
        return
    }
    LazyColumn(modifier.fillMaxWidth().height(height)) {
        items(nodes, key = { it.node.key }) { flat ->
            TreeRow(
                flat = flat,
                expanded = flat.node.key in expandedKeys,
                selected = selectedKey == flat.node.key,
                checked = flat.node.key in checkedKeys,
                halfChecked = showCheckbox && !checkStrictly && isHalfChecked(flat.node, checkedKeys),
                showCheckbox = showCheckbox,
                highlightCurrent = highlightCurrent,
                indent = indent,
                nodeContent = nodeContent,
                onToggleExpand = {
                    expandedKeys = nextExpanded(flat.node.key)
                },
                onSelect = {
                    if (!flat.node.disabled) {
                        onSelectedChange?.invoke(flat.node.key)
                        if (expandOnClickNode && !flat.node.isLeaf) {
                            expandedKeys = nextExpanded(flat.node.key)
                        }
                    }
                },
                onToggleCheck = {
                    if (!flat.node.disabled) {
                        val next = if (checkStrictly) {
                            if (flat.node.key in checkedKeys) checkedKeys - flat.node.key else checkedKeys + flat.node.key
                        } else computeCascadeCheck(data, checkedKeys, flat.node)
                        onCheckedChange?.invoke(next)
                    }
                },
            )
        }
    }
}

@Composable
private fun TreeRow(
    flat: FlatTreeNode,
    expanded: Boolean,
    selected: Boolean,
    checked: Boolean,
    halfChecked: Boolean,
    showCheckbox: Boolean,
    highlightCurrent: Boolean,
    indent: Dp,
    nodeContent: (@Composable (ElTreeNode, Boolean) -> Unit)?,
    onToggleExpand: () -> Unit,
    onSelect: () -> Unit,
    onToggleCheck: () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val interaction = remember(flat.node.key) { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val rotation by animateFloatAsState(if (expanded) 90f else 0f, label = "tree-arrow")
    val rowBg = when {
        selected && highlightCurrent -> colors.light(cn.enaium.elementplus.theme.ElType.Primary, 9)
        hovered -> colors.fillLight
        else -> Color.Transparent
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .clickable(enabled = !flat.node.disabled) { onSelect() }
            .height(28.dp)
            .padding(start = indent * flat.depth + 4.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(20.dp)
                .clickable(enabled = !flat.node.isLeaf) { onToggleExpand() },
            contentAlignment = Alignment.Center,
        ) {
            if (!flat.node.isLeaf) {
                ElIcon(
                    ElIcons.ArrowRight,
                    size = 12.dp,
                    tint = colors.textPlaceholder,
                    modifier = Modifier.rotate(rotation),
                )
            }
        }
        if (showCheckbox) {
            TreeCheckbox(
                checked = checked,
                indeterminate = halfChecked,
                enabled = !flat.node.disabled,
                onClick = onToggleCheck,
            )
            Spacer(Modifier.width(6.dp))
        }
        if (flat.node.icon != null) {
            ElIcon(flat.node.icon, size = 14.dp, tint = if (selected) colors.primary else colors.textPlaceholder)
            Spacer(Modifier.width(4.dp))
        }
        if (nodeContent != null) {
            nodeContent(flat.node, selected)
        } else {
            BasicText(
                text = flat.node.label,
                style = theme.typography.base.copy(
                    color = when {
                        flat.node.disabled -> colors.textDisabled
                        selected -> colors.primary
                        else -> colors.textRegular
                    },
                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun TreeCheckbox(
    checked: Boolean,
    indeterminate: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalElTheme.current.colors
    val active = checked || indeterminate
    Box(
        Modifier
            .size(14.dp)
            .background(if (active) colors.primary else colors.fillBlank, RoundedCornerShape(2.dp))
            .border(
                1.dp,
                when {
                    !enabled -> colors.disabledBorder
                    active -> colors.primary
                    else -> colors.border
                },
                RoundedCornerShape(2.dp),
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            ElIcon(ElIcons.Check, size = 10.dp, tint = colors.white)
        } else if (indeterminate) {
            Box(Modifier.size(width = 8.dp, height = 2.dp).background(colors.white))
        }
    }
}

private fun isHalfChecked(node: ElTreeNode, checkedKeys: Set<String>): Boolean {
    if (node.isLeaf || node.key in checkedKeys) return false
    return descendantKeys(node).any { it != node.key && it in checkedKeys }
}

internal fun computeCascadeCheck(
    roots: List<ElTreeNode>,
    checkedKeys: Set<String>,
    node: ElTreeNode,
): Set<String> {
    val next = checkedKeys.toMutableSet()
    val subtree = descendantKeys(node)
    val nowChecked = node.key !in checkedKeys
    if (nowChecked) next += subtree else next -= subtree
    // Recompute fully-checked ancestors from the roots.
    fun sync(node: ElTreeNode, parentKey: String?): Boolean {
        if (node.isLeaf) return node.key in next
        var allChecked = true
        node.children.forEach { child ->
            if (!sync(child, node.key)) allChecked = false
        }
        if (allChecked) next += node.key else next -= node.key
        return node.key in next
    }
    roots.forEach { sync(it, null) }
    return next
}
