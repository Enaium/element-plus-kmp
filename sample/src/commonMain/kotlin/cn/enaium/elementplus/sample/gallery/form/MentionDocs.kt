package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.ElTag
import cn.enaium.elementplus.components.ElTagEffect
import cn.enaium.elementplus.components.ElTreeNode
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.ElMessageType
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber

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
