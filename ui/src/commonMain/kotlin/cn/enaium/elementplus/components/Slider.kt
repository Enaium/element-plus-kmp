package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlin.math.abs

/**
 * A labelled mark drawn on a slider track, the Compose equivalent of an
 * `el-slider` `marks` entry (`{ label, style }`).
 */
@Immutable
data class ElSliderMark(
    val label: String,
    val color: Color? = null,
)

/**
 * Single-value `<el-slider>`.
 *
 * @param vertical lays the track out from bottom to top; [height] becomes the
 *   track length and [showInput] is ignored.
 * @param size wrapper/track/thumb size token; overrides [height] when set.
 * @param marks labelled marks shown next to the track.
 * @param showInput renders a built-in [ElInputNumber] beside the slider (single
 *   value and range overloads both support it).
 * @param placement side of the thumb the value tooltip is drawn on.
 * @param onChange commit event fired on click and when a drag ends.
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
    vertical: Boolean = false,
    marks: Map<Double, ElSliderMark> = emptyMap(),
    showInput: Boolean = false,
    showInputControls: Boolean = true,
    size: ElSize? = null,
    inputSize: ElSize? = null,
    placement: ElPlacement = ElPlacement.Top,
    onChange: ((Double) -> Unit)? = null,
    ariaLabel: String? = null,
) {
    require(max > min) { "max must be greater than min" }
    val latestOnChange by rememberUpdatedState(onChange)
    SliderBody(
        modifier = modifier,
        vertical = vertical,
        showInput = showInput,
        inputContent = {
            ElInputNumber(
                value = value,
                onValueChange = { next -> next?.let { onValueChange(it.coerceIn(min, max)) } },
                min = min,
                max = max,
                step = step,
                disabled = disabled,
                controls = showInputControls,
                size = inputSize ?: size,
                width = 110.dp,
            )
        },
    ) { sliderModifier ->
        SliderImpl(
            values = listOf(value),
            onValuesChange = { onValueChange(it.first()) },
            onCommit = { latestOnChange?.invoke(it.first()) },
            min = min,
            max = max,
            step = step,
            disabled = disabled,
            showStops = showStops,
            showTooltip = showTooltip,
            formatTooltip = formatTooltip,
            marks = marks,
            vertical = vertical,
            layoutHeight = if (vertical) height else wrapperHeight(height, size),
            size = size,
            placement = placement,
            ariaLabel = ariaLabel,
            rangeStartLabel = null,
            rangeEndLabel = null,
            modifier = sliderModifier,
        )
    }
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
    vertical: Boolean = false,
    marks: Map<Double, ElSliderMark> = emptyMap(),
    showInput: Boolean = false,
    showInputControls: Boolean = true,
    size: ElSize? = null,
    inputSize: ElSize? = null,
    placement: ElPlacement = ElPlacement.Top,
    onChange: ((ClosedFloatingPointRange<Double>) -> Unit)? = null,
    ariaLabel: String? = null,
    rangeStartLabel: String? = null,
    rangeEndLabel: String? = null,
) {
    require(max > min) { "max must be greater than min" }
    val latestOnChange by rememberUpdatedState(onChange)
    SliderBody(
        modifier = modifier,
        vertical = vertical,
        showInput = showInput,
        inputContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ElInputNumber(
                    value = value.start,
                    onValueChange = { next ->
                        next?.let { onValueChange(it.coerceIn(min, value.endInclusive)..value.endInclusive) }
                    },
                    min = min,
                    max = max,
                    step = step,
                    disabled = disabled,
                    controls = showInputControls,
                    size = inputSize ?: size,
                    width = 90.dp,
                )
                BasicText(
                    text = "-",
                    modifier = Modifier.width(16.dp),
                    style = LocalElTheme.current.typography.small.copy(
                        color = LocalElTheme.current.colors.textSecondary,
                    ),
                )
                ElInputNumber(
                    value = value.endInclusive,
                    onValueChange = { next ->
                        next?.let { onValueChange(value.start..it.coerceIn(value.start, max)) }
                    },
                    min = min,
                    max = max,
                    step = step,
                    disabled = disabled,
                    controls = showInputControls,
                    size = inputSize ?: size,
                    width = 90.dp,
                )
            }
        },
    ) { sliderModifier ->
        SliderImpl(
            values = listOf(value.start, value.endInclusive),
            onValuesChange = { onValueChange(it[0]..it[1]) },
            onCommit = { latestOnChange?.invoke(it[0]..it[1]) },
            min = min,
            max = max,
            step = step,
            disabled = disabled,
            showStops = showStops,
            showTooltip = showTooltip,
            formatTooltip = formatTooltip,
            marks = marks,
            vertical = vertical,
            layoutHeight = if (vertical) height else wrapperHeight(height, size),
            size = size,
            placement = placement,
            ariaLabel = ariaLabel,
            rangeStartLabel = rangeStartLabel,
            rangeEndLabel = rangeEndLabel,
            modifier = sliderModifier,
        )
    }
}

private fun wrapperHeight(height: Dp, size: ElSize?): Dp = when (size) {
    ElSize.Large -> 40.dp
    ElSize.Small -> 24.dp
    ElSize.Default -> 32.dp
    null -> height
}

/**
 * Lays the slider out with an optional input box. Vertical sliders always use
 * the full width of their own container, so `showInput` is ignored there.
 */
