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
fun CheckboxDocs() {
    val options = listOf(
        ElCheckboxOption("html", "HTML"),
        ElCheckboxOption("css", "CSS"),
        ElCheckboxOption("js", "JavaScript"),
    )
    var buttonSelected by remember { mutableStateOf(listOf("a")) }
    var limited by remember { mutableStateOf(listOf("a")) }
    var indeterminateChecked by remember { mutableStateOf(false) }
    DemoExample("Basic usage", "Use a checkbox group for multiple choices.") {
        var selected by remember { mutableStateOf(listOf("html")) }
        ElCheckboxGroup(options = options, selected = selected, onSelectedChange = { selected = it })
        DemoHint("Selected: ${selected.joinToString()}")
    }
    DemoExample("Button style", "Use the button style for a segmented look.") {
        ElCheckboxGroup(
            options = listOf(
                ElCheckboxOption("a", "Option A"),
                ElCheckboxOption("b", "Option B"),
                ElCheckboxOption("c", "Option C"),
            ),
            selected = buttonSelected,
            onSelectedChange = { buttonSelected = it },
            button = true,
        )
    }
    DemoExample("Disabled", "A disabled option cannot be toggled.") {
        var selected by remember { mutableStateOf(listOf("html")) }
        ElCheckboxGroup(
            options = options + ElCheckboxOption("vue", "Vue", disabled = true),
            selected = selected,
            onSelectedChange = { selected = it },
        )
    }
    DemoExample("Indeterminate", "Indeterminate is useful for a 'select all' state.") {
        DemoRowSpaced {
            ElCheckbox(checked = indeterminateChecked, onCheckedChange = { indeterminateChecked = it }, label = "Select all")
            ElCheckbox(checked = false, onCheckedChange = {}, label = "Indeterminate", indeterminate = true)
        }
    }
    DemoExample("Limitation", "Limit the number of selected options.") {
        ElCheckboxGroup(
            options = options,
            selected = limited,
            onSelectedChange = { limited = it },
            min = 1,
            max = 2,
        )
    }
    DemoExample("With border", "The border variant draws a frame around each checkbox.") {
        var selected by remember { mutableStateOf(listOf("html")) }
        ElCheckboxGroup(options = options, selected = selected, onSelectedChange = { selected = it }, border = true)
    }

    DemoExample("Options attribute", "Drive a checkbox group with an options list and limits.") {
        var selected by remember { mutableStateOf(listOf("html")) }
        ElCheckboxGroup(
            options = options,
            selected = selected,
            onSelectedChange = { selected = it },
            min = 1,
            max = 2,
            border = true,
        )
        DemoHint("Select one or two options: $selected")
    }
}
