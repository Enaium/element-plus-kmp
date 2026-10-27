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

/** `<el-radio>`. [content] replaces the label text, the Compose equivalent of the default slot. */
@Composable
fun ElRadio(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    disabled: Boolean = false,
    size: ElSize? = null,
    border: Boolean = false,
    content: (@Composable () -> Unit)? = null,
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
        if (content != null) {
            Spacer(Modifier.width(8.dp))
            content()
        } else if (label != null) {
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

/**
 * List-based `<el-radio-group>` with optional button styling.
 *
 * @param optionContent replaces each option's label, the Compose equivalent of
 *   a per-item default slot.
 * @param fill / [textColor] override the checked button color pair.
 */
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
    optionContent: (@Composable (ElRadioOption) -> Unit)? = null,
    fill: Color? = null,
    textColor: Color? = null,
) {
    if (button) {
        Row(modifier, horizontalArrangement = Arrangement.spacedBy((-1).dp)) {
            options.forEach { option ->
                val slot: (@Composable () -> Unit)? = if (optionContent != null) {
                    { optionContent(option) }
                } else {
                    null
                }
                ElRadioButton(
                    label = option.label,
                    checked = selected == option.value,
                    enabled = !option.disabled && !disabled,
                    onClick = { onSelectedChange(option.value) },
                    size = size,
                    fill = fill,
                    textColor = textColor,
                    content = slot,
                )
            }
        }
        return
    }
    val item: @Composable (ElRadioOption) -> Unit = { option ->
        val slot: (@Composable () -> Unit)? = if (optionContent != null) {
            { optionContent(option) }
        } else {
            null
        }
        ElRadio(
            checked = selected == option.value,
            onCheckedChange = { if (it) onSelectedChange(option.value) },
            label = option.label,
            disabled = disabled || option.disabled,
            size = size,
            border = border,
            content = slot,
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

/** `<el-radio-button>`; [fill] and [textColor] are the checked color pair. */
@Composable
fun ElRadioButton(
    label: String,
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: ElSize? = null,
    fill: Color? = null,
    textColor: Color? = null,
    content: (@Composable () -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val resolvedSize = resolveElSize(size)
    val checkedColor = fill ?: colors.primary
    val checkedText = textColor ?: colors.white
    val fg = when {
        !enabled -> colors.disabledText
        checked -> checkedText
        else -> colors.textRegular
    }
    Box(
        modifier = modifier
            .height(resolvedSize.height)
            .background(if (enabled && checked) checkedColor else if (!enabled) colors.fillLight else colors.fillBlank)
            .border(1.dp, if (enabled && checked) checkedColor else if (!enabled) colors.disabledBorder else colors.border)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = resolvedSize.horizontalPadding),
        contentAlignment = Alignment.Center,
    ) {
        if (content != null) {
            content()
        } else {
            BasicText(
                text = label,
                style = theme.typography.base.copy(
                    fontSize = resolvedSize.fontSize,
                    color = fg,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
    }
}

/**
 * `<el-radio-group>` over arbitrary items with selector aliases, the Compose
 * equivalent of the `props` option (`{ value, label, disabled }` keys) and of
 * non-string option values.
 */
@Composable
fun <T> ElRadioGroup(
    items: List<T>,
    selected: T?,
    onSelectedChange: (T) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
    itemDisabled: (T) -> Boolean = { false },
    itemContent: (@Composable (T) -> Unit)? = null,
    direction: ElRadioDirection = ElRadioDirection.Horizontal,
    disabled: Boolean = false,
    size: ElSize? = null,
    button: Boolean = false,
    border: Boolean = false,
    fill: Color? = null,
    textColor: Color? = null,
) {
    if (button) {
        Row(modifier, horizontalArrangement = Arrangement.spacedBy((-1).dp)) {
            items.forEach { item ->
                val slot: (@Composable () -> Unit)? = if (itemContent != null) {
                    { itemContent(item) }
                } else {
                    null
                }
                ElRadioButton(
                    label = itemLabel(item),
                    checked = selected == item,
                    enabled = !itemDisabled(item) && !disabled,
                    onClick = { onSelectedChange(item) },
                    size = size,
                    fill = fill,
                    textColor = textColor,
                    content = slot,
                )
            }
        }
        return
    }
    val itemComposable: @Composable (T) -> Unit = { item ->
        val slot: (@Composable () -> Unit)? = if (itemContent != null) {
            ({ itemContent(item) })
        } else {
            null
        }
        ElRadio(
            checked = selected == item,
            onCheckedChange = { if (it) onSelectedChange(item) },
            label = itemLabel(item),
            disabled = disabled || itemDisabled(item),
            size = size,
            border = border,
            content = slot,
        )
    }
    if (direction == ElRadioDirection.Vertical) {
        Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach { itemComposable(it) }
        }
    } else {
        Row(
            modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { itemComposable(it) }
        }
    }
}
