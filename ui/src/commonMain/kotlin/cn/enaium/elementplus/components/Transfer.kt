package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

data class ElTransferItem(
    val key: String,
    val label: String,
    val disabled: Boolean = false,
)

enum class ElTransferTargetOrder { Original, Push, Unshift }

/** Move direction reported by [ElTransfer.onChange]. */
enum class ElTransferDirection { Left, Right }

/**
 * Field alias adapter for `<el-transfer :props="{ key, label, disabled }">`.
 * Compose has no runtime field-name lookup, so the aliases are expressed as
 * selectors and applied with [toTransferItems].
 */
class ElTransferProps<T>(
    val key: (T) -> String,
    val label: (T) -> String,
    val disabled: (T) -> Boolean = { false },
) {
    fun toItem(item: T): ElTransferItem = ElTransferItem(key(item), label(item), disabled(item))
}

/** Maps a raw model list to [ElTransferItem]s using [props] aliases. */
fun <T> List<T>.toTransferItems(props: ElTransferProps<T>): List<ElTransferItem> =
    map { props.toItem(it) }

/**
 * Hoisted transfer panel state, used to implement `clearQuery` and to read the
 * panel queries from outside the component.
 */
class ElTransferState {
    var leftQuery by mutableStateOf("")
        internal set
    var rightQuery by mutableStateOf("")
        internal set

    /** Clears both panel filter inputs, equivalent to `el-transfer.clearQuery()`. */
    fun clearQuery() {
        leftQuery = ""
        rightQuery = ""
    }
}

/**
 * `<el-transfer>`: dual list with filtering, checkboxes and bulk actions.
 *
 * Parity additions: custom item rendering ([itemContent]), custom empty/footer
 * slots, [`noCheckedFormat`]/[`hasCheckedFormat`] counters, a `filterMethod`,
 * [`virtualScroll`] with [`itemSize`], direction-aware [`onChange`] and panel
 * state for `clearQuery`.
 */
