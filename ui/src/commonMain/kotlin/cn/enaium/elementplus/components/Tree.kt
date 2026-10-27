package cn.enaium.elementplus.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlin.math.roundToInt

/** Node model shared by [ElTree], [ElVirtualizedTree] and TreeSelect. */
data class ElTreeNode(
    val key: String,
    val label: String,
    val children: List<ElTreeNode> = emptyList(),
    val disabled: Boolean = false,
    val icon: ImageVector? = null,
    /** Explicit lazy leaf marker. `null` means "unknown until loaded". */
    val leaf: Boolean? = null,
) {
    val isLeaf: Boolean get() = leaf ?: children.isEmpty()
}

enum class ElTreeNodeDropType { Before, After, Inner }

internal data class FlatTreeNode(
    val node: ElTreeNode,
    val depth: Int,
    val parentKey: String?,
    val hasChildren: Boolean = !node.isLeaf,
)

internal fun flattenTree(
    nodes: List<ElTreeNode>,
    expandedKeys: Set<String>,
    parentKey: String? = null,
    depth: Int = 0,
): List<FlatTreeNode> {
    val result = mutableListOf<FlatTreeNode>()
    nodes.forEach { node ->
        result += FlatTreeNode(node, depth, parentKey, node.children.isNotEmpty())
        if (node.children.isNotEmpty() && node.key in expandedKeys) {
            result += flattenTree(node.children, expandedKeys, node.key, depth + 1)
        }
    }
    return result
}

