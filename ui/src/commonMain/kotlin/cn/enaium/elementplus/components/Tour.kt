package cn.enaium.elementplus.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

class ElTourStep(
    val title: String,
    val description: String? = null,
    val targetBounds: Rect? = null,
    val nextText: String? = null,
    val prevText: String? = null,
    val content: (@Composable () -> Unit)? = null,
)

/**
 * `<el-tour>` with masks, spotlight targeting, progress and previous/next
 * navigation. [ElTourStep.targetBounds] accepts window coordinates (for example
 * from `onGloballyPositioned { it.boundsInWindow() }`).
 */
@Composable
fun ElTour(
    visible: Boolean,
    steps: List<ElTourStep>,
    current: Int,
    onCurrentChange: (Int) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    onFinish: (() -> Unit)? = null,
    showMask: Boolean = true,
    targetPadding: Dp = 6.dp,
    width: Dp = 320.dp,
) {
    if (!visible || steps.isEmpty()) return
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val step = steps[current.coerceIn(0, steps.lastIndex)]
    val density = LocalDensity.current
    val paddingPx = with(density) { targetPadding.toPx() }
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val screenWidth = constraints.maxWidth.toFloat()
            val screenHeight = constraints.maxHeight.toFloat()
            val target = step.targetBounds?.inflate(paddingPx)
            if (showMask) {
                Canvas(Modifier.fillMaxSize()) {
                    val path = Path().apply {
                        fillType = PathFillType.EvenOdd
                        addRect(Rect(0f, 0f, size.width, size.height))
                        if (target != null) {
                            addRoundRect(
                                RoundRect(
                                    rect = target,
                                    cornerRadius = CornerRadius(6.dp.toPx()),
                                ),
                            )
                        }
                    }
                    drawPath(path, Color.Black.copy(alpha = 0.55f))
                    if (target != null) {
                        drawRoundRect(
                            color = colors.primary,
                            topLeft = Offset(target.left, target.top),
                            size = androidx.compose.ui.geometry.Size(target.width, target.height),
                            cornerRadius = CornerRadius(6.dp.toPx()),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()),
                        )
                    }
                }
            }
            val cardWidthPx = with(density) { width.toPx() }
            val estimatedCardHeightPx = with(density) { 190.dp.toPx() }
            val gapPx = with(density) { 12.dp.toPx() }
            val targetX = target?.center?.x ?: (screenWidth / 2f)
            val targetY = target?.center?.y ?: (screenHeight / 2f)
            val x = (targetX - cardWidthPx / 2f).coerceIn(12f, (screenWidth - cardWidthPx - 12f).coerceAtLeast(12f))
            val y = if (target != null) {
                val below = target.bottom + gapPx
                if (below + estimatedCardHeightPx <= screenHeight - gapPx) below
                else (target.top - estimatedCardHeightPx - gapPx).coerceAtLeast(gapPx)
            } else {
                (targetY - estimatedCardHeightPx / 2f).coerceAtLeast(12f)
            }
            Column(
                Modifier
                    .offset(x = with(density) { x.toDp() }, y = with(density) { y.toDp() })
                    .width(width)
                    .background(colors.background, RoundedCornerShape(8.dp))
                    .padding(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicText(
                        text = step.title,
                        modifier = Modifier.weight(1f),
                        style = theme.typography.medium.copy(color = colors.textPrimary, fontWeight = FontWeight.Medium),
                    )
                    Box(
                        Modifier
                            .size(20.dp)
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.Center,
                    ) {
                        ElIcon(
                            ElIcons.Close,
                            size = 14.dp,
                            tint = colors.textPlaceholder,
                            contentDescription = locale.tour.close,
                        )
                    }
                }
                if (step.description != null) {
                    Spacer(Modifier.padding(top = 8.dp))
                    BasicText(
                        text = step.description,
                        style = theme.typography.base.copy(color = colors.textRegular),
                    )
                }
                step.content?.let {
                    Spacer(Modifier.padding(top = 8.dp))
                    it()
                }
                Spacer(Modifier.padding(top = 14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicText(
                        text = "${current + 1}/${steps.size}",
                        style = theme.typography.small.copy(color = colors.textSecondary),
                    )
                    Spacer(Modifier.weight(1f))
                    if (current > 0) {
                        Box(
                            Modifier
                                .clickable { onCurrentChange(current - 1) }
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                        ) {
                            BasicText(
                                text = step.prevText ?: locale.tour.previous,
                                style = theme.typography.small.copy(color = colors.textRegular),
                            )
                        }
                    }
                    ElButton(
                        onClick = {
                            if (current >= steps.lastIndex) {
                                onFinish?.invoke()
                                onClose()
                            } else {
                                onCurrentChange(current + 1)
                            }
                        },
                        type = ElType.Primary,
                        size = cn.enaium.elementplus.theme.ElSize.Small,
                        text = step.nextText ?: if (current >= steps.lastIndex) locale.tour.finish else locale.tour.next,
                    )
                }
            }
        }
    }
}

private fun Rect.inflate(amount: Float): Rect = Rect(left - amount, top - amount, right + amount, bottom + amount)
