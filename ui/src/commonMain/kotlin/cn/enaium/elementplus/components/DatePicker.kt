package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.internal.rememberElInteractionState
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.todayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

enum class ElDatePickerType { Date, Month, Year }

data class ElDateShortcut(val text: String, val value: LocalDate)

/** Current system date, used as the initial visible month. */
@Composable
internal fun rememberToday(): LocalDate = remember {
    Clock.System.todayIn(TimeZone.currentSystemDefault())
}

fun formatLocalDate(date: LocalDate, pattern: String = "YYYY-MM-DD"): String =
    formatDatePart(date, pattern)

fun formatLocalTime(time: LocalTime, pattern: String = "HH:mm:ss"): String {
    val hh = time.hour.toString().padStart(2, '0')
    val mm = time.minute.toString().padStart(2, '0')
    val ss = time.second.toString().padStart(2, '0')
    return pattern.replace("HH", hh).replace("H", time.hour.toString())
        .replace("mm", mm).replace("m", time.minute.toString())
        .replace("ss", ss).replace("s", time.second.toString())
}

fun formatLocalDateTime(dateTime: LocalDateTime, pattern: String = "YYYY-MM-DD HH:mm:ss"): String {
    var result = pattern
    result = result.replace("YYYY", dateTime.year.toString().padStart(4, '0'))
    result = result.replace("yy", (dateTime.year % 100).toString().padStart(2, '0'))
    result = result.replace("MM", dateTime.month.number.toString().padStart(2, '0'))
    result = result.replace("DD", dateTime.day.toString().padStart(2, '0'))
    result = result.replace("HH", dateTime.hour.toString().padStart(2, '0'))
    result = result.replace("mm", dateTime.minute.toString().padStart(2, '0'))
    result = result.replace("ss", dateTime.second.toString().padStart(2, '0'))
    return result
}

private fun formatDatePart(date: LocalDate, pattern: String): String {
    var result = pattern
    result = result.replace("YYYY", date.year.toString().padStart(4, '0'))
    result = result.replace("yy", (date.year % 100).toString().padStart(2, '0'))
    result = result.replace("MM", date.month.number.toString().padStart(2, '0'))
    result = result.replace("DD", date.day.toString().padStart(2, '0'))
    result = result.replace("M", date.month.number.toString())
    result = result.replace("D", date.day.toString())
    return result
}

private fun daysInMonth(year: Int, month: Int): Int {
    val first = LocalDate(year, month, 1)
    return first.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).day
}

private fun LocalDate.isBefore(other: LocalDate): Boolean = this < other
private fun LocalDate.isAfter(other: LocalDate): Boolean = this > other

/**
 * `<el-date-picker-panel>`: full calendar panel with day, month and year views.
 */
@Composable
fun ElDatePickerPanel(
    value: LocalDate?,
    onValueChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    type: ElDatePickerType = ElDatePickerType.Date,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    disabledDate: ((LocalDate) -> Boolean)? = null,
    initialMonth: LocalDate? = null,
    showFooter: Boolean = true,
) {
    DatePanelContent(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        type = type,
        minDate = minDate,
        maxDate = maxDate,
        disabledDate = disabledDate,
        initialMonth = initialMonth,
        showFooter = showFooter,
        rangeStart = null,
        rangeEnd = null,
        onRangeValueChange = null,
    )
}

