package cn.enaium.elementplus.sample.gallery.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
private fun rememberDocsTabs() = remember {
    mutableStateListOf(
        ElTabData("components", "Components") { BasicText("Components panel", style = LocalElTheme.current.typography.base) },
        ElTabData("themes", "Themes") { BasicText("Themes panel", style = LocalElTheme.current.typography.base) },
        ElTabData("about", "About") { BasicText("About panel", style = LocalElTheme.current.typography.base) },
    )
}

@Composable
fun TabsDocs() {
    DemoExample("Basic usage", "Tabs switch between different views; tab-click fires on every click.") {
        val tabs = rememberDocsTabs()
        var active by remember { mutableStateOf("components") }
        var clicks by remember { mutableStateOf(0) }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElTabs(
                tabs = tabs,
                activeName = active,
                onTabChange = { active = it },
                onTabClick = { clicks++ },
            )
            BasicText(
                "tab-click count: $clicks",
                style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textSecondary),
            )
        }
    }
    DemoExample("Card style", "Use the card style for a framed header.") {
        val tabs = rememberDocsTabs()
        var active by remember { mutableStateOf("components") }
        ElTabs(tabs = tabs, activeName = active, onTabChange = { active = it }, type = ElTabsType.Card)
    }
    DemoExample("Border card", "A border-card tab set frames the whole content area.") {
        val tabs = rememberDocsTabs()
        var active by remember { mutableStateOf("components") }
        ElTabs(tabs = tabs, activeName = active, onTabChange = { active = it }, type = ElTabsType.BorderCard)
    }
    DemoExample("Tab position", "Switch between top, right, bottom and left tab positions.") {
        val tabs = rememberDocsTabs()
        var active by remember { mutableStateOf("components") }
        var position by remember { mutableStateOf(ElTabPosition.Left) }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(ElTabPosition.Top, ElTabPosition.Right, ElTabPosition.Bottom, ElTabPosition.Left).forEach { item ->
                    ElButton(
                        onClick = { position = item },
                        text = item.name,
                        size = ElSize.Small,
                        type = if (position == item) ElType.Primary else ElType.Default,
                    )
                }
            }
            ElTabs(
                tabs = tabs,
                activeName = active,
                onTabChange = { active = it },
                type = ElTabsType.BorderCard,
                tabPosition = position,
                modifier = Modifier.height(200.dp),
            )
        }
    }
    DemoExample("Closable and addable", "Tabs can be closed and added with the built-in buttons.") {
        val tabs = rememberDocsTabs()
        var current by remember { mutableStateOf("components") }
        ElTabs(
            tabs = tabs,
            activeName = current,
            onTabChange = { current = it },
            closable = true,
            addable = true,
            onTabRemove = { name ->
                tabs.removeAll { it.name == name }
                if (current == name) current = tabs.firstOrNull()?.name ?: ""
            },
            onTabAdd = {
                val name = "tab-${tabs.size + 1}"
                tabs.add(ElTabData(name, "New tab") { BasicText("New tab content") })
                current = name
            },
        )
    }
    DemoExample("Editable", "The editable shortcut makes tabs both closable and addable.") {
        val tabs = rememberDocsTabs()
        var current by remember { mutableStateOf("components") }
        ElTabs(
            tabs = tabs,
            activeName = current,
            onTabChange = { current = it },
            editable = true,
            onTabRemove = { name ->
                tabs.removeAll { it.name == name }
                if (current == name) current = tabs.firstOrNull()?.name ?: ""
            },
            onTabAdd = {
                val name = "tab-${tabs.size + 1}"
                tabs.add(ElTabData(name, "New tab") { BasicText("New tab content") })
                current = name
            },
        )
    }
    DemoExample("Custom tab label", "Replace a tab label with custom content using the label slot.") {
        var active by remember { mutableStateOf("route") }
        ElTabs(
            tabs = listOf(
                ElTabData(
                    name = "route",
                    label = "Route",
                    labelContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ElIcon(ElIcons.Calendar, size = 14.dp, tint = LocalElTheme.current.colors.textRegular)
                            Spacer(Modifier.width(4.dp))
                            BasicText("Route", style = LocalElTheme.current.typography.base)
                        }
                    },
                ) { BasicText("Route panel", style = LocalElTheme.current.typography.base) },
                ElTabData("config", "Config") { BasicText("Config panel", style = LocalElTheme.current.typography.base) },
                ElTabData("role", "Role") { BasicText("Role panel", style = LocalElTheme.current.typography.base) },
                ElTabData("task", "Task") { BasicText("Task panel", style = LocalElTheme.current.typography.base) },
            ),
            activeName = active,
            onTabChange = { active = it },
            type = ElTabsType.BorderCard,
        )
    }
    DemoExample("Custom add button icon", "Replace the add icon with another icon.") {
        val tabs = rememberDocsTabs()
        var current by remember { mutableStateOf("components") }
        ElTabs(
            tabs = tabs,
            activeName = current,
            onTabChange = { current = it },
            addable = true,
            addIcon = ElIcons.Select,
            onTabAdd = {
                val name = "tab-${tabs.size + 1}"
                tabs.add(ElTabData(name, "New tab") { BasicText("New tab content") })
                current = name
            },
        )
    }
    DemoExample("Custom add button slot", "Supply arbitrary content for the add button with the add-icon slot.") {
        val tabs = rememberDocsTabs()
        var current by remember { mutableStateOf("components") }
        ElTabs(
            tabs = tabs,
            activeName = current,
            onTabChange = { current = it },
            addable = true,
            addIconContent = {
                ElIcon(ElIcons.Calendar, size = 16.dp, tint = LocalElTheme.current.colors.primary)
            },
            onTabAdd = {
                val name = "tab-${tabs.size + 1}"
                tabs.add(ElTabData(name, "New tab") { BasicText("New tab content") })
                current = name
            },
        )
    }
    DemoExample("Customized trigger", "Add and remove tabs from outside the tab bar.") {
        val tabs = rememberDocsTabs()
        var current by remember { mutableStateOf("components") }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElButton(
                onClick = {
                    val name = "tab-${tabs.size + 1}"
                    tabs.add(ElTabData(name, "New tab") { BasicText("New tab content") })
                    current = name
                },
                text = "add tab",
                size = ElSize.Small,
            )
            ElTabs(
                tabs = tabs,
                activeName = current,
                onTabChange = { current = it },
                type = ElTabsType.Card,
                closable = true,
                onTabRemove = { name ->
                    val index = tabs.indexOfFirst { it.name == name }
                    tabs.removeAll { it.name == name }
                    if (current == name) {
                        current = tabs.getOrNull(index)?.name ?: tabs.getOrNull(index - 1)?.name ?: ""
                    }
                },
            )
        }
    }
    DemoExample("Default value", "An uncontrolled tab set starts at the default value.") {
        var current by remember { mutableStateOf<String?>(null) }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElTabs(
                tabs = listOf(
                    ElTabData("first", "First") { BasicText("First tab") },
                    ElTabData("second", "Second") { BasicText("Second tab") },
                    ElTabData("third", "Third") { BasicText("Third tab") },
                ),
                activeName = current,
                onTabChange = { current = it },
                defaultValue = "third",
            )
            BasicText(
                "active: ${current ?: "third"}",
                style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.textSecondary),
            )
        }
    }
    DemoExample("Keep pane state", "Inactive panes stay composed, so their state survives switching tabs.") {
        val tabs = remember {
            listOf(
                ElTabData("counter1", "Counter 1") {
                    var count by remember { mutableStateOf(0) }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        BasicText("Count: $count", style = LocalElTheme.current.typography.base)
                        ElButton(onClick = { count++ }, text = "Increment", size = ElSize.Small)
                    }
                },
                ElTabData("counter2", "Counter 2") {
                    var count by remember { mutableStateOf(0) }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        BasicText("Count: $count", style = LocalElTheme.current.typography.base)
                        ElButton(onClick = { count++ }, text = "Increment", size = ElSize.Small)
                    }
                },
            )
        }
        var active by remember { mutableStateOf("counter1") }
        ElTabs(tabs = tabs, activeName = active, onTabChange = { active = it })
    }
    DemoExample("Lazy panes", "With lazy panes the inactive content is disposed and its state resets.") {
        val tabs = remember {
            listOf(
                ElTabData("counter1", "Counter 1") {
                    var count by remember { mutableStateOf(0) }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        BasicText("Count: $count", style = LocalElTheme.current.typography.base)
                        ElButton(onClick = { count++ }, text = "Increment", size = ElSize.Small)
                    }
                },
                ElTabData("counter2", "Counter 2") {
                    var count by remember { mutableStateOf(0) }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        BasicText("Count: $count", style = LocalElTheme.current.typography.base)
                        ElButton(onClick = { count++ }, text = "Increment", size = ElSize.Small)
                    }
                },
            )
        }
        var active by remember { mutableStateOf("counter1") }
        ElTabs(tabs = tabs, activeName = active, onTabChange = { active = it }, lazy = true)
    }
}
