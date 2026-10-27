package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import cn.enaium.elementplus.components.ElBorder
import cn.enaium.elementplus.components.ElDateCellInfo
import cn.enaium.elementplus.components.ElDateMultiplePanel
import cn.enaium.elementplus.components.ElDatePickerPanel
import cn.enaium.elementplus.components.ElDatePickerType
import cn.enaium.elementplus.components.ElDateRangePanel
import cn.enaium.elementplus.components.ElDateRangeType
import cn.enaium.elementplus.components.ElDateTimePickerPanel
import cn.enaium.elementplus.components.ElDateTimeRangePanel
import cn.enaium.elementplus.components.ElRadioGroup
import cn.enaium.elementplus.components.ElRadioOption
import cn.enaium.elementplus.components.ElSwitch
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.sample.gallery.DemoRow
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number

@Composable
fun DatePickerPanelDocs() {
    var month by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 1)) }
    var year by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 1, 1)) }
    var quarter by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 7, 1)) }

    DemoExample("Basic usage", "A calendar panel for selecting a date.") {
        var date by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        ElDatePickerPanel(value = date, onValueChange = { date = it })
    }

    DemoExample("Month, quarter and year types", "Set `type` to month, quarter or year to pick larger periods.") {
        DemoRow {
            ElDatePickerPanel(value = month, onValueChange = { month = it }, type = ElDatePickerType.Month)
            ElDatePickerPanel(value = quarter, onValueChange = { quarter = it }, type = ElDatePickerType.Quarter)
            ElDatePickerPanel(value = year, onValueChange = { year = it }, type = ElDatePickerType.Year)
        }
        DemoHint("Month: $month · Quarter: $quarter · Year: $year")
    }

    DemoExample("Week panel and week numbers", "`type = Week` selects whole weeks; `show-week-number` adds the ISO week column.") {
        var week by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        var date by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        DemoRow {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                DemoHint("week")
                ElDatePickerPanel(
                    value = week,
                    onValueChange = { week = it },
                    type = ElDatePickerType.Week,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                DemoHint("show-week-number")
                ElDatePickerPanel(
                    value = date,
                    onValueChange = { date = it },
                    showWeekNumber = true,
                )
            }
        }
    }

    DemoExample("Multiple periods", "`dates`, `months`, `years` and `quarters` panels toggle several periods.") {
        var dates by remember { mutableStateOf(listOf(LocalDate(2026, 10, 26))) }
        var months by remember { mutableStateOf(listOf(LocalDate(2026, 1, 1))) }
        var years by remember { mutableStateOf(emptyList<LocalDate>()) }
        var quarters by remember { mutableStateOf(emptyList<LocalDate>()) }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DemoRow {
                ElDateMultiplePanel(values = dates, onValueChange = { dates = it }, type = ElDatePickerType.Dates)
                ElDateMultiplePanel(values = months, onValueChange = { months = it }, type = ElDatePickerType.Months)
            }
            DemoRow {
                ElDateMultiplePanel(values = years, onValueChange = { years = it }, type = ElDatePickerType.Years)
                ElDateMultiplePanel(values = quarters, onValueChange = { quarters = it }, type = ElDatePickerType.Quarters)
            }
        }
        DemoHint("Dates: $dates · Months: $months · Years: $years · Quarters: $quarters")
    }

    DemoExample("Range panels", "Date, month, year and quarter ranges, with one or two panels.") {
        var dateStart by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 5)) }
        var dateEnd by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 12)) }
        var monthStart by remember { mutableStateOf<LocalDate?>(null) }
        var monthEnd by remember { mutableStateOf<LocalDate?>(null) }
        var yearStart by remember { mutableStateOf<LocalDate?>(null) }
        var yearEnd by remember { mutableStateOf<LocalDate?>(null) }
        var quarterStart by remember { mutableStateOf<LocalDate?>(null) }
        var quarterEnd by remember { mutableStateOf<LocalDate?>(null) }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElDateRangePanel(
                start = dateStart,
                end = dateEnd,
                onValueChange = { s, e -> dateStart = s; dateEnd = e },
            )
            DemoRow {
                ElDateRangePanel(
                    start = monthStart,
                    end = monthEnd,
                    onValueChange = { s, e -> monthStart = s; monthEnd = e },
                    type = ElDateRangeType.Month,
                )
                ElDateRangePanel(
                    start = yearStart,
                    end = yearEnd,
                    onValueChange = { s, e -> yearStart = s; yearEnd = e },
                    type = ElDateRangeType.Year,
                )
                ElDateRangePanel(
                    start = quarterStart,
                    end = quarterEnd,
                    onValueChange = { s, e -> quarterStart = s; quarterEnd = e },
                    type = ElDateRangeType.Quarter,
                )
            }
        }
    }

    DemoExample("Disabled dates", "Weekends cannot be selected.") {
        var date by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        ElDatePickerPanel(
            value = date,
            onValueChange = { date = it },
            disabledDate = { it.dayOfWeek.isoDayNumber >= 6 },
        )
    }

    DemoExample("Disabled", "Toggle the whole panel.") {
        var disabled by remember { mutableStateOf(true) }
        var date by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        ElSwitch(
            checked = disabled,
            onCheckedChange = { disabled = it },
            activeText = "Disabled",
            inactiveText = "Enabled",
        )
        ElDatePickerPanel(value = date, onValueChange = { date = it }, disabled = disabled)
    }

    DemoExample("Border", "Panels are bordered by default; `border = false` blends into a container.") {
        var borderDate by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        var plainDate by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        DemoRow {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DemoHint("Border (default)")
                ElDatePickerPanel(value = borderDate, onValueChange = { borderDate = it })
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DemoHint("No border")
                ElBorder {
                    Box(Modifier.padding(8.dp)) {
                        ElDatePickerPanel(
                            value = plainDate,
                            onValueChange = { plainDate = it },
                            border = false,
                        )
                    }
                }
            }
        }
    }

    DemoExample("Custom content", "Panels accept `cell-content` to render custom period cells.") {
        var date by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        var monthValue by remember { mutableStateOf<LocalDate?>(null) }
        DemoRow {
            ElDatePickerPanel(
                value = date,
                onValueChange = { date = it },
                cellContent = { cell -> PanelCellText(cell, cell.text) },
            )
            ElDatePickerPanel(
                value = monthValue,
                onValueChange = { monthValue = it },
                type = ElDatePickerType.Month,
                cellContent = { cell -> PanelCellText(cell, "${cell.date.month.number}月") },
            )
        }
    }

    DemoExample("All types", "Every `type` supported by the date picker panel.") {
        val types = listOf(
            "year", "years", "month", "months", "date", "dates", "week", "quarter",
            "quarters", "quarterrange", "datetime", "datetimerange", "daterange",
            "monthrange", "yearrange",
        )
        var type by remember { mutableStateOf("date") }
        var date by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 10, 26)) }
        var multi by remember { mutableStateOf(emptyList<LocalDate>()) }
        var rangeStart by remember { mutableStateOf<LocalDate?>(null) }
        var rangeEnd by remember { mutableStateOf<LocalDate?>(null) }
        var dateTime by remember { mutableStateOf<LocalDateTime?>(LocalDateTime(2026, 10, 26, 9, 30)) }
        var dateTimeStart by remember { mutableStateOf<LocalDateTime?>(null) }
        var dateTimeEnd by remember { mutableStateOf<LocalDateTime?>(null) }
        ElRadioGroup(
            options = types.map { ElRadioOption(it, it) },
            selected = type,
            onSelectedChange = { type = it },
            direction = cn.enaium.elementplus.components.ElRadioDirection.Vertical,
        )
        when (type) {
            "year" -> ElDatePickerPanel(value = date, onValueChange = { date = it }, type = ElDatePickerType.Year)
            "years" -> ElDateMultiplePanel(values = multi, onValueChange = { multi = it }, type = ElDatePickerType.Years)
            "month" -> ElDatePickerPanel(value = date, onValueChange = { date = it }, type = ElDatePickerType.Month)
            "months" -> ElDateMultiplePanel(values = multi, onValueChange = { multi = it }, type = ElDatePickerType.Months)
            "date" -> ElDatePickerPanel(value = date, onValueChange = { date = it })
            "dates" -> ElDateMultiplePanel(values = multi, onValueChange = { multi = it }, type = ElDatePickerType.Dates)
            "week" -> ElDatePickerPanel(value = date, onValueChange = { date = it }, type = ElDatePickerType.Week)
            "quarter" -> ElDatePickerPanel(value = date, onValueChange = { date = it }, type = ElDatePickerType.Quarter)
            "quarters" -> ElDateMultiplePanel(values = multi, onValueChange = { multi = it }, type = ElDatePickerType.Quarters)
            "quarterrange" -> ElDateRangePanel(
                start = rangeStart,
                end = rangeEnd,
                onValueChange = { s, e -> rangeStart = s; rangeEnd = e },
                type = ElDateRangeType.Quarter,
            )
            "datetime" -> ElDateTimePickerPanel(value = dateTime, onValueChange = { dateTime = it })
            "datetimerange" -> ElDateTimeRangePanel(
                start = dateTimeStart,
                end = dateTimeEnd,
                onValueChange = { s, e -> dateTimeStart = s; dateTimeEnd = e },
            )
            "daterange" -> ElDateRangePanel(
                start = rangeStart,
                end = rangeEnd,
                onValueChange = { s, e -> rangeStart = s; rangeEnd = e },
            )
            "monthrange" -> ElDateRangePanel(
                start = rangeStart,
                end = rangeEnd,
                onValueChange = { s, e -> rangeStart = s; rangeEnd = e },
                type = ElDateRangeType.Month,
            )
            "yearrange" -> ElDateRangePanel(
                start = rangeStart,
                end = rangeEnd,
                onValueChange = { s, e -> rangeStart = s; rangeEnd = e },
                type = ElDateRangeType.Year,
            )
        }
        DemoHint("type = $type")
    }
}

@Composable
private fun PanelCellText(cell: ElDateCellInfo, text: String) {
    val colors = LocalElTheme.current.colors
    Box(
        Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(if (cell.isCurrent) colors.primary else androidx.compose.ui.graphics.Color.Transparent),
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
