package cn.enaium.elementplus.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.launch

/** `direction` of `<el-anchor>`: a stacked list or an inline row. */
enum class ElAnchorDirection { Vertical, Horizontal }

/** `type` of `<el-anchor>`: the default marker bar or an underline marker. */
enum class ElAnchorType { Default, Underline }

data class ElAnchorLink(
    val href: String,
    val title: String = "",
    val children: List<ElAnchorLink> = emptyList(),
)

/**
 * Compose equivalent of the `scrollTo(href)` exposed by `<el-anchor>`: create
 * with [rememberElAnchorState], pass to [ElAnchor] and call [scrollTo].
 */
class ElAnchorState {
    internal var scrollToHandler: ((href: String) -> Unit)? = null

    /** Scrolls the anchor container to [href] and highlights it. */
    fun scrollTo(href: String) {
        scrollToHandler?.invoke(href)
    }
}

@Composable
fun rememberElAnchorState(): ElAnchorState = remember { ElAnchorState() }

/**
 * `<el-anchor>` with nested links, scroll-spy, animated marker, horizontal and
 * underline modes. [sectionOffsets] are pixel offsets of the sections inside
 * [scrollState]; clicks scroll to them, clicks on already visible sections and
 * scrolling update [activeHref] through [onActiveChange].
 *
 * @param offsetTop distance kept above a section when scrolling to it (the
 * `offset` prop) and when computing the active section.
 * @param bound extra distance below [offsetTop] before a section becomes active
 * (the `bound` prop, `15dp` by default).
 * @param duration scroll animation duration in milliseconds (the `duration`
 * prop).
 * @param selectScrollTop selects the first link when the container is at the
 * very top (the `select-scroll-top` prop).
 * @param width fixed width of the anchor; defaults to `200dp` for vertical
 * anchors and to wrap-content for horizontal anchors. Pass [Dp.Unspecified] to
 * keep the width from [modifier] instead.
 * @param state programmatic [ElAnchorState] for the `scrollTo` expose.
 * @param linkContent replaces the default link label (the `default` slot of
 * `el-anchor-link`); nested links still render below vertical links unless the
 * slot renders them itself.
 */