@Composable
fun ElTransfer(
    data: List<ElTransferItem>,
    value: List<String>,
    onValueChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    titles: List<String>? = null,
    filterable: Boolean = false,
    filterPlaceholder: String? = null,
    buttonTexts: List<String> = listOf("", ""),
    leftDefaultChecked: List<String> = emptyList(),
    rightDefaultChecked: List<String> = emptyList(),
    targetOrder: ElTransferTargetOrder = ElTransferTargetOrder.Original,
    panelWidth: Dp = 200.dp,
    panelHeight: Dp = 260.dp,
    leftEmptyContent: (@Composable () -> Unit)? = null,
    rightEmptyContent: (@Composable () -> Unit)? = null,
    leftFooter: (@Composable () -> Unit)? = null,
    rightFooter: (@Composable () -> Unit)? = null,
    itemContent: (@Composable (ElTransferItem) -> Unit)? = null,
    filterMethod: ((query: String, item: ElTransferItem) -> Boolean)? = null,
    noCheckedFormat: String? = null,
    hasCheckedFormat: String? = null,
    virtualScroll: Boolean = false,
    itemSize: Dp = 30.dp,
    state: ElTransferState? = null,
    onChange: ((value: List<String>, direction: ElTransferDirection, movedKeys: List<String>) -> Unit)? = null,
    onLeftCheckChange: ((checkedKeys: List<String>) -> Unit)? = null,
    onRightCheckChange: ((checkedKeys: List<String>) -> Unit)? = null,
) {
    val locale = LocalElConfig.current.locale
    val resolvedTitles = titles ?: locale.transfer.titles
    val leftItems = remember(data, value) { data.filter { it.key !in value } }
    val rightItems = remember(data, value) { value.mapNotNull { key -> data.firstOrNull { it.key == key } } }
    val fallbackState = remember { ElTransferState() }
    val transferState = state ?: fallbackState
    var leftChecked by remember { mutableStateOf(leftDefaultChecked.filter { it !in value }.toSet()) }
    var rightChecked by remember { mutableStateOf(rightDefaultChecked.filter { it in value }.toSet()) }
    val matches = { query: String, item: ElTransferItem ->
        filterMethod?.invoke(query, item) ?: item.label.contains(query, ignoreCase = true)
    }
    val leftVisible = leftItems.filter { matches(transferState.leftQuery, it) }
    val rightVisible = rightItems.filter { matches(transferState.rightQuery, it) }
    val movableLeft = leftVisible.filter { it.key in leftChecked && !it.disabled }
    val movableRight = rightVisible.filter { it.key in rightChecked && !it.disabled }
    val resolvedNoCheckedFormat = noCheckedFormat ?: locale.transfer.noCheckedFormat
    val resolvedHasCheckedFormat = hasCheckedFormat ?: locale.transfer.hasCheckedFormat

    fun updateLeftChecked(next: Set<String>) {
        leftChecked = next
        onLeftCheckChange?.invoke(next.toList())
    }

    fun updateRightChecked(next: Set<String>) {
        rightChecked = next
        onRightCheckChange?.invoke(next.toList())
    }

    fun moveToRight(keys: Set<String>) {
        val additions = data.filter { it.key in keys && it.key !in value }.map { it.key }
        if (additions.isEmpty()) return
        val next = when (targetOrder) {
            ElTransferTargetOrder.Original -> data.map { it.key }.filter { it in value || it in additions }
            ElTransferTargetOrder.Push -> value + additions
            ElTransferTargetOrder.Unshift -> additions.reversed() + value
        }
        onValueChange(next)
        updateLeftChecked(leftChecked - additions.toSet())
        onChange?.invoke(next, ElTransferDirection.Right, additions)
    }

    fun moveToLeft(keys: Set<String>) {
        val removals = keys.filter { it in value }.toSet()
        if (removals.isEmpty()) return
        onValueChange(value.filter { it !in removals })
        updateRightChecked(rightChecked - removals)
        onChange?.invoke(value.filter { it !in removals }, ElTransferDirection.Left, removals.toList())
    }

    Row(modifier, horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        TransferPanel(
            title = resolvedTitles.getOrElse(0) { locale.transfer.titles[0] },
            entries = leftVisible,
            checkedKeys = leftChecked,
            filterable = filterable,
            filter = transferState.leftQuery,
            filterPlaceholder = filterPlaceholder ?: locale.transfer.filterPlaceholder,
            emptyText = if (transferState.leftQuery.isBlank()) locale.transfer.noData else locale.transfer.noMatch,
            emptyContent = leftEmptyContent,
            footer = leftFooter,
            itemContent = itemContent,
            noCheckedFormat = resolvedNoCheckedFormat,
            hasCheckedFormat = resolvedHasCheckedFormat,
            virtualScroll = virtualScroll,
            itemSize = itemSize,
            width = panelWidth,
            height = panelHeight,
            onFilterChange = { transferState.leftQuery = it },
            onCheckedChange = { key, checked ->
                updateLeftChecked(if (checked) leftChecked + key else leftChecked - key)
            },
            onCheckAll = { checked ->
                updateLeftChecked(
                    if (checked) leftVisible.filter { !it.disabled }.map { it.key }.toSet()
                    else emptySet(),
                )
            },
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TransferButton(
                icon = ElIcons.ArrowRight,
                label = buttonTexts.getOrNull(0),
                enabled = movableLeft.isNotEmpty(),
            ) { moveToRight(movableLeft.map { it.key }.toSet()) }
            TransferButton(
                icon = ElIcons.ArrowLeft,
                label = buttonTexts.getOrNull(1),
                enabled = movableRight.isNotEmpty(),
            ) { moveToLeft(movableRight.map { it.key }.toSet()) }
            TransferButton(
                icon = ElIcons.DArrowRight,
                label = null,
                enabled = leftVisible.any { !it.disabled },
            ) { moveToRight(leftVisible.filter { !it.disabled }.map { it.key }.toSet()) }
            TransferButton(
                icon = ElIcons.DArrowLeft,
                label = null,
                enabled = rightVisible.any { !it.disabled },
            ) { moveToLeft(rightVisible.filter { !it.disabled }.map { it.key }.toSet()) }
        }
        TransferPanel(
            title = resolvedTitles.getOrElse(1) { locale.transfer.titles[1] },
            entries = rightVisible,
            checkedKeys = rightChecked,
            filterable = filterable,
            filter = transferState.rightQuery,
            filterPlaceholder = filterPlaceholder ?: locale.transfer.filterPlaceholder,
            emptyText = if (transferState.rightQuery.isBlank()) locale.transfer.noData else locale.transfer.noMatch,
            emptyContent = rightEmptyContent,
            footer = rightFooter,
            itemContent = itemContent,
            noCheckedFormat = resolvedNoCheckedFormat,
            hasCheckedFormat = resolvedHasCheckedFormat,
            virtualScroll = virtualScroll,
            itemSize = itemSize,
            width = panelWidth,
            height = panelHeight,
            onFilterChange = { transferState.rightQuery = it },
            onCheckedChange = { key, checked ->
                updateRightChecked(if (checked) rightChecked + key else rightChecked - key)
            },
            onCheckAll = { checked ->
                updateRightChecked(
                    if (checked) rightVisible.filter { !it.disabled }.map { it.key }.toSet()
                    else emptySet(),
                )
            },
        )
    }
}

