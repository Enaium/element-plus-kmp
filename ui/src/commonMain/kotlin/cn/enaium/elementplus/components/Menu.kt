package cn.enaium.elementplus.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.internal.rememberElInteractionState
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElMenuMode { Vertical, Horizontal }

@Immutable
internal data class ElMenuContext(
    val mode: ElMenuMode,
    val activeIndex: String,
    val collapsed: Boolean,
    val background: Color,
    val textColor: Color,
    val activeTextColor: Color,
    val hoverBackground: Color,
    val onSelect: (String) -> Unit,
    val isOpen: (String) -> Boolean,
    val toggle: (String) -> Unit,
)

internal val LocalElMenuContext = staticCompositionLocalOf<ElMenuContext?> { null }
internal val LocalElMenuDepth = staticCompositionLocalOf { 0 }
internal val LocalElMenuPopup = staticCompositionLocalOf { false }

/**
 * `<el-menu>` with vertical/horizontal modes, submenus and collapsed icons.
 */
@Composable
fun ElMenu(
    activeIndex: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    mode: ElMenuMode = ElMenuMode.Vertical,
    collapsed: Boolean = false,
    uniqueOpened: Boolean = false,
    background: Color? = null,
    textColor: Color? = null,
    activeTextColor: Color? = null,
    hoverBackground: Color? = null,
    defaultOpeneds: Set<String> = emptySet(),
    content: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    var open by remember { mutableStateOf(defaultOpeneds) }
    val context = remember(mode, activeIndex, collapsed, background, textColor, activeTextColor, hoverBackground, uniqueOpened, onSelect, colors) {
        ElMenuContext(
            mode = mode,
            activeIndex = activeIndex,
            collapsed = collapsed,
            background = background ?: colors.background,
            textColor = textColor ?: colors.textRegular,
            activeTextColor = activeTextColor ?: colors.primary,
            hoverBackground = hoverBackground ?: colors.fillLight,
            onSelect = { index ->
                onSelect(index)
                if (mode == ElMenuMode.Horizontal || collapsed) open = emptySet()
            },
            isOpen = { it in open },
            toggle = { index ->
                open = if (index in open) open - index
                else if (uniqueOpened) setOf(index) else open + index
            },
        )
    }
    CompositionLocalProvider(LocalElMenuContext provides context) {
        if (mode == ElMenuMode.Horizontal) {
            Row(
                modifier
                    .background(context.background)
                    .border(1.dp, colors.borderLight)
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) { content() }
        } else {
            Column(
                modifier
                    .width(if (collapsed) 64.dp else 240.dp)
                    .background(context.background)
                    .border(1.dp, colors.borderLight),
            ) { content() }
        }
    }
}

