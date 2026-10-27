package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import cn.enaium.elementplus.components.ElRadio
import cn.enaium.elementplus.components.ElRadioDirection
import cn.enaium.elementplus.components.ElRadioGroup
import cn.enaium.elementplus.components.ElRadioOption
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElTheme

private data class RadioAliasItem(
    val id: Int,
    val name: String,
    val unable: Boolean = false,
)

@Composable
fun RadioDocs() {
    val options = listOf(
        ElRadioOption("vue", "Vue"),
        ElRadioOption("react", "React"),
        ElRadioOption("svelte", "Svelte"),
    )
    var button by remember { mutableStateOf("vue") }
    DemoExample("Basic usage", "Use a radio group for a single choice.") {
        var basic by remember { mutableStateOf("vue") }
        ElRadioGroup(options = options, selected = basic, onSelectedChange = { basic = it })
        DemoHint("Selected: $basic")
    }
    DemoExample("Group sizes", "The group size applies to every option.") {
        var large by remember { mutableStateOf("vue") }
        var normal by remember { mutableStateOf("vue") }
        var small by remember { mutableStateOf("vue") }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElRadioGroup(options = options, selected = large, onSelectedChange = { large = it }, size = ElSize.Large)
            ElRadioGroup(options = options, selected = normal, onSelectedChange = { normal = it })
            ElRadioGroup(options = options, selected = small, onSelectedChange = { small = it }, size = ElSize.Small)
        }
    }
    DemoExample("Radio button", "Use the button style for a compact group.") {
        ElRadioGroup(options = options, selected = button, onSelectedChange = { button = it }, button = true)
    }
    DemoExample(
        "Radio button colors and sizes",
        "fill and textColor style the checked button; size scales the whole group.",
    ) {
        var colored by remember { mutableStateOf("New York") }
        val cities = listOf(
            ElRadioOption("New York", "New York"),
            ElRadioOption("Washington", "Washington"),
            ElRadioOption("Los Angeles", "Los Angeles"),
            ElRadioOption("Chicago", "Chicago"),
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElRadioGroup(
                options = cities,
                selected = colored,
                onSelectedChange = { colored = it },
                button = true,
                size = ElSize.Large,
            )
            ElRadioGroup(
                options = cities,
                selected = colored,
                onSelectedChange = { colored = it },
                button = true,
                fill = Color(0xFF6C6CFF),
                textColor = Color.White,
            )
            ElRadioGroup(
                options = cities.mapIndexed { index, option ->
                    if (index == 1) option.copy(disabled = true) else option
                },
                selected = colored,
                onSelectedChange = { colored = it },
                button = true,
                size = ElSize.Small,
            )
        }
        DemoHint("Selected: $colored")
    }
    DemoExample("With border", "The border variant frames each option.") {
        var basic by remember { mutableStateOf("vue") }
        ElRadioGroup(options = options, selected = basic, onSelectedChange = { basic = it }, border = true)
    }
    DemoExample("Disabled", "Disable individual options or the entire group.") {
        var basic by remember { mutableStateOf("vue") }
        ElRadioGroup(
            options = options + ElRadioOption("angular", "Angular", disabled = true),
            selected = basic,
            onSelectedChange = { basic = it },
            disabled = false,
        )
    }
    DemoExample(
        "Standalone disabled radios",
        "A standalone radio can be disabled while keeping its checked state visible.",
    ) {
        var value by remember { mutableStateOf("selected and disabled") }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ElRadio(
                checked = value == "disabled",
                onCheckedChange = { if (it) value = "disabled" },
                label = "Option A",
                disabled = true,
            )
            ElRadio(
                checked = value == "selected and disabled",
                onCheckedChange = { if (it) value = "selected and disabled" },
                label = "Option B",
                disabled = true,
            )
        }
        DemoHint("Selected: $value")
    }
    DemoExample("Vertical", "Arrange radio options in a column.") {
        var basic by remember { mutableStateOf("vue") }
        ElRadioGroup(
            options = options,
            selected = basic,
            onSelectedChange = { basic = it },
            direction = ElRadioDirection.Vertical,
        )
    }
    DemoExample("Options attribute", "Drive a radio group with an options list.") {
        var selected by remember { mutableStateOf("a") }
        ElRadioGroup(
            options = listOf(
                ElRadioOption("a", "Option A"),
                ElRadioOption("b", "Option B"),
                ElRadioOption("c", "Option C", disabled = true),
            ),
            selected = selected,
            onSelectedChange = { selected = it },
            border = true,
        )
    }
    DemoExample(
        "Options with aliases",
        "Select value, label and disabled from arbitrary items, the Compose equivalent of the props option.",
    ) {
        var selected by remember { mutableStateOf(3) }
        val items = listOf(
            RadioAliasItem(3, "Option A"),
            RadioAliasItem(6, "Option B"),
            RadioAliasItem(9, "Option C", unable = true),
        )
        ElRadioGroup(
            items = items,
            selected = items.firstOrNull { it.id == selected },
            onSelectedChange = { selected = it.id },
            itemLabel = { it.name },
            itemDisabled = { it.unable },
        )
        DemoHint("Selected id: $selected")
    }
    DemoExample("Numeric values", "Radio groups can be bound to any value type, not just strings.") {
        var selected by remember { mutableStateOf(3) }
        ElRadioGroup(
            items = listOf(3, 6, 9),
            selected = selected,
            onSelectedChange = { selected = it },
            itemLabel = { "Option $it" },
        )
        DemoHint("Selected: $selected")
    }
    DemoExample(
        "Custom option content",
        "optionContent replaces the label while keeping the radio indicator.",
    ) {
        var selected by remember { mutableStateOf("vue") }
        val theme = LocalElTheme.current
        ElRadioGroup(
            options = options,
            selected = selected,
            onSelectedChange = { selected = it },
            optionContent = { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ElIcon(
                        ElIcons.User,
                        size = 14.dp,
                        tint = if (option.value == selected) theme.colors.primary else theme.colors.textSecondary,
                    )
                    BasicText(
                        text = option.label,
                        modifier = Modifier.padding(start = 6.dp),
                        style = theme.typography.base.copy(
                            color = if (option.value == selected) theme.colors.primary else theme.colors.textRegular,
                        ),
                    )
                }
            },
        )
    }
}
