package cn.enaium.elementplus.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
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

data class ElSelectOption<T>(
    val value: T,
    val label: String,
    val disabled: Boolean = false,
    val group: String? = null,
    /** Optional source object for aliases/custom option rendering. */
    val data: Any? = null,
)

/** Map domain items to select options when using the `options` attribute. */
fun <T, K> elSelectOptions(
    items: List<T>,
    value: (T) -> K,
    label: (T) -> String,
    disabled: (T) -> Boolean = { false },
    group: (T) -> String? = { null },
): List<ElSelectOption<K>> = items.map { item ->
    ElSelectOption(value(item), label(item), disabled(item), group(item), data = item)
}

private fun <T> sameSelectValue(a: T, b: T, valueKey: ((T) -> Any?)?): Boolean =
    if (valueKey != null) valueKey(a) == valueKey(b) else a == b

/**
 * `<el-select>` single-value select with local/remote filtering, grouping,
 * header/footer slots, option/tag/label renderers, create-item and value-key
 * support.
 */
@Composable
fun <T> ElSelect(
    value: T?,
    onValueChange: (T?) -> Unit,
    options: List<ElSelectOption<T>>,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: ElSize? = null,
    disabled: Boolean = false,
    clearable: Boolean = true,
    filterable: Boolean = false,
    loading: Boolean = false,
    virtualized: Boolean = false,
    noDataText: String? = null,
    noMatchText: String? = null,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    valueKey: ((T) -> Any?)? = null,
    emptyValues: List<T?> = listOf(null),
    valueOnClear: T? = null,
    onClear: (() -> Unit)? = null,
    remote: Boolean = false,
    remoteMethod: ((String) -> Unit)? = null,
    onQueryChange: ((String) -> Unit)? = null,
    reserveKeyword: Boolean = true,
    allowCreate: Boolean = false,
    defaultFirstOption: Boolean = false,
    onCreateOption: ((String) -> ElSelectOption<T>?)? = null,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    optionContent: (@Composable (ElSelectOption<T>, Boolean) -> Unit)? = null,
    tagContent: (@Composable (ElSelectOption<T>) -> Unit)? = null,
    labelContent: (@Composable (String, T, ElSelectOption<T>) -> Unit)? = null,
    loadingContent: (@Composable () -> Unit)? = null,
    fitInputWidth: Boolean = true,
    dropdownWidth: Dp? = null,
) {
    val globalConfig = LocalElConfig.current
    @Suppress("UNCHECKED_CAST")
    val effectiveEmptyValues: List<T?> = if (emptyValues.size == 1 && emptyValues[0] == null && globalConfig.emptyValues.isNotEmpty()) {
        globalConfig.emptyValues.map { it as? T }
    } else {
        emptyValues
    }
    @Suppress("UNCHECKED_CAST")
    val effectiveValueOnClear: T? = valueOnClear ?: globalConfig.valueOnClear as? T
    val isEmpty = value == null || effectiveEmptyValues.any { it == value }
    val selectedValues = if (isEmpty) emptyList() else listOf(value as T)
    SelectShell(
        modifier = modifier,
        placeholder = placeholder,
        size = size,
        disabled = disabled,
        clearable = clearable,
        filterable = filterable,
        loading = loading,
        multiple = false,
        values = selectedValues,
        sameValue = { a, b -> sameSelectValue(a, b, valueKey) },
        onClear = {
            onValueChange(effectiveValueOnClear)
            onClear?.invoke()
        },
        onRemoveOption = { option -> onValueChange(effectiveValueOnClear) },
        onToggle = { option -> onValueChange(option.value) },
        options = options,
        noDataText = noDataText,
        noMatchText = noMatchText,
        virtualized = virtualized,
        onVisibleChange = onVisibleChange,
        remote = remote,
        remoteMethod = remoteMethod,
        onQueryChange = onQueryChange,
        reserveKeyword = reserveKeyword,
        allowCreate = allowCreate,
        defaultFirstOption = defaultFirstOption,
        onCreateOption = onCreateOption,
        header = header,
        footer = footer,
        optionContent = optionContent,
        tagContent = tagContent,
        labelContent = labelContent,
        loadingContent = loadingContent,
        fitInputWidth = fitInputWidth,
        dropdownWidth = dropdownWidth,
    )
}