@Composable
private fun DatePanelContent(
    value: LocalDate?,
    onValueChange: ((LocalDate) -> Unit)?,
    modifier: Modifier,
    type: ElDatePickerType,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    disabledDate: ((LocalDate) -> Boolean)?,
    initialMonth: LocalDate?,
    showFooter: Boolean,
    rangeStart: LocalDate?,
    rangeEnd: LocalDate?,
    onRangeValueChange: ((LocalDate?, LocalDate?) -> Unit)?,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val today = rememberToday()
    var visible by remember {
        mutableStateOf(initialMonth ?: value ?: rangeStart ?: today)
    }
    var view by remember { mutableStateOf(type) }
    var hoveredDate by remember { mutableStateOf<LocalDate?>(null) }
    fun canSelect(date: LocalDate): Boolean =
        (minDate == null || !date.isBefore(minDate)) &&
            (maxDate == null || !date.isAfter(maxDate)) &&
            (disabledDate?.invoke(date) != true)

    fun select(date: LocalDate) {
        if (!canSelect(date)) return
        if (onRangeValueChange != null) {
            val start = rangeStart
            val end = rangeEnd
            when {
                start == null || end != null -> onRangeValueChange(date, null)
                date < start -> onRangeValueChange(date, start)
                else -> onRangeValueChange(start, date)
            }
        } else {
            onValueChange?.invoke(date)
        }
    }

    Column(modifier.width(292.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        DatePanelHeader(
            visible = visible,
            view = view,
            onPrevYear = {
                visible = if (view == ElDatePickerType.Year) visible.minus(10, DateTimeUnit.YEAR)
                else visible.minus(1, DateTimeUnit.YEAR)
            },
            onNextYear = {
                visible = if (view == ElDatePickerType.Year) visible.plus(10, DateTimeUnit.YEAR)
                else visible.plus(1, DateTimeUnit.YEAR)
            },
            onPrevMonth = { visible = visible.minus(1, DateTimeUnit.MONTH) },
            onNextMonth = { visible = visible.plus(1, DateTimeUnit.MONTH) },
            onToggleView = {
                view = when (view) {
                    ElDatePickerType.Date -> ElDatePickerType.Month
                    ElDatePickerType.Month -> ElDatePickerType.Year
                    ElDatePickerType.Year -> if (type == ElDatePickerType.Year) ElDatePickerType.Year else ElDatePickerType.Date
                }
            },
        )
        when (view) {
            ElDatePickerType.Date -> {
                WeekdayHeader(locale)
                CalendarGrid(
                    visible = visible,
                    value = value,
                    rangeStart = rangeStart,
                    rangeEnd = rangeEnd,
                    hoveredDate = hoveredDate,
                    today = today,
                    canSelect = ::canSelect,
                    onHover = { hoveredDate = it },
                    onClick = ::select,
                )
            }
            ElDatePickerType.Month -> {
                MonthGrid(
                    year = visible.year,
                    selected = value,
                    minDate = minDate,
                    maxDate = maxDate,
                    onSelect = { month ->
                        val first = LocalDate(visible.year, month, 1)
                        if (type == ElDatePickerType.Month) {
                            onValueChange?.invoke(first)
                        } else {
                            visible = first
                            view = ElDatePickerType.Date
                        }
                    },
                )
            }
            ElDatePickerType.Year -> {
                YearGrid(
                    year = visible.year,
                    selected = value,
                    onSelect = { year ->
                        val first = LocalDate(year, 1, 1)
                        if (type == ElDatePickerType.Year) {
                            onValueChange?.invoke(first)
                        } else {
                            visible = first
                            view = ElDatePickerType.Month
                        }
                    },
                )
            }
        }
        if (showFooter) {
            Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                if (onRangeValueChange != null) {
                    BasicText(
                        text = listOfNotNull(rangeStart, rangeEnd).joinToString(" - ") { formatLocalDate(it) },
                        style = theme.typography.extraSmall.copy(color = colors.textSecondary),
                    )
                }
                Spacer(Modifier.weight(1f))
                BasicText(
                    text = locale.datePicker.today,
                    modifier = Modifier
                        .clickable { if (canSelect(today)) { select(today); visible = today } }
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    style = theme.typography.small.copy(color = colors.primary),
                )
            }
        }
    }
}

