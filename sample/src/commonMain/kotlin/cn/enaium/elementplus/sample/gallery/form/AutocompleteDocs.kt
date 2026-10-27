package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElAutocomplete
import cn.enaium.elementplus.components.ElAutocompleteItems
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay

@Composable
fun AutocompleteDocs() {
    val theme = LocalElTheme.current
    var basic by remember { mutableStateOf("") }
    var remote by remember { mutableStateOf("") }
    val suggestions = listOf("Kotlin", "Kotlin Multiplatform", "Compose Multiplatform", "Ktor", "Kotlinx Coroutines")
    DemoExample("Basic usage", "Suggestions are listed when the input is activated or typed into.") {
        ElAutocomplete(
            value = basic,
            onValueChange = { basic = it },
            fetchSuggestions = { query -> suggestions.filter { it.contains(query, ignoreCase = true) } },
            placeholder = "Type a framework",
            clearable = true,
            modifier = Modifier.width(320.dp),
        )
        DemoHint("Value: $basic")
    }
    DemoExample("List suggestions on input", "triggerOnFocus = false keeps the list closed until the value changes.") {
        var value by remember { mutableStateOf("") }
        ElAutocomplete(
            value = value,
            onValueChange = { value = it },
            fetchSuggestions = { query -> sampleLinks.map { it.value }.filter { it.startsWith(query, ignoreCase = true) } },
            triggerOnFocus = false,
            clearable = true,
            placeholder = "Please input",
            modifier = Modifier.width(320.dp),
        )
        DemoHint("Value: $value")
    }
    DemoExample("Remote search", "asyncFetchSuggestions suspends for the remote lookup and drives the loading indicator.") {
        ElAutocomplete(
            value = remote,
            onValueChange = { remote = it },
            fetchSuggestions = { emptyList() },
            asyncFetchSuggestions = { query ->
                delay(600)
                sampleLinks.map { it.value }.filter { it.startsWith(query, ignoreCase = true) }
            },
            placeholder = "Search anything",
            debounce = 300,
            emptyText = "No matching data",
            clearable = true,
            modifier = Modifier.width(320.dp),
        )
    }
    DemoExample("Custom template", "ElAutocompleteItems renders object suggestions; itemContent receives the whole object.") {
        var value by remember { mutableStateOf("") }
        var selected by remember { mutableStateOf<LinkItem?>(null) }
        ElAutocompleteItems(
            value = value,
            onValueChange = { value = it },
            fetchSuggestions = { query -> filterLinks(query) },
            valueKey = { it.value },
            onSelect = { selected = it },
            placeholder = "Please input",
            clearable = true,
            itemContent = { item ->
                Column {
                    BasicText(
                        text = item.value,
                        style = theme.typography.base.copy(color = theme.colors.textRegular),
                    )
                    BasicText(
                        text = item.link,
                        style = theme.typography.extraSmall.copy(color = theme.colors.textSecondary),
                    )
                }
            },
            modifier = Modifier.width(320.dp),
        )
        DemoHint("Selected: ${selected?.value ?: "-"}")
    }
    DemoExample("Custom loading", "The loading slot overrides the default loading row while an async lookup runs.") {
        var value by remember { mutableStateOf("") }
        ElAutocomplete(
            value = value,
            onValueChange = { value = it },
            fetchSuggestions = { emptyList() },
            asyncFetchSuggestions = { query ->
                delay(1500)
                sampleLinks.map { it.value }.filter { it.startsWith(query, ignoreCase = true) }
            },
            placeholder = "Please input",
            loadingContent = {
                Row(
                    Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ElIcon(ElIcons.Loading, size = 16.dp, tint = theme.colors.primary, spin = true)
                    Spacer(Modifier.width(8.dp))
                    BasicText(
                        text = "Searching the server…",
                        style = theme.typography.small.copy(color = theme.colors.primary),
                    )
                }
            },
            modifier = Modifier.width(320.dp),
        )
    }
    DemoExample("Custom header and footer", "Header and footer slots wrap the suggestion list.") {
        var value by remember { mutableStateOf("") }
        ElAutocomplete(
            value = value,
            onValueChange = { value = it },
            fetchSuggestions = { query -> filterLinks(query).map { it.value } },
            placeholder = "Please input",
            header = {
                BasicText(
                    text = "Popular repositories",
                    style = theme.typography.small.copy(color = theme.colors.textSecondary),
                )
            },
            footer = {
                ElButton(
                    text = "Clear",
                    link = true,
                    size = ElSize.Small,
                    onClick = { value = "" },
                )
            },
            modifier = Modifier.width(320.dp),
        )
        DemoHint("Value: $value")
    }
    DemoExample(
        "Keyboard navigation",
        "Use the up/down arrows to highlight a suggestion, enter to select it and escape to close the list. loopNavigation wraps around and highlightFirstItem preselects the first row.",
    ) {
        var value by remember { mutableStateOf("") }
        var selected by remember { mutableStateOf("") }
        ElAutocomplete(
            value = value,
            onValueChange = { value = it },
            fetchSuggestions = { query -> filterLinks(query).map { it.value } },
            onSelect = { selected = it },
            highlightFirstItem = true,
            loopNavigation = true,
            placeholder = "Arrow keys + enter",
            clearable = true,
            modifier = Modifier.width(320.dp),
        )
        DemoHint("Selected: ${selected.ifBlank { "-" }}")
    }
    DemoExample(
        "Select when unmatched",
        "Enter selects the raw input when no suggestion is highlighted and selectWhenUnmatched is true.",
    ) {
        var value by remember { mutableStateOf("") }
        var selected by remember { mutableStateOf("") }
        ElAutocomplete(
            value = value,
            onValueChange = { value = it },
            fetchSuggestions = { query -> filterLinks(query).map { it.value } },
            onSelect = { selected = it },
            selectWhenUnmatched = true,
            placeholder = "Type anything then press enter",
            modifier = Modifier.width(320.dp),
        )
        DemoHint("Selected: ${selected.ifBlank { "-" }}")
    }
    DemoExample("Prefix and suffix", "Prefix and suffix slots customize the icons inside the input.") {
        var value by remember { mutableStateOf("") }
        var clicks by remember { mutableStateOf(0) }
        ElAutocomplete(
            value = value,
            onValueChange = { value = it },
            fetchSuggestions = { query -> filterLinks(query).map { it.value } },
            placeholder = "Please input",
            prefix = {
                ElIcon(ElIcons.Search, size = 14.dp, tint = theme.colors.primary)
            },
            suffix = {
                ElIcon(
                    ElIcons.Edit,
                    size = 14.dp,
                    tint = theme.colors.primary,
                    contentDescription = "Edit",
                    modifier = Modifier.clickable { clicks++ },
                )
            },
            modifier = Modifier.width(320.dp),
        )
        DemoHint("Suffix clicked $clicks time(s)")
    }
    DemoExample("Disabled", "Disable the autocomplete input.") {
        ElAutocomplete(
            value = "Kotlin",
            onValueChange = {},
            fetchSuggestions = { emptyList() },
            disabled = true,
            modifier = Modifier.width(320.dp),
        )
    }
}

private data class LinkItem(val value: String, val link: String)

private val sampleLinks = listOf(
    LinkItem("vue", "https://github.com/vuejs/vue"),
    LinkItem("element", "https://github.com/ElemeFE/element"),
    LinkItem("cooking", "https://github.com/ElemeFE/cooking"),
    LinkItem("mint-ui", "https://github.com/ElemeFE/mint-ui"),
    LinkItem("vuex", "https://github.com/vuejs/vuex"),
    LinkItem("vue-router", "https://github.com/vuejs/vue-router"),
    LinkItem("babel", "https://github.com/babel/babel"),
)

private fun filterLinks(query: String): List<LinkItem> = sampleLinks.filter {
    query.isBlank() || it.value.startsWith(query, ignoreCase = true)
}
