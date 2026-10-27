package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElContainerDirection { Horizontal, Vertical }

/**
 * `<el-container>`. Use [direction] to choose the main axis; [aside] and
 * [asideEnd] are placed on either side of the main content for horizontal
 * containers. Nested containers reproduce the upstream aside-wrapping layouts.
 * [background] defaults to the page background and can be set to `null` for
 * nested containers.
 */
@Composable
fun ElContainer(
    modifier: Modifier = Modifier,
    direction: ElContainerDirection = ElContainerDirection.Vertical,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    aside: (@Composable () -> Unit)? = null,
    asideWidth: Dp = 300.dp,
    asideEnd: (@Composable () -> Unit)? = null,
    asideEndWidth: Dp = 300.dp,
    background: Color? = LocalElTheme.current.colors.backgroundPage,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .then(if (background != null) Modifier.background(background) else Modifier),
    ) {
        header?.invoke()
        if ((aside != null || asideEnd != null) && direction == ElContainerDirection.Horizontal) {
            Row(Modifier.weight(1f).fillMaxWidth()) {
                if (aside != null) {
                    Box(Modifier.width(asideWidth).fillMaxHeight()) { aside() }
                }
                Box(Modifier.weight(1f).fillMaxHeight()) { content() }
                if (asideEnd != null) {
                    Box(Modifier.width(asideEndWidth).fillMaxHeight()) { asideEnd() }
                }
            }
        } else {
            Box(Modifier.weight(1f).fillMaxWidth()) { content() }
        }
        footer?.invoke()
    }
}

@Composable
fun ElHeader(
    modifier: Modifier = Modifier,
    height: Dp = 60.dp,
    background: Color = LocalElTheme.current.colors.background,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxWidth().height(height).background(background),
        contentAlignment = Alignment.CenterStart,
    ) {
        content()
    }
}

@Composable
fun ElAside(
    modifier: Modifier = Modifier,
    width: Dp = 300.dp,
    background: Color = LocalElTheme.current.colors.background,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.width(width).fillMaxHeight().background(background)) { content() }
}

@Composable
fun ElMain(
    modifier: Modifier = Modifier,
    background: Color = LocalElTheme.current.colors.background,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize().background(background)) { content() }
}

@Composable
fun ElFooter(
    modifier: Modifier = Modifier,
    height: Dp = 60.dp,
    background: Color = LocalElTheme.current.colors.background,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxWidth().height(height).background(background)) { content() }
}

/** A [Row]/[Column]-based page shell matching the Element Plus layout examples. */
@Composable
fun ElLayout(
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
    aside: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    asideWidth: Dp = 300.dp,
    asideEnd: (@Composable () -> Unit)? = null,
    asideEndWidth: Dp = 300.dp,
    content: @Composable () -> Unit,
) {
    ElContainer(
        modifier = modifier,
        direction = if (aside != null || asideEnd != null) ElContainerDirection.Horizontal else ElContainerDirection.Vertical,
        header = header,
        footer = footer,
        aside = aside,
        asideWidth = asideWidth,
        asideEnd = asideEnd,
        asideEndWidth = asideEndWidth,
        content = content,
    )
}

/**
 * Responsive column configuration, equivalent to a `:xs="{ span, offset }"`
 * value. Unset fields keep the base column values.
 */
data class ElColSize(
    val span: Int? = null,
    val offset: Int? = null,
    val push: Int? = null,
    val pull: Int? = null,
)

/** Element Plus breakpoints used by the responsive `xs/sm/md/lg/xl` props. */
enum class ElBreakpoint(val minWidth: Dp) { Xs(0.dp), Sm(768.dp), Md(992.dp), Lg(1200.dp), Xl(1920.dp) }

internal val LocalElRowWidth = androidx.compose.runtime.staticCompositionLocalOf<Dp?> { null }
internal val LocalElRowGutter = androidx.compose.runtime.staticCompositionLocalOf { 0.dp }

private data class ElResolvedCol(val span: Int, val offset: Int, val push: Int, val pull: Int)

