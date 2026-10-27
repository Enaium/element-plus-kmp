package cn.enaium.elementplus.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
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

data class ElCascaderOption(
    val value: String,
    val label: String,
    val children: List<ElCascaderOption> = emptyList(),
    val disabled: Boolean = false,
) {
    val isLeaf: Boolean get() = children.isEmpty()
}

/**
 * `<el-cascader>` single path selection with cascading columns, search and
 * configurable separator.
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
    var expandedKeys by remember { mutableStateOf(emptySet<String>()) }
    val density = LocalDensity.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val selectedLabels = labelsForPath(options, value)
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
    fun setExpanded(next: Boolean) {
        expanded = next
        if (!next) filterText = ""
        else activePath = value
        onVisibleChange?.invoke(next)
    }
    val effectivePlaceholder = placeholder ?: locale.cascader.placeholder
    val filterPlaceholder = locale.cascader.filterPlaceholder.ifBlank { locale.cascader.placeholder }
    val filtered = remember(options, filterText) {
        if (filterText.isBlank()) emptyList() else flattenCascaderMatches(options, filterText)
    }
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
                    ElIcons.CircleClose,
                    size = 14.dp,
                    tint = colors.textPlaceholder,
                    contentDescription = locale.clear,
                    modifier = Modifier.clickable { onValueChange(emptyList()) },
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
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 4.dp),
            focusable = true,
        ) {
            ElPopperSurface(Modifier.width(fieldWidth.coerceAtLeast(260.dp))) {
                Column(Modifier.heightIn(max = 274.dp)) {
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
                            Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)) {
                                BasicText(
                                    text = emptyText ?: locale.cascader.noMatch,
                                    style = theme.typography.small.copy(color = colors.textSecondary),
                                )
                            }
                        } else {
                            Column(Modifier.verticalScroll(rememberScrollState())) {
                                filtered.forEach { item ->
                                    val selected = value == item.path.map { it.value }
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .background(if (selected) colors.light(cn.enaium.elementplus.theme.ElType.Primary, 9) else colors.backgroundOverlay)
                                            .clickable(enabled = !item.option.disabled) {
                                                onValueChange(item.path.map { it.value })
                                                setExpanded(false)
                                            }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        BasicText(
                                            text = item.path.joinToString(separator) { it.label },
                                            style = theme.typography.base.copy(
                                                color = if (selected) colors.primary else colors.textRegular,
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                        }
                    } else if (options.isEmpty()) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)) {
                            BasicText(
                                text = emptyText ?: locale.cascader.noData,
                                style = theme.typography.small.copy(color = colors.textSecondary),
                            )
                        }
                    } else {
                        CascaderColumns(
                            options = options,
                            activePath = activePath,
                            selectedPath = value,
                            onNodeClick = { level, path, node ->
                                if (node.children.isEmpty()) {
                                    onValueChange(path.map { it.value })
                                    setExpanded(false)
                                } else {
                                    activePath = path.map { it.value }
                                    expandedKeys = expandedKeys + node.value
                                }
                            },
                        )
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
    selectedPath: List<String>,
    onNodeClick: (level: Int, path: List<ElCascaderOption>, node: ElCascaderOption) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
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
    Row(Modifier.heightIn(max = 240.dp)) {
        levels.forEachIndexed { level, nodes ->
            Column(
                Modifier
                    .width(160.dp)
                    .heightIn(max = 240.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                nodes.forEach { node ->
                    val pathNodes = pathAt(level, node)
                    val isActive = activePath.getOrNull(level) == node.value
                    val isSelected = selectedPath.getOrNull(level) == node.value
                    val interaction = remember(node.value, level) { MutableInteractionSource() }
                    val hovered by interaction.collectIsHoveredAsState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isActive || isSelected) colors.fillLight
                                else if (hovered) colors.fillLighter else colors.backgroundOverlay
                            )
                            .clickable(enabled = !node.disabled) { onNodeClick(level, pathNodes, node) }
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BasicText(
                            text = node.label,
                            modifier = Modifier.weight(1f),
                            style = theme.typography.base.copy(
                                color = when {
                                    node.disabled -> colors.textDisabled
                                    isSelected -> colors.primary
                                    else -> colors.textRegular
                                },
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (node.children.isNotEmpty()) {
                            ElIcon(ElIcons.ArrowRight, size = 10.dp, tint = colors.textPlaceholder)
                        } else if (isSelected) {
                            ElIcon(ElIcons.Check, size = 12.dp, tint = colors.primary)
                        }
                    }
                }
            }
        }
    }
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

private fun flattenCascaderMatches(options: List<ElCascaderOption>, query: String): List<CascaderMatch> {
    val result = mutableListOf<CascaderMatch>()
    fun walk(nodes: List<ElCascaderOption>, path: List<ElCascaderOption>) {
        nodes.forEach { node ->
            val next = path + node
            if (node.label.contains(query, ignoreCase = true)) {
                result += CascaderMatch(next, node)
            }
            if (node.children.isNotEmpty()) walk(node.children, next)
        }
    }
    walk(options, emptyList())
    return result
}
