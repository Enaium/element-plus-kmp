package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElInputTagTrigger { Enter, Space }

/**
 * `<el-input-tag>`: a field that turns text into closable tags.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ElInputTag(
    values: List<String>,
    onValueChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: ElSize? = null,
    disabled: Boolean = false,
    readonly: Boolean = false,
    clearable: Boolean = true,
    max: Int? = null,
    min: Int = 0,
    tagType: ElType = ElType.Info,
    tagEffect: ElTagEffect = ElTagEffect.Light,
    trigger: ElInputTagTrigger = ElInputTagTrigger.Enter,
    delimiter: String? = null,
    saveOnBlur: Boolean = true,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val resolvedSize = resolveElSize(size)
    val horizontal = when (resolvedSize) {
        ElSize.Large -> 16.dp
        ElSize.Default -> 12.dp
        ElSize.Small -> 8.dp
    }
    var input by remember { mutableStateOf("") }
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val hovered by interaction.collectIsHoveredAsState()
    val shape = RoundedCornerShape(theme.radius.base)
    val borderColor = when {
        disabled -> colors.disabledBorder
        focused -> colors.primary
        hovered -> colors.textDisabled
        else -> colors.border
    }
    val maxReached = max != null && values.size >= max
    val textStyle = theme.typography.base.copy(fontSize = resolvedSize.fontSize, color = if (disabled) colors.disabledText else colors.textRegular)

    fun add() {
        if (maxReached) return
        val pieces = if (delimiter != null) input.split(delimiter) else listOf(input)
        val additions = pieces.map { it.trim() }.filter { it.isNotEmpty() }
        if (additions.isNotEmpty()) {
            onValueChange(values + additions)
            input = ""
        }
    }

    fun remove(index: Int) {
        if (disabled || values.size <= min) return
        onValueChange(values.toMutableList().also { it.removeAt(index) })
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (disabled) colors.disabledBackground else colors.fillBlank)
            .border(1.dp, borderColor, shape)
            .padding(start = horizontal, end = if (clearable && values.isNotEmpty()) 26.dp else horizontal, top = 4.dp, bottom = 4.dp),
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            values.forEachIndexed { index, tag ->
                ElTag(
                    text = tag,
                    type = tagType,
                    effect = tagEffect,
                    size = if (resolvedSize == ElSize.Large) ElSize.Small else ElSize.Small,
                    closable = !disabled && !readonly,
                    onClose = { remove(index) },
                )
            }
            if (!maxReached) {
                BasicTextField(
                    value = input,
                    onValueChange = { next ->
                        input = next
                        if (delimiter != null && next.contains(delimiter)) add()
                    },
                    modifier = Modifier
                        .widthIn(min = 50.dp, max = 220.dp)
                        .height(resolvedSize.height - 8.dp)
                        .onPreviewKeyEvent { event ->
                            if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                            when {
                                event.key == Key.Enter -> { add(); true }
                                trigger == ElInputTagTrigger.Space && event.key == Key.Spacebar -> { add(); true }
                                event.key == Key.Backspace && input.isEmpty() && values.isNotEmpty() -> {
                                    remove(values.lastIndex); true
                                }
                                else -> false
                            }
                        },
                    textStyle = textStyle,
                    singleLine = true,
                    enabled = !disabled && !readonly,
                    cursorBrush = SolidColor(colors.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { add() }),
                    interactionSource = interaction,
                    decorationBox = { inner ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (input.isEmpty() && values.isEmpty() && placeholder != null) {
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
        }
        if (clearable && values.isNotEmpty() && !disabled && !readonly) {
            ElIcon(
                ElIcons.CircleClose,
                size = 14.dp,
                tint = colors.textPlaceholder,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable { onValueChange(emptyList()) },
            )
        }
    }
    LaunchedEffect(focused) {
        if (!focused && saveOnBlur && input.isNotBlank()) add()
    }
}
