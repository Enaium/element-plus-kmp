package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElCheckbox
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElSelect
import cn.enaium.elementplus.components.ElSelectMultiple
import cn.enaium.elementplus.components.ElSelectOption
import cn.enaium.elementplus.components.ElTag
import cn.enaium.elementplus.components.elSelectOptions
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.sample.gallery.DemoRow
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class SelectAlias(val id: String, val label: String, val disabled: Boolean = false)

private fun basicOptions(): List<ElSelectOption<String>> = listOf("Option1", "Option2", "Option3", "Option4", "Option5")
    .map { ElSelectOption(it, it) }

private val selectStates = listOf(
    "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware",
    "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky",
    "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi",
    "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico",
    "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania",
)

private val colorOptions = listOf(
    ElSelectOption("red", "red"),
    ElSelectOption("orange", "orange"),
    ElSelectOption("yellow", "yellow"),
    ElSelectOption("green", "green"),
    ElSelectOption("cyan", "cyan"),
    ElSelectOption("blue", "blue"),
    ElSelectOption("purple", "purple"),
)

private data class ValueKeyCity(val id: Int, val label: String, val description: String)

@Composable
fun SelectDocs() {
    val colors = LocalElTheme.current.colors
    val theme = LocalElTheme.current

    DemoExample("Basic usage", "Select a single value from a list of options.") {
        var value by remember { mutableStateOf<String?>("Option1") }
        ElSelect(
            value = value,
            onValueChange = { value = it },
            options = basicOptions(),
            placeholder = "Select",
            modifier = Modifier.width(240.dp),
        )
        DemoHint("Selected: ${value ?: "-"}")
    }

    DemoExample("Options attribute", "Use the options attribute and alias mapping for domain objects.") {
        val aliases = remember {
            listOf(
                SelectAlias("Option1", "Option1"),
                SelectAlias("Option2", "Option2", disabled = true),
                SelectAlias("Option3", "Option3"),
                SelectAlias("Option4", "Option4", disabled = true),
                SelectAlias("Option5", "Option5"),
            )
        }
        var value by remember { mutableStateOf<String?>("Option1") }
        ElSelect(
            value = value,
            onValueChange = { value = it },
            options = remember(aliases) {
                elSelectOptions(aliases, { it.id }, { it.label }, { it.disabled })
            },
            placeholder = "Select",
            modifier = Modifier.width(240.dp),
        )
    }

    DemoExample("Disabled option", "Individual options can be disabled.") {
        var value by remember { mutableStateOf<String?>(null) }
        ElSelect(
            value = value,
            onValueChange = { value = it },
            options = listOf(
                ElSelectOption("Option1", "Option1"),
                ElSelectOption("Option2", "Option2", disabled = true),
                ElSelectOption("Option3", "Option3"),
                ElSelectOption("Option4", "Option4"),
                ElSelectOption("Option5", "Option5"),
            ),
            placeholder = "Select",
            modifier = Modifier.width(240.dp),
        )
    }

    DemoExample("Disabled select", "Disable the whole selector.") {
        ElSelect(
            value = "Option1",
            onValueChange = {},
            options = basicOptions(),
            disabled = true,
            modifier = Modifier.width(240.dp),
        )
    }

    DemoExample("Clearable", "Clear the current selection.") {
        var value by remember { mutableStateOf<String?>("Option1") }
        var cleared by remember { mutableStateOf(false) }
        ElSelect(
            value = value,
            onValueChange = { value = it; cleared = false },
            options = basicOptions(),
            clearable = true,
            placeholder = "Select",
            onClear = { cleared = true },
            modifier = Modifier.width(240.dp),
        )
        DemoHint(if (cleared) "Cleared" else "Selected: ${value ?: "-"}")
    }

    DemoExample("Sizes", "Large, default and small select sizes.") {
        var large by remember { mutableStateOf<String?>("Option1") }
        var normal by remember { mutableStateOf<String?>("Option1") }
        var small by remember { mutableStateOf<String?>("Option1") }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElSelect(large, { large = it }, basicOptions(), size = ElSize.Large, modifier = Modifier.width(240.dp))
            ElSelect(normal, { normal = it }, basicOptions(), modifier = Modifier.width(240.dp))
            ElSelect(small, { small = it }, basicOptions(), size = ElSize.Small, modifier = Modifier.width(240.dp))
        }
    }

    DemoExample("Basic multiple select", "Select multiple values and show them as closable tags.") {
        var values by remember { mutableStateOf(listOf("Option1", "Option2")) }
        ElSelectMultiple(
            values = values,
            onValueChange = { values = it },
            options = basicOptions(),
            placeholder = "Select",
            modifier = Modifier.width(320.dp),
        )
        DemoHint("Selected: ${values.joinToString()}")
    }

    DemoExample("Custom template", "Customize each option row.") {
        var value by remember { mutableStateOf<String?>(null) }
        ElSelect(
            value = value,
            onValueChange = { value = it },
            options = basicOptions(),
            optionContent = { option, _ ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    BasicText(option.label, style = theme.typography.base.copy(color = colors.textRegular))
                    BasicText(option.value, style = theme.typography.small.copy(color = colors.textSecondary))
                }
            },
            placeholder = "Select",
            modifier = Modifier.width(240.dp),
        )
    }

    DemoExample("Header of the dropdown", "Use a header slot to select all options.") {
        var values by remember { mutableStateOf(listOf("Option1")) }
        val options = basicOptions()
        val allSelected = values.size == options.size
        val indeterminate = values.isNotEmpty() && !allSelected
        ElSelectMultiple(
            values = values,
            onValueChange = { values = it },
            options = options,
            clearable = true,
            collapseTags = true,
            maxCollapseTags = 1,
            header = {
                ElCheckbox(
                    checked = allSelected,
                    indeterminate = indeterminate,
                    onCheckedChange = { checked ->
                        values = if (checked) options.map { it.value } else emptyList()
                    },
                    label = "All",
                )
            },
            placeholder = "Select",
            modifier = Modifier.width(240.dp),
        )
    }

    DemoExample("Footer of the dropdown", "Add options from a footer slot.") {
        var options by remember { mutableStateOf(basicOptions()) }
        var value by remember { mutableStateOf<String?>("Option1") }
        var adding by remember { mutableStateOf(false) }
        var optionName by remember { mutableStateOf("") }
        ElSelect(
            value = value,
            onValueChange = { value = it },
            options = options,
            placeholder = "Select",
            footer = {
                if (!adding) {
                    ElButton(
                        text = "Add an option",
                        textButton = true,
                        size = ElSize.Small,
                        bg = true,
                        onClick = { adding = true },
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ElInput(
                            value = optionName,
                            onValueChange = { optionName = it },
                            placeholder = "input option name",
                            size = ElSize.Small,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ElButton(
                                text = "confirm",
                                type = ElType.Primary,
                                size = ElSize.Small,
                                onClick = {
                                    if (optionName.isNotBlank()) {
                                        options = options + ElSelectOption(optionName, optionName)
                                        optionName = ""
                                        adding = false
                                    }
                                },
                            )
                            ElButton(text = "cancel", size = ElSize.Small, onClick = {
                                optionName = ""
                                adding = false
                            })
                        }
                    }
                }
            },
            modifier = Modifier.width(240.dp),
        )
    }

    DemoExample("Grouping", "Group options with group labels.") {
        var value by remember { mutableStateOf<String?>(null) }
        ElSelect(
            value = value,
            onValueChange = { value = it },
            options = listOf(
                ElSelectOption("Shanghai", "Shanghai", group = "Popular cities"),
                ElSelectOption("Beijing", "Beijing", group = "Popular cities"),
                ElSelectOption("Chengdu", "Chengdu", group = "City name"),
                ElSelectOption("Shenzhen", "Shenzhen", group = "City name"),
                ElSelectOption("Guangzhou", "Guangzhou", group = "City name"),
                ElSelectOption("Dalian", "Dalian", group = "City name"),
            ),
            placeholder = "Select",
            modifier = Modifier.width(240.dp),
        )
    }

    DemoExample("Option filtering", "Filter options from the dropdown search field.") {
        var value by remember { mutableStateOf<String?>(null) }
        ElSelect(
            value = value,
            onValueChange = { value = it },
            options = basicOptions(),
            filterable = true,
            placeholder = "Select",
            modifier = Modifier.width(240.dp),
        )
    }

    DemoExample("Remote Search", "Fetch options when the query changes.") {
        val scope = rememberCoroutineScope()
        var options by remember { mutableStateOf(emptyList<ElSelectOption<String>>()) }
        var values by remember { mutableStateOf(emptyList<String>()) }
        var loading by remember { mutableStateOf(false) }
        fun remote(query: String) {
            scope.launch {
                if (query.isBlank()) {
                    options = emptyList()
                    loading = false
                } else {
                    loading = true
                    delay(250)
                    options = selectStates.filter { it.contains(query, ignoreCase = true) }
                        .map { ElSelectOption("value:$it", "label:$it") }
                    loading = false
                }
            }
        }
        ElSelectMultiple(
            values = values,
            onValueChange = { values = it },
            options = options,
            filterable = true,
            remote = true,
            remoteMethod = ::remote,
            loading = loading,
            reserveKeyword = true,
            clearable = true,
            placeholder = "Please enter a keyword",
            modifier = Modifier.width(260.dp),
        )
        DemoHint("Type in the dropdown search field.")
    }

    DemoExample("Create new items", "Type a value and press Enter to create it.") {
        var values by remember { mutableStateOf(emptyList<String>()) }
        ElSelectMultiple(
            values = values,
            onValueChange = { values = it },
            options = listOf(
                ElSelectOption("HTML", "HTML"),
                ElSelectOption("CSS", "CSS"),
                ElSelectOption("JavaScript", "JavaScript"),
            ),
            filterable = true,
            allowCreate = true,
            defaultFirstOption = true,
            reserveKeyword = false,
            onCreateOption = { ElSelectOption(it, it) },
            placeholder = "Choose tags for your article",
            modifier = Modifier.width(280.dp),
        )
        DemoHint("Type and press Enter to create. Selected: ${values.joinToString()}")
    }

    DemoExample("Use value-key attribute", "Object options are compared by a caller-provided key.") {
        val options = remember {
            listOf(
                ValueKeyCity(1, "Option A", "Option A - 230506"),
                ValueKeyCity(2, "Option B", "Option B - 230506"),
                ValueKeyCity(3, "Option C", "Option C - 230506"),
                ValueKeyCity(4, "Option A", "Option A - 230507"),
            )
        }
        var value by remember { mutableStateOf<ValueKeyCity?>(options[0]) }
        ElSelect(
            value = value,
            onValueChange = { value = it },
            options = options.map { ElSelectOption(it, it.label) },
            valueKey = { it.id },
            placeholder = "Select",
            modifier = Modifier.width(260.dp),
        )
        DemoHint("Selected description: ${value?.description ?: "no select"}")
    }

    DemoExample("Custom Tag", "Replace the default selected tags.") {
        var values by remember { mutableStateOf(colorOptions.map { it.value }) }
        ElSelectMultiple(
            values = values,
            onValueChange = { values = it },
            options = colorOptions,
            tagContent = { option ->
                ElTag(
                    text = option.label,
                    type = ElType.Success,
                    size = ElSize.Small,
                )
            },
            placeholder = "Select colors",
            modifier = Modifier.width(280.dp),
        )
    }

    DemoExample("Custom Loading", "Provide a custom dropdown loading state.") {
        var loading by remember { mutableStateOf(true) }
        ElSelect(
            value = null,
            onValueChange = {},
            options = emptyList(),
            loading = loading,
            loadingContent = {
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ElIcon(ElIcons.Loading, size = 20.dp, tint = colors.primary, spin = true)
                    BasicText(
                        text = "Loading states...",
                        modifier = Modifier.padding(start = 8.dp),
                        style = theme.typography.small.copy(color = colors.textSecondary),
                    )
                }
            },
            placeholder = "Open the loading dropdown",
            modifier = Modifier.width(260.dp),
        )
        DemoRow { ElButton(text = "Toggle loading", onClick = { loading = !loading }) }
    }

    DemoExample("Empty Values", "Distinguish an empty option from the clear value.") {
        val options = listOf(ElSelectOption("", "All")) + basicOptions()
        var value by remember { mutableStateOf<String?>("") }
        var cleared by remember { mutableStateOf(false) }
        ElSelect(
            value = value,
            onValueChange = { value = it },
            options = options,
            emptyValues = listOf(null),
            valueOnClear = null,
            clearable = true,
            onClear = { cleared = true },
            placeholder = "Select",
            modifier = Modifier.width(240.dp),
        )
        DemoHint(if (cleared) "Clear value: null" else "Selected: ${value?.ifBlank { "All" } ?: "-"}")
    }

    DemoExample("Custom Label", "Render the selected label with a custom template.") {
        val options = listOf(
            ElSelectOption("Option1", "Label1"),
            ElSelectOption("Option2", "Label2"),
            ElSelectOption("Option3", "Label3"),
        )
        var single by remember { mutableStateOf<String?>("Option1") }
        var multiple by remember { mutableStateOf(listOf("Option1")) }
        val labelContent: @Composable (String, String, ElSelectOption<String>) -> Unit = { label, value, _ ->
            BasicText(
                text = "$label: $value",
                style = theme.typography.base.copy(color = colors.textRegular),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElSelect(
                value = single,
                onValueChange = { single = it },
                options = options,
                clearable = true,
                labelContent = labelContent,
                placeholder = "Select",
                modifier = Modifier.width(240.dp),
            )
            ElSelectMultiple(
                values = multiple,
                onValueChange = { multiple = it },
                options = options,
                clearable = true,
                labelContent = labelContent,
                placeholder = "Select",
                modifier = Modifier.width(280.dp),
            )
        }
    }
}
