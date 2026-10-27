package cn.enaium.elementplus.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ElSpaceSize(val value: Dp) {
    Small(8.dp),
    Default(12.dp),
    Large(16.dp),
}

/**
 * `<el-space>`: a consistent gap between inline items.
 *
 * [vertical] stacks the items instead of laying them in a row, [wrap] breaks a
 * horizontal space into lines when the available width is exhausted, [fill]
 * makes every item grow so the items share the available space, using
 * [fillRatio] (percentage of the container) as the minimum share of each item.
 * [spacer] is composed between two adjacent items, like the `spacer` slot of
 * the original component.
 *
 * Sizes follow the original component: [spacing] overrides [size] on both
 * axes, and [verticalSpacing] sets the cross-axis gap (`size: [horizontal,
 * vertical]` in Element Plus).
 */
@Composable
fun ElSpace(
    modifier: Modifier = Modifier,
    size: ElSpaceSize = ElSpaceSize.Small,
    spacing: Dp? = null,
    verticalSpacing: Dp? = null,
    vertical: Boolean = false,
    wrap: Boolean = false,
    fill: Boolean = false,
    fillRatio: Float = 100f,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    spacer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val gap = spacing ?: size.value
    val horizontalGap: Dp
    val verticalGap: Dp
    when {
        verticalSpacing != null -> {
            horizontalGap = gap
            verticalGap = verticalSpacing
        }
        vertical -> {
            horizontalGap = 0.dp
            verticalGap = gap
        }
        wrap || fill -> {
            horizontalGap = gap
            verticalGap = gap
        }
        else -> {
            horizontalGap = gap
            verticalGap = 0.dp
        }
    }
    ElSpaceLayout(
        modifier = modifier,
        vertical = vertical,
        wrap = wrap,
        fill = fill,
        fillRatio = fillRatio,
        horizontalGap = horizontalGap,
        verticalGap = verticalGap,
        horizontalAlignment = horizontalAlignment,
        verticalAlignment = verticalAlignment,
        spacer = spacer,
        content = content,
    )
}

@Composable
fun ElSpaceItem(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier) { content() }
}

/**
 * Flexible layout backing [ElSpace]. Splitting the children into items and
 * spacers lets the spacer be composed once per gap, like the original
 * component inserts a spacer vnode between two children.
 */
