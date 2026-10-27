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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElTheme

data class ElRadioOption(
    val value: String,
    val label: String,
    val disabled: Boolean = false,
)

enum class ElRadioDirection { Horizontal, Vertical }

/** `<el-radio>`. */
@Composable
fun ElRadio(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    disabled: Boolean = false,
    size: ElSize? = null,
    border: Boolean = false,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val resolvedSize = resolveElSize(size)
    val outer = when (resolvedSize) {
        ElSize.Large -> 16.dp
        ElSize.Default -> 14.dp
        ElSize.Small -> 12.dp
    }
    val shape = CircleShape
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val borderColor = when {
        disabled -> colors.disabledBorder
        checked || (hovered && !disabled) -> colors.primary
        else -> colors.border
    }
    Row(
        modifier = modifier
            .then(
                if (border) Modifier
                    .border(
                        1.dp,
                        if (checked || (hovered && !disabled)) colors.primary else colors.border,
                        androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                    )
                    .padding(horizontal = 12.dp, vertical = 5.dp)
                else Modifier
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = !disabled,
            ) { onCheckedChange(true) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(outer)
                .background(if (disabled) colors.fillLight else colors.fillBlank, shape)
                .border(1.dp, borderColor, shape),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Box(
                    Modifier
                        .size(outer / 2)
                        .background(if (disabled) colors.disabledText else colors.primary, shape),
                )
            }
        }
        if (label != null) {
            Spacer(Modifier.width(8.dp))
            BasicText(
                text = label,
                style = theme.typography.base.copy(
                    fontSize = resolvedSize.fontSize,
                    color = if (disabled) colors.disabledText
                    else if (checked || hovered) colors.primary
                    else colors.textRegular,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
    }
}

/** List-based `<el-radio-group>` with optional button styling. */
@Composable
fun ElRadioGroup(
    options: List<ElRadioOption>,
    selected: String?,
    onSelectedChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    direction: ElRadioDirection = ElRadioDirection.Horizontal,
    disabled: Boolean = false,
    size: ElSize? = null,
    button: Boolean = false,
    border: Boolean = false,
) {
    if (button) {
        Row(modifier, horizontalArrangement = Arrangement.spacedBy((-1).dp)) {
            options.forEach { option ->
                ElRadioButton(
                    label = option.label,
                    checked = selected == option.value,
                    enabled = !option.disabled && !disabled,
                    onClick = { onSelectedChange(option.value) },
                )
            }
        }
        return
    }
    val item: @Composable (ElRadioOption) -> Unit = { option ->
        ElRadio(
            checked = selected == option.value,
            onCheckedChange = { if (it) onSelectedChange(option.value) },
            label = option.label,
            disabled = disabled || option.disabled,
            size = size,
            border = border,
        )
    }
    if (direction == ElRadioDirection.Vertical) {
        Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option -> item(option) }
        }
    } else {
        Row(modifier, horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            options.forEach { option -> item(option) }
        }
    }
}

@Composable
fun ElRadioButton(
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
    Box(
        modifier = modifier
            .height(32.dp)
            .background(if (enabled && checked) colors.primary else if (!enabled) colors.fillLight else colors.fillBlank)
            .border(1.dp, if (enabled && checked) colors.primary else if (!enabled) colors.disabledBorder else colors.border)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(text = label, style = theme.typography.base.copy(color = fg, fontWeight = FontWeight.Medium))
    }
}
