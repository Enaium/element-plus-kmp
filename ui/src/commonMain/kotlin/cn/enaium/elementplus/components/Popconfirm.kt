package cn.enaium.elementplus.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

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
    onConfirm: () -> Unit,
    onCancel: (() -> Unit)? = null,
    triggerContent: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedTitle = title ?: locale.popconfirm.title
    var visible by remember { mutableStateOf(false) }
    Box(modifier) {
        triggerContent()
        Box(Modifier.matchParentSize().clickable(enabled = !disabled) { visible = !visible })
        ElPopper(
            visible = visible && !disabled,
            onDismissRequest = { visible = false },
            placement = placement,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 8.dp),
            focusable = true,
        ) {
            ElPopperSurface(Modifier.width(width)) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ElIcon(
                            if (type == ElType.Danger) ElIcons.CircleCloseFilled else ElIcons.WarningFilled,
                            size = 16.dp,
                            tint = colors.colorOf(type),
                        )
                        Spacer(Modifier.width(6.dp))
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
                            onClick = { visible = false; onCancel?.invoke() },
                            text = cancelText ?: locale.popconfirm.cancelButtonText,
                            size = ElSize.Small,
                        )
                        androidx.compose.foundation.layout.Spacer(Modifier.width(6.dp))
                        ElButton(
                            onClick = { visible = false; onConfirm() },
                            text = confirmText ?: locale.popconfirm.confirmButtonText,
                            type = ElType.Primary,
                            size = ElSize.Small,
                        )
                    }
                }
            }
        }
    }
}