@Composable
private fun ElSpaceLayout(
    modifier: Modifier,
    vertical: Boolean,
    wrap: Boolean,
    fill: Boolean,
    fillRatio: Float,
    horizontalGap: Dp,
    verticalGap: Dp,
    horizontalAlignment: Alignment.Horizontal,
    verticalAlignment: Alignment.Vertical,
    spacer: (@Composable () -> Unit)?,
    content: @Composable () -> Unit,
) {
    val spacerCount = remember { mutableStateOf(0) }
    SubcomposeLayout(modifier) { constraints ->
        val items = subcompose(SpaceItemsSlot) { content() }
        if (items.isEmpty()) return@SubcomposeLayout layout(0, 0) {}

        val spacers = if (spacer != null && items.size > 1) {
            val needed = items.size - 1
            if (spacerCount.value != needed) spacerCount.value = needed
            subcompose(SpaceSpacerSlot) { repeat(spacerCount.value) { spacer() } }
        } else {
            emptyList()
        }

        val units = ArrayList<ElSpaceUnit>(items.size + spacers.size)
        for (index in items.indices) {
            units += ElSpaceUnit(items[index], isSpacer = false)
            if (index < spacers.size) units += ElSpaceUnit(spacers[index], isSpacer = true)
        }

        val hGap = horizontalGap.roundToPx()
        val vGap = verticalGap.roundToPx()
        val maxWidth = constraints.maxWidth
        val maxHeight = constraints.maxHeight
        val widthBounded = maxWidth != Constraints.Infinity
        val containerWidth = if (widthBounded) maxWidth else 0

        if (vertical) {
            val placeables = units.map { it.measurable.measure(Constraints(maxWidth = maxWidth)) }
            val contentWidth = placeables.maxOf { it.width }
            val width = if (widthBounded) {
                if (fill) maxWidth else maxOf(constraints.minWidth, minOf(contentWidth, maxWidth))
            } else {
                contentWidth
            }
            val contentHeight = placeables.sumOf { it.height } + vGap * (placeables.size - 1).coerceAtLeast(0)
            val heightLimit = if (maxHeight == Constraints.Infinity) {
                maxOf(contentHeight, constraints.minHeight)
            } else {
                maxHeight
            }
            val height = contentHeight.coerceIn(constraints.minHeight, heightLimit)
            return@SubcomposeLayout layout(width, height) {
                var y = 0
                placeables.forEach { placeable ->
                    placeable.placeRelative(horizontalAlignment.offsetX(width, placeable.width), y)
                    y += placeable.height + vGap
                }
            }
        }

        // Horizontal layout: split the units into lines first (the original
        // wraps when `fill` or `wrap` is set), then measure and place them.
        val groups = ArrayList<MutableList<Int>>()
        val placeables = arrayOfNulls<Placeable>(units.size)

        if (fill && widthBounded) {
            val ratio = (fillRatio / 100f).coerceIn(0f, 1f)
            val perLine = if (ratio <= 0f) Int.MAX_VALUE else maxOf(1, (1f / ratio).toInt())
            var line = ArrayList<Int>()
            var itemsInLine = 0
            units.forEachIndexed { index, unit ->
                if (!unit.isSpacer && itemsInLine >= perLine && line.isNotEmpty()) {
                    groups += line
                    line = ArrayList()
                    itemsInLine = 0
                }
                line += index
                if (!unit.isSpacer) itemsInLine++
            }
            if (line.isNotEmpty()) groups += line
            groups.forEach { group ->
                var spacerWidth = 0
                group.forEach { index ->
                    if (units[index].isSpacer) {
                        val placeable = units[index].measurable.measure(Constraints(maxWidth = containerWidth))
                        placeables[index] = placeable
                        spacerWidth += placeable.width
                    }
                }
                val itemCount = group.count { !units[it].isSpacer }
                val gapTotal = hGap * (group.size - 1).coerceAtLeast(0)
                val itemWidth = if (itemCount > 0) {
                    ((containerWidth - spacerWidth - gapTotal) / itemCount).coerceAtLeast(0)
                } else {
                    0
                }
                group.forEach { index ->
                    if (!units[index].isSpacer) {
                        placeables[index] = units[index].measurable.measure(Constraints.fixedWidth(itemWidth))
                    }
                }
            }
        } else {
            units.forEachIndexed { index, unit ->
                placeables[index] = unit.measurable.measure(Constraints(maxWidth = maxWidth))
            }
            if (wrap && widthBounded) {
                var line = ArrayList<Int>()
                var lineWidth = 0
                for (index in units.indices) {
                    val placeableWidth = placeables[index]!!.width
                    if (line.isNotEmpty() && lineWidth + hGap + placeableWidth > containerWidth) {
                        groups += line
                        line = ArrayList()
                        lineWidth = placeableWidth
                    } else {
                        lineWidth += if (line.isEmpty()) placeableWidth else hGap + placeableWidth
                    }
                    line += index
                }
                if (line.isNotEmpty()) groups += line
            } else {
                groups += ArrayList(units.indices.toList())
            }
        }

        val lineWidths = groups.map { group ->
            group.sumOf { placeables[it]!!.width } + hGap * (group.size - 1).coerceAtLeast(0)
        }
        val contentWidth = lineWidths.maxOrNull() ?: 0
        val width = when {
            !widthBounded -> contentWidth
            fill || wrap -> maxOf(constraints.minWidth, containerWidth)
            else -> maxOf(constraints.minWidth, minOf(contentWidth, containerWidth))
        }
        val lineHeights = groups.map { group -> group.maxOf { placeables[it]!!.height } }
        val contentHeight = lineHeights.sum() + vGap * (groups.size - 1).coerceAtLeast(0)
        val heightLimit = if (maxHeight == Constraints.Infinity) {
            maxOf(contentHeight, constraints.minHeight)
        } else {
            maxHeight
        }
        val height = contentHeight.coerceIn(constraints.minHeight, heightLimit)

        layout(width, height) {
            var y = 0
            groups.forEachIndexed { lineIndex, group ->
                val lineHeight = lineHeights[lineIndex]
                var x = horizontalAlignment.offsetX(width, lineWidths[lineIndex])
                group.forEach { index ->
                    val placeable = placeables[index]!!
                    placeable.placeRelative(
                        x,
                        y + verticalAlignment.offsetY(lineHeight, placeable.height),
                    )
                    x += placeable.width + hGap
                }
                y += lineHeight + vGap
            }
        }
    }
}

private data class ElSpaceUnit(val measurable: Measurable, val isSpacer: Boolean)

private val SpaceItemsSlot = "el-space-items"
private val SpaceSpacerSlot = "el-space-spacers"

private fun Alignment.Horizontal.offsetX(container: Int, child: Int): Int {
    val free = (container - child).coerceAtLeast(0)
    return when (this) {
        Alignment.Start -> 0
        Alignment.CenterHorizontally -> free / 2
        Alignment.End -> free
        else -> 0
    }
}

private fun Alignment.Vertical.offsetY(container: Int, child: Int): Int {
    val free = (container - child).coerceAtLeast(0)
    return when (this) {
        Alignment.Top -> 0
        Alignment.CenterVertically -> free / 2
        Alignment.Bottom -> free
        else -> 0
    }
}
