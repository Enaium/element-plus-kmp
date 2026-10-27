package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.ElPopupPositionProvider
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.internal.rememberElInteractionState
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay

enum class ElDropdownTrigger { Click, Hover, ContextMenu }

data class ElDropdownItem(
    val label: String,
    val command: String = label,
    val disabled: Boolean = false,
    val divided: Boolean = false,
    val icon: ImageVector? = null,
    val shortcut: String? = null,
)

/**
 * Virtual anchor for `virtual-triggering`: an offset/size relative to the
 * dropdown's own bounds, usually the pointer position of a context menu.
 */
@Immutable
data class ElVirtualTrigger(
    val x: Dp = 0.dp,
    val y: Dp = 0.dp,
    val width: Dp = 0.dp,
    val height: Dp = 0.dp,
)

/** Programmatic handle for [ElDropdown] (`handleOpen` / `handleClose`). */
@Stable
class ElDropdownState(initialVisible: Boolean = false) {
    var visible by mutableStateOf(initialVisible)
        internal set

    fun open() {
        visible = true
    }

    fun close() {
        visible = false
    }

    fun toggle() {
        visible = !visible
    }
}

/** Command dispatcher shared with [ElDropdownMenu]/[ElDropdownMenuItem] content. */
@Immutable
class ElDropdownMenuContext internal constructor(
    private val onSelect: (String) -> Unit,
) {
    fun select(command: String) = onSelect(command)
}

internal val LocalElDropdownMenuContext = staticCompositionLocalOf<ElDropdownMenuContext?> { null }

/**
 * `<el-dropdown>` anchored menu triggered by click, hover or context menu.
 *
 * [splitButton] renders a button group whose left half calls [onClick] and
 * whose caret half opens the menu, with [type]/[size] forwarded to the
 * buttons. Pass [state] for programmatic control, [visible] to fully control
 * the popup, or [virtualTrigger] to anchor the menu away from the trigger.
 */
@Composable
fun ElDropdown(
    items: List<ElDropdownItem>,
    onCommand: (String) -> Unit,
    modifier: Modifier = Modifier,
    trigger: ElDropdownTrigger = ElDropdownTrigger.Hover,
    placement: ElPlacement = ElPlacement.BottomEnd,
    disabled: Boolean = false,
    showArrow: Boolean = true,
    hideOnClick: Boolean = true,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    splitButton: Boolean = false,
    type: ElType? = null,
    size: ElSize? = null,
    showTimeout: Int = 150,
    hideTimeout: Int = 150,
    maxHeight: Dp? = null,
    offset: DpOffset = DpOffset(0.dp, 6.dp),
    state: ElDropdownState? = null,
    visible: Boolean? = null,
    virtualTrigger: ElVirtualTrigger? = null,
    itemContent: (@Composable (ElDropdownItem) -> Unit)? = null,
    menuContent: (@Composable () -> Unit)? = null,
    popperModifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    var internalVisible by remember { mutableStateOf(false) }
    var contextMenuPosition by remember { mutableStateOf<DpOffset?>(null) }
    val isVisible = visible ?: state?.visible ?: internalVisible
    val currentOnVisibleChange by rememberUpdatedState(onVisibleChange)
    fun setVisible(next: Boolean) {
        if (visible != null) {
            if (visible != next) currentOnVisibleChange?.invoke(next)
            return
        }
        val current = state?.visible ?: internalVisible
        if (current == next) return
        if (state != null) {
            if (next) state.open() else state.close()
        } else {
            internalVisible = next
        }
        currentOnVisibleChange?.invoke(next)
    }

    val interaction = remember { MutableInteractionSource() }
    val anchorHovered by interaction.collectIsHoveredAsState()
    val menuInteraction = remember { MutableInteractionSource() }
    val menuHovered by menuInteraction.collectIsHoveredAsState()
    var pointerInside by remember { mutableStateOf(false) }
    LaunchedEffect(anchorHovered, menuHovered, disabled) {
        if (trigger == ElDropdownTrigger.Hover && !disabled) {
            if (anchorHovered || menuHovered) {
                pointerInside = true
                if (showTimeout > 0) delay(showTimeout.toLong())
                setVisible(true)
            } else if (pointerInside) {
                if (hideTimeout > 0) delay(hideTimeout.toLong())
                pointerInside = false
                setVisible(false)
            }
        }
    }
    val contextMenuModifier = if (trigger == ElDropdownTrigger.ContextMenu) {
        Modifier.pointerInput(disabled) {
            if (disabled) return@pointerInput
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Main)
                    if (event.type == PointerEventType.Press && event.buttons.isSecondaryPressed) {
                        val position = event.changes.firstOrNull()?.position ?: Offset.Zero
                        contextMenuPosition = DpOffset(position.x.toDp(), position.y.toDp())
                        setVisible(true)
                    }
                }
            }
        }
    } else {
        Modifier
    }
    val virtualAnchor: ElVirtualTrigger? = virtualTrigger ?: contextMenuPosition?.let { ElVirtualTrigger(x = it.x, y = it.y) }
    val menu: @Composable () -> Unit = {
        val menuContext = ElDropdownMenuContext { command ->
            onCommand(command)
            if (hideOnClick) setVisible(false)
        }
        Box(Modifier.hoverable(menuInteraction)) {
            CompositionLocalProvider(LocalElDropdownMenuContext provides menuContext) {
                if (menuContent != null) {
                    menuContent()
                } else {
                    ElDropdownMenu(
                        modifier = popperModifier,
                        maxHeight = maxHeight,
                    ) {
                        items.forEach { item ->
                            val custom = itemContent
                            val slot: (@Composable (Color) -> Unit)? = if (custom != null) {
                                { _ -> custom(item) }
                            } else {
                                null
                            }
                            ElDropdownMenuItem(
                                label = item.label,
                                command = item.command,
                                disabled = item.disabled,
                                divided = item.divided,
                                icon = item.icon,
                                shortcut = item.shortcut,
                                content = slot,
                            )
                        }
                    }
                }
            }
        }
    }
    Box(modifier.hoverable(interaction).then(contextMenuModifier)) {
        if (splitButton) {
            ElButtonGroup(size = size, type = type) {
                ElButtonGroupItem(first = true, last = false) {
                    ElButton(
                        onClick = { onClick?.invoke() },
                        type = type ?: ElType.Default,
                        size = size,
                        disabled = disabled,
                        content = { _ -> content() },
                    )
                }
                ElButtonGroupItem(first = false, last = true) {
                    ElButton(
                        onClick = {
                            if (!disabled) {
                                contextMenuPosition = null
                                setVisible(!isVisible)
                            }
                        },
                        type = type ?: ElType.Default,
                        size = size,
                        disabled = disabled,
                        icon = ElIcons.ArrowDown,
                        content = { _ -> },
                    )
                }
            }
        } else {
            Box {
                content()
                if (showArrow) {
                    ElIcon(
                        ElIcons.ArrowDown,
                        size = 10.dp,
                        tint = colors.textPlaceholder,
                        contentDescription = locale.dropdown.toggleDropdown,
                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 7.dp),
                    )
                }
            }
            if (trigger == ElDropdownTrigger.Click) {
                Box(
                    Modifier.matchParentSize().clickable(enabled = !disabled) {
                        contextMenuPosition = null
                        setVisible(!isVisible)
                    },
                )
            }
        }
        if (virtualAnchor != null) {
            ElVirtualPopper(
                visible = isVisible,
                onDismissRequest = { setVisible(false) },
                placement = placement,
                anchor = virtualAnchor,
            ) { menu() }
        } else {
            ElPopper(
                visible = isVisible,
                onDismissRequest = { setVisible(false) },
                placement = placement,
                offset = offset,
                focusable = false,
            ) { menu() }
        }
    }
}