private fun resolveElCol(
    rowWidth: Dp?,
    span: Int,
    offset: Int,
    push: Int,
    pull: Int,
    xs: ElColSize?,
    sm: ElColSize?,
    md: ElColSize?,
    lg: ElColSize?,
    xl: ElColSize?,
): ElResolvedCol {
    var resolved = ElResolvedCol(span, offset, push, pull)
    if (rowWidth == null || rowWidth == Dp.Infinity) return resolved
    fun apply(size: ElColSize?) {
        if (size == null) return
        resolved = ElResolvedCol(
            span = size.span ?: resolved.span,
            offset = size.offset ?: resolved.offset,
            push = size.push ?: resolved.push,
            pull = size.pull ?: resolved.pull,
        )
    }
    if (rowWidth < ElBreakpoint.Sm.minWidth) {
        apply(xs)
        return resolved
    }
    apply(sm?.takeIf { rowWidth >= ElBreakpoint.Sm.minWidth })
    apply(md?.takeIf { rowWidth >= ElBreakpoint.Md.minWidth })
    apply(lg?.takeIf { rowWidth >= ElBreakpoint.Lg.minWidth })
    apply(xl?.takeIf { rowWidth >= ElBreakpoint.Xl.minWidth })
    return resolved
}

/** 24-column row from `<el-row>`. [ElRowCol] applies a responsive span. */
@Composable
fun ElRow(
    modifier: Modifier = Modifier,
    gutter: Dp = 0.dp,
    justify: ElJustify = ElJustify.Start,
    align: ElAlign = ElAlign.Top,
    content: @Composable RowScope.() -> Unit,
) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val rowWidth = maxWidth
        androidx.compose.runtime.CompositionLocalProvider(
            LocalElRowWidth provides rowWidth,
            LocalElRowGutter provides gutter,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = when (justify) {
                    ElJustify.Start -> Arrangement.Start
                    ElJustify.Center -> Arrangement.Center
                    ElJustify.End -> Arrangement.End
                    ElJustify.SpaceBetween -> Arrangement.SpaceBetween
                    ElJustify.SpaceAround -> Arrangement.SpaceAround
                    ElJustify.SpaceEvenly -> Arrangement.SpaceEvenly
                },
                verticalAlignment = when (align) {
                    ElAlign.Top -> Alignment.Top
                    ElAlign.Center -> Alignment.CenterVertically
                    ElAlign.Bottom -> Alignment.Bottom
                },
                content = content,
            )
        }
    }
}

/**
 * A column of [ElRow]. [span] defaults to the full 24-column width. [push]
 * shifts the column right and [pull] left; the responsive [xs]..[xl] sizes
 * override the base values from their breakpoint upwards.
 */
@Composable
fun RowScope.ElRowCol(
    span: Int = 24,
    modifier: Modifier = Modifier,
    offset: Int = 0,
    push: Int = 0,
    pull: Int = 0,
    xs: ElColSize? = null,
    sm: ElColSize? = null,
    md: ElColSize? = null,
    lg: ElColSize? = null,
    xl: ElColSize? = null,
    content: @Composable () -> Unit,
) {
    val rowWidth = LocalElRowWidth.current
    val gutter = LocalElRowGutter.current
    val resolved = resolveElCol(rowWidth, span, offset, push, pull, xs, sm, md, lg, xl)
    val total = (resolved.span + resolved.offset).coerceIn(1, 24)
    val columnFraction = total / 24f
    val contentFraction = resolved.span.toFloat() / total
    val pushOffset = when (rowWidth) {
        null, Dp.Infinity -> 0.dp
        else -> rowWidth * ((resolved.push - resolved.pull).toFloat() / 24f)
    }
    val widthModifier = if (rowWidth != null && rowWidth != Dp.Infinity) {
        Modifier.width(rowWidth * columnFraction)
    } else {
        Modifier.weight(columnFraction)
    }
    BoxWithConstraints(
        modifier
            .then(widthModifier)
            .padding(horizontal = gutter / 2)
            .then(if (pushOffset != 0.dp) Modifier.offset(x = pushOffset) else Modifier),
    ) {
        Box(Modifier.fillMaxWidth(contentFraction).align(Alignment.CenterEnd)) { content() }
    }
}

enum class ElJustify { Start, Center, End, SpaceBetween, SpaceAround, SpaceEvenly }
enum class ElAlign { Top, Center, Bottom }

/** A vertical stack that mirrors `<el-layout>`'s typical use. */
@Composable
fun ElLayoutColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) = Column(modifier, content = content)
