package cn.enaium.elementplus.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

/** Side of the target the guide card is attached to, mirroring `placement`. */
enum class ElTourPlacement {
    Top, TopStart, TopEnd,
    Bottom, BottomStart, BottomEnd,
    Left, LeftStart, LeftEnd,
    Right, RightStart, RightEnd,
}

/** Card theme, mirroring `type`: the primary variant paints the card with the theme color. */
enum class ElTourType { Default, Primary }

/** Transparent gap between mask and target plus the mask corner radius. */
class ElTourGap(
    val offset: Dp = 6.dp,
    val radius: Dp = 6.dp,
    val padding: Dp = 6.dp,
)

/** Mask configuration, mirroring the object form of the `mask` attribute. */
class ElTourMask(
    val visible: Boolean = true,
    val color: Color? = null,
)

class ElTourStep(
    val title: String,
    val description: String? = null,
    val targetBounds: Rect? = null,
    val nextText: String? = null,
    val prevText: String? = null,
    val content: (@Composable () -> Unit)? = null,
    val placement: ElTourPlacement? = null,
    val targetKey: String? = null,
    val showClose: Boolean? = null,
    val showArrow: Boolean? = null,
    val mask: Boolean? = null,
    val type: ElTourType? = null,
    val prevButtonType: ElType? = null,
    val nextButtonType: ElType? = null,
)

/**
 * Registry of tour targets. Attach [Modifier.elTourTarget] to any composable and
 * reference its key from [ElTourStep.targetKey] instead of reporting bounds by
 * hand; the tour follows the target while it is laid out.
 */
class ElTourTargetRegistry {
    private val targets = mutableStateMapOf<String, Rect>()

    fun update(key: String, bounds: Rect) {
        targets[key] = bounds
    }

    fun remove(key: String) {
        targets.remove(key)
    }

    fun bounds(key: String): Rect? = targets[key]

    fun clear() {
        targets.clear()
    }
}

/** Provides the registry consumed by [Modifier.elTourTarget] and [ElTour]. */
val LocalElTourTargets = staticCompositionLocalOf<ElTourTargetRegistry?> { null }

/** Marks this composable as a tour target for [key]. */
fun Modifier.elTourTarget(key: String): Modifier = composed {
    val registry = LocalElTourTargets.current
    DisposableEffect(key, registry) {
        onDispose { registry?.remove(key) }
    }
    onGloballyPositioned { coordinates ->
        val topLeft = coordinates.positionOnScreen()
        registry?.update(
            key,
            Rect(
                left = topLeft.x,
                top = topLeft.y,
                right = topLeft.x + coordinates.size.width,
                bottom = topLeft.y + coordinates.size.height,
            ),
        )
    }
}

