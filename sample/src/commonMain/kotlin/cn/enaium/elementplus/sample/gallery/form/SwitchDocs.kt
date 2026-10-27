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
    DemoExample("Loading and disabled", "Prevent changes while loading or disabled.") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ElSwitch(checked = loading, onCheckedChange = { loading = it }, loading = true)
            ElSwitch(checked = true, onCheckedChange = {}, disabled = true)
        }
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

    DemoExample("Prevent switching", "Reject changes by not updating the controlled value.") {
        ElSwitch(checked = true, onCheckedChange = { })
        DemoHint("The switch stays on because the controlled value is never updated.")
    }

    DemoExample("Disabled and loading", "Switch supports disabled and loading states.") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ElSwitch(checked = true, onCheckedChange = {}, disabled = true)
            ElSwitch(checked = true, onCheckedChange = {}, loading = true)
        }
    }
}