/** Multiple `<el-select multiple>` with tags, collapse and create support. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> ElSelectMultiple(
    values: List<T>,
    onValueChange: (List<T>) -> Unit,
    options: List<ElSelectOption<T>>,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: ElSize? = null,
    disabled: Boolean = false,
    clearable: Boolean = true,
    filterable: Boolean = false,
    loading: Boolean = false,
    virtualized: Boolean = false,
    multipleLimit: Int = 0,
    collapseTags: Boolean = false,
    maxCollapseTags: Int = 1,
    noDataText: String? = null,
    noMatchText: String? = null,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    valueKey: ((T) -> Any?)? = null,
    onClear: (() -> Unit)? = null,
    remote: Boolean = false,
    remoteMethod: ((String) -> Unit)? = null,
    onQueryChange: ((String) -> Unit)? = null,
    reserveKeyword: Boolean = true,
    allowCreate: Boolean = false,
    defaultFirstOption: Boolean = false,
    onCreateOption: ((String) -> ElSelectOption<T>?)? = null,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    optionContent: (@Composable (ElSelectOption<T>, Boolean) -> Unit)? = null,
    tagContent: (@Composable (ElSelectOption<T>) -> Unit)? = null,
    labelContent: (@Composable (String, T, ElSelectOption<T>) -> Unit)? = null,
    loadingContent: (@Composable () -> Unit)? = null,
    fitInputWidth: Boolean = true,
    dropdownWidth: Dp? = null,
) {
    SelectShell(
        modifier = modifier,
        placeholder = placeholder,
        size = size,
        disabled = disabled,
        clearable = clearable,
        filterable = filterable,
        loading = loading,
        multiple = true,
        values = values,
        sameValue = { a, b -> sameSelectValue(a, b, valueKey) },
        onClear = {
            onValueChange(emptyList())
            onClear?.invoke()
        },
        onRemoveOption = { option -> onValueChange(values.filterNot { sameSelectValue(it, option.value, valueKey) }) },
        onToggle = { option ->
            val exists = values.any { sameSelectValue(it, option.value, valueKey) }
            val next = if (exists) {
                values.filterNot { sameSelectValue(it, option.value, valueKey) }
            } else if (multipleLimit > 0 && values.size >= multipleLimit) {
                values
            } else {
                values + option.value
            }
            onValueChange(next)
        },
        options = options,
        noDataText = noDataText,
        noMatchText = noMatchText,
        virtualized = virtualized,
        onVisibleChange = onVisibleChange,
        collapseTags = collapseTags,
        maxCollapseTags = maxCollapseTags,
        remote = remote,
        remoteMethod = remoteMethod,
        onQueryChange = onQueryChange,
        reserveKeyword = reserveKeyword,
        allowCreate = allowCreate,
        defaultFirstOption = defaultFirstOption,
        onCreateOption = onCreateOption,
        header = header,
        footer = footer,
        optionContent = optionContent,
        tagContent = tagContent,
        labelContent = labelContent,
        loadingContent = loadingContent,
        fitInputWidth = fitInputWidth,
        dropdownWidth = dropdownWidth,
    )
}

/** `<el-select-v2>` single-value select backed by a lazy dropdown. */
@Composable
fun <T> ElVirtualizedSelect(
    value: T?,
    onValueChange: (T?) -> Unit,
    options: List<ElSelectOption<T>>,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: ElSize? = null,
    disabled: Boolean = false,
    clearable: Boolean = true,
    filterable: Boolean = false,
    loading: Boolean = false,
    noDataText: String? = null,
    noMatchText: String? = null,
    valueKey: ((T) -> Any?)? = null,
    emptyValues: List<T?> = listOf(null),
    valueOnClear: T? = null,
    onClear: (() -> Unit)? = null,
    remote: Boolean = false,
    remoteMethod: ((String) -> Unit)? = null,
    onQueryChange: ((String) -> Unit)? = null,
    reserveKeyword: Boolean = true,
    allowCreate: Boolean = false,
    defaultFirstOption: Boolean = false,
    onCreateOption: ((String) -> ElSelectOption<T>?)? = null,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    optionContent: (@Composable (ElSelectOption<T>, Boolean) -> Unit)? = null,
    labelContent: (@Composable (String, T, ElSelectOption<T>) -> Unit)? = null,
    loadingContent: (@Composable () -> Unit)? = null,
    fitInputWidth: Boolean = true,
    dropdownWidth: Dp? = null,
) = ElSelect(
    value = value,
    onValueChange = onValueChange,
    options = options,
    modifier = modifier,
    placeholder = placeholder,
    size = size,
    disabled = disabled,
    clearable = clearable,
    filterable = filterable,
    loading = loading,
    virtualized = true,
    noDataText = noDataText,
    noMatchText = noMatchText,
    valueKey = valueKey,
    emptyValues = emptyValues,
    valueOnClear = valueOnClear,
    onClear = onClear,
    remote = remote,
    remoteMethod = remoteMethod,
    onQueryChange = onQueryChange,
    reserveKeyword = reserveKeyword,
    allowCreate = allowCreate,
    defaultFirstOption = defaultFirstOption,
    onCreateOption = onCreateOption,
    header = header,
    footer = footer,
    optionContent = optionContent,
    labelContent = labelContent,
    loadingContent = loadingContent,
    fitInputWidth = fitInputWidth,
    dropdownWidth = dropdownWidth,
)

