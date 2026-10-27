package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Single-value `<el-slider>`.
 */
@Composable
fun ElSlider(
    value: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    min: Double = 0.0,
    max: Double = 100.0,
    step: Double = 1.0,
    disabled: Boolean = false,
    showStops: Boolean = false,
    showTooltip: Boolean = true,
    formatTooltip: (Double) -> String = { it.toString().removeSuffix(".0") },
    height: Dp = 32.dp,
) {
    require(max > min) { "max must be greater than min" }
    SliderImpl(
        values = listOf(value),
        onValuesChange = { onValueChange(it.first()) },
        min = min,
        max = max,
        step = step,
        disabled = disabled,
        showStops = showStops,
        showTooltip = showTooltip,
        formatTooltip = formatTooltip,
        height = height,
        modifier = modifier,
    )
}

/** Range `<el-slider range>`. */
@Composable
fun ElSlider(
    value: ClosedFloatingPointRange<Double>,
    onValueChange: (ClosedFloatingPointRange<Double>) -> Unit,
    modifier: Modifier = Modifier,
    min: Double = 0.0,
    max: Double = 100.0,
    step: Double = 1.0,
    disabled: Boolean = false,
    showStops: Boolean = false,
    showTooltip: Boolean = true,
    formatTooltip: (Double) -> String = { it.toString().removeSuffix(".0") },
    height: Dp = 32.dp,
) {
    require(max > min) { "max must be greater than min" }
    SliderImpl(
        values = listOf(value.start, value.endInclusive),
        onValuesChange = { onValueChange(it[0]..it[1]) },
        min = min,
        max = max,
        step = step,
        disabled = disabled,
        showStops = showStops,
        showTooltip = showTooltip,
        formatTooltip = formatTooltip,
        height = height,
        modifier = modifier,
    )
}

