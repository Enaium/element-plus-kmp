package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElTextAlign
import cn.enaium.elementplus.theme.LocalElTheme
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.roundToInt

enum class ElDescriptionsDirection { Horizontal, Vertical }

class ElDescriptionItem(
    val label: String,
    val span: Int = 1,
    val rowspan: Int = 1,
    val width: Dp? = null,
    val minWidth: Dp? = null,
    val labelWidth: Dp? = null,
    val align: ElTextAlign? = null,
    val labelAlign: ElTextAlign? = null,
    val labelModifier: Modifier = Modifier,
    val contentModifier: Modifier = Modifier,
    val content: @Composable () -> Unit,
)

/** `<el-descriptions>` with border, column/row spans and horizontal/vertical labels. */
@Composable
fun ElDescriptions(
    items: List<ElDescriptionItem>,
    modifier: Modifier = Modifier,
    column: Int = 3,
    border: Boolean = false,
    direction: ElDescriptionsDirection = ElDescriptionsDirection.Horizontal,
    title: String? = null,
    extra: (@Composable () -> Unit)? = null,
    labelWidth: Dp = 120.dp,
    size: ElSize? = null,
    titleContent: (@Composable () -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val resolvedSize = resolveElSize(size)
    val columnCount = column.coerceAtLeast(1)
    val placements = remember(items, columnCount) { descriptionPlacements(items, columnCount) }
    Column(modifier.fillMaxWidth()) {
        if (title != null || titleContent != null || extra != null) {
            Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                if (titleContent != null) {
                    Box(Modifier.weight(1f)) { titleContent() }
                } else if (title != null) {
                    BasicText(
                        text = title,
                        modifier = Modifier.weight(1f),
                        style = theme.typography.medium.copy(
                            color = colors.textPrimary,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                        ),
                    )
                } else {
                    androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                }
                extra?.invoke()
            }
        }
        if (placements.isNotEmpty()) {
            Layout(
                content = {
                    placements.forEach { placement ->
                        DescriptionCell(
                            item = placement.item,
                            direction = direction,
                            border = border,
                            labelWidth = labelWidth,
                            size = resolvedSize,
                        )
                    }
                },
            ) { measurables, constraints ->
                val widths = computeColumnWidths(placements, columnCount, measurables, constraints, this)
                val cellWidths = placements.map { placement ->
                    var total = 0f
                    for (index in placement.column until placement.column + placement.columnSpan) {
                        total += widths[index]
                    }
                    total.roundToInt().coerceAtLeast(0)
                }
                val rowCount = placements.maxOf { it.row + it.rowSpan }
                val columnOffsets = FloatArray(columnCount + 1)
                for (index in 0 until columnCount) {
                    columnOffsets[index + 1] = columnOffsets[index] + widths[index]
                }
                val intrinsicHeights = try {
                    FloatArray(placements.size) { index ->
                        measurables[index].maxIntrinsicHeight(cellWidths[index]).toFloat()
                    }
                } catch (ignored: IllegalStateException) {
                    null
                }
                val rowOffsets: FloatArray
                val placeables: List<androidx.compose.ui.layout.Placeable>
                if (intrinsicHeights != null) {
                    rowOffsets = descriptionOffsets(descriptionRowHeights(placements, rowCount, intrinsicHeights))
                    val cellHeights = placements.map { placement ->
                        val height = rowOffsets[placement.row + placement.rowSpan] - rowOffsets[placement.row]
                        height.roundToInt().coerceAtLeast(0)
                    }
                    placeables = measurables.mapIndexed { index, measurable ->
                        measurable.measure(Constraints.fixed(cellWidths[index], cellHeights[index]))
                    }
                } else {
                    val measured = measurables.mapIndexed { index, measurable ->
                        measurable.measure(Constraints(minWidth = cellWidths[index], maxWidth = cellWidths[index]))
                    }
                    val heights = FloatArray(measured.size) { measured[it].height.toFloat() }
                    rowOffsets = descriptionOffsets(descriptionRowHeights(placements, rowCount, heights))
                    placeables = measured
                }
                val totalWidth = if (constraints.hasBoundedWidth) {
                    constraints.maxWidth
                } else {
                    columnOffsets.last().roundToInt().coerceAtLeast(0)
                        .coerceIn(constraints.minWidth, constraints.maxWidth)
                }
                val totalHeight = rowOffsets.last().roundToInt()
                    .coerceIn(constraints.minHeight, constraints.maxHeight)
                layout(totalWidth, totalHeight) {
                    placeables.forEachIndexed { index, placeable ->
                        val placement = placements[index]
                        placeable.place(
                            columnOffsets[placement.column].roundToInt(),
                            rowOffsets[placement.row].roundToInt(),
                        )
                    }
                }
            }
        }
    }
}

