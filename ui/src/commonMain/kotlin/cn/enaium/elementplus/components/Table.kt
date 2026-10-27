package cn.enaium.elementplus.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElTextAlign
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElTableColumnType { Default, Selection, Index, Expand }
enum class ElTableFixed { Left, Right }
enum class ElTableSortOrder { Ascending, Descending }
enum class ElTableLayout { Fixed, Auto }

data class ElTableCellSpan(val rowSpan: Int = 1, val colSpan: Int = 1)
data class ElTableFilter(val text: String, val value: Any?)
data class ElTableSort(val key: String, val order: ElTableSortOrder?)
data class ElTableTooltipData<T>(
    val row: T,
    val rowIndex: Int,
    val column: ElTableColumn<T>,
    val cellValue: Any?,
)

/**
 * Table column definition. Columns may contain [children] to form grouped
 * headers; leaf columns own the cell renderer and sorting/filtering metadata.
 */
class ElTableColumn<T>(
    val label: String,
    val width: Dp? = null,
    val minWidth: Dp = 80.dp,
    val align: ElTextAlign = ElTextAlign.Start,
    val sortable: Boolean = false,
    val property: ((T) -> Any?)? = null,
    val formatter: ((T, Int) -> String)? = null,
    val sortComparator: ((T, T) -> Int)? = null,
    val cell: (@Composable (row: T, index: Int) -> Unit)? = null,
    val fixed: ElTableFixed? = null,
    val type: ElTableColumnType = ElTableColumnType.Default,
    val sortMethod: ((T, T) -> Int)? = null,
    val filters: List<ElTableFilter> = emptyList(),
    val filterMethod: ((Any?, T) -> Boolean)? = null,
    val showOverflowTooltip: Boolean = false,
    val tooltipFormatter: ((ElTableTooltipData<T>) -> String?)? = null,
    val key: String? = null,
    val header: (@Composable () -> Unit)? = null,
    val index: ((Int) -> Int)? = null,
    val children: List<ElTableColumn<T>> = emptyList(),
    val colSpan: ((row: T, rowIndex: Int, columnIndex: Int) -> Int)? = null,
    val rowSpan: ((row: T, rowIndex: Int, columnIndex: Int) -> Int)? = null,
) {
    fun text(row: T, index: Int): String =
        formatter?.invoke(row, index) ?: property?.invoke(row)?.toString() ?: row?.toString() ?: ""
}

/**
 * Hoisted sort and filter state for [ElTable] and [ElVirtualizedTable].
 * Selection and expansion remain explicit parameters because they usually
 * belong to the caller's domain model.
 */
@Stable
class ElTableState(initialSort: ElTableSort? = null) {
    var sortKey by mutableStateOf(initialSort?.key)
        internal set
    var sortOrder by mutableStateOf(initialSort?.order)
        internal set

    internal val filters = mutableStateMapOf<String, List<Any?>>()

    val filterValues: Map<String, List<Any?>> get() = filters.toMap()

    fun clearSort() {
        sortKey = null
        sortOrder = null
    }

    fun clearFilter(vararg keys: String) {
        if (keys.isEmpty()) filters.clear() else keys.forEach { filters.remove(it) }
    }

    fun setFilter(key: String, values: List<Any?>) {
        if (values.isEmpty()) filters.remove(key) else filters[key] = values
    }

    internal fun sortBy(key: String, order: ElTableSortOrder?) {
        sortKey = if (order == null) null else key
        sortOrder = order
    }
}

@Composable
fun rememberElTableState(initialSort: ElTableSort? = null): ElTableState =
    remember { ElTableState(initialSort) }

private data class ResolvedColumn<T>(
    val column: ElTableColumn<T>,
    val key: String,
    val fixed: ElTableFixed?,
)

private data class HeaderCell<T>(
    val column: ElTableColumn<T>?,
    val key: String,
    val label: String,
    val level: Int,
    val rowSpan: Int,
    val leafStart: Int,
    val leafSpan: Int,
    val fixed: ElTableFixed?,
    val align: ElTextAlign,
)

private data class FlatRow<T>(
    val row: T,
    val key: Any,
    val index: Int,
    val depth: Int,
    val parentKey: Any?,
    val hasChildren: Boolean,
)

private fun <T> prepareTree(
    columns: List<ElTableColumn<T>>,
    selection: Boolean,
    expandContent: (@Composable (T, Int) -> Unit)?,
): List<ElTableColumn<T>> {
    val result = columns.toMutableList()
    if (selection && result.none { it.type == ElTableColumnType.Selection }) {
        result.add(0, ElTableColumn("", key = "__selection", type = ElTableColumnType.Selection, width = 48.dp))
    }
    if (expandContent != null && result.none { it.type == ElTableColumnType.Expand }) {
        val insertAt = if (result.firstOrNull()?.type == ElTableColumnType.Selection) 1 else 0
        result.add(insertAt, ElTableColumn("", key = "__expand", type = ElTableColumnType.Expand, width = 48.dp))
    }
    return result
}

private fun <T> flattenColumns(
    columns: List<ElTableColumn<T>>,
    parentFixed: ElTableFixed? = null,
    parentKey: String = "",
): List<ResolvedColumn<T>> = buildList {
    columns.forEachIndexed { index, column ->
        val fixed = column.fixed ?: parentFixed
        val key = column.key ?: (if (parentKey.isEmpty()) "col-$index" else "$parentKey-$index")
        if (column.children.isEmpty()) {
            add(ResolvedColumn(column, key, fixed))
        } else {
            addAll(flattenColumns(column.children, fixed, key))
        }
    }
}

private fun <T> maxHeaderLevel(columns: List<ElTableColumn<T>>): Int {
    var max = 0
    fun walk(nodes: List<ElTableColumn<T>>, level: Int) {
        max = maxOf(max, level)
        nodes.forEach { if (it.children.isNotEmpty()) walk(it.children, level + 1) }
    }
    walk(columns, 0)
    return max
}

private fun <T> buildHeaderCells(
    columns: List<ElTableColumn<T>>,
    parentFixed: ElTableFixed?,
    parentKey: String,
    level: Int,
    maxLevel: Int,
    startLeaf: Int,
): Pair<List<HeaderCell<T>>, Int> {
    val cells = mutableListOf<HeaderCell<T>>()
    var leaf = startLeaf
    columns.forEachIndexed { index, column ->
        val fixed = column.fixed ?: parentFixed
        val key = column.key ?: (if (parentKey.isEmpty()) "col-$index" else "$parentKey-$index")
        if (column.children.isEmpty()) {
            cells += HeaderCell(
                column = column,
                key = key,
                label = column.label,
                level = level,
                rowSpan = (maxLevel - level + 1).coerceAtLeast(1),
                leafStart = leaf,
                leafSpan = 1,
                fixed = fixed,
                align = column.align,
            )
            leaf++
        } else {
            val childStart = leaf
            val (childCells, nextLeaf) = buildHeaderCells(column.children, fixed, key, level + 1, maxLevel, leaf)
            cells += childCells
            cells += HeaderCell(
                column = column,
                key = key,
                label = column.label,
                level = level,
                rowSpan = 1,
                leafStart = childStart,
                leafSpan = nextLeaf - childStart,
                fixed = fixed,
                align = column.align,
            )
            leaf = nextLeaf
        }
    }
    return cells to leaf
}

private data class ColumnWidths<T>(
    val byKey: Map<String, Dp>,
)

private fun <T> resolveWidths(
    leaves: List<ResolvedColumn<T>>,
    available: Dp,
    layout: ElTableLayout,
    left: List<ResolvedColumn<T>>,
    right: List<ResolvedColumn<T>>,
): ColumnWidths<T> {
    if (leaves.isEmpty()) return ColumnWidths(emptyMap())
    val widths = mutableMapOf<String, Dp>()
    val leftLeaves = leaves.filter { it in left }
    val rightLeaves = leaves.filter { it in right }
    val middleLeaves = leaves.filter { it !in left && it !in right }
    val utilityLeaves = middleLeaves.filter {
        it.column.type == ElTableColumnType.Selection || it.column.type == ElTableColumnType.Expand
    }
    val resizableLeaves = middleLeaves.filter { it !in utilityLeaves }

    fun autoWidths(list: List<ResolvedColumn<T>>): Map<String, Dp> =
        list.associate { it.key to (it.column.width ?: it.column.minWidth) }

    val leftMap = autoWidths(leftLeaves)
    val rightMap = autoWidths(rightLeaves)
    val utilityMap = autoWidths(utilityLeaves)
    widths.putAll(leftMap)
    widths.putAll(rightMap)
    widths.putAll(utilityMap)

    val leftWidth = leftLeaves.sumOf { (leftMap[it.key] ?: it.column.minWidth).value.toDouble() }.toFloat().dp
    val rightWidth = rightLeaves.sumOf { (rightMap[it.key] ?: it.column.minWidth).value.toDouble() }.toFloat().dp
    val utilityWidth = utilityLeaves.sumOf { (utilityMap[it.key] ?: it.column.minWidth).value.toDouble() }.toFloat().dp
    val middleAvailable = (available - leftWidth - rightWidth - utilityWidth).coerceAtLeast(1.dp)
    val specified = resizableLeaves.sumOf { it.column.width?.value?.toDouble() ?: 0.0 }
    val unspecified = resizableLeaves.count { it.column.width == null }
    if (layout == ElTableLayout.Auto) {
        resizableLeaves.forEach { widths[it.key] = it.column.width ?: it.column.minWidth }
    } else {
        if (unspecified > 0) {
            val availableForUnspecified = (middleAvailable.value - specified).coerceAtLeast(0.0)
            val share = (availableForUnspecified / unspecified).toFloat().dp
            resizableLeaves.forEach {
                widths[it.key] = it.column.width ?: maxOf(it.column.minWidth, share)
            }
            // If there was less space than the min widths, keep min widths and let the table scroll.
        } else {
            val total = specified.toFloat().dp
            if (total > middleAvailable && total.value > 0f) {
                resizableLeaves.forEach { widths[it.key] = it.column.width ?: it.column.minWidth }
            } else if (resizableLeaves.isNotEmpty()) {
                val factor = middleAvailable.value / total.value.coerceAtLeast(1f)
                resizableLeaves.forEach { widths[it.key] = (it.column.width ?: it.column.minWidth) * factor }
            }
        }
    }
    return ColumnWidths(widths)
}