@Composable
private fun SliderImpl(
    values: List<Double>,
    onValuesChange: (List<Double>) -> Unit,
    min: Double,
    max: Double,
    step: Double,
    disabled: Boolean,
    showStops: Boolean,
    showTooltip: Boolean,
    formatTooltip: (Double) -> String,
    height: Dp,
    modifier: Modifier,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val density = LocalDensity.current
    var draggingIndex by remember { mutableStateOf(-1) }
    var activeIndex by remember { mutableStateOf(-1) }
    val sorted = values.sorted()
    val isRange = sorted.size >= 2
    val latestValues by rememberUpdatedState(values)
    val latestOnValuesChange by rememberUpdatedState(onValuesChange)

    BoxWithConstraints(modifier.fillMaxWidth().height(height + 24.dp)) {
        val totalWidth = maxWidth
        val thumbRadius = 10.dp
        val trackWidth = (totalWidth - thumbRadius * 2).coerceAtLeast(1.dp)
        fun xFor(value: Double): Dp {
            val ratio = ((value - min) / (max - min)).coerceIn(0.0, 1.0)
            return thumbRadius + trackWidth * ratio.toFloat()
        }
        fun valueFor(x: Dp): Double {
            val px = with(density) { (x - thumbRadius).toPx() }
            val width = with(density) { trackWidth.toPx() }
            val ratio = (px / width).coerceIn(0f, 1f)
            val raw = min + ratio * (max - min)
            val stepped = (kotlin.math.round((raw - min) / step) * step + min)
            return stepped.coerceIn(min, max)
        }

        fun update(index: Int, next: Double) {
            val list = sorted.toMutableList()
            list[index] = next
            if (isRange) {
                list.sort()
            }
            onValuesChange(list)
        }

        fun nearestIndex(x: Dp): Int {
            if (!isRange) return 0
            return if (abs(with(density) { (x - xFor(sorted[0])).toPx() }) <= abs(with(density) { (x - xFor(sorted[1])).toPx() })) 0 else 1
        }

        // Tooltip
        if (showTooltip && activeIndex >= 0) {
            val bubbleValue = sorted[activeIndex.coerceIn(sorted.indices)]
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = xFor(bubbleValue) - 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .background(colors.textPrimary, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                ) {
                    BasicText(
                        text = formatTooltip(bubbleValue),
                        style = theme.typography.extraSmall.copy(color = colors.white),
                    )
                }
            }
        }

        RowTrack(
            modifier = Modifier.padding(top = 27.dp),
            xFor = { xFor(it) },
            edgeInset = thumbRadius,
            values = sorted,
            activeFromMin = !isRange,
            activeColor = if (disabled) colors.border else colors.primary,
            inactiveColor = colors.border,
        )

        if (showStops && step > 0) {
            val count = ((max - min) / step).toInt().coerceIn(0, 100)
            repeat(count) { index ->
                val dotValue = min + (index + 1) * step
                if (dotValue < max) {
                    Box(
                        Modifier
                            .padding(top = 27.dp)
                            .offset(x = xFor(dotValue) - 3.dp)
                            .size(6.dp)
                            .background(colors.background, CircleShape),
                    )
                }
            }
        }

        sorted.forEachIndexed { index, item ->
            Box(
                Modifier
                    .padding(top = 20.dp)
                    .offset(x = xFor(item) - thumbRadius)
                    .size(20.dp)
                    .shadow(2.dp, CircleShape)
                    .background(if (disabled) colors.fill else colors.white, CircleShape)
                    .semantics {
                        contentDescription = when {
                            isRange && index == 0 -> locale.slider.defaultRangeStartLabel
                            isRange -> locale.slider.defaultRangeEndLabel
                            else -> locale.slider.defaultLabel
                                .replace("{min}", formatTooltip(min))
                                .replace("{max}", formatTooltip(max))
                        }
                    },
            )
        }

        Box(
            Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .height(20.dp)
                .then(
                    if (disabled) Modifier else Modifier
                        .pointerInput(min, max, step, disabled, trackWidth, thumbRadius) {
                            fun fractionForX(px: Float): Float {
                                val radius = with(density) { thumbRadius.toPx() }
                                val track = with(density) { trackWidth.toPx() }
                                return ((px - radius) / track).coerceIn(0f, 1f)
                            }
                            detectTapGestures { offset ->
                                val fraction = fractionForX(offset.x)
                                val current = latestValues.sorted()
                                val index = if (current.size < 2) 0 else {
                                    val first = ((current[0] - min) / (max - min)).toFloat()
                                    val second = ((current[1] - min) / (max - min)).toFloat()
                                    if (abs(fraction - first) <= abs(fraction - second)) 0 else 1
                                }
                                val raw = min + fraction * (max - min)
                                val stepped = (kotlin.math.round((raw - min) / step) * step + min).coerceIn(min, max)
                                val next = if (current.size >= 2) {
                                    if (index == 0) stepped.coerceAtMost(current[1]) else stepped.coerceAtLeast(current[0])
                                } else stepped
                                val list = current.toMutableList()
                                list[index] = next
                                activeIndex = index
                                latestOnValuesChange(list)
                            }
                        }
                        .pointerInput(min, max, step, disabled, trackWidth, thumbRadius) {
                            fun fractionForX(px: Float): Float {
                                val radius = with(density) { thumbRadius.toPx() }
                                val track = with(density) { trackWidth.toPx() }
                                return ((px - radius) / track).coerceIn(0f, 1f)
                            }
                            fun applyAt(index: Int, fraction: Float) {
                                val current = latestValues.sorted()
                                if (index !in current.indices) return
                                val raw = min + fraction * (max - min)
                                val stepped = (kotlin.math.round((raw - min) / step) * step + min).coerceIn(min, max)
                                val next = if (current.size >= 2) {
                                    if (index == 0) stepped.coerceAtMost(current[1]) else stepped.coerceAtLeast(current[0])
                                } else stepped
                                val list = current.toMutableList()
                                list[index] = next
                                activeIndex = index
                                latestOnValuesChange(list)
                            }
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val fraction = fractionForX(offset.x)
                                    val current = latestValues.sorted()
                                    val index = if (current.size < 2) 0 else {
                                        val first = ((current[0] - min) / (max - min)).toFloat()
                                        val second = ((current[1] - min) / (max - min)).toFloat()
                                        if (abs(fraction - first) <= abs(fraction - second)) 0 else 1
                                    }
                                    draggingIndex = index
                                    activeIndex = index
                                    applyAt(index, fraction)
                                },
                                onDragEnd = { draggingIndex = -1; activeIndex = -1 },
                                onDragCancel = { draggingIndex = -1; activeIndex = -1 },
                            ) { change, _ ->
                                change.consume()
                                val current = draggingIndex
                                if (current < 0) return@detectDragGestures
                                applyAt(current, fractionForX(change.position.x))
                            }
                        }
                ),
        )
    }
}

@Composable
private fun RowTrack(
    modifier: Modifier,
    xFor: (Double) -> Dp,
    edgeInset: Dp,
    values: List<Double>,
    activeFromMin: Boolean,
    activeColor: androidx.compose.ui.graphics.Color,
    inactiveColor: androidx.compose.ui.graphics.Color,
) {
    val start = values.firstOrNull() ?: return
    val end = values.lastOrNull() ?: return
    BoxWithConstraints(modifier.fillMaxWidth().height(6.dp)) {
        val y = 0.dp
        val trackWidth = (maxWidth - edgeInset * 2).coerceAtLeast(0.dp)
        Box(
            Modifier
                .offset(x = edgeInset, y = y)
                .width(trackWidth)
                .height(6.dp)
                .background(inactiveColor, RoundedCornerShape(3.dp)),
        )
        val left = if (activeFromMin) 0.dp
        else (xFor(start) - edgeInset).coerceIn(0.dp, trackWidth)
        val right = (xFor(end) - edgeInset).coerceIn(0.dp, trackWidth)
        if (right > left) {
            Box(
                Modifier
                    .offset(x = edgeInset + left, y = y)
                    .width(right - left)
                    .height(6.dp)
                    .background(activeColor, RoundedCornerShape(3.dp)),
            )
        }
    }
}
