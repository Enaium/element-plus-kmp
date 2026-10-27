package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.internal.rememberElInteractionState
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElSplitterDirection { Horizontal, Vertical }

/** Side of a split bar whose adjacent panel is collapsed. */
enum class ElSplitterCollapseType { Start, End }

/**
 * Per-panel configuration of the [ElSplitter] overload that takes a list of
 * panels, mirroring `<el-splitter-panel>`.
 *
 * [size] is the initial size in pixels, [min] / [max] bound the size while
 * dragging (defaulting to the splitter-wide `minSize` and no maximum),
 * [collapsible] allows the panel to be collapsed by double-tapping a bar and
 * [resizable] allows the adjacent bars to be dragged.
 */
@Immutable
data class ElSplitterPanel(
    val size: Dp? = null,
    val min: Dp? = null,
    val max: Dp? = null,
    val collapsible: Boolean = false,
    val resizable: Boolean = true,
)

/**
 * `<el-splitter>`: resizable panes with draggable dividers.
 *
 * Panel sizes are kept in pixels, so fixed sizes, `min` / `max` limits and the
 * resize events all report pixel values like the original component. With
 * [lazy] set, the panel sizes are rescaled when the splitter itself is
 * resized; without it the pixel sizes stay untouched. Double-tapping a bar
 * collapses the adjacent [ElSplitterPanel.collapsible] panel and double-tapping
 * it again restores the previous size.
 */