private fun <T> compareRows(column: ResolvedColumn<T>, a: T, b: T): Int {
    val custom = column.column.sortComparator ?: column.column.sortMethod
    if (custom != null) return custom.invoke(a, b)
    val left = column.column.property?.invoke(a)
    val right = column.column.property?.invoke(b)
    return when {
        left is Number && right is Number -> left.toDouble().compareTo(right.toDouble())
        else -> {
            val leftText = left?.toString() ?: column.column.formatter?.invoke(a, 0) ?: a?.toString() ?: ""
            val rightText = right?.toString() ?: column.column.formatter?.invoke(b, 0) ?: b?.toString() ?: ""
            leftText.compareTo(rightText)
        }
    }
}

private fun <T> rowMatchesFilters(
    row: T,
    leaves: List<ResolvedColumn<T>>,
    filters: Map<String, List<Any?>>,
): Boolean {
    return leaves.all { leaf ->
        val selected = filters[leaf.key].orEmpty()
        if (selected.isEmpty() || leaf.column.filters.isEmpty()) return@all true
        selected.any { value ->
            leaf.column.filterMethod?.invoke(value, row)
                ?: (leaf.column.property?.invoke(row) == value)
        }
    }
}

private fun <T> filterTree(
    nodes: List<T>,
    childrenOf: (T) -> List<T>,
    predicate: (T) -> Boolean,
): Pair<List<T>, Map<Any, List<T>>> {
    val visibleChildren = mutableMapOf<Any, List<T>>()
    fun walk(list: List<T>): List<T> = list.mapNotNull { row ->
        val sourceChildren = childrenOf(row)
        val child = walk(sourceChildren)
        if (sourceChildren.isNotEmpty()) visibleChildren[rowKeyOf(row)] = child
        if (predicate(row) || child.isNotEmpty()) row else null
    }
    return walk(nodes) to visibleChildren
}

private fun rowKeyOf(row: Any?): Any = row ?: "null"

private fun <T> sortTree(
    nodes: List<T>,
    childrenOf: (T) -> List<T>,
    comparator: Comparator<T>,
): Pair<List<T>, Map<Any, List<T>>> {
    val visibleChildren = mutableMapOf<Any, List<T>>()
    fun walk(list: List<T>): List<T> = list.sortedWith(comparator).map { row ->
        val sourceChildren = childrenOf(row)
        val child = walk(sourceChildren)
        if (sourceChildren.isNotEmpty()) visibleChildren[rowKeyOf(row)] = child
        row
    }
    return walk(nodes) to visibleChildren
}

private fun <T> flattenRows(
    roots: List<T>,
    expanded: Set<Any>,
    rowKey: (T) -> Any,
    childrenOf: (T) -> List<T>,
    hasChildren: (T) -> Boolean,
): List<FlatRow<T>> {
    val result = mutableListOf<FlatRow<T>>()
    fun walk(nodes: List<T>, depth: Int, parent: Any?) {
        nodes.forEach { row ->
            val key = rowKey(row)
            val children = childrenOf(row)
            result += FlatRow(row, key, result.size, depth, parent, children.isNotEmpty() || hasChildren(row))
            if (key in expanded && children.isNotEmpty()) walk(children, depth + 1, key)
        }
    }
    walk(roots, 0, null)
    return result
}

/**
 * Element Plus table with property-based or fully custom cells, grouped
 * headers, fixed columns, sorting, filtering, selection, tree data, lazy
 * loading, expandable detail rows, merged cells and a summary row.
 *
 * Pass [height] to scroll the body under a sticky header. Use
 * [ElVirtualizedTable] when a large data set should compose only visible rows.
 */
