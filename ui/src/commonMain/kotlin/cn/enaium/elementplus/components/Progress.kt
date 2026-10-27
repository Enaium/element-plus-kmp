package cn.enaium.elementplus.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.theme.LocalElTheme
import kotlin.math.min

enum class ElProgressType { Line, Circle, Dashboard }
enum class ElProgressStatus { Success, Warning, Exception }

/**
 * `<el-progress>` in line, circle and dashboard forms, with status colors,
 * inside text and striped fills.
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
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val value = percentage.coerceIn(0.0, 100.0)
    val progressColor = when (status) {
        ElProgressStatus.Success -> colors.success
        ElProgressStatus.Warning -> colors.warning
        ElProgressStatus.Exception -> colors.danger
        null -> color ?: colors.primary
    }
    if (type == ElProgressType.Line) {
        val animated by animateFloatAsState(value.toFloat() / 100f, tween(400), label = "progress")
        val barHeight = if (textInside) maxOf(strokeWidth, 20.dp) else strokeWidth
        androidx.compose.foundation.layout.Column(modifier) {
            androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .weight(1f)
                        .height(barHeight)
                        .clip(RoundedCornerShape(barHeight / 2))
                        .background(colors.fillDark),
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(animated)
                            .height(barHeight)
                            .clip(RoundedCornerShape(barHeight / 2))
                            .background(progressColor),
                    ) {
                        if (striped) {
                            Canvas(Modifier.fillMaxWidth().height(barHeight)) {
                                val stripe = 6.dp.toPx()
                                var x = -size.height
                                while (x < size.width + size.height) {
                                    drawLine(
                                        color = Color.White.copy(alpha = 0.35f),
                                        start = Offset(x, size.height),
                                        end = Offset(x + size.height, 0f),
                                        strokeWidth = size.height / 2,
                                    )
                                    x += stripe
                                }
                            }
                        }
                        if (textInside && animated > 0.08f) {
                            BasicText(
                                text = format(value),
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 6.dp),
                                style = theme.typography.extraSmall.copy(color = colors.white),
                                maxLines = 1,
                            )
                        }
                    }
                }
                if (showText && !textInside) {
                    Box(
                        Modifier.widthIn(min = 46.dp, max = 140.dp).padding(start = 8.dp),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
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
    } else {
        val animated by animateFloatAsState(value.toFloat() / 100f, tween(400), label = "progress-circle")
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
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                drawArc(
                    color = progressColor,
                    startAngle = startAngle,
                    sweepAngle = maxSweep * animated,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
            }
            if (showText) {
                androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
