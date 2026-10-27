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
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElCheckbox
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElSelectOption
import cn.enaium.elementplus.components.ElTag
import cn.enaium.elementplus.components.ElVirtualizedSelect
import cn.enaium.elementplus.components.ElVirtualizedSelectMultiple
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

private fun virtualOptions(count: Int = 1000): List<ElSelectOption<String>> =
    (1..count).map { ElSelectOption("item-$it", "Item $it") }

private fun groupedVirtualOptions(): List<ElSelectOption<String>> =
    (1..10).flatMap { group ->
        (1..10).map { item ->
            ElSelectOption("group-$group-item-$item", "Group $group / Item $item", group = "Group $group")
        }
    }

private val virtualStates = listOf(
    "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut",
    "Delaware", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa",
    "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan",
)

private data class VirtualAlias(val id: String, val name: String)

private data class VirtualValueKey(val name: String, val test: String)

@Composable
fun VirtualizedSelectDocs() {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val options = remember { virtualOptions(1000) }
    val groupedOptions = remember { groupedVirtualOptions() }

    DemoExample("Basic usage", "A lazy option list with 1000 entries.") {
        var value by remember { mutableStateOf<String?>(null) }
        ElVirtualizedSelect(
            value = value,
            onValueChange = { value = it },
            options = options,
            placeholder = "Please select",
            modifier = Modifier.width(280.dp),
        )
        DemoHint("Selected: ${value ?: "-"}")
    }

    DemoExample("Multi select", "Select several values from a virtualized option list.") {
        var values by remember { mutableStateOf(listOf("item-1", "item-2")) }
        ElVirtualizedSelectMultiple(
            values = values,
            onValueChange = { values = it },
            options = options,
            placeholder = "Please select",
            modifier = Modifier.width(320.dp),
        )
        DemoHint("Selected: ${values.size}")
    }

    DemoExample("Sizes", "Large, default and small virtualized selects.") {
        var value by remember { mutableStateOf<String?>(null) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElVirtualizedSelect(value, { value = it }, options, size = ElSize.Large, modifier = Modifier.width(280.dp))
            ElVirtualizedSelect(value, { value = it }, options, modifier = Modifier.width(280.dp))
            ElVirtualizedSelect(value, { value = it }, options, size = ElSize.Small, modifier = Modifier.width(280.dp))
        }
    }

    DemoExample("Hide extra tags when the selected items are too many", "Collapse tags into a remaining count.") {
        var values by remember { mutableStateOf((1..6).map { "item-$it" }) }
        ElVirtualizedSelectMultiple(
            values = values,
            onValueChange = { values = it },
            options = options,
            collapseTags = true,
            maxCollapseTags = 1,
            placeholder = "Please select",
            modifier = Modifier.width(300.dp),
        )
        DemoHint("Selected: ${values.size}")
    }

    DemoExample("Filterable multi-select", "Filter the virtualized list while multiple selection is enabled.") {
        var values by remember { mutableStateOf(listOf("item-3")) }
        ElVirtualizedSelectMultiple(
            values = values,
            onValueChange = { values = it },
            options = options,
            filterable = true,
            placeholder = "Type to filter",
            modifier = Modifier.width(300.dp),
        )
    }

    DemoExample("Disabled selector and select options", "Disable the field or individual options.") {
        val disabledOptions = remember {
            options.take(100).mapIndexed { index, option -> if (index % 10 == 0) option.copy(disabled = true) else option }
        }
        var values by remember { mutableStateOf(emptyList<String>()) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElVirtualizedSelectMultiple(
                values = values,
                onValueChange = { values = it },
                options = disabledOptions,
                filterable = true,
                placeholder = "Some options are disabled",
                modifier = Modifier.width(300.dp),
            )
            ElVirtualizedSelectMultiple(
                values = values,
                onValueChange = { values = it },
                options = disabledOptions,
                disabled = true,
                placeholder = "Disabled select",
                modifier = Modifier.width(300.dp),
            )
        }
    }

    DemoExample("Option Grouping", "Group headers are rendered in the lazy list.") {
        var value by remember { mutableStateOf<String?>(null) }
        ElVirtualizedSelect(
            value = value,
            onValueChange = { value = it },
            options = groupedOptions,
            filterable = true,
            placeholder = "Grouped options",
            modifier = Modifier.width(300.dp),
        )
    }

    DemoExample("Clearable selector", "Clear a selected value.") {
        var value by remember { mutableStateOf<String?>("item-1") }
        ElVirtualizedSelect(
            value = value,
            onValueChange = { value = it },
            options = options,
            clearable = true,
            placeholder = "Clearable",
            modifier = Modifier.width(280.dp),
        )
        DemoHint("Selected: ${value ?: "-"}")
    }

    DemoExample("Customized option renderer", "Render option metadata in each lazy row.") {
        var value by remember { mutableStateOf<String?>(null) }
        ElVirtualizedSelect(
            value = value,
            onValueChange = { value = it },
            options = options,
            optionContent = { option, _ ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    BasicText(option.label, style = theme.typography.base)
                    BasicText(option.value, style = theme.typography.small.copy(color = colors.textSecondary))
                }
            },
            placeholder = "Custom option",
            modifier = Modifier.width(300.dp),
        )
    }

    DemoExample("Header of the dropdown", "Add a select-all header above the virtualized list.") {
        val headerOptions = remember { options.take(50) }
        var values by remember { mutableStateOf(emptyList<String>()) }
        val allChecked = values.size == headerOptions.size
        ElVirtualizedSelectMultiple(
            values = values,
            onValueChange = { values = it },
            options = headerOptions,
            collapseTags = true,
            maxCollapseTags = 1,
            header = {
                ElCheckbox(
                    checked = allChecked,
                    indeterminate = values.isNotEmpty() && !allChecked,
                    onCheckedChange = { checked ->
                        values = if (checked) headerOptions.map { it.value } else emptyList()
                    },
                    label = "All",
                )
            },
            placeholder = "Select",
            modifier = Modifier.width(300.dp),
        )
    }

    DemoExample("Footer of the dropdown", "Add an option from the dropdown footer.") {
        var footerOptions by remember { mutableStateOf(virtualOptions(100)) }
        var value by remember { mutableStateOf<String?>("item-1") }
        var adding by remember { mutableStateOf(false) }
        var name by remember { mutableStateOf("") }
        ElVirtualizedSelect(
            value = value,
            onValueChange = { value = it },
            options = footerOptions,
            footer = {
                if (!adding) {
                    ElButton(text = "Add an option", textButton = true, bg = true, size = ElSize.Small, onClick = { adding = true })
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ElInput(value = name, onValueChange = { name = it }, placeholder = "input option name", size = ElSize.Small)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ElButton(text = "confirm", type = ElType.Primary, size = ElSize.Small, onClick = {
                                if (name.isNotBlank()) {
                                    footerOptions = footerOptions + ElSelectOption("custom-${footerOptions.size}", name)
                                    name = ""
                                    adding = false
                                }
                            })
                            ElButton(text = "cancel", size = ElSize.Small, onClick = { name = ""; adding = false })
                        }
                    }
                }
            },
            placeholder = "Select",
            modifier = Modifier.width(300.dp),
        )
    }

    DemoExample("Create Option", "Create a new option from the query.") {
        var values by remember { mutableStateOf(emptyList<String>()) }
        ElVirtualizedSelectMultiple(
            values = values,
            onValueChange = { values = it },
            options = options.take(20),
            filterable = true,
            allowCreate = true,
            defaultFirstOption = true,
            onCreateOption = { ElSelectOption(it, it) },
            placeholder = "Type and press Enter",
            modifier = Modifier.width(300.dp),
        )
    }

    DemoExample("Remote search", "Fetch remote options while typing.") {
        val scope = rememberCoroutineScope()
        var remoteOptions by remember { mutableStateOf(emptyList<ElSelectOption<String>>()) }
        var values by remember { mutableStateOf(emptyList<String>()) }
        var loading by remember { mutableStateOf(false) }
        fun remote(query: String) {
            scope.launch {
                if (query.isBlank()) {
                    remoteOptions = emptyList()
                    loading = false
                } else {
                    loading = true
                    delay(250)
                    remoteOptions = virtualStates.filter { it.contains(query, ignoreCase = true) }
                        .map { ElSelectOption("value:$it", "label:$it") }
                    loading = false
                }
            }
        }
        ElVirtualizedSelectMultiple(
            values = values,
            onValueChange = { values = it },
            options = remoteOptions,
            filterable = true,
            remote = true,
            remoteMethod = ::remote,
            loading = loading,
            clearable = true,
            placeholder = "Please enter a keyword",
            modifier = Modifier.width(300.dp),
        )
    }

    DemoExample("Use value-key attribute", "Compare object options by a key.") {
        val valueOptions = remember {
            (1..1000).map { index ->
                ElSelectOption(VirtualValueKey("Option $index", "test ${index % 3}"), "Option $index")
            }
        }
        var value by remember { mutableStateOf<VirtualValueKey?>(VirtualValueKey("Option 1", "test 0")) }
        ElVirtualizedSelect(
            value = value,
            onValueChange = { value = it },
            options = valueOptions,
            valueKey = { it.name },
            placeholder = "Select",
            modifier = Modifier.width(280.dp),
        )
        DemoHint("Selected: ${value?.name ?: "-"} (${value?.test ?: "-"})")
    }

    DemoExample("Aliases for custom options", "Map domain aliases into the options attribute.") {
        val aliases = remember { (1..1000).map { VirtualAlias("id-$it", "Alias $it") } }
        var value by remember { mutableStateOf<String?>(null) }
        ElVirtualizedSelect(
            value = value,
            onValueChange = { value = it },
            options = remember(aliases) { elSelectOptions(aliases, { it.id }, { it.name }) },
            filterable = true,
            placeholder = "Select",
            modifier = Modifier.width(280.dp),
        )
    }

    DemoExample("Custom Tag", "Replace selected tags with custom content.") {
        val tagOptions = remember {
            listOf("red", "orange", "yellow", "green", "cyan", "blue", "purple")
                .map { ElSelectOption(it, it) }
        }
        var values by remember { mutableStateOf(tagOptions.map { it.value }) }
        ElVirtualizedSelectMultiple(
            values = values,
            onValueChange = { values = it },
            options = tagOptions,
            tagContent = { option -> ElTag(option.label, type = ElType.Success, size = ElSize.Small) },
            placeholder = "Select colors",
            modifier = Modifier.width(300.dp),
        )
    }

    DemoExample("Custom Loading", "Provide custom loading content.") {
        var loading by remember { mutableStateOf(true) }
        ElVirtualizedSelect(
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
                    BasicText("Loading states...", modifier = Modifier.padding(start = 8.dp), style = theme.typography.small)
                }
            },
            placeholder = "Open for custom loading",
            modifier = Modifier.width(300.dp),
        )
        DemoRow { ElButton(text = "Toggle loading", onClick = { loading = !loading }) }
    }

    DemoExample("Empty Values", "Keep an empty-string option while clearing to null.") {
        val emptyOptions = remember { listOf(ElSelectOption("", "All")) + options.take(100) }
        var value by remember { mutableStateOf<String?>("") }
        var cleared by remember { mutableStateOf(false) }
        ElVirtualizedSelect(
            value = value,
            onValueChange = { value = it },
            options = emptyOptions,
            emptyValues = listOf(null),
            valueOnClear = null,
            clearable = true,
            onClear = { cleared = true },
            placeholder = "Select",
            modifier = Modifier.width(280.dp),
        )
        DemoHint(if (cleared) "Clear value: null" else "Selected: ${value?.ifBlank { "All" } ?: "-"}")
    }

    DemoExample("Custom Label", "Render selected labels with a custom template.") {
        val labelOptions = remember { options.take(20) }
        var single by remember { mutableStateOf<String?>("item-1") }
        var multiple by remember { mutableStateOf(listOf("item-1")) }
        val labelContent: @Composable (String, String, ElSelectOption<String>) -> Unit = { label, value, _ ->
            BasicText("$label: $value", style = theme.typography.base.copy(color = colors.textRegular))
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElVirtualizedSelect(
                value = single,
                onValueChange = { single = it },
                options = labelOptions,
                clearable = true,
                labelContent = labelContent,
                placeholder = "Select",
                modifier = Modifier.width(280.dp),
            )
            ElVirtualizedSelectMultiple(
                values = multiple,
                onValueChange = { multiple = it },
                options = labelOptions,
                clearable = true,
                labelContent = labelContent,
                placeholder = "Select",
                modifier = Modifier.width(320.dp),
            )
        }
    }

    DemoExample("Custom Width", "Control dropdown width independently of the field.") {
        var value by remember { mutableStateOf<String?>(null) }
        val longOptions = remember {
            (1..1000).map { index -> ElSelectOption("item-$index", "Option $index ${"-".repeat((index % 20) + 1)}") }
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElVirtualizedSelect(
                value = value,
                onValueChange = { value = it },
                options = longOptions,
                fitInputWidth = false,
                placeholder = "Dropdown width: default",
                modifier = Modifier.width(240.dp),
            )
            ElVirtualizedSelect(
                value = value,
                onValueChange = { value = it },
                options = longOptions,
                fitInputWidth = true,
                placeholder = "Dropdown width: fit input",
                modifier = Modifier.width(240.dp),
            )
            ElVirtualizedSelect(
                value = value,
                onValueChange = { value = it },
                options = longOptions,
                dropdownWidth = 440.dp,
                placeholder = "Dropdown width: 440dp",
                modifier = Modifier.width(240.dp),
            )
        }
    }
}
