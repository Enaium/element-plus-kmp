package cn.enaium.elementplus.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.min

enum class ElCarouselArrow { Hover, Always, Never }
enum class ElCarouselIndicatorPosition { Inside, Outside, None }
enum class ElCarouselType { Default, Card }

/** How indicators switch slides, mirroring the `trigger` attribute. */
enum class ElCarouselTrigger { Hover, Click }

/**
 * `<el-carousel>` built on Compose's pager with autoplay, dots and hover arrows.
 *
 * [pauseOnHover] stops the autoplay timer while the carousel is hovered,
 * [cardScale] controls the size of inactive card-type slides, [autoHeight] sizes
 * the container to the tallest slide (slides should use natural heights then)
 * and [motionBlur] blurs slides while they are moving.
 */
@Composable
fun ElCarousel(
    itemCount: Int,
    modifier: Modifier = Modifier,
    height: Dp = 300.dp,
    autoplay: Boolean = true,
    interval: Long = 3000,
    loop: Boolean = true,
    arrow: ElCarouselArrow = ElCarouselArrow.Hover,
    indicatorPosition: ElCarouselIndicatorPosition = ElCarouselIndicatorPosition.Inside,
    type: ElCarouselType = ElCarouselType.Default,
    vertical: Boolean = false,
    initialPage: Int = 0,
    onPageChange: ((Int) -> Unit)? = null,
    pauseOnHover: Boolean = true,
    trigger: ElCarouselTrigger = ElCarouselTrigger.Hover,
    cardScale: Float = 0.83f,
    autoHeight: Boolean = false,
    motionBlur: Boolean = false,
    onChange: ((current: Int, prev: Int) -> Unit)? = null,
    itemContent: @Composable (index: Int) -> Unit,
) {
    if (itemCount <= 0) return
    if (autoHeight) {
        SubcomposeLayout(modifier.fillMaxWidth()) { constraints ->
            val width = if (constraints.hasBoundedWidth) constraints.maxWidth else constraints.minWidth.coerceAtLeast(320.dp.roundToPx())
            val measureHeight = if (constraints.hasBoundedHeight) constraints.maxHeight else 900.dp.roundToPx()
            var tallest = 0
            repeat(itemCount) { index ->
                val itemHeight = subcompose("el-carousel-measure-$index") {
                    Box(Modifier.fillMaxWidth()) { itemContent(index) }
                }.maxOfOrNull { it.measure(Constraints(maxWidth = width, maxHeight = measureHeight)).height } ?: 0
                if (itemHeight > tallest) tallest = itemHeight
            }
            tallest = tallest.coerceAtMost(measureHeight)
            val bodyHeight = if (tallest > 0) tallest.toDp() else height
            val placeables = subcompose("el-carousel-body") {
                CarouselBody(
                    itemCount = itemCount,
                    height = bodyHeight,
                    autoplay = autoplay,
                    interval = interval,
                    loop = loop,
                    arrow = arrow,
                    indicatorPosition = indicatorPosition,
                    type = type,
                    vertical = vertical,
                    initialPage = initialPage,
                    onPageChange = onPageChange,
                    pauseOnHover = pauseOnHover,
                    trigger = trigger,
                    cardScale = cardScale,
                    motionBlur = motionBlur,
                    onChange = onChange,
                    itemContent = itemContent,
                )
            }
            val placeable = placeables.first().measure(
                Constraints(
                    minWidth = width,
                    maxWidth = width,
                    minHeight = constraints.minHeight,
                    maxHeight = constraints.maxHeight,
                ),
            )
            layout(width, placeable.height) { placeable.place(0, 0) }
        }
    } else {
        CarouselBody(
            itemCount = itemCount,
            height = height,
            autoplay = autoplay,
            interval = interval,
            loop = loop,
            arrow = arrow,
            indicatorPosition = indicatorPosition,
            type = type,
            vertical = vertical,
            initialPage = initialPage,
            onPageChange = onPageChange,
            pauseOnHover = pauseOnHover,
            trigger = trigger,
            cardScale = cardScale,
            motionBlur = motionBlur,
            onChange = onChange,
            itemContent = itemContent,
            modifier = modifier,
        )
    }
}