/**
 * `<el-tour>` with masks, spotlight targeting, placement, progress and
 * previous/next navigation. Targets are supplied either as screen-coordinate
 * [Rect]s (for example from `onGloballyPositioned { it.positionOnScreen() }`)
 * or, preferably, through [ElTourStep.targetKey] and [Modifier.elTourTarget],
 * which keeps the card aligned even though the tour renders in its own window.
 *
 * Set [modal] to `false` to render the overlay inside the caller's layout
 * instead of a dialog: the mask then never blocks input, which is the Compose
 * equivalent of `target-area-clickable`. When embedding it this way, size the
 * overlay with `Modifier.matchParentSize()` inside a `Box`.
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
    mask: ElTourMask? = null,
    gap: ElTourGap? = null,
    placement: ElTourPlacement = ElTourPlacement.Bottom,
    showClose: Boolean = true,
    closeIcon: ImageVector? = null,
    showArrow: Boolean = true,
    type: ElTourType = ElTourType.Default,
    modal: Boolean = true,
    closeOnPressEscape: Boolean = true,
    indicators: (@Composable (current: Int, total: Int) -> Unit)? = null,
) {
    if (!visible || steps.isEmpty()) return
    val safeCurrent = current.coerceIn(0, steps.lastIndex)
    val step = steps[safeCurrent]
    val registry = LocalElTourTargets.current
    val target = step.targetBounds ?: step.targetKey?.let { registry?.bounds(it) }
    val overlay: @Composable (Modifier) -> Unit = { overlayModifier ->
        TourOverlay(
            modifier = overlayModifier,
            step = step,
            current = safeCurrent,
            total = steps.size,
            target = target,
            showMask = showMask,
            targetPadding = targetPadding,
            width = width,
            mask = mask,
            gap = gap,
            placement = placement,
            showClose = showClose,
            closeIcon = closeIcon,
            showArrow = showArrow,
            type = type,
            indicators = indicators,
            onClose = onClose,
            onCurrentChange = onCurrentChange,
            onFinish = onFinish,
        )
    }
    if (modal) {
        Dialog(
            onDismissRequest = onClose,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = closeOnPressEscape,
            ),
        ) {
            Box(Modifier.fillMaxSize()) { overlay(Modifier.fillMaxSize()) }
        }
    } else {
        overlay(modifier)
    }
}

@Composable
private fun TourOverlay(
    modifier: Modifier,
    step: ElTourStep,
    current: Int,
    total: Int,
    target: Rect?,
    showMask: Boolean,
    targetPadding: Dp,
    width: Dp,
    mask: ElTourMask?,
    gap: ElTourGap?,
    placement: ElTourPlacement,
    showClose: Boolean,
    closeIcon: ImageVector?,
    showArrow: Boolean,
    type: ElTourType,
    indicators: (@Composable (current: Int, total: Int) -> Unit)?,
    onClose: () -> Unit,
    onCurrentChange: (Int) -> Unit,
    onFinish: (() -> Unit)?,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val density = LocalDensity.current
    var overlayOrigin by remember { mutableStateOf(Offset.Zero) }
    var cardSize by remember { mutableStateOf(IntSize.Zero) }
    val paddingPx = with(density) { (gap?.padding ?: targetPadding).toPx() }
    val gapPx = with(density) { (gap?.offset ?: 12.dp).toPx() }
    val cardWidthPx = with(density) { width.toPx() }
    val estimatedCardHeightPx = with(density) { 190.dp.toPx() }
    val cornerPx = with(density) { (gap?.radius ?: 6.dp).toPx() }
    val maskVisible = step.mask ?: mask?.visible ?: showMask
    val cardType = step.type ?: type
    val isPrimary = cardType == ElTourType.Primary
    val resolvedPlacement = step.placement ?: placement
    val showArrowResolved = step.showArrow ?: showArrow
    val showCloseResolved = step.showClose ?: showClose
    val cardBackground = if (isPrimary) colors.primary else colors.background
    val titleColor = if (isPrimary) Color.White else colors.textPrimary
    val descriptionColor = if (isPrimary) Color.White.copy(alpha = 0.9f) else colors.textRegular
    val metaColor = if (isPrimary) Color.White.copy(alpha = 0.8f) else colors.textSecondary
    BoxWithConstraints(
        modifier.onGloballyPositioned { overlayOrigin = it.positionOnScreen() },
    ) {
        val containerWidth = if (constraints.hasBoundedWidth) {
            constraints.maxWidth.toFloat()
        } else {
            cardWidthPx + with(density) { 24.dp.toPx() }
        }
        val containerHeight = if (constraints.hasBoundedHeight) {
            constraints.maxHeight.toFloat()
        } else {
            estimatedCardHeightPx + with(density) { 24.dp.toPx() }
        }
        val targetLocal = target?.let { windowRect ->
            Rect(
                windowRect.left - overlayOrigin.x,
                windowRect.top - overlayOrigin.y,
                windowRect.right - overlayOrigin.x,
                windowRect.bottom - overlayOrigin.y,
            )
        }?.inflate(paddingPx)
        if (maskVisible) {
            Canvas(Modifier.fillMaxSize()) {
                val path = Path().apply {
                    fillType = PathFillType.EvenOdd
                    addRect(Rect(0f, 0f, size.width, size.height))
                    if (targetLocal != null) {
                        addRoundRect(RoundRect(rect = targetLocal, cornerRadius = CornerRadius(cornerPx)))
                    }
                }
                drawPath(path, mask?.color ?: Color.Black.copy(alpha = 0.55f))
                if (targetLocal != null) {
                    drawRoundRect(
                        color = colors.primary,
                        topLeft = Offset(targetLocal.left, targetLocal.top),
                        size = androidx.compose.ui.geometry.Size(targetLocal.width, targetLocal.height),
                        cornerRadius = CornerRadius(cornerPx),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()),
                    )
                }
            }
        }
        val cardWidth = if (cardSize.width > 0) cardSize.width.toFloat() else cardWidthPx
        val cardHeight = if (cardSize.height > 0) cardSize.height.toFloat() else estimatedCardHeightPx
        val margin = with(density) { 12.dp.toPx() }
        var x = 0f
        var y = 0f
        if (targetLocal == null) {
            x = (containerWidth - cardWidth) / 2f
            y = (containerHeight - cardHeight) / 2f
        } else {
            when (resolvedPlacement) {
                ElTourPlacement.Bottom, ElTourPlacement.BottomStart, ElTourPlacement.BottomEnd -> {
                    y = targetLocal.bottom + gapPx
                    x = when (resolvedPlacement) {
                        ElTourPlacement.BottomStart -> targetLocal.left
                        ElTourPlacement.BottomEnd -> targetLocal.right - cardWidth
                        else -> targetLocal.center.x - cardWidth / 2f
                    }
                    if (y + cardHeight > containerHeight - margin) y = targetLocal.top - cardHeight - gapPx
                }
                ElTourPlacement.Top, ElTourPlacement.TopStart, ElTourPlacement.TopEnd -> {
                    y = targetLocal.top - cardHeight - gapPx
                    x = when (resolvedPlacement) {
                        ElTourPlacement.TopStart -> targetLocal.left
                        ElTourPlacement.TopEnd -> targetLocal.right - cardWidth
                        else -> targetLocal.center.x - cardWidth / 2f
                    }
                    if (y < margin) y = targetLocal.bottom + gapPx
                }
                ElTourPlacement.Left, ElTourPlacement.LeftStart, ElTourPlacement.LeftEnd -> {
                    x = targetLocal.left - cardWidth - gapPx
                    y = when (resolvedPlacement) {
                        ElTourPlacement.LeftStart -> targetLocal.top
                        ElTourPlacement.LeftEnd -> targetLocal.bottom - cardHeight
                        else -> targetLocal.center.y - cardHeight / 2f
                    }
                    if (x < margin) x = targetLocal.right + gapPx
                }
                ElTourPlacement.Right, ElTourPlacement.RightStart, ElTourPlacement.RightEnd -> {
                    x = targetLocal.right + gapPx
                    y = when (resolvedPlacement) {
                        ElTourPlacement.RightStart -> targetLocal.top
                        ElTourPlacement.RightEnd -> targetLocal.bottom - cardHeight
                        else -> targetLocal.center.y - cardHeight / 2f
                    }
                    if (x + cardWidth > containerWidth - margin) x = targetLocal.left - cardWidth - gapPx
                }
            }
        }
        x = x.coerceIn(margin, (containerWidth - cardWidth - margin).coerceAtLeast(margin))
        y = y.coerceIn(margin, (containerHeight - cardHeight - margin).coerceAtLeast(margin))
        val side = when (resolvedPlacement) {
            ElTourPlacement.Bottom, ElTourPlacement.BottomStart, ElTourPlacement.BottomEnd -> ArrowSide.Top
            ElTourPlacement.Top, ElTourPlacement.TopStart, ElTourPlacement.TopEnd -> ArrowSide.Bottom
            ElTourPlacement.Left, ElTourPlacement.LeftStart, ElTourPlacement.LeftEnd -> ArrowSide.Right
            ElTourPlacement.Right, ElTourPlacement.RightStart, ElTourPlacement.RightEnd -> ArrowSide.Left
        }
        val arrowPoint = targetLocal?.let { Offset(it.center.x - x, it.center.y - y) }
        Column(
            Modifier
                .offset(x = with(density) { x.toDp() }, y = with(density) { y.toDp() })
                .width(width)
                .onGloballyPositioned { cardSize = it.size }
                .drawBehind {
                    if (showArrowResolved && arrowPoint != null) {
                        val half = 7.dp.toPx()
                        val edge = 10.dp.toPx()
                        val path = Path()
                        when (side) {
                            ArrowSide.Top -> {
                                val ax = arrowPoint.x.clampTo(half + edge, size.width - half - edge)
                                path.moveTo(ax - half, 0f)
                                path.lineTo(ax + half, 0f)
                                path.lineTo(ax, -half)
                            }
                            ArrowSide.Bottom -> {
                                val ax = arrowPoint.x.clampTo(half + edge, size.width - half - edge)
                                path.moveTo(ax - half, size.height)
                                path.lineTo(ax + half, size.height)
                                path.lineTo(ax, size.height + half)
                            }
                            ArrowSide.Left -> {
                                val ay = arrowPoint.y.clampTo(half + edge, size.height - half - edge)
                                path.moveTo(0f, ay - half)
                                path.lineTo(0f, ay + half)
                                path.lineTo(-half, ay)
                            }
                            ArrowSide.Right -> {
                                val ay = arrowPoint.y.clampTo(half + edge, size.height - half - edge)
                                path.moveTo(size.width, ay - half)
                                path.lineTo(size.width, ay + half)
                                path.lineTo(size.width + half, ay)
                            }
                        }
                        path.close()
                        drawPath(path, cardBackground)
                    }
                }
                .shadow(6.dp, RoundedCornerShape(8.dp))
                .background(cardBackground, RoundedCornerShape(8.dp))
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BasicText(
                    text = step.title,
                    modifier = Modifier.weight(1f),
                    style = theme.typography.medium.copy(color = titleColor, fontWeight = FontWeight.Medium),
                )
                if (showCloseResolved) {
                    Box(
                        Modifier
                            .size(20.dp)
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.Center,
                    ) {
                        ElIcon(
                            closeIcon ?: ElIcons.Close,
                            size = 14.dp,
                            tint = if (isPrimary) Color.White.copy(alpha = 0.85f) else colors.textPlaceholder,
                            contentDescription = locale.tour.close,
                        )
                    }
                }
            }
            if (step.description != null) {
                Spacer(Modifier.height(8.dp))
                BasicText(
                    text = step.description,
                    style = theme.typography.base.copy(color = descriptionColor),
                )
            }
            step.content?.let {
                Spacer(Modifier.height(8.dp))
                it()
            }
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (indicators != null) {
                    indicators(current, total)
                } else {
                    BasicText(
                        text = "${current + 1}/$total",
                        style = theme.typography.small.copy(color = metaColor),
                    )
                }
                Spacer(Modifier.weight(1f))
                if (current > 0) {
                    ElButton(
                        onClick = { onCurrentChange(current - 1) },
                        text = step.prevText ?: locale.tour.previous,
                        type = step.prevButtonType ?: ElType.Default,
                        size = cn.enaium.elementplus.theme.ElSize.Small,
                        textButton = !isPrimary,
                        plain = isPrimary,
                    )
                }
                Spacer(Modifier.width(8.dp))
                ElButton(
                    onClick = {
                        if (current >= total - 1) {
                            onFinish?.invoke()
                            onClose()
                        } else {
                            onCurrentChange(current + 1)
                        }
                    },
                    type = step.nextButtonType ?: ElType.Primary,
                    size = cn.enaium.elementplus.theme.ElSize.Small,
                    plain = isPrimary,
                    text = step.nextText ?: if (current >= total - 1) locale.tour.finish else locale.tour.next,
                )
            }
        }
    }
}

private enum class ArrowSide { Top, Bottom, Left, Right }

private fun Float.clampTo(lo: Float, hi: Float): Float = if (hi <= lo) (lo + hi) / 2f else coerceIn(lo, hi)

private fun Rect.inflate(amount: Float): Rect = Rect(left - amount, top - amount, right + amount, bottom + amount)
