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
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
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
fun InputTagDocs() {
    var limited by remember { mutableStateOf(listOf("One")) }
    var delimiter by remember { mutableStateOf(emptyList<String>()) }
    DemoExample("Basic usage", "Enter text and press Enter to create a tag.") {
        var basic by remember { mutableStateOf(listOf("Kotlin", "Compose")) }
        ElInputTag(values = basic, onValueChange = { basic = it }, placeholder = "Add tag")
        DemoHint("Tags: ${basic.joinToString()}")
    }
    DemoExample("Max", "Limit the number of tags.") {
        ElInputTag(values = limited, onValueChange = { limited = it }, max = 3, placeholder = "Up to 3 tags")
    }
    DemoExample("Delimiter", "A delimiter creates a tag automatically.") {
        ElInputTag(
            values = delimiter,
            onValueChange = { delimiter = it },
            delimiter = ",",
            placeholder = "Type a,b,c",
            modifier = Modifier.width(360.dp),
        )
    }
    DemoExample("Sizes and disabled", "Tag inputs follow the size scale.") {
        var large by remember { mutableStateOf(listOf("Kotlin")) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElInputTag(values = large, onValueChange = { large = it }, size = ElSize.Large)
            ElInputTag(values = listOf("Compose"), onValueChange = {}, disabled = true)
        }
    }

    DemoExample("Space trigger", "Create tags by pressing Space.") {
        var values by remember { mutableStateOf(listOf("Kotlin")) }
        ElInputTag(
            values = values,
            onValueChange = { values = it },
            trigger = ElInputTagTrigger.Space,
            placeholder = "Type and press Space",
            modifier = Modifier.width(360.dp),
        )
    }

    DemoExample("Clearable and sizes", "Tags can be cleared and follow the size scale.") {
        var values by remember { mutableStateOf(listOf("Alpha", "Beta")) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElInputTag(values = values, onValueChange = { values = it }, size = ElSize.Large, clearable = true, modifier = Modifier.width(360.dp))
            ElInputTag(values = values, onValueChange = { values = it }, size = ElSize.Small, clearable = true, modifier = Modifier.width(360.dp))
        }
    }

    DemoExample("Custom clear icon", "Replace the clear icon with the clearIcon slot.") {
        var values by remember { mutableStateOf(listOf("custom", "clear", "icon")) }
        ElInputTag(
            values = values,
            onValueChange = { values = it },
            clearable = true,
            clearIcon = {
                ElIcon(
                    ElIcons.CloseBold,
                    size = 14.dp,
                    tint = LocalElTheme.current.colors.textPlaceholder,
                )
            },
            placeholder = "Custom clear icon",
            modifier = Modifier.width(360.dp),
        )
    }

    DemoExample("Collapse tags", "Hide overflowing tags behind a + N chip, optionally with a tooltip.") {
        var collapsed by remember { mutableStateOf(listOf("tag1", "tag2", "tag3", "tag4", "tag5")) }
        var withTooltip by remember { mutableStateOf(listOf("tag1", "tag2", "tag3", "tag4", "tag5")) }
        var maxCollapsed by remember { mutableStateOf(listOf("tag1", "tag2", "tag3", "tag4", "tag5")) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElInputTag(
                values = collapsed,
                onValueChange = { collapsed = it },
                collapseTags = true,
                placeholder = "collapseTags",
                modifier = Modifier.width(420.dp),
            )
            ElInputTag(
                values = withTooltip,
                onValueChange = { withTooltip = it },
                collapseTags = true,
                collapseTagsTooltip = true,
                placeholder = "collapseTagsTooltip",
                modifier = Modifier.width(420.dp),
            )
            ElInputTag(
                values = maxCollapsed,
                onValueChange = { maxCollapsed = it },
                collapseTags = true,
                collapseTagsTooltip = true,
                maxCollapseTags = 3,
                placeholder = "maxCollapseTags = 3",
                modifier = Modifier.width(420.dp),
            )
        }
    }

    DemoExample("Draggable", "Long-press a tag and drag it to reorder the list.") {
        var values by remember { mutableStateOf(listOf("tag1", "tag2", "tag3")) }
        ElInputTag(
            values = values,
            onValueChange = { values = it },
            draggable = true,
            onDragTag = { values = it },
            placeholder = "Please input",
            modifier = Modifier.width(420.dp),
        )
        DemoHint("Order: ${values.joinToString()}")
    }

    DemoExample("Prefix and suffix", "Add content before the tags and after the input.") {
        var values by remember { mutableStateOf(emptyList<String>()) }
        ElInputTag(
            values = values,
            onValueChange = { values = it },
            clearable = true,
            prefix = {
                ElIcon(
                    ElIcons.Plus,
                    size = 14.dp,
                    tint = LocalElTheme.current.colors.textPlaceholder,
                )
            },
            suffix = {
                ElIcon(
                    ElIcons.Search,
                    size = 14.dp,
                    tint = LocalElTheme.current.colors.textPlaceholder,
                )
            },
            placeholder = "Please input",
            modifier = Modifier.width(420.dp),
        )
    }

    DemoExample("Custom tag content", "Override the tag body and pick a tag type and effect.") {
        var tagType by remember { mutableStateOf("primary") }
        var tagEffect by remember { mutableStateOf("plain") }
        var values by remember { mutableStateOf(listOf("tag1", "tag2", "tag3")) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DemoRowSpaced {
                ElSegmented(
                    options = listOf(
                        ElSegmentedOption("primary", "primary"),
                        ElSegmentedOption("success", "success"),
                        ElSegmentedOption("info", "info"),
                        ElSegmentedOption("warning", "warning"),
                        ElSegmentedOption("danger", "danger"),
                    ),
                    value = tagType,
                    onValueChange = { tagType = it },
                )
                ElSegmented(
                    options = listOf(
                        ElSegmentedOption("light", "light"),
                        ElSegmentedOption("dark", "dark"),
                        ElSegmentedOption("plain", "plain"),
                    ),
                    value = tagEffect,
                    onValueChange = { tagEffect = it },
                )
            }
            ElInputTag(
                values = values,
                onValueChange = { values = it },
                tagType = tagTypeOf(tagType),
                tagEffect = when (tagEffect) {
                    "dark" -> ElTagEffect.Dark
                    "plain" -> ElTagEffect.Plain
                    else -> ElTagEffect.Light
                },
                tag = { value, _ ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ElIcon(ElIcons.Plus, size = 12.dp)
                        BasicText(value, style = LocalElTheme.current.typography.extraSmall)
                    }
                },
                placeholder = "Please input",
                modifier = Modifier.width(420.dp),
            )
        }
    }
}

private fun tagTypeOf(value: String): ElType = when (value) {
    "success" -> ElType.Success
    "info" -> ElType.Info
    "warning" -> ElType.Warning
    "danger" -> ElType.Danger
    else -> ElType.Primary
}