/**
 * `<el-dropdown-menu>`: composes with [ElDropdownMenuItem] for custom rows when
 * [ElDropdown]'s `menuContent` slot is used.
 */
@Composable
fun ElDropdownMenu(
    modifier: Modifier = Modifier,
    maxHeight: Dp? = null,
    content: @Composable () -> Unit,
) {
    ElPopperSurface(
        Modifier
            .widthIn(min = 160.dp, max = 280.dp)
            .width(IntrinsicSize.Max)
            .then(modifier),
    ) {
        Column(
            Modifier
                .then(
                    if (maxHeight != null) {
                        Modifier.heightIn(max = maxHeight).verticalScroll(rememberScrollState())
                    } else {
                        Modifier
                    }
                )
                .padding(vertical = 5.dp),
        ) {
            content()
        }
    }
}

/**
 * `<el-dropdown-item>` with the `default` (custom content) bulk slot and an
 * optional divider.
 */
@Composable
fun ElDropdownMenuItem(
    modifier: Modifier = Modifier,
    label: String? = null,
    command: String? = label,
    disabled: Boolean = false,
    divided: Boolean = false,
    icon: ImageVector? = null,
    shortcut: String? = null,
    content: (@Composable (Color) -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val menuContext = LocalElDropdownMenuContext.current
    val resolvedCommand = command ?: label ?: ""
    if (divided) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 8.dp)
                .background(colors.borderLighter),
        )
    }
    val interaction = remember(resolvedCommand) { MutableInteractionSource() }
    val hovered = rememberElInteractionState(interaction).hovered
    val fg = if (disabled) colors.textDisabled else colors.textRegular
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 34.dp)
            .background(if (hovered && !disabled) colors.fillLight else colors.backgroundOverlay)
            .hoverable(interaction)
            .clickable(enabled = !disabled) { menuContext?.select(resolvedCommand) }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (content != null) {
            Box(Modifier.weight(1f)) { content(fg) }
        } else {
            if (icon != null) {
                ElIcon(icon, size = 14.dp, tint = fg)
                Spacer(Modifier.width(6.dp))
            }
            BasicText(
                text = label ?: "",
                modifier = Modifier.weight(1f),
                style = theme.typography.small.copy(color = fg),
            )
            if (shortcut != null) {
                BasicText(
                    text = shortcut,
                    style = theme.typography.extraSmall.copy(color = colors.textPlaceholder),
                )
            }
        }
    }
}

/** Popup anchored to an arbitrary point inside the dropdown bounds. */
@Composable
private fun ElVirtualPopper(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    placement: ElPlacement,
    anchor: ElVirtualTrigger,
    content: @Composable () -> Unit,
) {
    if (!visible) return
    val density = LocalDensity.current
    val provider = remember(placement, anchor, density) {
        ElVirtualTriggerPositionProvider(placement, anchor, density)
    }
    Popup(
        popupPositionProvider = provider,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(
            focusable = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
    ) {
        content()
    }
}

private class ElVirtualTriggerPositionProvider(
    placement: ElPlacement,
    private val anchor: ElVirtualTrigger,
    private val density: Density,
) : PopupPositionProvider {
    private val delegate = ElPopupPositionProvider(placement, DpOffset(0.dp, 0.dp), density)

    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val dx = with(density) { anchor.x.roundToPx() }
        val dy = with(density) { anchor.y.roundToPx() }
        val left = anchorBounds.left + dx
        val top = anchorBounds.top + dy
        return delegate.calculatePosition(
            IntRect(left, top, left + with(density) { anchor.width.roundToPx() }, top + with(density) { anchor.height.roundToPx() }),
            windowSize,
            layoutDirection,
            popupContentSize,
        )
    }
}
