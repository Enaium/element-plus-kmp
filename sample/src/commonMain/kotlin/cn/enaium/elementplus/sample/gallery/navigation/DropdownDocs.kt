package cn.enaium.elementplus.sample.gallery.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun DropdownDocs() {
    val items = listOf(
        ElDropdownItem("Edit", icon = ElIcons.Edit, shortcut = "⌘E"),
        ElDropdownItem("Duplicate", icon = ElIcons.CopyDocument),
        ElDropdownItem("Delete", icon = ElIcons.Delete, divided = true, command = "delete"),
    )
    DemoExample("Basic usage", "A dropdown opens on hover by default.") {
        ElDropdown(items = items, onCommand = { ElMessage.show("Command: $it") }) {
            ElButton(onClick = {}, text = "Hover dropdown")
        }
    }
    DemoExample("Click trigger", "Open the menu on click instead of hover.") {
        ElDropdown(items = items, onCommand = { ElMessage.show("Command: $it") }, trigger = ElDropdownTrigger.Click) {
            ElButton(onClick = {}, text = "Click dropdown", type = ElType.Primary, plain = true)
        }
    }
    DemoExample("Command event", "Each item emits a command string.") {
        ElDropdown(items = items, onCommand = { ElMessage.show("Selected: $it") }, trigger = ElDropdownTrigger.Click) {
            ElText("Choose an action", type = ElType.Primary)
        }
    }
    DemoExample("Placement", "Place the menu at the top or bottom.") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ElDropdown(
                items = items,
                onCommand = {},
                trigger = ElDropdownTrigger.Click,
                placement = cn.enaium.elementplus.internal.ElPlacement.TopEnd,
            ) { ElButton(onClick = {}, text = "Top end") }
            ElDropdown(
                items = items,
                onCommand = {},
                trigger = ElDropdownTrigger.Click,
                placement = cn.enaium.elementplus.internal.ElPlacement.BottomStart,
            ) { ElButton(onClick = {}, text = "Bottom start") }
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
}
