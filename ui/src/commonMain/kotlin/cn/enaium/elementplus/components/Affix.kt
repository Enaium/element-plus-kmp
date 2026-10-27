package cn.enaium.elementplus.components

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.max
import kotlin.math.min

/** `position` of `<el-affix>`: pin to the top or to the bottom of the viewport. */
enum class ElAffixPosition { Top, Bottom }

/**
 * `<el-affix>` for a Compose scroll container. Pass the same [scrollState] used
 * by the enclosing `verticalScroll`; the content is translated to stay at
 * `offsetTop` (or `offsetBottom` when [position] is [ElAffixPosition.Bottom])
 * and reports whether it is currently pinned.
 *
 * Compose equivalents of the DOM-only props:
 * - `target` → [targetHeight]: height of the target container measured from the
 *   affix's natural layout position (equal to the container height when the
 *   affix sits at its top); the affix stops pinning at that container's edge
 *   instead of floating over the rest of the scroll content.
 * - viewport height is taken from [viewportHeight] when given, otherwise from
 *   the window size (the same source Element Plus uses).
 * - [onChange] is the `change` event, [onScroll] the `scroll` event.
 *
 * When [scrollState] is `null` the content is rendered without pinning, so a
 * caller never silently gets an unrelated scroll state.
 */
@Composable
fun ElAffix(
    modifier: Modifier = Modifier,
    offsetTop: Dp = 0.dp,
    scrollState: ScrollState? = null,
    zIndex: Float = 2f,
    position: ElAffixPosition = ElAffixPosition.Top,
    offsetBottom: Dp = 0.dp,
    viewportHeight: Dp? = null,
    targetHeight: Dp? = null,
    onChange: ((fixed: Boolean) -> Unit)? = null,
    onScroll: ((scrollTop: Int, fixed: Boolean) -> Unit)? = null,
    content: @Composable (affixed: Boolean) -> Unit,
) {
    val density = LocalDensity.current
    val offsetTopPx = with(density) { offsetTop.toPx() }
    val offsetBottomPx = with(density) { offsetBottom.toPx() }
    val targetHeightPx = targetHeight?.let { with(density) { it.toPx() } }
    // WindowInfo reports Int.MIN_VALUE when the platform does not expose a
    // window size; treat that as "unknown" and keep the content unpinned.
    val windowHeightPx = LocalWindowInfo.current.containerSize.height
    val resolvedViewportPx = viewportHeight?.let { with(density) { it.toPx() } }
        ?: if (windowHeightPx > 0) windowHeightPx.toFloat() else Float.POSITIVE_INFINITY

    var baseY by remember { mutableFloatStateOf(-1f) }
    var contentHeight by remember { mutableFloatStateOf(0f) }

    val scrollValue = scrollState?.value ?: 0
    val naturalTop = if (baseY < 0f) 0f else baseY - scrollValue.toFloat()
    val translation = if (baseY < 0f) {
        0f
    } else {
        when (position) {
            ElAffixPosition.Top -> {
                var value = max(0f, offsetTopPx - naturalTop)
                if (targetHeightPx != null) {
                    value = min(value, max(0f, targetHeightPx - contentHeight))
                }
                value
            }

            ElAffixPosition.Bottom -> {
                var value = min(0f, (resolvedViewportPx - offsetBottomPx - contentHeight) - naturalTop)
                if (targetHeightPx != null) {
                    value = max(value, min(0f, contentHeight - targetHeightPx))
                }
                value
            }
        }
    }
    // Element Plus drops the fixed state once the target container has left the
    // viewport, so the reported flag stops together with the pinning.
    val knownTop = if (baseY < 0f) 0f else baseY
    val targetStillVisible = targetHeightPx == null ||
        position == ElAffixPosition.Bottom ||
        knownTop + targetHeightPx - scrollValue.toFloat() > 0f
    val affixed = translation != 0f && targetStillVisible

    var reportedFixed by remember { mutableStateOf(affixed) }
    LaunchedEffect(affixed, position) {
        if (affixed != reportedFixed) {
            reportedFixed = affixed
            onChange?.invoke(affixed)
        }
    }
    var lastScroll by remember { mutableStateOf(scrollValue) }
    LaunchedEffect(scrollValue) {
        if (scrollValue != lastScroll) {
            lastScroll = scrollValue
            onScroll?.invoke(scrollValue, affixed)
        }
    }

    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .zIndex(zIndex)
            .onGloballyPositioned { coordinates ->
                // Layout position (not layers), so it is unaffected by the
                // translation and by scrolling of the enclosing container.
                baseY = coordinates.positionInParent().y
                contentHeight = coordinates.size.height.toFloat()
            }
            .graphicsLayer { translationY = translation },
    ) {
        content(affixed)
    }
}
