package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElTextAlign
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

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
) {
    fun text(row: T, index: Int): String =
        formatter?.invoke(row, index) ?: property?.invoke(row)?.toString() ?: row?.toString() ?: ""
}

private data class TableSort(val columnIndex: Int, val ascending: Boolean)

/**
 * `<el-table>` with selection, sorting, stripe/border, custom cells and an
 * optional bounded body with a sticky header.
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
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedSize = resolveElSize(size)
    val rowHeight = when (resolvedSize) {
        ElSize.Large -> 48.dp
        ElSize.Default -> 40.dp
        ElSize.Small -> 32.dp
    }
    var sort by remember { mutableStateOf<TableSort?>(null) }
    val sortedData = remember(data, sort) {
        val active = sort ?: return@remember data
        val column = columns.getOrNull(active.columnIndex) ?: return@remember data
        if (!column.sortable) return@remember data
        val comparator: Comparator<T> = if (column.sortComparator != null) {
            Comparator { a, b -> column.sortComparator.invoke(a, b) }
        } else {
            Comparator { a, b ->
                val left = column.property?.invoke(a)
                val right = column.property?.invoke(b)
                when {
                    left is Number && right is Number -> left.toDouble().compareTo(right.toDouble())
                    else -> {
                        val leftText = left?.toString() ?: column.formatter?.invoke(a, 0) ?: a?.toString() ?: ""
                        val rightText = right?.toString() ?: column.formatter?.invoke(b, 0) ?: b?.toString() ?: ""
                        leftText.compareTo(rightText)
                    }
                }
            }
        }
        val sorted = data.sortedWith(comparator)
        if (active.ascending) sorted else sorted.reversed()
    }
    val selectableKeys = remember(sortedData) { sortedData.map { rowKey(it) } }
    val allSelected = selectableKeys.isNotEmpty() && selectableKeys.all { it in selectedKeys }

    Column(
        modifier = modifier
            .then(if (border) Modifier.border(1.dp, colors.borderLight, RoundedCornerShape(theme.radius.base)) else Modifier)
            .clip(RoundedCornerShape(theme.radius.base))
            .background(colors.background),
    ) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val maxWidth = maxWidth
            val widths = resolveColumnWidths(columns, maxWidth, if (selection) 40.dp else 0.dp)
            val totalWidth = widths.fold(0.dp) { acc, d -> acc + d } +
                if (selection) 40.dp else 0.dp
            val tableModifier = if (totalWidth > maxWidth) Modifier.horizontalScroll(rememberScrollState()) else Modifier
            Column(tableModifier) {
                if (showHeader) {
                    TableHeader(
                        columns = columns,
                        widths = widths,
                        selection = selection,
                        allSelected = allSelected,
                        sort = sort,
                        rowHeight = rowHeight,
                        border = border,
                        onSelectAll = {
                            onSelectionChange?.invoke(if (allSelected) emptySet() else selectableKeys.toSet())
                        },
                        onSort = { index ->
                            val column = columns[index]
                            if (column.sortable) {
                                val next = when {
                                    sort?.columnIndex != index -> TableSort(index, true)
                                    sort?.ascending == true -> TableSort(index, false)
                                    else -> null
                                }
                                sort = next
                                onSortChange?.invoke(column, next?.ascending)
                            }
                        },
                    )
                }
                val body: @Composable () -> Unit = {
                    if (sortedData.isEmpty() && !loading) {
                        Box(
                            Modifier.fillMaxWidth().height(80.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            BasicText(
                                text = emptyText ?: locale.table.emptyText,
                                style = theme.typography.small.copy(color = colors.textSecondary),
                            )
                        }
                    }
                    sortedData.forEachIndexed { index, row ->
                        TableRow(
                            row = row,
                            index = index,
                            columns = columns,
                            widths = widths,
                            selection = selection,
                            rowKey = rowKey(row),
                            selectedKeys = selectedKeys,
                            stripe = stripe && index % 2 == 1,
                            border = border,
                            rowHeight = rowHeight,
                            highlight = highlightCurrentRow && selectedRowKey == rowKey(row),
                            onSelectionChange = { checked ->
                                val key = rowKey(row)
                                onSelectionChange?.invoke(if (checked) selectedKeys + key else selectedKeys - key)
                            },
                            onClick = { onRowClick?.invoke(row, index) },
                        )
                    }
                    if (loading) {
                        Box(
                            Modifier.fillMaxWidth().height(60.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            ElIcon(ElIcons.Loading, size = 20.dp, tint = colors.primary, spin = true)
                        }
                    }
                }
                when {
                    height != null -> Column(Modifier.height(height).verticalScroll(rememberScrollState())) { body() }
                    maxHeight != null -> Column(Modifier.heightIn(max = maxHeight).verticalScroll(rememberScrollState())) { body() }
                    else -> Column { body() }
                }
            }
        }
    }
}

@Composable
private fun <T> TableHeader(
    columns: List<ElTableColumn<T>>,
    widths: List<Dp>,
    selection: Boolean,
    allSelected: Boolean,
    sort: TableSort?,
    rowHeight: Dp,
    border: Boolean,
    onSelectAll: () -> Unit,
    onSort: (Int) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    Row(
        Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .background(colors.fillLight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selection) {
            Box(Modifier.width(40.dp), contentAlignment = Alignment.Center) {
                TableCheckbox(
                    checked = allSelected,
                    indeterminate = false,
                    contentDescription = locale.table.selectAllLabel,
                    onClick = onSelectAll,
                )
            }
        }
        columns.forEachIndexed { index, column ->
            HeaderCell(
                column = column,
                width = widths.getOrNull(index) ?: 80.dp,
                border = border,
                sortDirection = if (sort?.columnIndex == index) sort.ascending else null,
                onClick = { onSort(index) },
            )
        }
    }
}

@Composable
private fun <T> HeaderCell(
    column: ElTableColumn<T>,
    width: Dp,
    border: Boolean,
    sortDirection: Boolean?,
    onClick: () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val modifier = Modifier
        .width(width)
        .height(40.dp)
        .then(if (border) Modifier.border(1.dp, colors.borderLight) else Modifier)
        .then(if (column.sortable) Modifier.clickable(onClick = onClick) else Modifier)
        .padding(horizontal = 12.dp)
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        BasicText(
            text = column.label,
            modifier = Modifier.weight(1f),
            style = theme.typography.small.copy(
                color = colors.textSecondary,
                fontWeight = FontWeight.Medium,
                textAlign = column.align.toTextAlign(),
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (column.sortable) {
            Spacer(Modifier.width(4.dp))
            SortingArrows(sortDirection)
        }
    }
}

@Composable
private fun SortingArrows(direction: Boolean?) {
    val colors = LocalElTheme.current.colors
    Column {
        ElIcon(
            ElIcons.CaretTop,
            size = 8.dp,
            tint = if (direction == true) colors.primary else colors.textPlaceholder,
        )
        ElIcon(
            ElIcons.CaretBottom,
            size = 8.dp,
            tint = if (direction == false) colors.primary else colors.textPlaceholder,
        )
    }
}

@Composable
private fun <T> TableRow(
    row: T,
    index: Int,
    columns: List<ElTableColumn<T>>,
    widths: List<Dp>,
    selection: Boolean,
    rowKey: Any,
    selectedKeys: Set<Any>,
    stripe: Boolean,
    border: Boolean,
    rowHeight: Dp,
    highlight: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    onClick: () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val interaction = remember(rowKey) { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Row(
        Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .background(
                when {
                    highlight -> colors.light(cn.enaium.elementplus.theme.ElType.Primary, 9)
                    hovered -> colors.fillLight
                    stripe -> colors.fillLighter
                    else -> colors.background
                }
            )
            .hoverable(interaction)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selection) {
            Box(Modifier.width(40.dp), contentAlignment = Alignment.Center) {
                TableCheckbox(
                    checked = rowKey in selectedKeys,
                    indeterminate = false,
                    contentDescription = locale.table.selectRowLabel,
                    onClick = { onSelectionChange(rowKey !in selectedKeys) },
                )
            }
        }
        columns.forEachIndexed { columnIndex, column ->
            val width = widths.getOrNull(columnIndex) ?: 80.dp
            Box(
                Modifier
                    .width(width)
                    .height(rowHeight)
                    .then(if (border) Modifier.border(1.dp, colors.borderLight) else Modifier)
                    .padding(horizontal = 12.dp),
                contentAlignment = column.align.toAlignment(),
            ) {
                if (column.cell != null) {
                    column.cell.invoke(row, index)
                } else {
                    BasicText(
                        text = column.text(row, index),
                        style = theme.typography.small.copy(color = colors.textRegular),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun TableCheckbox(
    checked: Boolean,
    indeterminate: Boolean,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val colors = LocalElTheme.current.colors
    Box(
        Modifier
            .size(14.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(if (checked) colors.primary else colors.fillBlank)
            .border(1.dp, if (checked) colors.primary else colors.border, RoundedCornerShape(2.dp))
            .semantics { this.contentDescription = contentDescription }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) ElIcon(ElIcons.Check, size = 10.dp, tint = colors.white)
        else if (indeterminate) Box(Modifier.size(width = 8.dp, height = 2.dp).background(colors.white))
    }
}

/** Fixed-height virtualized table sharing [ElTableColumn]. */
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
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedSize = resolveElSize(size)
    val rowHeight = when (resolvedSize) {
        ElSize.Large -> 48.dp
        ElSize.Default -> 40.dp
        ElSize.Small -> 32.dp
    }
    BoxWithConstraints(
        modifier
            .height(height)
            .then(if (border) Modifier.border(1.dp, colors.borderLight, RoundedCornerShape(4.dp)) else Modifier)
            .clip(RoundedCornerShape(4.dp))
            .background(colors.background),
    ) {
        val widths = resolveColumnWidths(columns, maxWidth, 0.dp)
        Column {
            if (showHeader) {
                Row(Modifier.fillMaxWidth().height(rowHeight).background(colors.fillLight), verticalAlignment = Alignment.CenterVertically) {
                    columns.forEachIndexed { index, column ->
                        Box(
                            Modifier
                                .width(widths.getOrNull(index) ?: 80.dp)
                                .padding(horizontal = 12.dp),
                        ) {
                            BasicText(
                                text = column.label,
                                style = theme.typography.small.copy(color = colors.textSecondary, fontWeight = FontWeight.Medium),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
            if (data.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                    BasicText(text = emptyText ?: locale.table.emptyText, style = theme.typography.small.copy(color = colors.textSecondary))
                }
            } else {
                LazyColumn(Modifier.fillMaxWidth()) {
                    itemsIndexed(data, key = { _, row -> rowKey(row) }) { index, row ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(rowHeight)
                                .background(if (stripe && index % 2 == 1) colors.fillLighter else colors.background)
                                .clickable(enabled = onRowClick != null) { onRowClick?.invoke(row, index) },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            columns.forEachIndexed { columnIndex, column ->
                                Box(
                                    Modifier
                                        .then(widths.getOrNull(columnIndex)?.let { Modifier.width(it) } ?: Modifier.weight(1f))
                                        .height(rowHeight)
                                        .padding(horizontal = 12.dp),
                                    contentAlignment = column.align.toAlignment(),
                                ) {
                                    if (column.cell != null) column.cell.invoke(row, index)
                                    else BasicText(
                                        text = column.text(row, index),
                                        style = theme.typography.small.copy(color = colors.textRegular),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun <T> resolveColumnWidths(
    columns: List<ElTableColumn<T>>,
    maxWidth: Dp,
    extra: Dp,
): List<Dp> {
    if (columns.isEmpty()) return emptyList()
    val available = (maxWidth - extra).coerceAtLeast(1.dp)
    val unspecified = columns.count { it.width == null }
    val specified = columns.fold(0.0) { acc, column -> acc + (column.width?.value?.toDouble() ?: 0.0) }
    if (unspecified > 0) {
        val remaining = (available.value - specified).coerceAtLeast(0.0)
        val defaultWidth = (remaining / unspecified).coerceAtLeast(80.0).dp
        return columns.map { it.width ?: defaultWidth }
    }
    val total = specified.dp
    if (total >= available || total.value <= 0f) return columns.map { it.width ?: 80.dp }
    val factor = available.value / total.value
    return columns.map { (it.width ?: 80.dp) * factor }
}

private fun ElTextAlign.toTextAlign(): TextAlign = when (this) {
    ElTextAlign.Start -> TextAlign.Start
    ElTextAlign.Center -> TextAlign.Center
    ElTextAlign.End -> TextAlign.End
}

private fun ElTextAlign.toAlignment(): Alignment = when (this) {
    ElTextAlign.Start -> Alignment.CenterStart
    ElTextAlign.Center -> Alignment.Center
    ElTextAlign.End -> Alignment.CenterEnd
}
