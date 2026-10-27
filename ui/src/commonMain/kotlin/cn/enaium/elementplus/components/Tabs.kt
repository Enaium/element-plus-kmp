package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElTabsType { Line, Card, BorderCard }
enum class ElTabPosition { Top, Bottom, Left, Right }

class ElTabData(
    val name: String,
    val label: String,
    val icon: ImageVector? = null,
    val disabled: Boolean = false,
    val closable: Boolean = true,
    val content: @Composable () -> Unit,
)

/** `<el-tabs>` with line/card styles, closable/addable tabs and four positions. */
@Composable
fun ElTabs(
    tabs: List<ElTabData>,
    activeName: String,
    onTabChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    type: ElTabsType = ElTabsType.Line,
    tabPosition: ElTabPosition = ElTabPosition.Top,
    closable: Boolean = false,
    addable: Boolean = false,
    onTabRemove: ((String) -> Unit)? = null,
    onTabAdd: (() -> Unit)? = null,
    stretch: Boolean = false,
    contentPadding: Dp = 16.dp,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val activeTab = tabs.firstOrNull { it.name == activeName } ?: tabs.firstOrNull { !it.disabled } ?: tabs.firstOrNull()
    val horizontal = tabPosition == ElTabPosition.Top || tabPosition == ElTabPosition.Bottom

    val contentBox: @Composable (Modifier) -> Unit = { extra ->
        Box(
            extra
                .then(if (type == ElTabsType.BorderCard) Modifier.border(1.dp, colors.borderLight) else Modifier)
                .padding(contentPadding),
        ) {
            activeTab?.content?.invoke()
        }
    }

    if (horizontal) {
        Column(modifier) {
            if (tabPosition == ElTabPosition.Top) {
                HorizontalTabHeader(
                    tabs = tabs,
                    activeName = activeName,
                    type = type,
                    tabPosition = tabPosition,
                    closable = closable,
                    addable = addable,
                    stretch = stretch,
                    onTabChange = onTabChange,
                    onTabRemove = onTabRemove,
                    onTabAdd = onTabAdd,
                )
                contentBox(Modifier.fillMaxWidth())
            } else {
                contentBox(Modifier.fillMaxWidth())
                HorizontalTabHeader(
                    tabs = tabs,
                    activeName = activeName,
                    type = type,
                    tabPosition = tabPosition,
                    closable = closable,
                    addable = addable,
                    stretch = stretch,
                    onTabChange = onTabChange,
                    onTabRemove = onTabRemove,
                    onTabAdd = onTabAdd,
                )
            }
        }
    } else {
        Row(modifier) {
            if (tabPosition == ElTabPosition.Left) {
                VerticalTabHeader(
                    tabs, activeName, type, closable, addable, onTabChange, onTabRemove, onTabAdd,
                )
                contentBox(Modifier.weight(1f))
            } else {
                contentBox(Modifier.weight(1f))
                VerticalTabHeader(
                    tabs, activeName, type, closable, addable, onTabChange, onTabRemove, onTabAdd,
                )
            }
        }
    }
}

