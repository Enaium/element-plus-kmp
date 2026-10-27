package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Scrollbar direction reported by [ElScrollbar]'s `onEndReached`. */
enum class ElScrollbarDirection { Top, Bottom, Left, Right }

/** Edge masks drawn over a scrollable [ElScrollbar], like the `mask` prop. */
enum class ElScrollbarMask { Top, Bottom, Left, Right }

/**
 * State handle of [ElScrollbar], equivalent to the instance exposes
 * `setScrollTop`, `setScrollLeft` and `update` of the original component.
 */
@Stable
class ElScrollbarState internal constructor() {
    internal var verticalState: androidx.compose.foundation.ScrollState? = null
    internal var horizontalState: androidx.compose.foundation.ScrollState? = null
    internal var scope: CoroutineScope? = null
    internal var version by mutableIntStateOf(0)

    /** Current vertical scroll offset in pixels. */
    val scrollTop: Float get() = verticalState?.value?.toFloat() ?: 0f

    /** Current horizontal scroll offset in pixels. */
    val scrollLeft: Float get() = horizontalState?.value?.toFloat() ?: 0f

    val maxScrollTop: Float get() = verticalState?.maxValue?.toFloat() ?: 0f
    val maxScrollLeft: Float get() = horizontalState?.maxValue?.toFloat() ?: 0f

    /** Scrolls the content to [value] pixels, mirroring `setScrollTop`. */
    fun setScrollTop(value: Float) {
        val target = value.roundToInt().coerceAtLeast(0)
        val state = verticalState ?: return
        val scope = scope ?: return
        scope.launch { state.scrollTo(target) }
    }

    /** Scrolls the content to [value] pixels, mirroring `setScrollLeft`. */
    fun setScrollLeft(value: Float) {
        val target = value.roundToInt().coerceAtLeast(0)
        val state = horizontalState ?: return
        val scope = scope ?: return
        scope.launch { state.scrollTo(target) }
    }

    /** Animates the scroll offsets; `null` keeps the current offset. */
    suspend fun animateScrollTo(top: Float? = null, left: Float? = null) {
        if (top != null) verticalState?.animateScrollTo(top.roundToInt().coerceAtLeast(0))
        if (left != null) horizontalState?.animateScrollTo(left.roundToInt().coerceAtLeast(0))
    }

    /**
     * Recomputes the thumb geometry. Compose tracks the scroll state
     * automatically, so this only forces an immediate re-measure.
     */
    fun update() {
        version++
    }
}

@Composable
fun rememberElScrollbarState(): ElScrollbarState {
    val scope = rememberCoroutineScope()
    val state = remember { ElScrollbarState() }
    state.scope = scope
    return state
}

/**
 * `<el-scrollbar>`: a scroll container with an overlay scrollbar.
 *
 * The container must be bounded on the scrolling axis: pass [height] (vertical)
 * or [width] (horizontal), [maxHeight] to grow with the content up to a limit,
 * or constrain the incoming [modifier]. [bothAxis] enables scrolling on both
 * axes at once; [onEndReached] reports when an edge is reached within
 * [distance]; [mask] fades the scrollable edges. [minSize] is the minimum thumb
 * length as a percentage of the viewport (`20` = 20%), mirroring `min-size`.
 */
