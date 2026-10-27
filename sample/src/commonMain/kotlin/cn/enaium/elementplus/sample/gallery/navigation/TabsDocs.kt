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
private fun rememberDocsTabs() = remember {
    mutableStateListOf(
        ElTabData("components", "Components") { BasicText("Components panel", style = LocalElTheme.current.typography.base) },
        ElTabData("themes", "Themes") { BasicText("Themes panel", style = LocalElTheme.current.typography.base) },
        ElTabData("about", "About") { BasicText("About panel", style = LocalElTheme.current.typography.base) },
    )
}

@Composable
fun TabsDocs() {
    DemoExample("Basic usage", "Tabs switch between different views.") {
        val tabs = rememberDocsTabs()
        var active by remember { mutableStateOf("components") }
        ElTabs(tabs = tabs, activeName = active, onTabChange = { active = it })
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
    DemoExample("Tab position", "Place tabs on the left side.") {
        val tabs = rememberDocsTabs()
        var active by remember { mutableStateOf("components") }
        ElTabs(
            tabs = tabs,
            activeName = active,
            onTabChange = { active = it },
            type = ElTabsType.BorderCard,
            tabPosition = ElTabPosition.Left,
            modifier = Modifier.height(180.dp),
        )
    }
    DemoExample("Closable and addable", "Tabs can be closed and added dynamically.") {
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

    DemoExample("Default value", "Initialize the active tab to a non-first value.") {
        var active by remember { mutableStateOf("second") }
        ElTabs(
            tabs = listOf(
                ElTabData("first", "First") { BasicText("First tab") },
                ElTabData("second", "Second") { BasicText("Second tab") },
                ElTabData("third", "Third") { BasicText("Third tab") },
            ),
            activeName = active,
            onTabChange = { active = it },
        )
    }
}
