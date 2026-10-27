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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
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
import kotlinx.coroutines.launch

/**
 * Field alias adapter for `<el-tree-select :props="{ key, label, children, disabled, isLeaf }">`
 * and `node-key`. Compose has no runtime field-name lookup, so the aliases are
 * expressed as selectors and applied with [toTreeNodes]; the selector result of
 * `key` is the node key (`node-key`).
 */
class ElTreeSelectProps<T>(
    val key: (T) -> String,
    val label: (T) -> String,
    val children: (T) -> List<T> = { emptyList() },
    val disabled: (T) -> Boolean = { false },
    val leaf: (T) -> Boolean? = { null },
) {
    fun toNode(item: T): ElTreeNode = ElTreeNode(
        key = key(item),
        label = label(item),
        children = children(item).map { toNode(it) },
        disabled = disabled(item),
        leaf = leaf(item),
    )
}

/** Maps a raw model list to [ElTreeNode]s using [props] aliases. */
fun <T> List<T>.toTreeNodes(props: ElTreeSelectProps<T>): List<ElTreeNode> =
    map { props.toNode(it) }

private fun syncParentChecks(nodes: List<ElTreeNode>, checked: MutableSet<String>) {
    nodes.forEach { node ->
        if (node.children.isNotEmpty()) {
            syncParentChecks(node.children, checked)
            if (node.children.all { it.key in checked }) checked += node.key else checked -= node.key
        }
    }
}

private fun treeHasMatch(
    nodes: List<ElTreeNode>,
    query: String,
    method: ((String, ElTreeNode) -> Boolean)?,
): Boolean = nodes.any { node ->
    (method?.invoke(query, node) ?: node.label.contains(query, ignoreCase = true)) ||
        treeHasMatch(node.children, query, method)
}

/**
 * `<el-tree-select>` tree dropdown built on top of [ElTree].
 *
 * Single-select mode uses [selectedKey]/[onSelectedChange]; [multiple] mode uses
 * [selectedKeys]/[onSelectedKeysChange] and renders the selection as tags.
 * Supports `show-checkbox`, `check-strictly`, `check-on-click-node`, `accordion`,
 * lazy loading (`lazy` + [load]), custom node rendering ([nodeContent]), custom
 * filtering ([filterMethod]) and field aliases via [ElTreeSelectProps].
 */