@Composable
fun ElSplitter(
    panels: List<ElSplitterPanel>,
    modifier: Modifier = Modifier,
    direction: ElSplitterDirection = ElSplitterDirection.Horizontal,
    lazy: Boolean = false,
    dividerSize: Dp = 1.dp,
    disabled: Boolean = false,
    minSize: Dp = 0.dp,
    onResizeStart: ((index: Int, sizes: List<Float>) -> Unit)? = null,
    onResize: ((index: Int, sizes: List<Float>) -> Unit)? = null,
    onResizeEnd: ((index: Int, sizes: List<Float>) -> Unit)? = null,
    onCollapse: ((index: Int, type: ElSplitterCollapseType, sizes: List<Float>) -> Unit)? = null,
    splitBarContent: (@Composable (index: Int) -> Unit)? = null,
    panelContent: @Composable (index: Int) -> Unit,
) {
    require(panels.isNotEmpty()) { "panels must not be empty" }
    val horizontal = direction == ElSplitterDirection.Horizontal
    val theme = LocalElTheme.current
    val colors = theme.colors
    val density = LocalDensity.current
    val sizes = remember(panels.size) { mutableStateListOf<Float>() }
    val restoreSizes = remember(panels.size) { mutableStateListOf<Float>() }
    var lastAvailablePx by remember(panels.size) { mutableStateOf(0f) }
    var hoveredIndex by remember { mutableStateOf(-1) }
    var draggingIndex by remember { mutableStateOf(-1) }

    BoxWithConstraints(modifier.fillMaxSize()) {
        val hitSize = 8.dp
        val hitPx = with(density) { hitSize.toPx() }
        val totalPx = (if (horizontal) constraints.maxWidth else constraints.maxHeight).toFloat()
        val availablePx = (totalPx - hitPx * (panels.size - 1)).coerceAtLeast(0f)
        val minPx = panels.map { with(density) { (it.min ?: minSize).toPx() } }
        val maxPx = panels.map { panel -> panel.max?.let { with(density) { it.toPx() } } ?: Float.MAX_VALUE }

        if (sizes.isEmpty() && availablePx > 0f) {
            val fixed = panels.map { panel -> panel.size?.let { with(density) { it.toPx() } } }
            val fixedSum = fixed.filterNotNull().sum()
            val flexible = fixed.count { it == null }
            val share = if (flexible > 0) (availablePx - fixedSum).coerceAtLeast(0f) / flexible else 0f
            sizes.addAll(fixed.map { it ?: share })
            restoreSizes.addAll(List(panels.size) { 0f })
            lastAvailablePx = availablePx
        }

        LaunchedEffect(availablePx, lazy) {
            if (availablePx <= 0f) return@LaunchedEffect
            if (lazy && lastAvailablePx > 0f && availablePx != lastAvailablePx && sizes.size == panels.size) {
                val factor = availablePx / lastAvailablePx
                sizes.indices.forEach { index ->
                    val limit = maxPx[index].coerceAtLeast(minPx[index])
                    sizes[index] = (sizes[index] * factor).coerceIn(minPx[index], limit)
                }
            }
            lastAvailablePx = availablePx
        }

        fun reportSizes(): List<Float> = sizes.toList()

        fun resize(index: Int, deltaPx: Float) {
            val next = index + 1
            if (next >= sizes.size) return
            val left = sizes[index]
            val right = sizes[next]
            val lower = maxOf(minPx[index] - left, right - maxPx[next])
            val upper = minOf(maxPx[index] - left, right - minPx[next])
            if (lower > upper) return
            val delta = deltaPx.coerceIn(lower, upper)
            if (delta == 0f) return
            sizes[index] = left + delta
            sizes[next] = right - delta
        }

        fun collapse(index: Int, type: ElSplitterCollapseType) {
            val panelIndex = if (type == ElSplitterCollapseType.Start) index else index + 1
            val otherIndex = if (type == ElSplitterCollapseType.Start) index + 1 else index
            if (panelIndex !in panels.indices || otherIndex !in panels.indices) return
            if (!panels[panelIndex].collapsible) return
            if (sizes[panelIndex] <= 0f) {
                val want = restoreSizes.getOrElse(panelIndex) { 0f }
                    .takeIf { it > 0f }
                    ?: minPx[panelIndex].takeIf { it > 0f }
                    ?: 0f
                val give = minOf(want, maxPx[panelIndex], (sizes[otherIndex] - minPx[otherIndex]).coerceAtLeast(0f))
                if (give <= 0f) return
                sizes[panelIndex] = give
                sizes[otherIndex] -= give
            } else {
                restoreSizes[panelIndex] = sizes[panelIndex]
                sizes[otherIndex] += sizes[panelIndex]
                sizes[panelIndex] = 0f
            }
            onCollapse?.invoke(index, type, reportSizes())
        }

        Box(Modifier.fillMaxSize()) {
            var offsetPx = 0f
            panels.forEachIndexed { index, panel ->
                val limit = maxPx[index].coerceAtLeast(minPx[index])
                val sizePx = sizes.getOrElse(index) { 0f }.coerceIn(minPx[index], limit)
                Box(
                    Modifier.then(
                        if (horizontal) {
                            Modifier
                                .offset(x = with(density) { offsetPx.toDp() })
                                .width(with(density) { sizePx.toDp() })
                                .fillMaxHeight()
                        } else {
                            Modifier
                                .offset(y = with(density) { offsetPx.toDp() })
                                .height(with(density) { sizePx.toDp() })
                                .fillMaxWidth()
                        },
                    ),
                ) {
                    panelContent(index)
                }
                offsetPx += sizePx
                if (index < panels.size - 1) {
                    val barOffset = offsetPx
                    offsetPx += hitPx
                    val barEnabled = !disabled && panel.resizable && panels[index + 1].resizable
                    val startCollapsible = panel.collapsible
                    val endCollapsible = panels[index + 1].collapsible
                    SplitterBar(
                        modifier = if (horizontal) {
                            Modifier
                                .offset(x = with(density) { barOffset.toDp() })
                                .width(hitSize)
                                .fillMaxHeight()
                        } else {
                            Modifier
                                .offset(y = with(density) { barOffset.toDp() })
                                .height(hitSize)
                                .fillMaxWidth()
                        },
                        direction = direction,
                        dividerSize = dividerSize,
                        enabled = barEnabled,
                        active = hoveredIndex == index || draggingIndex == index,
                        color = colors.border,
                        activeColor = colors.primary,
                        onHover = { hovered -> hoveredIndex = if (hovered) index else -1 },
                        onDragStart = {
                            draggingIndex = index
                            onResizeStart?.invoke(index, reportSizes())
                        },
                        onDrag = { delta -> resize(index, delta) },
                        onDragEnd = {
                            draggingIndex = -1
                            onResizeEnd?.invoke(index, reportSizes())
                        },
                        onDoubleTap = {
                            when {
                                startCollapsible -> collapse(index, ElSplitterCollapseType.Start)
                                endCollapsible -> collapse(index, ElSplitterCollapseType.End)
                            }
                        },
                        barContent = splitBarContent,
                        barIndex = index,
                    )
                }
            }
        }
    }
}

@Composable
private fun SplitterBar(
    modifier: Modifier,
    direction: ElSplitterDirection,
    dividerSize: Dp,
    enabled: Boolean,
    active: Boolean,
    color: Color,
    activeColor: Color,
    onHover: (Boolean) -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
    onDoubleTap: () -> Unit,
    barContent: (@Composable (Int) -> Unit)? = null,
    barIndex: Int = 0,
) {
    val interaction = remember { MutableInteractionSource() }
    val state = rememberElInteractionState(interaction)
    LaunchedEffect(state.hovered) { onHover(state.hovered) }
    val barColor = if (enabled && (active || state.pressed)) activeColor else color
    val horizontal = direction == ElSplitterDirection.Horizontal
    Box(
        modifier = modifier
            .hoverable(interaction)
            .pointerInput(direction, enabled) {
                if (!enabled) return@pointerInput
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() },
                ) { change, drag ->
                    change.consume()
                    onDrag(if (horizontal) drag.x else drag.y)
                }
            }
            .pointerInput(direction) {
                detectTapGestures(onDoubleTap = { onDoubleTap() })
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .then(
                    if (horizontal) {
                        Modifier.width(dividerSize).fillMaxHeight()
                    } else {
                        Modifier.height(dividerSize).fillMaxWidth()
                    },
                )
                .background(barColor),
        )
        if (barContent != null) barContent(barIndex)
    }
}