@Composable
fun <T> ElTable(
    data: List<T>,
    columns: List<ElTableColumn<T>>,
    modifier: Modifier = Modifier,
    rowKey: (T) -> Any = { it.hashCode() },
    border: Boolean = false,
    stripe: Boolean = false,
    size: ElSize? = null,
    height: Dp? = null,
    maxHeight: Dp? = null,
    showHeader: Boolean = true,
    highlightCurrentRow: Boolean = false,
    selectedRowKey: Any? = null,
    onRowClick: ((T, Int) -> Unit)? = null,
    emptyText: String? = null,
    selection: Boolean = false,
    selectedKeys: Set<Any> = emptySet(),
    onSelectionChange: ((Set<Any>) -> Unit)? = null,
    onSortChange: ((column: ElTableColumn<T>, ascending: Boolean?) -> Unit)? = null,
    loading: Boolean = false,
    defaultSort: ElTableSort? = null,
    state: ElTableState = rememberElTableState(defaultSort),
    tableLayout: ElTableLayout = ElTableLayout.Fixed,
    spanMethod: ((row: T, rowIndex: Int, columnIndex: Int, column: ElTableColumn<T>) -> ElTableCellSpan?)? = null,
    showSummary: Boolean = false,
    sumText: String? = null,
    summaryMethod: ((columns: List<ElTableColumn<T>>, data: List<T>) -> List<String>)? = null,
    rowBackground: ((row: T, index: Int) -> Color?)? = null,
    expandedRowKeys: Set<Any>? = null,
    defaultExpandAll: Boolean = false,
    onExpandedChange: ((Set<Any>) -> Unit)? = null,
    expandContent: (@Composable (row: T, index: Int) -> Unit)? = null,
    children: ((T) -> List<T>)? = null,
    lazy: Boolean = false,
    hasChildren: ((T) -> Boolean)? = null,
    load: ((row: T, resolve: (List<T>) -> Unit) -> Unit)? = null,
    checkStrictly: Boolean = false,
    selectable: ((row: T, index: Int) -> Boolean)? = null,
    tooltipFormatter: ((ElTableTooltipData<T>) -> String?)? = null,
    showOverflowTooltip: Boolean = false,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val showOverflowTooltip = showOverflowTooltip || LocalElConfig.current.table.showOverflowTooltip == true
    val resolvedSize = resolveElSize(size)
    val rowHeight = when (resolvedSize) {
        ElSize.Large -> 48.dp
        ElSize.Default -> 40.dp
        ElSize.Small -> 32.dp
    }
    val columnTree = remember(columns, selection, expandContent) {
        prepareTree(columns, selection, expandContent)
    }
    val leaves = remember(columnTree) { flattenColumns(columnTree) }
    val maxLevel = remember(columnTree) { maxHeaderLevel(columnTree) }
    val headerCells = remember(columnTree, maxLevel) {
        buildHeaderCells(columnTree, null, "", 0, maxLevel, 0).first
    }
    val hasSpan = spanMethod != null || leaves.any { it.column.rowSpan != null || it.column.colSpan != null }
    var internalExpanded by remember(data) {
        mutableStateOf(if (defaultExpandAll) allTreeKeys(data, children, hasChildren, rowKey) else emptySet())
    }
    val effectiveExpanded = expandedRowKeys ?: internalExpanded
    val loadedChildren = remember(data) { mutableStateMapOf<Any, List<T>>() }
    val loadingKeys = remember(data) { mutableStateMapOf<Any, Boolean>() }
    fun childrenOf(row: T): List<T> =
        loadedChildren[rowKey(row)] ?: children?.invoke(row) ?: emptyList()
    fun nodeHasChildren(row: T): Boolean =
        childrenOf(row).isNotEmpty() || hasChildren?.invoke(row) == true
    fun updateExpanded(next: Set<Any>) {
        if (expandedRowKeys == null) internalExpanded = next
        onExpandedChange?.invoke(next)
    }
    fun toggleExpanded(row: T) {
        val key = rowKey(row)
        val next = if (key in effectiveExpanded) effectiveExpanded - key else effectiveExpanded + key
        updateExpanded(next)
        if (key !in effectiveExpanded && lazy && nodeHasChildren(row) && childrenOf(row).isEmpty() && load != null) {
            loadingKeys[key] = true
            load.invoke(row) { list ->
                loadedChildren[key] = list
                loadingKeys[key] = false
            }
        }
    }

    val childrenOfForTree: (T) -> List<T> = { childrenOf(it) }
    val filteredPredicate: (T) -> Boolean = { row -> rowMatchesFilters(row, leaves, state.filters) }
    val filtered = remember(data, leaves, state.filterValues) {
        filterTree(data, childrenOfForTree, filteredPredicate)
    }
    val sorted = remember(filtered, state.sortKey, state.sortOrder, leaves) {
        val sortKey = state.sortKey
        val order = state.sortOrder
        if (sortKey == null || order == null) filtered
        else {
            val leaf = leaves.firstOrNull { it.key == sortKey }
            if (leaf == null || !leaf.column.sortable) filtered
            else {
                val comparator = Comparator<T> { a, b ->
                    val result = compareRows(leaf, a, b)
                    if (order == ElTableSortOrder.Ascending) result else -result
                }
                val visibleChildren: (T) -> List<T> = { filtered.second[rowKeyOf(it)] ?: childrenOf(it) }
                val sortedTree = sortTree(filtered.first, visibleChildren, comparator)
                sortedTree.first to (filtered.second + sortedTree.second)
            }
        }
    }
    val visibleData = sorted.first
    val childrenMap = sorted.second
    val effectiveChildren: (T) -> List<T> = { childrenMap[rowKeyOf(it)] ?: childrenOf(it) }
    val effectiveHasChildren: (T) -> Boolean = { row ->
        effectiveChildren(row).isNotEmpty() || hasChildren?.invoke(row) == true
    }
    val flatRows = remember(visibleData, effectiveExpanded, childrenMap, loadedChildren.toMap()) {
        flattenRows(visibleData, effectiveExpanded, rowKey, effectiveChildren, effectiveHasChildren)
    }
    val selectableKeys = remember(flatRows, selectable) {
        flatRows.filter { selectable?.invoke(it.row, it.index) != false }.map { it.key }
    }
    var internalSelected by remember(data) { mutableStateOf(selectedKeys) }
    val effectiveSelected = if (onSelectionChange != null) selectedKeys else internalSelected
    fun emitSelection(next: Set<Any>) {
        if (onSelectionChange != null) onSelectionChange.invoke(next) else internalSelected = next
    }
    val allSelected = selectableKeys.isNotEmpty() && selectableKeys.all { it in effectiveSelected }
    val someSelected = selectableKeys.any { it in effectiveSelected }
    val parentMap = remember(data, childrenOfForTree) {
        buildMap<Any, Any> {
            fun walk(rows: List<T>, parent: Any?) {
                rows.forEach { row ->
                    val key = rowKey(row)
                    if (parent != null) put(key, parent)
                    walk(childrenOf(row), key)
                }
            }
            walk(data, null)
        }
    }
    fun descendants(row: T): List<T> = buildList {
        fun walk(node: T) {
            childrenOf(node).forEach { child -> add(child); walk(child) }
        }
        walk(row)
    }
    fun selectionChecked(row: T): Boolean {
        val key = rowKey(row)
        if (checkStrictly) return key in effectiveSelected
        if (key in effectiveSelected) return true
        val descendants = descendants(row).filter { selectable?.invoke(it, 0) != false }
        return descendants.isNotEmpty() && descendants.all { rowKey(it) in effectiveSelected }
    }
    fun selectionIndeterminate(row: T): Boolean {
        if (checkStrictly || selectionChecked(row)) return false
        return descendants(row).any { rowKey(it) in effectiveSelected }
    }
    fun toggleSelection(row: T, checked: Boolean) {
        val key = rowKey(row)
        val targets = if (checkStrictly) setOf(key)
        else (listOf(row) + descendants(row)).map { rowKey(it) }.toSet()
        val next = effectiveSelected.toMutableSet()
        if (checked) next.addAll(targets) else next.removeAll(targets)
        if (!checkStrictly) {
            // Recompute ancestors so parent state follows its children.
            var parent = parentMap[key]
            while (parent != null) {
                val parentRow = flatRows.firstOrNull { it.key == parent }?.row
                if (parentRow != null) {
                    val childKeys = childrenOf(parentRow).filter { selectable?.invoke(it, 0) != false }.map { rowKey(it) }
                    if (childKeys.isNotEmpty() && childKeys.all { it in next }) next.add(parent) else next.remove(parent)
                }
                parent = parentMap[parent]
            }
        }
        emitSelection(next)
    }
    fun toggleSort(leaf: ResolvedColumn<T>) {
        if (!leaf.column.sortable) return
        val next = when {
            state.sortKey != leaf.key -> ElTableSortOrder.Ascending
            state.sortOrder == ElTableSortOrder.Ascending -> ElTableSortOrder.Descending
            else -> null
        }
        state.sortBy(leaf.key, next)
        onSortChange?.invoke(
            leaf.column,
            when (next) {
                ElTableSortOrder.Ascending -> true
                ElTableSortOrder.Descending -> false
                null -> null
            },
        )
    }

    val leftLeaves = if (hasSpan) emptyList() else leaves.filter { it.fixed == ElTableFixed.Left }
    val rightLeaves = if (hasSpan) emptyList() else leaves.filter { it.fixed == ElTableFixed.Right }
    Column(
        modifier = modifier
            .then(if (border) Modifier.border(1.dp, colors.borderLight, RoundedCornerShape(theme.radius.base)) else Modifier)
            .clip(RoundedCornerShape(theme.radius.base))
            .background(colors.background),
    ) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val allWidths = resolveWidths(leaves, maxWidth, tableLayout, leftLeaves, rightLeaves)
            val widths = allWidths.byKey
            val leftWidth = leftLeaves.sumOf { (widths[it.key] ?: it.column.minWidth).value.toDouble() }.toFloat().dp
            val rightWidth = rightLeaves.sumOf { (widths[it.key] ?: it.column.minWidth).value.toDouble() }.toFloat().dp
            val middleLeaves = leaves.filter { it !in leftLeaves && it !in rightLeaves }
            val middleWidth = middleLeaves.sumOf { (widths[it.key] ?: it.column.minWidth).value.toDouble() }.toFloat().dp
            val horizontal = rememberScrollState()
            val headerHeight = if (showHeader) rowHeight * (maxLevel + 1) else 0.dp
            val summaryHeight = if (showSummary) rowHeight else 0.dp
            val bodyHeight = when {
                height != null -> (height - headerHeight - summaryHeight).coerceAtLeast(rowHeight)
                else -> null
            }
            val bodyMaxHeight = when {
                maxHeight != null -> (maxHeight - headerHeight - summaryHeight).coerceAtLeast(rowHeight)
                else -> null
            }
            Column(Modifier.fillMaxWidth()) {
                if (showHeader) {
                    TableHeader(
                        cells = headerCells,
                        leaves = leaves,
                        widths = widths,
                        rowHeight = rowHeight,
                        headerHeight = headerHeight,
                        leftLeaves = leftLeaves,
                        rightLeaves = rightLeaves,
                        leftWidth = leftWidth,
                        rightWidth = rightWidth,
                        middleWidth = middleWidth,
                        horizontal = horizontal,
                        allSelected = allSelected,
                        indeterminate = someSelected && !allSelected,
                        sortOrders = state.sortKey?.let {
                            mapOf(it to (state.sortOrder ?: ElTableSortOrder.Ascending))
                        }.orEmpty(),
                        filters = state.filters,
                        border = border,
                        onSelectAll = {
                            emitSelection(
                                if (allSelected) emptySet() else selectableKeys.toSet()
                            )
                        },
                        onSort = { leaf -> toggleSort(leaf) },
                        onToggleFilter = { leaf, value, checked ->
                            val current = state.filters[leaf.key].orEmpty().toMutableSet()
                            if (checked) current.add(value) else current.remove(value)
                            state.filters[leaf.key] = current.toList()
                        },
                        onClearFilter = { leaf -> state.filters.remove(leaf.key) },
                    )
                }
                val body: @Composable () -> Unit = {
                    if (flatRows.isEmpty() && !loading) {
                        Box(
                            Modifier.fillMaxWidth().height(bodyHeight ?: bodyMaxHeight ?: 80.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            BasicText(
                                text = emptyText ?: locale.table.emptyText,
                                style = theme.typography.small.copy(color = colors.textSecondary),
                            )
                        }
                    } else if (hasSpan) {
                        Box(Modifier.fillMaxWidth().horizontalScroll(horizontal)) {
                            SpanBody(
                                rows = flatRows,
                                leaves = leaves,
                                widths = widths,
                                rowHeight = rowHeight,
                                border = border,
                                stripe = stripe,
                                rowBackground = rowBackground,
                                spanMethod = spanMethod,
                                cell = { leaf, row, rowIndex, flat ->
                                    TableCellContent(
                                        leaf = leaf,
                                        row = row,
                                        rowIndex = rowIndex,
                                        flat = flat,
                                        leaves = leaves,
                                        selection = selection,
                                        selected = selectionChecked(row),
                                        indeterminate = selectionIndeterminate(row),
                                        selectionSelectable = selectable?.invoke(row, rowIndex) != false,
                                        expandContent = expandContent,
                                        expanded = rowKey(row) in effectiveExpanded,
                                        treeExpanded = rowKey(row) in effectiveExpanded,
                                        loading = loadingKeys[rowKey(row)] == true,
                                        treeControl = leaf == firstDataLeaf(leaves) && flat.depth > 0 || (leaf == firstDataLeaf(leaves) && flat.hasChildren),
                                        tooltipFormatter = tooltipFormatter,
                                        showOverflowTooltip = showOverflowTooltip,
                                        onToggleSelection = { toggleSelection(row, !selectionChecked(row)) },
                                        onToggleExpand = { toggleExpanded(row) },
                                        onToggleTree = { toggleExpanded(row) },
                                    )
                                },
                            )
                            }
                    } else {
                        flatRows.forEach { flat ->
                            val row = flat.row
                            TableRow(
                                flat = flat,
                                leaves = leaves,
                                widths = widths,
                                leftLeaves = leftLeaves,
                                rightLeaves = rightLeaves,
                                leftWidth = leftWidth,
                                rightWidth = rightWidth,
                                middleWidth = middleWidth,
                                rowHeight = rowHeight,
                                horizontal = horizontal,
                                border = border,
                                stripe = stripe,
                                highlight = highlightCurrentRow && selectedRowKey == flat.key,
                                background = rowBackground?.invoke(row, flat.index),
                                selection = selection,
                                selected = selectionChecked(row),
                                indeterminate = selectionIndeterminate(row),
                                selectionSelectable = selectable?.invoke(row, flat.index) != false,
                                expandContent = expandContent,
                                expanded = flat.key in effectiveExpanded,
                                treeExpanded = flat.key in effectiveExpanded,
                                loading = loadingKeys[flat.key] == true,
                                tooltipFormatter = tooltipFormatter,
                                showOverflowTooltip = showOverflowTooltip,
                                onToggleSelection = { toggleSelection(row, !selectionChecked(row)) },
                                onToggleExpand = { toggleExpanded(row) },
                                onToggleTree = { toggleExpanded(row) },
                                onClick = onRowClick?.let { callback -> { callback.invoke(row, flat.index) } },
                            )
                            if (expandContent != null && flat.key in effectiveExpanded) {
                                Box(
                                    Modifier.fillMaxWidth().background(colors.background),
                                ) {
                                    expandContent(row, flat.index)
                                }
                            }
                        }
                    }
                    if (loading) {
                        Box(Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                            ElIcon(ElIcons.Loading, size = 20.dp, tint = colors.primary, spin = true)
                        }
                    }
                }
                when {
                    bodyHeight != null -> Box(
                        Modifier.fillMaxWidth().height(bodyHeight).verticalScroll(rememberScrollState()),
                    ) { Column { body() } }
                    bodyMaxHeight != null -> Box(
                        Modifier.fillMaxWidth().heightIn(max = bodyMaxHeight).verticalScroll(rememberScrollState()),
                    ) { Column { body() } }
                    else -> Column { body() }
                }
                if (showSummary) {
                    SummaryRow(
                        leaves = leaves,
                        widths = widths,
                        leftLeaves = leftLeaves,
                        rightLeaves = rightLeaves,
                        leftWidth = leftWidth,
                        rightWidth = rightWidth,
                        middleWidth = middleWidth,
                        rowHeight = rowHeight,
                        horizontal = horizontal,
                        border = border,
                        summary = summaryCells(
                            leaves = leaves,
                            data = flatRows.map { it.row },
                            sumText = sumText ?: locale.table.sumText,
                            summaryMethod = summaryMethod,
                        ),
                    )
                }
            }
        }
    }
}

