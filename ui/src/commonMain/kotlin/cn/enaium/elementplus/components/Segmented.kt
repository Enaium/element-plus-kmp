package cn.enaium.elementplus.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElTheme

data class ElSegmentedOption<T>(
    val value: T,
    val label: String,
    val icon: ImageVector? = null,
    val disabled: Boolean = false,
)

/**
 * `<el-segmented>` with animated selected pill, icons, block and vertical modes.
 * [optionContent] renders arbitrary per-option content, and the selected colors
 * and corner radius mirror the upstream CSS variables.
 */
@Composable
fun <T> ElSegmented(
    options: List<ElSegmentedOption<T>>,
    value: T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    size: ElSize? = null,
    block: Boolean = false,
    vertical: Boolean = false,
    disabled: Boolean = false,
    optionContent: (@Composable (ElSegmentedOption<T>) -> Unit)? = null,
    selectedColor: Color? = null,
    selectedBackgroundColor: Color? = null,
    cornerRadius: Dp? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val resolvedSize = resolveElSize(size)
    val height = resolvedSize.height
    val shape = RoundedCornerShape(cornerRadius ?: theme.radius.base)
    val containerModifier = modifier
        .then(
            when {
                !block -> Modifier
                vertical -> Modifier.fillMaxHeight()
                else -> Modifier.fillMaxWidth()
            }
        )
        .background(colors.fillLight, shape)
        .padding(2.dp)
    if (vertical) {
        Column(containerModifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            ColumnSegmentItems(
                options, value, onValueChange, height, resolvedSize, block, disabled,
                optionContent, selectedColor, selectedBackgroundColor, cornerRadius,
            )
        }
    } else {
        Row(containerModifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            RowSegmentItems(
                options, value, onValueChange, height, resolvedSize, block, disabled,
                optionContent, selectedColor, selectedBackgroundColor, cornerRadius,
            )
        }
    }
}

@Composable
private fun <T> RowScope.RowSegmentItems(
    options: List<ElSegmentedOption<T>>,
    value: T,
    onValueChange: (T) -> Unit,
    height: Dp,
    resolvedSize: ElSize,
    block: Boolean,
    disabled: Boolean,
    optionContent: (@Composable (ElSegmentedOption<T>) -> Unit)?,
    selectedColor: Color?,
    selectedBackgroundColor: Color?,
    cornerRadius: Dp?,
) {
    options.forEach { option ->
        SegmentItem(
            option = option,
            selected = option.value == value,
            enabled = !disabled && !option.disabled,
            height = height,
            resolvedSize = resolvedSize,
            onValueChange = onValueChange,
            optionContent = optionContent,
            selectedColor = selectedColor,
            selectedBackgroundColor = selectedBackgroundColor,
            cornerRadius = cornerRadius,
            modifier = if (block) Modifier.weight(1f) else Modifier,
        )
    }
}

@Composable
private fun <T> ColumnScope.ColumnSegmentItems(
    options: List<ElSegmentedOption<T>>,
    value: T,
    onValueChange: (T) -> Unit,
    height: Dp,
    resolvedSize: ElSize,
    block: Boolean,
    disabled: Boolean,
    optionContent: (@Composable (ElSegmentedOption<T>) -> Unit)?,
    selectedColor: Color?,
    selectedBackgroundColor: Color?,
    cornerRadius: Dp?,
) {
    options.forEach { option ->
        SegmentItem(
            option = option,
            selected = option.value == value,
            enabled = !disabled && !option.disabled,
            height = height,
            resolvedSize = resolvedSize,
            onValueChange = onValueChange,
            optionContent = optionContent,
            selectedColor = selectedColor,
            selectedBackgroundColor = selectedBackgroundColor,
            cornerRadius = cornerRadius,
            modifier = if (block) Modifier.weight(1f) else Modifier,
        )
    }
}

@Composable
private fun <T> SegmentItem(
    option: ElSegmentedOption<T>,
    selected: Boolean,
    enabled: Boolean,
    height: Dp,
    resolvedSize: ElSize,
    onValueChange: (T) -> Unit,
    optionContent: (@Composable (ElSegmentedOption<T>) -> Unit)?,
    selectedColor: Color?,
    selectedBackgroundColor: Color?,
    cornerRadius: Dp?,
    modifier: Modifier = Modifier,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val bg by animateColorAsState(
        if (selected) selectedBackgroundColor ?: colors.background else colors.fillLight.copy(alpha = 0f),
        label = "segmented-bg",
    )
    val fg by animateColorAsState(
        when {
            !enabled -> colors.textDisabled
            selected -> selectedColor ?: colors.textPrimary
            else -> colors.textRegular
        },
        label = "segmented-fg",
    )
    val baseRadius = cornerRadius ?: theme.radius.base
    val optionShape = RoundedCornerShape((baseRadius - 1.dp).coerceAtLeast(0.dp))
    Row(
        modifier = modifier
            .clip(optionShape)
            .background(bg, optionShape)
            .clickable(enabled = enabled) { onValueChange(option.value) }
            .then(
                if (optionContent != null) Modifier.heightIn(min = height - 4.dp)
                else Modifier.height(height - 4.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (optionContent != null) {
            optionContent(option)
        } else {
            if (option.icon != null) {
                ElIcon(option.icon, size = 14.dp, tint = fg)
                Spacer(Modifier.padding(end = 6.dp))
            }
            BasicText(
                text = option.label,
                style = theme.typography.base.copy(
                    fontSize = resolvedSize.fontSize,
                    color = fg,
                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                ),
            )
        }
    }
}
