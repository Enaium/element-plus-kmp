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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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

/** Ambient state shared with [ElCheckbox] children of a composed group. */
@Stable
internal class ElCheckboxGroupContext(
    val selected: () -> List<String>,
    val toggle: (String) -> Unit,
    val disabled: Boolean,
    val size: ElSize?,
    val border: Boolean,
    val button: Boolean,
    val fill: Color?,
    val textColor: Color?,
)

internal val LocalElCheckboxGroup = staticCompositionLocalOf<ElCheckboxGroupContext?> { null }

/**
 * `<el-checkbox>`, including indeterminate and bordered modes.
 *
 * Inside a group created with the content-based [ElCheckboxGroup] overload,
 * [value] is bound to the group selection automatically.
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
    value: String? = null,
    name: String? = null,
    ariaLabel: String? = null,
) {
    val group = LocalElCheckboxGroup.current
    val boundValue = value ?: label
    ElCheckboxVisual(
        checked = if (group != null && boundValue != null) group.selected().contains(boundValue) else checked,
        indeterminate = indeterminate,
        disabled = disabled || group?.disabled == true,
        size = size ?: group?.size,
        border = border || group?.border == true,
        button = group?.button == true,
        label = label,
        fill = group?.fill,
        textColor = group?.textColor,
        accessibilityLabel = ariaLabel ?: name,
        modifier = modifier,
    ) {
        if (group != null && boundValue != null) group.toggle(boundValue) else onCheckedChange(!checked)
    }
}

/**
 * A `<el-checkbox>` bound to the enclosing composed [ElCheckboxGroup] through
 * [value]; the group owns the checked state, so no explicit state is needed.
 */
@Composable
fun ElCheckbox(
    value: String,
    label: String? = null,
    modifier: Modifier = Modifier,
    indeterminate: Boolean = false,
    disabled: Boolean = false,
    size: ElSize? = null,
    border: Boolean = false,
    name: String? = null,
    ariaLabel: String? = null,
) {
    val group = LocalElCheckboxGroup.current
    ElCheckboxVisual(
        checked = group?.selected()?.contains(value) == true,
        indeterminate = indeterminate,
        disabled = disabled || group?.disabled == true,
        size = size ?: group?.size,
        border = border || group?.border == true,
        button = group?.button == true,
        label = label,
        fill = group?.fill,
        textColor = group?.textColor,
        accessibilityLabel = ariaLabel ?: name,
        modifier = modifier,
    ) {
        group?.toggle?.invoke(value)
    }
}

@Composable
private fun ElCheckboxVisual(
    checked: Boolean,
    indeterminate: Boolean,
    disabled: Boolean,
    size: ElSize?,
    border: Boolean,
    button: Boolean,
    label: String?,
    fill: Color?,
    textColor: Color?,
    accessibilityLabel: String?,
    modifier: Modifier,
    onToggle: () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val resolvedSize = resolveElSize(size)
    if (button) {
        CheckboxButtonVisual(
            label = label ?: "",
            checked = checked,
            enabled = !disabled,
            size = size,
            fill = fill,
            textColor = textColor,
            accessibilityLabel = accessibilityLabel,
            modifier = modifier,
            onClick = onToggle,
        )
        return
    }
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
        active || (hovered && !disabled) -> textColor ?: colors.primary
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
                            active || hovered -> textColor ?: colors.primary
                            else -> colors.border
                        },
                        shape,
                    )
                    .clip(shape)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                else Modifier
            )
            .then(
                if (accessibilityLabel != null) {
                    Modifier.semantics { contentDescription = accessibilityLabel }
                } else {
                    Modifier
                },
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = !disabled,
                onClick = onToggle,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(boxSize)
                .background(
                    when {
                        disabled && active -> colors.fillLight
                        active -> fill ?: colors.primary
                        else -> colors.fillBlank
                    },
                    shape,
                )
                .border(
                    1.dp,
                    when {
                        disabled -> colors.disabledBorder
                        active || (hovered && !disabled) -> textColor ?: colors.primary
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
    fill: Color? = null,
    textColor: Color? = null,
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
                    size = size,
                    fill = fill,
                    textColor = textColor,
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
                    value = option.value,
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
                    value = option.value,
                )
            }
        }
    }
}

/**
 * Group whose options are composed from [ElCheckbox] children, matching the
 * upstream `<el-checkbox-group>` + `<el-checkbox>` pattern.
 */
@Composable
fun ElCheckboxGroup(
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
    fill: Color? = null,
    textColor: Color? = null,
    content: @Composable () -> Unit,
) {
    val toggle: (String) -> Unit = { value ->
        val next = if (priorityContains(selected, value)) selected - value else {
            if (selected.size >= max) selected else selected + value
        }
        if (next.size >= min) onSelectedChange(next)
    }
    val context = ElCheckboxGroupContext(
        selected = { selected },
        toggle = toggle,
        disabled = disabled,
        size = size,
        border = border,
        button = button,
        fill = fill,
        textColor = textColor,
    )
    CompositionLocalProvider(LocalElCheckboxGroup provides context) {
        if (direction == ElCheckboxDirection.Vertical) {
            Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) { content() }
        } else {
            Row(modifier, horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) { content() }
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
    size: ElSize? = null,
    fill: Color? = null,
    textColor: Color? = null,
    ariaLabel: String? = null,
) {
    CheckboxButtonVisual(
        label = label,
        checked = checked,
        enabled = enabled,
        size = size,
        fill = fill,
        textColor = textColor,
        accessibilityLabel = ariaLabel,
        modifier = modifier,
        onClick = onClick,
    )
}

@Composable
private fun CheckboxButtonVisual(
    label: String,
    checked: Boolean,
    enabled: Boolean,
    size: ElSize?,
    fill: Color?,
    textColor: Color?,
    accessibilityLabel: String?,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val resolvedSize = resolveElSize(size)
    val height = when (resolvedSize) {
        ElSize.Large -> 40.dp
        ElSize.Default -> 32.dp
        ElSize.Small -> 24.dp
    }
    val fg = when {
        !enabled -> colors.disabledText
        checked -> textColor ?: colors.white
        else -> colors.textRegular
    }
    val bg = when {
        !enabled -> colors.fillLight
        checked -> fill ?: colors.primary
        else -> colors.fillBlank
    }
    val borderColor = when {
        !enabled -> colors.disabledBorder
        checked -> fill ?: colors.primary
        else -> colors.border
    }
    Box(
        modifier = modifier
            .height(height)
            .background(bg)
            .border(1.dp, borderColor)
            .then(
                if (accessibilityLabel != null) {
                    Modifier.semantics { contentDescription = accessibilityLabel }
                } else {
                    Modifier
                },
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
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

internal fun priorityContains(list: List<String>, value: String): Boolean = list.contains(value)
