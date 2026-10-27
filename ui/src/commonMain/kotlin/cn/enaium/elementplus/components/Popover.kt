package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElPopoverTrigger { Click, Hover }

/** `<el-popover>` with optional title and rich body content. */
@Composable
fun ElPopover(
    modifier: Modifier = Modifier,
    title: String? = null,
    trigger: ElPopoverTrigger = ElPopoverTrigger.Click,
    placement: ElPlacement = ElPlacement.Bottom,
    width: Dp = 180.dp,
    disabled: Boolean = false,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    triggerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    var visible by remember { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    if (trigger == ElPopoverTrigger.Hover) {
        LaunchedEffect(hovered, disabled) {
            if (!disabled) {
                visible = hovered
                onVisibleChange?.invoke(visible)
            }
        }
    }
    Box(modifier.hoverable(interaction)) {
        triggerContent()
        if (trigger == ElPopoverTrigger.Click) {
            Box(
                Modifier.matchParentSize().clickable(enabled = !disabled) {
                    visible = !visible
                    onVisibleChange?.invoke(visible)
                },
            )
        }
        ElPopper(
            visible = visible && !disabled,
            onDismissRequest = {
                visible = false
                onVisibleChange?.invoke(false)
            },
            placement = placement,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 8.dp),
            focusable = trigger == ElPopoverTrigger.Click,
        ) {
            ElPopperSurface(Modifier.width(width)) {
                Column {
                    if (title != null) {
                        BasicText(
                            text = title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.fillLight)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            style = theme.typography.small.copy(
                                color = colors.textPrimary,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                            ),
                        )
                    }
                    Box(Modifier.fillMaxWidth().padding(12.dp)) { content() }
                }
            }
        }
    }
}