@Composable
private fun CarouselBody(
    itemCount: Int,
    height: Dp,
    autoplay: Boolean,
    interval: Long,
    loop: Boolean,
    arrow: ElCarouselArrow,
    indicatorPosition: ElCarouselIndicatorPosition,
    type: ElCarouselType,
    vertical: Boolean,
    initialPage: Int,
    onPageChange: ((Int) -> Unit)?,
    pauseOnHover: Boolean,
    trigger: ElCarouselTrigger,
    cardScale: Float,
    motionBlur: Boolean,
    onChange: ((current: Int, prev: Int) -> Unit)?,
    modifier: Modifier = Modifier,
    itemContent: @Composable (index: Int) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val pagerState = rememberPagerState(initialPage = initialPage.coerceIn(0, itemCount - 1)) { itemCount }
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    var lastPage by remember { mutableIntStateOf(pagerState.currentPage) }
    LaunchedEffect(pagerState.currentPage) {
        val current = pagerState.currentPage
        if (current != lastPage) {
            onChange?.invoke(current, lastPage)
            lastPage = current
        }
        onPageChange?.invoke(current)
    }
    val scope = rememberCoroutineScope()
    LaunchedEffect(autoplay, interval, itemCount, loop) {
        if (!autoplay || itemCount <= 1) return@LaunchedEffect
        while (true) {
            delay(interval)
            if (pauseOnHover && hovered) continue
            if (pagerState.isScrollInProgress) continue
            val next = pagerState.currentPage + 1
            if (next < itemCount) {
                pagerState.animateScrollToPage(next)
            } else if (loop) {
                pagerState.animateScrollToPage(0)
            }
        }
    }
    Column(modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(height)
                .hoverable(interaction),
        ) {
            if (vertical) {
                VerticalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    CarouselSlide(page, pagerState, type, cardScale, motionBlur) { itemContent(page) }
                }
            } else {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    CarouselSlide(page, pagerState, type, cardScale, motionBlur) { itemContent(page) }
                }
            }
            if (arrow != ElCarouselArrow.Never && (arrow == ElCarouselArrow.Always || hovered)) {
                CarouselArrow(
                    icon = if (vertical) ElIcons.ArrowUp else ElIcons.ArrowLeft,
                    contentDescription = locale.carousel.leftArrow,
                    modifier = Modifier
                        .align(if (vertical) Alignment.TopCenter else Alignment.CenterStart)
                        .then(if (vertical) Modifier.padding(top = 10.dp) else Modifier.padding(start = 10.dp)),
                    enabled = loop || pagerState.currentPage > 0,
                ) {
                    val target = if (pagerState.currentPage > 0) pagerState.currentPage - 1 else if (loop) itemCount - 1 else 0
                    scope.launch { pagerState.animateScrollToPage(target) }
                }
                CarouselArrow(
                    icon = if (vertical) ElIcons.ArrowDown else ElIcons.ArrowRight,
                    contentDescription = locale.carousel.rightArrow,
                    modifier = Modifier
                        .align(if (vertical) Alignment.BottomCenter else Alignment.CenterEnd)
                        .then(if (vertical) Modifier.padding(bottom = 10.dp) else Modifier.padding(end = 10.dp)),
                    enabled = loop || pagerState.currentPage < itemCount - 1,
                ) {
                    val target = if (pagerState.currentPage < itemCount - 1) pagerState.currentPage + 1 else if (loop) 0 else itemCount - 1
                    scope.launch { pagerState.animateScrollToPage(target) }
                }
            }
            if (indicatorPosition == ElCarouselIndicatorPosition.Inside) {
                CarouselIndicators(
                    count = itemCount,
                    current = pagerState.currentPage,
                    modifier = if (vertical) {
                        Modifier.align(Alignment.CenterEnd).padding(end = 12.dp)
                    } else {
                        Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
                    },
                    inside = true,
                    vertical = vertical,
                    trigger = trigger,
                ) { target -> scope.launch { pagerState.animateScrollToPage(target) } }
            }
        }
        if (indicatorPosition == ElCarouselIndicatorPosition.Outside) {
            CarouselIndicators(
                count = itemCount,
                current = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                inside = false,
                vertical = false,
                trigger = trigger,
            ) { target -> scope.launch { pagerState.animateScrollToPage(target) } }
        }
    }
}

