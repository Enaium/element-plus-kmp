package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
 *
 * [collapseTags] replaces the overflowing tags with a `+ N` chip, optionally
 * showing the hidden tags in a tooltip ([collapseTagsTooltip]). [draggable]
 * enables long-press drag reordering of the visible tags.
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
    collapseTags: Boolean = false,
    collapseTagsTooltip: Boolean = false,
    maxCollapseTags: Int = 1,
    draggable: Boolean = false,
    tag: (@Composable (value: String, index: Int) -> Unit)? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    clearIcon: (@Composable () -> Unit)? = null,
    triggerKey: Key? = null,
    autofocus: Boolean = false,
    ariaLabel: String? = null,
    onAddTag: ((String) -> Unit)? = null,
    onRemoveTag: ((String, Int) -> Unit)? = null,
    onDragTag: ((List<String>) -> Unit)? = null,
    onFocus: (() -> Unit)? = null,
    onBlur: (() -> Unit)? = null,
    onClear: (() -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val resolvedSize = resolveElSize(size)
    val horizontal = when (resolvedSize) {
        ElSize.Large -> 16.dp
        ElSize.Default -> 12.dp
        ElSize.Small -> 8.dp
    }
    val tagSize = if (resolvedSize == ElSize.Large) ElSize.Default else ElSize.Small
    var input by remember { mutableStateOf("") }
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val hovered by interaction.collectIsHoveredAsState()
    val focusRequester = remember { FocusRequester() }
    val latestValues by rememberUpdatedState(values)
    val latestOnValueChange by rememberUpdatedState(onValueChange)
    var hadFocus by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (autofocus) runCatching { focusRequester.requestFocus() }
    }
    LaunchedEffect(focused) {
        if (focused) {
            hadFocus = true
            onFocus?.invoke()
        } else if (hadFocus) {
            onBlur?.invoke()
        }
    }
    val shape = RoundedCornerShape(theme.radius.base)
    val borderColor = when {
        disabled -> colors.disabledBorder
        focused -> colors.primary
        hovered -> colors.textDisabled
        else -> colors.border
    }
    val maxReached = max != null && values.size >= max
    val textStyle = theme.typography.base.copy(fontSize = resolvedSize.fontSize, color = if (disabled) colors.disabledText else colors.textRegular)
    val showClear = clearable && values.isNotEmpty() && !disabled && !readonly
    val visibleTags = if (collapseTags && values.size > maxCollapseTags) values.take(maxCollapseTags) else values
    val hiddenCount = values.size - visibleTags.size

    fun add() {
        if (maxReached) return
        val pieces = if (delimiter != null) input.split(delimiter) else listOf(input)
        val additions = pieces.map { it.trim() }.filter { it.isNotEmpty() }
        if (additions.isNotEmpty()) {
            val room = max?.let { (it - values.size).coerceAtLeast(0) } ?: additions.size
            val accepted = additions.take(room)
            if (accepted.isNotEmpty()) {
                onValueChange(values + accepted)
                accepted.forEach { onAddTag?.invoke(it) }
                input = ""
            }
        }
    }

    fun remove(index: Int) {
        if (disabled || values.size <= min) return
        val removed = values.getOrNull(index) ?: return
        onValueChange(values.toMutableList().also { it.removeAt(index) })
        onRemoveTag?.invoke(removed, index)
    }

    fun clear() {
        onValueChange(emptyList())
        onClear?.invoke()
    }

    val tagWidths = remember { mutableStateMapOf<String, Int>() }
    var draggingTag by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }

    fun moveTag(tagValue: String, direction: Int) {
        val list = latestValues
        val index = list.indexOf(tagValue)
        val target = index + direction
        if (index < 0 || target !in list.indices) return
        val next = list.toMutableList()
        next.removeAt(index)
        next.add(target, tagValue)
        latestOnValueChange(next)
        onDragTag?.invoke(next)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (disabled) colors.disabledBackground else colors.fillBlank)
            .border(1.dp, borderColor, shape)
            .padding(horizontal = horizontal, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (prefix != null) prefix()
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f),
        ) {
            visibleTags.forEachIndexed { index, tagValue ->
                key(tagValue) {
                    val dragModifier = if (draggable && !disabled && !readonly) {
                        Modifier
                            .onSizeChanged { tagWidths[tagValue] = it.width }
                            .pointerInput(tagValue) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        draggingTag = tagValue
                                        dragOffset = 0f
                                    },
                                    onDragEnd = {
                                        draggingTag = null
                                        dragOffset = 0f
                                    },
                                    onDragCancel = {
                                        draggingTag = null
                                        dragOffset = 0f
                                    },
                                    onDrag = { change, amount ->
                                        change.consume()
                                        if (draggingTag == tagValue) {
                                            dragOffset += amount.x
                                            val current = latestValues.indexOf(tagValue)
                                            if (dragOffset > 0 && current in 0 until latestValues.lastIndex) {
                                                val threshold = (tagWidths[tagValue] ?: 0) +
                                                    (tagWidths[latestValues[current + 1]] ?: 0)
                                                if (threshold > 0 && dragOffset > threshold / 2f) {
                                                    dragOffset -= threshold / 2f
                                                    moveTag(tagValue, 1)
                                                }
                                            } else if (dragOffset < 0 && current > 0) {
                                                val threshold = (tagWidths[tagValue] ?: 0) +
                                                    (tagWidths[latestValues[current - 1]] ?: 0)
                                                if (threshold > 0 && -dragOffset > threshold / 2f) {
                                                    dragOffset += threshold / 2f
                                                    moveTag(tagValue, -1)
                                                }
                                            }
                                        }
                                    },
                                )
                            }
                    } else {
                        Modifier
                    }
                    val tagSlot = tag
                    val customTagContent: (@Composable () -> Unit)? = if (tagSlot != null) {
                        { tagSlot(tagValue, index) }
                    } else {
                        null
                    }
                    ElInputTagChip(
                        text = tagValue,
                        type = tagType,
                        effect = tagEffect,
                        size = tagSize,
                        closable = !disabled && !readonly,
                        onClose = { remove(index) },
                        modifier = dragModifier.alpha(if (draggingTag == tagValue) 0.6f else 1f),
                        content = customTagContent,
                    )
                }
            }
            if (hiddenCount > 0) {
                val collapsed: @Composable () -> Unit = {
                    ElTag(
                        text = "+ $hiddenCount",
                        type = tagType,
                        effect = tagEffect,
                        size = tagSize,
                    )
                }
                if (collapseTagsTooltip) {
                    ElTooltip(
                        placement = cn.enaium.elementplus.internal.ElPlacement.Bottom,
                        content = collapsed,
                        tooltip = {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                values.drop(maxCollapseTags).forEachIndexed { offset, value ->
                                    val tagSlot = tag
                                    val customTagContent: (@Composable () -> Unit)? = if (tagSlot != null) {
                                        { tagSlot(value, offset + maxCollapseTags) }
                                    } else {
                                        null
                                    }
                                    ElInputTagChip(
                                        text = value,
                                        type = tagType,
                                        effect = tagEffect,
                                        size = tagSize,
                                        closable = !disabled && !readonly,
                                        onClose = { remove(offset + maxCollapseTags) },
                                        content = customTagContent,
                                    )
                                }
                            }
                        },
                    )
                } else {
                    collapsed()
                }
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
                        .focusRequester(focusRequester)
                        .then(
                            if (ariaLabel != null) Modifier.semantics { contentDescription = ariaLabel } else Modifier,
                        )
                        .onPreviewKeyEvent { event ->
                            if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                            val createKey = triggerKey ?: when (trigger) {
                                ElInputTagTrigger.Enter -> Key.Enter
                                ElInputTagTrigger.Space -> Key.Spacebar
                            }
                            when {
                                event.key == createKey -> { add(); true }
                                event.key == Key.Enter -> { add(); true }
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
                                BasicText(
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
        if (suffix != null) suffix()
        if (showClear) {
            Box(Modifier.clickable { clear() }) {
                if (clearIcon != null) {
                    clearIcon()
                } else {
                    ElIcon(
                        ElIcons.CircleClose,
                        size = 14.dp,
                        tint = colors.textPlaceholder,
                    )
                }
            }
        }
    }
    LaunchedEffect(focused) {
        if (!focused && saveOnBlur && input.isNotBlank()) add()
    }
}

/** Renders a tag, using [content] as the tag body when a custom slot is given. */
@Composable
private fun ElInputTagChip(
    text: String,
    type: ElType,
    effect: ElTagEffect,
    size: ElSize,
    closable: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
) {
    if (content == null) {
        ElTag(
            text = text,
            type = type,
            effect = effect,
            size = size,
            closable = closable,
            onClose = onClose,
            modifier = modifier,
        )
        return
    }
    val theme = LocalElTheme.current
    val colors = theme.colors
    val resolvedSize = resolveElSize(size)
    val height = when (resolvedSize) {
        ElSize.Large -> 32.dp
        ElSize.Default -> 24.dp
        ElSize.Small -> 20.dp
    }
    val horizontal = when (resolvedSize) {
        ElSize.Large -> 12.dp
        ElSize.Default -> 10.dp
        ElSize.Small -> 8.dp
    }
    val typeColor = colors.colorOf(type)
    val (background, border, foreground) = when (effect) {
        ElTagEffect.Dark -> Triple(typeColor, typeColor, colors.white)
        ElTagEffect.Plain -> Triple(androidx.compose.ui.graphics.Color.Transparent, typeColor, typeColor)
        ElTagEffect.Light -> Triple(colors.light(type, 9), colors.light(type, 8), typeColor)
    }
    val chipShape = RoundedCornerShape(theme.radius.base)
    Row(
        modifier = modifier
            .height(height)
            .clip(chipShape)
            .background(background, chipShape)
            .border(1.dp, border, chipShape)
            .padding(start = horizontal, end = if (closable) 5.dp else horizontal),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
        if (closable) {
            Spacer(Modifier.width(6.dp))
            ElIcon(
                ElIcons.Close,
                size = 12.dp,
                tint = foreground,
                modifier = Modifier.clickable { onClose() },
            )
        }
    }
}
