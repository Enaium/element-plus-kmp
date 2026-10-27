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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElTabsType { Line, Card, BorderCard }
enum class ElTabPosition { Top, Bottom, Left, Right }

/**
 * A single tab.
 *
 * `labelContent` mirrors the upstream `label` slot, `closeIcon` overrides the
 * close glyph and `content` is the pane body.
 */
class ElTabData(
    val name: String,
    val label: String,
    val icon: ImageVector? = null,
    val disabled: Boolean = false,
    val closable: Boolean = true,
    val labelContent: (@Composable () -> Unit)? = null,
    val closeIcon: ImageVector? = null,
    val content: @Composable () -> Unit,
)

/**
 * `<el-tabs>` with line/card styles, closable/addable tabs and four positions.
 *
 * When [activeName] is `null` the tabs are uncontrolled and start at
 * [defaultValue] (or the first enabled tab). [lazy] disposes inactive panes;
 * by default all panes stay composed so their state survives tab switches.
 * [beforeLeave] can veto a switch and [onTabClick] fires for every click,
 * distinct from [onTabChange].
 */
@Composable
fun ElTabs(
    tabs: List<ElTabData>,
    activeName: String? = null,
    onTabChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    type: ElTabsType = ElTabsType.Line,
    tabPosition: ElTabPosition = ElTabPosition.Top,
    closable: Boolean = false,
    addable: Boolean = false,
    onTabRemove: ((String) -> Unit)? = null,
    onTabAdd: (() -> Unit)? = null,
    stretch: Boolean = false,
    contentPadding: Dp = 16.dp,
    defaultValue: String? = null,
    editable: Boolean = false,
    onTabClick: ((String) -> Unit)? = null,
    beforeLeave: ((newName: String, oldName: String) -> Boolean)? = null,
    lazy: Boolean = false,
    addIcon: ImageVector? = null,
    addIconContent: (@Composable () -> Unit)? = null,
    closeIcon: ImageVector = ElIcons.Close,
) {
    val colors = LocalElTheme.current.colors
    var internalName by remember {
        mutableStateOf(
            defaultValue ?: activeName
                ?: tabs.firstOrNull { !it.disabled }?.name
                ?: tabs.firstOrNull()?.name
                ?: "",
        )
    }
    val requestedName = activeName ?: internalName
    val resolvedName = requestedName.takeIf { name -> tabs.any { it.name == name } }
        ?: tabs.firstOrNull { !it.disabled }?.name
        ?: tabs.firstOrNull()?.name
        ?: ""
    val effectiveClosable = closable || editable
    val effectiveAddable = addable || editable
    val horizontal = tabPosition == ElTabPosition.Top || tabPosition == ElTabPosition.Bottom

    val select: (ElTabData) -> Unit = { tab ->
        if (!tab.disabled) {
            onTabClick?.invoke(tab.name)
            val canLeave = resolvedName == tab.name || beforeLeave?.invoke(tab.name, resolvedName) != false
            if (canLeave && tab.name != resolvedName) {
                internalName = tab.name
                onTabChange(tab.name)
            }
        }
    }

    val contentBox: @Composable (Modifier) -> Unit = { extra ->
        Box(
            extra
                .then(if (type == ElTabsType.BorderCard) Modifier.border(1.dp, colors.borderLight) else Modifier)
                .padding(contentPadding),
        ) {
            if (lazy) {
                tabs.firstOrNull { it.name == resolvedName }?.content?.invoke()
            } else {
                TabsPaneHost(tabs, resolvedName)
            }
        }
    }

    if (horizontal) {
        Column(modifier) {
            if (tabPosition == ElTabPosition.Top) {
                HorizontalTabHeader(
                    tabs = tabs,
                    activeName = resolvedName,
                    type = type,
                    tabPosition = tabPosition,
                    closable = effectiveClosable,
                    addable = effectiveAddable,
                    stretch = stretch,
                    onTabSelect = select,
                    onTabRemove = onTabRemove,
                    onTabAdd = onTabAdd,
                    addIcon = addIcon,
                    addIconContent = addIconContent,
                    closeIcon = closeIcon,
                )
                contentBox(Modifier.fillMaxWidth())
            } else {
                contentBox(Modifier.fillMaxWidth())
                HorizontalTabHeader(
                    tabs = tabs,
                    activeName = resolvedName,
                    type = type,
                    tabPosition = tabPosition,
                    closable = effectiveClosable,
                    addable = effectiveAddable,
                    stretch = stretch,
                    onTabSelect = select,
                    onTabRemove = onTabRemove,
                    onTabAdd = onTabAdd,
                    addIcon = addIcon,
                    addIconContent = addIconContent,
                    closeIcon = closeIcon,
                )
            }
        }
    } else {
        Row(modifier) {
            if (tabPosition == ElTabPosition.Left) {
                VerticalTabHeader(
                    tabs, resolvedName, type, tabPosition, effectiveClosable, effectiveAddable,
                    select, onTabRemove, onTabAdd, addIcon, addIconContent, closeIcon,
                )
                contentBox(Modifier.weight(1f))
            } else {
                contentBox(Modifier.weight(1f))
                VerticalTabHeader(
                    tabs, resolvedName, type, tabPosition, effectiveClosable, effectiveAddable,
                    select, onTabRemove, onTabAdd, addIcon, addIconContent, closeIcon,
                )
            }
        }
    }
}

