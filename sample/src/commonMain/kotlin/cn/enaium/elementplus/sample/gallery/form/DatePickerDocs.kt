package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElDateCellInfo
import cn.enaium.elementplus.components.ElDateMultiplePicker
import cn.enaium.elementplus.components.ElDatePicker
import cn.enaium.elementplus.components.ElDatePickerType
import cn.enaium.elementplus.components.ElDateRangePicker
import cn.enaium.elementplus.components.ElDateRangeType
import cn.enaium.elementplus.components.ElDateShortcut
import cn.enaium.elementplus.components.ElDateTimeRangePicker
import cn.enaium.elementplus.components.ElRadioGroup
import cn.enaium.elementplus.components.ElRadioOption
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.sample.gallery.DemoRow
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

@Composable
fun DatePickerDocs() {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

    var formatted by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 1, 2)) }
    var formattedText by remember { mutableStateOf<String?>(null) }
    var timestamp by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 1, 2)) }
    var timestampText by remember { mutableStateOf<String?>(null) }
    var month by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 1)) }
    var size by remember { mutableStateOf("default") }

    val singleShortcuts = listOf(
        ElDateShortcut("Today", value = today),
        ElDateShortcut("Yesterday", value = today.minus(1, DateTimeUnit.DAY)),
        ElDateShortcut("A week ago", value = today.minus(7, DateTimeUnit.DAY)),
    )
    val rangeShortcuts = listOf(
        ElDateShortcut("Last week", start = today.minus(7, DateTimeUnit.DAY), end = today),
        ElDateShortcut("Last month", start = today.minus(30, DateTimeUnit.DAY), end = today),
        ElDateShortcut("Last 3 months", start = today.minus(90, DateTimeUnit.DAY), end = today),
    )

    DemoExample("Basic usage", "Pick a date from a popup calendar.") {
        var basic by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        ElDatePicker(value = basic, onValueChange = { basic = it }, placeholder = "Pick a date", modifier = Modifier.width(220.dp))
        DemoHint("Selected: $basic")
    }

    DemoExample("Date formats", "`format` controls the text in the field, `value-format` the emitted string.") {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Top) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DemoHint("Emits a date object")
                ElDatePicker(
                    value = formatted,
                    onValueChange = { formatted = it },
                    format = "YYYY/MM/DD",
                    modifier = Modifier.width(200.dp),
                )
                DemoHint("Value: $formatted")
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DemoHint("Use value-format")
                ElDatePicker(
                    value = formatted,
                    onValueChange = { formatted = it },
                    format = "YYYY/MM/DD",
                    valueFormat = "YYYY-MM-DD",
                    onFormattedValueChange = { formattedText = it },
                    modifier = Modifier.width(200.dp),
                )
                DemoHint("Value: ${formattedText ?: "-"}")
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DemoHint("Timestamp")
                ElDatePicker(
                    value = timestamp,
                    onValueChange = { timestamp = it },
                    format = "YYYY/MM/DD",
                    valueFormat = "x",
                    onFormattedValueChange = { timestampText = it },
                    modifier = Modifier.width(200.dp),
                )
                DemoHint("Value: ${timestampText ?: "-"}")
            }
        }
    }

    DemoExample("Week, month, quarter and year", "Single period types: week (`ww`), quarter (`Q`) and the plain month/year grids.") {
        var week by remember { mutableStateOf<LocalDate?>(today) }
        var quarter by remember { mutableStateOf<LocalDate?>(null) }
        var year by remember { mutableStateOf<LocalDate?>(null) }
        DemoRow {
            ElDatePicker(
                value = week,
                onValueChange = { week = it },
                type = ElDatePickerType.Week,
                format = "[Week] ww",
                placeholder = "Pick a week",
                modifier = Modifier.width(180.dp),
            )
            ElDatePicker(
                value = month,
                onValueChange = { month = it },
                type = ElDatePickerType.Month,
                format = "YYYY-MM",
                placeholder = "Pick a month",
                modifier = Modifier.width(180.dp),
            )
            ElDatePicker(
                value = quarter,
                onValueChange = { quarter = it },
                type = ElDatePickerType.Quarter,
                format = "YYYY-[Q]Q",
                placeholder = "Pick a quarter",
                modifier = Modifier.width(180.dp),
            )
            ElDatePicker(
                value = year,
                onValueChange = { year = it },
                type = ElDatePickerType.Year,
                format = "YYYY",
                placeholder = "Pick a year",
                modifier = Modifier.width(160.dp),
            )
        }
        DemoHint("Week: $week · Month: $month · Quarter: $quarter · Year: $year")
    }

    DemoExample("Multiple dates, months, years and quarters", "`dates`, `months`, `years` and `quarters` select several periods at once.") {
        var dates by remember { mutableStateOf(listOf(LocalDate(2026, 10, 26))) }
        var months by remember { mutableStateOf(listOf(LocalDate(2026, 1, 1))) }
        var years by remember { mutableStateOf(listOf(LocalDate(2026, 1, 1))) }
        var quarters by remember { mutableStateOf(emptyList<LocalDate>()) }
        DemoRow {
            ElDateMultiplePicker(
                values = dates,
                onValueChange = { dates = it },
                type = ElDatePickerType.Dates,
                placeholder = "Pick one or more dates",
                modifier = Modifier.width(210.dp),
            )
            ElDateMultiplePicker(
                values = months,
                onValueChange = { months = it },
                type = ElDatePickerType.Months,
                format = "YYYY-MM",
                placeholder = "Pick one or more months",
                modifier = Modifier.width(210.dp),
            )
            ElDateMultiplePicker(
                values = years,
                onValueChange = { years = it },
                type = ElDatePickerType.Years,
                format = "YYYY",
                placeholder = "Pick one or more years",
                modifier = Modifier.width(210.dp),
            )
            ElDateMultiplePicker(
                values = quarters,
                onValueChange = { quarters = it },
                type = ElDatePickerType.Quarters,
                format = "YYYY-[Q]Q",
                placeholder = "Pick one or more quarters",
                modifier = Modifier.width(210.dp),
            )
        }
        DemoHint("Dates: $dates · Months: $months · Years: $years · Quarters: $quarters")
    }

    DemoExample("Default value", "Without a model value the popup opens on `default-value`.") {
        var emptyDate by remember { mutableStateOf<LocalDate?>(null) }
        var emptyStart by remember { mutableStateOf<LocalDate?>(null) }
        var emptyEnd by remember { mutableStateOf<LocalDate?>(null) }
        DemoRow {
            DemoHint("date")
            ElDatePicker(
                value = emptyDate,
                onValueChange = { emptyDate = it },
                defaultValue = LocalDate(2010, 9, 1),
                placeholder = "Pick a date",
                modifier = Modifier.width(200.dp),
            )
            DemoHint("daterange")
            ElDateRangePicker(
                start = emptyStart,
                end = emptyEnd,
                onValueChange = { s, e -> emptyStart = s; emptyEnd = e },
                defaultValue = LocalDate(2010, 9, 1),
                defaultEndValue = LocalDate(2010, 10, 1),
                startPlaceholder = "Start Date",
                endPlaceholder = "End Date",
                singlePanel = true,
                modifier = Modifier.width(260.dp),
            )
        }
    }

    DemoExample("Default time", "Date ranges pick the start at 00:00:00 and the end at 23:59:59 by default.") {
        var rangeStart by remember { mutableStateOf<LocalDateTime?>(null) }
        var rangeEnd by remember { mutableStateOf<LocalDateTime?>(null) }
        ElDateTimeRangePicker(
            start = rangeStart,
            end = rangeEnd,
            onValueChange = { s, e -> rangeStart = s; rangeEnd = e },
            startPlaceholder = "Start date",
            endPlaceholder = "End date",
            defaultStartTime = LocalTime(0, 0, 0),
            defaultEndTime = LocalTime(23, 59, 59),
            format = "YYYY-MM-DD HH:mm:ss",
            singlePanel = true,
            modifier = Modifier.width(320.dp),
        )
        DemoHint("Value: ${rangeStart ?: "-"} → ${rangeEnd ?: "-"}")
    }

    DemoExample("Enter date", "Editable: type a date matching the format and press Enter.") {
        var typed by remember { mutableStateOf<LocalDate?>(null) }
        ElDatePicker(
            value = typed,
            onValueChange = { typed = it },
            editable = true,
            format = "YYYY-MM-DD",
            placeholder = "Pick a day or type it",
            disabledDate = { it > today },
            shortcuts = singleShortcuts,
            modifier = Modifier.width(240.dp),
        )
        DemoHint("Value: $typed (future dates are disabled)")
    }

    DemoExample("Show confirm", "`show-confirm` keeps the popup open until the footer button is used.") {
        var value by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        ElDatePicker(
            value = value,
            onValueChange = { value = it },
            showConfirm = true,
            placeholder = "Pick a day",
            modifier = Modifier.width(240.dp),
        )
        DemoHint("Value: $value")
    }

    DemoExample("Date range", "Two linked calendars with quick options.") {
        var start by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 1)) }
        var end by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 8)) }
        var quickStart by remember { mutableStateOf<LocalDate?>(null) }
        var quickEnd by remember { mutableStateOf<LocalDate?>(null) }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ElDateRangePicker(
                    start = start,
                    end = end,
                    onValueChange = { s, e -> start = s; end = e },
                    rangeSeparator = "To",
                    startPlaceholder = "Start date",
                    endPlaceholder = "End date",
                    modifier = Modifier.width(280.dp),
                )
                ElDateRangePicker(
                    start = quickStart,
                    end = quickEnd,
                    onValueChange = { s, e -> quickStart = s; quickEnd = e },
                    shortcuts = rangeShortcuts,
                    rangeSeparator = "To",
                    startPlaceholder = "Start date",
                    endPlaceholder = "End date",
                    unlinkPanels = true,
                    modifier = Modifier.width(280.dp),
                )
            }
            DemoHint("$start → $end")
        }
    }

    DemoExample("Month range", "Range pickers work for months as well.") {
        var start by remember { mutableStateOf<LocalDate?>(null) }
        var end by remember { mutableStateOf<LocalDate?>(null) }
        var quickStart by remember { mutableStateOf<LocalDate?>(null) }
        var quickEnd by remember { mutableStateOf<LocalDate?>(null) }
        val thisMonth = LocalDate(today.year, today.month.number, 1)
        val monthShortcuts = listOf(
            ElDateShortcut("This month", start = thisMonth, end = thisMonth),
            ElDateShortcut("This year", start = LocalDate(today.year, 1, 1), end = thisMonth),
            ElDateShortcut("Last 6 months", start = thisMonth.minus(6, DateTimeUnit.MONTH), end = thisMonth),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ElDateRangePicker(
                start = start,
                end = end,
                onValueChange = { s, e -> start = s; end = e },
                type = ElDateRangeType.Month,
                format = "YYYY-MM",
                rangeSeparator = "To",
                startPlaceholder = "Start month",
                endPlaceholder = "End month",
                modifier = Modifier.width(290.dp),
            )
            ElDateRangePicker(
                start = quickStart,
                end = quickEnd,
                onValueChange = { s, e -> quickStart = s; quickEnd = e },
                type = ElDateRangeType.Month,
                format = "YYYY-MM",
                shortcuts = monthShortcuts,
                rangeSeparator = "To",
                startPlaceholder = "Start month",
                endPlaceholder = "End month",
                modifier = Modifier.width(290.dp),
            )
        }
    }

    DemoExample("Year range", "Range pickers also support decade panels.") {
        var start by remember { mutableStateOf<LocalDate?>(null) }
        var end by remember { mutableStateOf<LocalDate?>(null) }
        var quickStart by remember { mutableStateOf<LocalDate?>(null) }
        var quickEnd by remember { mutableStateOf<LocalDate?>(null) }
        val yearShortcuts = listOf(
            ElDateShortcut("This year", start = LocalDate(today.year, 1, 1), end = LocalDate(today.year, 1, 1)),
            ElDateShortcut("Last 10 years", start = LocalDate(today.year - 10, 1, 1), end = LocalDate(today.year, 1, 1)),
            ElDateShortcut("Next 50 years", start = LocalDate(today.year, 1, 1), end = LocalDate(today.year + 50, 1, 1)),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ElDateRangePicker(
                start = start,
                end = end,
                onValueChange = { s, e -> start = s; end = e },
                type = ElDateRangeType.Year,
                format = "YYYY",
                rangeSeparator = "To",
                startPlaceholder = "Start Year",
                endPlaceholder = "End Year",
                modifier = Modifier.width(280.dp),
            )
            ElDateRangePicker(
                start = quickStart,
                end = quickEnd,
                onValueChange = { s, e -> quickStart = s; quickEnd = e },
                type = ElDateRangeType.Year,
                format = "YYYY",
                shortcuts = yearShortcuts,
                rangeSeparator = "To",
                startPlaceholder = "Start Year",
                endPlaceholder = "End Year",
                modifier = Modifier.width(280.dp),
            )
        }
    }

    DemoExample("Quarter range", "Quarter ranges select whole quarters.") {
        var start by remember { mutableStateOf<LocalDate?>(null) }
        var end by remember { mutableStateOf<LocalDate?>(null) }
        var quickStart by remember { mutableStateOf<LocalDate?>(null) }
        var quickEnd by remember { mutableStateOf<LocalDate?>(null) }
        val quarterStart = LocalDate(today.year, ((today.month.number - 1) / 3) * 3 + 1, 1)
        val quarterShortcuts = listOf(
            ElDateShortcut("This quarter", start = quarterStart, end = quarterStart),
            ElDateShortcut("Last 4 quarters", start = quarterStart.minus(12, DateTimeUnit.MONTH), end = quarterStart),
            ElDateShortcut("Next 8 quarters", start = quarterStart, end = quarterStart.plus(24, DateTimeUnit.MONTH)),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ElDateRangePicker(
                start = start,
                end = end,
                onValueChange = { s, e -> start = s; end = e },
                type = ElDateRangeType.Quarter,
                format = "YYYY-[Q]Q",
                rangeSeparator = "To",
                startPlaceholder = "Start quarter",
                endPlaceholder = "End quarter",
                modifier = Modifier.width(290.dp),
            )
            ElDateRangePicker(
                start = quickStart,
                end = quickEnd,
                onValueChange = { s, e -> quickStart = s; quickEnd = e },
                type = ElDateRangeType.Quarter,
                format = "YYYY-[Q]Q",
                shortcuts = quarterShortcuts,
                rangeSeparator = "To",
                startPlaceholder = "Start quarter",
                endPlaceholder = "End quarter",
                modifier = Modifier.width(290.dp),
            )
        }
    }

    DemoExample("Single panel", "Every range type can render a single calendar instead of a pair.") {
        var dateStart by remember { mutableStateOf<LocalDate?>(null) }
        var dateEnd by remember { mutableStateOf<LocalDate?>(null) }
        var monthStart by remember { mutableStateOf<LocalDate?>(null) }
        var monthEnd by remember { mutableStateOf<LocalDate?>(null) }
        var yearStart by remember { mutableStateOf<LocalDate?>(null) }
        var yearEnd by remember { mutableStateOf<LocalDate?>(null) }
        var quarterStart by remember { mutableStateOf<LocalDate?>(null) }
        var quarterEnd by remember { mutableStateOf<LocalDate?>(null) }
        DemoRow {
            ElDateRangePicker(
                start = dateStart,
                end = dateEnd,
                onValueChange = { s, e -> dateStart = s; dateEnd = e },
                singlePanel = true,
                placeholder = "daterange",
                modifier = Modifier.width(240.dp),
            )
            ElDateRangePicker(
                start = monthStart,
                end = monthEnd,
                onValueChange = { s, e -> monthStart = s; monthEnd = e },
                type = ElDateRangeType.Month,
                format = "YYYY-MM",
                singlePanel = true,
                startPlaceholder = "monthrange",
                endPlaceholder = "monthrange",
                modifier = Modifier.width(240.dp),
            )
            ElDateRangePicker(
                start = yearStart,
                end = yearEnd,
                onValueChange = { s, e -> yearStart = s; yearEnd = e },
                type = ElDateRangeType.Year,
                format = "YYYY",
                singlePanel = true,
                startPlaceholder = "yearrange",
                endPlaceholder = "yearrange",
                modifier = Modifier.width(240.dp),
            )
            ElDateRangePicker(
                start = quarterStart,
                end = quarterEnd,
                onValueChange = { s, e -> quarterStart = s; quarterEnd = e },
                type = ElDateRangeType.Quarter,
                format = "YYYY-[Q]Q",
                singlePanel = true,
                startPlaceholder = "quarterrange",
                endPlaceholder = "quarterrange",
                modifier = Modifier.width(240.dp),
            )
        }
    }

    DemoExample("Custom content", "Replace calendar cells through `cell-content` (holiday dots).") {
        val holidays = (1..7).map { LocalDate(2021, 10, it) }
        var date by remember { mutableStateOf<LocalDate?>(LocalDate(2021, 10, 29)) }
        var monthValue by remember { mutableStateOf<LocalDate?>(null) }
        var yearValue by remember { mutableStateOf<LocalDate?>(null) }
        var quarterValue by remember { mutableStateOf<LocalDate?>(null) }
        DemoRow {
            ElDatePicker(
                value = date,
                onValueChange = { date = it },
                format = "YYYY/MM/DD",
                defaultValue = LocalDate(2021, 10, 1),
                placeholder = "Pick a day",
                modifier = Modifier.width(200.dp),
                cellContent = { cell ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        DemoCellText(cell, cell.text)
                        if (cell.date in holidays) {
                            Box(
                                Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(LocalElTheme.current.colors.danger),
                            )
                        }
                    }
                },
            )
            ElDatePicker(
                value = monthValue,
                onValueChange = { monthValue = it },
                type = ElDatePickerType.Month,
                defaultValue = LocalDate(2021, 1, 1),
                placeholder = "Pick a month",
                modifier = Modifier.width(200.dp),
                cellContent = { cell -> DemoCellText(cell, "${cell.date.month.number}期") },
            )
            ElDatePicker(
                value = yearValue,
                onValueChange = { yearValue = it },
                type = ElDatePickerType.Year,
                defaultValue = LocalDate(2021, 1, 1),
                placeholder = "Pick a year",
                modifier = Modifier.width(200.dp),
                cellContent = { cell -> DemoCellText(cell, "${cell.date.year}y") },
            )
            ElDatePicker(
                value = quarterValue,
                onValueChange = { quarterValue = it },
                type = ElDatePickerType.Quarter,
                defaultValue = LocalDate(2021, 1, 1),
                placeholder = "Pick a quarter",
                modifier = Modifier.width(200.dp),
                cellContent = { cell -> DemoCellText(cell, "Q${(cell.date.month.number - 1) / 3 + 1}") },
            )
        }
    }

    DemoExample("Custom icons", "Header arrows accept custom icons for date and range pickers.") {
        var date by remember { mutableStateOf<LocalDate?>(today) }
        var rangeStart by remember { mutableStateOf<LocalDate?>(null) }
        var rangeEnd by remember { mutableStateOf<LocalDate?>(null) }
        DemoRow {
            ElDatePicker(
                value = date,
                onValueChange = { date = it },
                format = "YYYY/MM/DD",
                placeholder = "Pick a day",
                prevIcon = ElIcons.CaretLeft,
                nextIcon = ElIcons.CaretRight,
                prevYearIcon = ElIcons.ArrowLeftBold,
                nextYearIcon = ElIcons.ArrowRightBold,
                modifier = Modifier.width(220.dp),
            )
            ElDateRangePicker(
                start = rangeStart,
                end = rangeEnd,
                onValueChange = { s, e -> rangeStart = s; rangeEnd = e },
                startPlaceholder = "Start date",
                endPlaceholder = "End date",
                prevIcon = ElIcons.CaretLeft,
                nextIcon = ElIcons.CaretRight,
                prevYearIcon = ElIcons.ArrowLeftBold,
                nextYearIcon = ElIcons.ArrowRightBold,
                singlePanel = true,
                modifier = Modifier.width(280.dp),
            )
        }
    }

    DemoExample("Custom prefix icon", "`prefix-icon` replaces the leading decoration, `clear-icon` the clear affordance.") {
        var value by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        ElDatePicker(
            value = value,
            onValueChange = { value = it },
            prefixIcon = ElIcons.Clock,
            clearIcon = ElIcons.CloseBold,
            placeholder = "Pick a day",
            modifier = Modifier.width(240.dp),
        )
    }

    DemoExample("Sizes", "`size` controls the field height for pickers and ranges.") {
        ElRadioGroup(
            options = listOf(
                ElRadioOption("large", "large"),
                ElRadioOption("default", "default"),
                ElRadioOption("small", "small"),
            ),
            selected = size,
            onSelectedChange = { size = it },
            button = true,
        )
        val resolved = when (size) {
            "large" -> ElSize.Large
            "small" -> ElSize.Small
            else -> ElSize.Default
        }
        var value by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        var start by remember { mutableStateOf<LocalDate?>(null) }
        var end by remember { mutableStateOf<LocalDate?>(null) }
        ElDatePicker(
            value = value,
            onValueChange = { value = it },
            size = resolved,
            modifier = Modifier.width(220.dp),
        )
        ElDateRangePicker(
            start = start,
            end = end,
            onValueChange = { s, e -> start = s; end = e },
            size = resolved,
            singlePanel = true,
            modifier = Modifier.width(280.dp),
        )
    }

    DemoExample("Disabled", "Disable the field entirely.") {
        ElDatePicker(
            value = LocalDate(2026, 10, 26),
            onValueChange = {},
            disabled = true,
            modifier = Modifier.width(220.dp),
        )
    }

    DemoExample("Disabled dates", "`disabled-date` rejects a subset of days, e.g. weekends.") {
        var value by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        ElDatePicker(
            value = value,
            onValueChange = { value = it },
            disabledDate = { it.dayOfWeek.isoDayNumber >= 6 },
            placeholder = "Weekdays only",
            modifier = Modifier.width(220.dp),
        )
    }
}

@Composable
private fun DemoCellText(cell: ElDateCellInfo, text: String) {
    val colors = LocalElTheme.current.colors
    Box(
        Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(if (cell.isCurrent) colors.primary else Color.Transparent),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = text,
            style = LocalElTheme.current.typography.small.copy(
                color = if (cell.isCurrent) colors.white else if (cell.disabled) colors.textDisabled else colors.textRegular,
            ),
        )
    }
}