@Composable
private fun CarouselSlide(
    page: Int,
    pagerState: PagerState,
    type: ElCarouselType,
    cardScale: Float,
    motionBlur: Boolean,
    content: @Composable () -> Unit,
) {
    val offset = (pagerState.currentPage - page + pagerState.currentPageOffsetFraction).absoluteValue
    val blurRadius = if (motionBlur && pagerState.isScrollInProgress) {
        (min(offset, 1f) * 6f).dp
    } else {
        0.dp
    }
    Box(
        Modifier
            .fillMaxSize()
            .blur(blurRadius)
            .graphicsLayer {
                if (type == ElCarouselType.Card) {
                    val scale = 1f - min(offset, 1f) * (1f - cardScale)
                    scaleX = scale
                    scaleY = scale
                    alpha = 1f - min(offset, 1f) * 0.25f
                }
            },
    ) {
        content()
    }
}

@Composable
private fun CarouselArrow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    modifier: Modifier,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier
            .size(36.dp)
            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        ElIcon(icon, size = 18.dp, tint = Color.White.copy(alpha = if (enabled) 1f else 0.4f), contentDescription = contentDescription)
    }
}

@Composable
private fun CarouselIndicators(
    count: Int,
    current: Int,
    modifier: Modifier,
    inside: Boolean,
    vertical: Boolean = false,
    trigger: ElCarouselTrigger = ElCarouselTrigger.Hover,
    onSelect: (Int) -> Unit,
) {
    val colors = LocalElTheme.current.colors
    val locale = LocalElConfig.current.locale
    val indicatorColor = { active: Boolean ->
        if (active) colors.primary else if (inside) Color.White.copy(alpha = 0.6f) else colors.border
    }
    if (vertical) {
        Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            repeat(count) { index ->
                CarouselIndicator(
                    active = index == current,
                    horizontal = false,
                    color = indicatorColor,
                    description = locale.carousel.indicator.replace("{index}", (index + 1).toString()),
                    trigger = trigger,
                    onClick = { onSelect(index) },
                )
            }
        }
    } else {
        Row(
            modifier,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(count) { index ->
                CarouselIndicator(
                    active = index == current,
                    horizontal = true,
                    color = indicatorColor,
                    description = locale.carousel.indicator.replace("{index}", (index + 1).toString()),
                    trigger = trigger,
                    onClick = { onSelect(index) },
                )
            }
        }
    }
}

@Composable
private fun CarouselIndicator(
    active: Boolean,
    horizontal: Boolean,
    color: (Boolean) -> Color,
    description: String,
    trigger: ElCarouselTrigger,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    LaunchedEffect(hovered, trigger) {
        if (hovered && trigger == ElCarouselTrigger.Hover && !active) onClick()
    }
    val length by animateDpAsState(if (active) 24.dp else 8.dp, label = "carousel-indicator")
    val sizeModifier = if (horizontal) {
        Modifier.width(length).height(4.dp)
    } else {
        Modifier.width(4.dp).height(length)
    }
    Box(
        sizeModifier
            .background(color(active), RoundedCornerShape(2.dp))
            .hoverable(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
            )
            .semantics { contentDescription = description },
    )
}
