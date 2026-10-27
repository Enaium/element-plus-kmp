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
fun DatePickerDocs() {
    var formatted by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 1, 2)) }
    var month by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 1)) }
    DemoExample("Basic usage", "Pick a date from a popup calendar.") {
        var basic by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        ElDatePicker(value = basic, onValueChange = { basic = it }, placeholder = "Pick a date", modifier = Modifier.width(220.dp))
        DemoHint("Selected: $basic")
    }
    DemoExample("Date formats", "Customize the displayed format.") {
        ElDatePicker(
            value = formatted,
            onValueChange = { formatted = it },
            format = "YYYY/MM/DD",
            modifier = Modifier.width(220.dp),
        )
    }
    DemoExample("Month and year", "Set type to month or year.") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ElDatePicker(value = month, onValueChange = { month = it }, type = ElDatePickerType.Month, format = "YYYY-MM", modifier = Modifier.width(180.dp))
            ElDatePicker(value = month, onValueChange = { month = it }, type = ElDatePickerType.Year, format = "YYYY", modifier = Modifier.width(160.dp))
        }
    }
    DemoExample("Disabled", "The field can be disabled entirely.") {
        ElDatePicker(value = LocalDate(2026, 10, 26), onValueChange = {}, disabled = true, modifier = Modifier.width(220.dp))
    }

    DemoExample("Date range", "Select a start and end date with the range picker.") {
        var start by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 1)) }
        var end by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 8)) }
        ElDateRangePicker(
            start = start,
            end = end,
            onValueChange = { s, e -> start = s; end = e },
            placeholder = "Pick a range",
            modifier = Modifier.width(280.dp),
        )
    }

    DemoExample("Single panel", "Embed the calendar panel directly.") {
        var value by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        ElDatePickerPanel(value = value, onValueChange = { value = it })
    }
}
