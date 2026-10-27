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
import androidx.compose.foundation.layout.widthIn
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
)

/**
 * `<el-select>` single-value select. Use [ElSelectMultiple] for multiple
 * selection. Options are grouped when [ElSelectOption.group] is set.
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
) {
    SelectShell(
        modifier = modifier,
        placeholder = placeholder,
        size = size,
        disabled = disabled,
        clearable = clearable,
        filterable = filterable,
        loading = loading,
        multiple = false,
        selectedLabels = listOfNotNull(options.firstOrNull { it.value == value }?.label),
        selectedOptions = emptyList(),
        onClear = { onValueChange(null) },
        onRemoveOption = { },
        noDataText = noDataText,
        noMatchText = noMatchText,
        virtualized = virtualized,
        options = options,
        isSelected = { it.value == value },
        onToggle = { option ->
            onValueChange(option.value)
        },
        onVisibleChange = onVisibleChange,
        singleValueSelected = value != null,
    )
}

/** Multiple `<el-select multiple>` with tag display. */
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
) {
    val selected = options.filter { option -> values.contains(option.value) }
    val visibleTags = if (collapseTags) selected.take(maxCollapseTags) else selected
    val hiddenCount = selected.size - visibleTags.size
    SelectShell(
        modifier = modifier,
        placeholder = placeholder,
        size = size,
        disabled = disabled,
        clearable = clearable,
        filterable = filterable,
        loading = loading,
        multiple = true,
        selectedLabels = visibleTags.map { it.label },
        selectedOptions = visibleTags,
        onClear = { onValueChange(emptyList()) },
        onRemoveOption = { option -> onValueChange(values - option.value) },
        noDataText = noDataText,
        noMatchText = noMatchText,
        virtualized = virtualized,
        options = options,
        isSelected = { values.contains(it.value) },
        onToggle = { option ->
            val next = if (values.contains(option.value)) values - option.value else {
                if (multipleLimit > 0 && values.size >= multipleLimit) values else values + option.value
            }
            onValueChange(next)
        },
        onVisibleChange = onVisibleChange,
        singleValueSelected = values.isNotEmpty(),
        multipleTags = true,
        hiddenCount = hiddenCount,
    )
}

/** `<el-select-v2 virtualized>`: same API, always uses a lazy dropdown. */
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
    filterable: Boolean = true,
    loading: Boolean = false,
    noDataText: String? = null,
    noMatchText: String? = null,
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
    selectedLabels: List<String>,
    selectedOptions: List<ElSelectOption<T>>,
    onClear: () -> Unit,
    onRemoveOption: (ElSelectOption<T>) -> Unit,
    noDataText: String?,
    noMatchText: String?,
    virtualized: Boolean,
    options: List<ElSelectOption<T>>,
    isSelected: (ElSelectOption<T>) -> Boolean,
    onToggle: (ElSelectOption<T>) -> Unit,
    onVisibleChange: ((Boolean) -> Unit)?,
    singleValueSelected: Boolean,
    multipleTags: Boolean = false,
    hiddenCount: Int = 0,
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

    fun setExpanded(value: Boolean) {
        expanded = value
        if (!value) query = ""
        onVisibleChange?.invoke(value)
    }
    LaunchedEffect(expanded) {
        if (!expanded) query = ""
    }
    val filtered = if (query.isBlank()) options else options.filter {
        it.label.contains(query, ignoreCase = true)
    }
    val showFilter = filterable
    val arrowRotation by animateFloatAsState(if (expanded) 180f else 0f, label = "select-arrow")
    val borderColor = when {
        disabled -> colors.disabledBorder
        expanded -> colors.primary
        hoverInteraction -> colors.textDisabled
        else -> colors.border
    }
    val height = resolvedSize.height
    val effectivePlaceholder = placeholder ?: locale.select.placeholder
    val filterPlaceholder = locale.select.filterPlaceholder.ifBlank { locale.select.placeholder }
    val textStyle = theme.typography.base.copy(fontSize = resolvedSize.fontSize, color = if (disabled) colors.disabledText else colors.textRegular)

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
                .padding(start = 11.dp, end = 30.dp, top = if (multipleTags) 3.dp else 0.dp, bottom = if (multipleTags) 3.dp else 0.dp)
                .defaultMinSize(minHeight = height),
        ) {
            if (multipleTags && selectedLabels.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                ) {
                    selectedOptions.forEachIndexed { index, option ->
                        ElTag(
                            text = selectedLabels.getOrElse(index) { "" },
                            type = cn.enaium.elementplus.theme.ElType.Info,
                            size = ElSize.Small,
                            closable = !disabled,
                            onClose = { onRemoveOption(option) },
                        )
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
                    if (selectedLabels.isNotEmpty()) {
                        BasicText(
                            text = selectedLabels.first(),
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
        // Clear / arrow indicators.
        if (clearable && singleValueSelected && hoverInteraction && !disabled) {
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
            ElPopperSurface(modifier = Modifier.width(fieldWidth.coerceAtLeast(160.dp))) {
                Column(Modifier.heightIn(max = 274.dp)) {
                    if (showFilter) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp)) {
                            BasicTextField(
                                value = query,
                                onValueChange = { query = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                textStyle = theme.typography.base.copy(color = colors.textRegular),
                                cursorBrush = SolidColor(colors.primary),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    val first = filtered.firstOrNull { !it.disabled }
                                    if (first != null) {
                                        onToggle(first)
                                        if (!multiple) setExpanded(false)
                                    }
                                }),
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
                    if (loading) {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ElIcon(ElIcons.Loading, size = 14.dp, tint = colors.primary, spin = true)
                            Spacer(Modifier.width(8.dp))
                            BasicText(text = locale.loading, style = theme.typography.small.copy(color = colors.textSecondary))
                        }
                    } else if (filtered.isEmpty()) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)) {
                            BasicText(
                                text = if (query.isBlank()) (noDataText ?: locale.select.noData) else (noMatchText ?: locale.select.noMatch),
                                style = theme.typography.small.copy(color = colors.textSecondary),
                            )
                        }
                    } else if (virtualized) {
                        LazyColumn(Modifier.heightIn(max = 240.dp)) {
                            items(filtered) { option ->
                                SelectOptionRow(
                                    option = option,
                                    selected = isSelected(option),
                                    multiple = multiple,
                                    onToggle = {
                                        onToggle(option)
                                        if (!multiple) setExpanded(false)
                                    },
                                )
                            }
                        }
                    } else {
                        Column(Modifier.heightIn(max = 240.dp).verticalScroll(rememberScrollState())) {
                            var lastGroup: String? = null
                            filtered.forEach { option ->
                                if (option.group != null && option.group != lastGroup) {
                                    lastGroup = option.group
                                    BasicText(
                                        text = option.group,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = theme.typography.extraSmall.copy(color = colors.textSecondary),
                                    )
                                }
                                SelectOptionRow(
                                    option = option,
                                    selected = isSelected(option),
                                    multiple = multiple,
                                    onToggle = {
                                        onToggle(option)
                                        if (!multiple) setExpanded(false)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> SelectOptionRow(
    option: ElSelectOption<T>,
    selected: Boolean,
    multiple: Boolean,
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
        if (!multiple && selected) {
            ElIcon(ElIcons.Check, size = 14.dp, tint = colors.primary)
        }
    }
}
