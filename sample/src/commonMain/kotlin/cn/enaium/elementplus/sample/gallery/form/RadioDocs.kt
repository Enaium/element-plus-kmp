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
    DemoExample("Radio button", "Use the button style for a compact group.") {
        ElRadioGroup(options = options, selected = button, onSelectedChange = { button = it }, button = true)
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
}
