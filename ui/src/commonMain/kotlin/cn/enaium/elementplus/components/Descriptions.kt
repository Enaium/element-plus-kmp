package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElTheme
import cn.enaium.elementplus.internal.resolveElSize

enum class ElDescriptionsDirection { Horizontal, Vertical }

class ElDescriptionItem(
    val label: String,
    val span: Int = 1,
    val content: @Composable () -> Unit,
)

/** `<el-descriptions>` with border, column spans and horizontal/vertical labels. */
@Composable
fun ElDescriptions(
    items: List<ElDescriptionItem>,
    modifier: Modifier = Modifier,
    column: Int = 3,
    border: Boolean = false,
    direction: ElDescriptionsDirection = ElDescriptionsDirection.Horizontal,
    title: String? = null,
    extra: (@Composable () -> Unit)? = null,
    labelWidth: Dp = 120.dp,
    size: ElSize? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val resolvedSize = resolveElSize(size)
    val rows = buildList {
        var current = mutableListOf<ElDescriptionItem>()
        var used = 0
        items.forEach { item ->
            val span = item.span.coerceIn(1, column)
            if (used + span > column) {
                add(current)
                current = mutableListOf()
                used = 0
            }
            current += item
            used += span
        }
        if (current.isNotEmpty()) add(current)
    }
    Column(modifier.fillMaxWidth()) {
        if (title != null || extra != null) {
            Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                if (title != null) {
                    BasicText(
                        text = title,
                        modifier = Modifier.weight(1f),
                        style = theme.typography.medium.copy(color = colors.textPrimary, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
                    )
                } else {
                    androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                }
                extra?.invoke()
            }
        }
        rows.forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { item ->
                    DescriptionCell(
                        item = item,
                        direction = direction,
                        border = border,
                        labelWidth = labelWidth,
                        size = resolvedSize,
                        modifier = Modifier.weight(item.span.coerceIn(1, column).toFloat()),
                    )
                }
                val usedSpan = row.sumOf { it.span.coerceIn(1, column) }
                if (usedSpan < column) {
                    Box(Modifier.weight((column - usedSpan).toFloat()))
                }
            }
        }
    }
}

@Composable
private fun RowScope.DescriptionCell(
    item: ElDescriptionItem,
    direction: ElDescriptionsDirection,
    border: Boolean,
    labelWidth: Dp,
    size: cn.enaium.elementplus.theme.ElSize,
    modifier: Modifier = Modifier,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val borderModifier = if (border) Modifier.border(1.dp, colors.borderLight) else Modifier
    if (direction == ElDescriptionsDirection.Horizontal) {
        Row(modifier.then(borderModifier)) {
            Box(
                Modifier
                    .width(labelWidth)
                    .background(if (border) colors.fillLight else colors.background)
                    .border(if (border) 0.dp else 0.dp, colors.border)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                BasicText(
                    text = item.label,
                    style = theme.typography.small.copy(color = colors.textSecondary, fontSize = size.fontSize),
                )
            }
            Box(Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 10.dp)) {
                item.content()
            }
        }
    } else {
        Column(modifier.then(borderModifier)) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(if (border) colors.fillLight else colors.background)
                    .padding(horizontal = 12.dp, vertical = 7.dp),
            ) {
                BasicText(text = item.label, style = theme.typography.small.copy(color = colors.textSecondary, fontSize = size.fontSize))
            }
            Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)) {
                item.content()
            }
        }
    }
}
