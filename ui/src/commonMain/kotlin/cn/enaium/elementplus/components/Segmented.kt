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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

/** `<el-segmented>` with animated selected pill, icons, block and vertical modes. */
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
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val resolvedSize = resolveElSize(size)
    val height = resolvedSize.height
    val shape = RoundedCornerShape(theme.radius.base)
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
            ColumnSegmentItems(options, value, onValueChange, height, resolvedSize, block, disabled)
        }
    } else {
        Row(containerModifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            RowSegmentItems(options, value, onValueChange, height, resolvedSize, block, disabled)
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
) {
    options.forEach { option ->
        SegmentItem(
            option = option,
            selected = option.value == value,
            enabled = !disabled && !option.disabled,
            height = height,
            resolvedSize = resolvedSize,
            onValueChange = onValueChange,
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
) {
    options.forEach { option ->
        SegmentItem(
            option = option,
            selected = option.value == value,
            enabled = !disabled && !option.disabled,
            height = height,
            resolvedSize = resolvedSize,
            onValueChange = onValueChange,
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
    modifier: Modifier = Modifier,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val bg by animateColorAsState(
        if (selected) colors.background else colors.fillLight.copy(alpha = 0f),
        label = "segmented-bg",
    )
    val fg by animateColorAsState(
        if (!enabled) colors.textDisabled else if (selected) colors.textPrimary else colors.textRegular,
        label = "segmented-fg",
    )
    val optionShape = RoundedCornerShape(theme.radius.base - 1.dp)
    Row(
        modifier = modifier
            .clip(optionShape)
            .background(bg, optionShape)
            .clickable(enabled = enabled) { onValueChange(option.value) }
            .height(height - 4.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
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
