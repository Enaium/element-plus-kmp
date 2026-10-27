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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay

enum class ElMenuMode { Vertical, Horizontal }

/** How submenus open: on click, or on hover (Element Plus default). */
enum class ElMenuTrigger { Click, Hover }

/** Assigns composition-ordered indices to the direct children of a menu bar. */
internal class ElMenuChildCounter {
    private var value = 0

    fun next(): Int = value++
}

@Immutable
internal data class ElMenuContext(
    val mode: ElMenuMode,
    val activeIndex: String,
    val collapsed: Boolean,
    val background: Color,
    val textColor: Color,
    val activeTextColor: Color,
    val hoverBackground: Color,
    val activeBackground: Color,
    val trigger: ElMenuTrigger,
    val popperOffset: Dp,
    val showTimeout: Int,
    val hideTimeout: Int,
    val closeOnClickOutside: Boolean,
    val onSelect: (String) -> Unit,
    val isOpen: (String) -> Boolean,
    val open: (String) -> Unit,
    val close: (String) -> Unit,
    val toggle: (String) -> Unit,
)

internal val LocalElMenuContext = staticCompositionLocalOf<ElMenuContext?> { null }
internal val LocalElMenuDepth = staticCompositionLocalOf { 0 }
internal val LocalElMenuPopup = staticCompositionLocalOf { false }
internal val LocalElMenuOverflowStart = staticCompositionLocalOf { -1 }
internal val LocalElMenuChildCounter = staticCompositionLocalOf<ElMenuChildCounter?> { null }

/**
 * `<el-menu>` with vertical/horizontal modes, submenus, hover/click submenu
 * triggers, collapsed popups and horizontal overflow ellipsis.
 *
 * [activeIndex] keeps the controlled behaviour of earlier versions; leave it
 * `null` to let the menu manage selection itself starting at [defaultActive].
 * [openeds] likewise makes the open submenus controlled; when `null` the menu
 * keeps them internally from [defaultOpeneds].
 */
