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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.launch

data class ElCascaderOption(
    val value: String,
    val label: String,
    val children: List<ElCascaderOption> = emptyList(),
    val disabled: Boolean = false,
    /** Marks a lazily loaded node as a leaf; inferred from [children] when null. */
    val leaf: Boolean? = null,
) {
    val isLeaf: Boolean get() = leaf ?: children.isEmpty()
}

/** How child options are expanded in the panel. */
enum class ElCascaderExpandTrigger { Click, Hover }

/** How checked nodes are displayed as tags in multiple selection mode. */
enum class ElCascaderShowCheckedStrategy { Child, Parent }

/**
 * Behavioral configuration mirroring upstream's `props` object.
 *
 * The `value`/`label`/`children` key mapping is not needed in Compose: options
 * are typed as [ElCascaderOption]. Multiple selection is modeled by the
 * [ElCascaderMultiple] entry point instead of a `multiple` flag.
 */
data class ElCascaderProps(
    val checkStrictly: Boolean = false,
    val lazy: Boolean = false,
    val expandTrigger: ElCascaderExpandTrigger = ElCascaderExpandTrigger.Click,
    val checkOnClickNode: Boolean = false,
    val checkOnClickLeaf: Boolean = true,
    val showPrefix: Boolean = true,
)

/** A selected value rendered as a tag by [ElCascaderMultiple]. */
data class ElCascaderTag(
    val text: String,
    val path: List<String>,
    val pathLabels: List<String>,
    val onRemove: () -> Unit,
)

/**
 * Loads children for a lazily expanded node. [path] is the path of the expanded
 * node (`emptyList()` for the root) and [level] is its zero-based depth.
 */
typealias ElCascaderLazyLoad = suspend (path: List<String>, level: Int) -> List<ElCascaderOption>

/**
 * `<el-cascader>` with single-path selection, cascading columns, search,
 * optional lazy loading and check-strictly (any level) selection.
 */