@Composable
fun ElAnchor(
    links: List<ElAnchorLink>,
    activeHref: String,
    onActiveChange: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    scrollState: ScrollState? = null,
    sectionOffsets: Map<String, Int> = emptyMap(),
    offsetTop: Dp = 0.dp,
    onLinkClick: ((String) -> Unit)? = null,
    direction: ElAnchorDirection = ElAnchorDirection.Vertical,
    type: ElAnchorType = ElAnchorType.Default,
    marker: Boolean = true,
    bound: Dp = 15.dp,
    duration: Int = 300,
    selectScrollTop: Boolean = false,
    width: Dp? = null,
    state: ElAnchorState? = null,
    linkContent: (@Composable (link: ElAnchorLink, active: Boolean) -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val offsetTopPx = with(density) { offsetTop.roundToPx() }
    val boundPx = with(density) { bound.roundToPx() }
    val horizontal = direction == ElAnchorDirection.Horizontal

    val linkBounds = remember { mutableStateMapOf<String, Rect>() }
    var rootPosition by remember { mutableStateOf(Offset.Zero) }
    var rootSize by remember { mutableStateOf(IntSize.Zero) }

    // Element Plus keeps the highlighted link while a smooth scroll is running;
    // the flag suppresses scroll-spy updates until the animation has finished.
    var suppressSpy by remember { mutableStateOf(false) }

    LaunchedEffect(
        scrollState?.value,
        sectionOffsets,
        offsetTopPx,
        boundPx,
        selectScrollTop,
        activeHref,
        suppressSpy,
    ) {
        val scroll = scrollState ?: return@LaunchedEffect
        if (sectionOffsets.isEmpty() || onActiveChange == null || suppressSpy) return@LaunchedEffect
        val scrollTop = scroll.value
        val ordered = sectionOffsets.entries.sortedBy { it.value }
        val href = if (scrollTop == 0) {
            if (selectScrollTop) ordered.firstOrNull()?.key else null
        } else {
            var current: String? = null
            ordered.forEachIndexed { index, entry ->
                val top = entry.value - offsetTopPx - boundPx
                val nextTop = ordered.getOrNull(index + 1)?.let { it.value - offsetTopPx - boundPx }
                if (top <= scrollTop && (nextTop == null || nextTop > scrollTop)) current = entry.key
            }
            current
        }
        if (href != null && href != activeHref) onActiveChange(href)
    }

    val scrollTo: (String) -> Unit = { href ->
        onActiveChange?.invoke(href)
        val target = sectionOffsets[href]
        if (scrollState != null && target != null) {
            scope.launch {
                suppressSpy = true
                try {
                    scrollState.animateScrollTo(
                        value = (target - offsetTopPx).coerceAtLeast(0),
                        animationSpec = tween(durationMillis = duration.coerceAtLeast(0)),
                    )
                } finally {
                    suppressSpy = false
                }
            }
        }
    }
    val currentScrollTo by rememberUpdatedState(scrollTo)
    DisposableEffect(state) {
        state?.scrollToHandler = { href -> currentScrollTo(href) }
        onDispose { state?.scrollToHandler = null }
    }

    val activate: (String) -> Unit = { href ->
        onLinkClick?.invoke(href)
        scrollTo(href)
    }

    val activeBounds = linkBounds[activeHref]
    val markerBaseWidth = if (type == ElAnchorType.Underline) 2.dp else 4.dp
    val markerHeight = if (horizontal) 2.dp else 14.dp
    val markerWidthTarget = if (horizontal && activeBounds != null) {
        with(density) { activeBounds.width.toDp() }
    } else {
        markerBaseWidth
    }
    val markerXTarget = if (horizontal && activeBounds != null) {
        with(density) { (activeBounds.left - rootPosition.x).toDp() }
    } else {
        0.dp
    }
    val markerYTarget = when {
        horizontal -> with(density) { rootSize.height.toDp() } - markerHeight
        activeBounds != null ->
            with(density) { (activeBounds.center.y - rootPosition.y).toDp() } - markerHeight / 2f
        else -> 0.dp
    }
    val animatedX by animateDpAsState(markerXTarget, tween(250), label = "el-anchor-marker-x")
    val animatedY by animateDpAsState(markerYTarget, tween(250), label = "el-anchor-marker-y")
    val animatedWidth by animateDpAsState(markerWidthTarget, tween(250), label = "el-anchor-marker-width")
    val animatedAlpha by animateFloatAsState(
        targetValue = if (activeBounds != null) 1f else 0f,
        animationSpec = tween(250),
        label = "el-anchor-marker-alpha",
    )

    val resolvedWidth: Dp? = if (width == null) {
        if (horizontal) null else 200.dp
    } else if (width == Dp.Unspecified) {
        null
    } else {
        width
    }
    val guideline = if (type == ElAnchorType.Underline) {
        Modifier.drawBehind {
            val thickness = 2.dp.toPx()
            if (horizontal) {
                drawRect(
                    color = colors.borderLighter,
                    topLeft = Offset(0f, size.height - thickness),
                    size = Size(size.width, thickness),
                )
            } else {
                drawRect(
                    color = colors.borderLighter,
                    size = Size(thickness, size.height),
                )
            }
        }
    } else {
        Modifier
    }
    Box(
        modifier
            .then(if (resolvedWidth != null) Modifier.width(resolvedWidth) else Modifier)
            .background(colors.background)
            .then(guideline)
            .onGloballyPositioned {
                rootPosition = it.positionInRoot()
                rootSize = it.size
            },
    ) {
        if (marker) {
            Box(
                Modifier
                    .offset { IntOffset(animatedX.roundToPx(), animatedY.roundToPx()) }
                    .size(animatedWidth, markerHeight)
                    .graphicsLayer { alpha = animatedAlpha }
                    .background(
                        color = colors.primary,
                        shape = if (horizontal || type == ElAnchorType.Underline) {
                            RectangleShape
                        } else {
                            RoundedCornerShape(4.dp)
                        },
                    ),
            )
        }
        if (horizontal) {
            Row(
                Modifier.padding(top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                links.forEachIndexed { index, link ->
                    AnchorHorizontalLink(
                        link = link,
                        isFirst = index == 0,
                        activeHref = activeHref,
                        linkContent = linkContent,
                        onBounds = { href, bounds ->
                            if (linkBounds[href] != bounds) linkBounds[href] = bounds
                        },
                        onActivate = activate,
                    )
                }
            }
        } else {
            Column(Modifier.padding(vertical = 8.dp)) {
                links.forEach { link ->
                    AnchorVerticalLink(
                        link = link,
                        depth = 0,
                        activeHref = activeHref,
                        linkContent = linkContent,
                        onBounds = { href, bounds ->
                            if (linkBounds[href] != bounds) linkBounds[href] = bounds
                        },
                        onActivate = activate,
                    )
                }
            }
        }
    }
}

private fun ElAnchorLink.contains(href: String): Boolean =
    this.href == href || children.any { it.contains(href) }

@Composable
private fun AnchorVerticalLink(
    link: ElAnchorLink,
    depth: Int,
    activeHref: String,
    linkContent: (@Composable (link: ElAnchorLink, active: Boolean) -> Unit)?,
    onBounds: (String, Rect) -> Unit,
    onActivate: (String) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val exactActive = link.href == activeHref
    val descendantActive = link.children.any { it.contains(activeHref) }
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .onGloballyPositioned { onBounds(link.href, it.boundsInRoot()) }
                .clickable { onActivate(link.href) }
                .height(28.dp)
                .padding(start = 14.dp * (depth + 1)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (linkContent != null) {
                linkContent(link, exactActive)
            } else {
                BasicText(
                    text = link.title,
                    style = theme.typography.small.copy(
                        color = when {
                            exactActive -> colors.primary
                            descendantActive -> colors.textPrimary
                            else -> colors.textSecondary
                        },
                        fontWeight = if (exactActive || descendantActive) FontWeight.Medium else FontWeight.Normal,
                    ),
                )
            }
        }
        link.children.forEach { child ->
            AnchorVerticalLink(
                link = child,
                depth = depth + 1,
                activeHref = activeHref,
                linkContent = linkContent,
                onBounds = onBounds,
                onActivate = onActivate,
            )
        }
    }
}

@Composable
private fun AnchorHorizontalLink(
    link: ElAnchorLink,
    isFirst: Boolean,
    activeHref: String,
    linkContent: (@Composable (link: ElAnchorLink, active: Boolean) -> Unit)?,
    onBounds: (String, Rect) -> Unit,
    onActivate: (String) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val exactActive = link.href == activeHref
    val descendantActive = link.children.any { it.contains(activeHref) }
    // Spacing lives outside the measured row so the marker matches the label.
    if (!isFirst) Spacer(Modifier.width(16.dp))
    Row(
        Modifier
            .onGloballyPositioned { onBounds(link.href, it.boundsInRoot()) }
            .clickable { onActivate(link.href) }
            .height(28.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (linkContent != null) {
            linkContent(link, exactActive)
        } else {
            BasicText(
                text = link.title,
                style = theme.typography.small.copy(
                    color = when {
                        exactActive -> colors.primary
                        descendantActive -> colors.textPrimary
                        else -> colors.textSecondary
                    },
                    fontWeight = if (exactActive || descendantActive) FontWeight.Medium else FontWeight.Normal,
                ),
            )
        }
    }
}
