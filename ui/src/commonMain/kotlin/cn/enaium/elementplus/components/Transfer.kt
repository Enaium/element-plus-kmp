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

/**
 * `<el-transfer>`: dual list with filtering, checkboxes and bulk actions.
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
    panelWidth: androidx.compose.ui.unit.Dp = 200.dp,
    panelHeight: androidx.compose.ui.unit.Dp = 260.dp,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedTitles = titles ?: locale.transfer.titles
    val leftItems = remember(data, value) { data.filter { it.key !in value } }
    val rightItems = remember(data, value) { value.mapNotNull { key -> data.firstOrNull { it.key == key } } }
    var leftFilter by remember { mutableStateOf("") }
    var rightFilter by remember { mutableStateOf("") }
    var leftChecked by remember { mutableStateOf(leftDefaultChecked.filter { it !in value }.toSet()) }
    var rightChecked by remember { mutableStateOf(rightDefaultChecked.filter { it in value }.toSet()) }
    val leftVisible = leftItems.filter { it.label.contains(leftFilter, ignoreCase = true) }
    val rightVisible = rightItems.filter { it.label.contains(rightFilter, ignoreCase = true) }
    val movableLeft = leftVisible.filter { it.key in leftChecked && !it.disabled }
    val movableRight = rightVisible.filter { it.key in rightChecked && !it.disabled }

    fun moveToRight(keys: Set<String>) {
        val additions = data.filter { it.key in keys && it.key !in value }.map { it.key }
        if (additions.isEmpty()) return
        val next = when (targetOrder) {
            ElTransferTargetOrder.Original -> data.map { it.key }.filter { it in value || it in additions }
            ElTransferTargetOrder.Push -> value + additions
            ElTransferTargetOrder.Unshift -> additions.reversed() + value
        }
        onValueChange(next)
        leftChecked = leftChecked - additions
    }

    fun moveToLeft(keys: Set<String>) {
        val removals = keys.filter { it in value }.toSet()
        if (removals.isEmpty()) return
        onValueChange(value.filter { it !in removals })
        rightChecked = rightChecked - removals
    }

    Row(modifier, horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        TransferPanel(
            title = resolvedTitles.getOrElse(0) { locale.transfer.titles[0] },
            items = leftVisible,
            checkedKeys = leftChecked,
            filterable = filterable,
            filter = leftFilter,
            filterPlaceholder = filterPlaceholder ?: locale.transfer.filterPlaceholder,
            emptyText = if (leftFilter.isBlank()) locale.transfer.noData else locale.transfer.noMatch,
            width = panelWidth,
            height = panelHeight,
            onFilterChange = { leftFilter = it },
            onCheckedChange = { key, checked ->
                leftChecked = if (checked) leftChecked + key else leftChecked - key
            },
            onCheckAll = { checked ->
                leftChecked = if (checked) leftVisible.filter { !it.disabled }.map { it.key }.toSet()
                else emptySet()
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
            items = rightVisible,
            checkedKeys = rightChecked,
            filterable = filterable,
            filter = rightFilter,
            filterPlaceholder = filterPlaceholder ?: locale.transfer.filterPlaceholder,
            emptyText = if (rightFilter.isBlank()) locale.transfer.noData else locale.transfer.noMatch,
            width = panelWidth,
            height = panelHeight,
            onFilterChange = { rightFilter = it },
            onCheckedChange = { key, checked ->
                rightChecked = if (checked) rightChecked + key else rightChecked - key
            },
            onCheckAll = { checked ->
                rightChecked = if (checked) rightVisible.filter { !it.disabled }.map { it.key }.toSet()
                else emptySet()
            },
        )
    }
}

@Composable
private fun TransferPanel(
    title: String,
    items: List<ElTransferItem>,
    checkedKeys: Set<String>,
    filterable: Boolean,
    filter: String,
    filterPlaceholder: String,
    emptyText: String,
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    onFilterChange: (String) -> Unit,
    onCheckedChange: (String, Boolean) -> Unit,
    onCheckAll: (Boolean) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val allChecked = items.isNotEmpty() && items.filter { !it.disabled }.all { it.key in checkedKeys }
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
                if (allChecked) ElIcon(ElIcons.Check, size = 10.dp, tint = colors.white)
            }
            Spacer(Modifier.width(6.dp))
            BasicText(text = title, style = theme.typography.small.copy(color = colors.textRegular))
            Spacer(Modifier.weight(1f))
            BasicText(
                text = if (checkedKeys.isEmpty()) {
                    locale.transfer.noCheckedFormat.replace("{total}", items.size.toString())
                } else {
                    locale.transfer.hasCheckedFormat
                        .replace("{checked}", checkedKeys.size.toString())
                        .replace("{total}", items.size.toString())
                },
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
            if (items.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(top = 30.dp), contentAlignment = Alignment.TopCenter) {
                    BasicText(text = emptyText, style = theme.typography.small.copy(color = colors.textSecondary))
                }
            } else {
                Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    items.forEach { item ->
                        val checked = item.key in checkedKeys
                        val interaction = remember(item.key) { MutableInteractionSource() }
                        val hovered by interaction.collectIsHoveredAsState()
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(if (hovered) colors.fillLight else colors.fillBlank)
                                .clickable(enabled = !item.disabled) { onCheckedChange(item.key, !checked) }
                                .padding(horizontal = 10.dp, vertical = 7.dp),
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
                            BasicText(
                                text = item.label,
                                style = theme.typography.small.copy(
                                    color = if (item.disabled) colors.textDisabled else colors.textRegular,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
        Row(Modifier.fillMaxWidth().height(30.dp), verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(1f))
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