private fun <T> firstDataLeaf(leaves: List<ResolvedColumn<T>>): ResolvedColumn<T>? =
    leaves.firstOrNull { it.column.type == ElTableColumnType.Default }

private fun <T> allTreeKeys(
    data: List<T>,
    children: ((T) -> List<T>)?,
    hasChildren: ((T) -> Boolean)?,
    rowKey: (T) -> Any,
): Set<Any> {
    if (children == null && hasChildren == null) return emptySet()
    val keys = mutableSetOf<Any>()
    fun walk(rows: List<T>) {
        rows.forEach { row ->
            val child = children?.invoke(row).orEmpty()
            if (child.isNotEmpty()) keys += rowKey(row)
            walk(child)
            if (hasChildren?.invoke(row) == true) keys += rowKey(row)
        }
    }
    walk(data)
    return keys
}

private fun <T> summaryCells(
    leaves: List<ResolvedColumn<T>>,
    data: List<T>,
    sumText: String?,
    summaryMethod: ((List<ElTableColumn<T>>, List<T>) -> List<String>)?,
): List<String> {
    if (summaryMethod != null) {
        val result = summaryMethod.invoke(leaves.map { it.column }, data)
        return List(leaves.size) { result.getOrElse(it) { "" } }
    }
    val firstData = leaves.indexOfFirst { it.column.type == ElTableColumnType.Default }
    return leaves.mapIndexed { index, leaf ->
        if (index != firstData) {
            val values = data.mapNotNull { leaf.column.property?.invoke(it) as? Number }
            if (values.isNotEmpty()) {
                val sum = values.sumOf { it.toDouble() }
                if (sum % 1.0 == 0.0) sum.toLong().toString() else sum.toString()
            } else ""
        } else {
            sumText ?: ""
        }
    }
}

@Composable
private fun <T> TableHeader(
    cells: List<HeaderCell<T>>,
    leaves: List<ResolvedColumn<T>>,
    widths: Map<String, Dp>,
    rowHeight: Dp,
    headerHeight: Dp,
    leftLeaves: List<ResolvedColumn<T>>,
    rightLeaves: List<ResolvedColumn<T>>,
    leftWidth: Dp,
    rightWidth: Dp,
    middleWidth: Dp,
    horizontal: androidx.compose.foundation.ScrollState,
    allSelected: Boolean,
    indeterminate: Boolean,
    sortOrders: Map<String, ElTableSortOrder>,
    filters: Map<String, List<Any?>>,
    border: Boolean,
    onSelectAll: () -> Unit,
    onSort: (ResolvedColumn<T>) -> Unit,
    onToggleFilter: (ResolvedColumn<T>, Any?, Boolean) -> Unit,
    onClearFilter: (ResolvedColumn<T>) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val allLeaves = leaves
    Row(
        Modifier.fillMaxWidth().height(headerHeight).background(colors.fillLight),
    ) {
        if (leftLeaves.isNotEmpty()) {
            HeaderPane(
                cells = cells, paneLeaves = leftLeaves, allLeaves = allLeaves, widths = widths,
                paneWidth = leftWidth, rowHeight = rowHeight, headerHeight = headerHeight,
                allSelected = allSelected, indeterminate = indeterminate,
                sortOrders = sortOrders, filters = filters, border = border,
                onSelectAll = onSelectAll, onSort = onSort, onToggleFilter = onToggleFilter,
                onClearFilter = onClearFilter,
            )
        }
        Box(
            Modifier.weight(1f).height(headerHeight).horizontalScroll(horizontal),
        ) {
            HeaderPane(
                cells = cells, paneLeaves = leaves.filter { it !in leftLeaves && it !in rightLeaves },
                allLeaves = allLeaves, widths = widths, paneWidth = middleWidth,
                rowHeight = rowHeight, headerHeight = headerHeight,
                allSelected = allSelected, indeterminate = indeterminate,
                sortOrders = sortOrders, filters = filters, border = border,
                onSelectAll = onSelectAll, onSort = onSort, onToggleFilter = onToggleFilter,
                onClearFilter = onClearFilter,
            )
        }
        if (rightLeaves.isNotEmpty()) {
            HeaderPane(
                cells = cells, paneLeaves = rightLeaves, allLeaves = allLeaves, widths = widths,
                paneWidth = rightWidth, rowHeight = rowHeight, headerHeight = headerHeight,
                allSelected = allSelected, indeterminate = indeterminate,
                sortOrders = sortOrders, filters = filters, border = border,
                onSelectAll = onSelectAll, onSort = onSort, onToggleFilter = onToggleFilter,
                onClearFilter = onClearFilter,
            )
        }
    }
}

@Composable
private fun <T> HeaderPane(
    cells: List<HeaderCell<T>>,
    paneLeaves: List<ResolvedColumn<T>>,
    allLeaves: List<ResolvedColumn<T>>,
    widths: Map<String, Dp>,
    paneWidth: Dp,
    rowHeight: Dp,
    headerHeight: Dp,
    allSelected: Boolean,
    indeterminate: Boolean,
    sortOrders: Map<String, ElTableSortOrder>,
    filters: Map<String, List<Any?>>,
    border: Boolean,
    onSelectAll: () -> Unit,
    onSort: (ResolvedColumn<T>) -> Unit,
    onToggleFilter: (ResolvedColumn<T>, Any?, Boolean) -> Unit,
    onClearFilter: (ResolvedColumn<T>) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val paneKeys = paneLeaves.map { it.key }.toSet()
    val paneIndex = paneLeaves.mapIndexed { index, leaf -> leaf.key to index }.toMap()
    val paneCellWidths = paneLeaves.associate { it.key to (widths[it.key] ?: it.column.minWidth) }
    val paneCells = cells.filter { cell ->
        val cellLeaves = allLeaves.subList(cell.leafStart, (cell.leafStart + cell.leafSpan).coerceAtMost(allLeaves.size))
        cellLeaves.isNotEmpty() && cellLeaves.all { it.key in paneKeys }
    }
    Box(Modifier.width(paneWidth).height(headerHeight).background(colors.fillLight)) {
        paneCells.forEach { cell ->
            val firstLeaf = allLeaves.getOrNull(cell.leafStart) ?: return@forEach
            val start = paneIndex[firstLeaf.key] ?: return@forEach
            val x = paneLeaves.take(start).sumOf { (paneCellWidths[it.key] ?: it.column.minWidth).value.toDouble() }.toFloat().dp
            val w = allLeaves.subList(cell.leafStart, (cell.leafStart + cell.leafSpan).coerceAtMost(allLeaves.size))
                .sumOf { (paneCellWidths[it.key] ?: it.column.minWidth).value.toDouble() }.toFloat().dp
            Box(
                Modifier
                    .offset(x = x, y = rowHeight * cell.level)
                    .width(w)
                    .height(rowHeight * cell.rowSpan)
                    .then(if (border) Modifier.tableCellBorder(colors.borderLighter) else Modifier),
            ) {
                HeaderCellView(
                    cell = cell,
                    allSelected = allSelected,
                    indeterminate = indeterminate,
                    sortOrders = sortOrders,
                    filters = filters,
                    onSelectAll = onSelectAll,
                    onSort = onSort,
                    onToggleFilter = onToggleFilter,
                    onClearFilter = onClearFilter,
                )
            }
        }
    }
}

