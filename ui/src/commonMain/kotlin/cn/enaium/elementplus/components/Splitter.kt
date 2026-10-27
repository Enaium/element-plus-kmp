package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
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
import kotlin.math.max

enum class ElSplitterDirection { Horizontal, Vertical }

/**
 * `<el-splitter>`: resizable panes with draggable dividers. Sizes are kept as
 * fractions so the splitter adapts to its container.
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