@Composable
fun ElMenu(
    activeIndex: String? = null,
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
    defaultActive: String = "",
    menuTrigger: ElMenuTrigger = ElMenuTrigger.Hover,
    ellipsis: Boolean = true,
    ellipsisIcon: ImageVector? = null,
    popperOffset: Dp = 4.dp,
    showTimeout: Int = 300,
    hideTimeout: Int = 300,
    closeOnClickOutside: Boolean = true,
    openeds: Set<String>? = null,
    onOpen: ((String) -> Unit)? = null,
    onClose: ((String) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    var internalActive by remember { mutableStateOf(defaultActive) }
    val resolvedActive = activeIndex ?: internalActive
    var internalOpen by remember { mutableStateOf(defaultOpeneds) }
    val openSet = openeds ?: internalOpen

    fun applyOpen(next: Set<String>) {
        if (openeds == null) internalOpen = next
    }

    fun openIndex(index: String) {
        if (index in openSet) return
        applyOpen(if (uniqueOpened) setOf(index) else openSet + index)
        onOpen?.invoke(index)
    }

    fun closeIndex(index: String) {
        if (index !in openSet) return
        applyOpen(openSet - index)
        onClose?.invoke(index)
    }

    fun closeAll() {
        val removed = openSet
        if (removed.isEmpty()) return
        applyOpen(emptySet())
        removed.forEach { onClose?.invoke(it) }
    }

    val resolvedBackground = background ?: colors.background
    val resolvedText = textColor ?: colors.textRegular
    val resolvedActiveText = activeTextColor ?: colors.primary
    val resolvedHover = hoverBackground ?: if (background == null) {
        colors.fillLight
    } else {
        lerp(resolvedBackground, resolvedText, 0.15f)
    }
    val context = ElMenuContext(
        mode = mode,
        activeIndex = resolvedActive,
        collapsed = collapsed,
        background = resolvedBackground,
        textColor = resolvedText,
        activeTextColor = resolvedActiveText,
        hoverBackground = resolvedHover,
        activeBackground = if (background == null) {
            colors.light(cn.enaium.elementplus.theme.ElType.Primary, 9)
        } else {
            lerp(resolvedBackground, resolvedActiveText, 0.2f)
        },
        trigger = menuTrigger,
        popperOffset = popperOffset,
        showTimeout = showTimeout,
        hideTimeout = hideTimeout,
        closeOnClickOutside = closeOnClickOutside,
        onSelect = { index ->
            onSelect(index)
            if (activeIndex == null) internalActive = index
            if (mode == ElMenuMode.Horizontal || collapsed) closeAll()
        },
        isOpen = { it in openSet },
        open = { index -> openIndex(index) },
        close = { index -> closeIndex(index) },
        toggle = { index -> if (index in openSet) closeIndex(index) else openIndex(index) },
    )
    CompositionLocalProvider(LocalElMenuContext provides context) {
        when {
            mode == ElMenuMode.Horizontal && ellipsis -> ElMenuBarEllipsis(
                context = context,
                modifier = modifier
                    .background(context.background)
                    .border(1.dp, colors.borderLight),
                ellipsisIcon = ellipsisIcon,
                content = content,
            )

            mode == ElMenuMode.Horizontal -> Row(
                modifier
                    .background(context.background)
                    .border(1.dp, colors.borderLight)
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) { content() }

            else -> Column(
                modifier
                    .width(if (collapsed) 64.dp else 240.dp)
                    .background(context.background)
                    .border(1.dp, colors.borderLight),
            ) { content() }
        }
    }
}

/**
 * Horizontal bar that measures every direct child and folds whatever does not
 * fit into a trailing "..." submenu (`:ellipsis="true"`).
 */
@Composable
private fun ElMenuBarEllipsis(
    context: ElMenuContext,
    modifier: Modifier,
    ellipsisIcon: ImageVector?,
    content: @Composable () -> Unit,
) {
    SubcomposeLayout(modifier) { constraints ->
        val barHeight = 60.dp.roundToPx()
        val ellipsisWidth = 56.dp.roundToPx()
        val maxWidth = constraints.maxWidth
        val items = subcompose("items", content).map { it.measure(Constraints()) }
        var used = 0
        var fitting = 0
        while (fitting < items.size && used + items[fitting].width + ellipsisWidth <= maxWidth) {
            used += items[fitting].width
            fitting++
        }
        val overflowed = fitting < items.size
        val more = if (overflowed) {
            subcompose("more") {
                ElMenuEllipsisTrigger(
                    context = context,
                    ellipsisIcon = ellipsisIcon,
                    start = fitting,
                    content = content,
                )
            }.map { it.measure(Constraints()) }
        } else {
            emptyList()
        }
        more.forEach { used += it.width }
        val height = maxOf(
            barHeight,
            items.take(fitting).maxOfOrNull { it.height } ?: 0,
            more.maxOfOrNull { it.height } ?: 0,
        )
        val width = if (constraints.hasBoundedWidth) maxWidth else used
        layout(width, height.coerceIn(constraints.minHeight, if (constraints.hasBoundedHeight) constraints.maxHeight else height)) {
            var x = 0
            for (i in 0 until fitting) {
                val placeable = items[i]
                placeable.placeRelative(x, (height - placeable.height) / 2)
                x += placeable.width
            }
            more.firstOrNull()?.let { placeable ->
                placeable.placeRelative(x, (height - placeable.height) / 2)
            }
        }
    }
}

@Composable
private fun ElMenuEllipsisTrigger(
    context: ElMenuContext,
    ellipsisIcon: ImageVector?,
    start: Int,
    content: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    var open by remember { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val menuInteraction = remember { MutableInteractionSource() }
    val menuHovered by menuInteraction.collectIsHoveredAsState()
    LaunchedEffect(hovered, menuHovered) {
        if (context.trigger == ElMenuTrigger.Hover) {
            if (hovered || menuHovered) {
                if (context.showTimeout > 0) delay(context.showTimeout.toLong())
                open = true
            } else if (open) {
                if (context.hideTimeout > 0) delay(context.hideTimeout.toLong())
                open = false
            }
        }
    }
    Box {
        Row(
            Modifier
                .height(60.dp)
                .background(if (hovered || open) context.hoverBackground else context.background)
                .hoverable(interaction)
                .clickable { open = !open }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (ellipsisIcon != null) {
                ElIcon(ellipsisIcon, size = 16.dp, tint = context.textColor)
            } else {
                BasicText(
                    text = "...",
                    style = theme.typography.base.copy(color = context.textColor),
                )
            }
        }
        ElPopper(
            visible = open,
            onDismissRequest = { open = false },
            placement = ElPlacement.BottomStart,
            offset = DpOffset(0.dp, context.popperOffset),
            dismissOnClickOutside = context.closeOnClickOutside,
        ) {
            ElPopperSurface {
                Column(
                    Modifier
                        .width(IntrinsicSize.Max)
                        .padding(vertical = 4.dp)
                        .hoverable(menuInteraction),
                ) {
                    val counter = remember { ElMenuChildCounter() }
                    CompositionLocalProvider(
                        LocalElMenuDepth provides 1,
                        LocalElMenuPopup provides true,
                        LocalElMenuOverflowStart provides start,
                        LocalElMenuChildCounter provides counter,
                    ) { content() }
                }
            }
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
    onClick: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val context = LocalElMenuContext.current ?: return
    val depth = LocalElMenuDepth.current
    val inPopup = LocalElMenuPopup.current
    val counter = LocalElMenuChildCounter.current
    val overflowStart = LocalElMenuOverflowStart.current
    val childIndex = if (counter != null) remember { counter.next() } else -1
    if (overflowStart >= 0 && childIndex >= 0 && childIndex < overflowStart) return
    val active = context.activeIndex == index
    val interaction = remember(index) { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val bg = when {
        active -> if (context.mode == ElMenuMode.Vertical) context.activeBackground else context.background
        hovered && !disabled -> context.hoverBackground
        else -> context.background
    }
    val fg = when {
        disabled -> colors.textDisabled
        active -> context.activeTextColor
        else -> context.textColor
    }
    val row: @Composable () -> Unit = {
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
                .clickable(enabled = !disabled) {
                    context.onSelect(index)
                    onClick?.invoke()
                }
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
    if (context.collapsed && !inPopup) {
        ElTooltip(text = title, placement = ElPlacement.Right, disabled = disabled) { row() }
    } else {
        row()
    }
}

@Composable
fun ElSubMenu(
    index: String,
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    disabled: Boolean = false,
    popperOffset: Dp? = null,
    showTimeout: Int? = null,
    hideTimeout: Int? = null,
    titleContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val context = LocalElMenuContext.current ?: return
    val depth = LocalElMenuDepth.current
    val inPopup = LocalElMenuPopup.current
    val counter = LocalElMenuChildCounter.current
    val overflowStart = LocalElMenuOverflowStart.current
    val childIndex = if (counter != null) remember { counter.next() } else -1
    if (overflowStart >= 0 && childIndex >= 0 && childIndex < overflowStart) return
    val open = context.isOpen(index)
    val interaction = remember(index) { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val menuInteraction = remember(index) { MutableInteractionSource() }
    val menuHovered by menuInteraction.collectIsHoveredAsState()
    val popupMode = context.mode == ElMenuMode.Horizontal ||
        (context.collapsed && context.mode == ElMenuMode.Vertical)
    val resolvedShowTimeout = showTimeout ?: context.showTimeout
    val resolvedHideTimeout = hideTimeout ?: context.hideTimeout
    var wasInside by remember(index) { mutableStateOf(false) }
    LaunchedEffect(hovered, menuHovered, disabled, popupMode, resolvedShowTimeout, resolvedHideTimeout) {
        if (popupMode && context.trigger == ElMenuTrigger.Hover && !disabled) {
            if (hovered || menuHovered) {
                wasInside = true
                if (resolvedShowTimeout > 0) delay(resolvedShowTimeout.toLong())
                context.open(index)
            } else if (wasInside) {
                if (resolvedHideTimeout > 0) delay(resolvedHideTimeout.toLong())
                wasInside = false
                context.close(index)
            }
        }
    }
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
                if (titleContent != null) {
                    titleContent()
                } else {
                    BasicText(
                        text = title,
                        modifier = if (inPopup || context.mode == ElMenuMode.Horizontal) Modifier else Modifier.weight(1f),
                        style = theme.typography.base.copy(color = context.textColor),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    )
                }
                ElIcon(
                    ElIcons.ArrowDown,
                    size = 10.dp,
                    tint = colors.textPlaceholder,
                    modifier = Modifier.rotate(if (open) 180f else 0f),
                )
            }
        }
    }
    if (popupMode) {
        Box(modifier) {
            header()
            ElPopper(
                visible = open,
                onDismissRequest = { context.close(index) },
                placement = when {
                    inPopup -> ElPlacement.RightStart
                    context.mode == ElMenuMode.Horizontal -> ElPlacement.BottomStart
                    else -> ElPlacement.RightStart
                },
                offset = DpOffset(0.dp, popperOffset ?: context.popperOffset),
                dismissOnClickOutside = context.closeOnClickOutside,
            ) {
                ElPopperSurface {
                    Column(
                        Modifier
                            .width(IntrinsicSize.Max)
                            .padding(vertical = 4.dp)
                            .hoverable(menuInteraction),
                    ) {
                        CompositionLocalProvider(
                            LocalElMenuDepth provides depth + 1,
                            LocalElMenuPopup provides true,
                            LocalElMenuOverflowStart provides -1,
                            LocalElMenuChildCounter provides null,
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

/**
 * `<el-menu-item-group>`: labelled section inside a menu or submenu.
 */
@Composable
fun ElMenuItemGroup(
    modifier: Modifier = Modifier,
    title: String? = null,
    titleContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val context = LocalElMenuContext.current
    val inPopup = LocalElMenuPopup.current
    val counter = LocalElMenuChildCounter.current
    val overflowStart = LocalElMenuOverflowStart.current
    val childIndex = if (counter != null) remember { counter.next() } else -1
    if (overflowStart >= 0 && childIndex >= 0 && childIndex < overflowStart) return
    val showTitle = (title != null || titleContent != null) && context?.collapsed != true
    Column(modifier.fillMaxWidth()) {
        if (showTitle) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .padding(start = if (inPopup) 12.dp else 16.dp, end = 12.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (titleContent != null) {
                    titleContent()
                } else {
                    BasicText(
                        text = title ?: "",
                        style = theme.typography.small.copy(color = colors.textPlaceholder),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    )
                }
            }
        }
        CompositionLocalProvider(
            LocalElMenuOverflowStart provides -1,
            LocalElMenuChildCounter provides null,
        ) { content() }
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