@Composable
private fun TransferPanel(
    title: String,
    entries: List<ElTransferItem>,
    checkedKeys: Set<String>,
    filterable: Boolean,
    filter: String,
    filterPlaceholder: String,
    emptyText: String,
    emptyContent: (@Composable () -> Unit)?,
    footer: (@Composable () -> Unit)?,
    itemContent: (@Composable (ElTransferItem) -> Unit)?,
    noCheckedFormat: String,
    hasCheckedFormat: String,
    virtualScroll: Boolean,
    itemSize: Dp,
    width: Dp,
    height: Dp,
    onFilterChange: (String) -> Unit,
    onCheckedChange: (String, Boolean) -> Unit,
    onCheckAll: (Boolean) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val allChecked = entries.isNotEmpty() && entries.filter { !it.disabled }.all { it.key in checkedKeys }
    val partiallyChecked = checkedKeys.isNotEmpty() && !allChecked
    val totalText = entries.size.toString()
    val checkedText = checkedKeys.size.toString()
    val countText = if (checkedKeys.isEmpty()) {
        noCheckedFormat.replace("\${total}", totalText).replace("{total}", totalText)
    } else {
        hasCheckedFormat
            .replace("\${checked}", checkedText)
            .replace("\${total}", totalText)
            .replace("{checked}", checkedText)
            .replace("{total}", totalText)
    }
    Column(
        Modifier
            .width(width)
            .height(height)
            .border(1.dp, colors.border, RoundedCornerShape(4.dp))
            .background(colors.fillBlank, RoundedCornerShape(4.dp)),
    ) {
        Row(
            Modifier.fillMaxWidth().height(38.dp).background(colors.fillLight).padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(14.dp)
                    .border(1.dp, if (allChecked) colors.primary else colors.border, RoundedCornerShape(2.dp))
                    .background(if (allChecked) colors.primary else colors.fillBlank, RoundedCornerShape(2.dp))
                    .clickable { onCheckAll(!allChecked) },
                contentAlignment = Alignment.Center,
            ) {
                if (allChecked) {
                    ElIcon(ElIcons.Check, size = 10.dp, tint = colors.white)
                } else if (partiallyChecked) {
                    ElIcon(ElIcons.Minus, size = 10.dp, tint = colors.primary)
                }
            }
            Spacer(Modifier.width(6.dp))
            BasicText(text = title, style = theme.typography.small.copy(color = colors.textRegular))
            Spacer(Modifier.weight(1f))
            BasicText(
                text = countText,
                style = theme.typography.extraSmall.copy(color = colors.textSecondary),
            )
        }
        if (filterable) {
            Box(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp)) {
                BasicTextField(
                    value = filter,
                    onValueChange = onFilterChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = theme.typography.small.copy(color = colors.textRegular),
                    cursorBrush = SolidColor(colors.primary),
                    decorationBox = { inner ->
                        Box {
                            if (filter.isEmpty()) {
                                BasicText(
                                    text = filterPlaceholder,
                                    style = theme.typography.small.copy(color = colors.textPlaceholder),
                                )
                            }
                            inner()
                        }
                    },
                )
            }
        }
        Box(Modifier.weight(1f).fillMaxWidth()) {
            if (entries.isEmpty()) {
                if (emptyContent != null) {
                    Box(Modifier.fillMaxWidth().padding(top = 10.dp), contentAlignment = Alignment.TopCenter) {
                        emptyContent()
                    }
                } else {
                    Box(Modifier.fillMaxWidth().padding(top = 30.dp), contentAlignment = Alignment.TopCenter) {
                        BasicText(text = emptyText, style = theme.typography.small.copy(color = colors.textSecondary))
                    }
                }
            } else if (virtualScroll) {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(items = entries, key = { it.key }) { item ->
                        TransferRow(
                            item = item,
                            checked = item.key in checkedKeys,
                            itemContent = itemContent,
                            itemSize = itemSize,
                            fixedHeight = true,
                            onToggle = { onCheckedChange(item.key, item.key !in checkedKeys) },
                        )
                    }
                }
            } else {
                Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    entries.forEach { item ->
                        TransferRow(
                            item = item,
                            checked = item.key in checkedKeys,
                            itemContent = itemContent,
                            itemSize = itemSize,
                            fixedHeight = false,
                            onToggle = { onCheckedChange(item.key, item.key !in checkedKeys) },
                        )
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
        Row(
            Modifier.fillMaxWidth().heightIn(min = 30.dp).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            footer?.invoke()
        }
    }
}

