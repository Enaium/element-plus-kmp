package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElTheme

data class ElMentionOption(
    val value: String,
    val label: String = value,
    val disabled: Boolean = false,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
)

/**
 * `<el-mention>`: textarea with `@` style suggestion popups. The active query
 * is derived from the last trigger after the last split token.
 */
@Composable
fun ElMention(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<ElMentionOption>,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: ElSize? = null,
    disabled: Boolean = false,
    trigger: Char = '@',
    split: String = " ",
    minLines: Int = 2,
    maxLines: Int = 4,
) {
    val theme = LocalElTheme.current
    val resolvedSize = resolveElSize(size)
    var focused by remember { mutableStateOf(false) }
    val triggerIndex = value.lastIndexOf(trigger)
    val lastSplit = value.lastIndexOf(split)
    val active = focused && triggerIndex >= 0 && triggerIndex > lastSplit
    val query = if (active) value.substring(triggerIndex + 1) else ""
    val filtered = remember(options, query, active) {
        if (!active) emptyList() else options.filter {
            it.label.contains(query, ignoreCase = true) || it.value.contains(query, ignoreCase = true)
        }
    }

    Box(modifier) {
        ElInput(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = placeholder,
            size = resolvedSize,
            type = ElInputType.Textarea,
            disabled = disabled,
            minLines = minLines,
            maxLines = maxLines,
            onFocusChanged = { focused = it },
        )
        ElPopper(
            visible = active && filtered.isNotEmpty(),
            onDismissRequest = { },
            placement = ElPlacement.BottomStart,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 4.dp),
            dismissOnClickOutside = false,
        ) {
            ElPopperSurface(modifier = Modifier.width(240.dp)) {
                Column(Modifier.heightIn(max = 260.dp)) {
                    filtered.forEach { option ->
                        val source = remember(option) { MutableInteractionSource() }
                        val hovered by source.collectIsHoveredAsState()
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(if (hovered) theme.colors.fillLight else theme.colors.backgroundOverlay)
                                .clickable(enabled = !option.disabled) {
                                    if (!option.disabled) {
                                        val prefix = if (triggerIndex >= 0) value.substring(0, triggerIndex) else value
                                        onValueChange("$prefix$trigger${option.value}$split")
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (option.icon != null) {
                                ElIcon(option.icon, size = 16.dp)
                                Spacer(Modifier.width(8.dp))
                            }
                            BasicText(
                                text = option.label,
                                style = theme.typography.base.copy(
                                    fontSize = resolvedSize.fontSize,
                                    color = if (option.disabled) theme.colors.textDisabled
                                    else if (hovered) theme.colors.primary else theme.colors.textRegular,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}