/**
 * `<el-splitter>`: resizable panes with draggable dividers. Sizes are kept as
 * fractions so the splitter adapts to its container.
 *
 * Prefer the overload taking a list of [ElSplitterPanel] when per-panel
 * `min` / `max` / `size`, collapsing or resize events are needed.
 */
@Composable
fun ElSplitter(
    modifier: Modifier = Modifier,
    direction: ElSplitterDirection = ElSplitterDirection.Horizontal,
    panelCount: Int = 2,
    initialSizes: List<Float>? = null,
    minSize: Dp = 24.dp,
    dividerSize: Dp = 1.dp,
    disabled: Boolean = false,
    onResize: ((List<Float>) -> Unit)? = null,
    panelContent: @Composable (index: Int) -> Unit,
) {
    require(panelCount > 0) { "panelCount must be positive" }
    val sizes = remember(panelCount) {
        val initial = initialSizes?.takeIf { it.size == panelCount && it.all { v -> v > 0f } }
            ?: List(panelCount) { 1f / panelCount }
        val sum = initial.sum().takeIf { it > 0f } ?: 1f
        mutableStateListOf(*initial.map { it / sum }.toTypedArray())
    }
    var hoveredIndex by remember { mutableStateOf(-1) }
    val density = LocalDensity.current
    val minFractionBase = with(density) { minSize.toPx() }
    val colors = LocalElTheme.current.colors

    BoxWithConstraints(modifier.fillMaxSize()) {
        val totalPx = if (direction == ElSplitterDirection.Horizontal) constraints.maxWidth.toFloat() else constraints.maxHeight.toFloat()
        val minFraction = if (totalPx > 0f) (minFractionBase / totalPx).coerceAtMost(0.45f) else 0f

        fun resize(index: Int, deltaFraction: Float) {
            if (totalPx <= 0f) return
            val left = sizes[index]
            val right = sizes[index + 1]
            val delta = deltaFraction.coerceIn(-left + minFraction, right - minFraction)
            if (delta == 0f) return
            sizes[index] = left + delta
            sizes[index + 1] = right - delta
            onResize?.invoke(sizes.toList())
        }

        if (direction == ElSplitterDirection.Horizontal) {
            Row(Modifier.fillMaxSize()) {
                for (index in 0 until panelCount) {
                    Box(Modifier.weight(sizes[index].coerceAtLeast(0.0001f)).fillMaxHeight()) {
                        panelContent(index)
                    }
                    if (index < panelCount - 1) {
                        SplitterDivider(
                            direction = direction,
                            size = dividerSize,
                            hovered = hoveredIndex == index,
                            onHover = { hoveredIndex = if (it) index else -1 },
                            color = colors.border,
                            activeColor = colors.primary,
                            disabled = disabled,
                        ) { drag ->
                            resize(index, drag / totalPx)
                        }
                    }
                }
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                for (index in 0 until panelCount) {
                    Box(Modifier.weight(sizes[index].coerceAtLeast(0.0001f)).fillMaxWidth()) {
                        panelContent(index)
                    }
                    if (index < panelCount - 1) {
                        SplitterDivider(
                            direction = direction,
                            size = dividerSize,
                            hovered = hoveredIndex == index,
                            onHover = { hoveredIndex = if (it) index else -1 },
                            color = colors.border,
                            activeColor = colors.primary,
                            disabled = disabled,
                        ) { drag ->
                            resize(index, drag / totalPx)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SplitterDivider(
    direction: ElSplitterDirection,
    size: Dp,
    hovered: Boolean,
    onHover: (Boolean) -> Unit,
    color: Color,
    activeColor: Color,
    disabled: Boolean = false,
    onDrag: (Float) -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val state = rememberElInteractionState(interaction)
    LaunchedEffect(state.hovered) { onHover(state.hovered) }
    val hitSize = 8.dp
    val modifier = if (direction == ElSplitterDirection.Horizontal) {
        Modifier.width(hitSize).fillMaxHeight()
    } else {
        Modifier.height(hitSize).fillMaxWidth()
    }
    val barColor = if (!disabled && (hovered || state.pressed)) activeColor else color
    if (disabled) {
        Box(modifier.background(Color.Transparent), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .then(if (direction == ElSplitterDirection.Horizontal) Modifier.width(size).fillMaxHeight() else Modifier.height(size).fillMaxWidth())
                    .background(barColor),
            )
        }
        return
    }
    Box(
        modifier = modifier
            .hoverable(interaction)
            .pointerInput(direction) {
                detectDragGestures(
                    onDragStart = { onHover(true) },
                    onDragEnd = { onHover(false) },
                ) { change, drag ->
                    change.consume()
                    onDrag(if (direction == ElSplitterDirection.Horizontal) drag.x else drag.y)
                }
            }
            .background(Color.Transparent),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .then(
                    if (direction == ElSplitterDirection.Horizontal) Modifier.width(size).fillMaxHeight()
                    else Modifier.height(size).fillMaxWidth()
                )
                .background(barColor),
        )
    }
}