@Composable
fun ElScrollbar(
    modifier: Modifier = Modifier,
    height: Dp? = null,
    width: Dp? = null,
    maxHeight: Dp? = null,
    horizontal: Boolean = false,
    bothAxis: Boolean = false,
    always: Boolean = false,
    nativeScrollbar: Boolean = false,
    minSize: Int = 20,
    distance: Dp = 0.dp,
    mask: Set<ElScrollbarMask> = emptySet(),
    state: ElScrollbarState = rememberElScrollbarState(),
    onScroll: ((scrollTop: Float, scrollLeft: Float) -> Unit)? = null,
    onEndReached: ((ElScrollbarDirection) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val verticalAxis = bothAxis || !horizontal
    val horizontalAxis = bothAxis || horizontal
    val verticalState = rememberScrollState()
    val horizontalState = rememberScrollState()
    state.verticalState = if (verticalAxis) verticalState else null
    state.horizontalState = if (horizontalAxis) horizontalState else null
    val revision = state.version

    val theme = LocalElTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered = cn.enaium.elementplus.internal.rememberElInteractionState(interaction).hovered
    var active by remember { mutableStateOf(false) }
    var dragging by remember { mutableStateOf(false) }
    var reachedTop by remember { mutableStateOf(true) }
    var reachedBottom by remember { mutableStateOf(false) }
    var reachedLeft by remember { mutableStateOf(true) }
    var reachedRight by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val distancePx = with(density) { distance.toPx() }
    val scrolling = verticalState.isScrollInProgress || horizontalState.isScrollInProgress

    LaunchedEffect(scrolling, verticalState.value, horizontalState.value) {
        active = true
        delay(900)
        if (!scrolling && !dragging && !hovered) active = false
    }

    LaunchedEffect(verticalState.value, horizontalState.value) {
        onScroll?.invoke(verticalState.value.toFloat(), horizontalState.value.toFloat())
    }

    LaunchedEffect(verticalState.value, verticalState.maxValue) {
        if (verticalState.maxValue <= 0) {
            reachedTop = true
            reachedBottom = false
            return@LaunchedEffect
        }
        val atBottom = verticalState.value >= verticalState.maxValue - distancePx
        if (atBottom && !reachedBottom) onEndReached?.invoke(ElScrollbarDirection.Bottom)
        reachedBottom = atBottom
        val atTop = verticalState.value <= distancePx
        if (atTop && !reachedTop) onEndReached?.invoke(ElScrollbarDirection.Top)
        reachedTop = atTop
    }

    LaunchedEffect(horizontalState.value, horizontalState.maxValue) {
        if (horizontalState.maxValue <= 0) {
            reachedLeft = true
            reachedRight = false
            return@LaunchedEffect
        }
        val atRight = horizontalState.value >= horizontalState.maxValue - distancePx
        if (atRight && !reachedRight) onEndReached?.invoke(ElScrollbarDirection.Right)
        reachedRight = atRight
        val atLeft = horizontalState.value <= distancePx
        if (atLeft && !reachedLeft) onEndReached?.invoke(ElScrollbarDirection.Left)
        reachedLeft = atLeft
    }

    BoxWithConstraints(
        modifier = modifier
            .then(
                when {
                    height != null -> Modifier.height(height)
                    maxHeight != null -> Modifier.heightIn(max = maxHeight)
                    else -> Modifier
                },
            )
            .then(if (width != null) Modifier.width(width) else Modifier)
            .hoverable(interaction),
    ) {
        val viewportHeightPx = if (constraints.maxHeight != Constraints.Infinity) constraints.maxHeight.toFloat() else 0f
        val viewportWidthPx = if (constraints.maxWidth != Constraints.Infinity) constraints.maxWidth.toFloat() else 0f
        val fillViewport = height != null || maxHeight == null

        when {
            verticalAxis && horizontalAxis -> Box(
                (if (fillViewport) Modifier.fillMaxSize() else Modifier.fillMaxWidth())
                    .verticalScroll(verticalState)
                    .horizontalScroll(horizontalState),
            ) { content() }

            horizontalAxis -> Row(
                (if (fillViewport) Modifier.fillMaxSize() else Modifier.fillMaxWidth())
                    .horizontalScroll(horizontalState),
            ) { content() }

            else -> Column(
                (if (fillViewport) Modifier.fillMaxSize() else Modifier.fillMaxWidth())
                    .verticalScroll(verticalState),
            ) { content() }
        }

        val thumbColor = if (hovered || dragging) {
            theme.colors.textPlaceholder
        } else {
            theme.colors.textDisabled.copy(alpha = 0.5f)
        }
        val visible = always || active || hovered || dragging || scrolling

        if (verticalAxis) {
            val vMax = verticalState.maxValue
            val minThumbPx = viewportHeightPx * (minSize / 100f)
            val thumbSizePx = if (vMax <= 0 || viewportHeightPx <= 0f) {
                0f
            } else {
                (viewportHeightPx * viewportHeightPx / (viewportHeightPx + vMax))
                    .coerceAtLeast(minThumbPx)
            }
            val trackPx = (viewportHeightPx - thumbSizePx).coerceAtLeast(0f)
            val thumbOffsetPx = if (vMax <= 0) 0f else (verticalState.value.toFloat() / vMax) * trackPx
            if (mask.any { it == ElScrollbarMask.Top || it == ElScrollbarMask.Bottom }) {
                if (ElScrollbarMask.Top in mask && verticalState.value > 0) {
                    ScrollbarMaskEdge(ElScrollbarMask.Top, theme.colors.background)
                }
                if (ElScrollbarMask.Bottom in mask && vMax > 0 && verticalState.value < vMax) {
                    ScrollbarMaskEdge(ElScrollbarMask.Bottom, theme.colors.background)
                }
            }
            if (!nativeScrollbar && visible && thumbSizePx > 0f) {
                ScrollbarThumb(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(6.dp)
                        .padding(vertical = 2.dp),
                    horizontal = false,
                    thumbSizePx = thumbSizePx,
                    thumbOffsetPx = thumbOffsetPx,
                    trackPx = trackPx,
                    maxScroll = vMax,
                    thumbColor = thumbColor,
                    onScrollTo = { target -> scope.launch { verticalState.scrollTo(target) } },
                    onDrag = { delta ->
                        scope.launch {
                            verticalState.scrollTo(
                                (verticalState.value + delta).roundToInt().coerceIn(0, verticalState.maxValue),
                            )
                        }
                    },
                    onDragStateChange = { dragging = it },
                    revision = revision,
                )
            }
        }

        if (horizontalAxis) {
            val hMax = horizontalState.maxValue
            val minThumbPx = viewportWidthPx * (minSize / 100f)
            val thumbSizePx = if (hMax <= 0 || viewportWidthPx <= 0f) {
                0f
            } else {
                (viewportWidthPx * viewportWidthPx / (viewportWidthPx + hMax)).coerceAtLeast(minThumbPx)
            }
            val trackPx = (viewportWidthPx - thumbSizePx).coerceAtLeast(0f)
            val thumbOffsetPx = if (hMax <= 0) 0f else (horizontalState.value.toFloat() / hMax) * trackPx
            if (mask.any { it == ElScrollbarMask.Left || it == ElScrollbarMask.Right }) {
                if (ElScrollbarMask.Left in mask && horizontalState.value > 0) {
                    ScrollbarMaskEdge(ElScrollbarMask.Left, theme.colors.background)
                }
                if (ElScrollbarMask.Right in mask && hMax > 0 && horizontalState.value < hMax) {
                    ScrollbarMaskEdge(ElScrollbarMask.Right, theme.colors.background)
                }
            }
            if (!nativeScrollbar && visible && thumbSizePx > 0f) {
                ScrollbarThumb(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(6.dp)
                        .padding(horizontal = 2.dp),
                    horizontal = true,
                    thumbSizePx = thumbSizePx,
                    thumbOffsetPx = thumbOffsetPx,
                    trackPx = trackPx,
                    maxScroll = hMax,
                    thumbColor = thumbColor,
                    onScrollTo = { target -> scope.launch { horizontalState.scrollTo(target) } },
                    onDrag = { delta ->
                        scope.launch {
                            horizontalState.scrollTo(
                                (horizontalState.value + delta).roundToInt().coerceIn(0, horizontalState.maxValue),
                            )
                        }
                    },
                    onDragStateChange = { dragging = it },
                    revision = revision,
                )
            }
        }
    }
}

@Composable
private fun BoxScope.ScrollbarMaskEdge(
    edge: ElScrollbarMask,
    background: Color,
    size: Dp = 40.dp,
) {
    val modifier = when (edge) {
        ElScrollbarMask.Top -> Modifier.align(Alignment.TopCenter).fillMaxWidth().height(size)
        ElScrollbarMask.Bottom -> Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(size)
        ElScrollbarMask.Left -> Modifier.align(Alignment.CenterStart).fillMaxHeight().width(size)
        ElScrollbarMask.Right -> Modifier.align(Alignment.CenterEnd).fillMaxHeight().width(size)
    }
    val brush = when (edge) {
        ElScrollbarMask.Top -> Brush.verticalGradient(listOf(background, background.copy(alpha = 0f)))
        ElScrollbarMask.Bottom -> Brush.verticalGradient(listOf(background.copy(alpha = 0f), background))
        ElScrollbarMask.Left -> Brush.horizontalGradient(listOf(background, background.copy(alpha = 0f)))
        ElScrollbarMask.Right -> Brush.horizontalGradient(listOf(background.copy(alpha = 0f), background))
    }
    Box(modifier.background(brush))
}

@Composable
private fun ScrollbarThumb(
    modifier: Modifier,
    horizontal: Boolean,
    thumbSizePx: Float,
    thumbOffsetPx: Float,
    trackPx: Float,
    maxScroll: Int,
    thumbColor: Color,
    onScrollTo: (Int) -> Unit,
    onDrag: (Float) -> Unit,
    onDragStateChange: (Boolean) -> Unit,
    revision: Int,
) {
    val density = LocalDensity.current
    Box(
        modifier.pointerInput(maxScroll, horizontal, revision) {
            detectTapGestures { tap ->
                if (maxScroll <= 0) return@detectTapGestures
                val extent = if (horizontal) size.width.toFloat() else size.height.toFloat()
                if (extent <= 0f) return@detectTapGestures
                val ratio = ((if (horizontal) tap.x else tap.y) / extent).coerceIn(0f, 1f)
                onScrollTo((ratio * maxScroll).roundToInt())
            }
        },
    ) {
        val thumbModifier = if (horizontal) {
            Modifier
                .offset(x = with(density) { thumbOffsetPx.toDp() })
                .width(with(density) { thumbSizePx.toDp() })
                .fillMaxHeight()
        } else {
            Modifier
                .offset(y = with(density) { thumbOffsetPx.toDp() })
                .height(with(density) { thumbSizePx.toDp() })
                .fillMaxWidth()
        }
        Box(
            thumbModifier
                .clip(RoundedCornerShape(3.dp))
                .background(thumbColor)
                .pointerInput(maxScroll, thumbSizePx, trackPx) {
                    detectDragGestures(
                        onDragStart = { onDragStateChange(true) },
                        onDragEnd = { onDragStateChange(false) },
                        onDragCancel = { onDragStateChange(false) },
                    ) { change, dragAmount ->
                        change.consume()
                        if (maxScroll <= 0 || trackPx <= 0f) return@detectDragGestures
                        val delta = if (horizontal) dragAmount.x else dragAmount.y
                        onDrag(delta / trackPx * maxScroll)
                    }
                },
        )
    }
}
