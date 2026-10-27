package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

data class ElMentionOption(
    val value: String,
    val label: String = value,
    val disabled: Boolean = false,
    val icon: ImageVector? = null,
)

/**
 * `<el-mention>`: textarea with `@` style suggestion popups. The active query
 * is derived from the last trigger before the last split token.
 *
 * @param prefix trigger characters, the Compose equivalent of `prefix`
 *   (`['@', '#']`). When empty, [trigger] is used.
 * @param loading shows the loading row instead of the option list and enables
 *   asynchronous options supplied through [onSearch].
 * @param whole when the character removed from the value completes a mention
 *   token, removes the whole token instead, the Compose equivalent of `whole`.
 * @param checkIsWhole decides whether a token is removable as a whole. Defaults
 *   to a token that matches an option value.
 * @param onSearch fires with the active query and prefix whenever the query
 *   changes, used to load options remotely.
 * @param onWholeRemove fires when [whole] removes a token as a whole.
 * @param optionContent replaces the option label, the Compose equivalent of the
 *   `label` slot.
 */
@Composable
fun ElMention(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<ElMentionOption>,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: ElSize? = null,
    disabled: Boolean = false,
    trigger: Char = '@',
    split: String = " ",
    minLines: Int = 2,
    maxLines: Int = 4,
    prefix: List<Char> = emptyList(),
    loading: Boolean = false,
    whole: Boolean = false,
    checkIsWhole: ((String, Char) -> Boolean)? = null,
    onSearch: ((String, Char) -> Unit)? = null,
    onSelect: ((ElMentionOption, Char) -> Unit)? = null,
    onWholeRemove: ((String, Char) -> Unit)? = null,
    placement: ElPlacement = ElPlacement.BottomStart,
    offset: DpOffset = DpOffset(0.dp, 4.dp),
    filterOption: ((String, ElMentionOption) -> Boolean)? = null,
    optionContent: (@Composable (ElMentionOption) -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    loadingContent: (@Composable () -> Unit)? = null,
) {
    MentionImpl(
        value = value,
        onValueChange = onValueChange,
        entries = options.map { MentionEntry(it, it) },
        modifier = modifier,
        placeholder = placeholder,
        size = size,
        disabled = disabled,
        prefixes = prefix.ifEmpty { listOf(trigger) },
        split = split,
        minLines = minLines,
        maxLines = maxLines,
        loading = loading,
        whole = whole,
        checkIsWhole = checkIsWhole,
        onSearch = onSearch,
        onSelect = { entry, prefixChar ->
            @Suppress("UNCHECKED_CAST")
            onSelect?.invoke(entry.source as ElMentionOption, prefixChar)
        },
        onWholeRemove = onWholeRemove,
        placement = placement,
        offset = offset,
        filterOption = filterOption,
        optionContent = optionContent,
        header = header,
        footer = footer,
        loadingContent = loadingContent,
    )
}

/**
 * `<el-mention>` over arbitrary items with selector aliases, the Compose
 * equivalent of the `props` option (`{ label, value, disabled }` keys).
 */
@Composable
fun <T> ElMention(
    value: String,
    onValueChange: (String) -> Unit,
    items: List<T>,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
    itemValue: (T) -> String = itemLabel,
    itemDisabled: (T) -> Boolean = { false },
    itemIcon: ((T) -> ImageVector?)? = null,
    placeholder: String? = null,
    size: ElSize? = null,
    disabled: Boolean = false,
    trigger: Char = '@',
    split: String = " ",
    minLines: Int = 2,
    maxLines: Int = 4,
    prefix: List<Char> = emptyList(),
    loading: Boolean = false,
    whole: Boolean = false,
    checkIsWhole: ((String, Char) -> Boolean)? = null,
    onSearch: ((String, Char) -> Unit)? = null,
    onSelect: ((T, Char) -> Unit)? = null,
    placement: ElPlacement = ElPlacement.BottomStart,
    offset: DpOffset = DpOffset(0.dp, 4.dp),
) {
    MentionImpl(
        value = value,
        onValueChange = onValueChange,
        entries = items.map { item ->
            MentionEntry(
                option = ElMentionOption(
                    value = itemValue(item),
                    label = itemLabel(item),
                    disabled = itemDisabled(item),
                    icon = itemIcon?.invoke(item),
                ),
                source = item,
            )
        },
        modifier = modifier,
        placeholder = placeholder,
        size = size,
        disabled = disabled,
        prefixes = prefix.ifEmpty { listOf(trigger) },
        split = split,
        minLines = minLines,
        maxLines = maxLines,
        loading = loading,
        whole = whole,
        checkIsWhole = checkIsWhole,
        onSearch = onSearch,
        onSelect = { entry, prefixChar ->
            @Suppress("UNCHECKED_CAST")
            val typed = entry.source as T
            onSelect?.invoke(typed, prefixChar)
        },
        onWholeRemove = null,
        placement = placement,
        offset = offset,
        filterOption = null,
        optionContent = null,
        header = null,
        footer = null,
        loadingContent = null,
    )
}

private class MentionEntry(
    val option: ElMentionOption,
    val source: Any?,
)

private data class MentionContext(
    val prefix: Char,
    val prefixIndex: Int,
    val pattern: String,
)

private data class WholeRemoval(
    val value: String,
    val pattern: String,
    val prefix: Char,
)

/**
 * Finds the mention token ending at the current end of the text. A whitespace
 * or split character terminates the search: once the token has been completed,
 * the suggestion list must close without requiring a second separator.
 */
private fun findMentionContext(
    value: String,
    prefixes: List<Char>,
    split: String,
): MentionContext? {
    if (value.isEmpty() || prefixes.isEmpty()) return null
    for (index in value.indices.reversed()) {
        val char = value[index]
        if (char in prefixes) {
            return MentionContext(
                prefix = char,
                prefixIndex = index,
                pattern = value.substring(index + 1),
            )
        }
        if (char.isWhitespace() || split.contains(char)) return null
    }
    return null
}

/**
 * Detects a backspace/delete that removed the split character right after a
 * whole mention token and widens it to the whole token, mirroring `whole`.
 */
private fun wholeRemovalFor(
    value: String,
    next: String,
    prefixes: List<Char>,
    split: String,
    options: List<ElMentionOption>,
    checkIsWhole: ((String, Char) -> Boolean)?,
): WholeRemoval? {
    if (value.length - next.length != 1) return null
    var removedIndex = 0
    while (removedIndex < next.length && value[removedIndex] == next[removedIndex]) removedIndex++
    if (removedIndex >= value.length) return null
    val removed = value[removedIndex]
    if (!(removed.isWhitespace() || split.contains(removed))) return null
    var prefixIndex = -1
    var cursor = removedIndex - 1
    while (cursor >= 0 && !value[cursor].isWhitespace()) {
        if (value[cursor] in prefixes) {
            prefixIndex = cursor
            break
        }
        cursor--
    }
    if (prefixIndex < 0) return null
    val pattern = value.substring(prefixIndex + 1, removedIndex)
    if (pattern.isEmpty()) return null
    val isWhole = checkIsWhole?.invoke(pattern, value[prefixIndex])
        ?: options.any { it.value == pattern }
    if (!isWhole) return null
    return WholeRemoval(
        value = value.substring(0, prefixIndex) + value.substring(removedIndex + 1),
        pattern = pattern,
        prefix = value[prefixIndex],
    )
}

@Composable
private fun MentionImpl(
    value: String,
    onValueChange: (String) -> Unit,
    entries: List<MentionEntry>,
    modifier: Modifier,
    placeholder: String?,
    size: ElSize?,
    disabled: Boolean,
    prefixes: List<Char>,
    split: String,
    minLines: Int,
    maxLines: Int,
    loading: Boolean,
    whole: Boolean,
    checkIsWhole: ((String, Char) -> Boolean)?,
    onSearch: ((String, Char) -> Unit)?,
    onSelect: ((MentionEntry, Char) -> Unit)?,
    onWholeRemove: ((String, Char) -> Unit)?,
    placement: ElPlacement,
    offset: DpOffset,
    filterOption: ((String, ElMentionOption) -> Boolean)?,
    optionContent: (@Composable (ElMentionOption) -> Unit)?,
    header: (@Composable () -> Unit)?,
    footer: (@Composable () -> Unit)?,
    loadingContent: (@Composable () -> Unit)?,
) {
    val theme = LocalElTheme.current
    val locale = LocalElConfig.current.locale
    val resolvedSize = resolveElSize(size)
    var focused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var selectionRequest by remember { mutableStateOf<TextRange?>(null) }
    val context = findMentionContext(value, prefixes, split)
    val active = focused && context != null
    val query = if (active) context.pattern else ""
    val latestOnSearch by rememberUpdatedState(onSearch)
    val latestOnWholeRemove by rememberUpdatedState(onWholeRemove)

    LaunchedEffect(active, query, context?.prefix) {
        if (active) {
            val ctx = requireNotNull(context)
            latestOnSearch?.invoke(query, ctx.prefix)
        }
    }

    val filtered = remember(entries, query, active, loading, filterOption) {
        if (!active || loading) {
            emptyList()
        } else {
            entries.filter { entry ->
                val pattern = query
                filterOption?.invoke(pattern, entry.option)
                    ?: entry.option.label.contains(pattern, ignoreCase = true) ||
                    entry.option.value.contains(pattern, ignoreCase = true)
            }
        }
    }

    fun handleValueChange(next: String) {
        if (whole) {
            val removal = wholeRemovalFor(
                value = value,
                next = next,
                prefixes = prefixes,
                split = split,
                options = entries.map { it.option },
                checkIsWhole = checkIsWhole,
            )
            if (removal != null) {
                onValueChange(removal.value)
                latestOnWholeRemove?.invoke(removal.pattern, removal.prefix)
                return
            }
        }
        onValueChange(next)
    }

    Box(modifier) {
        ElInput(
            value = value,
            onValueChange = { handleValueChange(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = placeholder,
            size = resolvedSize,
            type = ElInputType.Textarea,
            disabled = disabled,
            minLines = minLines,
            maxLines = maxLines,
            onFocusChanged = { focused = it },
            focusRequester = focusRequester,
            selection = selectionRequest,
            onSelectionApplied = { selectionRequest = null },
        )
        ElPopper(
            visible = active && (loading || filtered.isNotEmpty() || header != null || footer != null),
            onDismissRequest = { },
            placement = placement,
            offset = offset,
            dismissOnClickOutside = false,
        ) {
            ElPopperSurface(modifier = Modifier.width(240.dp)) {
                Column(Modifier.heightIn(max = 280.dp)) {
                    if (header != null) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
                            header()
                        }
                    }
                    if (loading) {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (loadingContent != null) {
                                loadingContent()
                            } else {
                                ElIcon(
                                    ElIcons.Loading,
                                    size = 14.dp,
                                    tint = theme.colors.textSecondary,
                                    spin = true,
                                )
                                Spacer(Modifier.width(8.dp))
                                BasicText(
                                    text = locale.mention.loading,
                                    style = theme.typography.small.copy(color = theme.colors.textSecondary),
                                )
                            }
                        }
                    }
                    filtered.forEach { entry ->
                        val option = entry.option
                        val source = remember(option.value) { MutableInteractionSource() }
                        val hovered by source.collectIsHoveredAsState()
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(if (hovered) theme.colors.fillLight else theme.colors.backgroundOverlay)
                                .clickable(enabled = !option.disabled) {
                                    if (option.disabled) return@clickable
                                    val ctx = findMentionContext(value, prefixes, split) ?: return@clickable
                                    val next = value.substring(0, ctx.prefixIndex + 1) + option.value + split
                                    onValueChange(next)
                                    selectionRequest = TextRange(next.length)
                                    runCatching { focusRequester.requestFocus() }
                                    onSelect?.invoke(entry, ctx.prefix)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (option.icon != null) {
                                ElIcon(option.icon, size = 16.dp)
                                Spacer(Modifier.width(8.dp))
                            }
                            if (optionContent != null) {
                                optionContent(option)
                            } else {
                                BasicText(
                                    text = option.label,
                                    style = theme.typography.base.copy(
                                        fontSize = resolvedSize.fontSize,
                                        color = if (option.disabled) theme.colors.textDisabled
                                        else if (hovered) theme.colors.primary else theme.colors.textRegular,
                                    ),
                                )
                            }
                        }
                    }
                    if (footer != null) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
                            footer()
                        }
                    }
                }
            }
        }
    }
}