internal fun flattenTreeAll(nodes: List<ElTreeNode>, depth: Int = 0): List<FlatTreeNode> {
    val result = mutableListOf<FlatTreeNode>()
    nodes.forEach { node ->
        result += FlatTreeNode(node, depth, null, node.children.isNotEmpty())
        if (node.children.isNotEmpty()) result += flattenTreeAll(node.children, depth + 1)
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
    fun walk(node: ElTreeNode, depth: Int, parentKey: String?) {
        if (!treeMatches(node, query)) return
        result += FlatTreeNode(node, depth, parentKey, node.children.isNotEmpty())
        node.children.forEach { walk(it, depth + 1, node.key) }
    }
    nodes.forEach { walk(it, 0, null) }
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

private fun flattenResolved(
    nodes: List<ElTreeNode>,
    expandedKeys: Set<String>,
    hasChildrenOf: (ElTreeNode) -> Boolean,
    parentKey: String? = null,
    depth: Int = 0,
): List<FlatTreeNode> {
    val result = mutableListOf<FlatTreeNode>()
    nodes.forEach { node ->
        result += FlatTreeNode(node, depth, parentKey, hasChildrenOf(node))
        if (node.key in expandedKeys && node.children.isNotEmpty()) {
            result += flattenResolved(node.children, expandedKeys, hasChildrenOf, node.key, depth + 1)
        }
    }
    return result
}

private fun filterResolved(
    nodes: List<ElTreeNode>,
    query: String,
    predicate: (String, ElTreeNode) -> Boolean,
    hasChildrenOf: (ElTreeNode) -> Boolean,
    parentKey: String? = null,
    depth: Int = 0,
): List<FlatTreeNode> {
    fun matches(node: ElTreeNode): Boolean =
        predicate(query, node) || node.children.any { matches(it) }
    val result = mutableListOf<FlatTreeNode>()
    fun walk(node: ElTreeNode, depth: Int, parentKey: String?) {
        if (!matches(node)) return
        result += FlatTreeNode(node, depth, parentKey, hasChildrenOf(node))
        node.children.forEach { walk(it, depth + 1, node.key) }
    }
    nodes.forEach { walk(it, depth, parentKey) }
    return result
}

private fun materializeTree(nodes: List<ElTreeNode>, childrenOf: (ElTreeNode) -> List<ElTreeNode>): List<ElTreeNode> {
    fun materialize(node: ElTreeNode): ElTreeNode {
        val children = childrenOf(node)
        return if (children.isEmpty()) {
            node
        } else {
            node.copy(children = children.map(::materialize))
        }
    }
    return nodes.map(::materialize)
}

private fun nodeInSubtree(node: ElTreeNode, key: String): Boolean =
    node.key == key || node.children.any { nodeInSubtree(it, key) }

private fun removeNode(nodes: List<ElTreeNode>, key: String): List<ElTreeNode> =
    nodes.filterNot { it.key == key }.map { it.copy(children = removeNode(it.children, key)) }

private fun moveTreeNode(
    roots: List<ElTreeNode>,
    dragKey: String,
    dropKey: String,
    type: ElTreeNodeDropType,
): List<ElTreeNode> {
    if (dragKey == dropKey) return roots
    val dragging = findTreeNode(roots, dragKey) ?: return roots
    val target = findTreeNode(roots, dropKey) ?: return roots
    if (nodeInSubtree(dragging, dropKey)) return roots
    val without = removeNode(roots, dragKey)

    fun insertInner(nodes: List<ElTreeNode>): List<ElTreeNode> = nodes.map { node ->
        if (node.key == dropKey) {
            node.copy(children = node.children + dragging)
        } else {
            node.copy(children = insertInner(node.children))
        }
    }

    fun insertSibling(nodes: List<ElTreeNode>): List<ElTreeNode> {
        val index = nodes.indexOfFirst { it.key == dropKey }
        if (index >= 0) {
            return nodes.toMutableList().also { list ->
                list.add(if (type == ElTreeNodeDropType.Before) index else index + 1, dragging)
            }
        }
        return nodes.map { node -> node.copy(children = insertSibling(node.children)) }
    }

    return when (type) {
        ElTreeNodeDropType.Inner -> insertInner(without)
        ElTreeNodeDropType.Before, ElTreeNodeDropType.After -> insertSibling(without)
    }
}

private data class TreeDragState(
    val key: String,
    val startIndex: Int,
    val offset: Offset = Offset.Zero,
    val targetKey: String? = null,
    val dropType: ElTreeNodeDropType? = null,
)

/**
 * `<el-tree>`: expandable, selectable, checkable, filterable, lazy and
 * draggable tree.
 */
@Composable
fun ElTree(
    data: List<ElTreeNode>,
    modifier: Modifier = Modifier,
    selectedKey: String? = null,
    onSelectedChange: ((String) -> Unit)? = null,
    checkedKeys: Set<String> = emptySet(),
    onCheckedChange: ((Set<String>) -> Unit)? = null,
    defaultCheckedKeys: Set<String> = emptySet(),
    showCheckbox: Boolean = false,
    checkStrictly: Boolean = false,
    expandedKeys: Set<String>? = null,
    onExpandedChange: ((Set<String>) -> Unit)? = null,
    defaultExpandedKeys: Set<String> = emptySet(),
    defaultExpandAll: Boolean = false,
    accordion: Boolean = false,
    expandOnClickNode: Boolean = true,
    lazy: Boolean = false,
    load: ((node: ElTreeNode?, resolve: (List<ElTreeNode>) -> Unit, reject: () -> Unit) -> Unit)? = null,
    filterText: String = "",
    filterNodeMethod: ((query: String, node: ElTreeNode) -> Boolean)? = null,
    highlightCurrent: Boolean = false,
    emptyText: String? = null,
    indent: Dp = 16.dp,
    nodeContent: (@Composable (ElTreeNode, Boolean) -> Unit)? = null,
    nodeModifier: ((ElTreeNode) -> Modifier)? = null,
    nodeIcon: ((ElTreeNode, expanded: Boolean) -> ImageVector?)? = null,
    animateExpand: Boolean = true,
    hoverHighlight: Boolean = true,
    draggable: Boolean = false,
    allowDrag: ((ElTreeNode) -> Boolean)? = null,
    allowDrop: ((dragging: ElTreeNode, drop: ElTreeNode, type: ElTreeNodeDropType) -> Boolean)? = null,
    onNodeDrop: ((dragging: ElTreeNode, drop: ElTreeNode, type: ElTreeNodeDropType) -> Unit)? = null,
    onDataChange: ((List<ElTreeNode>) -> Unit)? = null,
    onNodeClick: ((ElTreeNode) -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val density = LocalDensity.current
    var internalData by remember(data) { mutableStateOf(data) }
    LaunchedEffect(data) { internalData = data }
    val baseData = if (onDataChange != null) data else internalData

    var lazyRoots by remember(data, lazy) { mutableStateOf(data) }
    LaunchedEffect(data) { lazyRoots = data }
    val loadedChildren = remember(data) { mutableStateMapOf<String, List<ElTreeNode>>() }
    val loadedKeys = remember(data) { mutableStateMapOf<String, Boolean>() }
    val loadingKeys = remember(data) { mutableStateMapOf<String, Boolean>() }
    val failedKeys = remember(data) { mutableStateMapOf<String, Boolean>() }

    fun childrenOf(node: ElTreeNode): List<ElTreeNode> = loadedChildren[node.key] ?: node.children
    fun nodeHasChildren(node: ElTreeNode): Boolean {
        if (node.leaf == true) return false
        if (childrenOf(node).isNotEmpty()) return true
        if (!lazy) return false
        return loadedKeys[node.key] != true
    }
    fun materializedRoots(): List<ElTreeNode> =
        if (lazy) materializeTree(lazyRoots, ::childrenOf) else baseData

    fun loadNode(node: ElTreeNode?) {
        if (!lazy || load == null) return
        val key = node?.key ?: "__root__"
        if (loadingKeys[key] == true || loadedKeys[key] == true) return
        loadingKeys[key] = true
        failedKeys.remove(key)
        load.invoke(
            node,
            { children ->
                if (node == null) lazyRoots = children else loadedChildren[node.key] = children
                loadedKeys[key] = true
                loadingKeys.remove(key)
                failedKeys.remove(key)
            },
            {
                loadingKeys.remove(key)
                failedKeys[key] = true
            },
        )
    }

    LaunchedEffect(lazy, data, load) {
        if (lazy && data.isEmpty() && load != null && loadedKeys["__root__"] != true) {
            loadNode(null)
        }
    }

    val initialExpanded = remember(data) {
        if (defaultExpandAll) flattenTreeAll(data).map { it.node.key }.toSet()
        else defaultExpandedKeys
    }
    var internalExpanded by remember { mutableStateOf(initialExpanded) }
    val effectiveExpanded = expandedKeys ?: internalExpanded
    fun emitExpanded(next: Set<String>) {
        if (expandedKeys == null) internalExpanded = next
        onExpandedChange?.invoke(next)
    }

    var internalChecked by remember { mutableStateOf(if (checkedKeys.isEmpty()) defaultCheckedKeys else checkedKeys) }
    val effectiveChecked = if (onCheckedChange != null) checkedKeys else internalChecked
    fun emitChecked(next: Set<String>) {
        if (onCheckedChange != null) onCheckedChange.invoke(next) else internalChecked = next
    }

    val query = filterText
    val filterActive = query.isNotBlank()
    val roots = remember(baseData, lazy, lazyRoots, loadedChildren.toMap(), loadedKeys.toMap()) {
        materializedRoots()
    }
    fun hasChildrenForDisplay(node: ElTreeNode): Boolean = nodeHasChildren(node)
    val nodes = remember(roots, effectiveExpanded, query, filterNodeMethod, lazy, loadedKeys.toMap()) {
        if (filterActive) {
            filterResolved(roots, query, filterNodeMethod ?: { q, n -> n.label.contains(q, ignoreCase = true) }, ::hasChildrenForDisplay)
        } else {
            flattenResolved(roots, effectiveExpanded, ::hasChildrenForDisplay)
        }
    }

    fun siblingsOf(parentKey: String?): List<ElTreeNode> =
        if (parentKey == null) roots else findTreeNode(roots, parentKey)?.children.orEmpty()

    fun toggleExpanded(flat: FlatTreeNode) {
        val node = flat.node
        val key = node.key
        if (loadingKeys[key] == true) return
        if (failedKeys[key] == true) {
            failedKeys.remove(key)
            loadNode(node)
            return
        }
        if (key in effectiveExpanded) {
            emitExpanded(effectiveExpanded - key)
            return
        }
        val next = if (accordion) {
            val siblings = siblingsOf(flat.parentKey).map { it.key }.toSet()
            (effectiveExpanded - siblings) + key
        } else {
            effectiveExpanded + key
        }
        emitExpanded(next)
        if (lazy && nodeHasChildren(node) && loadedKeys[key] != true) loadNode(node)
    }

    fun toggleCheck(flat: FlatTreeNode) {
        val node = flat.node
        if (node.disabled) return
        val next = if (checkStrictly) {
            if (node.key in effectiveChecked) effectiveChecked - node.key else effectiveChecked + node.key
        } else {
            computeCascadeCheck(roots, effectiveChecked, node)
        }
        emitChecked(next)
    }

    var dragState by remember { mutableStateOf<TreeDragState?>(null) }
    val dragThreshold = with(density) { 24.dp.toPx() }
    val rowHeightPx = with(density) { 28.dp.toPx() }

    fun startDrag(flat: FlatTreeNode) {
        if (!draggable || flat.node.disabled) return
        if (allowDrag?.invoke(flat.node) == false) return
        dragState = TreeDragState(
            key = flat.node.key,
            startIndex = nodes.indexOfFirst { it.node.key == flat.node.key },
        )
    }

    fun updateDrag(amount: Offset) {
        val current = dragState ?: return
        val offset = current.offset + amount
        val start = current.startIndex
        if (start < 0 || nodes.isEmpty()) return
        val targetIndex = (start + (offset.y / rowHeightPx).roundToInt()).coerceIn(0, nodes.lastIndex)
        val target = nodes[targetIndex]
        val dragged = findTreeNode(roots, current.key)
        val inner = offset.x > dragThreshold && target.node.key != current.key
        val type = when {
            inner -> ElTreeNodeDropType.Inner
            targetIndex > start -> ElTreeNodeDropType.After
            targetIndex < start -> ElTreeNodeDropType.Before
            else -> null
        }
        val allowed = type != null &&
            target.node.key != current.key &&
            dragged != null &&
            !nodeInSubtree(dragged, target.node.key) &&
            allowDrop?.invoke(dragged, target.node, type) != false
        dragState = current.copy(
            offset = offset,
            targetKey = if (allowed) target.node.key else null,
            dropType = if (allowed) type else null,
        )
    }

    fun endDrag() {
        val current = dragState ?: return
        val targetKey = current.targetKey
        val type = current.dropType
        if (targetKey != null && type != null) {
            val dragged = findTreeNode(roots, current.key)
            val target = findTreeNode(roots, targetKey)
            if (dragged != null && target != null) {
                val next = moveTreeNode(roots, current.key, targetKey, type)
                if (next !== roots) {
                    if (onDataChange != null) onDataChange.invoke(next) else internalData = next
                    if (type == ElTreeNodeDropType.Inner) {
                        val nextExpanded = effectiveExpanded + targetKey
                        emitExpanded(nextExpanded)
                    }
                    onNodeDrop?.invoke(dragged, target, type)
                }
            }
        }
        dragState = null
    }

    if (roots.isEmpty() && lazy) {
        Box(
            modifier.fillMaxWidth().height(80.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (loadingKeys["__root__"] == true) {
                ElIcon(ElIcons.Loading, size = 18.dp, tint = colors.primary, spin = true)
            } else {
                BasicText(
                    text = emptyText ?: locale.tree.emptyText,
                    style = theme.typography.small.copy(color = colors.textSecondary),
                )
            }
        }
        return
    }
    if (roots.isEmpty()) {
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
            val expanded = flat.node.key in effectiveExpanded
            val selected = selectedKey == flat.node.key
            val drag = dragState
            TreeRow(
                flat = flat,
                expanded = expanded,
                selected = selected,
                checked = flat.node.key in effectiveChecked,
                halfChecked = showCheckbox && !checkStrictly && isHalfChecked(flat.node, effectiveChecked),
                showCheckbox = showCheckbox,
                highlightCurrent = highlightCurrent,
                indent = indent,
                nodeContent = nodeContent,
                nodeModifier = nodeModifier,
                resolvedIcon = nodeIcon?.invoke(flat.node, expanded) ?: flat.node.icon,
                animateExpand = animateExpand,
                hoverHighlight = hoverHighlight,
                loading = loadingKeys[flat.node.key] == true,
                loadFailed = failedKeys[flat.node.key] == true,
                draggable = draggable,
                dragging = drag?.key == flat.node.key,
                dragOffsetY = if (drag?.key == flat.node.key) drag.offset.y else 0f,
                dropTarget = drag?.targetKey == flat.node.key,
                dropType = if (drag?.targetKey == flat.node.key) drag.dropType else null,
                onToggleExpand = { toggleExpanded(flat) },
                onSelect = {
                    if (!flat.node.disabled) {
                        onNodeClick?.invoke(flat.node)
                        onSelectedChange?.invoke(flat.node.key)
                        if (expandOnClickNode && flat.hasChildren) toggleExpanded(flat)
                    }
                },
                onToggleCheck = { toggleCheck(flat) },
                onDragStart = { startDrag(flat) },
                onDrag = ::updateDrag,
                onDragEnd = ::endDrag,
                onDragCancel = { dragState = null },
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
    defaultCheckedKeys: Set<String> = emptySet(),
    showCheckbox: Boolean = false,
    checkStrictly: Boolean = false,
    expandedKeys: Set<String>? = null,
    onExpandedChange: ((Set<String>) -> Unit)? = null,
    defaultExpandedKeys: Set<String> = emptySet(),
    defaultExpandAll: Boolean = false,
    accordion: Boolean = false,
    expandOnClickNode: Boolean = true,
    filterText: String = "",
    filterNodeMethod: ((query: String, node: ElTreeNode) -> Boolean)? = null,
    highlightCurrent: Boolean = false,
    emptyText: String? = null,
    indent: Dp = 16.dp,
    height: Dp = 300.dp,
    nodeContent: (@Composable (ElTreeNode, Boolean) -> Unit)? = null,
    nodeModifier: ((ElTreeNode) -> Modifier)? = null,
    nodeIcon: ((ElTreeNode, expanded: Boolean) -> ImageVector?)? = null,
    animateExpand: Boolean = true,
    hoverHighlight: Boolean = true,
    width: Dp? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val initialExpanded = remember(data) {
        if (defaultExpandAll) flattenTreeAll(data).map { it.node.key }.toSet()
        else defaultExpandedKeys
    }
    var internalExpanded by remember { mutableStateOf(initialExpanded) }
    val effectiveExpanded = expandedKeys ?: internalExpanded
    fun emitExpanded(next: Set<String>) {
        if (expandedKeys == null) internalExpanded = next
        onExpandedChange?.invoke(next)
    }
    var internalChecked by remember { mutableStateOf(if (checkedKeys.isEmpty()) defaultCheckedKeys else checkedKeys) }
    val effectiveChecked = if (onCheckedChange != null) checkedKeys else internalChecked
    fun emitChecked(next: Set<String>) {
        if (onCheckedChange != null) onCheckedChange.invoke(next) else internalChecked = next
    }
    fun siblingsOf(parentKey: String?): List<ElTreeNode> =
        if (parentKey == null) data else findTreeNode(data, parentKey)?.children.orEmpty()
    fun toggleExpanded(flat: FlatTreeNode) {
        val key = flat.node.key
        if (key in effectiveExpanded) {
            emitExpanded(effectiveExpanded - key)
        } else {
            val next = if (accordion) {
                (effectiveExpanded - siblingsOf(flat.parentKey).map { it.key }.toSet()) + key
            } else {
                effectiveExpanded + key
            }
            emitExpanded(next)
        }
    }
    val query = filterText
    val hasChildrenOf: (ElTreeNode) -> Boolean = { it.children.isNotEmpty() }
    val nodes = remember(data, effectiveExpanded, query, filterNodeMethod) {
        if (query.isNotBlank()) {
            filterResolved(data, query, filterNodeMethod ?: { q, n -> n.label.contains(q, ignoreCase = true) }, hasChildrenOf)
        } else {
            flattenResolved(data, effectiveExpanded, hasChildrenOf)
        }
    }
    val treeModifier = modifier.then(if (width != null) Modifier.width(width) else Modifier.fillMaxWidth())
    if (nodes.isEmpty()) {
        Box(treeModifier.height(height), contentAlignment = Alignment.Center) {
            BasicText(text = emptyText ?: locale.tree.emptyText, style = theme.typography.small.copy(color = colors.textSecondary))
        }
        return
    }
    LazyColumn(treeModifier.height(height)) {
        items(nodes, key = { it.node.key }) { flat ->
            val expanded = flat.node.key in effectiveExpanded
            TreeRow(
                flat = flat,
                expanded = expanded,
                selected = selectedKey == flat.node.key,
                checked = flat.node.key in effectiveChecked,
                halfChecked = showCheckbox && !checkStrictly && isHalfChecked(flat.node, effectiveChecked),
                showCheckbox = showCheckbox,
                highlightCurrent = highlightCurrent,
                indent = indent,
                nodeContent = nodeContent,
                nodeModifier = nodeModifier,
                resolvedIcon = nodeIcon?.invoke(flat.node, expanded) ?: flat.node.icon,
                animateExpand = animateExpand,
                hoverHighlight = hoverHighlight,
                loading = false,
                loadFailed = false,
                draggable = false,
                dragging = false,
                dragOffsetY = 0f,
                dropTarget = false,
                dropType = null,
                onToggleExpand = { toggleExpanded(flat) },
                onSelect = {
                    if (!flat.node.disabled) {
                        onSelectedChange?.invoke(flat.node.key)
                        if (expandOnClickNode && flat.hasChildren) toggleExpanded(flat)
                    }
                },
                onToggleCheck = {
                    if (!flat.node.disabled) {
                        emitChecked(
                            if (checkStrictly) {
                                if (flat.node.key in effectiveChecked) effectiveChecked - flat.node.key else effectiveChecked + flat.node.key
                            } else {
                                computeCascadeCheck(data, effectiveChecked, flat.node)
                            },
                        )
                    }
                },
                onDragStart = {},
                onDrag = {},
                onDragEnd = {},
                onDragCancel = {},
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
    nodeModifier: ((ElTreeNode) -> Modifier)?,
    resolvedIcon: ImageVector?,
    animateExpand: Boolean,
    hoverHighlight: Boolean,
    loading: Boolean,
    loadFailed: Boolean,
    draggable: Boolean,
    dragging: Boolean,
    dragOffsetY: Float,
    dropTarget: Boolean,
    dropType: ElTreeNodeDropType?,
    onToggleExpand: () -> Unit,
    onSelect: () -> Unit,
    onToggleCheck: () -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val interaction = remember(flat.node.key) { MutableInteractionSource() }
    val hovered = if (hoverHighlight) interaction.collectIsHoveredAsState().value else false
    val rotation = if (animateExpand) {
        animateFloatAsState(if (expanded) 90f else 0f, label = "tree-arrow").value
    } else {
        if (expanded) 90f else 0f
    }
    val rowBg = when {
        dropTarget -> colors.primary.copy(alpha = 0.12f)
        selected && highlightCurrent -> colors.primary.copy(alpha = 0.10f)
        hovered -> colors.fillLight
        else -> Color.Transparent
    }
    val custom = nodeModifier?.invoke(flat.node) ?: Modifier
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .then(custom)
            .clickable(enabled = !flat.node.disabled) { onSelect() }
            .then(if (hoverHighlight) Modifier.hoverable(interaction) else Modifier)
            .heightIn(min = 28.dp)
            .then(
                if (dragging) {
                    Modifier
                        .zIndex(1f)
                        .graphicsLayer { translationY = dragOffsetY; alpha = 0.85f }
                } else Modifier,
            )
            .then(
                if (draggable && !flat.node.disabled) {
                    Modifier.pointerInput(flat.node.key) {
                        detectDragGestures(
                            onDragStart = { onDragStart() },
                            onDrag = { change, amount ->
                                change.consume()
                                onDrag(amount)
                            },
                            onDragEnd = onDragEnd,
                            onDragCancel = onDragCancel,
                        )
                    }
                } else Modifier,
            )
            .padding(start = indent * flat.depth + 4.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(20.dp)
                .then(
                    if (flat.hasChildren) {
                        Modifier.semantics {
                            contentDescription = when {
                                loading -> "Loading node"
                                loadFailed -> "Retry loading node"
                                expanded -> "Collapse node"
                                else -> "Expand node"
                            }
                        }
                    } else Modifier,
                )
                .clickable(enabled = flat.hasChildren && !loading && !flat.node.disabled) { onToggleExpand() },
            contentAlignment = Alignment.Center,
        ) {
            when {
                loading -> ElIcon(ElIcons.Loading, size = 12.dp, tint = colors.textPlaceholder, spin = true)
                flat.hasChildren -> ElIcon(
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
                label = flat.node.label,
                onClick = onToggleCheck,
            )
            Spacer(Modifier.width(6.dp))
        }
        if (resolvedIcon != null) {
            ElIcon(resolvedIcon, size = 14.dp, tint = if (selected) colors.primary else colors.textPlaceholder)
            Spacer(Modifier.width(4.dp))
        }
        if (nodeContent != null) {
            Box(Modifier.weight(1f)) { nodeContent(flat.node, selected) }
        } else {
            BasicText(
                text = flat.node.label,
                modifier = Modifier.weight(1f),
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
    label: String,
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
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = { onClick() },
            )
            .semantics { contentDescription = "Select $label" },
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
    val descendants = descendantKeys(node).filter { it != node.key }
    return descendants.any { it in checkedKeys } && !descendants.all { it in checkedKeys }
}

internal fun computeCascadeCheck(
    roots: List<ElTreeNode>,
    checkedKeys: Set<String>,
    node: ElTreeNode,
): Set<String> {
    val next = checkedKeys.toMutableSet()
    fun enabledDescendants(n: ElTreeNode): List<String> = buildList {
        n.children.forEach { child ->
            if (!child.disabled) add(child.key)
            addAll(enabledDescendants(child))
        }
    }
    val targets = (if (node.disabled) emptyList() else listOf(node.key)) + enabledDescendants(node)
    val nowChecked = node.key !in checkedKeys
    if (nowChecked) next.addAll(targets) else next.removeAll(targets)

    fun syncLeaf(n: ElTreeNode) {
        if (n.disabled) next.remove(n.key)
    }
    fun allEnabledChecked(n: ElTreeNode): Boolean {
        val enabledChildren = n.children.filterNot { it.disabled }
        if (enabledChildren.isEmpty()) return n.key in next
        return enabledChildren.all { allEnabledChecked(it) }
    }
    fun sync(n: ElTreeNode) {
        if (n.children.isEmpty()) {
            syncLeaf(n)
            return
        }
        n.children.forEach { sync(it) }
        if (n.disabled) {
            next.remove(n.key)
        } else if (allEnabledChecked(n)) {
            next.add(n.key)
        } else {
            next.remove(n.key)
        }
    }
    roots.forEach { sync(it) }
    return next
}
