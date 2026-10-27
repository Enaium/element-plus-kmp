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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElInputType { Text, Textarea, Password }
enum class ElInputStatus { Error, Warning }

/**
 * `<el-input>` with prefix/suffix, prepend/append, clear, password reveal,
 * word limit and textarea modes.
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
    val exceeds = limit != null && value.length > limit
    val hasLimit = limit != null && showWordLimit

    fun update(next: String) {
        val trimmed = if (limit != null && next.length > limit) next.take(limit) else next
        onValueChange(trimmed)
    }

    val textStyle: TextStyle = theme.typography.base.copy(
        fontSize = resolvedSize.fontSize,
        color = textColor,
    )
    val fieldHeight = resolvedSize.height
    val isTextarea = type == ElInputType.Textarea

    val content: @Composable () -> Unit = {
        val fieldModifier = if (isTextarea) {
            Modifier.fillMaxWidth()
        } else {
            Modifier.fillMaxWidth().height(fieldHeight - 2.dp)
        }
        if (isTextarea) {
            Column(Modifier.padding(horizontal = 11.dp, vertical = 5.dp)) {
                BasicTextField(
                    value = value,
                    onValueChange = ::update,
                    modifier = fieldModifier,
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
                            if (value.isEmpty() && placeholder != null) {
                                androidx.compose.foundation.text.BasicText(
                                    text = placeholder,
                                    style = textStyle.copy(color = colors.textPlaceholder),
                                )
                            }
                            inner()
                        }
                    },
                )
                if (hasLimit) {
                    androidx.compose.foundation.text.BasicText(
                        text = locale.input.characters.replace("{count}", value.length.toString()).replace("{max}", limit.toString()),
                        modifier = Modifier.align(Alignment.End),
                        style = theme.typography.extraSmall.copy(
                            color = if (exceeds) colors.danger else colors.textSecondary,
                        ),
                    )
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
                    value = value,
                    onValueChange = ::update,
                    modifier = Modifier.weight(1f),
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
                            if (value.isEmpty() && placeholder != null) {
                                androidx.compose.foundation.text.BasicText(
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
                if (clearable && value.isNotEmpty() && !disabled && !readOnly) {
                    Spacer(Modifier.width(6.dp))
                    ElIcon(
                        ElIcons.CircleClose,
                        size = 14.dp,
                        tint = colors.textDisabled,
                        contentDescription = locale.clear,
                        modifier = Modifier.clickable { update("") },
                    )
                }
                if (type == ElInputType.Password && showPassword) {
                    Spacer(Modifier.width(6.dp))
                    ElIcon(
                        if (passwordVisible) ElIcons.View else ElIcons.Hide,
                        size = 14.dp,
                        tint = colors.textPlaceholder,
                        modifier = Modifier.clickable { passwordVisible = !passwordVisible },
                    )
                }
                if (hasLimit) {
                    Spacer(Modifier.width(6.dp))
                    androidx.compose.foundation.text.BasicText(
                        text = locale.input.characters.replace("{count}", value.length.toString()).replace("{max}", limit.toString()),
                        style = theme.typography.extraSmall.copy(
                            color = if (exceeds) colors.danger else colors.textSecondary,
                        ),
                    )
                }
                if (suffix != null) {
                    Spacer(Modifier.width(6.dp))
                    suffix()
                }
            }
        }
    }

    val grouped = prepend != null || append != null
    if (grouped) {
        Row(
            modifier = modifier
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
            modifier = modifier
                .fillMaxWidth()
                .clip(shape)
                .background(background)
                .border(1.dp, borderColor, shape)
,
        ) {
            content()
        }
    }
    // Report focus changes without replacing the interaction source.
    FocusReporter(interaction, onFocusChanged)
}

@Composable
private fun FocusReporter(
    interaction: MutableInteractionSource,
    onFocusChanged: ((Boolean) -> Unit)?,
) {
    if (onFocusChanged == null) return
    val focused by interaction.collectIsFocusedAsState()
    androidx.compose.runtime.LaunchedEffect(focused) { onFocusChanged(focused) }
}
