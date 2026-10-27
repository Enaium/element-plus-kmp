package cn.enaium.elementplus.sample.gallery.data

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElCalendar
import cn.enaium.elementplus.components.ElCalendarControllerType
import cn.enaium.elementplus.components.ElCalendarDateCellType
import cn.enaium.elementplus.components.ElCalendarLabelType
import cn.enaium.elementplus.components.ElRadioGroup
import cn.enaium.elementplus.components.ElRadioOption
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

@Composable
fun CalendarDocs() {
    val locale = LocalElConfig.current.locale
    DemoExample("Basic usage", "A month calendar with previous, next and today actions.") {
        var basic by remember { mutableStateOf(LocalDate(2026, 10, 26)) }
        ElCalendar(value = basic, onValueChange = { basic = it })
        DemoHint("Selected: $basic")
    }
    DemoExample("Custom header", "Provide a custom header to control navigation.") {
        var headerDate by remember { mutableStateOf(LocalDate(2026, 10, 26)) }
        ElCalendar(
            value = headerDate,
            onValueChange = { headerDate = it },
            header = { month, previous, next, today ->
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicText("${month.year} / ${month.month.number}", style = LocalElTheme.current.typography.base)
                    Spacer(Modifier.weight(1f))
                    ElButton(onClick = previous, text = "Prev", size = ElSize.Small)
                    Spacer(Modifier.width(6.dp))
                    ElButton(onClick = today, text = "Today", size = ElSize.Small)
                    Spacer(Modifier.width(6.dp))
                    ElButton(onClick = next, text = "Next", size = ElSize.Small)
                }
            },
        )
    }
    DemoExample(
        "Year navigation",
        "The headerContent slot exposes every selectDate action, including previous/next year.",
    ) {
        var yearDate by remember { mutableStateOf(LocalDate(2026, 10, 26)) }
        ElCalendar(
            value = yearDate,
            onValueChange = { yearDate = it },
            headerContent = { scope ->
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    BasicText(
                        text = "Custom header · ${scope.date.year} / ${scope.date.month.number}",
                        style = LocalElTheme.current.typography.base,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ElButton(onClick = scope.prevYear, text = "Previous Year", size = ElSize.Small)
                        ElButton(onClick = scope.prevMonth, text = "Previous Month", size = ElSize.Small)
                        ElButton(onClick = scope.today, text = "Today", size = ElSize.Small)
                        ElButton(onClick = scope.nextMonth, text = "Next Month", size = ElSize.Small)
                        ElButton(onClick = scope.nextYear, text = "Next Year", size = ElSize.Small)
                    }
                }
            },
        )
    }
    DemoExample(
        "Controller type",
        "Switch the header between button and month/year select controllers.",
    ) {
        var controller by remember { mutableStateOf("button") }
        var controllerDate by remember { mutableStateOf(LocalDate(2026, 10, 26)) }
        ElRadioGroup(
            options = listOf(
                ElRadioOption("button", "Button"),
                ElRadioOption("select", "Select"),
            ),
            selected = controller,
            onSelectedChange = { controller = it },
            button = true,
            size = ElSize.Small,
        )
        ElCalendar(
            value = controllerDate,
            onValueChange = { controllerDate = it },
            controllerType = if (controller == "select") ElCalendarControllerType.Select else ElCalendarControllerType.Button,
        )
    }
    DemoExample("Custom formatter", "Format the year and month labels of the select controller.") {
        var formatterDate by remember { mutableStateOf(LocalDate(2026, 10, 26)) }
        ElCalendar(
            value = formatterDate,
            onValueChange = { formatterDate = it },
            controllerType = ElCalendarControllerType.Select,
            formatter = { value, type ->
                when (type) {
                    ElCalendarLabelType.Year -> "Year $value"
                    ElCalendarLabelType.Month -> locale.datePicker.months[value - 1]
                }
            },
        )
        DemoHint("The month select shows localized month names instead of numbers.")
    }
    DemoExample(
        "Selectable range",
        "Limit selection to a week-aligned window; the header then only shows the month title.",
    ) {
        var rangeDate by remember { mutableStateOf(LocalDate(2019, 3, 4)) }
        ElCalendar(
            value = rangeDate,
            onValueChange = { rangeDate = it },
            range = LocalDate(2019, 3, 4)..LocalDate(2019, 3, 24),
        )
        DemoHint("Selected: $rangeDate — only 2019-03-04 to 2019-03-24 can be picked.")
    }
    DemoExample("Custom date cell", "Render custom content inside each date cell.") {
        var custom by remember { mutableStateOf(LocalDate(2026, 10, 26)) }
        ElCalendar(
            value = custom,
            onValueChange = { custom = it },
            dateCell = { date, selected, inMonth ->
                val theme = LocalElTheme.current
                Column(
                    Modifier.fillMaxSize().padding(4.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    BasicText(
                        date.day.toString(),
                        style = theme.typography.small.copy(
                            color = when {
                                !inMonth -> theme.colors.textDisabled
                                selected -> theme.colors.primary
                                else -> theme.colors.textRegular
                            },
                        ),
                    )
                    if (date.day % 5 == 0) {
                        BasicText("Event", style = theme.typography.extraSmall.copy(color = theme.colors.success))
                    }
                }
            },
        )
    }
    DemoExample(
        "Date cell payload",
        "The dateCellContent slot receives data.day, the cell type and the selected flag.",
    ) {
        var payloadDate by remember { mutableStateOf(LocalDate(2026, 10, 26)) }
        ElCalendar(
            value = payloadDate,
            onValueChange = { payloadDate = it },
            data = mapOf(
                LocalDate(2026, 10, 1) to "Release",
                LocalDate(2026, 10, 15) to "Review",
            ),
            dateCellContent = { cell ->
                val theme = LocalElTheme.current
                Column(
                    Modifier.fillMaxSize().padding(4.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    BasicText(
                        text = cell.day.split("-").drop(1).joinToString("-"),
                        style = theme.typography.small.copy(
                            color = when {
                                cell.isSelected -> theme.colors.primary
                                cell.type != ElCalendarDateCellType.CurrentMonth -> theme.colors.textPlaceholder
                                else -> theme.colors.textRegular
                            },
                            textAlign = TextAlign.End,
                        ),
                    )
                    BasicText(
                        text = cell.data ?: cell.type.name,
                        style = theme.typography.extraSmall.copy(
                            color = if (cell.data != null) theme.colors.success else theme.colors.textPlaceholder,
                        ),
                    )
                }
            },
        )
        DemoHint("Selected: $payloadDate")
    }
}
