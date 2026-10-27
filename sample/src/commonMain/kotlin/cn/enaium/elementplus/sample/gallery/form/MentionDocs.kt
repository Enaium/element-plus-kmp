package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElAvatar
import cn.enaium.elementplus.components.ElDivider
import cn.enaium.elementplus.components.ElForm
import cn.enaium.elementplus.components.ElFormItem
import cn.enaium.elementplus.components.ElMention
import cn.enaium.elementplus.components.ElMentionOption
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MentionDocs() {
    var value by remember { mutableStateOf("Hello @") }
    val options = listOf(
        ElMentionOption("alice", "Alice"),
        ElMentionOption("bob", "Bob"),
        ElMentionOption("charlie", "Charlie"),
        ElMentionOption("diana", "Diana"),
    )
    DemoExample("Basic usage", "Type @ to open the mention suggestions.") {
        ElMention(
            value = value,
            onValueChange = { value = it },
            options = options,
            placeholder = "Type @ to mention",
            modifier = Modifier.width(380.dp),
        )
    }
    DemoExample("Textarea", "Mention is built on the textarea input.") {
        var text by remember { mutableStateOf("") }
        ElMention(
            value = text,
            onValueChange = { text = it },
            options = options,
            placeholder = "Leave a comment...",
            minLines = 3,
            maxLines = 6,
            modifier = Modifier.width(380.dp),
        )
    }
    DemoExample("Disabled", "Disable the mention textarea.") {
        ElMention(value = "Hello @alice", onValueChange = {}, options = options, disabled = true, modifier = Modifier.width(380.dp))
    }
    DemoExample("Customize trigger token", "Use a different trigger character.") {
        var value by remember { mutableStateOf("") }
        ElMention(
            value = value,
            onValueChange = { value = it },
            options = listOf(
                ElMentionOption("kotlin", "Kotlin"),
                ElMentionOption("compose", "Compose"),
            ),
            trigger = '#',
            placeholder = "Type # to mention",
        )
    }
    DemoExample("Custom option label", "optionContent renders rich labels with an avatar and the option value.") {
        var value by remember { mutableStateOf("") }
        val theme = LocalElTheme.current
        val people = listOf("Fuphoenixes", "kooriookami", "Jeremy", "btea")
            .map { ElMentionOption(it) }
        ElMention(
            value = value,
            onValueChange = { value = it },
            options = people,
            placeholder = "Please input",
            optionContent = { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ElAvatar(size = 24.dp, text = option.label.take(1))
                    BasicText(
                        text = option.label,
                        modifier = Modifier.padding(start = 6.dp),
                        style = theme.typography.base.copy(color = theme.colors.textRegular),
                    )
                }
            },
            modifier = Modifier.width(320.dp),
        )
    }
    DemoExample(
        "Async loading",
        "onSearch loads options remotely while loading renders the loading row.",
    ) {
        var text by remember { mutableStateOf("") }
        var loading by remember { mutableStateOf(false) }
        var remote by remember { mutableStateOf(emptyList<ElMentionOption>()) }
        val scope = rememberCoroutineScope()
        ElMention(
            value = text,
            onValueChange = { text = it },
            options = remote,
            loading = loading,
            onSearch = { pattern, _ ->
                loading = true
                scope.launch {
                    delay(1200)
                    remote = listOf("Fuphoenixes", "kooriookami", "Jeremy", "btea").map {
                        ElMentionOption(pattern + it, pattern + it)
                    }
                    loading = false
                }
            },
            placeholder = "Please input",
            modifier = Modifier.width(320.dp),
        )
        DemoHint("Type @ to load options.")
    }
    DemoExample(
        "Multiple prefixes",
        "prefix accepts several trigger characters and onSearch exposes which one is active.",
    ) {
        var text by remember { mutableStateOf("") }
        var prefixed by remember { mutableStateOf(emptyList<ElMentionOption>()) }
        val people = listOf("Fuphoenixes", "kooriookami", "Jeremy", "btea")
        val tags = listOf("1.0", "2.0", "3.0")
        ElMention(
            value = text,
            onValueChange = { text = it },
            options = prefixed,
            prefix = listOf('@', '#'),
            onSearch = { _, prefix ->
                prefixed = (if (prefix == '@') people else tags).map { ElMentionOption(it) }
            },
            placeholder = "input @ to mention people, # to mention tag",
            modifier = Modifier.width(320.dp),
        )
        DemoHint("Current value: $text")
    }
    DemoExample(
        "Option aliases",
        "Select label, value and disabled from arbitrary items, the Compose equivalent of the props option.",
    ) {
        var text by remember { mutableStateOf("@") }
        val items = listOf(
            mapOf("name" to "Fuphoenixes", "id" to "Fuphoenixes", "unable" to true),
            mapOf("name" to "kooriookami", "id" to "kooriookami", "unable" to false),
            mapOf("name" to "Jeremy", "id" to "Jeremy", "unable" to true),
            mapOf("name" to "btea", "id" to "btea", "unable" to false),
        )
        ElMention(
            value = text,
            onValueChange = { text = it },
            items = items,
            itemLabel = { it["name"] as String },
            itemValue = { it["id"] as String },
            itemDisabled = { it["unable"] == true },
            placeholder = "Please input",
            modifier = Modifier.width(320.dp),
        )
    }
    DemoExample(
        "Whole token deletion",
        "With whole, deleting the separator after a mention removes the entire token; checkIsWhole can decide which tokens are removable.",
    ) {
        var simple by remember { mutableStateOf("") }
        var custom by remember { mutableStateOf("") }
        var customOptions by remember { mutableStateOf(emptyList<ElMentionOption>()) }
        val people = listOf("Fuphoenixes", "kooriookami", "Jeremy", "btea")
            .map { ElMentionOption(it) }
        val tags = listOf("1.0", "2.0", "3.0").map { ElMentionOption(it) }
        ElMention(
            value = simple,
            onValueChange = { simple = it },
            options = people,
            whole = true,
            placeholder = "Mention someone, then backspace the trailing space",
            modifier = Modifier.width(320.dp),
        )
        ElDivider()
        ElMention(
            value = custom,
            onValueChange = { custom = it },
            options = customOptions,
            prefix = listOf('@', '#'),
            onSearch = { _, prefix ->
                customOptions = if (prefix == '@') people else tags
            },
            whole = true,
            checkIsWhole = { pattern, prefix ->
                (if (prefix == '@') people else tags).any { it.value == pattern }
            },
            placeholder = "input @ to mention people, # to mention tag",
            modifier = Modifier.width(320.dp),
        )
        DemoHint("Whole token removal keeps the value consistent while deleting.")
    }
    DemoExample("Work with form", "Place a mention inside a form item.") {
        var value by remember { mutableStateOf("") }
        ElForm(labelWidth = 90.dp) {
            ElFormItem(label = "Mention") {
                ElMention(
                    value = value,
                    onValueChange = { value = it },
                    options = listOf(
                        ElMentionOption("alice", "Alice"),
                        ElMentionOption("bob", "Bob"),
                    ),
                    placeholder = "Type @ to mention",
                    modifier = Modifier.width(420.dp),
                )
            }
        }
    }
}
