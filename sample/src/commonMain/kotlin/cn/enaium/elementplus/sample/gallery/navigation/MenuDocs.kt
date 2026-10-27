package cn.enaium.elementplus.sample.gallery.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun MenuDocs() {
    val theme = LocalElTheme.current
    var horizontal by remember { mutableStateOf("home") }
    var customColors by remember { mutableStateOf("home") }
    var vertical by remember { mutableStateOf("1-2") }
    var collapseSelection by remember { mutableStateOf("2") }
    var isCollapse by remember { mutableStateOf(true) }
    var leftRight by remember { mutableStateOf("1") }
    var events by remember { mutableStateOf("Interact with a submenu to see open/close events.") }
    var controlledOpen by remember { mutableStateOf(setOf("1")) }

    DemoExample(
        "Basic usage",
        "Horizontal menu with nested submenus (three levels), a disabled item and custom colors.",
    ) {
        ElMenu(
            activeIndex = horizontal,
            onSelect = { horizontal = it },
            mode = ElMenuMode.Horizontal,
            modifier = Modifier.fillMaxWidth(),
        ) {
            ElMenuItem("home", "Processing Center")
            ElSubMenu("workspace", "Workspace") {
                ElMenuItem("1-1", "item one")
                ElMenuItem("1-2", "item two")
                ElMenuItem("1-3", "item three")
                ElSubMenu("1-4", "item four") {
                    ElMenuItem("1-4-1", "item one")
                    ElMenuItem("1-4-2", "item two")
                    ElMenuItem("1-4-3", "item three")
                }
            }
            ElMenuItem("info", "Info", disabled = true)
            ElMenuItem("orders", "Orders")
        }
        ElMenu(
            activeIndex = customColors,
            onSelect = { customColors = it },
            mode = ElMenuMode.Horizontal,
            background = Color(0xFF545C64),
            textColor = Color(0xFFFFFFFF),
            activeTextColor = Color(0xFFFFD04B),
            modifier = Modifier.fillMaxWidth(),
        ) {
            ElMenuItem("home", "Processing Center")
            ElSubMenu("workspace", "Workspace") {
                ElMenuItem("1-1", "item one")
                ElMenuItem("1-2", "item two")
            }
            ElMenuItem("info", "Info", disabled = true)
            ElMenuItem("orders", "Orders")
        }
    }

    DemoExample(
        "Vertical menu with item groups",
        "Submenu content can be split into titled groups; open/close events fire for every submenu.",
    ) {
        ElMenu(
            activeIndex = vertical,
            onSelect = { vertical = it },
            defaultOpeneds = setOf("1"),
            onOpen = { events = "open: $it" },
            onClose = { events = "close: $it" },
            modifier = Modifier.height(320.dp),
        ) {
            ElSubMenu("1", "Navigator One", icon = ElIcons.Location) {
                ElMenuItemGroup(title = "Group One") {
                    ElMenuItem("1-1", "item one")
                    ElMenuItem("1-2", "item two")
                }
                ElMenuItemGroup(title = "Group Two") {
                    ElMenuItem("1-3", "item three")
                }
                ElSubMenu("1-4", "item four") {
                    ElMenuItem("1-4-1", "item one")
                }
            }
            ElMenuItem("2", "Navigator Two", icon = ElIcons.Menu)
            ElMenuItem("3", "Navigator Three", icon = ElIcons.Document, disabled = true)
            ElMenuItem("4", "Navigator Four", icon = ElIcons.Setting)
        }
        ElText(events, type = cn.enaium.elementplus.theme.ElType.Info)
        ElMenu(
            activeIndex = vertical,
            onSelect = { vertical = it },
            defaultOpeneds = setOf("1"),
            background = Color(0xFF545C64),
            textColor = Color(0xFFFFFFFF),
            activeTextColor = Color(0xFFFFD04B),
            modifier = Modifier.height(320.dp),
        ) {
            ElSubMenu("1", "Navigator One", icon = ElIcons.Location) {
                ElMenuItemGroup(title = "Group One") {
                    ElMenuItem("1-1", "item one")
                    ElMenuItem("1-2", "item two")
                }
                ElMenuItemGroup(title = "Group Two") {
                    ElMenuItem("1-3", "item three")
                }
            }
            ElMenuItem("2", "Navigator Two", icon = ElIcons.Menu)
            ElMenuItem("3", "Navigator Three", icon = ElIcons.Document, disabled = true)
            ElMenuItem("4", "Navigator Four", icon = ElIcons.Setting)
        }
    }

    DemoExample(
        "Collapsed menu with groups",
        "Collapse the menu to icons; plain items show a tooltip and submenus open in a popup.",
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ElButton(
                onClick = { isCollapse = false },
                text = "expand",
                type = if (!isCollapse) cn.enaium.elementplus.theme.ElType.Primary else cn.enaium.elementplus.theme.ElType.Default,
                size = cn.enaium.elementplus.theme.ElSize.Small,
            )
            ElButton(
                onClick = { isCollapse = true },
                text = "collapse",
                type = if (isCollapse) cn.enaium.elementplus.theme.ElType.Primary else cn.enaium.elementplus.theme.ElType.Default,
                size = cn.enaium.elementplus.theme.ElSize.Small,
            )
        }
        ElMenu(
            activeIndex = collapseSelection,
            onSelect = { collapseSelection = it },
            collapsed = isCollapse,
            defaultOpeneds = setOf("1"),
            onOpen = { events = "open: $it" },
            onClose = { events = "close: $it" },
            modifier = Modifier.height(280.dp),
        ) {
            ElSubMenu("1", "Navigator One", icon = ElIcons.Location) {
                ElMenuItemGroup(title = "Group One") {
                    ElMenuItem("1-1", "item one")
                    ElMenuItem("1-2", "item two")
                }
                ElMenuItemGroup(title = "Group Two") {
                    ElMenuItem("1-3", "item three")
                }
            }
            ElMenuItem("2", "Navigator Two", icon = ElIcons.Menu)
            ElMenuItem("3", "Navigator Three", icon = ElIcons.Document, disabled = true)
            ElMenuItem("4", "Navigator Four", icon = ElIcons.Setting)
        }
        ElText(events, type = cn.enaium.elementplus.theme.ElType.Info)
    }

    DemoExample(
        "Ellipsis overflow",
        "Horizontal items that do not fit collapse into a trailing \"...\" submenu.",
    ) {
        ElMenu(
            activeIndex = horizontal,
            onSelect = { horizontal = it },
            mode = ElMenuMode.Horizontal,
            modifier = Modifier.width(360.dp),
        ) {
            ElMenuItem("home", "Processing Center")
            ElSubMenu("workspace", "Workspace") {
                ElMenuItem("1-1", "item one")
                ElMenuItem("1-2", "item two")
            }
            ElMenuItem("info", "Info", disabled = true)
            ElMenuItem("orders", "Orders")
            ElMenuItem("reports", "Reports")
            ElMenuItem("archive", "Archive")
        }
    }

    DemoExample(
        "Custom ellipsis icon",
        "Replace the default \"...\" overflow trigger with an icon.",
    ) {
        ElMenu(
            activeIndex = horizontal,
            onSelect = { horizontal = it },
            mode = ElMenuMode.Horizontal,
            ellipsisIcon = ElIcons.MoreFilled,
            modifier = Modifier.width(300.dp),
        ) {
            ElMenuItem("home", "Processing Center")
            ElSubMenu("workspace", "Workspace") {
                ElMenuItem("1-1", "item one")
                ElMenuItem("1-2", "item two")
            }
            ElMenuItem("info", "Info", disabled = true)
            ElMenuItem("orders", "Orders")
            ElMenuItem("reports", "Reports")
        }
    }

    DemoExample(
        "Left and right layout",
        "Disable ellipsis to let the first item act as a logo on the left and keep the rest in a plain row.",
    ) {
        ElMenu(
            activeIndex = leftRight,
            onSelect = { leftRight = it },
            mode = ElMenuMode.Horizontal,
            ellipsis = false,
            modifier = Modifier.fillMaxWidth(),
        ) {
            ElMenuItem("0", "Logo") {
                Box(
                    Modifier
                        .padding(vertical = 8.dp)
                        .background(theme.colors.primary, RoundedCornerShape(4.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        text = "ELEMENT PLUS",
                        style = theme.typography.small.copy(color = theme.colors.white),
                    )
                }
            }
            ElMenuItem("1", "Processing Center")
            ElSubMenu("2", "Workspace") {
                ElMenuItem("2-1", "item one")
                ElMenuItem("2-2", "item two")
            }
        }
    }

    DemoExample(
        "Hover submenu trigger",
        "menuTrigger = Hover (the default) opens horizontal submenus without a click.",
    ) {
        ElMenu(
            activeIndex = horizontal,
            onSelect = { horizontal = it },
            mode = ElMenuMode.Horizontal,
            menuTrigger = ElMenuTrigger.Hover,
            showTimeout = 100,
            hideTimeout = 200,
            modifier = Modifier.fillMaxWidth(),
        ) {
            ElMenuItem("home", "Home")
            ElSubMenu("workspace", "Hover me") {
                ElMenuItem("1-1", "item one")
                ElMenuItem("1-2", "item two")
            }
            ElMenuItem("orders", "Orders")
        }
    }

    DemoExample(
        "Click submenu trigger",
        "menuTrigger = Click requires a click to open a submenu.",
    ) {
        ElMenu(
            activeIndex = horizontal,
            onSelect = { horizontal = it },
            mode = ElMenuMode.Horizontal,
            menuTrigger = ElMenuTrigger.Click,
            modifier = Modifier.fillMaxWidth(),
        ) {
            ElMenuItem("home", "Home")
            ElSubMenu("workspace", "Click me") {
                ElMenuItem("1-1", "item one")
                ElMenuItem("1-2", "item two")
            }
            ElMenuItem("orders", "Orders")
        }
    }

    DemoExample(
        "Popper offset",
        "Set the popup gap per menu and override it on individual submenus.",
    ) {
        ElMenu(
            activeIndex = horizontal,
            onSelect = { horizontal = it },
            mode = ElMenuMode.Horizontal,
            popperOffset = 16.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            ElMenuItem("home", "Processing Center")
            ElSubMenu("workspace", "Workspace") {
                ElMenuItem("1-1", "item one")
                ElMenuItem("1-2", "item two")
                ElSubMenu("1-4", "item four", popperOffset = 20.dp) {
                    ElMenuItem("1-4-1", "item one")
                    ElMenuItem("1-4-2", "item two")
                }
            }
            ElSubMenu("override", "Override Popper Offset", popperOffset = 8.dp) {
                ElMenuItem("2-1", "item one")
                ElMenuItem("2-2", "item two")
            }
            ElMenuItem("orders", "Orders")
        }
    }

    DemoExample(
        "Controlled submenu state",
        "Pass `openeds` to control which submenus are expanded from outside the menu.",
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ElButton(
                onClick = { controlledOpen = setOf("1", "1-4") },
                text = "open all",
                size = cn.enaium.elementplus.theme.ElSize.Small,
            )
            ElButton(
                onClick = { controlledOpen = emptySet() },
                text = "close all",
                size = cn.enaium.elementplus.theme.ElSize.Small,
            )
        }
        ElMenu(
            activeIndex = vertical,
            onSelect = { vertical = it },
            openeds = controlledOpen,
            modifier = Modifier.height(260.dp),
        ) {
            ElSubMenu("1", "Navigator One", icon = ElIcons.Location) {
                ElMenuItem("1-1", "item one")
                ElSubMenu("1-4", "item four") {
                    ElMenuItem("1-4-1", "item one")
                }
            }
            ElMenuItem("2", "Navigator Two", icon = ElIcons.Menu)
        }
    }

    DemoExample(
        "Item and submenu slots",
        "MenuItem takes a content slot and SubMenu takes a title slot for custom titles and badges.",
    ) {
        ElMenu(
            activeIndex = vertical,
            onSelect = { vertical = it },
            defaultOpeneds = setOf("1"),
            modifier = Modifier.height(240.dp),
        ) {
            ElSubMenu("1", "Navigator One", icon = ElIcons.Location, titleContent = {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    BasicText(
                        text = "Navigator One",
                        style = theme.typography.base.copy(color = theme.colors.textRegular),
                    )
                    Box(
                        Modifier
                            .background(theme.colors.primary, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 1.dp),
                    ) {
                        BasicText(
                            text = "New",
                            style = theme.typography.extraSmall.copy(color = theme.colors.white),
                        )
                    }
                }
            }) {
                ElMenuItem("1-1", "item one")
                ElMenuItem("1-2", "item two")
            }
            ElMenuItem("2", "Navigator Two", icon = ElIcons.Menu, content = {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    BasicText(
                        text = "Custom row",
                        style = theme.typography.base.copy(color = theme.colors.textRegular),
                    )
                    Box(
                        Modifier
                            .background(theme.colors.danger, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 1.dp),
                    ) {
                        BasicText(
                            text = "3",
                            style = theme.typography.extraSmall.copy(color = theme.colors.white),
                        )
                    }
                }
            })
        }
    }
}