@Composable
fun ElMenuItem(
    index: String,
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    disabled: Boolean = false,
    content: (@Composable () -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val context = LocalElMenuContext.current ?: return
    val depth = LocalElMenuDepth.current
    val inPopup = LocalElMenuPopup.current
    val active = context.activeIndex == index
    val interaction = remember(index) { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val bg = when {
        active -> if (context.mode == ElMenuMode.Vertical) colors.light(cn.enaium.elementplus.theme.ElType.Primary, 9) else context.background
        hovered && !disabled -> context.hoverBackground
        else -> context.background
    }
    val fg = when {
        disabled -> colors.textDisabled
        active -> context.activeTextColor
        else -> context.textColor
    }
    Row(
        modifier = modifier
            .then(
                if (inPopup || context.mode == ElMenuMode.Vertical) Modifier.fillMaxWidth()
                else Modifier
            )
            .height(
                when {
                    inPopup -> 36.dp
                    context.mode == ElMenuMode.Horizontal -> 60.dp
                    else -> 50.dp
                }
            )
            .background(bg)
            .hoverable(interaction)
            .clickable(enabled = !disabled) { context.onSelect(index) }
            .padding(
                start = if (inPopup || context.collapsed) 12.dp else (16 + depth * 18).dp,
                end = 12.dp,
            )
            .then(
                if (context.mode == ElMenuMode.Horizontal) Modifier.bottomBorder(
                    if (active) context.activeTextColor else Color.Transparent
                ) else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (inPopup || context.collapsed) Arrangement.Center else Arrangement.Start,
    ) {
        if (icon != null) {
            ElIcon(icon, size = 18.dp, tint = fg)
            if (!context.collapsed) Spacer(Modifier.width(8.dp))
        } else if (context.collapsed) {
            BasicText(text = title.take(1), style = theme.typography.base.copy(color = fg))
        }
        if (!context.collapsed) {
            if (content != null) content() else BasicText(
                text = title,
                modifier = if (inPopup || context.mode == ElMenuMode.Horizontal) Modifier
                else Modifier.weight(1f, fill = false),
                style = theme.typography.base.copy(
                    color = fg,
                    fontWeight = if (active) FontWeight.Medium else FontWeight.Normal,
                ),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun ElSubMenu(
    index: String,
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    disabled: Boolean = false,
    content: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val context = LocalElMenuContext.current ?: return
    val depth = LocalElMenuDepth.current
    val inPopup = LocalElMenuPopup.current
    val open = context.isOpen(index)
    val interaction = remember(index) { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val header: @Composable () -> Unit = {
        Row(
            Modifier
                .then(
                    if (inPopup || context.mode == ElMenuMode.Vertical) Modifier.fillMaxWidth()
                    else Modifier
                )
                .height(
                    when {
                        inPopup -> 36.dp
                        context.mode == ElMenuMode.Horizontal -> 60.dp
                        else -> 50.dp
                    }
                )
                .background(if (hovered && !disabled) context.hoverBackground else context.background)
                .hoverable(interaction)
                .clickable(enabled = !disabled) { context.toggle(index) }
                .padding(
                    start = if (inPopup || context.collapsed) 12.dp else (16 + depth * 18).dp,
                    end = 12.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (inPopup || context.collapsed) Arrangement.Center else Arrangement.Start,
        ) {
            if (icon != null) {
                ElIcon(icon, size = 18.dp, tint = context.textColor)
                if (!context.collapsed) Spacer(Modifier.width(8.dp))
            }
            if (!context.collapsed) {
                BasicText(
                    text = title,
                    modifier = if (inPopup || context.mode == ElMenuMode.Horizontal) Modifier else Modifier.weight(1f),
                    style = theme.typography.base.copy(color = context.textColor),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
                ElIcon(
                    ElIcons.ArrowDown,
                    size = 10.dp,
                    tint = colors.textPlaceholder,
                    modifier = Modifier.rotate(if (open) 180f else 0f),
                )
            }
        }
    }
    if (context.mode == ElMenuMode.Horizontal || (context.collapsed && context.mode == ElMenuMode.Vertical)) {
        Box(modifier) {
            header()
            ElPopper(
                visible = open,
                onDismissRequest = { context.toggle(index) },
                placement = if (context.mode == ElMenuMode.Horizontal) ElPlacement.BottomStart else ElPlacement.RightStart,
                offset = androidx.compose.ui.unit.DpOffset(0.dp, 2.dp),
            ) {
                ElPopperSurface {
                    Column(Modifier.width(IntrinsicSize.Max).padding(vertical = 4.dp)) {
                        CompositionLocalProvider(
                            LocalElMenuDepth provides depth + 1,
                            LocalElMenuPopup provides true,
                        ) { content() }
                    }
                }
            }
        }
    } else {
        Column(modifier.fillMaxWidth()) {
            header()
            AnimatedVisibility(open, enter = expandVertically(), exit = shrinkVertically()) {
                Column(Modifier.fillMaxWidth()) {
                    CompositionLocalProvider(LocalElMenuDepth provides depth + 1) { content() }
                }
            }
        }
    }
}

private fun Modifier.bottomBorder(color: Color): Modifier = this.drawBehind {
    if (color.alpha > 0f) {
        val stroke = 2.dp.toPx()
        drawRect(
            color = color,
            topLeft = Offset(0f, size.height - stroke),
            size = Size(size.width, stroke),
        )
    }
}
