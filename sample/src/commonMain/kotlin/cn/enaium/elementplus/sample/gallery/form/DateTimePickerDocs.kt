package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElDateTimePicker
import cn.enaium.elementplus.components.ElDateTimePickerPanel
import cn.enaium.elementplus.components.ElDateTimeRangePicker
import cn.enaium.elementplus.components.ElDateTimeShortcut
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.sample.gallery.DemoRow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Composable
fun DateTimePickerDocs() {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val now = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }
    val singleShortcuts = listOf(
        ElDateTimeShortcut("Today", value = LocalDateTime(today, LocalTime(9, 0))),
        ElDateTimeShortcut("Yesterday", value = LocalDateTime(today.minus(1, DateTimeUnit.DAY), LocalTime(9, 0))),
        ElDateTimeShortcut("A week ago", value = LocalDateTime(today.minus(7, DateTimeUnit.DAY), LocalTime(9, 0))),
    )
    val rangeShortcuts = listOf(
        ElDateTimeShortcut(
            "Last week",
            start = LocalDateTime(today.minus(7, DateTimeUnit.DAY), LocalTime(0, 0)),
            end = LocalDateTime(today, LocalTime(23, 59, 59)),
        ),
        ElDateTimeShortcut(
            "Last month",
            start = LocalDateTime(today.minus(30, DateTimeUnit.DAY), LocalTime(0, 0)),
            end = LocalDateTime(today, LocalTime(23, 59, 59)),
        ),
    )
    var noSeconds by remember { mutableStateOf<LocalDateTime?>(LocalDateTime(2026, 10, 26, 9, 30)) }
    var valueFormatText by remember { mutableStateOf<String?>(null) }

    DemoExample("Basic usage", "Select date and time in one popup.") {
        var value by remember { mutableStateOf<LocalDateTime?>(LocalDateTime(2026, 10, 26, 9, 30)) }
        ElDateTimePicker(value = value, onValueChange = { value = it }, modifier = Modifier.width(300.dp))
        DemoHint("Selected: $value")
    }

    DemoExample("Date and time formats", "`format` is displayed in the field, `value-format` is emitted.") {
        var value by remember { mutableStateOf<LocalDateTime?>(LocalDateTime(2026, 10, 26, 9, 30)) }
        ElDateTimePicker(
            value = value,
            onValueChange = { value = it },
            format = "YYYY/MM/DD HH:mm:ss",
            valueFormat = "YYYY-MM-DD h:m:s a",
            onFormattedValueChange = { valueFormatText = it },
            modifier = Modifier.width(300.dp),
        )
        DemoHint("Value: ${valueFormatText ?: value}")
    }

    DemoExample("Date and time formats with date-format and time-format", "Split the date and time patterns for the field text.") {
        var dateTime by remember { mutableStateOf<LocalDateTime?>(null) }
        var rangeStart by remember { mutableStateOf<LocalDateTime?>(null) }
        var rangeEnd by remember { mutableStateOf<LocalDateTime?>(null) }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Top) {
            ElDateTimePicker(
                value = dateTime,
                onValueChange = { dateTime = it },
                dateFormat = "MMM DD, YYYY",
                timeFormat = "HH:mm",
                placeholder = "Pick a Date",
                modifier = Modifier.width(280.dp),
            )
            ElDateTimeRangePicker(
                start = rangeStart,
                end = rangeEnd,
                onValueChange = { s, e -> rangeStart = s; rangeEnd = e },
                startPlaceholder = "Start date",
                endPlaceholder = "End date",
                dateFormat = "YYYY/MM/DD ddd",
                timeFormat = "A hh:mm:ss",
                singlePanel = true,
                showConfirm = false,
                modifier = Modifier.width(340.dp),
            )
        }
    }

    DemoExample("Date and time range", "Two calendars plus start and end time columns.") {
        var start by remember { mutableStateOf<LocalDateTime?>(LocalDateTime(2026, 10, 10, 10, 10)) }
        var end by remember { mutableStateOf<LocalDateTime?>(LocalDateTime(2026, 10, 11, 10, 10)) }
        ElDateTimeRangePicker(
            start = start,
            end = end,
            onValueChange = { s, e -> start = s; end = e },
            rangeSeparator = "To",
            startPlaceholder = "Start date",
            endPlaceholder = "End date",
            modifier = Modifier.width(640.dp),
        )
        DemoHint("$start → $end")
    }

    DemoExample("Date and time range with shortcuts", "Quick options sit next to the calendars.") {
        var start by remember { mutableStateOf<LocalDateTime?>(null) }
        var end by remember { mutableStateOf<LocalDateTime?>(null) }
        ElDateTimeRangePicker(
            start = start,
            end = end,
            onValueChange = { s, e -> start = s; end = e },
            shortcuts = rangeShortcuts,
            rangeSeparator = "To",
            startPlaceholder = "Start date",
            endPlaceholder = "End date",
            showConfirm = false,
            singlePanel = true,
            modifier = Modifier.width(400.dp),
        )
        DemoHint("$start → $end")
    }

    DemoExample("Default time", "`default-time` fills the time portion when only a date is picked.") {
        var value by remember { mutableStateOf<LocalDateTime?>(null) }
        var start by remember { mutableStateOf<LocalDateTime?>(null) }
        var end by remember { mutableStateOf<LocalDateTime?>(null) }
        var secondStart by remember { mutableStateOf<LocalDateTime?>(null) }
        var secondEnd by remember { mutableStateOf<LocalDateTime?>(null) }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElDateTimePicker(
                value = value,
                onValueChange = { value = it },
                defaultTime = LocalTime(12, 0, 0),
                placeholder = "Select date and time",
                modifier = Modifier.width(300.dp),
            )
            DemoRow {
                ElDateTimeRangePicker(
                    start = start,
                    end = end,
                    onValueChange = { s, e -> start = s; end = e },
                    defaultStartTime = LocalTime(12, 0, 0),
                    defaultEndTime = LocalTime(12, 0, 0),
                    startPlaceholder = "Start and end time 12:00:00",
                    endPlaceholder = "Start and end time 12:00:00",
                    singlePanel = true,
                    showConfirm = false,
                    modifier = Modifier.width(340.dp),
                )
                ElDateTimeRangePicker(
                    start = secondStart,
                    end = secondEnd,
                    onValueChange = { s, e -> secondStart = s; secondEnd = e },
                    defaultStartTime = LocalTime(12, 0, 0),
                    defaultEndTime = LocalTime(8, 0, 0),
                    startPlaceholder = "Start date time 12:00:00",
                    endPlaceholder = "End date time 08:00:00",
                    singlePanel = true,
                    showConfirm = false,
                    modifier = Modifier.width(340.dp),
                )
            }
        }
        DemoHint("$value · $start → $end · $secondStart → $secondEnd")
    }

    DemoExample("Single panel", "Both the range picker and the panel can render a single calendar.") {
        var start by remember { mutableStateOf<LocalDateTime?>(null) }
        var end by remember { mutableStateOf<LocalDateTime?>(null) }
        var value by remember { mutableStateOf<LocalDateTime?>(LocalDateTime(2026, 10, 26, 9, 30)) }
        DemoRow {
            ElDateTimeRangePicker(
                start = start,
                end = end,
                onValueChange = { s, e -> start = s; end = e },
                singlePanel = true,
                showConfirm = false,
                modifier = Modifier.width(300.dp),
            )
            ElDateTimePickerPanel(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.width(300.dp),
            )
        }
        DemoHint("Value: $value")
    }

    DemoExample("Disabled hours, minutes and seconds", "Time-level disabling keeps only working hours selectable.") {
        var value by remember { mutableStateOf<LocalDateTime?>(LocalDateTime(2026, 10, 26, 9, 30)) }
        ElDateTimePicker(
            value = value,
            onValueChange = { value = it },
            disabledHours = { it < 8 || it > 18 },
            disabledMinutes = { it % 15 != 0 },
            modifier = Modifier.width(320.dp),
        )
        DemoHint("Only 08:00–18:59 and quarter-hour minutes are enabled")
    }

    DemoExample("Custom icons", "Custom previous/next month and year icons.") {
        var value by remember { mutableStateOf<LocalDateTime?>(now) }
        ElDateTimePicker(
            value = value,
            onValueChange = { value = it },
            format = "YYYY-MM-DD HH:mm:ss",
            dateFormat = "MMM DD, YYYY",
            timeFormat = "HH:mm",
            prevIcon = ElIcons.CaretLeft,
            nextIcon = ElIcons.CaretRight,
            prevYearIcon = ElIcons.ArrowLeftBold,
            nextYearIcon = ElIcons.ArrowRightBold,
            modifier = Modifier.width(320.dp),
        )
    }

    DemoExample("With shortcuts", "Quick options for the date-time picker.") {
        var value by remember { mutableStateOf<LocalDateTime?>(null) }
        ElDateTimePicker(
            value = value,
            onValueChange = { value = it },
            shortcuts = singleShortcuts,
            placeholder = "Select date and time",
            modifier = Modifier.width(300.dp),
        )
        DemoHint("Value: $value")
    }

    DemoExample("Disabled", "Disable date and time selection.") {
        ElDateTimePicker(
            value = LocalDateTime(2026, 10, 26, 9, 30),
            onValueChange = {},
            disabled = true,
            modifier = Modifier.width(300.dp),
        )
    }

    DemoExample("Editable", "Type a full date-time and press Enter.") {
        var value by remember { mutableStateOf<LocalDateTime?>(null) }
        ElDateTimePicker(
            value = value,
            onValueChange = { value = it },
            editable = true,
            placeholder = "YYYY-MM-DD HH:mm:ss",
            modifier = Modifier.width(280.dp),
        )
        DemoHint("Value: $value")
    }

    DemoExample("Seconds can be omitted", "`show-seconds = false` hides the seconds column.") {
        ElDateTimePicker(
            value = noSeconds,
            onValueChange = { noSeconds = it },
            showSeconds = false,
            format = "YYYY-MM-DD HH:mm",
            modifier = Modifier.width(280.dp),
        )
    }

    DemoExample("Date and time range formats", "A datetime range with a localized display format.") {
        var start by remember { mutableStateOf<LocalDateTime?>(null) }
        var end by remember { mutableStateOf<LocalDateTime?>(null) }
        ElDateTimeRangePicker(
            start = start,
            end = end,
            onValueChange = { s, e -> start = s; end = e },
            dateFormat = "YYYY/MM/DD ddd",
            timeFormat = "A hh:mm:ss",
            startPlaceholder = "Start date",
            endPlaceholder = "End date",
            singlePanel = true,
            showConfirm = false,
            modifier = Modifier.width(360.dp),
        )
        DemoHint("$start → $end")
    }

    DemoExample("Panel with default value", "The panel opens on the current date when no value is provided.") {
        var value by remember { mutableStateOf<LocalDateTime?>(null) }
        ElDateTimePickerPanel(
            value = value,
            onValueChange = { value = it },
            defaultTime = LocalTime(9, 0),
            showFooter = true,
            modifier = Modifier.width(300.dp),
        )
        DemoHint("Value: $value")
    }

    DemoExample("Range panel", "Embed a standalone date-time range panel.") {
        var start by remember { mutableStateOf<LocalDateTime?>(null) }
        var end by remember { mutableStateOf<LocalDateTime?>(null) }
        ElDateTimeRangePicker(
            start = start,
            end = end,
            onValueChange = { s, e -> start = s; end = e },
            showConfirm = false,
            modifier = Modifier.width(300.dp),
        )
        DemoHint("Single calendar range with default times · $start → $end")
    }

    DemoExample("Disabled date", "Block days in the calendar part of the picker.") {
        var value by remember { mutableStateOf<LocalDateTime?>(null) }
        ElDateTimePicker(
            value = value,
            onValueChange = { value = it },
            disabledDate = { it < LocalDate(2026, 10, 1) },
            placeholder = "October 2026 onwards",
            modifier = Modifier.width(300.dp),
        )
    }
}