/**
 * Keeps every pane composed so inactive panes retain their state, while only
 * the active pane is measured and placed.
 */
@Composable
private fun TabsPaneHost(tabs: List<ElTabData>, activeName: String) {
    if (tabs.isEmpty()) return
    val activeIndex = tabs.indexOfFirst { it.name == activeName }.let { if (it < 0) 0 else it }
    Layout(
        content = {
            tabs.forEach { tab ->
                key(tab.name) { Box { tab.content() } }
            }
        },
    ) { measurables, constraints ->
        if (measurables.isEmpty()) {
            layout(0, 0) {}
        } else {
            val effectiveIndex = activeIndex.coerceIn(0, measurables.lastIndex)
            measurables.forEachIndexed { index, measurable ->
                if (index != effectiveIndex) measurable.measure(Constraints.fixed(0, 0))
            }
            val placeable = measurables[effectiveIndex].measure(constraints)
            layout(placeable.width, placeable.height) {
                placeable.place(0, 0)
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
    onTabSelect: (ElTabData) -> Unit,
    onTabRemove: ((String) -> Unit)?,
    onTabAdd: (() -> Unit)?,
    addIcon: ImageVector?,
    addIconContent: (@Composable () -> Unit)?,
    closeIcon: ImageVector,
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
                    tabPosition = tabPosition,
                    closable = closable && tab.closable,
                    onSelect = { onTabSelect(tab) },
                    onClose = { onTabRemove?.invoke(tab.name) },
                    closeIcon = closeIcon,
                )
            }
            if (stretch) Box(Modifier.weight(1f)) { item() } else item()
        }
        if (addable) AddTabButton(onTabAdd, addIcon, addIconContent)
    }
}

@Composable
private fun VerticalTabHeader(
    tabs: List<ElTabData>,
    activeName: String,
    type: ElTabsType,
    tabPosition: ElTabPosition,
    closable: Boolean,
    addable: Boolean,
    onTabSelect: (ElTabData) -> Unit,
    onTabRemove: ((String) -> Unit)?,
    onTabAdd: (() -> Unit)?,
    addIcon: ImageVector?,
    addIconContent: (@Composable () -> Unit)?,
    closeIcon: ImageVector,
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
                tabPosition = tabPosition,
                closable = closable && tab.closable,
                onSelect = { onTabSelect(tab) },
                onClose = { onTabRemove?.invoke(tab.name) },
                closeIcon = closeIcon,
            )
        }
        if (addable) AddTabButton(onTabAdd, addIcon, addIconContent)
    }
}

@Composable
private fun TabHeaderContent(
    tab: ElTabData,
    active: Boolean,
    type: ElTabsType,
    tabPosition: ElTabPosition,
    closable: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit,
    closeIcon: ImageVector,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val horizontal = tabPosition == ElTabPosition.Top || tabPosition == ElTabPosition.Bottom
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
                    when (tabPosition) {
                        ElTabPosition.Top -> Modifier.drawBottomLine(colors.primary, 2.dp)
                        ElTabPosition.Bottom -> Modifier.drawTopLine(colors.primary, 2.dp)
                        ElTabPosition.Left -> Modifier.drawRightLine(colors.primary, 2.dp)
                        ElTabPosition.Right -> Modifier.drawLeftLine(colors.primary, 2.dp)
                    }
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
        if (tab.labelContent != null) {
            tab.labelContent()
        } else {
            BasicText(
                text = tab.label,
                style = theme.typography.base.copy(
                    color = fg,
                    fontWeight = if (active) FontWeight.Medium else FontWeight.Normal,
                ),
            )
        }
        if (closable) {
            Spacer(Modifier.width(6.dp))
            ElIcon(
                tab.closeIcon ?: closeIcon,
                size = 12.dp,
                tint = fg,
                modifier = Modifier.clickable(enabled = !tab.disabled) { onClose() },
            )
        }
    }
}

@Composable
private fun AddTabButton(
    onAdd: (() -> Unit)?,
    icon: ImageVector?,
    content: (@Composable () -> Unit)?,
) {
    Box(
        Modifier
            .size(40.dp)
            .clickable(enabled = onAdd != null) { onAdd?.invoke() },
        contentAlignment = Alignment.Center,
    ) {
        if (content != null) {
            content()
        } else {
            ElIcon(icon ?: ElIcons.Plus, size = 14.dp, tint = LocalElTheme.current.colors.textSecondary)
        }
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

private fun Modifier.drawLeftLine(color: Color, width: Dp): Modifier = this.drawBehind {
    drawRect(color, Offset.Zero, Size(width.toPx(), size.height))
}
