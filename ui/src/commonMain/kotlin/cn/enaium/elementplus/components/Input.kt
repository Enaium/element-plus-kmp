package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElInputType { Text, Textarea, Password }
enum class ElInputStatus { Error, Warning }

/** Where the word count is rendered when [ElInput] shows a limit. */
enum class ElInputWordLimitPosition { Inside, Outside }

/**
 * `<el-input>` with prefix/suffix, prepend/append, clear, password reveal,
 * word limit and textarea modes.
 *
 * [formatter] renders the displayed text while [parser] converts the displayed
 * text back into the value passed to [onValueChange] (mirroring the upstream
 * `formatter`/`parser` pair). [countGraphemes] replaces `String.length` when
 * measuring `maxLength` and the word count.
 */
@Composable
fun ElInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: ElSize? = null,
    type: ElInputType = ElInputType.Text,
    disabled: Boolean = false,
    readOnly: Boolean = false,
    clearable: Boolean = false,
    showPassword: Boolean = false,
    maxLength: Int? = null,
    showWordLimit: Boolean = false,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    prepend: (@Composable () -> Unit)? = null,
    append: (@Composable () -> Unit)? = null,
    status: ElInputStatus? = null,
    minLines: Int = 1,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    onKeyboardAction: (() -> Unit)? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null,
    formatter: ((String) -> String)? = null,
    parser: ((String) -> String)? = null,
    countGraphemes: ((String) -> Int)? = null,
    clearIcon: (@Composable () -> Unit)? = null,
    passwordIcon: (@Composable () -> Unit)? = null,
    wordLimitPosition: ElInputWordLimitPosition = ElInputWordLimitPosition.Inside,
    autofocus: Boolean = false,
    onChange: ((String) -> Unit)? = null,
    onClear: (() -> Unit)? = null,
    ariaLabel: String? = null,
    focusRequester: FocusRequester? = null,
    selection: TextRange? = null,
    onSelectionApplied: (() -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedSize = resolveElSize(size)
    val shape = RoundedCornerShape(theme.radius.base)
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    val hovered by interaction.collectIsHoveredAsState()
    val internalFocusRequester = remember { FocusRequester() }
    val resolvedFocusRequester = focusRequester ?: internalFocusRequester
    LaunchedEffect(Unit) {
        if (autofocus) runCatching { resolvedFocusRequester.requestFocus() }
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
    val background = if (disabled) colors.disabledBackground else colors.fillBlank
    val textColor = if (disabled) colors.disabledText else colors.textRegular
    val limit = maxLength
    val displayValue = formatter?.invoke(value) ?: value
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(displayValue, selection = TextRange(displayValue.length)))
    }
    LaunchedEffect(displayValue, textFieldValue.composition) {
        // Do not replace the field state while an IME is composing. Replacing
        // it cancels the composition and makes the placeholder/composing text
        // jump before the Chinese characters are committed.
        if (textFieldValue.composition == null && textFieldValue.text != displayValue) {
            textFieldValue = TextFieldValue(displayValue, selection = TextRange(displayValue.length))
        }
    }
    LaunchedEffect(selection) {
        val requested = selection ?: return@LaunchedEffect
        val start = requested.start.coerceIn(0, textFieldValue.text.length)
        val end = requested.end.coerceIn(0, textFieldValue.text.length)
        textFieldValue = textFieldValue.copy(
            selection = TextRange(minOf(start, end), maxOf(start, end)),
        )
        onSelectionApplied?.invoke()
    }
    val countOf: (String) -> Int = { text -> countGraphemes?.invoke(text) ?: text.length }
    val counted = countOf(displayValue)
    val exceeds = limit != null && counted > limit
    val hasLimit = limit != null && showWordLimit
    val outsideLimit = hasLimit && wordLimitPosition == ElInputWordLimitPosition.Outside
    val showClear = clearable && value.isNotEmpty() && !disabled && !readOnly

    fun update(next: String) {
        val clamped = if (limit == null) next else clampToCount(next, limit, countGraphemes)
        onValueChange(parser?.invoke(clamped) ?: clamped)
    }

    val textStyle: TextStyle = theme.typography.base.copy(
        fontSize = resolvedSize.fontSize,
        color = textColor,
    )
    val fieldHeight = resolvedSize.height
    val isTextarea = type == ElInputType.Textarea
    val accessibility = if (ariaLabel != null) {
        Modifier.semantics { contentDescription = ariaLabel }
    } else {
        Modifier
    }
    val countText: @Composable () -> Unit = {
        if (hasLimit) {
            val maxLength = requireNotNull(limit)
            BasicText(
                text = locale.input.characters.replace("{count}", counted.toString()).replace("{max}", maxLength.toString()),
                style = theme.typography.extraSmall.copy(
                    color = if (exceeds) colors.danger else colors.textSecondary,
                ),
            )
        }
    }
    val clearButton: @Composable () -> Unit = {
        Box(
            Modifier
                .clip(CircleShape)
                .clickable {
                    update("")
                    onClear?.invoke()
                }
                .padding(2.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (clearIcon != null) {
                clearIcon()
            } else {
                ElIcon(
                    ElIcons.CircleClose,
                    size = 14.dp,
                    tint = colors.textDisabled,
                    contentDescription = locale.clear,
                )
            }
        }
    }

    val content: @Composable () -> Unit = {
        val fieldModifier = if (isTextarea) {
            Modifier.fillMaxWidth()
        } else {
            Modifier.fillMaxWidth().height(fieldHeight - 2.dp)
        }
        if (isTextarea) {
            Column(Modifier.padding(horizontal = 11.dp, vertical = 5.dp)) {
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { next ->
                        textFieldValue = next
                        update(next.text)
                    },
                    modifier = fieldModifier
                        .focusRequester(resolvedFocusRequester)
                        .then(accessibility),
                    textStyle = textStyle,
                    enabled = !disabled,
                    readOnly = readOnly,
                    maxLines = maxLines,
                    minLines = minLines,
                    cursorBrush = SolidColor(colors.primary),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
                    keyboardActions = KeyboardActions(onAny = { onKeyboardAction?.invoke() }),
                    interactionSource = interaction,
                    decorationBox = { inner ->
                        Box {
                            if (textFieldValue.text.isEmpty() && textFieldValue.composition == null && placeholder != null) {
                                BasicText(
                                    text = placeholder,
                                    style = textStyle.copy(color = colors.textPlaceholder),
                                )
                            }
                            inner()
                        }
                    },
                )
                if ((hasLimit && !outsideLimit) || showClear) {
                    Row(
                        Modifier.fillMaxWidth().padding(top = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (showClear) clearButton()
                        if (hasLimit && !outsideLimit) countText()
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(fieldHeight - 2.dp)
                    .padding(horizontal = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (prefix != null) {
                    prefix()
                    Spacer(Modifier.width(6.dp))
                }
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { next ->
                        textFieldValue = next
                        update(next.text)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(resolvedFocusRequester)
                        .then(accessibility),
                    textStyle = textStyle,
                    singleLine = true,
                    enabled = !disabled,
                    readOnly = readOnly,
                    cursorBrush = SolidColor(colors.primary),
                    visualTransformation = if (type == ElInputType.Password && !passwordVisible) {
                        PasswordVisualTransformation()
                    } else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (type == ElInputType.Password && !passwordVisible) KeyboardType.Password else keyboardType,
                        imeAction = imeAction,
                    ),
                    keyboardActions = KeyboardActions(onAny = { onKeyboardAction?.invoke() }),
                    interactionSource = interaction,
                    decorationBox = { inner ->
                        Box {
                            if (textFieldValue.text.isEmpty() && textFieldValue.composition == null && placeholder != null) {
                                BasicText(
                                    text = placeholder,
                                    style = textStyle.copy(color = colors.textPlaceholder),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            inner()
                        }
                    },
                )
                if (showClear) {
                    Spacer(Modifier.width(6.dp))
                    clearButton()
                }
                if (type == ElInputType.Password && showPassword) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .clickable { passwordVisible = !passwordVisible }
                            .padding(2.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (passwordIcon != null) {
                            passwordIcon()
                        } else {
                            ElIcon(
                                if (passwordVisible) ElIcons.View else ElIcons.Hide,
                                size = 14.dp,
                                tint = colors.textPlaceholder,
                            )
                        }
                    }
                }
                if (hasLimit && !outsideLimit) {
                    Spacer(Modifier.width(6.dp))
                    countText()
                }
                if (suffix != null) {
                    Spacer(Modifier.width(6.dp))
                    suffix()
                }
            }
        }
    }

    val grouped = prepend != null || append != null
    Column(modifier.fillMaxWidth()) {
        if (grouped) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(background)
                    .border(1.dp, borderColor, shape)
                    .hoverable(interaction),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (prepend != null) {
                    Box(
                        Modifier
                            .height(if (isTextarea) Dp.Unspecified else fieldHeight - 2.dp)
                            .background(colors.fillLight)
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center,
                    ) { prepend() }
                }
                Box(Modifier.weight(1f)) { content() }
                if (append != null) {
                    Box(
                        Modifier
                            .height(if (isTextarea) Dp.Unspecified else fieldHeight - 2.dp)
                            .background(colors.fillLight)
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center,
                    ) { append() }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(background)
                    .border(1.dp, borderColor, shape),
            ) {
                content()
            }
        }
        if (outsideLimit) {
            Box(Modifier.fillMaxWidth().padding(top = 2.dp), contentAlignment = Alignment.CenterEnd) {
                countText()
            }
        }
    }
    // Report focus changes without replacing the interaction source.
    FocusReporter(interaction, value, onFocusChanged, onChange)
}

/** Trims [value] so that [counter] never reports more than [limit]. */
private fun clampToCount(value: String, limit: Int, counter: ((String) -> Int)?): String {
    if (limit <= 0) return ""
    if (counter == null) return if (value.length > limit) value.take(limit) else value
    if (counter(value) <= limit) return value
    var end = 0
    var index = 0
    while (index < value.length) {
        val highSurrogate = value[index].code in 0xD800..0xDBFF
        index = if (highSurrogate && index + 1 < value.length) index + 2 else index + 1
        if (counter(value.substring(0, index)) > limit) break
        end = index
    }
    return value.substring(0, end)
}

@Composable
private fun FocusReporter(
    interaction: MutableInteractionSource,
    value: String,
    onFocusChanged: ((Boolean) -> Unit)?,
    onChange: ((String) -> Unit)?,
) {
    if (onFocusChanged == null && onChange == null) return
    val focused by interaction.collectIsFocusedAsState()
    var committed by remember { mutableStateOf(value) }
    LaunchedEffect(focused) {
        onFocusChanged?.invoke(focused)
        if (focused) {
            committed = value
        } else if (committed != value) {
            committed = value
            onChange?.invoke(value)
        }
    }
    LaunchedEffect(value) {
        if (!focused) committed = value
    }
}
