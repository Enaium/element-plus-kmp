package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElTheme

data class ElCheckboxOption(
    val value: String,
    val label: String,
    val disabled: Boolean = false,
)

enum class ElCheckboxDirection { Horizontal, Vertical }

/**
 * `<el-checkbox>`, including indeterminate and bordered modes.
 */
@Composable
fun ElCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    indeterminate: Boolean = false,
    disabled: Boolean = false,
    size: ElSize? = null,
    border: Boolean = false,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val resolvedSize = resolveElSize(size)
    val active = checked || indeterminate
    val boxSize = when (resolvedSize) {
        ElSize.Large -> 16.dp
        ElSize.Default -> 14.dp
        ElSize.Small -> 12.dp
    }
    val shape = RoundedCornerShape(theme.radius.small)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val contentColor = when {
        disabled -> colors.disabledText
        active || (hovered && !disabled) -> colors.primary
        else -> colors.textRegular
    }
    Row(
        modifier = modifier
            .then(
                if (border) Modifier
                    .border(
                        1.dp,
                        when {
                            disabled -> colors.disabledBorder
                            active || hovered -> colors.primary
                            else -> colors.border
                        },
                        shape,
                    )
                    .clip(shape)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                else Modifier
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = !disabled,
            ) { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(boxSize)
                .background(
                    when {
                        disabled && active -> colors.fillLight
                        active -> colors.primary
                        else -> colors.fillBlank
                    },
                    shape,
                )
                .border(
                    1.dp,
                    when {
                        disabled -> colors.disabledBorder
                        active || (hovered && !disabled) -> colors.primary
                        else -> colors.border
                    },
                    shape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (indeterminate) {
                Box(Modifier.width(boxSize - 5.dp).height(2.dp).background(if (disabled) colors.disabledText else colors.white))
            } else if (checked) {
                ElIcon(
                    ElIcons.Check,
                    size = boxSize - 4.dp,
                    tint = if (disabled) colors.disabledText else colors.white,
                )
            }
        }
        if (label != null) {
            Spacer(Modifier.width(8.dp))
            BasicText(
                text = label,
                style = theme.typography.base.copy(
                    fontSize = resolvedSize.fontSize,
                    color = contentColor,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
    }
}

/**
 * List-based checkbox group. Each option keeps its own state in [selected].
 */
@Composable
fun ElCheckboxGroup(
    options: List<ElCheckboxOption>,
    selected: List<String>,
    onSelectedChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    direction: ElCheckboxDirection = ElCheckboxDirection.Horizontal,
    disabled: Boolean = false,
    min: Int = 0,
    max: Int = Int.MAX_VALUE,
    size: ElSize? = null,
    button: Boolean = false,
    border: Boolean = false,
) {
    val toggle: (String) -> Unit = { value ->
        val next = if (priorityContains(selected, value)) selected - value else {
            if (selected.size >= max) selected else selected + value
        }
        if (next.size >= min) onSelectedChange(next)
    }
    if (button) {
        Row(modifier, horizontalArrangement = Arrangement.spacedBy((-1).dp)) {
            options.forEach { option ->
                ElCheckboxButton(
                    label = option.label,
                    checked = priorityContains(selected, option.value),
                    enabled = !option.disabled && !disabled && (priorityContains(selected, option.value) || selected.size < max),
                    onClick = { toggle(option.value) },
                )
            }
        }
        return
    }
    if (direction == ElCheckboxDirection.Vertical) {
        Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                ElCheckbox(
                    checked = priorityContains(selected, option.value),
                    onCheckedChange = { toggle(option.value) },
                    label = option.label,
                    disabled = disabled || option.disabled,
                    size = size,
                    border = border,
                )
            }
        }
    } else {
        Row(modifier, horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            options.forEach { option ->
                ElCheckbox(
                    checked = priorityContains(selected, option.value),
                    onCheckedChange = { toggle(option.value) },
                    label = option.label,
                    disabled = disabled || option.disabled,
                    size = size,
                    border = border,
                )
            }
        }
    }
}

@Composable
fun ElCheckboxButton(
    label: String,
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val fg = when {
        !enabled -> colors.disabledText
        checked -> colors.white
        else -> colors.textRegular
    }
    val bg = when {
        !enabled -> colors.fillLight
        checked -> colors.primary
        else -> colors.fillBlank
    }
    val borderColor = when {
        !enabled -> colors.disabledBorder
        checked -> colors.primary
        else -> colors.border
    }
    Box(
        modifier = modifier
            .height(32.dp)
            .background(bg)
            .border(1.dp, borderColor)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = label,
            style = theme.typography.base.copy(color = fg, fontWeight = FontWeight.Medium),
        )
    }
}

internal fun priorityContains(list: List<String>, value: String): Boolean = list.contains(value)
