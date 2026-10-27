package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElTimeSelect
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import kotlinx.datetime.LocalTime

@Composable
fun TimeSelectDocs() {
    var step by remember { mutableStateOf<LocalTime?>(null) }
    DemoExample("Basic usage", "Choose a time from options generated every 15 minutes.") {
        var basic by remember { mutableStateOf<LocalTime?>(LocalTime(10, 0)) }
        ElTimeSelect(
            value = basic,
            onValueChange = { basic = it },
            start = LocalTime(8, 30),
            step = 15,
            end = LocalTime(18, 30),
            placeholder = "Select time",
            modifier = Modifier.width(240.dp),
        )
    }
    DemoExample("Generated option range", "Set start, end and step to generate options.") {
        ElTimeSelect(
            value = step,
            onValueChange = { step = it },
            start = LocalTime(8, 0),
            end = LocalTime(12, 0),
            step = 15,
            format = "HH:mm",
            modifier = Modifier.width(200.dp),
        )
    }
    DemoExample(
        "Linked start and end times",
        "Link two selectors: the start selector caps at the end time and the end selector starts after the start time.",
    ) {
        var startTime by remember { mutableStateOf<LocalTime?>(null) }
        var endTime by remember { mutableStateOf<LocalTime?>(null) }
        ElTimeSelect(
            value = startTime,
            onValueChange = { startTime = it },
            maxTime = endTime,
            placeholder = "Start time",
            start = LocalTime(8, 30),
            step = 15,
            end = LocalTime(18, 30),
            modifier = Modifier.width(240.dp),
        )
        ElTimeSelect(
            value = endTime,
            onValueChange = { endTime = it },
            minTime = startTime,
            placeholder = "End time",
            start = LocalTime(8, 30),
            step = 15,
            end = LocalTime(18, 30),
            modifier = Modifier.width(240.dp),
        )
        DemoHint("Range: $startTime - $endTime")
    }
    DemoExample("Excluded end time", "Exclude the end value from the generated options.") {
        var value by remember { mutableStateOf<LocalTime?>(null) }
        ElTimeSelect(
            value = value,
            onValueChange = { value = it },
            start = LocalTime(9, 0),
            end = LocalTime(12, 0),
            step = 60,
            includeEndTime = false,
            modifier = Modifier.width(200.dp),
        )
        DemoHint("12:00 is not listed.")
    }
    DemoExample(
        "Editable input",
        "Type a time that exists in the generated options.",
    ) {
        var value by remember { mutableStateOf<LocalTime?>(LocalTime(9, 30)) }
        ElTimeSelect(
            value = value,
            onValueChange = { value = it },
            editable = true,
            start = LocalTime(8, 0),
            end = LocalTime(18, 0),
            step = 30,
            modifier = Modifier.width(200.dp),
        )
        DemoHint("Selected: $value")
    }
    DemoExample("Disabled", "Disable the time select field.") {
        ElTimeSelect(value = LocalTime(10, 0), onValueChange = {}, disabled = true, modifier = Modifier.width(200.dp))
    }

    DemoExample("Time formats", "Customize the fixed option format.") {
        var value by remember { mutableStateOf<LocalTime?>(LocalTime(9, 30)) }
        ElTimeSelect(value = value, onValueChange = { value = it }, format = "HH:mm", step = 60, modifier = Modifier.width(200.dp))
    }
    DemoExample("12-hour format", "Use hh:mm A to render 12-hour options with AM/PM.") {
        var value by remember { mutableStateOf<LocalTime?>(LocalTime(13, 30)) }
        ElTimeSelect(
            value = value,
            onValueChange = { value = it },
            start = LocalTime(0, 0),
            step = 30,
            end = LocalTime(23, 59),
            format = "hh:mm A",
            placeholder = "Select time",
            modifier = Modifier.width(240.dp),
        )
    }
}