@Composable
fun ElCascader(
    value: List<String>,
    onValueChange: (List<String>) -> Unit,
    options: List<ElCascaderOption>,
    modifier: Modifier = Modifier,
    separator: String = " / ",
    placeholder: String? = null,
    size: ElSize? = null,
    disabled: Boolean = false,
    clearable: Boolean = true,
    filterable: Boolean = true,
    showAllLevels: Boolean = true,
    emptyText: String? = null,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    props: ElCascaderProps = ElCascaderProps(),
    lazyLoad: ElCascaderLazyLoad? = null,
    clearIcon: ImageVector? = null,
    prefix: (@Composable () -> Unit)? = null,
    optionContent: (@Composable (ElCascaderOption, List<ElCascaderOption>) -> Unit)? = null,
    suggestionContent: (@Composable (ElCascaderOption, List<ElCascaderOption>) -> Unit)? = null,
    emptyContent: (@Composable () -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    filterMethod: ((ElCascaderOption, String) -> Boolean)? = null,
    fitInputWidth: Boolean = false,
    suggestionWidth: Dp? = null,
    onExpandChange: ((List<String>) -> Unit)? = null,
    onClear: (() -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedSize = resolveElSize(size)
    val shape = RoundedCornerShape(theme.radius.base)
    var expanded by remember { mutableStateOf(false) }
    var filterText by remember { mutableStateOf("") }
    var fieldWidth by remember { mutableStateOf(0.dp) }
    var activePath by remember(value) { mutableStateOf(value) }
    val density = LocalDensity.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val lazyState = rememberCascaderOptionsState(options, props.lazy, lazyLoad)
    val effectiveOptions = lazyState.roots
    val selectedLabels = labelsForPath(effectiveOptions, value)
    val label = when {
        selectedLabels.isEmpty() -> null
        showAllLevels -> selectedLabels.joinToString(separator)
        else -> selectedLabels.last()
    }
    val borderColor = when {
        disabled -> colors.disabledBorder
        expanded -> colors.primary
        hovered -> colors.textDisabled
        else -> colors.border
    }
    val arrow by animateFloatAsState(if (expanded) 180f else 0f, label = "cascader-arrow")
    fun clear() {
        onValueChange(emptyList())
        onClear?.invoke()
    }
    fun expandPath(pathNodes: List<ElCascaderOption>) {
        val path = pathNodes.map { it.value }
        activePath = path
        onExpandChange?.invoke(path)
        lazyState.ensureChildren(path, pathNodes.size)
    }
    fun setExpanded(next: Boolean) {
        expanded = next
        if (!next) {
            filterText = ""
        } else {
            activePath = value
            if (props.lazy && lazyLoad != null && effectiveOptions.isEmpty()) {
                lazyState.ensureChildren(emptyList(), 0)
            }
        }
        onVisibleChange?.invoke(next)
    }
    val effectivePlaceholder = placeholder ?: locale.cascader.placeholder
    val filterPlaceholder = locale.cascader.filterPlaceholder.ifBlank { locale.cascader.placeholder }
    val filtered = remember(effectiveOptions, filterText, filterMethod, props.checkStrictly) {
        if (filterText.isBlank()) {
            emptyList()
        } else {
            flattenCascaderMatches(effectiveOptions, filterText, filterMethod, showAllLevels=showAllLevels)
        }
    }
    val suggestionContentWidth = rememberCascaderSuggestionWidth(filtered, separator, resolvedSize)
    val popupWidth = when {
        suggestionWidth != null -> Modifier.width(suggestionWidth)
        fitInputWidth -> Modifier.width(fieldWidth.coerceAtLeast(200.dp))
        filterText.isNotBlank() -> Modifier.width(
            fieldWidth.coerceAtLeast(220.dp)
                .coerceAtLeast(suggestionContentWidth)
                .coerceAtMost(560.dp)
        )
        else -> Modifier.widthIn(min = fieldWidth.coerceAtLeast(260.dp))
    }
    val rootLoading = props.lazy && lazyState.loadingKeys.contains(cascaderPathKey(emptyList()))
    Box(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .onSizeChanged { fieldWidth = with(density) { it.width.toDp() } }
                .height(resolvedSize.height)
                .clip(shape)
                .background(if (disabled) colors.disabledBackground else colors.fillBlank, shape)
                .border(1.dp, borderColor, shape)
                .hoverable(interaction)
                .clickable(enabled = !disabled) { setExpanded(!expanded) }
                .padding(horizontal = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (prefix != null) {
                prefix()
                Spacer(Modifier.width(6.dp))
            }
            BasicText(
                text = label ?: effectivePlaceholder,
                modifier = Modifier.weight(1f),
                style = theme.typography.base.copy(
                    fontSize = resolvedSize.fontSize,
                    color = if (label != null) colors.textRegular else colors.textPlaceholder,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (clearable && label != null && hovered && !disabled) {
                ElIcon(
                    clearIcon ?: ElIcons.CircleClose,
                    size = 14.dp,
                    tint = colors.textPlaceholder,
                    contentDescription = locale.clear,
                    modifier = Modifier.clickable { clear() },
                )
            } else {
                ElIcon(
                    ElIcons.ArrowDown,
                    size = 12.dp,
                    tint = colors.textPlaceholder,
                    modifier = Modifier.rotate(arrow),
                )
            }
        }
        ElPopper(
            visible = expanded,
            onDismissRequest = { setExpanded(false) },
            placement = ElPlacement.BottomStart,
            offset = DpOffset(0.dp, 4.dp),
            focusable = true,
        ) {
            ElPopperSurface(popupWidth) {
                Column(Modifier.heightIn(max = 320.dp)) {
                    if (header != null) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp)) { header() }
                    }
                    if (filterable) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp)) {
                            BasicTextField(
                                value = filterText,
                                onValueChange = { filterText = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                textStyle = theme.typography.base.copy(color = colors.textRegular),
                                cursorBrush = SolidColor(colors.primary),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                decorationBox = { inner ->
                                    Box {
                                        if (filterText.isEmpty()) {
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
                    if (filterText.isNotBlank()) {
                        if (filtered.isEmpty()) {
                            CascaderEmpty(emptyText ?: locale.cascader.noMatch, emptyContent)
                        } else {
                            Column(Modifier.verticalScroll(rememberScrollState())) {
                                filtered.forEach { item ->
                                    val itemValues = item.path.map { it.value }
                                    val selected = value == itemValues
                                    CascaderSuggestionRow(
                                        text = item.path.joinToString(separator) { it.label },
                                        selected = selected,
                                        disabled = item.option.disabled,
                                        option = item.option,
                                        path = item.path,
                                        suggestionContent = suggestionContent,
                                        onClick = {
                                            onValueChange(itemValues)
                                            setExpanded(false)
                                        },
                                    )
                                }
                            }
                        }
                    } else if (effectiveOptions.isEmpty()) {
                        if (rootLoading) {
                            CascaderLoadingRow(locale.cascader.loading)
                        } else {
                            CascaderEmpty(emptyText ?: locale.cascader.noData, emptyContent)
                        }
                    } else {
                        CascaderColumns(
                            options = effectiveOptions,
                            activePath = activePath,
                            selectedPaths = if (value.isEmpty()) emptyList() else listOf(value),
                            multiple = false,
                            checkStrictly = props.checkStrictly,
                            props = props,
                            loadingKeys = lazyState.loadingKeys,
                            loadingText = locale.cascader.loading,
                            onNodeClick = { pathNodes ->
                                val node = pathNodes.last()
                                if (!node.disabled) {
                                    if (props.checkStrictly || node.isLeaf) {
                                        onValueChange(pathNodes.map { it.value })
                                        setExpanded(false)
                                    } else {
                                        expandPath(pathNodes)
                                    }
                                }
                            },
                            onToggleCheck = { pathNodes, _ ->
                                if (!pathNodes.last().disabled) {
                                    onValueChange(pathNodes.map { it.value })
                                    setExpanded(false)
                                }
                            },
                            onHoverNode = { pathNodes ->
                                val node = pathNodes.last()
                                if (props.expandTrigger == ElCascaderExpandTrigger.Hover && !node.disabled && !node.isLeaf) {
                                    expandPath(pathNodes)
                                }
                            },
                            optionContent = optionContent,
                        )
                    }
                    if (footer != null) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp)) { footer() }
                    }
                }
            }
        }
    }
}

/**
 * `<el-cascader multiple>`: every selected item is a full path, so the value
 * type is `List<List<String>>`. Supports checkboxes, collapsed tags, custom
 * tags, `show-checked-strategy`, any-level selection and lazy loading.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ElCascaderMultiple(
    values: List<List<String>>,
    onValuesChange: (List<List<String>>) -> Unit,
    options: List<ElCascaderOption>,
    modifier: Modifier = Modifier,
    separator: String = " / ",
    placeholder: String? = null,
    size: ElSize? = null,
    disabled: Boolean = false,
    clearable: Boolean = true,
    filterable: Boolean = true,
    showAllLevels: Boolean = true,
    emptyText: String? = null,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    props: ElCascaderProps = ElCascaderProps(),
    lazyLoad: ElCascaderLazyLoad? = null,
    collapseTags: Boolean = false,
    collapseTagsTooltip: Boolean = false,
    maxCollapseTags: Int = 1,
    showCheckedStrategy: ElCascaderShowCheckedStrategy = ElCascaderShowCheckedStrategy.Child,
    clearIcon: ImageVector? = null,
    prefix: (@Composable () -> Unit)? = null,
    tagContent: (@Composable (ElCascaderTag) -> Unit)? = null,
    optionContent: (@Composable (ElCascaderOption, List<ElCascaderOption>) -> Unit)? = null,
    suggestionContent: (@Composable (ElCascaderOption, List<ElCascaderOption>) -> Unit)? = null,
    emptyContent: (@Composable () -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    filterMethod: ((ElCascaderOption, String) -> Boolean)? = null,
    fitInputWidth: Boolean = false,
    suggestionWidth: Dp? = null,
    tagType: ElType = ElType.Info,
    tagEffect: ElTagEffect = ElTagEffect.Light,
    onExpandChange: ((List<String>) -> Unit)? = null,
    onClear: (() -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedSize = resolveElSize(size)
    val shape = RoundedCornerShape(theme.radius.base)
    var expanded by remember { mutableStateOf(false) }
    var filterText by remember { mutableStateOf("") }
    var fieldWidth by remember { mutableStateOf(0.dp) }
    var activePath by remember { mutableStateOf(emptyList<String>()) }
    val density = LocalDensity.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val lazyState = rememberCascaderOptionsState(options, props.lazy, lazyLoad)
    val effectiveOptions = lazyState.roots
    val selectedKeys = remember(values) { values.map { cascaderPathKey(it) }.toSet() }
    val borderColor = when {
        disabled -> colors.disabledBorder
        expanded -> colors.primary
        hovered -> colors.textDisabled
        else -> colors.border
    }
    val arrow by animateFloatAsState(if (expanded) 180f else 0f, label = "cascader-multiple-arrow")

    fun pathText(path: List<String>): String {
        val labels = labelsForPath(effectiveOptions, path)
        return when {
            labels.isEmpty() -> path.joinToString(separator)
            showAllLevels -> labels.joinToString(separator)
            else -> labels.last()
        }
    }
    fun toggle(pathNodes: List<ElCascaderOption>, checked: Boolean) {
        val path = pathNodes.map { it.value }
        val node = pathNodes.last()
        val next: List<List<String>> = when {
            props.checkStrictly || node.children.isEmpty() ->
                if (checked) (values + listOf(path)).distinct() else values.filterNot { it == path }
            else -> {
                val leafPaths = cascaderLeafPaths(node, path.dropLast(1))
                if (checked) (values + leafPaths).distinct() else values.filterNot { it in leafPaths }
            }
        }
        onValuesChange(next)
    }
    fun expandPath(pathNodes: List<ElCascaderOption>) {
        val path = pathNodes.map { it.value }
        activePath = path
        onExpandChange?.invoke(path)
        lazyState.ensureChildren(path, pathNodes.size)
    }
    fun setExpanded(next: Boolean) {
        expanded = next
        if (!next) {
            filterText = ""
        } else {
            activePath = values.firstOrNull() ?: emptyList()
            if (props.lazy && lazyLoad != null && effectiveOptions.isEmpty()) {
                lazyState.ensureChildren(emptyList(), 0)
            }
        }
        onVisibleChange?.invoke(next)
    }
    val effectivePlaceholder = placeholder ?: locale.cascader.placeholder
    val filterPlaceholder = locale.cascader.filterPlaceholder.ifBlank { locale.cascader.placeholder }
    val displayPaths = remember(effectiveOptions, values, showCheckedStrategy) {
        displayCascaderPaths(effectiveOptions, values, showCheckedStrategy)
    }
    val tags = displayPaths.map { path ->
        ElCascaderTag(
            text = pathText(path),
            path = path,
            pathLabels = labelsForPath(effectiveOptions, path),
            onRemove = { onValuesChange(values.filterNot { it == path }) },
        )
    }
    val visibleTags = if (collapseTags && tagContent == null) tags.take(maxCollapseTags.coerceAtLeast(0)) else tags
    val hiddenTags = tags.drop(visibleTags.size)
    val filtered = remember(effectiveOptions, filterText, filterMethod) {
        if (filterText.isBlank()) {
            emptyList()
        } else {
            flattenCascaderMatches(effectiveOptions, filterText, filterMethod, showAllLevels = showAllLevels)
        }
    }
    val suggestionContentWidth = rememberCascaderSuggestionWidth(filtered, separator, resolvedSize)
    val popupWidth = when {
        suggestionWidth != null -> Modifier.width(suggestionWidth)
        fitInputWidth -> Modifier.width(fieldWidth.coerceAtLeast(200.dp))
        filterText.isNotBlank() -> Modifier.width(
            fieldWidth.coerceAtLeast(220.dp)
                .coerceAtLeast(suggestionContentWidth)
                .coerceAtMost(560.dp)
        )
        else -> Modifier.widthIn(min = fieldWidth.coerceAtLeast(260.dp))
    }
    val rootLoading = props.lazy && lazyState.loadingKeys.contains(cascaderPathKey(emptyList()))
    Box(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .onSizeChanged { fieldWidth = with(density) { it.width.toDp() } }
                .defaultMinSize(minHeight = resolvedSize.height)
                .clip(shape)
                .background(if (disabled) colors.disabledBackground else colors.fillBlank, shape)
                .border(1.dp, borderColor, shape)
                .hoverable(interaction)
                .clickable(enabled = !disabled) { setExpanded(!expanded) }
                .padding(start = 11.dp, end = 11.dp, top = 3.dp, bottom = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (prefix != null) {
                prefix()
                Spacer(Modifier.width(6.dp))
            }
            if (tags.isEmpty()) {
                BasicText(
                    text = effectivePlaceholder,
                    modifier = Modifier.weight(1f).heightIn(min = resolvedSize.height - 8.dp),
                    style = theme.typography.base.copy(
                        fontSize = resolvedSize.fontSize,
                        color = colors.textPlaceholder,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            } else {
                FlowRow(
                    modifier = Modifier.weight(1f).padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    visibleTags.forEach { tag ->
                        if (tagContent != null) {
                            tagContent.invoke(tag)
                        } else {
                            ElTag(
                                text = tag.text,
                                type = tagType,
                                effect = tagEffect,
                                size = ElSize.Small,
                                closable = !disabled,
                                onClose = tag.onRemove,
                            )
                        }
                    }
                    if (hiddenTags.isNotEmpty()) {
                        CascaderCollapsedTags(hiddenTags, collapseTagsTooltip)
                    }
                }
            }
            if (clearable && tags.isNotEmpty() && hovered && !disabled) {
                ElIcon(
                    clearIcon ?: ElIcons.CircleClose,
                    size = 14.dp,
                    tint = colors.textPlaceholder,
                    contentDescription = locale.clear,
                    modifier = Modifier.clickable {
                        onValuesChange(emptyList())
                        onClear?.invoke()
                    },
                )
            } else {
                ElIcon(
                    ElIcons.ArrowDown,
                    size = 12.dp,
                    tint = colors.textPlaceholder,
                    modifier = Modifier.rotate(arrow),
                )
            }
        }
        ElPopper(
            visible = expanded,
            onDismissRequest = { setExpanded(false) },
            placement = ElPlacement.BottomStart,
            offset = DpOffset(0.dp, 4.dp),
            focusable = true,
        ) {
            ElPopperSurface(popupWidth) {
                Column(Modifier.heightIn(max = 320.dp)) {
                    if (header != null) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp)) { header() }
                    }
                    if (filterable) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp)) {
                            BasicTextField(
                                value = filterText,
                                onValueChange = { filterText = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                textStyle = theme.typography.base.copy(color = colors.textRegular),
                                cursorBrush = SolidColor(colors.primary),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                decorationBox = { inner ->
                                    Box {
                                        if (filterText.isEmpty()) {
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
                    if (filterText.isNotBlank()) {
                        if (filtered.isEmpty()) {
                            CascaderEmpty(emptyText ?: locale.cascader.noMatch, emptyContent)
                        } else {
                            Column(Modifier.verticalScroll(rememberScrollState())) {
                                filtered.forEach { item ->
                                    val itemValues = item.path.map { it.value }
                                    val checkState = cascaderCheckState(
                                        node = item.option,
                                        path = itemValues,
                                        selectedKeys = selectedKeys,
                                        multiple = true,
                                        checkStrictly = props.checkStrictly,
                                    )
                                    CascaderSuggestionRow(
                                        text = item.path.joinToString(separator) { it.label },
                                        selected = checkState.checked,
                                        disabled = item.option.disabled,
                                        option = item.option,
                                        path = item.path,
                                        suggestionContent = suggestionContent,
                                        onClick = {
                                            if (!item.option.disabled) toggle(item.path, !checkState.checked)
                                        },
                                    )
                                }
                            }
                        }
                    } else if (effectiveOptions.isEmpty()) {
                        if (rootLoading) {
                            CascaderLoadingRow(locale.cascader.loading)
                        } else {
                            CascaderEmpty(emptyText ?: locale.cascader.noData, emptyContent)
                        }
                    } else {
                        CascaderColumns(
                            options = effectiveOptions,
                            activePath = activePath,
                            selectedPaths = values,
                            multiple = true,
                            checkStrictly = props.checkStrictly,
                            props = props,
                            loadingKeys = lazyState.loadingKeys,
                            loadingText = locale.cascader.loading,
                            onNodeClick = { pathNodes ->
                                val node = pathNodes.last()
                                if (!node.disabled) {
                                    if (node.children.isNotEmpty()) {
                                        expandPath(pathNodes)
                                    } else {
                                        activePath = pathNodes.map { it.value }
                                        lazyState.ensureChildren(pathNodes.map { it.value }, pathNodes.size)
                                    }
                                    if (props.checkOnClickNode && (!node.isLeaf || props.checkOnClickLeaf)) {
                                        val state = cascaderCheckState(
                                            node = node,
                                            path = pathNodes.map { it.value },
                                            selectedKeys = selectedKeys,
                                            multiple = true,
                                            checkStrictly = props.checkStrictly,
                                        )
                                        toggle(pathNodes, !state.checked)
                                    }
                                }
                            },
                            onToggleCheck = { pathNodes, checked ->
                                if (!pathNodes.last().disabled) toggle(pathNodes, checked)
                            },
                            onHoverNode = { pathNodes ->
                                val node = pathNodes.last()
                                if (props.expandTrigger == ElCascaderExpandTrigger.Hover && !node.disabled && !node.isLeaf) {
                                    expandPath(pathNodes)
                                }
                            },
                            optionContent = optionContent,
                        )
                    }
                    if (footer != null) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp)) { footer() }
                    }
                }
            }
        }
    }
}

/**
 * Standalone `<el-cascader-panel>`: the columns of [ElCascader] without the
 * trigger field or popup. Useful inside dialogs, drawers or custom triggers.
 */
@Composable
fun ElCascaderPanel(
    value: List<String>,
    onValueChange: (List<String>) -> Unit,
    options: List<ElCascaderOption>,
    modifier: Modifier = Modifier,
    props: ElCascaderProps = ElCascaderProps(),
    emptyText: String? = null,
    emptyContent: (@Composable () -> Unit)? = null,
    optionContent: (@Composable (ElCascaderOption, List<ElCascaderOption>) -> Unit)? = null,
    lazyLoad: ElCascaderLazyLoad? = null,
    onExpandChange: ((List<String>) -> Unit)? = null,
) {
    val locale = LocalElConfig.current.locale
    var activePath by remember(value) { mutableStateOf(value) }
    val lazyState = rememberCascaderOptionsState(options, props.lazy, lazyLoad)
    val effectiveOptions = lazyState.roots
    LaunchedEffect(props.lazy, effectiveOptions.isEmpty()) {
        if (props.lazy && lazyLoad != null && effectiveOptions.isEmpty()) {
            lazyState.ensureChildren(emptyList(), 0)
        }
    }
    ElPopperSurface(modifier) {
        if (effectiveOptions.isEmpty()) {
            if (props.lazy && lazyState.loadingKeys.contains(cascaderPathKey(emptyList()))) {
                CascaderLoadingRow(locale.cascader.loading)
            } else {
                CascaderEmpty(emptyText ?: locale.cascader.noData, emptyContent)
            }
        } else {
            CascaderColumns(
                options = effectiveOptions,
                activePath = activePath,
                selectedPaths = if (value.isEmpty()) emptyList() else listOf(value),
                multiple = false,
                checkStrictly = props.checkStrictly,
                props = props,
                loadingKeys = lazyState.loadingKeys,
                loadingText = locale.cascader.loading,
                onNodeClick = { pathNodes ->
                    val node = pathNodes.last()
                    if (!node.disabled) {
                        if (props.checkStrictly || node.isLeaf) {
                            activePath = pathNodes.map { it.value }
                            onValueChange(activePath)
                        } else {
                            activePath = pathNodes.map { it.value }
                            onExpandChange?.invoke(activePath)
                            lazyState.ensureChildren(activePath, pathNodes.size)
                        }
                    }
                },
                onToggleCheck = { pathNodes, _ ->
                    if (!pathNodes.last().disabled) onValueChange(pathNodes.map { it.value })
                },
                onHoverNode = { pathNodes ->
                    val node = pathNodes.last()
                    if (props.expandTrigger == ElCascaderExpandTrigger.Hover && !node.disabled && !node.isLeaf) {
                        activePath = pathNodes.map { it.value }
                        onExpandChange?.invoke(activePath)
                        lazyState.ensureChildren(activePath, pathNodes.size)
                    }
                },
                optionContent = optionContent,
            )
        }
    }
}

@Composable
private fun CascaderEmpty(text: String, content: (@Composable () -> Unit)?) {
    Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)) {
        if (content != null) {
            content()
        } else {
            BasicText(
                text = text,
                style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textSecondary),
            )
        }
    }
}