@Composable
fun ElTreeSelect(
    selectedKey: String?,
    onSelectedChange: (String?) -> Unit,
    data: List<ElTreeNode>,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: ElSize? = null,
    disabled: Boolean = false,
    clearable: Boolean = true,
    filterable: Boolean = true,
    defaultExpandedKeys: Set<String> = emptySet(),
    defaultExpandAll: Boolean = false,
    emptyText: String? = null,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    multiple: Boolean = false,
    selectedKeys: List<String> = emptyList(),
    onSelectedKeysChange: (List<String>) -> Unit = {},
    showCheckbox: Boolean = false,
    checkStrictly: Boolean = false,
    checkOnClickNode: Boolean = false,
    defaultCheckedKeys: Set<String> = emptySet(),
    nodeContent: (@Composable (ElTreeNode) -> Unit)? = null,
    filterMethod: ((query: String, node: ElTreeNode) -> Boolean)? = null,
    maxCollapseTags: Int? = null,
    accordion: Boolean = false,
    lazy: Boolean = false,
    load: (suspend (ElTreeNode) -> List<ElTreeNode>)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedSize = resolveElSize(size)
    val shape = RoundedCornerShape(theme.radius.base)
    var expanded by remember { mutableStateOf(false) }
    var filterText by remember { mutableStateOf("") }
    var fieldWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val scope = rememberCoroutineScope()
    val loader = load
    var lazyLoadedChildren by remember(data) { mutableStateOf<Map<String, List<ElTreeNode>>>(emptyMap()) }

    val initialExpanded = remember(data) {
        if (defaultExpandAll) flattenTreeAll(data).map { it.node.key }.toSet() else defaultExpandedKeys
    }
    var expandedKeys by remember(data) { mutableStateOf(initialExpanded) }

    var internalChecked by remember(data) {
        mutableStateOf(if (selectedKeys.isEmpty()) defaultCheckedKeys else selectedKeys.toSet())
    }
    var checkedInitialised by remember(data) { mutableStateOf(false) }
    LaunchedEffect(selectedKeys) {
        val next = selectedKeys.toSet()
        if (!checkedInitialised) {
            checkedInitialised = true
            if (selectedKeys.isNotEmpty()) internalChecked = next
        } else if (next != internalChecked) {
            internalChecked = next
        }
    }

    fun findLazyNode(key: String): ElTreeNode? {
        findTreeNode(data, key)?.let { return it }
        lazyLoadedChildren.values.forEach { nodes -> findTreeNode(nodes, key)?.let { return it } }
        return null
    }

    val checkedKeys: Set<String> = if (multiple) internalChecked else setOfNotNull(selectedKey)
    val searchRoots = remember(data, lazyLoadedChildren) { data + lazyLoadedChildren.values.flatten() }
    val selectedNodes = checkedKeys.mapNotNull { findLazyNode(it) }
    val selectedLabel = if (multiple) null else selectedKey?.let { findLazyNode(it)?.label }
    val selectedExists = if (multiple) selectedNodes.isNotEmpty() else selectedLabel != null
    val effectivePlaceholder = placeholder ?: locale.select.placeholder
    val height = resolvedSize.height
    val borderColor = when {
        disabled -> colors.disabledBorder
        expanded -> colors.primary
        hovered -> colors.textDisabled
        else -> colors.border
    }

    fun setExpanded(value: Boolean) {
        expanded = value
        if (!value) filterText = ""
        onVisibleChange?.invoke(value)
    }

    fun commitSelection(next: Set<String>) {
        internalChecked = next
        onSelectedKeysChange(next.toList())
    }

    fun toggleRowCheck(node: ElTreeNode) {
        val next = internalChecked.toMutableSet()
        if (checkStrictly) {
            if (node.key in next) next -= node.key else next += node.key
        } else {
            val affected = descendantKeys(node)
            if (node.key in next) next -= affected else next += affected
            syncParentChecks(data, next)
        }
        commitSelection(next)
    }

    val customNodeContent: (@Composable (ElTreeNode) -> Unit)? = nodeContent
    val treeNodeContent: (@Composable (ElTreeNode, Boolean) -> Unit)? = if (customNodeContent != null) {
        { node, _ -> customNodeContent(node) }
    } else {
        null
    }

    val suspendLoader = loader
    val treeLoad: ((ElTreeNode?, (List<ElTreeNode>) -> Unit, () -> Unit) -> Unit)? = if (suspendLoader == null) {
        null
    } else {
        { node, resolve, reject ->
            if (node == null) {
                resolve(emptyList())
            } else {
                scope.launch {
                    runCatching { suspendLoader.invoke(node) }
                        .onSuccess { children ->
                            lazyLoadedChildren = lazyLoadedChildren + (node.key to children)
                            resolve(children)
                        }
                        .onFailure { reject() }
                }
            }
        }
    }

    val arrow by animateFloatAsState(if (expanded) 180f else 0f, label = "tree-select-arrow")
    Box(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .onSizeChanged { fieldWidth = with(density) { it.width.toDp() } }
                .height(height)
                .clip(shape)
                .background(if (disabled) colors.disabledBackground else colors.fillBlank, shape)
                .border(1.dp, borderColor, shape)
                .hoverable(interaction)
                .clickable(enabled = !disabled) { setExpanded(!expanded) }
                .padding(horizontal = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (multiple) {
                if (selectedNodes.isEmpty()) {
                    BasicText(
                        text = effectivePlaceholder,
                        modifier = Modifier.weight(1f),
                        style = theme.typography.base.copy(
                            fontSize = resolvedSize.fontSize,
                            color = colors.textPlaceholder,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                } else {
                    val shown = maxCollapseTags?.let { selectedNodes.take(it) } ?: selectedNodes
                    Row(
                        Modifier.weight(1f).clipToBounds(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        shown.forEach { node ->
                            ElTag(
                                text = node.label,
                                size = ElSize.Small,
                                closable = !disabled,
                                onClose = { commitSelection(internalChecked - node.key) },
                            )
                        }
                        val hidden = selectedNodes.size - shown.size
                        if (hidden > 0) ElTag(text = "+$hidden", size = ElSize.Small)
                    }
                }
            } else {
                BasicText(
                    text = selectedLabel ?: effectivePlaceholder,
                    modifier = Modifier.weight(1f),
                    style = theme.typography.base.copy(
                        fontSize = resolvedSize.fontSize,
                        color = if (selectedLabel != null) colors.textRegular else colors.textPlaceholder,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (clearable && selectedExists && hovered && !disabled) {
                ElIcon(
                    ElIcons.CircleClose,
                    size = 14.dp,
                    tint = colors.textPlaceholder,
                    modifier = Modifier.clickable {
                        if (multiple) commitSelection(emptySet()) else onSelectedChange(null)
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
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 4.dp),
            focusable = true,
        ) {
            ElPopperSurface(Modifier.width(fieldWidth.coerceAtLeast(200.dp))) {
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
                                                text = locale.select.filterPlaceholder,
                                                style = theme.typography.base.copy(color = colors.textPlaceholder),
                                            )
                                        }
                                        inner()
                                    }
                                },
                            )
                        }
                    }
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        if (filterText.isNotBlank() && !treeHasMatch(searchRoots, filterText, filterMethod)) {
                            Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)) {
                                BasicText(
                                    text = emptyText ?: locale.noMatchText,
                                    style = theme.typography.small.copy(color = colors.textSecondary),
                                )
                            }
                        } else {
                            ElTree(
                                data = data,
                                selectedKey = if (multiple) null else selectedKey,
                                onSelectedChange = { key ->
                                    if (multiple) {
                                        if (!showCheckbox || checkOnClickNode) {
                                            findLazyNode(key)?.let { toggleRowCheck(it) }
                                        }
                                    } else {
                                        onSelectedChange(key)
                                        setExpanded(false)
                                    }
                                },
                                checkedKeys = checkedKeys,
                                onCheckedChange = { next ->
                                    if (multiple) {
                                        commitSelection(next)
                                    } else {
                                        val added = next - checkedKeys
                                        onSelectedChange(added.firstOrNull())
                                    }
                                },
                                showCheckbox = multiple || showCheckbox,
                                checkStrictly = if (multiple) checkStrictly else true,
                                expandedKeys = expandedKeys,
                                onExpandedChange = { expandedKeys = it },
                                accordion = accordion,
                                expandOnClickNode = false,
                                lazy = lazy,
                                load = treeLoad,
                                filterText = filterText,
                                filterNodeMethod = filterMethod,
                                emptyText = emptyText,
                                nodeContent = treeNodeContent,
                            )
                        }
                    }
                }
            }
        }
    }
}