@Composable
private fun <T> HeaderCellView(
    cell: HeaderCell<T>,
    allSelected: Boolean,
    indeterminate: Boolean,
    sortOrders: Map<String, ElTableSortOrder>,
    filters: Map<String, List<Any?>>,
    onSelectAll: () -> Unit,
    onSort: (ResolvedColumn<T>) -> Unit,
    onToggleFilter: (ResolvedColumn<T>, Any?, Boolean) -> Unit,
    onClearFilter: (ResolvedColumn<T>) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val tableLocale = LocalElConfig.current.locale.table
    val column = cell.column
    val effectiveAlign = if (column?.children?.isNotEmpty() == true) ElTextAlign.Center else cell.align
    val alignment = when (effectiveAlign) {
        ElTextAlign.Start -> Alignment.CenterStart
        ElTextAlign.Center -> Alignment.Center
        ElTextAlign.End -> Alignment.CenterEnd
    }
    Box(
        Modifier.fillMaxSize().background(colors.fillLight).padding(horizontal = 10.dp),
        contentAlignment = alignment,
    ) {
        when {
            column?.type == ElTableColumnType.Selection -> {
                ElCheckbox(
                    checked = allSelected,
                    indeterminate = indeterminate,
                    onCheckedChange = { onSelectAll() },
                    modifier = Modifier.semantics { contentDescription = tableLocale.selectAllLabel },
                )
            }
            column?.type == ElTableColumnType.Expand -> Unit
            column?.type == ElTableColumnType.Index && cell.label.isBlank() -> Unit
            column?.header != null -> {
                Box(Modifier.fillMaxWidth(), contentAlignment = alignment) { column.header.invoke() }
            }
            else -> {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = when (effectiveAlign) {
                        ElTextAlign.Start -> Arrangement.Start
                        ElTextAlign.Center -> Arrangement.Center
                        ElTextAlign.End -> Arrangement.End
                    },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val labelContent: @Composable () -> Unit = {
                        BasicText(
                            text = cell.label,
                            style = theme.typography.small.copy(
                                fontWeight = FontWeight.Medium,
                                color = colors.textRegular,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                    }
                    if (column != null && column.sortable) {
                        Row(
                            Modifier
                                .clickable { onSort(ResolvedColumn(column, cell.key, cell.fixed)) }
                                .semantics {
                                    contentDescription = tableLocale.sortLabel.replace("{column}", cell.label)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) { labelContent() }
                    } else {
                        labelContent()
                    }
                    if (column != null && column.filters.isNotEmpty()) {
                        Spacer(Modifier.width(4.dp))
                        FilterTrigger(
                            leaf = ResolvedColumn(column, cell.key, cell.fixed),
                            filters = filters,
                            onToggleFilter = onToggleFilter,
                            onClearFilter = onClearFilter,
                        )
                    }
                    if (column != null && column.sortable) {
                        Spacer(Modifier.width(2.dp))
                        Column(Modifier.width(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            ElIcon(
                                ElIcons.CaretTop,
                                size = 10.dp,
                                tint = if (sortOrders[cell.key] == ElTableSortOrder.Ascending) colors.primary else colors.textPlaceholder,
                            )
                            ElIcon(
                                ElIcons.CaretBottom,
                                size = 10.dp,
                                tint = if (sortOrders[cell.key] == ElTableSortOrder.Descending) colors.primary else colors.textPlaceholder,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> FilterTrigger(
    leaf: ResolvedColumn<T>,
    filters: Map<String, List<Any?>>,
    onToggleFilter: (ResolvedColumn<T>, Any?, Boolean) -> Unit,
    onClearFilter: (ResolvedColumn<T>) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val tableLocale = LocalElConfig.current.locale.table
    val selected = filters[leaf.key].orEmpty()
    ElPopover(
        title = leaf.column.label,
        width = 180.dp,
        triggerContent = {
            Box(
                Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .semantics {
                        contentDescription = tableLocale.filterLabel.replace("{column}", leaf.column.label)
                    },
                contentAlignment = Alignment.Center,
            ) {
                ElIcon(
                    ElIcons.Filter,
                    size = 12.dp,
                    tint = if (selected.isNotEmpty()) colors.primary else colors.textSecondary,
                )
            }
        },
    ) {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            leaf.column.filters.forEach { filter ->
                ElCheckbox(
                    checked = selected.contains(filter.value),
                    onCheckedChange = { checked -> onToggleFilter(leaf, filter.value, checked) },
                    label = filter.text,
                    modifier = Modifier.semantics {
                        contentDescription = "${tableLocale.filterLabel.replace("{column}", leaf.column.label)}: ${filter.text}"
                    },
                )
            }
            if (selected.isNotEmpty()) {
                ElButton(
                    onClick = { onClearFilter(leaf) },
                    text = tableLocale.resetFilter,
                    size = ElSize.Small,
                    textButton = true,
                )
            }
        }
    }
}

@Composable
private fun <T> TableRow(
    flat: FlatRow<T>,
    leaves: List<ResolvedColumn<T>>,
    widths: Map<String, Dp>,
    leftLeaves: List<ResolvedColumn<T>>,
    rightLeaves: List<ResolvedColumn<T>>,
    leftWidth: Dp,
    rightWidth: Dp,
    middleWidth: Dp,
    rowHeight: Dp,
    horizontal: androidx.compose.foundation.ScrollState,
    border: Boolean,
    stripe: Boolean,
    highlight: Boolean,
    background: Color?,
    selection: Boolean,
    selected: Boolean,
    indeterminate: Boolean,
    selectionSelectable: Boolean,
    expandContent: (@Composable (T, Int) -> Unit)?,
    expanded: Boolean,
    treeExpanded: Boolean,
    loading: Boolean,
    tooltipFormatter: ((ElTableTooltipData<T>) -> String?)?,
    showOverflowTooltip: Boolean,
    onToggleSelection: () -> Unit,
    onToggleExpand: () -> Unit,
    onToggleTree: () -> Unit,
    onClick: (() -> Unit)?,
    fixedHeight: Boolean = true,
    crossHovering: Boolean = false,
    hoveredColumnKey: String? = null,
    onCellHover: ((String, Boolean) -> Unit)? = null,
    treeControlKey: String? = null,
    indentSize: Dp = 16.dp,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val stripeBg = if (stripe && flat.index % 2 == 1) colors.fillLighter else Color.Transparent
    val bg = background ?: when {
        highlight -> colors.primary.copy(alpha = 0.10f)
        hovered -> colors.fillLight
        else -> stripeBg
    }
    val firstData = firstDataLeaf(leaves)
    fun treeControl(leaf: ResolvedColumn<T>): Boolean =
        if (treeControlKey != null) leaf.key == treeControlKey else leaf == firstData
    val rowModifier = if (fixedHeight) Modifier.height(rowHeight) else Modifier.heightIn(min = rowHeight)
    val cell: @Composable (ResolvedColumn<T>) -> Unit = { leaf ->
        TableCellBox(
            leaf = leaf,
            widths = widths,
            rowHeight = rowHeight,
            border = border,
            borderColor = colors.borderLighter,
            fixedHeight = fixedHeight,
            highlighted = crossHovering && hoveredColumnKey == leaf.key,
            crossHovering = crossHovering,
            onHover = onCellHover?.let { callback -> { hovered -> callback(leaf.key, hovered) } },
        ) {
            TableCellContent(
                leaf = leaf, row = flat.row, rowIndex = flat.index, flat = flat,
                leaves = leaves, selection = selection, selected = selected,
                indeterminate = indeterminate, selectionSelectable = selectionSelectable,
                expandContent = expandContent, expanded = expanded, treeExpanded = treeExpanded,
                loading = loading, treeControl = treeControl(leaf),
                tooltipFormatter = tooltipFormatter, showOverflowTooltip = showOverflowTooltip,
                onToggleSelection = onToggleSelection, onToggleExpand = onToggleExpand,
                onToggleTree = onToggleTree, indentSize = indentSize,
                singleLine = fixedHeight,
            )
        }
    }
    Row(
        Modifier
            .fillMaxWidth()
            .then(rowModifier)
            .background(bg)
            .hoverable(interaction)
            .then(if (onClick != null) Modifier.clickable { onClick.invoke() } else Modifier),
    ) {
        if (leftLeaves.isNotEmpty()) {
            Row(Modifier.width(leftWidth).then(rowModifier)) {
                leftLeaves.forEach { leaf -> cell(leaf) }
            }
        }
        Box(Modifier.weight(1f).then(rowModifier).horizontalScroll(horizontal)) {
            Row(Modifier.width(middleWidth).then(rowModifier)) {
                leaves.filter { it !in leftLeaves && it !in rightLeaves }.forEach { leaf -> cell(leaf) }
            }
        }
        if (rightLeaves.isNotEmpty()) {
            Row(Modifier.width(rightWidth).then(rowModifier)) {
                rightLeaves.forEach { leaf -> cell(leaf) }
            }
        }
    }
}

@Composable
private fun <T> TableCellBox(
    leaf: ResolvedColumn<T>,
    widths: Map<String, Dp>,
    rowHeight: Dp,
    border: Boolean,
    borderColor: Color,
    fixedHeight: Boolean = true,
    highlighted: Boolean = false,
    crossHovering: Boolean = false,
    onHover: ((Boolean) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val width = widths[leaf.key] ?: leaf.column.minWidth
    val alignment = when (leaf.column.align) {
        ElTextAlign.Start -> Alignment.CenterStart
        ElTextAlign.Center -> Alignment.Center
        ElTextAlign.End -> Alignment.CenterEnd
    }
    val interaction = remember { MutableInteractionSource() }
    if (crossHovering) {
        val hoveredState = interaction.collectIsHoveredAsState()
        LaunchedEffect(interaction) {
            interaction.interactions.collect { event ->
                when (event) {
                    is androidx.compose.foundation.interaction.HoverInteraction.Enter -> onHover?.invoke(true)
                    is androidx.compose.foundation.interaction.HoverInteraction.Exit -> onHover?.invoke(false)
                    else -> Unit
                }
            }
        }
        DisposableEffect(Unit) {
            onDispose {
                if (onHover != null && hoveredState.value) onHover.invoke(false)
            }
        }
    }
    val heightModifier = if (fixedHeight) Modifier.height(rowHeight) else Modifier.heightIn(min = rowHeight)
    Box(
        Modifier
            .width(width)
            .then(heightModifier)
            .then(
                if (highlighted) {
                    Modifier.background(LocalElTheme.current.colors.primary.copy(alpha = 0.07f))
                } else Modifier,
            )
            .then(if (crossHovering) Modifier.hoverable(interaction) else Modifier)
            .then(if (border) Modifier.tableCellBorder(borderColor) else Modifier)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = alignment,
    ) { content() }
}

private fun Modifier.tableCellBorder(color: Color, borderWidth: Dp = 0.5.dp): Modifier =
    this.drawBehind {
        val stroke = borderWidth.toPx()
        drawLine(color, Offset(size.width, 0f), Offset(size.width, size.height), stroke)
        drawLine(color, Offset(0f, size.height), Offset(size.width, size.height), stroke)
    }

@Composable
private fun <T> TableCellContent(
    leaf: ResolvedColumn<T>,
    row: T,
    rowIndex: Int,
    flat: FlatRow<T>,
    leaves: List<ResolvedColumn<T>>,
    selection: Boolean,
    selected: Boolean,
    indeterminate: Boolean,
    selectionSelectable: Boolean,
    expandContent: (@Composable (T, Int) -> Unit)?,
    expanded: Boolean,
    treeExpanded: Boolean,
    loading: Boolean,
    treeControl: Boolean,
    tooltipFormatter: ((ElTableTooltipData<T>) -> String?)?,
    showOverflowTooltip: Boolean,
    onToggleSelection: () -> Unit,
    onToggleExpand: () -> Unit,
    onToggleTree: () -> Unit,
    indentSize: Dp = 16.dp,
    singleLine: Boolean = true,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val tableLocale = LocalElConfig.current.locale.table
    val column = leaf.column
    val alignment = when (column.align) {
        ElTextAlign.Start -> TextAlign.Start
        ElTextAlign.Center -> TextAlign.Center
        ElTextAlign.End -> TextAlign.End
    }
    when (column.type) {
        ElTableColumnType.Selection -> {
            ElCheckbox(
                checked = selected,
                indeterminate = indeterminate,
                disabled = !selectionSelectable,
                onCheckedChange = { onToggleSelection() },
                modifier = Modifier.semantics { contentDescription = tableLocale.selectRowLabel },
            )
        }
        ElTableColumnType.Expand -> {
            if (expandContent != null) {
                ExpandArrow(expanded, loading, onToggleExpand)
            }
        }
        ElTableColumnType.Index -> {
            BasicText(
                text = (column.index?.invoke(rowIndex) ?: (rowIndex + 1)).toString(),
                style = theme.typography.small.copy(color = colors.textRegular, textAlign = alignment),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        ElTableColumnType.Default -> {
            val body: @Composable () -> Unit = {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (treeControl) {
                        Spacer(Modifier.width(indentSize * flat.depth))
                        if (flat.hasChildren) {
                            if (loading) {
                                ElIcon(ElIcons.Loading, size = 16.dp, tint = colors.textSecondary, spin = true)
                            } else {
                                ElIcon(
                                    ElIcons.ArrowRight,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .rotate(if (treeExpanded) 90f else 0f)
                                        .clickable { onToggleTree() }
                                        .semantics {
                                            contentDescription = if (treeExpanded) tableLocale.collapseRowLabel else tableLocale.expandRowLabel
                                        },
                                    tint = colors.textSecondary,
                                )
                            }
                            Spacer(Modifier.width(4.dp))
                        }
                    }
                    if (column.cell != null) {
                        Box(Modifier.weight(1f)) { column.cell.invoke(row, rowIndex) }
                    } else {
                        val oneLine = singleLine || showOverflowTooltip || column.showOverflowTooltip
                        BasicText(
                            text = column.text(row, rowIndex),
                            style = theme.typography.small.copy(color = colors.textRegular, textAlign = alignment),
                            maxLines = if (oneLine) 1 else Int.MAX_VALUE,
                            overflow = if (oneLine) TextOverflow.Ellipsis else TextOverflow.Clip,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            val tooltipText = when {
                column.showOverflowTooltip || showOverflowTooltip ->
                    column.tooltipFormatter?.invoke(ElTableTooltipData(row, rowIndex, column, column.property?.invoke(row)))
                        ?: tooltipFormatter?.invoke(ElTableTooltipData(row, rowIndex, column, column.property?.invoke(row)))
                        ?: column.text(row, rowIndex)
                else -> null
            }
            if (!tooltipText.isNullOrBlank()) {
                ElTooltip(text = tooltipText, maxWidth = 320.dp) { body() }
            } else {
                body()
            }
        }
    }
}

@Composable
private fun ExpandArrow(expanded: Boolean, loading: Boolean, onClick: () -> Unit) {
    val colors = LocalElTheme.current.colors
    val tableLocale = LocalElConfig.current.locale.table
    if (loading) {
        ElIcon(ElIcons.Loading, size = 16.dp, tint = colors.textSecondary, spin = true)
    } else {
        ElIcon(
            ElIcons.ArrowRight,
            modifier = Modifier
                .size(16.dp)
                .rotate(if (expanded) 90f else 0f)
                .clickable { onClick() }
                .semantics {
                    contentDescription = if (expanded) tableLocale.collapseRowLabel else tableLocale.expandRowLabel
                },
            tint = colors.textSecondary,
        )
    }
}

@Composable
private fun <T> SpanBody(
    rows: List<FlatRow<T>>,
    leaves: List<ResolvedColumn<T>>,
    widths: Map<String, Dp>,
    rowHeight: Dp,
    border: Boolean,
    stripe: Boolean,
    rowBackground: ((T, Int) -> Color?)?,
    spanMethod: ((row: T, rowIndex: Int, columnIndex: Int, column: ElTableColumn<T>) -> ElTableCellSpan?)?,
    cell: @Composable (ResolvedColumn<T>, T, Int, FlatRow<T>) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val density = LocalDensity.current
    val widthPx = with(density) { leaves.sumOf { (widths[it.key] ?: it.column.minWidth).value.toDouble() }.toFloat().dp.roundToPx() }
    val rowHeightPx = with(density) { rowHeight.roundToPx() }
    if (rows.isEmpty()) return
    val placements = remember(rows, leaves, widths, border, spanMethod) {
        val occupied = Array(rows.size) { BooleanArray(leaves.size) }
        buildList<SpanPlacement> {
            rows.forEachIndexed { rowIndex, flat ->
                val row = flat.row
                leaves.forEachIndexed { colIndex, leaf ->
                    if (occupied[rowIndex][colIndex]) return@forEachIndexed
                    val explicit = spanMethod?.invoke(row, rowIndex, colIndex, leaf.column)
                    val colSpan = explicit?.colSpan ?: leaf.column.colSpan?.invoke(row, rowIndex, colIndex) ?: 1
                    val rowSpan = explicit?.rowSpan ?: leaf.column.rowSpan?.invoke(row, rowIndex, colIndex) ?: 1
                    if (colSpan <= 0 || rowSpan <= 0) return@forEachIndexed
                    val cs = colSpan.coerceAtMost(leaves.size - colIndex)
                    val rs = rowSpan.coerceAtMost(rows.size - rowIndex)
                    for (r in rowIndex until rowIndex + rs) {
                        for (c in colIndex until colIndex + cs) occupied[r][c] = true
                    }
                    add(SpanPlacement(rowIndex, colIndex, rs, cs))
                }
            }
        }
    }
    SubcomposeLayout(
        Modifier.width(with(density) { widthPx.toDp() }).height(rowHeight * rows.size),
    ) { _ ->
        val measurable = placements.mapIndexed { index, placement ->
            val x = leaves.take(placement.col).sumOf { (widths[it.key] ?: it.column.minWidth).value.toDouble() }.toFloat().dp.roundToPx()
            val w = leaves.drop(placement.col).take(placement.colSpan)
                .sumOf { (widths[it.key] ?: it.column.minWidth).value.toDouble() }.toFloat().dp.roundToPx()
            val h = rowHeightPx * placement.rowSpan
            val row = rows[placement.row]
            val leaf = leaves[placement.col]
            val bg = rowBackground?.invoke(row.row, placement.row) ?: when {
                stripe && row.index % 2 == 1 -> colors.fillLighter
                else -> Color.Transparent
            }
            val placeables = subcompose(index) {
                Box(
                    Modifier
                        .width(with(density) { w.toDp() })
                        .height(with(density) { h.toDp() })
                        .background(bg)
                        .then(if (border) Modifier.tableCellBorder(colors.borderLighter) else Modifier)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = when (leaf.column.align) {
                        ElTextAlign.Start -> Alignment.CenterStart
                        ElTextAlign.Center -> Alignment.Center
                        ElTextAlign.End -> Alignment.CenterEnd
                    },
                ) { cell(leaf, row.row, row.index, row) }
            }.map { it.measure(Constraints.fixed(w, h)) }
            Triple(placeables, with(density) { x.toDp() }, with(density) { (rowHeightPx * placement.row).toDp() })
        }
        layout(widthPx, rowHeightPx * rows.size) {
            measurable.forEach { (placeables, x, y) ->
                placeables.forEach { it.place(x.roundToPx(), y.roundToPx()) }
            }
        }
    }
}

private data class SpanPlacement(val row: Int, val col: Int, val rowSpan: Int, val colSpan: Int)

@Composable
private fun <T> SummaryRow(
    leaves: List<ResolvedColumn<T>>,
    widths: Map<String, Dp>,
    leftLeaves: List<ResolvedColumn<T>>,
    rightLeaves: List<ResolvedColumn<T>>,
    leftWidth: Dp,
    rightWidth: Dp,
    middleWidth: Dp,
    rowHeight: Dp,
    horizontal: androidx.compose.foundation.ScrollState,
    border: Boolean,
    summary: List<String>,
    ) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val byKey = leaves.mapIndexed { index, leaf -> leaf.key to summary.getOrElse(index) { "" } }.toMap()
    Row(Modifier.fillMaxWidth().height(rowHeight).background(colors.fillLight)) {
        if (leftLeaves.isNotEmpty()) {
            Row(Modifier.width(leftWidth).height(rowHeight)) {
                leftLeaves.forEach { leaf -> SummaryCell(leaf, widths, rowHeight, border, byKey[leaf.key].orEmpty()) }
            }
        }
        Box(Modifier.weight(1f).height(rowHeight).horizontalScroll(horizontal)) {
            Row(Modifier.width(middleWidth).height(rowHeight)) {
                leaves.filter { it !in leftLeaves && it !in rightLeaves }.forEach { leaf ->
                    SummaryCell(leaf, widths, rowHeight, border, byKey[leaf.key].orEmpty())
                }
            }
        }
        if (rightLeaves.isNotEmpty()) {
            Row(Modifier.width(rightWidth).height(rowHeight)) {
                rightLeaves.forEach { leaf -> SummaryCell(leaf, widths, rowHeight, border, byKey[leaf.key].orEmpty()) }
            }
        }
    }
}

@Composable
private fun <T> SummaryCell(
    leaf: ResolvedColumn<T>,
    widths: Map<String, Dp>,
    rowHeight: Dp,
    border: Boolean,
    text: String,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val width = widths[leaf.key] ?: leaf.column.minWidth
    Box(
        Modifier
            .width(width)
            .height(rowHeight)
            .then(if (border) Modifier.tableCellBorder(colors.borderLighter) else Modifier)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = when (leaf.column.align) {
            ElTextAlign.Start -> Alignment.CenterStart
            ElTextAlign.Center -> Alignment.Center
            ElTextAlign.End -> Alignment.CenterEnd
        },
    ) {
        BasicText(
            text = text,
            style = theme.typography.small.copy(color = colors.textRegular, fontWeight = FontWeight.Medium),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Lazy table for large data sets. Only rows intersecting the viewport are
 * composed; the column model, fixed panes, sorting and filtering are shared
 * with [ElTable].
 */
@Composable
fun <T> ElVirtualizedTable(
    data: List<T>,
    columns: List<ElTableColumn<T>>,
    height: Dp,
    modifier: Modifier = Modifier,
    rowKey: (T) -> Any = { it.hashCode() },
    border: Boolean = false,
    stripe: Boolean = false,
    size: ElSize? = null,
    showHeader: Boolean = true,
    emptyText: String? = null,
    onRowClick: ((T, Int) -> Unit)? = null,
    rowHeight: Dp? = null,
    highlightCurrentRow: Boolean = false,
    selectedRowKey: Any? = null,
    selection: Boolean = false,
    selectedKeys: Set<Any> = emptySet(),
    onSelectionChange: ((Set<Any>) -> Unit)? = null,
    onSortChange: ((column: ElTableColumn<T>, ascending: Boolean?) -> Unit)? = null,
    loading: Boolean = false,
    defaultSort: ElTableSort? = null,
    state: ElTableState = rememberElTableState(defaultSort),
    tableLayout: ElTableLayout = ElTableLayout.Fixed,
    rowBackground: ((row: T, index: Int) -> Color?)? = null,
    tooltipFormatter: ((ElTableTooltipData<T>) -> String?)? = null,
    showOverflowTooltip: Boolean = false,
    listState: LazyListState = rememberLazyListState(),
    horizontalScrollState: ScrollState = rememberScrollState(),
    fixedData: List<T> = emptyList(),
    estimatedRowHeight: Dp? = null,
    crossHovering: Boolean = false,
    fixedRowBackground: Color? = null,
    sortState: Map<String, ElTableSortOrder>? = null,
    onSortStateChange: ((Map<String, ElTableSortOrder>) -> Unit)? = null,
    children: ((T) -> List<T>)? = null,
    expandColumnKey: String? = null,
    expandedRowKeys: Set<Any>? = null,
    defaultExpandedRowKeys: Set<Any> = emptySet(),
    onExpandedChange: ((Set<Any>) -> Unit)? = null,
    indentSize: Dp = 16.dp,
    spanMethod: ((row: T, rowIndex: Int, columnIndex: Int, column: ElTableColumn<T>) -> ElTableCellSpan?)? = null,
    detailContent: (@Composable (row: T, index: Int) -> Unit)? = null,
    footerHeight: Dp = 40.dp,
    footer: (@Composable () -> Unit)? = null,
    empty: (@Composable () -> Unit)? = null,
    overlay: (@Composable () -> Unit)? = null,
    width: Dp? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val showOverflowTooltip = showOverflowTooltip || LocalElConfig.current.table.showOverflowTooltip == true
    val resolvedSize = resolveElSize(size)
    val rowHeightValue = rowHeight ?: when (resolvedSize) {
        ElSize.Large -> 48.dp
        ElSize.Default -> 40.dp
        ElSize.Small -> 32.dp
    }
    val dynamicHeight = estimatedRowHeight != null
    val minRowHeight = estimatedRowHeight ?: rowHeightValue
    val columnTree = remember(columns, selection, detailContent) {
        prepareTree(columns, selection, detailContent)
    }
    val leaves = remember(columnTree) { flattenColumns(columnTree) }
    val maxLevel = remember(columnTree) { maxHeaderLevel(columnTree) }
    val headerCells = remember(columnTree, maxLevel) {
        buildHeaderCells(columnTree, null, "", 0, maxLevel, 0).first
    }
    val filterValues = state.filterValues
    val filtered = remember(data, leaves, filterValues) {
        if (filterValues.isEmpty()) data
        else data.filter { row -> rowMatchesFilters(row, leaves, filterValues) }
    }
    val hasSpan = spanMethod != null || leaves.any { it.column.rowSpan != null || it.column.colSpan != null }
    val controlledSort = sortState
    val sortOrders = controlledSort ?: state.sortKey?.let {
        mapOf(it to (state.sortOrder ?: ElTableSortOrder.Ascending))
    }.orEmpty()
    val sorted = remember(filtered, sortOrders, leaves) {
        if (sortOrders.isEmpty()) filtered
        else {
            val sortColumns = sortOrders.keys.mapNotNull { key -> leaves.firstOrNull { it.key == key } }
            if (sortColumns.isEmpty()) filtered
            else filtered.sortedWith(Comparator { a, b ->
                var result = 0
                for (leaf in sortColumns) {
                    val order = sortOrders[leaf.key] ?: continue
                    result = compareRows(leaf, a, b)
                    if (result != 0) {
                        result = if (order == ElTableSortOrder.Ascending) result else -result
                        break
                    }
                }
                result
            })
        }
    }
    var internalExpanded by remember(data) { mutableStateOf(defaultExpandedRowKeys) }
    val effectiveExpanded = expandedRowKeys ?: internalExpanded
    fun emitExpanded(next: Set<Any>) {
        if (expandedRowKeys == null) internalExpanded = next
        onExpandedChange?.invoke(next)
    }
    fun toggleExpanded(key: Any) {
        emitExpanded(if (key in effectiveExpanded) effectiveExpanded - key else effectiveExpanded + key)
    }
    val treeMode = expandColumnKey != null
    val treeChildren: (T) -> List<T> = { children?.invoke(it).orEmpty() }
    val treeHasChildren: (T) -> Boolean = { treeChildren(it).isNotEmpty() }
    val flatRows = remember(sorted, effectiveExpanded, rowKey, children, expandColumnKey, hasSpan) {
        when {
            treeMode -> flattenRows(sorted, effectiveExpanded, rowKey, treeChildren, treeHasChildren)
            hasSpan -> sorted.mapIndexed { index, row -> FlatRow(row, rowKey(row), index, 0, null, false) }
            else -> emptyList()
        }
    }
    var internalSelected by remember(data) { mutableStateOf(selectedKeys) }
    val effectiveSelected = if (onSelectionChange != null) selectedKeys else internalSelected
    fun emitSelection(next: Set<Any>) {
        if (onSelectionChange != null) onSelectionChange.invoke(next) else internalSelected = next
    }
    val allRowKeys = remember(sorted, flatRows, treeMode, hasSpan, selection, rowKey) {
        if (!selection) emptyList()
        else if (treeMode || hasSpan) flatRows.map { it.key }
        else sorted.map(rowKey)
    }
    val allSelected = selection && allRowKeys.isNotEmpty() && allRowKeys.all { it in effectiveSelected }
    val someSelected = selection && allRowKeys.any { it in effectiveSelected }
    fun toggleRowSelection(flat: FlatRow<T>) {
        val next = effectiveSelected.toMutableSet()
        if (flat.key in next) next.remove(flat.key) else next.add(flat.key)
        emitSelection(next)
    }
    fun handleSort(leaf: ResolvedColumn<T>) {
        if (!leaf.column.sortable) return
        if (sortState != null) {
            val current = sortState[leaf.key]
            val nextOrder = when (current) {
                null -> ElTableSortOrder.Ascending
                ElTableSortOrder.Ascending -> ElTableSortOrder.Descending
                ElTableSortOrder.Descending -> null
            }
            val next = sortState.toMutableMap()
            if (nextOrder == null) next.remove(leaf.key) else next[leaf.key] = nextOrder
            onSortStateChange?.invoke(next)
            onSortChange?.invoke(
                leaf.column,
                when (nextOrder) {
                    ElTableSortOrder.Ascending -> true
                    ElTableSortOrder.Descending -> false
                    null -> null
                },
            )
        } else {
            val next = when {
                state.sortKey != leaf.key -> ElTableSortOrder.Ascending
                state.sortOrder == ElTableSortOrder.Ascending -> ElTableSortOrder.Descending
                else -> null
            }
            state.sortBy(leaf.key, next)
            onSortChange?.invoke(
                leaf.column,
                when (next) {
                    ElTableSortOrder.Ascending -> true
                    ElTableSortOrder.Descending -> false
                    null -> null
                },
            )
        }
    }
    val hoverCounts = remember { mutableStateMapOf<String, Int>() }
    var hoveredColumnKey by remember { mutableStateOf<String?>(null) }
    fun updateHoveredColumn(key: String, hovered: Boolean) {
        val next = if (hovered) (hoverCounts[key] ?: 0) + 1 else (hoverCounts[key] ?: 1) - 1
        if (next <= 0) hoverCounts.remove(key) else hoverCounts[key] = next
        hoveredColumnKey = hoverCounts.entries.firstOrNull { it.value > 0 }?.key
    }
    val headerHeight = if (showHeader) rowHeightValue * (maxLevel + 1) else 0.dp
    val resolvedFooterHeight = if (footer != null) footerHeight else 0.dp
    val fixedRowsHeight = rowHeightValue * fixedData.size
    val bodyHeight = (height - headerHeight - resolvedFooterHeight - fixedRowsHeight).coerceAtLeast(minRowHeight)
    val firstData = firstDataLeaf(leaves)
    Column(
        modifier = modifier
            .then(if (width != null) Modifier.width(width) else Modifier)
            .then(if (border) Modifier.border(1.dp, colors.borderLight, RoundedCornerShape(theme.radius.base)) else Modifier)
            .clip(RoundedCornerShape(theme.radius.base))
            .background(colors.background)
            .height(height),
    ) {
        val content: @Composable (Dp) -> Unit = { availableWidth ->
            val maxWidth = availableWidth
            val leftLeaves = if (hasSpan) emptyList() else leaves.filter { it.fixed == ElTableFixed.Left }
            val rightLeaves = if (hasSpan) emptyList() else leaves.filter { it.fixed == ElTableFixed.Right }
            val allWidths = resolveWidths(leaves, maxWidth, tableLayout, leftLeaves, rightLeaves)
            val widths = allWidths.byKey
            val leftWidth = leftLeaves.sumOf { (widths[it.key] ?: it.column.minWidth).value.toDouble() }.toFloat().dp
            val rightWidth = rightLeaves.sumOf { (widths[it.key] ?: it.column.minWidth).value.toDouble() }.toFloat().dp
            val middleLeaves = leaves.filter { it !in leftLeaves && it !in rightLeaves }
            val middleWidth = middleLeaves.sumOf { (widths[it.key] ?: it.column.minWidth).value.toDouble() }.toFloat().dp
            val horizontal = horizontalScrollState
            fun cellContent(
                leaf: ResolvedColumn<T>,
                row: T,
                rowIndex: Int,
                flat: FlatRow<T>,
            ): @Composable () -> Unit = {
                TableCellContent(
                    leaf = leaf, row = row, rowIndex = rowIndex, flat = flat,
                    leaves = leaves, selection = selection, selected = flat.key in effectiveSelected,
                    indeterminate = false, selectionSelectable = true,
                    expandContent = detailContent, expanded = flat.key in effectiveExpanded,
                    treeExpanded = flat.key in effectiveExpanded, loading = false,
                    treeControl = if (expandColumnKey != null) leaf.key == expandColumnKey else leaf == firstData,
                    tooltipFormatter = tooltipFormatter, showOverflowTooltip = showOverflowTooltip,
                    onToggleSelection = { toggleRowSelection(flat) },
                    onToggleExpand = { toggleExpanded(flat.key) },
                    onToggleTree = { toggleExpanded(flat.key) },
                    indentSize = indentSize,
                )
            }
            val renderRow: @Composable (FlatRow<T>) -> Unit = { flat ->
                val row = flat.row
                Column(Modifier.fillMaxWidth()) {
                    TableRow(
                        flat = flat,
                        leaves = leaves,
                        widths = widths,
                        leftLeaves = leftLeaves,
                        rightLeaves = rightLeaves,
                        leftWidth = leftWidth,
                        rightWidth = rightWidth,
                        middleWidth = middleWidth,
                        rowHeight = if (dynamicHeight) minRowHeight else rowHeightValue,
                        horizontal = horizontal,
                        border = border,
                        stripe = stripe,
                        highlight = highlightCurrentRow && selectedRowKey == flat.key,
                        background = rowBackground?.invoke(row, flat.index),
                        selection = selection,
                        selected = flat.key in effectiveSelected,
                        indeterminate = false,
                        selectionSelectable = true,
                        expandContent = detailContent,
                        expanded = flat.key in effectiveExpanded,
                        treeExpanded = flat.key in effectiveExpanded,
                        loading = false,
                        tooltipFormatter = tooltipFormatter,
                        showOverflowTooltip = showOverflowTooltip,
                        onToggleSelection = { toggleRowSelection(flat) },
                        onToggleExpand = { toggleExpanded(flat.key) },
                        onToggleTree = { toggleExpanded(flat.key) },
                        onClick = onRowClick?.let { callback -> { callback.invoke(row, flat.index) } },
                        fixedHeight = !dynamicHeight,
                        crossHovering = crossHovering,
                        hoveredColumnKey = hoveredColumnKey,
                        onCellHover = { key, hovered -> updateHoveredColumn(key, hovered) },
                        treeControlKey = expandColumnKey,
                        indentSize = indentSize,
                    )
                    if (detailContent != null && flat.key in effectiveExpanded) {
                        Box(Modifier.fillMaxWidth().background(colors.background)) {
                            detailContent.invoke(row, flat.index)
                        }
                    }
                }
            }
            Box(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth()) {
                    if (showHeader) {
                        TableHeader(
                            cells = headerCells,
                            leaves = leaves,
                            widths = widths,
                            rowHeight = rowHeightValue,
                            headerHeight = headerHeight,
                            leftLeaves = leftLeaves,
                            rightLeaves = rightLeaves,
                            leftWidth = leftWidth,
                            rightWidth = rightWidth,
                            middleWidth = middleWidth,
                            horizontal = horizontal,
                            allSelected = allSelected,
                            indeterminate = someSelected && !allSelected,
                            sortOrders = sortOrders,
                            filters = filterValues,
                            border = border,
                            onSelectAll = {
                                emitSelection(if (allSelected) emptySet() else allRowKeys.toSet())
                            },
                            onSort = { leaf -> handleSort(leaf) },
                            onToggleFilter = { leaf, value, checked ->
                                val current = state.filters[leaf.key].orEmpty().toMutableSet()
                                if (checked) current.add(value) else current.remove(value)
                                state.filters[leaf.key] = current.toList()
                            },
                            onClearFilter = { leaf -> state.filters.remove(leaf.key) },
                        )
                    }
                    fixedData.forEach { row ->
                        val flat = FlatRow(row, rowKey(row), -1, 0, null, false)
                        TableRow(
                            flat = flat,
                            leaves = leaves,
                            widths = widths,
                            leftLeaves = leftLeaves,
                            rightLeaves = rightLeaves,
                            leftWidth = leftWidth,
                            rightWidth = rightWidth,
                            middleWidth = middleWidth,
                            rowHeight = rowHeightValue,
                            horizontal = horizontal,
                            border = border,
                            stripe = false,
                            highlight = false,
                            background = fixedRowBackground ?: colors.primary.copy(alpha = 0.10f),
                            selection = selection,
                            selected = flat.key in effectiveSelected,
                            indeterminate = false,
                            selectionSelectable = true,
                            expandContent = detailContent,
                            expanded = flat.key in effectiveExpanded,
                            treeExpanded = flat.key in effectiveExpanded,
                            loading = false,
                            tooltipFormatter = tooltipFormatter,
                            showOverflowTooltip = showOverflowTooltip,
                            onToggleSelection = { toggleRowSelection(flat) },
                            onToggleExpand = { toggleExpanded(flat.key) },
                            onToggleTree = { toggleExpanded(flat.key) },
                            onClick = onRowClick?.let { callback -> { callback.invoke(row, 0) } },
                            crossHovering = crossHovering,
                            hoveredColumnKey = hoveredColumnKey,
                            onCellHover = { key, hovered -> updateHoveredColumn(key, hovered) },
                            treeControlKey = expandColumnKey,
                            indentSize = indentSize,
                        )
                    }
                    if (sorted.isEmpty() && fixedData.isEmpty()) {
                        Box(
                            Modifier.fillMaxWidth().height(bodyHeight),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (loading) {
                                ElIcon(ElIcons.Loading, size = 20.dp, tint = colors.primary, spin = true)
                            } else if (empty != null) {
                                empty.invoke()
                            } else {
                                BasicText(
                                    text = emptyText ?: locale.table.emptyText,
                                    style = theme.typography.small.copy(color = colors.textSecondary),
                                )
                            }
                        }
                    } else if (hasSpan) {
                        Box(
                            Modifier.fillMaxWidth().height(bodyHeight).verticalScroll(rememberScrollState()),
                        ) {
                            Box(Modifier.horizontalScroll(horizontal)) {
                                SpanBody(
                                    rows = flatRows,
                                    leaves = leaves,
                                    widths = widths,
                                    rowHeight = rowHeightValue,
                                    border = border,
                                    stripe = stripe,
                                    rowBackground = rowBackground,
                                    spanMethod = spanMethod,
                                    cell = { leaf, row, rowIndex, flat ->
                                        cellContent(leaf, row, rowIndex, flat).invoke()
                                    },
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxWidth().height(bodyHeight),
                        ) {
                            if (treeMode) {
                                items(flatRows, key = { it.key }) { flat -> renderRow(flat) }
                            } else {
                                itemsIndexed(sorted, key = { _, row -> rowKey(row) }) { index, row ->
                                    renderRow(FlatRow(row, rowKey(row), index, 0, null, false))
                                }
                            }
                        }
                    }
                    if (footer != null && resolvedFooterHeight > 0.dp) {
                        Box(Modifier.fillMaxWidth().height(resolvedFooterHeight)) { footer.invoke() }
                    }
                }
                if (overlay != null) {
                    Box(Modifier.matchParentSize()) { overlay.invoke() }
                }
            }
        }
        if (width != null) {
            Box(Modifier.width(width).height(height)) { content(width) }
        } else {
            BoxWithConstraints(Modifier.fillMaxWidth().height(height)) { content(maxWidth) }
        }
    }
}