private fun descriptionRowHeights(
    placements: List<DescriptionPlacement>,
    rowCount: Int,
    cellHeights: FloatArray,
): FloatArray {
    val rowHeights = FloatArray(rowCount)
    placements.forEachIndexed { index, placement ->
        if (placement.rowSpan == 1) {
            rowHeights[placement.row] = maxOf(rowHeights[placement.row], cellHeights[index])
        }
    }
    placements.indices.sortedBy { placements[it].rowSpan }.forEach { index ->
        val placement = placements[index]
        if (placement.rowSpan > 1) {
            var available = 0f
            for (row in placement.row until placement.row + placement.rowSpan) {
                available += rowHeights[row]
            }
            val deficit = cellHeights[index] - available
            if (deficit > 0f) {
                val addition = deficit / placement.rowSpan
                for (row in placement.row until placement.row + placement.rowSpan) {
                    rowHeights[row] += addition
                }
            }
        }
    }
    return rowHeights
}

private fun descriptionOffsets(sizes: FloatArray): FloatArray {
    val offsets = FloatArray(sizes.size + 1)
    for (index in sizes.indices) {
        offsets[index + 1] = offsets[index] + sizes[index]
    }
    return offsets
}

private data class DescriptionPlacement(
    val item: ElDescriptionItem,
    val row: Int,
    val column: Int,
    val rowSpan: Int,
    val columnSpan: Int,
)

private fun descriptionPlacements(items: List<ElDescriptionItem>, column: Int): List<DescriptionPlacement> {
    val occupied = mutableListOf<MutableList<Boolean>>()
    fun ensure(row: Int) {
        while (occupied.size <= row) {
            occupied += MutableList(column) { false }
        }
    }

    fun isFree(row: Int, start: Int, rowSpan: Int, columnSpan: Int): Boolean {
        for (currentRow in row until row + rowSpan) {
            val cells = occupied.getOrNull(currentRow) ?: continue
            for (currentColumn in start until start + columnSpan) {
                if (cells[currentColumn]) return false
            }
        }
        return true
    }

    fun occupy(row: Int, start: Int, rowSpan: Int, columnSpan: Int) {
        for (currentRow in row until row + rowSpan) {
            ensure(currentRow)
            for (currentColumn in start until start + columnSpan) {
                occupied[currentRow][currentColumn] = true
            }
        }
    }

    val result = mutableListOf<DescriptionPlacement>()
    var row = 0
    var currentColumn = 0
    items.forEach { item ->
        val columnSpan = item.span.coerceIn(1, column)
        val rowSpan = item.rowspan.coerceAtLeast(1)
        while (true) {
            if (currentColumn + columnSpan > column) {
                currentColumn = 0
                row += 1
                continue
            }
            if (isFree(row, currentColumn, rowSpan, columnSpan)) break
            currentColumn += 1
        }
        result += DescriptionPlacement(item, row, currentColumn, rowSpan, columnSpan)
        occupy(row, currentColumn, rowSpan, columnSpan)
        currentColumn += columnSpan
        if (currentColumn >= column) {
            currentColumn = 0
            row += 1
        }
    }
    return result
}

