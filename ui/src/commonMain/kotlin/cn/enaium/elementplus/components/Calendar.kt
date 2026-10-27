package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

/** Header controller style of [ElCalendar], mirroring `controller-type`. */
enum class ElCalendarControllerType { Button, Select }

/** Navigation actions available to the calendar header. */
enum class ElCalendarDateType { PrevMonth, NextMonth, PrevYear, NextYear, Today }

/** Position of a rendered day relative to the displayed month. */
enum class ElCalendarDateCellType { PrevMonth, CurrentMonth, NextMonth }

/** Kind of value passed to [ElCalendar]'s `formatter`. */
enum class ElCalendarLabelType { Year, Month }

/** Payload of the `date-cell` slot: equivalent to Element Plus' `{ data }`. */
class ElCalendarDateCell(
    val date: LocalDate,
    /** ISO `yyyy-MM-dd` form, matching `data.day` upstream. */
    val day: String,
    val type: ElCalendarDateCellType,
    val isSelected: Boolean,
    val data: String? = null,
)

/** Scope of the [ElCalendar] `headerContent` slot with every navigation action. */
class ElCalendarHeaderScope(
    val date: LocalDate,
    val prevYear: () -> Unit,
    val prevMonth: () -> Unit,
    val today: () -> Unit,
    val nextMonth: () -> Unit,
    val nextYear: () -> Unit,
    val selectDate: (ElCalendarDateType) -> Unit,
)

/**
 * `<el-calendar>`: month view with previous/next/today controls and optional
 * custom date cells.
 *
 * [range] restricts the selectable window to the week-aligned span between two
 * dates (the header then shows only the month title, as upstream does).
 * [controllerType] switches the header between button and month/year select
 * controllers; [formatter] customizes the select labels.
 */
