package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.elTriggerPress
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperArrow
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay

enum class ElTooltipEffect { Dark, Light }

enum class ElTooltipTrigger { Hover, Click, Focus, ContextMenu }

/** `<el-tooltip>` shown after a short hover delay. */
@Composable
fun ElTooltip(
    modifier: Modifier = Modifier,
    text: String? = null,
    placement: ElPlacement = ElPlacement.Top,
    effect: ElTooltipEffect = ElTooltipEffect.Dark,
    disabled: Boolean = false,
    showAfter: Long = 0,
    hideAfter: Long = 200,
    maxWidth: Dp = 260.dp,
    trigger: ElTooltipTrigger = ElTooltipTrigger.Hover,
    showArrow: Boolean = true,
    offset: DpOffset = DpOffset(0.dp, 6.dp),
    visible: Boolean? = null,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    tooltip: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    var internalVisible by remember { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }
    val hoverState by interaction.collectIsHoveredAsState()
    fun setVisible(next: Boolean) {
        if (visible == null) internalVisible = next
        onVisibleChange?.invoke(next)
    }
    LaunchedEffect(hoverState, disabled, trigger) {
        if (trigger == ElTooltipTrigger.Hover && visible == null) {
            if (hoverState && !disabled) {
                if (showAfter > 0) delay(showAfter)
                setVisible(true)
            } else {
                if (hideAfter > 0) delay(hideAfter)
                setVisible(false)
            }
        }
    }
    val focusModifier = Modifier.onFocusChanged { focus ->
        if (trigger == ElTooltipTrigger.Focus && visible == null) setVisible(focus.isFocused && !disabled)
    }
    val contextMenuModifier = if (trigger == ElTooltipTrigger.ContextMenu) {
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
            .elTriggerPress(trigger == ElTooltipTrigger.Click && !disabled) {
                setVisible(!(visible ?: internalVisible))
            },
    ) {
        content()
        ElPopper(
            visible = (visible ?: internalVisible) && !disabled,
            onDismissRequest = { setVisible(false) },
            placement = placement,
            offset = offset,
            dismissOnClickOutside = trigger == ElTooltipTrigger.ContextMenu,
        ) {
            val dark = effect == ElTooltipEffect.Dark
            val arrowColor = if (dark) colors.textPrimary else colors.backgroundOverlay
            val tooltipBody: @Composable () -> Unit = {
                Box(
                    Modifier
                        .then(if (dark) Modifier.background(colors.textPrimary, RoundedCornerShape(4.dp)) else Modifier)
                        .widthIn(max = maxWidth)
                        .then(if (!dark) Modifier.background(colors.backgroundOverlay, RoundedCornerShape(4.dp)) else Modifier)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                ) {
                    if (tooltip != null) tooltip() else BasicText(
                        text = text ?: "",
                        style = theme.typography.extraSmall.copy(color = if (dark) colors.white else colors.textRegular),
                    )
                }
            }
            if (showArrow) {
                when {
                    placement.isTop -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        tooltipBody(); ElPopperArrow(placement, arrowColor)
                    }
                    placement.isBottom -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ElPopperArrow(placement, arrowColor); tooltipBody()
                    }
                    placement.isLeft -> Row(verticalAlignment = Alignment.CenterVertically) {
                        tooltipBody(); ElPopperArrow(placement, arrowColor)
                    }
                    else -> Row(verticalAlignment = Alignment.CenterVertically) {
                        ElPopperArrow(placement, arrowColor); tooltipBody()
                    }
                }
            } else {
                tooltipBody()
            }
        }
    }
}

