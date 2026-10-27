package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlin.math.pow
import kotlin.math.round

enum class ElInputNumberControlsPosition { Left, Right, Split }

/**
 * `<el-input-number>` with step controls, bounds and precision.
 */
@Composable
fun ElInputNumber(
    value: Double?,
    onValueChange: (Double?) -> Unit,
    modifier: Modifier = Modifier,
    min: Double? = null,
    max: Double? = null,
    step: Double = 1.0,
    precision: Int? = null,
    disabled: Boolean = false,
    controls: Boolean = true,
    controlsPosition: ElInputNumberControlsPosition = ElInputNumberControlsPosition.Right,
    placeholder: String? = null,
    size: ElSize? = null,
    width: androidx.compose.ui.unit.Dp = 150.dp,
    readOnly: Boolean = false,
    status: ElInputStatus? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedSize = resolveElSize(size)
    val shape = RoundedCornerShape(theme.radius.base)
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val hovered by interaction.collectIsHoveredAsState()
    var text by remember { mutableStateOf(formatInputNumber(value, precision)) }
    var internalFocus by remember { mutableStateOf(false) }
    LaunchedEffect(value) {
        val formatted = formatInputNumber(value, precision)
        if (!internalFocus && formatted != text) text = formatted
    }
    LaunchedEffect(focused) {
        internalFocus = focused
        onFocusChanged?.invoke(focused)
        if (!focused) {
            val parsed = text.toDoubleOrNull()
            if (text.isBlank()) {
                onValueChange(null)
            } else if (parsed == null) {
                text = formatInputNumber(value, precision)
            } else {
                commit(parsed, min, max, precision, onValueChange)
            }
        }
    }
    val statusColor = when (status) {
        ElInputStatus.Error -> colors.danger
        ElInputStatus.Warning -> colors.warning
        null -> null
    }
    val borderColor = when {
        disabled -> colors.disabledBorder
        statusColor != null -> statusColor
        focused -> colors.primary
        hovered -> colors.textDisabled
        else -> colors.border
    }
    val canDecrease = !disabled && !readOnly && (min == null || (value ?: min) > min)
    val canIncrease = !disabled && !readOnly && (max == null || (value ?: max) < max)
    val textStyle = theme.typography.base.copy(fontSize = resolvedSize.fontSize, color = if (disabled) colors.disabledText else colors.textRegular)

    Row(
        modifier = modifier
            .width(width)
            .clip(shape)
            .background(if (disabled) colors.disabledBackground else colors.fillBlank)
            .border(1.dp, borderColor, shape),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (controls && controlsPosition == ElInputNumberControlsPosition.Left) {
            NumberControls(
                enabledDown = canDecrease,
                enabledUp = canIncrease,
                height = resolvedSize.height - 2.dp,
                onDown = { change(-step, value, min, max, precision, onValueChange) },
                onUp = { change(step, value, min, max, precision, onValueChange) },
                downDescription = locale.inputNumber.decrease,
                upDescription = locale.inputNumber.increase,
            )
        }
        if (controls && controlsPosition == ElInputNumberControlsPosition.Split) {
            NumberButton(
                icon = ElIcons.Minus,
                enabled = canDecrease,
                modifier = Modifier.width(28.dp).height(resolvedSize.height - 2.dp),
                tint = colors.textPlaceholder,
                contentDescription = locale.inputNumber.decrease,
            ) { change(-step, value, min, max, precision, onValueChange) }
            Box(Modifier.width(1.dp).height(resolvedSize.height - 2.dp).background(colors.border))
        }
        Row(
            Modifier.weight(1f).padding(horizontal = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (prefix != null) {
                prefix()
                Spacer(Modifier.width(4.dp))
            }
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            BasicTextField(
                value = text,
                onValueChange = { next ->
                    text = next
                    next.toDoubleOrNull()?.let { commit(it, min, max, precision, onValueChange) }
                },
                modifier = Modifier.fillMaxWidth().height(resolvedSize.height - 2.dp),
                textStyle = textStyle,
                singleLine = true,
                enabled = !disabled,
                readOnly = readOnly,
                cursorBrush = SolidColor(colors.primary),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    val parsed = text.toDoubleOrNull()
                    if (parsed != null) commit(parsed, min, max, precision, onValueChange)
                }),
                interactionSource = interaction,
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (text.isEmpty() && placeholder != null) {
                            androidx.compose.foundation.text.BasicText(
                                text = placeholder,
                                style = textStyle.copy(color = colors.textPlaceholder),
                            )
                        }
                        inner()
                    }
                },
            )
            }
            if (suffix != null) {
                Spacer(Modifier.width(4.dp))
                suffix()
            }
        }
        if (controls && controlsPosition == ElInputNumberControlsPosition.Split) {
            Box(Modifier.width(1.dp).height(resolvedSize.height - 2.dp).background(colors.border))
            NumberButton(
                icon = ElIcons.Plus,
                enabled = canIncrease,
                modifier = Modifier.width(28.dp).height(resolvedSize.height - 2.dp),
                tint = colors.textPlaceholder,
                contentDescription = locale.inputNumber.increase,
            ) { change(step, value, min, max, precision, onValueChange) }
        }
        if (controls && controlsPosition == ElInputNumberControlsPosition.Right) {
            NumberControls(
                enabledDown = canDecrease,
                enabledUp = canIncrease,
                height = resolvedSize.height - 2.dp,
                onDown = { change(-step, value, min, max, precision, onValueChange) },
                onUp = { change(step, value, min, max, precision, onValueChange) },
                downDescription = locale.inputNumber.decrease,
                upDescription = locale.inputNumber.increase,
            )
        }
    }
}

