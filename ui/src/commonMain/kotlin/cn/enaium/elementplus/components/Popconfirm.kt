package cn.enaium.elementplus.components

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column as LayoutColumn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.elTriggerPress
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperArrow
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay

/** `<el-popconfirm>` confirmation bubble. */
@Composable
fun ElPopconfirm(
    modifier: Modifier = Modifier,
    title: String? = null,
    message: String? = null,
    confirmText: String? = null,
    cancelText: String? = null,
    type: ElType = ElType.Warning,
    placement: ElPlacement = ElPlacement.Top,
    width: Dp = 180.dp,
    disabled: Boolean = false,
    trigger: ElPopoverTrigger = ElPopoverTrigger.Click,
    confirmButtonType: ElType = ElType.Primary,
    cancelButtonType: ElType = ElType.Default,
    icon: ImageVector? = null,
    hideIcon: Boolean = false,
    iconColor: Color? = null,
    showArrow: Boolean = true,
    offset: DpOffset = DpOffset(0.dp, 8.dp),
    showAfter: Long = 0,
    hideAfter: Long = 200,
    visible: Boolean? = null,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    onConfirm: () -> Unit,
    onCancel: (() -> Unit)? = null,
    triggerContent: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedTitle = title ?: locale.popconfirm.title
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
    Box(
        modifier
            .then(focusModifier)
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
            focusable = true,
        ) {
            val arrowColor = colors.backgroundOverlay
            val body: @Composable () -> Unit = {
                ElPopperSurface(Modifier.width(width)) {
                    Column(Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!hideIcon) {
                                ElIcon(
                                    icon ?: if (type == ElType.Danger) ElIcons.CircleCloseFilled else ElIcons.WarningFilled,
                                    size = 16.dp,
                                    tint = iconColor ?: colors.colorOf(type),
                                )
                                Spacer(Modifier.width(6.dp))
                            }
                            BasicText(
                                text = resolvedTitle,
                                style = theme.typography.small.copy(color = colors.textPrimary),
                            )
                        }
                        if (message != null) {
                            BasicText(
                                text = message,
                                modifier = Modifier.padding(top = 6.dp),
                                style = theme.typography.extraSmall.copy(color = colors.textSecondary),
                            )
                        }
                        Row(
                            Modifier.fillMaxWidth().padding(top = 12.dp),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            ElButton(
                                onClick = { setVisible(false); onCancel?.invoke() },
                                text = cancelText ?: locale.popconfirm.cancelButtonText,
                                type = cancelButtonType,
                                size = ElSize.Small,
                            )
                            Spacer(Modifier.width(6.dp))
                            ElButton(
                                onClick = { setVisible(false); onConfirm() },
                                text = confirmText ?: locale.popconfirm.confirmButtonText,
                                type = confirmButtonType,
                                size = ElSize.Small,
                            )
                        }
                    }
                }
            }
            if (showArrow) {
                when {
                    placement.isTop -> LayoutColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                        body(); ElPopperArrow(placement, arrowColor)
                    }
                    placement.isBottom -> LayoutColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                        ElPopperArrow(placement, arrowColor); body()
                    }
                    placement.isLeft -> Row(verticalAlignment = Alignment.CenterVertically) {
                        body(); ElPopperArrow(placement, arrowColor)
                    }
                    else -> Row(verticalAlignment = Alignment.CenterVertically) {
                        ElPopperArrow(placement, arrowColor); body()
                    }
                }
            } else {
                body()
            }
        }
    }
}