@Composable
private fun SliderBody(
    modifier: Modifier,
    vertical: Boolean,
    showInput: Boolean,
    inputContent: @Composable () -> Unit,
    slider: @Composable (Modifier) -> Unit,
) {
    if (!showInput || vertical) {
        slider(modifier)
        return
    }
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f)) { slider(Modifier) }
        Spacer(Modifier.width(12.dp))
        inputContent()
    }
}

@Composable
private fun SliderImpl(
    values: List<Double>,
    onValuesChange: (List<Double>) -> Unit,
    onCommit: (List<Double>) -> Unit,
    min: Double,
    max: Double,
    step: Double,
    disabled: Boolean,
    showStops: Boolean,
    showTooltip: Boolean,
    formatTooltip: (Double) -> String,
    marks: Map<Double, ElSliderMark>,
    vertical: Boolean,
    layoutHeight: Dp,
    size: ElSize?,
    placement: ElPlacement,
    ariaLabel: String?,
    rangeStartLabel: String?,
    rangeEndLabel: String?,
    modifier: Modifier,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    var draggingIndex by remember { mutableStateOf(-1) }
    var hoverIndex by remember { mutableStateOf(-1) }
    var lastDragValues: List<Double>? = null
    val sorted = values.sorted()
    val isRange = sorted.size >= 2
    val latestValues by rememberUpdatedState(values)
    val latestOnValuesChange by rememberUpdatedState(onValuesChange)
    val latestOnCommit by rememberUpdatedState(onCommit)

    val thumbDiameter = if (size == ElSize.Small) 14.dp else 20.dp
    val thumbRadius = thumbDiameter / 2
    val trackThickness = if (size == ElSize.Small) 4.dp else 6.dp
    val sliderLabel = ariaLabel
    val tooltipSpace = if (showTooltip) 22.dp else 0.dp
    val marksWidth = if (marks.isEmpty()) 0.dp else 56.dp
    val reserveAbove = if (showTooltip && (placement.isTop || (!vertical && !placement.isBottom))) tooltipSpace else 0.dp
    val reserveBelow = if (showTooltip && placement.isBottom) tooltipSpace else 0.dp
    val marksSpace = if (!vertical && marks.isNotEmpty()) 24.dp else 0.dp
    val totalHeight = if (vertical) layoutHeight else reserveAbove + layoutHeight + reserveBelow + marksSpace
    val totalWidth = if (vertical) thumbDiameter + marksWidth else 0.dp

    BoxWithConstraints(
        modifier = if (vertical) {
            modifier.height(totalHeight).width(totalWidth)
        } else {
            modifier.fillMaxWidth().height(totalHeight)
        },
    ) {
        val trackLength = (
            if (vertical) maxHeight - thumbDiameter else maxWidth - thumbDiameter
            ).coerceAtLeast(1.dp)
        val centerX = if (vertical) thumbDiameter / 2 else 0.dp
        val centerY = if (vertical) 0.dp else reserveAbove + layoutHeight / 2
        fun ratioOf(value: Double): Float = ((value - min) / (max - min)).coerceIn(0.0, 1.0).toFloat()
        fun xFor(value: Double): Dp =
            if (vertical) centerX else thumbRadius + trackLength * ratioOf(value)
        fun yFor(value: Double): Dp =
            if (vertical) thumbRadius + trackLength * (1f - ratioOf(value)) else centerY

        fun clampStep(raw: Double): Double {
            if (step <= 0.0) return raw.coerceIn(min, max)
            val stepped = kotlin.math.round((raw - min) / step) * step + min
            return stepped.coerceIn(min, max)
        }

        fun nearestIndex(fraction: Float): Int {
            if (!isRange) return 0
            val current = sorted
            val first = ratioOf(current[0])
            val second = ratioOf(current[1])
            return if (abs(fraction - first) <= abs(fraction - second)) 0 else 1
        }

        fun fractionAt(position: Offset): Float {
            val lengthPx = with(density) { trackLength.toPx() }
            val radiusPx = with(density) { thumbRadius.toPx() }
            val raw = if (vertical) {
                (position.y - radiusPx) / lengthPx
            } else {
                (position.x - radiusPx) / lengthPx
            }
            val fraction = if (vertical) 1f - raw else raw
            return fraction.coerceIn(0f, 1f)
        }

        fun applyAt(index: Int, fraction: Float): List<Double>? {
            val current = latestValues.sorted()
            if (index !in current.indices) return null
            val stepped = clampStep(min + fraction * (max - min))
            val next = if (current.size >= 2) {
                if (index == 0) stepped.coerceAtMost(current[1]) else stepped.coerceAtLeast(current[0])
            } else {
                stepped
            }
            if (next == current[index]) return current
            val list = current.toMutableList()
            list[index] = next
            latestOnValuesChange(list)
            return list
        }

        // Track and active range.
        if (vertical) {
            Box(
                Modifier
                    .offset(x = centerX - trackThickness / 2, y = thumbRadius)
                    .width(trackThickness)
                    .height(trackLength)
                    .background(colors.border, RoundedCornerShape(trackThickness / 2)),
            )
            val activeColor = if (disabled) colors.border else colors.primary
            val bottom = if (isRange) yFor(sorted[0]) else yFor(min)
            val top = if (isRange) yFor(sorted[1]) else yFor(sorted[0])
            if (bottom > top) {
                Box(
                    Modifier
                        .offset(x = centerX - trackThickness / 2, y = top)
                        .width(trackThickness)
                        .height(bottom - top)
                        .background(activeColor, RoundedCornerShape(trackThickness / 2)),
                )
            }
        } else {
            Box(
                Modifier
                    .offset(x = thumbRadius, y = centerY - trackThickness / 2)
                    .width(trackLength)
                    .height(trackThickness)
                    .background(colors.border, RoundedCornerShape(trackThickness / 2)),
            )
            val activeColor = if (disabled) colors.border else colors.primary
            val left = if (isRange) xFor(sorted[0]) - thumbRadius else 0.dp
            val right = xFor(sorted.last()) - thumbRadius
            if (right > left) {
                Box(
                    Modifier
                        .offset(x = thumbRadius + left, y = centerY - trackThickness / 2)
                        .width(right - left)
                        .height(trackThickness)
                        .background(activeColor, RoundedCornerShape(trackThickness / 2)),
                )
            }
        }

        // Stop markers.
        if (showStops && step > 0) {
            val count = ((max - min) / step).toInt().coerceIn(0, 100)
            repeat(count) { index ->
                val dotValue = min + (index + 1) * step
                if (dotValue < max) {
                    if (vertical) {
                        Box(
                            Modifier
                                .offset(x = centerX - 3.dp, y = yFor(dotValue) - 3.dp)
                                .width(6.dp)
                                .height(6.dp)
                                .background(colors.background, CircleShape),
                        )
                    } else {
                        Box(
                            Modifier
                                .offset(x = xFor(dotValue) - 3.dp, y = centerY - 3.dp)
                                .width(6.dp)
                                .height(6.dp)
                                .background(colors.background, CircleShape),
                        )
                    }
                }
            }
        }

        // Marks and their labels.
        if (marks.isNotEmpty()) {
            val labelStyle = theme.typography.extraSmall.copy(color = colors.textSecondary)
            marks.forEach { (markValue, mark) ->
                if (markValue < min || markValue > max) return@forEach
                val style = if (mark.color != null) labelStyle.copy(color = mark.color) else labelStyle
                val labelWidth = with(density) {
                    textMeasurer.measure(mark.label, style = style).size.width.toDp()
                }
                if (vertical) {
                    BasicText(
                        text = mark.label,
                        modifier = Modifier.offset(
                            x = thumbDiameter + 6.dp,
                            y = yFor(markValue) - 7.dp,
                        ),
                        style = style,
                        maxLines = 1,
                    )
                } else {
                    BasicText(
                        text = mark.label,
                        modifier = Modifier.offset(
                            x = (xFor(markValue) - labelWidth / 2)
                                .coerceIn(0.dp, (maxWidth - labelWidth).coerceAtLeast(0.dp)),
                            y = reserveAbove + layoutHeight + 4.dp,
                        ),
                        style = style,
                        maxLines = 1,
                    )
                }
            }
        }

        // Thumbs.
        sorted.forEachIndexed { index, item ->
            val offsetX = (xFor(item) - thumbRadius).coerceIn(0.dp, (maxWidth - thumbDiameter).coerceAtLeast(0.dp))
            val offsetY = if (vertical) yFor(item) - thumbRadius else reserveAbove + (layoutHeight - thumbDiameter) / 2
            Box(
                Modifier
                    .offset(x = offsetX, y = offsetY)
                    .width(thumbDiameter)
                    .height(thumbDiameter)
                    .shadow(2.dp, CircleShape)
                    .background(
                        if (disabled) colors.fill else colors.white,
                        CircleShape,
                    )
                    .border(
                        2.dp,
                        if (disabled) colors.borderLight else colors.primary,
                        CircleShape,
                    )
                    .semantics {
                        contentDescription = when {
                            sliderLabel != null && sliderLabel.isNotBlank() -> sliderLabel
                            isRange && index == 0 -> rangeStartLabel ?: locale.slider.defaultRangeStartLabel
                            isRange -> rangeEndLabel ?: locale.slider.defaultRangeEndLabel
                            else -> locale.slider.defaultLabel
                                .replace("{min}", formatTooltip(min))
                                .replace("{max}", formatTooltip(max))
                        }
                    },
            )
        }

        // Value tooltip.
        val shownIndex = when {
            draggingIndex in sorted.indices -> draggingIndex
            hoverIndex in sorted.indices -> hoverIndex
            else -> -1
        }
        if (showTooltip && shownIndex >= 0) {
            val bubbleValue = sorted[shownIndex]
            val text = formatTooltip(bubbleValue)
            val bubbleStyle = theme.typography.extraSmall.copy(color = colors.white)
            val textWidth = with(density) { textMeasurer.measure(text, style = bubbleStyle).size.width.toDp() }
            val bubbleWidth = textWidth + 12.dp
            val bubbleHeight = 20.dp
            val thumbX = xFor(bubbleValue)
            val thumbY = yFor(bubbleValue)
            val bubbleX = when {
                placement.isLeft -> thumbX - thumbRadius - bubbleWidth - 4.dp
                placement.isRight -> thumbX + thumbRadius + 4.dp
                else -> thumbX - bubbleWidth / 2
            }
            val bubbleY = when {
                placement.isTop -> thumbY - thumbRadius - bubbleHeight - 4.dp
                placement.isBottom -> thumbY + thumbRadius + 4.dp
                else -> thumbY - bubbleHeight / 2
            }
            Box(
                Modifier
                    .offset(x = bubbleX, y = bubbleY)
                    .background(colors.textPrimary, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp),
            ) {
                BasicText(text = text, style = bubbleStyle, maxLines = 1)
            }
        }

        // Interaction layer (track + thumbs region, excludes marks).
        Box(
            Modifier
                .then(
                    if (vertical) {
                        Modifier.offset(x = 0.dp, y = 0.dp).width(thumbDiameter).height(layoutHeight)
                    } else {
                        Modifier.offset(x = 0.dp, y = reserveAbove).fillMaxWidth().height(layoutHeight)
                    }
                )
                .then(
                    if (disabled) {
                        Modifier
                    } else {
                        Modifier
                            .pointerInput(min, max, step, trackLength, thumbDiameter, vertical) {
                                awaitPointerEventScope {
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        val position = event.changes.firstOrNull()?.position
                                        if (position == null) continue
                                        when (event.type) {
                                            PointerEventType.Enter, PointerEventType.Move ->
                                                hoverIndex = nearestIndex(fractionAt(position))
                                            PointerEventType.Exit -> hoverIndex = -1
                                            else -> Unit
                                        }
                                    }
                                }
                            }
                            .pointerInput(min, max, step, disabled, trackLength, thumbDiameter, vertical) {
                                detectTapGestures { offset ->
                                    val fraction = fractionAt(offset)
                                    val updated = applyAt(nearestIndex(fraction), fraction)
                                    latestOnCommit(updated ?: latestValues.sorted())
                                }
                            }
                            .pointerInput(min, max, step, disabled, trackLength, thumbDiameter, vertical) {
                                fun clampIndex(fraction: Float): Int {
                                    if (!isRange) return 0
                                    val current = latestValues.sorted()
                                    val first = ratioOf(current[0])
                                    val second = ratioOf(current[1])
                                    return if (abs(fraction - first) <= abs(fraction - second)) 0 else 1
                                }
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        val index = clampIndex(fractionAt(offset))
                                        draggingIndex = index
                                        lastDragValues = applyAt(index, fractionAt(offset))
                                    },
                                    onDragEnd = {
                                        draggingIndex = -1
                                        latestOnCommit(lastDragValues ?: latestValues.sorted())
                                        lastDragValues = null
                                    },
                                    onDragCancel = {
                                        draggingIndex = -1
                                        lastDragValues = null
                                    },
                                ) { change, _ ->
                                    change.consume()
                                    val current = draggingIndex
                                    if (current < 0) return@detectDragGestures
                                    lastDragValues = applyAt(current, fractionAt(change.position))
                                }
                            }
                    }
                ),
        )
    }
}