@Composable
private fun NumberControls(
    enabledDown: Boolean,
    enabledUp: Boolean,
    height: androidx.compose.ui.unit.Dp,
    onDown: () -> Unit,
    onUp: () -> Unit,
    downDescription: String,
    upDescription: String,
) {
    val colors = LocalElTheme.current.colors
    val width = 24.dp
    Column(Modifier.height(height)) {
        NumberButton(ElIcons.ArrowUp, enabledUp, Modifier.weight(1f).width(width), colors.textPlaceholder, upDescription) { onUp() }
        Box(Modifier.width(width).height(1.dp).background(colors.border))
        NumberButton(ElIcons.ArrowDown, enabledDown, Modifier.weight(1f).width(width), colors.textPlaceholder, downDescription) { onDown() }
    }
}

@Composable
private fun NumberButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    modifier: Modifier,
    tint: androidx.compose.ui.graphics.Color,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        ElIcon(icon, size = 10.dp, tint = if (enabled) tint else tint.copy(alpha = 0.4f), contentDescription = contentDescription)
    }
}


private fun change(
    delta: Double,
    value: Double?,
    min: Double?,
    max: Double?,
    precision: Int?,
    onValueChange: (Double?) -> Unit,
) {
    val base = value ?: 0.0
    var next = base + delta
    min?.let { next = next.coerceAtLeast(it) }
    max?.let { next = next.coerceAtMost(it) }
    onValueChange(roundTo(next, precision))
}

private fun commit(
    value: Double,
    min: Double?,
    max: Double?,
    precision: Int?,
    onValueChange: (Double?) -> Unit,
) {
    var next = value
    min?.let { next = next.coerceAtLeast(it) }
    max?.let { next = next.coerceAtMost(it) }
    onValueChange(roundTo(next, precision))
}

private fun roundTo(value: Double, precision: Int?): Double {
    if (precision == null) return value
    val factor = 10.0.pow(precision)
    return round(value * factor) / factor
}

private fun formatInputNumber(value: Double?, precision: Int?): String {
    if (value == null) return ""
    return if (precision == null) {
        if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()
    } else {
        val factor = 10.0.pow(precision)
        val rounded = round(value * factor) / factor
        val sign = if (rounded < 0) "-" else ""
        val absolute = kotlin.math.abs(rounded)
        val intPart = absolute.toLong()
        if (precision == 0) sign + intPart.toString() else {
            val decimal = round((absolute - intPart) * factor).toLong().toString().padStart(precision, '0').take(precision)
            "$sign$intPart.$decimal"
        }
    }
}
