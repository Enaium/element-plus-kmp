package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.elTriggerPress
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperArrow
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay

enum class ElPopoverTrigger { Click, Hover, Focus, ContextMenu }

/** `<el-popover>` with optional title and rich body content. */
@Composable
fun ElPopover(
    modifier: Modifier = Modifier,
    title: String? = null,
    trigger: ElPopoverTrigger = ElPopoverTrigger.Click,
    placement: ElPlacement = ElPlacement.Bottom,
    width: Dp = 180.dp,
    disabled: Boolean = false,
    effect: ElTooltipEffect = ElTooltipEffect.Light,
    showArrow: Boolean = true,
    offset: DpOffset = DpOffset(0.dp, 8.dp),
    showAfter: Long = 0,
    hideAfter: Long = 200,
    visible: Boolean? = null,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    titleContent: (@Composable () -> Unit)? = null,
    triggerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    var internalVisible by remember { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    fun setVisible(next: Boolean) {
        if (visible == null) internalVisible = next
        onVisibleChange?.invoke(next)
    }
    if (trigger == ElPopoverTrigger.Hover) {
        LaunchedEffect(hovered, disabled, showAfter, hideAfter) {
            if (!disabled && visible == null) {
                if (hovered) {
                    if (showAfter > 0) delay(showAfter)
                    setVisible(true)
                } else {
                    if (hideAfter > 0) delay(hideAfter)
                    setVisible(false)
                }
            }
        }
    }
    val focusModifier = Modifier.onFocusChanged { focus ->
        if (trigger == ElPopoverTrigger.Focus && visible == null) setVisible(focus.isFocused && !disabled)
    }
    val contextMenuModifier = if (trigger == ElPopoverTrigger.ContextMenu) {
        Modifier.pointerInput(trigger, disabled, visible) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    if (event.type == PointerEventType.Press && event.buttons.isSecondaryPressed && !disabled) {
                        setVisible(true)
                        event.changes.forEach { it.consume() }
                    }
                }
            }
        }
    } else Modifier
    Box(
        modifier
            .then(focusModifier)
            .then(contextMenuModifier)
            .hoverable(interaction)
            .elTriggerPress(trigger == ElPopoverTrigger.Click && !disabled) {
                setVisible(!(visible ?: internalVisible))
            },
    ) {
        triggerContent()
        ElPopper(
            visible = (visible ?: internalVisible) && !disabled,
            onDismissRequest = { setVisible(false) },
            placement = placement,
            offset = offset,
            focusable = trigger == ElPopoverTrigger.Click,
        ) {
            val dark = effect == ElTooltipEffect.Dark
            val arrowColor = if (dark) colors.textPrimary else colors.backgroundOverlay
            val body: @Composable () -> Unit = {
                ElPopperSurface(
                    modifier = Modifier.width(width),
                    background = if (dark) colors.textPrimary else colors.backgroundOverlay,
                    borderColor = if (dark) colors.textPrimary else colors.borderLight,
                ) {
                    Column {
                        if (titleContent != null) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .background(if (dark) colors.textPrimary else colors.fillLight)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                            ) { titleContent() }
                        } else if (title != null) {
                            BasicText(
                                text = title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (dark) colors.textPrimary else colors.fillLight)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                style = theme.typography.small.copy(
                                    color = if (dark) colors.white else colors.textPrimary,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                                ),
                            )
                        }
                        Box(Modifier.fillMaxWidth().padding(12.dp)) { content() }
                    }
                }
            }
            if (showArrow) {
                when {
                    placement.isTop -> Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                        body(); ElPopperArrow(placement, arrowColor)
                    }
                    placement.isBottom -> Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                        ElPopperArrow(placement, arrowColor); body()
                    }
                    placement.isLeft -> Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        body(); ElPopperArrow(placement, arrowColor)
                    }
                    else -> Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        ElPopperArrow(placement, arrowColor); body()
                    }
                }
            } else {
                body()
            }
        }
    }
}