@Composable
private fun rememberCascaderSuggestionWidth(
    filtered: List<CascaderMatch>,
    separator: String,
    size: ElSize,
): Dp {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val style = LocalElTheme.current.typography.base.copy(fontSize = size.fontSize)
    return remember(filtered, separator, style, textMeasurer) {
        val maxPx = filtered.maxOfOrNull { item ->
            textMeasurer.measure(
                text = item.path.joinToString(separator) { it.label },
                style = style,
                maxLines = 1,
            ).size.width
        } ?: 0
        with(density) { maxPx.toDp() } + 44.dp
    }
}

@Composable
private fun CascaderLoadingRow(text: String) {
    val theme = LocalElTheme.current
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ElIcon(ElIcons.Loading, size = 14.dp, tint = theme.colors.primary, spin = true)
        Spacer(Modifier.width(8.dp))
        BasicText(text = text, style = theme.typography.small.copy(color = theme.colors.textSecondary))
    }
}

@Composable
private fun CascaderSuggestionRow(
    text: String,
    selected: Boolean,
    disabled: Boolean,
    option: ElCascaderOption,
    path: List<ElCascaderOption>,
    suggestionContent: (@Composable (ElCascaderOption, List<ElCascaderOption>) -> Unit)?,
    onClick: () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .background(
                if (selected) colors.light(ElType.Primary, 9) else colors.backgroundOverlay
            )
            .clickable(enabled = !disabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (suggestionContent != null) {
            Box(Modifier.weight(1f)) { suggestionContent(option, path) }
        } else {
            BasicText(
                text = text,
                modifier = Modifier.weight(1f),
                style = theme.typography.base.copy(
                    color = when {
                        disabled -> colors.textDisabled
                        selected -> colors.primary
                        else -> colors.textRegular
                    },
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (selected) {
            ElIcon(ElIcons.Check, size = 12.dp, tint = colors.primary)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CascaderCollapsedTags(hiddenTags: List<ElCascaderTag>, showTooltip: Boolean) {
    val theme = LocalElTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Box(Modifier.hoverable(interaction)) {
        ElTag(text = "+${hiddenTags.size}", type = ElType.Info, size = ElSize.Small)
        if (showTooltip) {
            ElPopper(
                visible = hovered,
                onDismissRequest = {},
                placement = ElPlacement.Top,
                offset = DpOffset(0.dp, (-6).dp),
                dismissOnClickOutside = false,
            ) {
                ElPopperSurface(Modifier.widthIn(max = 260.dp)) {
                    Column(Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                        hiddenTags.forEach { tag ->
                            BasicText(
                                text = tag.text,
                                style = theme.typography.small.copy(color = theme.colors.textRegular),
                                modifier = Modifier.padding(vertical = 2.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CascaderColumns(
    options: List<ElCascaderOption>,
    activePath: List<String>,
    selectedPaths: List<List<String>>,
    multiple: Boolean,
    checkStrictly: Boolean,
    props: ElCascaderProps,
    loadingKeys: Set<String>,
    loadingText: String,
    onNodeClick: (List<ElCascaderOption>) -> Unit,
    onToggleCheck: (List<ElCascaderOption>, Boolean) -> Unit,
    onHoverNode: (List<ElCascaderOption>) -> Unit,
    optionContent: (@Composable (ElCascaderOption, List<ElCascaderOption>) -> Unit)?,
    columnWidth: Dp = 170.dp,
    maxHeight: Dp = 240.dp,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val selectedKeys = remember(selectedPaths) { selectedPaths.map { cascaderPathKey(it) }.toSet() }
    val levels = mutableListOf<List<ElCascaderOption>>()
    levels += options
    var current = options
    activePath.forEach { value ->
        val node = current.firstOrNull { it.value == value } ?: return@forEach
        if (node.children.isNotEmpty()) {
            levels += node.children
            current = node.children
        }
    }
    fun pathAt(level: Int, node: ElCascaderOption): List<ElCascaderOption> {
        val nodes = mutableListOf<ElCascaderOption>()
        var levelOptions = options
        for (index in 0 until level) {
            val active = levelOptions.firstOrNull { it.value == activePath.getOrNull(index) } ?: return emptyList()
            nodes += active
            levelOptions = active.children
        }
        nodes += node
        return nodes
    }
    Row(Modifier.heightIn(max = maxHeight)) {
        levels.forEachIndexed { level, nodes ->
            Column(
                Modifier
                    .width(columnWidth)
                    .heightIn(max = maxHeight)
                    .verticalScroll(rememberScrollState()),
            ) {
                nodes.forEach { node ->
                    val pathNodes = pathAt(level, node)
                    if (pathNodes.isEmpty()) return@forEach
                    val pathValues = pathNodes.map { it.value }
                    val isActive = activePath.getOrNull(level) == node.value
                    val checkState = cascaderCheckState(
                        node = node,
                        path = pathValues,
                        selectedKeys = selectedKeys,
                        multiple = multiple,
                        checkStrictly = checkStrictly,
                    )
                    val selected = checkState.checked
                    val isLoading = loadingKeys.contains(cascaderPathKey(pathValues))
                    val interaction = remember(node.value, level) { MutableInteractionSource() }
                    val hovered by interaction.collectIsHoveredAsState()
                    LaunchedEffect(hovered) {
                        if (hovered) onHoverNode(pathNodes)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                when {
                                    isActive || selected -> colors.fillLight
                                    hovered -> colors.fillLighter
                                    else -> colors.backgroundOverlay
                                }
                            )
                            .hoverable(interaction)
                            .clickable(enabled = !node.disabled) { onNodeClick(pathNodes) }
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if ((multiple || checkStrictly) && props.showPrefix) {
                            CascaderCheckPrefix(
                                multiple = multiple,
                                checked = checkState.checked,
                                indeterminate = checkState.indeterminate,
                                disabled = node.disabled,
                                onToggle = { onToggleCheck(pathNodes, !checkState.checked) },
                            )
                            Spacer(Modifier.width(6.dp))
                        }
                        if (optionContent != null) {
                            Box(Modifier.weight(1f)) { optionContent(node, pathNodes) }
                        } else {
                            BasicText(
                                text = node.label,
                                modifier = Modifier.weight(1f),
                                style = theme.typography.base.copy(
                                    color = when {
                                        node.disabled -> colors.textDisabled
                                        selected -> colors.primary
                                        else -> colors.textRegular
                                    },
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        when {
                            isLoading -> ElIcon(
                                ElIcons.Loading,
                                size = 12.dp,
                                tint = colors.primary,
                                contentDescription = loadingText,
                                spin = true,
                            )
                            node.children.isNotEmpty() -> ElIcon(ElIcons.ArrowRight, size = 10.dp, tint = colors.textPlaceholder)
                            selected && !multiple -> ElIcon(ElIcons.Check, size = 12.dp, tint = colors.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CascaderCheckPrefix(
    multiple: Boolean,
    checked: Boolean,
    indeterminate: Boolean,
    disabled: Boolean,
    onToggle: () -> Unit,
) {
    val colors = LocalElTheme.current.colors
    val active = checked || indeterminate
    val shape = if (multiple) RoundedCornerShape(2.dp) else CircleShape
    Box(
        Modifier
            .size(14.dp)
            .clip(shape)
            .background(if (active) colors.primary else colors.fillBlank, shape)
            .border(
                1.dp,
                when {
                    disabled -> colors.disabledBorder
                    active -> colors.primary
                    else -> colors.border
                },
                shape,
            )
            .clickable(enabled = !disabled, onClick = onToggle),
        contentAlignment = Alignment.Center,
    ) {
        when {
            indeterminate -> Box(Modifier.size(width = 8.dp, height = 1.5.dp).background(colors.white))
            checked && multiple -> ElIcon(ElIcons.Check, size = 10.dp, tint = colors.white)
            checked -> Box(Modifier.size(6.dp).clip(CircleShape).background(colors.white))
        }
    }
}

private class CascaderLazyState {
    var loaded by mutableStateOf<Map<String, List<ElCascaderOption>>>(emptyMap())
    var loadingKeys by mutableStateOf<Set<String>>(emptySet())
}

private class CascaderOptionsState(
    val roots: List<ElCascaderOption>,
    val loadingKeys: Set<String>,
    val ensureChildren: (path: List<String>, level: Int) -> Unit,
)

internal fun cascaderPathKey(path: List<String>): String = path.joinToString("\u0000")

private fun mergeLoadedCascaderChildren(
    nodes: List<ElCascaderOption>,
    loaded: Map<String, List<ElCascaderOption>>,
    parentPath: List<String> = emptyList(),
): List<ElCascaderOption> = nodes.map { node ->
    val path = parentPath + node.value
    val children = loaded[cascaderPathKey(path)] ?: node.children
    node.copy(children = mergeLoadedCascaderChildren(children, loaded, path))
}

@Composable
private fun rememberCascaderOptionsState(
    options: List<ElCascaderOption>,
    lazy: Boolean,
    lazyLoad: ElCascaderLazyLoad?,
): CascaderOptionsState {
    val state = remember(options) { CascaderLazyState() }
    val scope = rememberCoroutineScope()
    val roots = if (lazy && lazyLoad != null) {
        remember(options, state.loaded) {
            mergeLoadedCascaderChildren(state.loaded[cascaderPathKey(emptyList())] ?: options, state.loaded)
        }
    } else {
        options
    }
    val ensureChildren: (List<String>, Int) -> Unit = { path, level ->
        if (lazy && lazyLoad != null) {
            val key = cascaderPathKey(path)
            val node = if (path.isEmpty()) null else findCascaderNode(roots, path)
            val alreadyLoaded = state.loaded.containsKey(key)
            val hasChildren = node?.children?.isNotEmpty() == true
            if (!state.loadingKeys.contains(key) && !alreadyLoaded && !hasChildren) {
                state.loadingKeys = state.loadingKeys + key
                scope.launch {
                    try {
                        val children = lazyLoad.invoke(path, level)
                        state.loaded = state.loaded + (key to children)
                    } finally {
                        state.loadingKeys = state.loadingKeys - key
                    }
                }
            }
        }
    }
    return CascaderOptionsState(roots, state.loadingKeys, ensureChildren)
}

internal fun findCascaderNode(options: List<ElCascaderOption>, path: List<String>): ElCascaderOption? {
    var current = options
    var node: ElCascaderOption? = null
    path.forEach { value ->
        val found = current.firstOrNull { it.value == value } ?: return null
        node = found
        current = found.children
    }
    return node
}

internal fun cascaderLeafPaths(node: ElCascaderOption, parentPath: List<String>): List<List<String>> {
    val path = parentPath + node.value
    return if (node.children.isEmpty()) {
        if (node.disabled) emptyList() else listOf(path)
    } else {
        node.children.flatMap { cascaderLeafPaths(it, path) }
    }
}

private data class CascaderCheckState(val checked: Boolean, val indeterminate: Boolean)

private fun cascaderCheckState(
    node: ElCascaderOption,
    path: List<String>,
    selectedKeys: Set<String>,
    multiple: Boolean,
    checkStrictly: Boolean,
): CascaderCheckState {
    val selected = selectedKeys.contains(cascaderPathKey(path))
    if (!multiple || checkStrictly || node.children.isEmpty()) return CascaderCheckState(selected, false)
    val leaves = cascaderLeafPaths(node, path.dropLast(1)).map { cascaderPathKey(it) }
    val selectedLeaves = leaves.count { selectedKeys.contains(it) }
    return CascaderCheckState(
        checked = leaves.isNotEmpty() && selectedLeaves == leaves.size,
        indeterminate = selectedLeaves in 1 until leaves.size,
    )
}

internal fun displayCascaderPaths(
    options: List<ElCascaderOption>,
    selectedPaths: List<List<String>>,
    strategy: ElCascaderShowCheckedStrategy,
): List<List<String>> {
    if (strategy == ElCascaderShowCheckedStrategy.Child) return selectedPaths
    val selectedKeys = selectedPaths.map { cascaderPathKey(it) }.toSet()
    val result = mutableListOf<List<String>>()
    fun visit(nodes: List<ElCascaderOption>, parentPath: List<String>) {
        nodes.forEach { node ->
            val path = parentPath + node.value
            val leaves = cascaderLeafPaths(node, parentPath)
            val allLeavesSelected = leaves.isNotEmpty() && leaves.all { selectedKeys.contains(cascaderPathKey(it)) }
            when {
                allLeavesSelected -> result += path
                selectedKeys.contains(cascaderPathKey(path)) -> result += path
                else -> if (node.children.isNotEmpty()) visit(node.children, path)
            }
        }
    }
    visit(options, emptyList())
    return result
}

internal fun labelsForPath(options: List<ElCascaderOption>, values: List<String>): List<String> {
    if (values.isEmpty()) return emptyList()
    val result = mutableListOf<String>()
    var current = options
    values.forEach { value ->
        val node = current.firstOrNull { it.value == value } ?: return result
        result += node.label
        current = node.children
    }
    return result
}

internal fun pathToNode(options: List<ElCascaderOption>, target: String): List<ElCascaderOption>? {
    for (node in options) {
        if (node.value == target) return listOf(node)
        pathToNode(node.children, target)?.let { return listOf(node) + it }
    }
    return null
}

private data class CascaderMatch(val path: List<ElCascaderOption>, val option: ElCascaderOption)

private fun flattenCascaderMatches(
    options: List<ElCascaderOption>,
    query: String,
    filterMethod: ((ElCascaderOption, String) -> Boolean)? = null,
    showAllLevels: Boolean = true,
): List<CascaderMatch> {
    val result = mutableListOf<CascaderMatch>()
    fun walk(nodes: List<ElCascaderOption>, path: List<ElCascaderOption>) {
        nodes.forEach { node ->
            val next = path + node
            val matches = if (filterMethod != null) {
                filterMethod(node, query)
            } else {
                node.label.contains(query, ignoreCase = true) ||
                    (showAllLevels && path.any { it.label.contains(query, ignoreCase = true) })
            }
            if (matches) {
                result += CascaderMatch(next, node)
            }
            if (node.children.isNotEmpty()) walk(node.children, next)
        }
    }
    walk(options, emptyList())
    return result
}
