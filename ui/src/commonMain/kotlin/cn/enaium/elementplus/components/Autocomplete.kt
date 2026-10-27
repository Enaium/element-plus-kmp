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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
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
import kotlinx.coroutines.delay

/**
 * `<el-autocomplete>`: input with asynchronously/lazily fetched suggestions.
 * [fetchSuggestions] is invoked in a debounced coroutine; pass
 * [asyncFetchSuggestions] instead when the lookup suspends.
 *
 * Keyboard navigation (arrow up/down, enter, escape), [header]/[footer] slots,
 * custom [suggestionContent], [loadingContent], prefix/suffix slots and the
 * `select-when-unmatched`/`highlight-first-item` behaviours are supported.
 */
@Composable
fun ElAutocomplete(
    value: String,
    onValueChange: (String) -> Unit,
    fetchSuggestions: (String) -> List<String>,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: ElSize? = null,
    disabled: Boolean = false,
    clearable: Boolean = true,
    debounce: Long = 300,
    triggerOnFocus: Boolean = true,
    onSelect: ((String) -> Unit)? = null,
    emptyText: String? = null,
    asyncFetchSuggestions: (suspend (String) -> List<String>)? = null,
    selectWhenUnmatched: Boolean = false,
    highlightFirstItem: Boolean = false,
    loopNavigation: Boolean = true,
    hideLoading: Boolean = false,
    fitInputWidth: Boolean = true,
    dropdownWidth: Dp? = null,
    loadingContent: (@Composable () -> Unit)? = null,
    suggestionContent: (@Composable (String) -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    placement: ElPlacement = ElPlacement.BottomStart,
) {
    AutocompleteCore(
        value = value,
        onValueChange = onValueChange,
        fetchSuggestions = fetchSuggestions,
        asyncFetchSuggestions = asyncFetchSuggestions,
        valueKey = { it },
        modifier = modifier,
        placeholder = placeholder,
        size = size,
        disabled = disabled,
        clearable = clearable,
        debounce = debounce,
        triggerOnFocus = triggerOnFocus,
        emptyText = emptyText,
        highlightFirstItem = highlightFirstItem,
        loopNavigation = loopNavigation,
        hideLoading = hideLoading,
        fitInputWidth = fitInputWidth,
        dropdownWidth = dropdownWidth,
        loadingContent = loadingContent,
        itemContent = suggestionContent,
        header = header,
        footer = footer,
        prefix = prefix,
        suffix = suffix,
        placement = placement,
        onSelected = onSelect,
        onSelectUnmatched = if (selectWhenUnmatched) onSelect else null,
    )
}

/**
 * `<el-autocomplete>` with object suggestions: [valueKey] selects the text
 * written back into the field and [itemContent] renders each suggestion.
 */
@Composable
fun <T> ElAutocompleteItems(
    value: String,
    onValueChange: (String) -> Unit,
    fetchSuggestions: (String) -> List<T>,
    valueKey: (T) -> String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: ElSize? = null,
    disabled: Boolean = false,
    clearable: Boolean = true,
    debounce: Long = 300,
    triggerOnFocus: Boolean = true,
    onSelect: ((T) -> Unit)? = null,
    emptyText: String? = null,
    asyncFetchSuggestions: (suspend (String) -> List<T>)? = null,
    highlightFirstItem: Boolean = false,
    loopNavigation: Boolean = true,
    hideLoading: Boolean = false,
    fitInputWidth: Boolean = true,
    dropdownWidth: Dp? = null,
    loadingContent: (@Composable () -> Unit)? = null,
    itemContent: (@Composable (T) -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    placement: ElPlacement = ElPlacement.BottomStart,
) {
    AutocompleteCore(
        value = value,
        onValueChange = onValueChange,
        fetchSuggestions = fetchSuggestions,
        asyncFetchSuggestions = asyncFetchSuggestions,
        valueKey = valueKey,
        modifier = modifier,
        placeholder = placeholder,
        size = size,
        disabled = disabled,
        clearable = clearable,
        debounce = debounce,
        triggerOnFocus = triggerOnFocus,
        emptyText = emptyText,
        highlightFirstItem = highlightFirstItem,
        loopNavigation = loopNavigation,
        hideLoading = hideLoading,
        fitInputWidth = fitInputWidth,
        dropdownWidth = dropdownWidth,
        loadingContent = loadingContent,
        itemContent = itemContent,
        header = header,
        footer = footer,
        prefix = prefix,
        suffix = suffix,
        placement = placement,
        onSelected = onSelect,
        onSelectUnmatched = null,
    )
}

@Composable
private fun <T> AutocompleteCore(
    value: String,
    onValueChange: (String) -> Unit,
    fetchSuggestions: (String) -> List<T>,
    asyncFetchSuggestions: (suspend (String) -> List<T>)?,
    valueKey: (T) -> String,
    modifier: Modifier,
    placeholder: String?,
    size: ElSize?,
    disabled: Boolean,
    clearable: Boolean,
    debounce: Long,
    triggerOnFocus: Boolean,
    emptyText: String?,
    highlightFirstItem: Boolean,
    loopNavigation: Boolean,
    hideLoading: Boolean,
    fitInputWidth: Boolean,
    dropdownWidth: Dp?,
    loadingContent: (@Composable () -> Unit)?,
    itemContent: (@Composable (T) -> Unit)?,
    header: (@Composable () -> Unit)?,
    footer: (@Composable () -> Unit)?,
    prefix: (@Composable () -> Unit)?,
    suffix: (@Composable () -> Unit)?,
    placement: ElPlacement,
    onSelected: ((T) -> Unit)?,
    onSelectUnmatched: ((String) -> Unit)?,
) {
    val theme = LocalElTheme.current
    val locale = LocalElConfig.current.locale
    val resolvedSize = resolveElSize(size)
    val effectivePlaceholder = placeholder ?: locale.select.placeholder
    val effectiveEmptyText = emptyText ?: locale.select.noMatch
    val defaultPrefix: @Composable () -> Unit = { ElIcon(ElIcons.Search, size = 14.dp) }
    var focused by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf(emptyList<T>()) }
    var loading by remember { mutableStateOf(false) }
    var highlightedIndex by remember { mutableStateOf(-1) }
    var lastSelectedValue by remember { mutableStateOf<String?>(null) }
    var fieldWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val listState = rememberLazyListState()

    LaunchedEffect(value, focused) {
        if (!focused) {
            visible = false
            return@LaunchedEffect
        }
        if (value == lastSelectedValue) {
            lastSelectedValue = null
            visible = false
            return@LaunchedEffect
        }
        if (!triggerOnFocus && value.isBlank()) {
            visible = false
            return@LaunchedEffect
        }
        delay(debounce)
        loading = true
        var next: List<T> = emptyList()
        try {
            next = asyncFetchSuggestions?.invoke(value) ?: fetchSuggestions(value)
        } finally {
            loading = false
        }
        suggestions = next
        highlightedIndex = if (highlightFirstItem && next.isNotEmpty()) 0 else -1
        visible = true
    }

    LaunchedEffect(highlightedIndex, visible, loading) {
        if (!loading && visible && highlightedIndex in suggestions.indices) {
            listState.animateScrollToItem(highlightedIndex)
        }
    }

    fun select(item: T) {
        val text = valueKey(item)
        lastSelectedValue = text
        onValueChange(text)
        onSelected?.invoke(item)
        visible = false
        highlightedIndex = -1
    }

    fun moveHighlight(delta: Int) {
        if (suggestions.isEmpty()) return
        if (!visible) visible = true
        val next = if (highlightedIndex < 0) {
            if (delta > 0) 0 else suggestions.lastIndex
        } else {
            highlightedIndex + delta
        }
        highlightedIndex = when {
            next in suggestions.indices -> next
            loopNavigation -> if (delta > 0) 0 else suggestions.lastIndex
            else -> highlightedIndex
        }
    }

    fun commit() {
        if (visible) {
            val item = suggestions.getOrNull(highlightedIndex)
            if (item != null) {
                select(item)
                return
            }
            if (suggestions.isNotEmpty()) return
        }
        if (onSelectUnmatched != null && value.isNotBlank()) {
            onSelectUnmatched.invoke(value)
            visible = false
        }
    }

    val keyHandler: (KeyEvent) -> Boolean = { event ->
        if (event.type != KeyEventType.KeyDown) {
            false
        } else {
            when (event.key) {
                Key.DirectionDown -> {
                    moveHighlight(1)
                    suggestions.isNotEmpty()
                }
                Key.DirectionUp -> {
                    moveHighlight(-1)
                    suggestions.isNotEmpty()
                }
                Key.Enter -> {
                    if (visible || onSelectUnmatched != null) {
                        commit()
                        true
                    } else {
                        false
                    }
                }
                Key.Escape -> {
                    if (visible) {
                        visible = false
                        highlightedIndex = -1
                        true
                    } else {
                        false
                    }
                }
                else -> false
            }
        }
    }

    Box(modifier.onPreviewKeyEvent(keyHandler)) {
        ElInput(
            value = value,
            onValueChange = {
                onValueChange(it)
                visible = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { fieldWidth = with(density) { it.width.toDp() } },
            placeholder = effectivePlaceholder,
            size = resolvedSize,
            disabled = disabled,
            clearable = clearable,
            prefix = prefix ?: defaultPrefix,
            suffix = suffix,
            onFocusChanged = { focused = it },
        )
        ElPopper(
            visible = visible && (loading || suggestions.isNotEmpty() || effectiveEmptyText.isNotEmpty() || header != null || footer != null),
            onDismissRequest = { visible = false },
            placement = placement,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 4.dp),
        ) {
            ElPopperSurface(
                modifier = when {
                    dropdownWidth != null -> Modifier.width(dropdownWidth)
                    fitInputWidth -> Modifier.width(fieldWidth.coerceAtLeast(200.dp))
                    else -> Modifier.widthIn(min = 200.dp)
                },
            ) {
                Column(Modifier.heightIn(max = 300.dp)) {
                    if (header != null) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp)) { header() }
                    }
                    if (loading && !hideLoading) {
                        if (loadingContent != null) {
                            Box(
                                Modifier.fillMaxWidth().heightIn(min = 60.dp),
                                contentAlignment = Alignment.Center,
                            ) { loadingContent() }
                        } else {
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                ElIcon(ElIcons.Loading, size = 14.dp, tint = theme.colors.primary, spin = true)
                                Spacer(Modifier.width(8.dp))
                                BasicText(
                                    text = locale.loading,
                                    style = theme.typography.small.copy(color = theme.colors.textSecondary),
                                )
                            }
                        }
                    } else if (suggestions.isEmpty()) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)) {
                            BasicText(
                                text = effectiveEmptyText,
                                style = theme.typography.small.copy(color = theme.colors.textSecondary),
                            )
                        }
                    } else {
                        LazyColumn(Modifier.heightIn(max = 274.dp), state = listState) {
                            itemsIndexed(suggestions) { index, item ->
                                val highlighted = index == highlightedIndex
                                val itemInteraction = remember(index) { MutableInteractionSource() }
                                val hovered by itemInteraction.collectIsHoveredAsState()
                                LaunchedEffect(hovered) {
                                    if (hovered) highlightedIndex = index
                                }
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (highlighted) theme.colors.fillLight else theme.colors.backgroundOverlay
                                        )
                                        .clickable { select(item) }
                                        .padding(horizontal = 12.dp, vertical = 9.dp),
                                ) {
                                    if (itemContent != null) {
                                        itemContent(item)
                                    } else {
                                        BasicText(
                                            text = valueKey(item),
                                            style = theme.typography.base.copy(
                                                color = if (highlighted) theme.colors.primary else theme.colors.textRegular,
                                                fontSize = resolvedSize.fontSize,
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (footer != null) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp)) { footer() }
                    }
                }
            }
        }
    }
}
