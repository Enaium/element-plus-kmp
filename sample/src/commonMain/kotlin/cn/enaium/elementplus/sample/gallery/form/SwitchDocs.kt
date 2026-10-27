package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElSwitch
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay

@Composable
fun SwitchDocs() {
    var prompt by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    DemoExample("Basic usage", "Toggle a boolean value.") {
        var basic by remember { mutableStateOf(true) }
        ElSwitch(checked = basic, onCheckedChange = { basic = it })
        DemoHint("Checked: $basic")
    }
    DemoExample("Text description", "Show labels beside or inside the switch.") {
        var described by remember { mutableStateOf(true) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElSwitch(checked = described, onCheckedChange = { described = it }, activeText = "On", inactiveText = "Off")
            ElSwitch(checked = prompt, onCheckedChange = { prompt = it }, activeText = "On", inactiveText = "Off", inlinePrompt = true)
        }
    }
    DemoExample("Sizes", "Switches are available in three sizes.") {
        var large by remember { mutableStateOf(true) }
        var normal by remember { mutableStateOf(true) }
        var small by remember { mutableStateOf(true) }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ElSwitch(checked = large, onCheckedChange = { large = it }, size = ElSize.Large)
            ElSwitch(checked = normal, onCheckedChange = { normal = it }, size = ElSize.Default)
            ElSwitch(checked = small, onCheckedChange = { small = it }, size = ElSize.Small)
        }
    }
    DemoExample(
        "Custom icons",
        "Replace the label content with active-icon and inactive-icon.",
    ) {
        var plain by remember { mutableStateOf(true) }
        var inline by remember { mutableStateOf(true) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElSwitch(
                checked = plain,
                onCheckedChange = { plain = it },
                activeIcon = ElIcons.Check,
                inactiveIcon = ElIcons.Close,
            )
            ElSwitch(
                checked = inline,
                onCheckedChange = { inline = it },
                inlinePrompt = true,
                activeIcon = ElIcons.Check,
                inactiveIcon = ElIcons.Close,
            )
        }
    }
    DemoExample(
        "Custom action icons",
        "active-action-icon and inactive-action-icon render inside the thumb.",
    ) {
        var value by remember { mutableStateOf(true) }
        ElSwitch(
            checked = value,
            onCheckedChange = { value = it },
            activeActionIcon = ElIcons.View,
            inactiveActionIcon = ElIcons.Hide,
        )
    }
    DemoExample(
        "Custom action slot",
        "Compose arbitrary content inside the thumb with the action slots.",
    ) {
        var value by remember { mutableStateOf(true) }
        val colors = LocalElTheme.current.colors
        ElSwitch(
            checked = value,
            onCheckedChange = { value = it },
            activeAction = {
                BasicText(
                    text = "T",
                    style = LocalElTheme.current.typography.extraSmall.copy(color = colors.primary),
                )
            },
            inactiveAction = {
                BasicText(
                    text = "F",
                    style = LocalElTheme.current.typography.extraSmall.copy(color = colors.info),
                )
            },
        )
    }
    DemoExample(
        "Extended value types",
        "Bind the switch to non-boolean values such as strings or numbers.",
    ) {
        var value by remember { mutableStateOf("100") }
        ElSwitch(
            value = value,
            onValueChange = { value = it },
            activeValue = "100",
            inactiveValue = "0",
            activeText = "100",
            inactiveText = "0",
            inlinePrompt = true,
        )
        DemoHint("Switch value: $value")
    }
    DemoExample("Loading and disabled", "Prevent changes while loading or disabled.") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ElSwitch(checked = loading, onCheckedChange = { loading = it }, loading = true)
            ElSwitch(checked = true, onCheckedChange = {}, disabled = true)
        }
    }
    DemoExample(
        "Prevent switching",
        "An async before-change guard runs while the thumb shows a spinner; returning false keeps the value.",
    ) {
        var allowed by remember { mutableStateOf(false) }
        var accepted by remember { mutableStateOf(false) }
        var rejected by remember { mutableStateOf(false) }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ElSwitch(
                checked = accepted,
                onCheckedChange = { accepted = it },
                beforeChange = {
                    delay(800)
                    allowed = true
                    true
                },
            )
            ElSwitch(
                checked = rejected,
                onCheckedChange = { rejected = it },
                beforeChange = {
                    delay(800)
                    allowed = false
                    false
                },
            )
        }
        DemoHint("Guard result: " + if (allowed) "success" else "failure")
    }
    DemoExample("Custom colors", "Override the active and inactive track colors.") {
        var colored by remember { mutableStateOf(true) }
        val colors = LocalElTheme.current.colors
        ElSwitch(
            checked = colored,
            onCheckedChange = { colored = it },
            activeColor = colors.success,
            inactiveColor = colors.warning,
        )
    }
}