/** `<el-select-v2 multiple>` backed by a lazy option list. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> ElVirtualizedSelectMultiple(
    values: List<T>,
    onValueChange: (List<T>) -> Unit,
    options: List<ElSelectOption<T>>,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: ElSize? = null,
    disabled: Boolean = false,
    clearable: Boolean = true,
    filterable: Boolean = false,
    loading: Boolean = false,
    multipleLimit: Int = 0,
    collapseTags: Boolean = false,
    maxCollapseTags: Int = 1,
    noDataText: String? = null,
    noMatchText: String? = null,
    valueKey: ((T) -> Any?)? = null,
    onClear: (() -> Unit)? = null,
    remote: Boolean = false,
    remoteMethod: ((String) -> Unit)? = null,
    onQueryChange: ((String) -> Unit)? = null,
    reserveKeyword: Boolean = true,
    allowCreate: Boolean = false,
    defaultFirstOption: Boolean = false,
    onCreateOption: ((String) -> ElSelectOption<T>?)? = null,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    optionContent: (@Composable (ElSelectOption<T>, Boolean) -> Unit)? = null,
    tagContent: (@Composable (ElSelectOption<T>) -> Unit)? = null,
    labelContent: (@Composable (String, T, ElSelectOption<T>) -> Unit)? = null,
    loadingContent: (@Composable () -> Unit)? = null,
    fitInputWidth: Boolean = true,
    dropdownWidth: Dp? = null,
) = ElSelectMultiple(
    values = values,
    onValueChange = onValueChange,
    options = options,
    modifier = modifier,
    placeholder = placeholder,
    size = size,
    disabled = disabled,
    clearable = clearable,
    filterable = filterable,
    loading = loading,
    virtualized = true,
    multipleLimit = multipleLimit,
    collapseTags = collapseTags,
    maxCollapseTags = maxCollapseTags,
    noDataText = noDataText,
    noMatchText = noMatchText,
    valueKey = valueKey,
    onClear = onClear,
    remote = remote,
    remoteMethod = remoteMethod,
    onQueryChange = onQueryChange,
    reserveKeyword = reserveKeyword,
    allowCreate = allowCreate,
    defaultFirstOption = defaultFirstOption,
    onCreateOption = onCreateOption,
    header = header,
    footer = footer,
    optionContent = optionContent,
    tagContent = tagContent,
    labelContent = labelContent,
    loadingContent = loadingContent,
    fitInputWidth = fitInputWidth,
    dropdownWidth = dropdownWidth,
)

private data class SelectDropdownEntry<T>(
    val group: String? = null,
    val option: ElSelectOption<T>? = null,
    val createText: String? = null,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> SelectShell(
    modifier: Modifier,
    placeholder: String?,
    size: ElSize?,
    disabled: Boolean,
    clearable: Boolean,
    filterable: Boolean,
    loading: Boolean,
    multiple: Boolean,
    values: List<T>,
    sameValue: (T, T) -> Boolean,
    onClear: () -> Unit,
    onRemoveOption: (ElSelectOption<T>) -> Unit,
    onToggle: (ElSelectOption<T>) -> Unit,
    options: List<ElSelectOption<T>>,
    noDataText: String?,
    noMatchText: String?,
    virtualized: Boolean,
    onVisibleChange: ((Boolean) -> Unit)?,
    collapseTags: Boolean = false,
    maxCollapseTags: Int = 1,
    remote: Boolean = false,
    remoteMethod: ((String) -> Unit)? = null,
    onQueryChange: ((String) -> Unit)? = null,
    reserveKeyword: Boolean = true,
    allowCreate: Boolean = false,
    defaultFirstOption: Boolean = false,
    onCreateOption: ((String) -> ElSelectOption<T>?)? = null,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    optionContent: (@Composable (ElSelectOption<T>, Boolean) -> Unit)? = null,
    tagContent: (@Composable (ElSelectOption<T>) -> Unit)? = null,
    labelContent: (@Composable (String, T, ElSelectOption<T>) -> Unit)? = null,
    loadingContent: (@Composable () -> Unit)? = null,
    fitInputWidth: Boolean = true,
    dropdownWidth: Dp? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedSize = resolveElSize(size)
    val shape = RoundedCornerShape(theme.radius.base)
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var fieldWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val interaction = remember { MutableInteractionSource() }
    val hoverInteraction by interaction.collectIsHoveredAsState()
    val createdOptions = remember { mutableStateListOf<ElSelectOption<T>>() }

    fun setExpanded(value: Boolean) {
        expanded = value
        if (!value) query = ""
        onVisibleChange?.invoke(value)
    }
    LaunchedEffect(expanded) {
        if (!expanded) query = ""
    }

    val allOptions = remember(options, createdOptions.toList()) {
        val result = options.toMutableList()
        createdOptions.forEach { created ->
            if (result.none { sameValue(it.value, created.value) }) result += created
        }
        result
    }
    val selectedOptions = allOptions.filter { option -> values.any { sameValue(it, option.value) } }
    val visibleOptions = if (multiple && collapseTags) selectedOptions.take(maxCollapseTags) else selectedOptions
    val hiddenCount = (selectedOptions.size - visibleOptions.size).coerceAtLeast(0)

    val filtered = if (query.isBlank()) allOptions else if (remote) {
        allOptions
    } else {
        allOptions.filter { it.label.contains(query, ignoreCase = true) }
    }
    val entries = buildList {
        var lastGroup: String? = null
        filtered.forEach { option ->
            if (option.group != null && option.group != lastGroup) {
                lastGroup = option.group
                add(SelectDropdownEntry<T>(group = option.group))
            }
            add(SelectDropdownEntry(option = option))
        }
        val createText = query.trim()
        val canCreate = allowCreate &&
            onCreateOption != null &&
            createText.isNotEmpty() &&
            filtered.none { it.label.equals(createText, ignoreCase = true) }
        if (canCreate) add(SelectDropdownEntry(createText = createText))
    }
    val popupWidth = dropdownWidth ?: if (fitInputWidth) fieldWidth.coerceAtLeast(160.dp) else 220.dp
    val height = resolvedSize.height
    val effectivePlaceholder = placeholder ?: locale.select.placeholder
    val filterPlaceholder = locale.select.filterPlaceholder.ifBlank { locale.select.placeholder }
    val textStyle = theme.typography.base.copy(
        fontSize = resolvedSize.fontSize,
        color = if (disabled) colors.disabledText else colors.textRegular,
    )
    val showFilter = filterable
    val arrowRotation by animateFloatAsState(if (expanded) 180f else 0f, label = "select-arrow")
    val borderColor = when {
        disabled -> colors.disabledBorder
        expanded -> colors.primary
        hoverInteraction -> colors.textDisabled
        else -> colors.border
    }

    fun finishSelection() {
        if (!multiple) setExpanded(false) else if (!reserveKeyword) query = ""
    }

    fun createFromQuery() {
        val text = query.trim()
        if (text.isEmpty()) return
        val exact = allOptions.firstOrNull { it.label.equals(text, ignoreCase = true) && !it.disabled }
        if (exact != null) {
            onToggle(exact)
            finishSelection()
            return
        }
        val created = onCreateOption?.invoke(text) ?: return
        if (createdOptions.none { sameValue(it.value, created.value) }) createdOptions += created
        onToggle(created)
        finishSelection()
    }

    fun commitQuery() {
        if (allowCreate && onCreateOption != null && query.isNotBlank()) {
            createFromQuery()
            return
        }
        val first = filtered.firstOrNull { !it.disabled }
        if (first != null && (defaultFirstOption || query.isNotBlank())) {
            onToggle(first)
            finishSelection()
        }
    }

    Box(modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .onSizeChanged { fieldWidth = with(density) { it.width.toDp() } }
                .clip(shape)
                .background(if (disabled) colors.disabledBackground else colors.fillBlank)
                .border(1.dp, borderColor, shape)
                .hoverable(interaction)
                .clickable(enabled = !disabled) { setExpanded(!expanded) }
                .padding(start = 11.dp, end = 30.dp, top = if (multiple) 3.dp else 0.dp, bottom = if (multiple) 3.dp else 0.dp)
                .defaultMinSize(minHeight = height),
        ) {
            if (multiple && selectedOptions.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                ) {
                    visibleOptions.forEach { option ->
                        when {
                            tagContent != null -> tagContent.invoke(option)
                            labelContent != null -> SelectLabelTag(
                                option = option,
                                disabled = disabled,
                                onClose = { onRemoveOption(option) },
                            ) {
                                labelContent.invoke(option.label, option.value, option)
                            }
                            else -> ElTag(
                                text = option.label,
                                type = cn.enaium.elementplus.theme.ElType.Info,
                                size = ElSize.Small,
                                closable = !disabled,
                                onClose = { onRemoveOption(option) },
                            )
                        }
                    }
                    if (hiddenCount > 0) {
                        ElTag(
                            text = "+$hiddenCount",
                            type = cn.enaium.elementplus.theme.ElType.Info,
                            size = ElSize.Small,
                        )
                    }
                }
            } else {
                Row(Modifier.height(height - 2.dp), verticalAlignment = Alignment.CenterVertically) {
                    val selected = selectedOptions.firstOrNull()
                    if (selected != null && labelContent != null) {
                        labelContent.invoke(selected.label, selected.value, selected)
                    } else if (selected != null) {
                        BasicText(
                            text = selected.label,
                            style = textStyle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    } else {
                        BasicText(
                            text = effectivePlaceholder,
                            style = textStyle.copy(color = colors.textPlaceholder),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
        if (clearable && values.isNotEmpty() && hoverInteraction && !disabled) {
            ElIcon(
                ElIcons.CircleClose,
                size = 14.dp,
                tint = colors.textDisabled,
                contentDescription = locale.clear,
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 10.dp).clickable { onClear() },
            )
        } else {
            ElIcon(
                ElIcons.ArrowDown,
                size = 12.dp,
                tint = colors.textPlaceholder,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 10.dp)
                    .rotate(arrowRotation),
            )
        }
        ElPopper(
            visible = expanded,
            onDismissRequest = { setExpanded(false) },
            placement = ElPlacement.BottomStart,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 4.dp),
            focusable = true,
        ) {
            ElPopperSurface(modifier = Modifier.width(popupWidth)) {
                Column(Modifier.heightIn(max = 360.dp)) {
                    if (header != null) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp)) { header() }
                    }
                    if (showFilter) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp)) {
                            BasicTextField(
                                value = query,
                                onValueChange = { value ->
                                    query = value
                                    onQueryChange?.invoke(value)
                                    if (remote) remoteMethod?.invoke(value)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                textStyle = theme.typography.base.copy(color = colors.textRegular),
                                cursorBrush = SolidColor(colors.primary),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { commitQuery() }),
                                decorationBox = { inner ->
                                    Box {
                                        if (query.isEmpty()) {
                                            BasicText(
                                                text = filterPlaceholder,
                                                style = theme.typography.base.copy(color = colors.textPlaceholder),
                                            )
                                        }
                                        inner()
                                    }
                                },
                            )
                        }
                    }
                    when {
                        loading -> {
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
                                    ElIcon(ElIcons.Loading, size = 14.dp, tint = colors.primary, spin = true)
                                    Spacer(Modifier.width(8.dp))
                                    BasicText(
                                        text = locale.loading,
                                        style = theme.typography.small.copy(color = colors.textSecondary),
                                    )
                                }
                            }
                        }
                        entries.isEmpty() -> {
                            Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)) {
                                BasicText(
                                    text = if (query.isBlank()) (noDataText ?: locale.select.noData) else (noMatchText ?: locale.select.noMatch),
                                    style = theme.typography.small.copy(color = colors.textSecondary),
                                )
                            }
                        }
                        virtualized -> {
                            LazyColumn(Modifier.heightIn(max = 240.dp)) {
                                items(entries) { entry ->
                                    SelectDropdownEntryContent(
                                        entry = entry,
                                        selected = entry.option?.let { option ->
                                            values.any { sameValue(it, option.value) }
                                        } == true,
                                        multiple = multiple,
                                        optionContent = optionContent,
                                        onToggle = { option ->
                                            if (!option.disabled) {
                                                onToggle(option)
                                                finishSelection()
                                            }
                                        },
                                        onCreate = ::createFromQuery,
                                    )
                                }
                            }
                        }
                        else -> {
                            Column(Modifier.heightIn(max = 240.dp).verticalScroll(rememberScrollState())) {
                                entries.forEach { entry ->
                                    SelectDropdownEntryContent(
                                        entry = entry,
                                        selected = entry.option?.let { option ->
                                            values.any { sameValue(it, option.value) }
                                        } == true,
                                        multiple = multiple,
                                        optionContent = optionContent,
                                        onToggle = { option ->
                                            if (!option.disabled) {
                                                onToggle(option)
                                                finishSelection()
                                            }
                                        },
                                        onCreate = ::createFromQuery,
                                    )
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

@Composable
private fun <T> SelectDropdownEntryContent(
    entry: SelectDropdownEntry<T>,
    selected: Boolean,
    multiple: Boolean,
    optionContent: (@Composable (ElSelectOption<T>, Boolean) -> Unit)?,
    onToggle: (ElSelectOption<T>) -> Unit,
    onCreate: () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    when {
        entry.group != null -> {
            BasicText(
                text = entry.group,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = theme.typography.extraSmall.copy(color = colors.textSecondary),
            )
        }
        entry.createText != null -> {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onCreate)
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ElIcon(ElIcons.Plus, size = 14.dp, tint = colors.textSecondary)
                Spacer(Modifier.width(6.dp))
                BasicText(
                    text = "Create \"${entry.createText}\"",
                    style = theme.typography.base.copy(color = colors.textRegular),
                )
            }
        }
        entry.option != null -> SelectOptionRow(
            option = entry.option,
            selected = selected,
            multiple = multiple,
            optionContent = optionContent,
            onToggle = { onToggle(entry.option) },
        )
    }
}

@Composable
private fun <T> SelectLabelTag(
    option: ElSelectOption<T>,
    disabled: Boolean,
    onClose: () -> Unit,
    content: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    Row(
        Modifier
            .background(colors.fillLight, RoundedCornerShape(theme.radius.small))
            .padding(start = 8.dp, end = 4.dp, top = 3.dp, bottom = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
        if (!disabled) {
            Spacer(Modifier.width(4.dp))
            ElIcon(
                ElIcons.CircleClose,
                size = 12.dp,
                tint = colors.textPlaceholder,
                modifier = Modifier.clickable { onClose() },
            )
        }
    }
}

@Composable
private fun <T> SelectOptionRow(
    option: ElSelectOption<T>,
    selected: Boolean,
    multiple: Boolean,
    optionContent: (@Composable (ElSelectOption<T>, Boolean) -> Unit)?,
    onToggle: () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val interaction = remember(option) { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                when {
                    option.disabled -> colors.fillLighter
                    hovered -> colors.fillLight
                    else -> colors.backgroundOverlay
                }
            )
            .clickable(enabled = !option.disabled, onClick = onToggle)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (multiple) {
            Box(
                Modifier
                    .size(14.dp)
                    .background(if (selected) colors.primary else colors.fillBlank, RoundedCornerShape(2.dp))
                    .border(1.dp, if (selected) colors.primary else colors.border, RoundedCornerShape(2.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (selected) ElIcon(ElIcons.Check, size = 10.dp, tint = colors.white)
            }
            Spacer(Modifier.width(8.dp))
        }
        if (optionContent != null) {
            Box(Modifier.weight(1f)) { optionContent(option, selected) }
        } else {
            BasicText(
                text = option.label,
                modifier = Modifier.weight(1f),
                style = theme.typography.base.copy(
                    color = when {
                        option.disabled -> colors.textDisabled
                        selected -> colors.primary
                        else -> colors.textRegular
                    },
                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (!multiple && selected) {
            ElIcon(ElIcons.Check, size = 14.dp, tint = colors.primary)
        }
    }
}
