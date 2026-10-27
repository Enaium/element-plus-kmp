package cn.enaium.elementplus.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElProgressType { Line, Circle, Dashboard }
enum class ElProgressStatus { Success, Warning, Exception }
enum class ElProgressStrokeLinecap { Round, Square, Butt }

/** A color stop of a segmented progress bar (upstream `ProgressColor`). */
data class ElProgressColor(val color: Color, val percentage: Double)

/**
 * `<el-progress>` in line, circle and dashboard forms, with status colors,
 * inside text, striped/indeterminate fills, segmented colors and custom content.
 */
@Composable
fun ElProgress(
    percentage: Double,
    modifier: Modifier = Modifier,
    type: ElProgressType = ElProgressType.Line,
    strokeWidth: Dp = 6.dp,
    textInside: Boolean = false,
    status: ElProgressStatus? = null,
    color: Color? = null,
    showText: Boolean = true,
    striped: Boolean = false,
    width: Dp = 126.dp,
    format: (Double) -> String = { "${it.toInt()}%" },
    indeterminate: Boolean = false,
    duration: Int = 3,
    stripedFlow: Boolean = false,
    strokeLinecap: ElProgressStrokeLinecap = ElProgressStrokeLinecap.Round,
    colorStops: List<ElProgressColor> = emptyList(),
    colorFunction: ((Double) -> Color)? = null,
    content: (@Composable (Double) -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val value = percentage.coerceIn(0.0, 100.0)
    // A custom color always wins over `status`, matching upstream.
    val stopColor = colorStops.sortedBy { it.percentage }.let { stops ->
        stops.firstOrNull { it.percentage > value }?.color ?: stops.lastOrNull()?.color
    }
    val progressColor = color
        ?: colorFunction?.invoke(value)
        ?: stopColor
        ?: when (status) {
            ElProgressStatus.Success -> colors.success
            ElProgressStatus.Warning -> colors.warning
            ElProgressStatus.Exception -> colors.danger
            null -> colors.primary
        }
    if (type == ElProgressType.Line) {
        val animated by animateFloatAsState(value.toFloat() / 100f, tween(400), label = "progress")
        val barHeight = if (textInside) maxOf(strokeWidth, 20.dp) else strokeWidth
        val barShape = RoundedCornerShape(barHeight / 2)
        val transition = rememberInfiniteTransition(label = "progress-flow")
        val flow by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                tween(durationMillis = duration.coerceAtLeast(1) * 1000, easing = LinearEasing),
                RepeatMode.Restart,
            ),
            label = "progress-flow-value",
        )
        Column(modifier) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BoxWithConstraints(
                    Modifier
                        .weight(1f)
                        .height(barHeight)
                        .clip(barShape)
                        .background(colors.fillDark),
                ) {
                    val barWidth = maxWidth * animated
                    val translation = if (indeterminate) {
                        val parentPx = constraints.maxWidth.toFloat()
                        val barPx = parentPx * animated
                        -barPx + flow * (parentPx + barPx)
                    } else {
                        0f
                    }
                    Box(
                        Modifier
                            .width(barWidth)
                            .height(barHeight)
                            .graphicsLayer { translationX = translation }
                            .clip(barShape)
                            .background(progressColor),
                    ) {
                        if (striped) {
                            Canvas(Modifier.fillMaxSize()) {
                                val stripe = 10.dp.toPx()
                                val phase = if (stripedFlow) flow * stripe else 0f
                                var x = -size.height + phase
                                while (x < size.width + size.height) {
                                    drawLine(
                                        color = Color.Black.copy(alpha = 0.12f),
                                        start = Offset(x, size.height),
                                        end = Offset(x + size.height, 0f),
                                        strokeWidth = size.height / 2,
                                    )
                                    x += stripe
                                }
                            }
                        }
                        if (textInside && (content != null || showText)) {
                            Box(
                                Modifier.fillMaxSize().padding(horizontal = 6.dp),
                                contentAlignment = Alignment.CenterEnd,
                            ) {
                                if (content != null) {
                                    content(value)
                                } else {
                                    BasicText(
                                        text = format(value),
                                        style = theme.typography.extraSmall.copy(color = colors.white),
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                    }
                }
                if (!textInside && (content != null || showText)) {
                    Box(
                        Modifier
                            .then(if (content == null) Modifier.widthIn(min = 46.dp, max = 140.dp) else Modifier)
                            .padding(start = 8.dp),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        if (content != null) {
                            content(value)
                        } else {
                            BasicText(
                                text = format(value),
                                style = theme.typography.small.copy(color = colors.textSecondary),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    } else {
        val animated by animateFloatAsState(value.toFloat() / 100f, tween(400), label = "progress-circle")
        val strokeCap = when (strokeLinecap) {
            ElProgressStrokeLinecap.Round -> StrokeCap.Round
            ElProgressStrokeLinecap.Square -> StrokeCap.Square
            ElProgressStrokeLinecap.Butt -> StrokeCap.Butt
        }
        Box(modifier.size(width), contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(width)) {
                val stroke = strokeWidth.toPx()
                val inset = stroke / 2
                val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
                val startAngle = if (type == ElProgressType.Dashboard) 150f else -90f
                val maxSweep = if (type == ElProgressType.Dashboard) 240f else 360f
                drawArc(
                    color = colors.fillDark,
                    startAngle = startAngle,
                    sweepAngle = maxSweep,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = stroke, cap = strokeCap),
                )
                drawArc(
                    color = progressColor,
                    startAngle = startAngle,
                    sweepAngle = maxSweep * animated,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = stroke, cap = strokeCap),
                )
            }
            if (content != null || showText) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (content != null) {
                        content(value)
                    } else {
                        BasicText(
                            text = format(value),
                            style = theme.typography.extraLarge.copy(
                                color = colors.textPrimary,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                            ),
                        )
                    }
                }
            }
        }
    }
}
