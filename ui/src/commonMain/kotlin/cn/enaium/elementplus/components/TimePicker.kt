package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.internal.rememberElInteractionState
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.datetime.LocalTime

/** `<el-time-picker>`: spinner based time selection with confirm/cancel. */
@Composable
fun ElTimePicker(
    value: LocalTime?,
    onValueChange: (LocalTime?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: cn.enaium.elementplus.theme.ElSize? = null,
    format: String = "HH:mm:ss",
    disabled: Boolean = false,
    clearable: Boolean = true,
    showSeconds: Boolean = true,
    onVisibleChange: ((Boolean) -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    var expanded by remember { mutableStateOf(false) }
    var draft by remember(value, expanded) { mutableStateOf(value ?: LocalTime(0, 0)) }
    fun setExpanded(next: Boolean) {
        expanded = next
        if (next) draft = value ?: LocalTime(0, 0)
        onVisibleChange?.invoke(next)
    }
    Box(modifier) {
        DateField(
            display = value?.let { formatLocalTime(it, format) },
            placeholder = placeholder ?: LocalElConfig.current.locale.timePicker.selectTime,
            onClick = { setExpanded(true) },
            onClear = if (clearable) ({ onValueChange(null) }) else null,
            modifier = Modifier,
            size = size,
            disabled = disabled,
            icon = cn.enaium.elementplus.icons.ElIcons.Clock,
        )
        ElPopper(
            visible = expanded,
            onDismissRequest = { setExpanded(false) },
            placement = ElPlacement.BottomStart,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 4.dp),
            focusable = true,
        ) {
            ElPopperSurface(Modifier.padding(10.dp)) {
                Column(Modifier.width(240.dp)) {
                    BasicText(
                        text = locale.timePicker.selectTime,
                        style = theme.typography.small.copy(color = colors.textSecondary),
                    )
                    Row(Modifier.fillMaxWidth().height(196.dp).padding(top = 6.dp)) {
                        TimeSpinner(24, draft.hour, Modifier.weight(1f)) { hour ->
                            draft = LocalTime(hour, draft.minute, draft.second)
                        }
                        TimeSpinner(60, draft.minute, Modifier.weight(1f)) { minute ->
                            draft = LocalTime(draft.hour, minute, draft.second)
                        }
                        if (showSeconds) {
                            TimeSpinner(60, draft.second, Modifier.weight(1f)) { second ->
                                draft = LocalTime(draft.hour, draft.minute, second)
                            }
                        }
                    }
                    Row(
                        Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BasicText(
                            text = formatLocalTime(draft, format),
                            style = theme.typography.small.copy(color = colors.primary),
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            BasicText(
                                text = locale.timePicker.cancel,
                                modifier = Modifier.clickable { setExpanded(false) }.padding(4.dp),
                                style = theme.typography.small.copy(color = colors.textRegular),
                            )
                            BasicText(
                                text = locale.timePicker.confirm,
                                modifier = Modifier
                                    .clickable {
                                        onValueChange(draft)
                                        setExpanded(false)
                                    }
                                    .padding(4.dp),
                                style = theme.typography.small.copy(color = colors.primary, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * `<el-time-select>`: a list of generated times between [start] and [end].
 */
@Composable
fun ElTimeSelect(
    value: LocalTime?,
    onValueChange: (LocalTime?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: cn.enaium.elementplus.theme.ElSize? = null,
    start: LocalTime = LocalTime(9, 0),
    end: LocalTime = LocalTime(18, 0),
    step: Int = 30,
    disabled: Boolean = false,
    clearable: Boolean = true,
    format: String = "HH:mm",
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    var expanded by remember { mutableStateOf(false) }
    var hovered by remember { mutableStateOf(false) }
    val options = remember(start, end, step) {
        buildList {
            var current = start.hour * 60 + start.minute
            val limit = end.hour * 60 + end.minute
            while (current <= limit) {
                add(LocalTime(current / 60, current % 60, 0))
                current += step.coerceAtLeast(1)
            }
        }
    }
    Box(modifier) {
        Box(Modifier.fillMaxWidth()) {
            DateField(
                display = value?.let { formatLocalTime(it, format) },
                placeholder = placeholder ?: LocalElConfig.current.locale.timePicker.selectTime,
                onClick = { expanded = true },
                onClear = if (clearable) ({ onValueChange(null) }) else null,
                modifier = Modifier,
                size = size,
                disabled = disabled,
                icon = cn.enaium.elementplus.icons.ElIcons.Clock,
            )
        }
        ElPopper(
            visible = expanded,
            onDismissRequest = { expanded = false },
            placement = ElPlacement.BottomStart,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 4.dp),
            focusable = true,
        ) {
            ElPopperSurface(Modifier.width(180.dp)) {
                Column(Modifier.heightIn(max = 274.dp).verticalScroll(rememberScrollState())) {
                    options.forEach { option ->
                        val selected = option == value
                        val interaction = remember(option) { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        val isHovered = rememberElInteractionState(interaction).hovered
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    when {
                                        selected -> colors.light(cn.enaium.elementplus.theme.ElType.Primary, 9)
                                        isHovered -> colors.fillLight
                                        else -> colors.backgroundOverlay
                                    }
                                )
                                .clickable {
                                    onValueChange(option)
                                    expanded = false
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                        ) {
                            BasicText(
                                text = formatLocalTime(option, format),
                                style = theme.typography.base.copy(
                                    color = if (selected) colors.primary else colors.textRegular,
                                    fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.Medium
                                    else androidx.compose.ui.text.font.FontWeight.Normal,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun Modifier.hoverState(onChange: (Boolean) -> Unit): Modifier = this
