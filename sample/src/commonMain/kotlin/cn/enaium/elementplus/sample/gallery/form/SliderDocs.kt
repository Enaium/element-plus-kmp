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
fun SliderDocs() {
    var range by remember { mutableStateOf(20.0..70.0) }
    var steps by remember { mutableStateOf(30.0) }
    DemoExample("Basic usage", "Drag the handle to choose a value.") {
        var basic by remember { mutableStateOf(40.0) }
        ElSlider(value = basic, onValueChange = { basic = it })
        DemoHint("Value: ${basic.toInt()}")
    }
    DemoExample("Range selection", "Use a range for a start and end value.") {
        ElSlider(value = range, onValueChange = { range = it })
        DemoHint("Range: ${range.start.toInt()} - ${range.endInclusive.toInt()}")
    }
    DemoExample("Discrete values", "Set step and show stops to snap to values.") {
        ElSlider(value = steps, onValueChange = { steps = it }, step = 10.0, showStops = true)
    }
    DemoExample("Disabled", "A disabled slider cannot be moved.") {
        ElSlider(value = 50.0, onValueChange = {}, disabled = true)
    }
    DemoExample("Component height", "Change the vertical space reserved for the slider.") {
        var large by remember { mutableStateOf(40.0) }
        var small by remember { mutableStateOf(40.0) }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElSlider(value = large, onValueChange = { large = it }, height = 40.dp)
            ElSlider(value = small, onValueChange = { small = it }, height = 24.dp)
        }
    }

    DemoExample("Show marks", "Show stop marks at each step.") {
        var value by remember { mutableStateOf(40.0) }
        ElSlider(value = value, onValueChange = { value = it }, step = 10.0, showStops = true, modifier = Modifier.width(360.dp))
    }
    DemoExample("Restrict value", "Restrict the slider to a custom range.") {
        var value by remember { mutableStateOf(30.0) }
        ElSlider(value = value, onValueChange = { value = it }, min = 10.0, max = 50.0, step = 5.0, modifier = Modifier.width(360.dp))
        DemoHint("Value: $value")
    }

    DemoExample("Slider with input box", "Pair the slider with an input number.") {
        var value by remember { mutableStateOf(40.0) }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            ElSlider(value = value, onValueChange = { value = it }, modifier = Modifier.weight(1f))
            ElInputNumber(value = value, onValueChange = { value = it ?: 0.0 }, min = 0.0, max = 100.0, width = 100.dp)
        }
    }
}