@Composable
fun ElCalendar(
    value: LocalDate,
    onValueChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    cellHeight: Dp = 64.dp,
    header: (@Composable (month: LocalDate, previous: () -> Unit, next: () -> Unit, today: () -> Unit) -> Unit)? = null,
    dateCell: (@Composable (date: LocalDate, selected: Boolean, isCurrentMonth: Boolean) -> Unit)? = null,
    data: Map<LocalDate, String> = emptyMap(),
    range: ClosedRange<LocalDate>? = null,
    controllerType: ElCalendarControllerType = ElCalendarControllerType.Button,
    formatter: ((value: Int, type: ElCalendarLabelType) -> String)? = null,
    headerContent: (@Composable (ElCalendarHeaderScope) -> Unit)? = null,
    dateCellContent: (@Composable (ElCalendarDateCell) -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val today = remember { kotlin.time.Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date }
    var visibleMonth by remember(value) {
        mutableStateOf(LocalDate(value.year, value.month.number, 1))
    }
    val first = LocalDate(visibleMonth.year, visibleMonth.month.number, 1)
    val offset = first.dayOfWeek.isoDayNumber % 7
    val gridStart = if (range != null) {
        val start = range.start
        start.minus(start.dayOfWeek.isoDayNumber % 7, DateTimeUnit.DAY)
    } else {
        first.minus(offset, DateTimeUnit.DAY)
    }
    val weekRows = if (range == null) {
        6
    } else {
        var rows = 0
        var cursor = gridStart
        val gridEnd = range.endInclusive.plus((6 - range.endInclusive.dayOfWeek.isoDayNumber % 7), DateTimeUnit.DAY)
        while (cursor <= gridEnd) {
            rows++
            cursor = cursor.plus(7, DateTimeUnit.DAY)
        }
        rows.coerceAtLeast(1)
    }
    val monthStartOf = { date: LocalDate -> LocalDate(date.year, date.month.number, 1) }
    val isMonthInRange = { month: LocalDate ->
        val start = range?.start
        val end = range?.endInclusive
        if (start == null || end == null) {
            true
        } else {
            val monthEnd = month.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
            month <= end && monthEnd >= start
        }
    }
    val selectDate: (ElCalendarDateType) -> Unit = { type ->
        val target = when (type) {
            ElCalendarDateType.PrevMonth -> visibleMonth.minus(1, DateTimeUnit.MONTH)
            ElCalendarDateType.NextMonth -> visibleMonth.plus(1, DateTimeUnit.MONTH)
            ElCalendarDateType.PrevYear -> visibleMonth.minus(1, DateTimeUnit.YEAR)
            ElCalendarDateType.NextYear -> visibleMonth.plus(1, DateTimeUnit.YEAR)
            ElCalendarDateType.Today -> monthStartOf(today)
        }
        if (isMonthInRange(target)) {
            visibleMonth = target
            onValueChange(
                when (type) {
                    ElCalendarDateType.Today -> today
                    else -> monthStartOf(target)
                },
            )
        }
    }
    val canSelect = { date: LocalDate ->
        val start = range?.start
        val end = range?.endInclusive
        (start == null || date >= start) && (end == null || date <= end)
    }
    Column(
        modifier
            .fillMaxWidth()
            .border(1.dp, colors.borderLight, RoundedCornerShape(4.dp))
            .background(colors.background, RoundedCornerShape(4.dp)),
    ) {
        when {
            headerContent != null -> headerContent(
                ElCalendarHeaderScope(
                    date = visibleMonth,
                    prevYear = { selectDate(ElCalendarDateType.PrevYear) },
                    prevMonth = { selectDate(ElCalendarDateType.PrevMonth) },
                    today = { selectDate(ElCalendarDateType.Today) },
                    nextMonth = { selectDate(ElCalendarDateType.NextMonth) },
                    nextYear = { selectDate(ElCalendarDateType.NextYear) },
                    selectDate = selectDate,
                ),
            )
            header != null -> header(
                visibleMonth,
                { selectDate(ElCalendarDateType.PrevMonth) },
                { selectDate(ElCalendarDateType.NextMonth) },
                { selectDate(ElCalendarDateType.Today) },
            )
            range != null -> Row(
                Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText(
                    text = "${locale.datePicker.months[visibleMonth.month.number - 1]} ${visibleMonth.year}",
                    style = theme.typography.base.copy(color = colors.textPrimary, fontWeight = FontWeight.Medium),
                )
            }
            else -> Row(
                Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (controllerType == ElCalendarControllerType.Select) {
                    ElSelect(
                        value = visibleMonth.year,
                        onValueChange = { year ->
                            if (year != null) {
                                val target = monthStartOf(LocalDate(year, visibleMonth.month.number, 1))
                                if (isMonthInRange(target)) {
                                    visibleMonth = target
                                    onValueChange(target)
                                }
                            }
                        },
                        options = yearOptions(visibleMonth.year, range, formatter),
                        modifier = Modifier.width(96.dp),
                        size = ElSize.Small,
                        clearable = false,
                    )
                    Spacer(Modifier.width(8.dp))
                    ElSelect(
                        value = visibleMonth.month.number,
                        onValueChange = { month ->
                            if (month != null) {
                                val target = monthStartOf(LocalDate(visibleMonth.year, month, 1))
                                if (isMonthInRange(target)) {
                                    visibleMonth = target
                                    onValueChange(target)
                                }
                            }
                        },
                        options = monthOptions(formatter),
                        modifier = Modifier.width(88.dp),
                        size = ElSize.Small,
                        clearable = false,
                    )
                    Spacer(Modifier.weight(1f))
                    CalendarNav(locale.datePicker.today) { selectDate(ElCalendarDateType.Today) }
                } else {
                    BasicText(
                        text = "${locale.datePicker.months[visibleMonth.month.number - 1]} ${visibleMonth.year}",
                        style = theme.typography.base.copy(color = colors.textPrimary, fontWeight = FontWeight.Medium),
                    )
                    Spacer(Modifier.weight(1f))
                    CalendarNav(
                        icon = ElIcons.ArrowLeft,
                        enabled = isMonthInRange(visibleMonth.minus(1, DateTimeUnit.MONTH)),
                    ) { selectDate(ElCalendarDateType.PrevMonth) }
                    CalendarNav(locale.datePicker.today) { selectDate(ElCalendarDateType.Today) }
                    CalendarNav(
                        icon = ElIcons.ArrowRight,
                        enabled = isMonthInRange(visibleMonth.plus(1, DateTimeUnit.MONTH)),
                    ) { selectDate(ElCalendarDateType.NextMonth) }
                }
            }
        }
        Row(Modifier.fillMaxWidth().height(28.dp).background(colors.fillLight)) {
            locale.datePicker.weekdaysShort.forEach { day ->
                BasicText(
                    text = day,
                    modifier = Modifier.weight(1f),
                    style = theme.typography.extraSmall.copy(color = colors.textSecondary, textAlign = TextAlign.Center),
                )
            }
        }
        repeat(weekRows) { week ->
            Row(Modifier.fillMaxWidth().height(cellHeight)) {
                repeat(7) { day ->
                    val date = gridStart.plus(week * 7 + day, DateTimeUnit.DAY)
                    val inMonth = date.month.number == visibleMonth.month.number && date.year == visibleMonth.year
                    val selected = date == value
                    val selectable = canSelect(date)
                    val interaction = remember(date) { MutableInteractionSource() }
                    val hovered by interaction.collectIsHoveredAsState()
                    Box(
                        Modifier
                            .weight(1f)
                            .height(cellHeight)
                            .border(1.dp, colors.borderLighter)
                            .background(
                                when {
                                    selected -> colors.light(cn.enaium.elementplus.theme.ElType.Primary, 9)
                                    hovered && selectable -> colors.fillLighter
                                    else -> Color.Transparent
                                }
                            )
                            .hoverable(interaction)
                            .clickable(enabled = selectable) { onValueChange(date) }
                            .padding(4.dp),
                    ) {
                        val cellType = when {
                            date.year == visibleMonth.year && date.month.number == visibleMonth.month.number ->
                                ElCalendarDateCellType.CurrentMonth
                            date < first -> ElCalendarDateCellType.PrevMonth
                            else -> ElCalendarDateCellType.NextMonth
                        }
                        when {
                            dateCellContent != null -> dateCellContent(
                                ElCalendarDateCell(
                                    date = date,
                                    day = formatCalendarDay(date),
                                    type = cellType,
                                    isSelected = selected,
                                    data = data[date],
                                ),
                            )
                            dateCell != null -> dateCell(date, selected, inMonth)
                            else -> {
                                BasicText(
                                    text = date.day.toString(),
                                    modifier = Modifier.align(Alignment.TopEnd),
                                    style = theme.typography.small.copy(
                                        color = when {
                                            !selectable -> colors.textDisabled
                                            !inMonth -> colors.textPlaceholder
                                            selected -> colors.primary
                                            date == today -> colors.primary
                                            else -> colors.textRegular
                                        },
                                        fontWeight = if (selected || date == today) FontWeight.Medium else FontWeight.Normal,
                                    ),
                                )
                                data[date]?.let { label ->
                                    BasicText(
                                        text = label,
                                        modifier = Modifier.align(Alignment.BottomStart),
                                        style = theme.typography.extraSmall.copy(color = colors.textSecondary),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatCalendarDay(date: LocalDate): String {
    val month = date.month.number.toString().padStart(2, '0')
    val day = date.day.toString().padStart(2, '0')
    return "${date.year}-$month-$day"
}

private fun yearOptions(
    currentYear: Int,
    range: ClosedRange<LocalDate>?,
    formatter: ((value: Int, type: ElCalendarLabelType) -> String)?,
): List<ElSelectOption<Int>> {
    val years = if (range == null) {
        (currentYear - 10 until currentYear + 10).toList()
    } else {
        (range.start.year..range.endInclusive.year).toList()
    }
    return years.filter { it > 0 }.map { year ->
        ElSelectOption(year, formatter?.invoke(year, ElCalendarLabelType.Year) ?: year.toString())
    }
}

private fun monthOptions(
    formatter: ((value: Int, type: ElCalendarLabelType) -> String)?,
): List<ElSelectOption<Int>> = (1..12).map { month ->
    ElSelectOption(month, formatter?.invoke(month, ElCalendarLabelType.Month) ?: month.toString())
}

@Composable
private fun CalendarNav(text: String, onClick: () -> Unit) {
    Box(Modifier.padding(horizontal = 6.dp).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        BasicText(
            text = text,
            style = LocalElTheme.current.typography.small.copy(color = LocalElTheme.current.colors.primary),
        )
    }
}

@Composable
private fun CalendarNav(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        Modifier.padding(horizontal = 6.dp).clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        ElIcon(
            icon,
            size = 12.dp,
            tint = if (enabled) LocalElTheme.current.colors.textSecondary else LocalElTheme.current.colors.textDisabled,
        )
    }
}