@Composable
private fun DatePanelHeader(
    visible: LocalDate,
    view: ElDatePickerType,
    onPrevYear: () -> Unit,
    onNextYear: () -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToggleView: () -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    Row(Modifier.fillMaxWidth().height(28.dp), verticalAlignment = Alignment.CenterVertically) {
        HeaderIcon(ElIcons.DArrowLeft, locale.datePicker.prevYear, onPrevYear)
        if (view == ElDatePickerType.Date) HeaderIcon(ElIcons.ArrowLeft, locale.datePicker.prevMonth, onPrevMonth)
        Spacer(Modifier.weight(1f))
        val label = when (view) {
            ElDatePickerType.Date -> "${locale.datePicker.months[visible.month.number - 1]} ${visible.year}"
            ElDatePickerType.Month -> visible.year.toString()
            ElDatePickerType.Year -> "${visible.year / 10 * 10} - ${visible.year / 10 * 10 + 9}"
        }
        BasicText(
            text = label,
            modifier = Modifier.clickable(onClick = onToggleView).padding(horizontal = 6.dp),
            style = theme.typography.small.copy(color = colors.textPrimary, fontWeight = FontWeight.Medium),
        )
        Spacer(Modifier.weight(1f))
        if (view == ElDatePickerType.Date) HeaderIcon(ElIcons.ArrowRight, locale.datePicker.nextMonth, onNextMonth)
        HeaderIcon(ElIcons.DArrowRight, locale.datePicker.nextYear, onNextYear)
    }
}

@Composable
private fun HeaderIcon(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    val colors = LocalElTheme.current.colors
    ElIcon(
        icon,
        size = 14.dp,
        tint = colors.textPlaceholder,
        contentDescription = contentDescription,
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 4.dp),
    )
}