@Composable
private fun TransferRow(
    item: ElTransferItem,
    checked: Boolean,
    itemContent: (@Composable (ElTransferItem) -> Unit)?,
    itemSize: Dp,
    fixedHeight: Boolean,
    onToggle: () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val interaction = remember(item.key) { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Row(
        Modifier
            .fillMaxWidth()
            .then(if (fixedHeight) Modifier.height(itemSize) else Modifier.heightIn(min = itemSize))
            .background(if (hovered) colors.fillLight else colors.fillBlank)
            .clickable(enabled = !item.disabled, onClick = onToggle)
            .padding(horizontal = 10.dp, vertical = if (fixedHeight) 0.dp else 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(14.dp)
                .border(
                    1.dp,
                    if (item.disabled) colors.disabledBorder else if (checked) colors.primary else colors.border,
                    RoundedCornerShape(2.dp),
                )
                .background(if (checked) colors.primary else colors.fillBlank, RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) ElIcon(ElIcons.Check, size = 10.dp, tint = colors.white)
        }
        Spacer(Modifier.width(6.dp))
        if (itemContent != null) {
            Box(Modifier.weight(1f)) { itemContent(item) }
        } else {
            BasicText(
                text = item.label,
                modifier = Modifier.weight(1f),
                style = theme.typography.small.copy(
                    color = if (item.disabled) colors.textDisabled else colors.textRegular,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun TransferButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String?,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalElTheme.current.colors
    Row(
        Modifier
            .height(28.dp)
            .border(1.dp, if (enabled) colors.border else colors.borderLight, RoundedCornerShape(4.dp))
            .background(if (enabled) colors.fillBlank else colors.fillLight, RoundedCornerShape(4.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (label.isNullOrEmpty()) {
            ElIcon(icon, size = 14.dp, tint = if (enabled) colors.textRegular else colors.textDisabled)
        } else {
            BasicText(
                text = label,
                style = LocalElTheme.current.typography.small.copy(color = if (enabled) colors.textRegular else colors.textDisabled),
            )
        }
    }
}
