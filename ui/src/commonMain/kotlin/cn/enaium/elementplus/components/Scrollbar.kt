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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * `<el-scrollbar>`: a scroll container with an overlay scrollbar.
 *
 * The container must be bounded on the scrolling axis: pass [height] (vertical)
 * or [width] (horizontal), or constrain the incoming [modifier].
 */
@Composable
fun ElScrollbar(
    modifier: Modifier = Modifier,
    height: Dp? = null,
    width: Dp? = null,
    horizontal: Boolean = false,
    always: Boolean = false,
    nativeScrollbar: Boolean = false,
    content: @Composable () -> Unit,
) {
    val state = rememberScrollState()
    val theme = LocalElTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered = cn.enaium.elementplus.internal.rememberElInteractionState(interaction).hovered
    var active by remember { mutableStateOf(false) }
    var dragging by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    LaunchedEffect(state.value, state.isScrollInProgress) {
        active = true
        delay(900)
        if (!state.isScrollInProgress && !dragging && !hovered) active = false
    }

    BoxWithConstraints(
        modifier = modifier
            .then(if (height != null) Modifier.height(height) else Modifier)
            .then(if (width != null) Modifier.width(width) else Modifier)
            .hoverable(interaction),
    ) {
        val viewportPx = if (horizontal) constraints.maxWidth.toFloat() else constraints.maxHeight.toFloat()
        val maxScroll = state.maxValue.toFloat()
        val thumbSizePx = if (maxScroll <= 0f) 0f else {
            (viewportPx * viewportPx / (viewportPx + maxScroll)).coerceAtLeast(with(density) { 20.dp.toPx() })
        }
        val trackPx = (viewportPx - thumbSizePx).coerceAtLeast(0f)
        val thumbOffsetPx = if (maxScroll <= 0f) 0f else (state.value / maxScroll) * trackPx
        val visible = thumbSizePx > 0f && (always || active || hovered || state.isScrollInProgress)

        if (horizontal) {
            Row(Modifier.fillMaxSize().horizontalScroll(state)) { content() }
        } else {
            Column(Modifier.fillMaxSize().verticalScroll(state)) { content() }
        }

        if (!nativeScrollbar && visible) {
            val thumbWidth = 6.dp
            val barModifier = if (horizontal) {
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(thumbWidth)
                    .padding(horizontal = 2.dp)
                    .pointerInput(maxScroll) {
                        detectTapGestures { tap ->
                            if (maxScroll <= 0f) return@detectTapGestures
                            val ratio = (tap.x / size.width.toFloat()).coerceIn(0f, 1f)
                            scope.launch { state.scrollTo((ratio * maxScroll).roundToInt()) }
                        }
                    }
            } else {
                Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(thumbWidth)
                    .padding(vertical = 2.dp)
                    .pointerInput(maxScroll) {
                        detectTapGestures { tap ->
                            if (maxScroll <= 0f) return@detectTapGestures
                            val ratio = (tap.y / size.height.toFloat()).coerceIn(0f, 1f)
                            scope.launch { state.scrollTo((ratio * maxScroll).roundToInt()) }
                        }
                    }
            }
            Box(barModifier) {
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
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(3.dp))
                        .background(if (hovered || dragging) theme.colors.textPlaceholder else theme.colors.textDisabled.copy(alpha = 0.5f))
                        .pointerInput(maxScroll, thumbSizePx, trackPx) {
                            detectDragGestures(
                                onDragStart = { dragging = true },
                                onDragEnd = { dragging = false },
                                onDragCancel = { dragging = false },
                            ) { change, dragAmount ->
                                change.consume()
                                if (maxScroll <= 0f || trackPx <= 0f) return@detectDragGestures
                                val delta = if (horizontal) dragAmount.x else dragAmount.y
                                val scrollDelta = delta / trackPx * maxScroll
                                scope.launch {
                                    state.scrollTo((state.value + scrollDelta).roundToInt().coerceIn(0, state.maxValue))
                                }
                            }
                        },
                )
            }
        }
    }
}
