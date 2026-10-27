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
 * `<el-container>`. Use [direction] to choose the main axis; [aside] is placed
 * next to the main content for horizontal containers.
 */
@Composable
fun ElContainer(
    modifier: Modifier = Modifier,
    direction: ElContainerDirection = ElContainerDirection.Vertical,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    aside: (@Composable () -> Unit)? = null,
    asideWidth: Dp = 300.dp,
    content: @Composable () -> Unit,
) {
    val colors = LocalElTheme.current.colors
    Column(modifier = modifier.fillMaxSize().background(colors.backgroundPage)) {
        header?.invoke()
        if (aside != null && direction == ElContainerDirection.Horizontal) {
            Row(Modifier.weight(1f).fillMaxWidth()) {
                Box(Modifier.width(asideWidth).fillMaxHeight()) { aside() }
                Box(Modifier.weight(1f).fillMaxHeight()) { content() }
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
    content: @Composable () -> Unit,
) {
    ElContainer(
        modifier = modifier,
        direction = if (aside != null) ElContainerDirection.Horizontal else ElContainerDirection.Vertical,
        header = header,
        footer = footer,
        aside = aside,
        asideWidth = asideWidth,
        content = content,
    )
}

/** 24-column row from `<el-row>`. [ElRowCol] applies a weight-based span. */
@Composable
fun ElRow(
    modifier: Modifier = Modifier,
    gutter: Dp = 0.dp,
    justify: ElJustify = ElJustify.Start,
    align: ElAlign = ElAlign.Top,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = gutter / 2),
        horizontalArrangement = when (justify) {
            ElJustify.Start -> Arrangement.spacedBy(gutter)
            ElJustify.Center -> Arrangement.spacedBy(gutter, Alignment.CenterHorizontally)
            ElJustify.End -> Arrangement.spacedBy(gutter, Alignment.End)
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

@Composable
fun RowScope.ElRowCol(
    span: Int,
    modifier: Modifier = Modifier,
    offset: Int = 0,
    content: @Composable () -> Unit,
) {
    val total = (span + offset).coerceIn(1, 24).toFloat()
    BoxWithConstraints(Modifier.weight(total)) {
        val offsetFraction = if (span + offset == 0) 0f else offset.toFloat() / (span + offset)
        Box(Modifier.fillMaxWidth(1f - offsetFraction).align(Alignment.CenterEnd)) { content() }
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