@Composable
private fun HorizontalTabHeader(
    tabs: List<ElTabData>,
    activeName: String,
    type: ElTabsType,
    tabPosition: ElTabPosition,
    closable: Boolean,
    addable: Boolean,
    stretch: Boolean,
    onTabChange: (String) -> Unit,
    onTabRemove: ((String) -> Unit)?,
    onTabAdd: (() -> Unit)?,
) {
    val colors = LocalElTheme.current.colors
    Row(
        Modifier
            .fillMaxWidth()
            .then(
                if (type == ElTabsType.Card || type == ElTabsType.BorderCard) Modifier.background(colors.fillLight)
                else Modifier
            )
            .then(
                if (tabPosition == ElTabPosition.Top) Modifier.drawBottomLine(colors.borderLight, 1.dp)
                else Modifier.drawTopLine(colors.borderLight, 1.dp)
            )
            .then(if (!stretch) Modifier.horizontalScroll(rememberScrollState()) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEach { tab ->
            val item: @Composable () -> Unit = {
                TabHeaderContent(
                    tab = tab,
                    active = tab.name == activeName,
                    type = type,
                    horizontal = true,
                    closable = closable && tab.closable,
                    onSelect = { if (!tab.disabled) onTabChange(tab.name) },
                    onClose = { onTabRemove?.invoke(tab.name) },
                )
            }
            if (stretch) Box(Modifier.weight(1f)) { item() } else item()
        }
        if (addable) AddTabButton(onTabAdd)
    }
}

@Composable
private fun VerticalTabHeader(
    tabs: List<ElTabData>,
    activeName: String,
    type: ElTabsType,
    closable: Boolean,
    addable: Boolean,
    onTabChange: (String) -> Unit,
    onTabRemove: ((String) -> Unit)?,
    onTabAdd: (() -> Unit)?,
) {
    val colors = LocalElTheme.current.colors
    Column(
        Modifier
            .width(160.dp)
            .fillMaxHeight()
            .background(colors.fillLight)
            .drawRightLine(colors.borderLight),
    ) {
        tabs.forEach { tab ->
            TabHeaderContent(
                tab = tab,
                active = tab.name == activeName,
                type = type,
                horizontal = false,
                closable = closable && tab.closable,
                onSelect = { if (!tab.disabled) onTabChange(tab.name) },
                onClose = { onTabRemove?.invoke(tab.name) },
            )
        }
        if (addable) AddTabButton(onTabAdd)
    }
}

@Composable
private fun TabHeaderContent(
    tab: ElTabData,
    active: Boolean,
    type: ElTabsType,
    horizontal: Boolean,
    closable: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val fg = when {
        tab.disabled -> colors.textDisabled
        active -> colors.primary
        else -> colors.textRegular
    }
    val bg = if (active && (type == ElTabsType.Card || type == ElTabsType.BorderCard)) colors.background else Color.Transparent
    Row(
        Modifier
            .fillMaxWidth()
            .height(if (horizontal) 40.dp else 48.dp)
            .then(
                if (active && type == ElTabsType.Line) {
                    if (horizontal) Modifier.drawBottomLine(colors.primary, 2.dp)
                    else Modifier.drawRightLine(colors.primary, 2.dp)
                } else Modifier
            )
            .background(bg, if (type == ElTabsType.Card) RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp) else RoundedCornerShape(0.dp))
            .clickable(enabled = !tab.disabled, onClick = onSelect)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (tab.icon != null) {
            ElIcon(tab.icon, size = 14.dp, tint = fg)
            Spacer(Modifier.width(6.dp))
        }
        BasicText(
            text = tab.label,
            style = theme.typography.base.copy(
                color = fg,
                fontWeight = if (active) FontWeight.Medium else FontWeight.Normal,
            ),
        )
        if (closable) {
            Spacer(Modifier.width(6.dp))
            ElIcon(
                ElIcons.Close,
                size = 12.dp,
                tint = fg,
                modifier = Modifier.clickable(enabled = !tab.disabled) { onClose() },
            )
        }
    }
}

@Composable
private fun AddTabButton(onAdd: (() -> Unit)?) {
    Box(
        Modifier
            .size(40.dp)
            .clickable(enabled = onAdd != null) { onAdd?.invoke() },
        contentAlignment = Alignment.Center,
    ) {
        ElIcon(ElIcons.Plus, size = 14.dp, tint = LocalElTheme.current.colors.textSecondary)
    }
}

private fun Modifier.drawBottomLine(color: Color, width: Dp): Modifier = this.drawBehind {
    val stroke = width.toPx()
    drawRect(color, Offset(0f, size.height - stroke), Size(size.width, stroke))
}

private fun Modifier.drawTopLine(color: Color, width: Dp): Modifier = this.drawBehind {
    drawRect(color, Offset.Zero, Size(size.width, width.toPx()))
}

private fun Modifier.drawRightLine(color: Color, width: Dp = 1.dp): Modifier = this.drawBehind {
    val stroke = width.toPx()
    drawRect(color, Offset(size.width - stroke, 0f), Size(stroke, size.height))
}