@Composable
private fun WeekdayHeader(locale: cn.enaium.elementplus.theme.ElLocale) {
    Row(Modifier.fillMaxWidth()) {
        locale.datePicker.weekdaysShort.forEach { day ->
            BasicText(
                text = day,
                modifier = Modifier.weight(1f).height(26.dp),
                style = LocalElTheme.current.typography.extraSmall.copy(
                    color = LocalElTheme.current.colors.textSecondary,
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    visible: LocalDate,
    value: LocalDate?,
    rangeStart: LocalDate?,
    rangeEnd: LocalDate?,
    hoveredDate: LocalDate?,
    today: LocalDate,
    canSelect: (LocalDate) -> Boolean,
    onHover: (LocalDate?) -> Unit,
    onClick: (LocalDate) -> Unit,
) {
    val first = LocalDate(visible.year, visible.month.number, 1)
    val offset = first.dayOfWeek.isoDayNumber % 7
    val startDate = first.minus(offset, DateTimeUnit.DAY)
    val rangeEndResolved = rangeEnd ?: if (rangeStart != null) hoveredDate else null
    val rangeLow = listOfNotNull(rangeStart, rangeEndResolved).minOrNull()
    val rangeHigh = listOfNotNull(rangeStart, rangeEndResolved).maxOrNull()
    Column {
        repeat(6) { week ->
            Row(Modifier.fillMaxWidth().height(32.dp)) {
                repeat(7) { dayIndex ->
                    val date = startDate.plus(week * 7 + dayIndex, DateTimeUnit.DAY)
                    val inMonth = date.month.number == visible.month.number && date.year == visible.year
                    val selected = date == value || date == rangeStart || date == rangeEnd
                    val inRange = rangeLow != null && rangeHigh != null && date > rangeLow && date < rangeHigh
                    val hovered = date == hoveredDate
                    val enabled = canSelect(date)
                    val interaction = remember(date) { MutableInteractionSource() }
                    val hoverState = rememberElInteractionState(interaction).hovered
                    LaunchedEffect(hoverState) { if (hoverState) onHover(date) else if (hoveredDate == date) onHover(null) }
                    Box(
                        Modifier
                            .weight(1f)
                            .height(32.dp)
                            .padding(1.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                when {
                                    selected -> LocalElTheme.current.colors.primary
                                    inRange -> LocalElTheme.current.colors.light(ElType.Primary, 9)
                                    hoverState && enabled -> LocalElTheme.current.colors.fillLight
                                    else -> Color.Transparent
                                }
                            )
                            .hoverable(interaction)
                            .clickable(enabled = enabled) { onClick(date) },
                        contentAlignment = Alignment.Center,
                    ) {
                        BasicText(
                            text = date.day.toString(),
                            style = LocalElTheme.current.typography.small.copy(
                                color = when {
                                    !inMonth -> LocalElTheme.current.colors.textDisabled
                                    !enabled -> LocalElTheme.current.colors.textDisabled
                                    selected -> LocalElTheme.current.colors.white
                                    date == today -> LocalElTheme.current.colors.primary
                                    else -> LocalElTheme.current.colors.textRegular
                                },
                                fontWeight = if (date == today || selected) FontWeight.Medium else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthGrid(
    year: Int,
    selected: LocalDate?,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    onSelect: (Int) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    Column(Modifier.fillMaxWidth()) {
        for (row in 0 until 3) {
            Row(Modifier.fillMaxWidth().height(52.dp)) {
                for (col in 0 until 4) {
                    val month = row * 4 + col + 1
                    val first = LocalDate(year, month, 1)
                    val disabled = (minDate != null && first.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY) < minDate) ||
                        (maxDate != null && first > maxDate)
                    val isSelected = selected?.year == year && selected.month.number == month
                    Box(
                        Modifier
                            .weight(1f)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) colors.primary else Color.Transparent)
                            .clickable(enabled = !disabled) { onSelect(month) },
                        contentAlignment = Alignment.Center,
                    ) {
                        BasicText(
                            text = locale.datePicker.monthsShort[month - 1],
                            style = theme.typography.small.copy(
                                color = when {
                                    disabled -> colors.textDisabled
                                    isSelected -> colors.white
                                    else -> colors.textRegular
                                },
                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun YearGrid(
    year: Int,
    selected: LocalDate?,
    onSelect: (Int) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val startYear = (year / 10) * 10
    Column {
        for (row in 0 until 3) {
            Row(Modifier.fillMaxWidth().height(52.dp)) {
                for (col in 0 until 4) {
                    val y = startYear + row * 4 + col
                    if (y >= startYear + 10) {
                        Box(Modifier.weight(1f))
                    } else {
                        val isSelected = selected?.year == y
                        Box(
                            Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) colors.primary else Color.Transparent)
                                .clickable { onSelect(y) },
                            contentAlignment = Alignment.Center,
                        ) {
                            BasicText(
                                text = y.toString(),
                                style = theme.typography.small.copy(
                                    color = if (isSelected) colors.white else colors.textRegular,
                                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun DateField(
    display: String?,
    placeholder: String?,
    onClick: () -> Unit,
    onClear: (() -> Unit)?,
    modifier: Modifier,
    size: ElSize?,
    disabled: Boolean,
    icon: ImageVector,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val resolvedSize = resolveElSize(size)
    val shape = RoundedCornerShape(theme.radius.base)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(resolvedSize.height)
            .clip(shape)
            .background(if (disabled) colors.disabledBackground else colors.fillBlank, shape)
            .border(1.dp, if (disabled) colors.disabledBorder else if (hovered) colors.textDisabled else colors.border, shape)
            .hoverable(interaction)
            .clickable(enabled = !disabled, onClick = onClick)
            .padding(horizontal = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = display ?: placeholder ?: "",
            modifier = Modifier.weight(1f),
            style = theme.typography.base.copy(
                fontSize = resolvedSize.fontSize,
                color = if (display != null) colors.textRegular else colors.textPlaceholder,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (onClear != null && display != null && hovered && !disabled) {
            ElIcon(
                ElIcons.CircleClose,
                size = 14.dp,
                tint = colors.textPlaceholder,
                modifier = Modifier.clickable { onClear() },
            )
            Spacer(Modifier.width(4.dp))
        }
        ElIcon(icon, size = 14.dp, tint = colors.textPlaceholder)
    }
}

/** `<el-date-picker>`. */
@Composable
fun ElDatePicker(
    value: LocalDate?,
    onValueChange: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: ElSize? = null,
    type: ElDatePickerType = ElDatePickerType.Date,
    format: String = "YYYY-MM-DD",
    disabled: Boolean = false,
    clearable: Boolean = true,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    disabledDate: ((LocalDate) -> Boolean)? = null,
    onVisibleChange: ((Boolean) -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    var expanded by remember { mutableStateOf(false) }
    val locale = LocalElConfig.current.locale
    val display = value?.let { formatLocalDate(it, format) }
    Box(modifier) {
        DateField(
            display = display,
            placeholder = placeholder ?: locale.datePicker.selectDate,
            onClick = { expanded = true; onVisibleChange?.invoke(true) },
            onClear = if (clearable) ({ onValueChange(null) }) else null,
            modifier = Modifier,
            size = size,
            disabled = disabled,
            icon = ElIcons.Calendar,
        )
        ElPopper(
            visible = expanded,
            onDismissRequest = { expanded = false; onVisibleChange?.invoke(false) },
            placement = ElPlacement.BottomStart,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 4.dp),
            focusable = true,
        ) {
            ElPopperSurface(Modifier.padding(10.dp)) {
                ElDatePickerPanel(
                    value = value,
                    onValueChange = {
                        onValueChange(it)
                        expanded = false
                    },
                    type = type,
                    minDate = minDate,
                    maxDate = maxDate,
                    disabledDate = disabledDate,
                    initialMonth = value,
                )
            }
        }
    }
}

/** `<el-date-picker type="datetime">`. */
@Composable
fun ElDateTimePicker(
    value: LocalDateTime?,
    onValueChange: (LocalDateTime?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: ElSize? = null,
    format: String = "YYYY-MM-DD HH:mm:ss",
    disabled: Boolean = false,
    clearable: Boolean = true,
    showSeconds: Boolean = true,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    disabledDate: ((LocalDate) -> Boolean)? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    val locale = LocalElConfig.current.locale
    val colors = LocalElTheme.current.colors
    val display = value?.let { formatLocalDateTime(it, format) }
    Box(modifier) {
        DateField(
            display = display,
            placeholder = placeholder ?: locale.datePicker.selectDate,
            onClick = { expanded = true },
            onClear = if (clearable) ({ onValueChange(null) }) else null,
            modifier = Modifier,
            size = size,
            disabled = disabled,
            icon = ElIcons.Calendar,
        )
        ElPopper(
            visible = expanded,
            onDismissRequest = { expanded = false },
            placement = ElPlacement.BottomStart,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 4.dp),
            focusable = true,
        ) {
            ElPopperSurface(Modifier.width(320.dp).padding(10.dp)) {
                Column(Modifier.width(300.dp)) {
                    ElDateTimePickerPanel(
                        value = value,
                        onValueChange = onValueChange,
                        showSeconds = showSeconds,
                        minDate = minDate,
                        maxDate = maxDate,
                        disabledDate = disabledDate,
                        modifier = Modifier.width(300.dp),
                    )
                    Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.End) {
                    BasicText(
                        text = cn.enaium.elementplus.theme.LocalElConfig.current.locale.datePicker.now,
                        modifier = Modifier
                            .clickable {
                                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                                onValueChange(now)
                                expanded = false
                            }
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        style = LocalElTheme.current.typography.small.copy(color = colors.textSecondary),
                    )
                    Spacer(Modifier.width(6.dp))
                    BasicText(
                        text = cn.enaium.elementplus.theme.LocalElConfig.current.locale.datePicker.confirm,
                        modifier = Modifier
                            .clickable { expanded = false }
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        style = LocalElTheme.current.typography.small.copy(color = colors.primary),
                    )
                    }
                }
            }
        }
    }
}

/** Date + time panel used by [ElDateTimePicker]. */
@Composable
fun ElDateTimePickerPanel(
    value: LocalDateTime?,
    onValueChange: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier,
    showSeconds: Boolean = true,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    disabledDate: ((LocalDate) -> Boolean)? = null,
) {
    val today = rememberToday()
    var date by remember(value) { mutableStateOf(value?.date ?: today) }
    var time by remember(value) { mutableStateOf(value?.time ?: LocalTime(0, 0)) }
    Column(modifier.widthIn(max = 300.dp)) {
        ElDatePickerPanel(
            value = value?.date ?: date,
            onValueChange = {
                date = it
                onValueChange(LocalDateTime(it, time))
            },
            showFooter = false,
            minDate = minDate,
            maxDate = maxDate,
            disabledDate = disabledDate,
            initialMonth = date,
        )
        Row(
            Modifier.fillMaxWidth().height(180.dp).padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TimeSpinner(24, time.hour, Modifier.weight(1f)) { hour ->
                time = LocalTime(hour, time.minute, time.second)
                onValueChange(LocalDateTime(date, time))
            }
            TimeSpinner(60, time.minute, Modifier.weight(1f)) { minute ->
                time = LocalTime(time.hour, minute, time.second)
                onValueChange(LocalDateTime(date, time))
            }
            if (showSeconds) {
                TimeSpinner(60, time.second, Modifier.weight(1f)) { second ->
                    time = LocalTime(time.hour, time.minute, second)
                    onValueChange(LocalDateTime(date, time))
                }
            }
        }
    }
}

/** Range picker with a single shared calendar and start/end selection. */
@Composable
fun ElDateRangePicker(
    start: LocalDate?,
    end: LocalDate?,
    onValueChange: (LocalDate?, LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: ElSize? = null,
    format: String = "YYYY-MM-DD",
    disabled: Boolean = false,
    clearable: Boolean = true,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    disabledDate: ((LocalDate) -> Boolean)? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    val locale = LocalElConfig.current.locale
    val display = listOfNotNull(start, end).joinToString(" - ") { formatLocalDate(it, format) }
        .ifBlank { null }
    Box(modifier) {
        DateField(
            display = display,
            placeholder = placeholder ?: "${locale.datePicker.startDate} - ${locale.datePicker.endDate}",
            onClick = { expanded = true },
            onClear = if (clearable) ({ onValueChange(null, null) }) else null,
            modifier = Modifier,
            size = size,
            disabled = disabled,
            icon = ElIcons.Calendar,
        )
        ElPopper(
            visible = expanded,
            onDismissRequest = { expanded = false },
            placement = ElPlacement.BottomStart,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 4.dp),
            focusable = true,
        ) {
            ElPopperSurface(Modifier.padding(10.dp)) {
                DatePanelContent(
                    value = null,
                    onValueChange = null,
                    modifier = Modifier,
                    type = ElDatePickerType.Date,
                    minDate = minDate,
                    maxDate = maxDate,
                    disabledDate = disabledDate,
                    initialMonth = start ?: end,
                    showFooter = true,
                    rangeStart = start,
                    rangeEnd = end,
                    onRangeValueChange = { s, e ->
                        onValueChange(s, e)
                        if (s != null && e != null) expanded = false
                    },
                )
            }
        }
    }
}

/** Hour/minute/second spinner shared by date-time and time pickers. */
@Composable
internal fun TimeSpinner(
    count: Int,
    selected: Int,
    modifier: Modifier = Modifier,
    onSelect: (Int) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val state = rememberLazyListState(initialFirstVisibleItemIndex = (selected - 2).coerceAtLeast(0))
    LaunchedEffect(selected) {
        val visible = state.layoutInfo.visibleItemsInfo
        if (visible.none { it.index == selected }) {
            state.animateScrollToItem(selected)
        }
    }
    LazyColumn(modifier.heightIn(max = 180.dp), state = state) {
        items((0 until count).toList()) { value ->
            val isSelected = value == selected
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isSelected) colors.light(ElType.Primary, 9) else Color.Transparent)
                    .clickable { onSelect(value) },
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = value.toString().padStart(2, '0'),
                    style = theme.typography.small.copy(
                        color = if (isSelected) colors.primary else colors.textRegular,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    ),
                )
            }
        }
    }
}
