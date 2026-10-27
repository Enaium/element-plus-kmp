package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElTimePicker
import cn.enaium.elementplus.components.ElTimeRangePicker
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import kotlinx.datetime.LocalTime

@Composable
fun TimePickerDocs() {
    var noSeconds by remember { mutableStateOf<LocalTime?>(LocalTime(14, 15)) }
    DemoExample("Basic usage", "Pick a time from the spinner panel.") {
        var basic by remember { mutableStateOf<LocalTime?>(LocalTime(9, 30)) }
        ElTimePicker(value = basic, onValueChange = { basic = it }, modifier = Modifier.width(200.dp))
        DemoHint("Selected: $basic")
    }
    DemoExample("No seconds", "Hide the seconds column.") {
        ElTimePicker(
            value = noSeconds,
            onValueChange = { noSeconds = it },
            showSeconds = false,
            format = "HH:mm",
            modifier = Modifier.width(180.dp),
        )
    }
    DemoExample(
        "Disabled hours, minutes and seconds",
        "Disable individual spinner entries; unavailable values are greyed out.",
    ) {
        var value by remember { mutableStateOf<LocalTime?>(LocalTime(18, 30)) }
        ElTimePicker(
            value = value,
            onValueChange = { value = it },
            disabledHours = { hour -> hour <= 16 || hour >= 19 },
            disabledMinutes = { hour, minute ->
                when (hour) {
                    17 -> minute <= 29
                    18 -> minute >= 31
                    else -> false
                }
            },
            disabledSeconds = { hour, minute, second ->
                hour == 18 && minute == 30 && second >= 1
            },
            modifier = Modifier.width(200.dp),
        )
        DemoHint("Selected: $value")
    }
    DemoExample(
        "Time range",
        "Pick a start and an end time from a two-panel range picker.",
    ) {
        var start by remember { mutableStateOf<LocalTime?>(LocalTime(8, 40)) }
        var end by remember { mutableStateOf<LocalTime?>(LocalTime(9, 40)) }
        ElTimeRangePicker(
            start = start,
            end = end,
            onValueChange = { nextStart, nextEnd ->
                start = nextStart
                end = nextEnd
            },
            rangeSeparator = "To",
            startPlaceholder = "Start time",
            endPlaceholder = "End time",
            modifier = Modifier.width(300.dp),
        )
        DemoHint("Range: $start To $end")
    }
    DemoExample(
        "Range with min and max time",
        "Restrict both panels to a selectable window.",
    ) {
        var start by remember { mutableStateOf<LocalTime?>(LocalTime(9, 0)) }
        var end by remember { mutableStateOf<LocalTime?>(LocalTime(17, 0)) }
        ElTimeRangePicker(
            start = start,
            end = end,
            onValueChange = { nextStart, nextEnd ->
                start = nextStart
                end = nextEnd
            },
            minTime = LocalTime(8, 0),
            maxTime = LocalTime(18, 0),
            modifier = Modifier.width(300.dp),
        )
        DemoHint("Range: $start - $end (08:00 - 18:00 only)")
    }
    DemoExample(
        "Editable input",
        "Type the time directly; the spinner panel opens on focus.",
    ) {
        var value by remember { mutableStateOf<LocalTime?>(LocalTime(10, 15)) }
        ElTimePicker(
            value = value,
            onValueChange = { value = it },
            editable = true,
            showSeconds = false,
            format = "HH:mm",
            modifier = Modifier.width(200.dp),
        )
        DemoHint("Selected: $value")
    }
    DemoExample("Disabled", "Disable the time picker.") {
        ElTimePicker(value = LocalTime(9, 30), onValueChange = {}, disabled = true, modifier = Modifier.width(200.dp))
    }

    DemoExample("Time formats", "Customize the displayed time format.") {
        var value by remember { mutableStateOf<LocalTime?>(LocalTime(9, 30, 0)) }
        ElTimePicker(value = value, onValueChange = { value = it }, format = "HH:mm", showSeconds = false, modifier = Modifier.width(200.dp))
    }
    DemoExample("12-hour format", "Use hh:mm A to render a 12-hour clock with AM/PM.") {
        var value by remember { mutableStateOf<LocalTime?>(LocalTime(15, 30)) }
        ElTimePicker(
            value = value,
            onValueChange = { value = it },
            format = "hh:mm A",
            showSeconds = false,
            modifier = Modifier.width(200.dp),
        )
        DemoHint("Selected: $value")
    }
}
