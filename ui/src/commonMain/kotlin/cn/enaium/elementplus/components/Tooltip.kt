package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay

enum class ElTooltipEffect { Dark, Light }

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
    visible: Boolean? = null,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    tooltip: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    var hovered by remember { mutableStateOf(false) }
    var internalVisible by remember { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }
    val hoverState by interaction.collectIsHoveredAsState()
    LaunchedEffect(hoverState, disabled) {
        hovered = hoverState
        if (visible == null) {
            if (hoverState && !disabled) {
                if (showAfter > 0) delay(showAfter)
                internalVisible = true
            } else {
                if (hideAfter > 0) delay(hideAfter)
                internalVisible = false
            }
            onVisibleChange?.invoke(internalVisible)
        }
    }
    Box(modifier.hoverable(interaction)) {
        content()
        ElPopper(
            visible = (visible ?: internalVisible) && !disabled,
            onDismissRequest = {
                if (visible == null) internalVisible = false
                onVisibleChange?.invoke(false)
            },
            placement = placement,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 6.dp),
            dismissOnClickOutside = false,
        ) {
            val dark = effect == ElTooltipEffect.Dark
            if (dark) {
                Box(
                    Modifier
                        .background(colors.textPrimary, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                ) {
                    if (tooltip != null) tooltip() else BasicText(
                        text = text ?: "",
                        style = theme.typography.extraSmall.copy(color = colors.white),
                    )
                }
            } else {
                ElPopperSurface(
                    modifier = Modifier.widthIn(max = maxWidth),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                ) {
                    if (tooltip != null) tooltip() else BasicText(
                        text = text ?: "",
                        style = theme.typography.extraSmall.copy(color = colors.textRegular),
                    )
                }
            }
        }
    }
}

