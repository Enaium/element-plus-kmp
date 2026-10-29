package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElInputOtpType { Outlined, Filled, Underlined }

/**
 * `<el-input-otp>`: fixed-length code input with auto-advance, arrows,
 * separators and masked mode.
 */
@Composable
fun ElInputOtp(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    size: ElSize? = null,
    type: ElInputOtpType = ElInputOtpType.Outlined,
    disabled: Boolean = false,
    readonly: Boolean = false,
    mask: Boolean = false,
    separator: (@Composable (index: Int) -> Unit)? = null,
    validator: (char: String, index: Int) -> Boolean = { _, _ -> true },
    onFinish: ((String) -> Unit)? = null,
    onChange: ((String) -> Unit)? = null,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
    ariaLabel: String? = null,
) {
    require(length > 0) { "length must be positive" }
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedSize = resolveElSize(size)
    val cell = when (resolvedSize) {
        ElSize.Large -> 48.dp
        ElSize.Default -> 40.dp
        ElSize.Small -> 32.dp
    }
    val textStyle = theme.typography.base.copy(
        fontSize = when (resolvedSize) {
            ElSize.Large -> 18.sp
            ElSize.Default -> 16.sp
            ElSize.Small -> 14.sp
        },
        color = if (disabled) colors.disabledText else colors.textPrimary,
        textAlign = TextAlign.Center,
    )
    val focusRequesters = remember(length) { List(length) { FocusRequester() } }
    var focusedIndex by remember { mutableStateOf(-1) }
    var hadFocus by remember { mutableStateOf(false) }
    LaunchedEffect(focusedIndex) {
        if (focusedIndex >= 0) {
            if (!hadFocus) {
                hadFocus = true
                onFocus?.invoke()
            }
        } else if (hadFocus) {
            hadFocus = false
            onBlur?.invoke()
        }
    }
    val chars = remember(value) { value.padEnd(length).take(length).toMutableList() }

    fun setAt(index: Int, char: Char?) {
        val next = value.padEnd(length).take(length).toCharArray()
        next[index] = char ?: ' '
        val text = next.concatToString().replace(" ", "")
        onValueChange(text.take(length))
        onChange?.invoke(text.take(length))
        if (text.length >= length) onFinish?.invoke(text)
    }

    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(length) { index ->
            val char = value.getOrNull(index)?.toString() ?: ""
            val shape = RoundedCornerShape(theme.radius.base)
            val cellBg = if (type == ElInputOtpType.Filled) colors.fillLight else colors.fillBlank
            val borderModifier = when (type) {
                ElInputOtpType.Outlined -> Modifier.border(1.dp, if (focusedIndex == index) colors.primary else colors.border, shape)
                ElInputOtpType.Filled -> Modifier
                ElInputOtpType.Underlined -> Modifier
            }
            Box(
                Modifier
                    .size(cell)
                    .clip(shape)
                    .background(cellBg, shape)
                    .then(borderModifier)
                    .then(
                        if (type == ElInputOtpType.Underlined) Modifier.drawBehind {
                            val y = this.size.height - 1.dp.toPx()
                            drawLine(colors.border, Offset(0f, y), Offset(this.size.width, y), 1.dp.toPx())
                        }
                        else Modifier
                    )
                    .then(if (disabled) Modifier else Modifier),
                contentAlignment = Alignment.Center,
            ) {
                BasicTextField(
                    value = char,
                    onValueChange = { next ->
                        if (readonly || disabled) return@BasicTextField
                        if (next.length > 1) {
                            // Pasting: distribute from the current cell.
                            val charsToInsert = next.filter { validator(it.toString(), index) }
                            if (charsToInsert.isNotEmpty()) {
                                val result = value.padEnd(length).take(length).toCharArray()
                                charsToInsert.forEachIndexed { offset, c ->
                                    if (index + offset < length) result[index + offset] = c
                                }
                                val text = result.concatToString().replace(" ", "").take(length)
                                onValueChange(text)
                                onChange?.invoke(text)
                                if (text.length == length) onFinish?.invoke(text)
                                val target = (index + charsToInsert.length).coerceAtMost(length - 1)
                                focusedIndex = target
                                focusRequesters[target].requestFocus()
                            }
                        } else if (next.isEmpty()) {
                            setAt(index, null)
                        } else {
                            val c = next.last()
                            if (validator(c.toString(), index)) {
                                setAt(index, c)
                                if (index < length - 1) {
                                    focusedIndex = index + 1
                                    focusRequesters[index + 1].requestFocus()
                                } else {
                                    focusRequesters[index].requestFocus()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesters[index])
                        .onFocusChanged {
                            if (it.isFocused) {
                                focusedIndex = index
                            } else if (focusedIndex == index) {
                                focusedIndex = -1
                            }
                        }
                        .semantics {
                            contentDescription = if (ariaLabel != null) {
                                "$ariaLabel ${index + 1}"
                            } else {
                                locale.inputOtp.defaultLabel.replace("{index}", (index + 1).toString())
                            }
                        }
                        .onPreviewKeyEvent { event ->
                            if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                            when (event.key) {
                                Key.Backspace -> {
                                    if (char.isNotEmpty()) setAt(index, null)
                                    val target = (index - 1).coerceAtLeast(0)
                                    focusedIndex = target
                                    focusRequesters[target].requestFocus()
                                    true
                                }
                                Key.Delete -> {
                                    if (char.isNotEmpty()) setAt(index, null)
                                    focusRequesters[index].requestFocus()
                                    true
                                }
                                Key.DirectionLeft -> {
                                    if (index > 0) { focusedIndex = index - 1; focusRequesters[index - 1].requestFocus() }
                                    true
                                }
                                Key.DirectionRight -> {
                                    if (index < length - 1) { focusedIndex = index + 1; focusRequesters[index + 1].requestFocus() }
                                    true
                                }
                                else -> false
                            }
                        },
                    textStyle = textStyle,
                    singleLine = true,
                    enabled = !disabled,
                    readOnly = readonly,
                    cursorBrush = SolidColor(colors.primary),
                    visualTransformation = if (mask) PasswordVisualTransformation() else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = if (index == length - 1) ImeAction.Done else ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { if (index < length - 1) focusRequesters[index + 1].requestFocus() },
                        onDone = { onFinish?.invoke(value) },
                    ),
                )
            }
            if (separator != null && index < length - 1) separator(index)
        }
    }
}
