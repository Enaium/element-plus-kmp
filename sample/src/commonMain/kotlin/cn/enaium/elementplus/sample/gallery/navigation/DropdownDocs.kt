package cn.enaium.elementplus.sample.gallery.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElContentColor
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun DropdownDocs() {
    val theme = LocalElTheme.current
    val items = listOf(
        ElDropdownItem("Edit", icon = ElIcons.Edit, shortcut = "⌘E"),
        ElDropdownItem("Duplicate", icon = ElIcons.CopyDocument),
        ElDropdownItem("Delete", icon = ElIcons.Delete, divided = true, command = "delete"),
    )
    val plainActions = listOf(
        ElDropdownItem("Action 1"),
        ElDropdownItem("Action 2"),
        ElDropdownItem("Action 3"),
        ElDropdownItem("Action 4"),
    )
    val scrollingActions = remember { (1..12).map { ElDropdownItem("Action $it") } }

    DemoExample("Basic usage", "A dropdown opens on hover by default.") {
        ElDropdown(items = items, onCommand = { ElMessage.show("Command: $it") }) {
            ElButton(onClick = {}, text = "Hover dropdown")
        }
    }

    DemoExample("Click trigger", "Open the menu on click instead of hover.") {
        ElDropdown(
            items = items,
            onCommand = { ElMessage.show("Command: $it") },
            trigger = ElDropdownTrigger.Click,
        ) {
            ElButton(onClick = {}, text = "Click dropdown", type = ElType.Primary, plain = true)
        }
    }

    DemoExample(
        "Context menu trigger",
        "Right-click the area to open the menu anchored at the pointer.",
    ) {
        ElDropdown(
            items = items,
            onCommand = { ElMessage.show("Command: $it") },
            trigger = ElDropdownTrigger.ContextMenu,
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(theme.colors.fillLight, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    "Right click here",
                    style = theme.typography.base.copy(color = theme.colors.textRegular),
                )
            }
        }
    }

    DemoExample("Command event", "Each item emits a command string.") {
        ElDropdown(
            items = items,
            onCommand = { ElMessage.show("Selected: $it") },
            trigger = ElDropdownTrigger.Click,
        ) {
            ElText("Choose an action", type = ElType.Primary)
        }
    }

    DemoExample("Placements", "Anchor the menu on any of the six top/bottom placements.") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ElDropdown(items = plainActions, onCommand = {}, trigger = ElDropdownTrigger.Click, placement = ElPlacement.TopStart) {
                ElButton(onClick = {}, text = "topStart", size = ElSize.Small)
            }
            ElDropdown(items = plainActions, onCommand = {}, trigger = ElDropdownTrigger.Click, placement = ElPlacement.Top) {
                ElButton(onClick = {}, text = "top", size = ElSize.Small)
            }
            ElDropdown(items = plainActions, onCommand = {}, trigger = ElDropdownTrigger.Click, placement = ElPlacement.TopEnd) {
                ElButton(onClick = {}, text = "topEnd", size = ElSize.Small)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ElDropdown(items = plainActions, onCommand = {}, trigger = ElDropdownTrigger.Click, placement = ElPlacement.BottomStart) {
                ElButton(onClick = {}, text = "bottomStart", size = ElSize.Small)
            }
            ElDropdown(items = plainActions, onCommand = {}, trigger = ElDropdownTrigger.Click, placement = ElPlacement.Bottom) {
                ElButton(onClick = {}, text = "bottom", size = ElSize.Small)
            }
            ElDropdown(items = plainActions, onCommand = {}, trigger = ElDropdownTrigger.Click, placement = ElPlacement.BottomEnd) {
                ElButton(onClick = {}, text = "bottomEnd", size = ElSize.Small)
            }
        }
    }

    DemoExample("Disabled item", "Disabled menu items cannot emit commands.") {
        ElDropdown(
            items = listOf(
                ElDropdownItem("Edit"),
                ElDropdownItem("Delete", disabled = true),
                ElDropdownItem("Archive"),
            ),
            onCommand = { ElMessage.show("Command: $it") },
            trigger = ElDropdownTrigger.Click,
        ) {
            ElButton(onClick = {}, text = "With disabled item")
        }
    }

    DemoExample("How to trigger", "Use hover or click to open the menu.") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ElDropdown(items = items, onCommand = {}, trigger = ElDropdownTrigger.Hover) {
                ElButton(onClick = {}, text = "Hover trigger")
            }
            ElDropdown(items = items, onCommand = {}, trigger = ElDropdownTrigger.Click) {
                ElButton(onClick = {}, text = "Click trigger")
            }
        }
    }

    DemoExample("Menu hiding behavior", "Keep the menu open after a command when hideOnClick is false.") {
        ElDropdown(items = items, onCommand = { ElMessage.show("Command: $it") }, hideOnClick = false) {
            ElButton(onClick = {}, text = "Stays open")
        }
    }

    DemoExample(
        "Split button",
        "A split button pairs a primary action with a caret that opens the menu.",
    ) {
        ElDropdown(
            items = plainActions,
            onCommand = { ElMessage.show("Command: $it") },
            trigger = ElDropdownTrigger.Click,
            splitButton = true,
            type = ElType.Primary,
            onClick = { ElMessage.show("Main button clicked") },
        ) { SplitButtonLabel("Dropdown List") }
    }

    DemoExample(
        "Split button sizes",
        "Split buttons accept the large, default and small sizes.",
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ElDropdown(
                items = plainActions,
                onCommand = {},
                trigger = ElDropdownTrigger.Click,
                splitButton = true,
                type = ElType.Primary,
                size = ElSize.Large,
            ) { SplitButtonLabel("Large") }
            ElDropdown(
                items = plainActions,
                onCommand = {},
                trigger = ElDropdownTrigger.Click,
                splitButton = true,
                type = ElType.Primary,
            ) { SplitButtonLabel("Default") }
            ElDropdown(
                items = plainActions,
                onCommand = {},
                trigger = ElDropdownTrigger.Click,
                splitButton = true,
                type = ElType.Primary,
                size = ElSize.Small,
            ) { SplitButtonLabel("Small") }
        }
    }

    DemoExample(
        "Programmatic control",
        "Open and close menus from code with ElDropdownState; opening list 2 closes list 1.",
    ) {
        val first = remember { ElDropdownState() }
        val second = remember { ElDropdownState() }
        ElButton(onClick = { first.open() }, text = "show", size = ElSize.Small)
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            ElDropdown(
                items = plainActions,
                onCommand = {},
                trigger = ElDropdownTrigger.ContextMenu,
                state = first,
            ) {
                BasicText(
                    "Dropdown List 1",
                    style = theme.typography.base.copy(color = theme.colors.primary),
                )
            }
            ElDropdown(
                items = plainActions,
                onCommand = {},
                trigger = ElDropdownTrigger.ContextMenu,
                state = second,
                onVisibleChange = { visible -> if (visible) first.close() else first.open() },
            ) {
                BasicText(
                    "Dropdown List 2",
                    style = theme.typography.base.copy(color = theme.colors.primary),
                )
            }
        }
    }

    DemoExample(
        "Virtual trigger",
        "Anchor the popup at an arbitrary point with virtualTrigger: right-click the card to open the menu at the pointer.",
    ) {
        val dropdownState = remember { ElDropdownState() }
        var trigger by remember { mutableStateOf(ElVirtualTrigger()) }
        Box(
            Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(theme.colors.fillLight, RoundedCornerShape(6.dp))
                .pointerInput(Unit) {
                    val density = this
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type == PointerEventType.Press && event.buttons.isSecondaryPressed) {
                                val position = event.changes.firstOrNull()?.position ?: Offset.Zero
                                trigger = ElVirtualTrigger(
                                    x = with(density) { position.x.toDp() },
                                    y = with(density) { position.y.toDp() },
                                )
                                dropdownState.open()
                            }
                        }
                    }
                },
            contentAlignment = Alignment.TopStart,
        ) {
            BasicText(
                "Right click anywhere in this card",
                modifier = Modifier.align(Alignment.Center),
                style = theme.typography.base.copy(color = theme.colors.textRegular),
            )
            ElDropdown(
                items = plainActions,
                onCommand = { ElMessage.show("Command: $it") },
                trigger = ElDropdownTrigger.Click,
                state = dropdownState,
                virtualTrigger = trigger,
                placement = ElPlacement.BottomStart,
                showArrow = false,
            ) {}
        }
    }

    DemoExample(
        "Max height",
        "Constrain the popup height with maxHeight; long item lists scroll.",
    ) {
        ElDropdown(
            items = scrollingActions,
            onCommand = { ElMessage.show("Command: $it") },
            trigger = ElDropdownTrigger.Click,
            maxHeight = 140.dp,
        ) {
            ElButton(onClick = {}, text = "Scrollable menu")
        }
    }

    DemoExample(
        "Custom item content",
        "itemContent replaces the row content of every generated item, keeping hover and command handling.",
    ) {
        ElDropdown(
            items = listOf(
                ElDropdownItem("Edit", command = "edit"),
                ElDropdownItem("Duplicate", command = "duplicate"),
                ElDropdownItem("Delete", command = "delete"),
            ),
            onCommand = { ElMessage.show("Command: $it") },
            trigger = ElDropdownTrigger.Click,
            itemContent = { item ->
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    BasicText(
                        text = item.label,
                        modifier = Modifier.weight(1f),
                        style = theme.typography.small.copy(color = theme.colors.textRegular),
                    )
                    Box(
                        Modifier
                            .background(theme.colors.primary, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 1.dp),
                    ) {
                        BasicText(
                            text = "NEW",
                            style = theme.typography.extraSmall.copy(color = theme.colors.white),
                        )
                    }
                }
            },
        ) {
            ElButton(onClick = {}, text = "Custom items")
        }
    }

    DemoExample(
        "Custom menu content",
        "Compose the popup from ElDropdownMenu and ElDropdownMenuItem for headers, dividers and custom rows.",
    ) {
        ElDropdown(
            items = emptyList(),
            onCommand = { ElMessage.show("Command: $it") },
            trigger = ElDropdownTrigger.Click,
            menuContent = {
                ElDropdownMenu(maxHeight = 220.dp) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        BasicText(
                            "Recent files",
                            style = theme.typography.extraSmall.copy(color = theme.colors.textPlaceholder),
                        )
                    }
                    ElDropdownMenuItem(label = "Action 1", icon = ElIcons.Edit, shortcut = "⌘E")
                    ElDropdownMenuItem(label = "Action 2", icon = ElIcons.CopyDocument)
                    ElDropdownMenuItem(label = "Action 3", divided = true)
                    ElDropdownMenuItem(
                        label = "Custom row",
                        content = { color ->
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                BasicText(
                                    text = "With an icon slot",
                                    modifier = Modifier.weight(1f),
                                    style = theme.typography.small.copy(color = color),
                                )
                                ElIcon(ElIcons.MoreFilled, size = 12.dp, tint = color)
                            }
                        },
                    )
                    ElDropdownMenuItem(label = "Disabled", disabled = true)
                }
            },
        ) {
            ElButton(onClick = {}, text = "Custom menu")
        }
    }
}

/** Label styled with the surrounding button's content color. */
@Composable
private fun SplitButtonLabel(text: String) {
    BasicText(
        text = text,
        style = LocalElTheme.current.typography.base.copy(color = LocalElContentColor.current),
    )
}
