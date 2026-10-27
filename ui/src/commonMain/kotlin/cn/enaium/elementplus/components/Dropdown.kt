package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.internal.rememberElInteractionState
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElDropdownTrigger { Click, Hover }

data class ElDropdownItem(
    val label: String,
    val command: String = label,
    val disabled: Boolean = false,
    val divided: Boolean = false,
    val icon: ImageVector? = null,
    val shortcut: String? = null,
)

/**
 * `<el-dropdown>` anchored menu triggered by click or hover.
 */
@Composable
fun ElDropdown(
    items: List<ElDropdownItem>,
    onCommand: (String) -> Unit,
    modifier: Modifier = Modifier,
    trigger: ElDropdownTrigger = ElDropdownTrigger.Hover,
    placement: ElPlacement = ElPlacement.BottomEnd,
    disabled: Boolean = false,
    showArrow: Boolean = true,
    hideOnClick: Boolean = true,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    var visible by remember { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    if (trigger == ElDropdownTrigger.Hover) {
        androidx.compose.runtime.LaunchedEffect(hovered) {
            if (hovered && !visible) {
                visible = true
                onVisibleChange?.invoke(true)
            }
        }
    }
    Box(modifier.hoverable(interaction)) {
        Box {
            content()
            if (showArrow) {
                ElIcon(
                    ElIcons.ArrowDown,
                    size = 10.dp,
                    tint = colors.textPlaceholder,
                    contentDescription = locale.dropdown.toggleDropdown,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 7.dp),
                )
            }
        }
        if (trigger == ElDropdownTrigger.Click) {
            Box(
                Modifier.matchParentSize().clickable(enabled = !disabled) {
                    visible = !visible
                    onVisibleChange?.invoke(visible)
                },
            )
        }
        ElPopper(
            visible = visible,
            onDismissRequest = {
                visible = false
                onVisibleChange?.invoke(false)
            },
            placement = placement,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 6.dp),
            focusable = false,
        ) {
            DropdownMenu(items = items, onCommand = { command ->
                onCommand(command)
                if (hideOnClick) {
                    visible = false
                    onVisibleChange?.invoke(false)
                }
            })
        }
    }
}

@Composable
private fun DropdownMenu(
    items: List<ElDropdownItem>,
    onCommand: (String) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    ElPopperSurface(
        Modifier
            .widthIn(min = 160.dp, max = 280.dp)
            .width(IntrinsicSize.Max),
    ) {
        Column(Modifier.padding(vertical = 5.dp)) {
            items.forEach { item ->
                if (item.divided) {
                    Box(Modifier.fillMaxWidth().height(1.dp).padding(horizontal = 8.dp).background(colors.borderLighter))
                }
                val interaction = remember(item) { MutableInteractionSource() }
                val hovered = rememberElInteractionState(interaction).hovered
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .background(if (hovered && !item.disabled) colors.fillLight else colors.backgroundOverlay)
                        .hoverable(interaction)
                        .clickable(enabled = !item.disabled) { onCommand(item.command) }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (item.icon != null) {
                        ElIcon(item.icon, size = 14.dp, tint = if (item.disabled) colors.textDisabled else colors.textRegular)
                        Spacer(Modifier.width(6.dp))
                    }
                    BasicText(
                        text = item.label,
                        modifier = Modifier.weight(1f),
                        style = theme.typography.small.copy(
                            color = if (item.disabled) colors.textDisabled else colors.textRegular,
                        ),
                    )
                    if (item.shortcut != null) {
                        BasicText(
                            text = item.shortcut,
                            style = theme.typography.extraSmall.copy(color = colors.textPlaceholder),
                        )
                    }
                }
            }
        }
    }
}
