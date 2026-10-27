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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

/**
 * `<el-tree-select>` single-select tree dropdown.
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
    val selectedLabel = selectedKey?.let { findTreeNode(data, it)?.label }
    val selectedExists = selectedLabel != null
    val effectivePlaceholder = placeholder ?: locale.select.placeholder
    val initialExpanded = remember(data) {
        if (defaultExpandAll) cn.enaium.elementplus.components.flattenTreeAll(data).map { it.node.key }.toSet()
        else defaultExpandedKeys
    }
    var expandedKeys by remember(data) { mutableStateOf(initialExpanded) }
    val visible = if (filterText.isBlank()) flattenTree(data, expandedKeys) else filteredTree(data, filterText)
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
            if (clearable && selectedExists && hovered && !disabled) {
                cn.enaium.elementplus.icons.ElIcon(
                    cn.enaium.elementplus.icons.ElIcons.CircleClose,
                    size = 14.dp,
                    tint = colors.textPlaceholder,
                    modifier = Modifier.clickable { onSelectedChange(null) },
                )
            } else {
                cn.enaium.elementplus.icons.ElIcon(
                    cn.enaium.elementplus.icons.ElIcons.ArrowDown,
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
                    if (visible.isEmpty()) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)) {
                            BasicText(
                                text = emptyText ?: if (filterText.isBlank()) locale.tree.emptyText else locale.noMatchText,
                                style = theme.typography.small.copy(color = colors.textSecondary),
                            )
                        }
                    } else {
                        Column(Modifier.verticalScroll(rememberScrollState())) {
                            visible.forEach { flat ->
                                val isSelected = selectedKey == flat.node.key
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .background(if (isSelected) colors.light(cn.enaium.elementplus.theme.ElType.Primary, 9) else colors.backgroundOverlay)
                                        .clickable(enabled = !flat.node.disabled) {
                                            onSelectedChange(flat.node.key)
                                            setExpanded(false)
                                        }
                                        .height(30.dp)
                                        .padding(start = 10.dp + 16.dp * flat.depth, end = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        Modifier
                                            .size(20.dp)
                                            .clickable(enabled = !flat.node.isLeaf) {
                                                expandedKeys = if (flat.node.key in expandedKeys) expandedKeys - flat.node.key
                                                else expandedKeys + flat.node.key
                                            },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (!flat.node.isLeaf) {
                                            cn.enaium.elementplus.icons.ElIcon(
                                                cn.enaium.elementplus.icons.ElIcons.ArrowRight,
                                                size = 10.dp,
                                                tint = colors.textPlaceholder,
                                                modifier = Modifier.rotate(if (flat.node.key in expandedKeys) 90f else 0f),
                                            )
                                        }
                                    }
                                    BasicText(
                                        text = flat.node.label,
                                        style = theme.typography.base.copy(
                                            color = when {
                                                flat.node.disabled -> colors.textDisabled
                                                isSelected -> colors.primary
                                                else -> colors.textRegular
                                            },
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