private fun computeColumnWidths(
    placements: List<DescriptionPlacement>,
    column: Int,
    measurables: List<androidx.compose.ui.layout.Measurable>,
    constraints: Constraints,
    density: androidx.compose.ui.unit.Density,
): FloatArray {
    val widths = FloatArray(column)
    val known = BooleanArray(column)
    val minimum = FloatArray(column)
    placements.forEach { placement ->
        val minWidth = placement.item.minWidth?.let { with(density) { it.toPx() } } ?: 0f
        if (minWidth > 0f) {
            val share = minWidth / placement.columnSpan
            for (index in placement.column until placement.column + placement.columnSpan) {
                minimum[index] = maxOf(minimum[index], share)
            }
        }
    }
    if (constraints.hasBoundedWidth) {
        placements.forEach { placement ->
            val itemWidth = placement.item.width?.let { with(density) { it.toPx() } }
            if (itemWidth != null) {
                val share = itemWidth / placement.columnSpan
                for (index in placement.column until placement.column + placement.columnSpan) {
                    widths[index] = if (known[index]) maxOf(widths[index], share) else share
                    known[index] = true
                }
            }
        }
        val unknown = (0 until column).filter { !known[it] }
        if (unknown.isNotEmpty()) {
            val remaining = (constraints.maxWidth - widths.sum()).coerceAtLeast(0f)
            val share = remaining / unknown.size
            unknown.forEach { index -> widths[index] = maxOf(share, minimum[index]) }
        }
        for (index in 0 until column) {
            widths[index] = maxOf(widths[index], minimum[index])
        }
        val total = widths.sum()
        if (total > 0f && total != constraints.maxWidth.toFloat()) {
            val scale = constraints.maxWidth.toFloat() / total
            for (index in 0 until column) widths[index] *= scale
        }
    } else {
        val natural = measurables.map { it.measure(Constraints()) }
        placements.forEachIndexed { index, placement ->
            if (placement.columnSpan == 1) {
                widths[placement.column] = maxOf(widths[placement.column], natural[index].width.toFloat())
            }
        }
        placements.forEachIndexed { index, placement ->
            if (placement.columnSpan > 1) {
                var available = 0f
                for (columnIndex in placement.column until placement.column + placement.columnSpan) {
                    available += widths[columnIndex]
                }
                val needed = natural[index].width.toFloat()
                if (needed > available) {
                    val addition = (needed - available) / placement.columnSpan
                    for (columnIndex in placement.column until placement.column + placement.columnSpan) {
                        widths[columnIndex] += addition
                    }
                }
            }
        }
        for (index in 0 until column) {
            widths[index] = maxOf(widths[index], minimum[index])
        }
    }
    return widths
}

private fun ElTextAlign.asTextAlign(): TextAlign = when (this) {
    ElTextAlign.Start -> TextAlign.Start
    ElTextAlign.Center -> TextAlign.Center
    ElTextAlign.End -> TextAlign.End
}

private fun ElTextAlign.asBoxAlignment(): Alignment = when (this) {
    ElTextAlign.Start -> Alignment.CenterStart
    ElTextAlign.Center -> Alignment.Center
    ElTextAlign.End -> Alignment.CenterEnd
}

@Composable
private fun DescriptionCell(
    item: ElDescriptionItem,
    direction: ElDescriptionsDirection,
    border: Boolean,
    labelWidth: Dp,
    size: ElSize,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val borderModifier = if (border) Modifier.border(1.dp, colors.borderLight) else Modifier
    val resolvedLabelWidth = item.labelWidth ?: labelWidth
    val align = item.align ?: ElTextAlign.Start
    val labelAlign = item.labelAlign ?: align
    val labelStyle = theme.typography.small.copy(
        color = colors.textSecondary,
        fontSize = size.fontSize,
        textAlign = labelAlign.asTextAlign(),
    )
    if (direction == ElDescriptionsDirection.Horizontal) {
        Row(Modifier.fillMaxHeight().then(borderModifier)) {
            Box(
                Modifier
                    .width(resolvedLabelWidth)
                    .fillMaxHeight()
                    .background(if (border) colors.fillLight else colors.background)
                    .then(item.labelModifier)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = labelAlign.asBoxAlignment(),
            ) {
                BasicText(text = item.label, style = labelStyle)
            }
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(item.contentModifier)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = align.asBoxAlignment(),
            ) {
                item.content()
            }
        }
    } else {
        Column(Modifier.fillMaxHeight().then(borderModifier)) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(if (border) colors.fillLight else colors.background)
                    .then(item.labelModifier)
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                contentAlignment = labelAlign.asBoxAlignment(),
            ) {
                BasicText(text = item.label, style = labelStyle)
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .then(item.contentModifier)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = align.asBoxAlignment(),
            ) {
                item.content()
            }
        }
    }
}
