package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
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
import cn.enaium.elementplus.theme.ElLocale
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
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
import kotlinx.datetime.toInstant
import kotlinx.datetime.todayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/**
 * Date unit handled by [ElDatePicker] and [ElDatePickerPanel].
 *
 * `Dates`, `Months`, `Years` and `Quarters` select several periods at once and are
 * consumed by [ElDateMultiplePicker] / [ElDateMultiplePanel].
 */
enum class ElDatePickerType { Date, Week, Month, Quarter, Year, Dates, Months, Years, Quarters }

/** Range unit handled by [ElDateRangePicker] / [ElDateRangePanel]. */
enum class ElDateRangeType { Date, Month, Year, Quarter }

/** Position of a calendar cell relative to the displayed month. */
enum class ElDateCellType { PrevMonth, CurrentMonth, NextMonth }

/**
 * Description of a rendered calendar cell, passed to `cellContent` / `cellModifier`
 * so callers can mirror the `#default="{ cell }"` slot of Element Plus.
 */
data class ElDateCellInfo(
    val date: LocalDate,
    val text: String,
    val type: ElDateCellType = ElDateCellType.CurrentMonth,
    val isCurrent: Boolean = false,
    val isToday: Boolean = false,
    val disabled: Boolean = false,
    val isRangeStart: Boolean = false,
    val isRangeEnd: Boolean = false,
    val inRange: Boolean = false,
)

/**
 * Quick option rendered in the picker sidebar.
 *
 * Single-value pickers read [value]; range pickers read [start] and [end].
 */
data class ElDateShortcut(
    val text: String,
    val value: LocalDate? = null,
    val start: LocalDate? = null,
    val end: LocalDate? = null,
)

/** Quick option for [ElDateTimePicker] and [ElDateTimeRangePicker]. */
data class ElDateTimeShortcut(
    val text: String,
    val value: LocalDateTime? = null,
    val start: LocalDateTime? = null,
    val end: LocalDateTime? = null,
)

/** Current system date, used as the initial visible month. */
@Composable
internal fun rememberToday(): LocalDate = remember {
    Clock.System.todayIn(TimeZone.currentSystemDefault())
}

// ---------------------------------------------------------------------------
// Formatting
// ---------------------------------------------------------------------------

/** Month / weekday names used when formatting a pattern. */
internal class ElDateFormatNames(
    val months: List<String> = DefaultMonths,
    val monthsShort: List<String> = DefaultMonthsShort,
    val weekdays: List<String> = DefaultWeekdays,
    val weekdaysFull: List<String> = DefaultWeekdaysFull,
    val am: String = "AM",
    val pm: String = "PM",
)

private val DefaultMonths = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December",
)

private val DefaultMonthsShort = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
)

private val DefaultWeekdays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

private val DefaultWeekdaysFull = listOf(
    "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
)

private fun elDateFormatNames(locale: ElLocale): ElDateFormatNames = ElDateFormatNames(
    months = locale.datePicker.months,
    monthsShort = locale.datePicker.monthsShort,
    weekdays = locale.datePicker.weekdays,
    weekdaysFull = locale.datePicker.weekdaysFull,
    am = locale.timePicker.am,
    pm = locale.timePicker.pm,
)

fun formatLocalDate(date: LocalDate, pattern: String = "YYYY-MM-DD"): String =
    formatDatePattern(LocalDateTime(date, LocalTime(0, 0)), pattern)

fun formatLocalTime(time: LocalTime, pattern: String = "HH:mm:ss"): String =
    formatDatePattern(LocalDateTime(LocalDate(1970, 1, 1), time), pattern)

fun formatLocalDateTime(dateTime: LocalDateTime, pattern: String = "YYYY-MM-DD HH:mm:ss"): String =
    formatDatePattern(dateTime, pattern)

internal fun formatLocalDateNamed(
    date: LocalDate,
    pattern: String,
    names: ElDateFormatNames,
): String = formatDatePattern(LocalDateTime(date, LocalTime(0, 0)), pattern, names)

internal fun formatLocalDateTimeNamed(
    dateTime: LocalDateTime,
    pattern: String,
    names: ElDateFormatNames,
): String = formatDatePattern(dateTime, pattern, names)

/**
 * dayjs-flavoured formatter. Supported tokens: `YYYY`, `YY`, `M`/`MM`/`MMM`/`MMMM`,
 * `D`/`DD`, `d`/`dd`/`ddd`/`dddd`, `H`/`HH`, `h`/`hh`, `m`/`mm`, `s`/`ss`, `a`/`A`,
 * `Q`, `w`/`ww` and `x`/`X`. Text in `[brackets]` is copied verbatim.
 */
internal fun formatDatePattern(
    dateTime: LocalDateTime,
    pattern: String,
    names: ElDateFormatNames = ElDateFormatNames(),
): String {
    val out = StringBuilder()
    var i = 0
    while (i < pattern.length) {
        val c = pattern[i]
        if (c == '[') {
            val close = pattern.indexOf(']', i + 1)
            if (close > i) {
                out.append(pattern.substring(i + 1, close))
                i = close + 1
                continue
            }
        }
        if (c.isLetter()) {
            var j = i
            while (j < pattern.length && pattern[j] == c) j++
            out.append(formatToken(c, j - i, dateTime, names))
            i = j
            continue
        }
        out.append(c)
        i++
    }
    return out.toString()
}

private fun formatToken(token: Char, length: Int, dateTime: LocalDateTime, names: ElDateFormatNames): String {
    val date = dateTime.date
    val monthIndex = date.month.number - 1
    val weekdayIndex = date.dayOfWeek.isoDayNumber % 7
    return when (token) {
        'Y', 'y' -> when {
            length >= 4 -> date.year.toString().padStart(4, '0')
            length == 2 -> (date.year % 100).toString().padStart(2, '0')
            else -> date.year.toString()
        }
        'M' -> when (length) {
            1 -> date.month.number.toString()
            2 -> pad2(date.month.number)
            3 -> names.monthsShort.getOrElse(monthIndex) { "" }
            else -> names.months.getOrElse(monthIndex) { "" }
        }
        'D' -> if (length == 1) date.day.toString() else pad2(date.day)
        'd' -> when (length) {
            1 -> weekdayIndex.toString()
            2 -> names.weekdays.getOrElse(weekdayIndex) { "" }.take(2)
            3 -> names.weekdays.getOrElse(weekdayIndex) { "" }
            else -> names.weekdaysFull.getOrElse(weekdayIndex) { "" }
        }
        'H' -> if (length == 1) dateTime.hour.toString() else pad2(dateTime.hour)
        'h' -> {
            val hour12 = ((dateTime.hour + 11) % 12) + 1
            if (length == 1) hour12.toString() else pad2(hour12)
        }
        'm' -> if (length == 1) dateTime.minute.toString() else pad2(dateTime.minute)
        's' -> if (length == 1) dateTime.second.toString() else pad2(dateTime.second)
        'S' -> "000"
        'a', 'A' -> if (dateTime.hour < 12) names.am else names.pm
        'Q' -> quarterOf(date.month.number).toString()
        'w', 'W' -> {
            val week = isoWeekNumber(date)
            if (length >= 2) pad2(week) else week.toString()
        }
        'x' -> epochMilliseconds(dateTime).toString()
        'X' -> (epochMilliseconds(dateTime) / 1000).toString()
        else -> token.toString().repeat(length)
    }
}

private fun epochMilliseconds(dateTime: LocalDateTime): Long =
    dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()

private fun pad2(value: Int): String = value.toString().padStart(2, '0')

// ---------------------------------------------------------------------------
// Parsing (editable fields)
// ---------------------------------------------------------------------------

internal fun parseDatePattern(text: String, pattern: String): LocalDate? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null
    parseDateWithPattern(trimmed, pattern)?.let { return it }
    return listOf("YYYY-MM-DD", "YYYY/MM/DD", "YYYY.MM.DD", "YYYYMMDD")
        .firstNotNullOfOrNull { parseDateWithPattern(trimmed, it) }
}

internal fun parseLocalDateTimePattern(text: String, pattern: String): LocalDateTime? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null
    parseDateTimeWithPattern(trimmed, pattern)?.let { return it }
    parseDateTimeWithPattern(trimmed, "YYYY-MM-DD HH:mm:ss")?.let { return it }
    val datePart = trimmed.substringBefore(' ')
    val date = parseDatePattern(datePart, pattern) ?: parseDatePattern(trimmed, "YYYY-MM-DD") ?: return null
    val timePart = trimmed.substringAfter(' ', "")
    val time = parseTimeFallback(timePart) ?: LocalTime(0, 0)
    return LocalDateTime(date, time)
}

private fun parseTimeFallback(text: String): LocalTime? {
    if (text.isEmpty()) return null
    val match = Regex("(\\d{1,2}):(\\d{1,2})(?::(\\d{1,2}))?").matchEntire(text.trim()) ?: return null
    val hour = match.groupValues[1].toIntOrNull() ?: return null
    val minute = match.groupValues[2].toIntOrNull() ?: return null
    val second = match.groupValues[3].toIntOrNull() ?: 0
    return runCatching { LocalTime(hour, minute, second) }.getOrNull()
}

private class PatternRegex(val regex: Regex, val tokens: List<Char>)

private fun parseDateWithPattern(text: String, pattern: String): LocalDate? {
    val parsed = parseDateTimeWithPattern(text, pattern) ?: return null
    return parsed.date
}

private fun parseDateTimeWithPattern(text: String, pattern: String): LocalDateTime? {
    val built = buildPatternRegex(pattern) ?: return null
    val match = built.regex.matchEntire(text) ?: return null
    var year = 1970
    var month = 1
    var day = 1
    var hour = 0
    var minute = 0
    var second = 0
    var pm: Boolean? = null
    built.tokens.forEachIndexed { index, token ->
        val raw = match.groupValues.getOrElse(index + 1) { "" }
        val value = raw.toIntOrNull()
        when (token) {
            'Y' -> if (value != null) year = value
            'y' -> if (value != null) year = 2000 + value
            'M' -> if (value != null) month = value
            'D' -> if (value != null) day = value
            'H' -> if (value != null) hour = value
            'h' -> if (value != null) hour = value
            'm' -> if (value != null) minute = value
            's' -> if (value != null) second = value
            'a' -> pm = raw.contains('P', ignoreCase = true)
        }
    }
    if (pm == true && hour < 12) hour += 12
    if (pm == false && hour == 12) hour = 0
    return runCatching { LocalDateTime(LocalDate(year, month, day), LocalTime(hour, minute, second)) }.getOrNull()
}

private fun buildPatternRegex(pattern: String): PatternRegex? {
    val sb = StringBuilder("^")
    val tokens = mutableListOf<Char>()
    var i = 0
    while (i < pattern.length) {
        val c = pattern[i]
        if (c == '[') {
            val close = pattern.indexOf(']', i + 1)
            if (close > i) {
                sb.append(Regex.escape(pattern.substring(i + 1, close)))
                i = close + 1
                continue
            }
        }
        if (c.isLetter()) {
            var j = i
            while (j < pattern.length && pattern[j] == c) j++
            val length = j - i
            when (c) {
                'Y' -> {
                    sb.append(if (length >= 4) "(\\d{4})" else if (length == 2) "(\\d{2})" else "(\\d{1,4})")
                    tokens.add('Y')
                }
                'y' -> {
                    if (length >= 4) {
                        sb.append("(\\d{4})")
                        tokens.add('Y')
                    } else {
                        sb.append("(\\d{2})")
                        tokens.add('y')
                    }
                }
                'M', 'D', 'H', 'h', 'm', 's' -> {
                    sb.append("(\\d{1,2})")
                    tokens.add(c)
                }
                'a', 'A' -> {
                    sb.append("([AaPp][Mm])")
                    tokens.add('a')
                }
                'd' -> sb.append("[A-Za-z]+")
                else -> sb.append("\\d+")
            }
            i = j
            continue
        }
        if (c.isWhitespace()) sb.append("\\s+") else sb.append(Regex.escape(c.toString()))
        i++
    }
    sb.append("$")
    return runCatching { PatternRegex(Regex(sb.toString(), RegexOption.IGNORE_CASE), tokens) }.getOrNull()
}

// ---------------------------------------------------------------------------
// Date math helpers
// ---------------------------------------------------------------------------

internal fun isoWeekNumber(date: LocalDate): Int {
    val week = (date.dayOfYear - date.dayOfWeek.isoDayNumber + 10) / 7
    return when {
        week < 1 -> isoWeeksInYear(date.year - 1)
        week > isoWeeksInYear(date.year) -> 1
        else -> week
    }
}

private fun isoWeeksInYear(year: Int): Int {
    val jan1 = LocalDate(year, 1, 1).dayOfWeek.isoDayNumber
    val leap = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
    return if (jan1 == 4 || (jan1 == 3 && leap)) 53 else 52
}

private fun quarterOf(month: Int): Int = (month - 1) / 3 + 1

private fun quarterStart(year: Int, quarter: Int): LocalDate =
    LocalDate(year, (quarter - 1) * 3 + 1, 1)

private fun ElDatePickerType.rootView(): ElDatePickerType = when (this) {
    ElDatePickerType.Date, ElDatePickerType.Week, ElDatePickerType.Dates -> ElDatePickerType.Date
    ElDatePickerType.Month, ElDatePickerType.Months -> ElDatePickerType.Month
    ElDatePickerType.Quarter, ElDatePickerType.Quarters -> ElDatePickerType.Quarter
    ElDatePickerType.Year, ElDatePickerType.Years -> ElDatePickerType.Year
}

private fun ElDateRangeType.pickerType(): ElDatePickerType = when (this) {
    ElDateRangeType.Date -> ElDatePickerType.Date
    ElDateRangeType.Month -> ElDatePickerType.Month
    ElDateRangeType.Year -> ElDatePickerType.Year
    ElDateRangeType.Quarter -> ElDatePickerType.Quarter
}

internal fun alignToPeriod(date: LocalDate, type: ElDatePickerType): LocalDate = when (type) {
    ElDatePickerType.Month, ElDatePickerType.Months -> LocalDate(date.year, date.month.number, 1)
    ElDatePickerType.Quarter, ElDatePickerType.Quarters -> quarterStart(date.year, quarterOf(date.month.number))
    ElDatePickerType.Year, ElDatePickerType.Years -> LocalDate(date.year, 1, 1)
    else -> date
}

private fun shiftPanel(anchor: LocalDate, type: ElDatePickerType, steps: Int): LocalDate = when (type) {
    ElDatePickerType.Date -> anchor.plus(steps, DateTimeUnit.MONTH)
    ElDatePickerType.Year, ElDatePickerType.Years -> anchor.plus(steps * 10, DateTimeUnit.YEAR)
    else -> anchor.plus(steps, DateTimeUnit.YEAR)
}

private fun panelLabel(type: ElDatePickerType, anchor: LocalDate, locale: ElDatePickerLocaleRef): String = when (type) {
    ElDatePickerType.Date -> "${locale.months[anchor.month.number - 1]} ${anchor.year}"
    ElDatePickerType.Year, ElDatePickerType.Years -> {
        val start = anchor.year / 10 * 10
        "$start - ${start + 9}"
    }
    else -> anchor.year.toString()
}

private typealias ElDatePickerLocaleRef = cn.enaium.elementplus.theme.ElDatePickerLocale

// ---------------------------------------------------------------------------
// Panels
// ---------------------------------------------------------------------------

/**
 * `<el-date-picker-panel>`: calendar panel with day, week, month, quarter and year views.
 *
 * For `dates` / `months` / `years` / `quarters` use [ElDateMultiplePanel]; for range
 * types use [ElDateRangePanel] or [ElDateTimeRangePanel].
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
    defaultValue: LocalDate? = null,
    disabled: Boolean = false,
    border: Boolean = true,
    showWeekNumber: Boolean = false,
    cellContent: (@Composable (ElDateCellInfo) -> Unit)? = null,
    cellModifier: ((ElDateCellInfo) -> Modifier)? = null,
    prevIcon: ImageVector? = null,
    nextIcon: ImageVector? = null,
    prevYearIcon: ImageVector? = null,
    nextYearIcon: ImageVector? = null,
) {
    val today = rememberToday()
    var hovered by remember { mutableStateOf<LocalDate?>(null) }
    val selected = setOfNotNull(value?.let { alignToPeriod(it, type.rootView()) })
    Column(modifier.then(panelSurfaceModifier(border))) {
        CalendarPanelCore(
            type = type,
            selected = selected,
            hovered = hovered,
            today = today,
            initialMonth = initialMonth ?: defaultValue ?: value,
            minDate = minDate,
            maxDate = maxDate,
            disabledDate = disabledDate,
            disabled = disabled,
            showWeekNumber = showWeekNumber,
            cellContent = cellContent,
            cellModifier = cellModifier,
            prevIcon = prevIcon,
            nextIcon = nextIcon,
            prevYearIcon = prevYearIcon,
            nextYearIcon = nextYearIcon,
            showFooter = showFooter,
            onHover = { hovered = it },
            onSelect = onValueChange,
        )
    }
}

/**
 * Date panel that selects several periods: `Dates`, `Months`, `Years` or `Quarters`.
 */
@Composable
fun ElDateMultiplePanel(
    values: List<LocalDate>,
    onValueChange: (List<LocalDate>) -> Unit,
    modifier: Modifier = Modifier,
    type: ElDatePickerType = ElDatePickerType.Dates,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    disabledDate: ((LocalDate) -> Boolean)? = null,
    initialMonth: LocalDate? = null,
    showFooter: Boolean = true,
    disabled: Boolean = false,
    border: Boolean = true,
    showWeekNumber: Boolean = false,
    cellContent: (@Composable (ElDateCellInfo) -> Unit)? = null,
    cellModifier: ((ElDateCellInfo) -> Modifier)? = null,
    prevIcon: ImageVector? = null,
    nextIcon: ImageVector? = null,
    prevYearIcon: ImageVector? = null,
    nextYearIcon: ImageVector? = null,
) {
    val today = rememberToday()
    val root = type.rootView()
    val normalized = values.map { alignToPeriod(it, root) }
    Column(modifier.then(panelSurfaceModifier(border))) {
        CalendarPanelCore(
            type = type,
            selected = normalized.toSet(),
            today = today,
            initialMonth = initialMonth ?: values.firstOrNull(),
            minDate = minDate,
            maxDate = maxDate,
            disabledDate = disabledDate,
            disabled = disabled,
            showWeekNumber = showWeekNumber,
            cellContent = cellContent,
            cellModifier = cellModifier,
            prevIcon = prevIcon,
            nextIcon = nextIcon,
            prevYearIcon = prevYearIcon,
            nextYearIcon = nextYearIcon,
            showFooter = showFooter,
            onHover = {},
            onSelect = { picked ->
                val next = if (normalized.contains(picked)) {
                    values.filterNot { alignToPeriod(it, root) == picked }
                } else {
                    values + picked
                }
                onValueChange(next)
            },
        )
    }
}

/**
 * Range calendar panel for date, month, year and quarter ranges. Renders two linked
 * calendars, or a single one when [singlePanel] is set.
 */
@Composable
fun ElDateRangePanel(
    start: LocalDate?,
    end: LocalDate?,
    onValueChange: (LocalDate?, LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    type: ElDateRangeType = ElDateRangeType.Date,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    disabledDate: ((LocalDate) -> Boolean)? = null,
    initialMonth: LocalDate? = null,
    defaultValue: LocalDate? = null,
    defaultEndValue: LocalDate? = null,
    singlePanel: Boolean = false,
    unlinkPanels: Boolean = false,
    showFooter: Boolean = true,
    showWeekNumber: Boolean = false,
    disabled: Boolean = false,
    border: Boolean = true,
    cellContent: (@Composable (ElDateCellInfo) -> Unit)? = null,
    cellModifier: ((ElDateCellInfo) -> Modifier)? = null,
    prevIcon: ImageVector? = null,
    nextIcon: ImageVector? = null,
    prevYearIcon: ImageVector? = null,
    nextYearIcon: ImageVector? = null,
) {
    val today = rememberToday()
    val pickerType = type.pickerType()
    val base = initialMonth ?: defaultValue ?: start ?: end ?: today
    var left by remember(type) {
        mutableStateOf(alignToPeriod(base, pickerType.rootView()))
    }
    var right by remember(type) {
        mutableStateOf(alignToPeriod(defaultEndValue ?: end ?: shiftPanel(base, pickerType, 1), pickerType.rootView()))
    }
    var hovered by remember { mutableStateOf<LocalDate?>(null) }

    fun pick(date: LocalDate) {
        if (disabled) return
        val currentStart = start
        val currentEnd = end
        when {
            currentStart == null || currentEnd != null -> onValueChange(date, null)
            date < currentStart -> onValueChange(date, currentStart)
            else -> onValueChange(currentStart, date)
        }
    }

    val resolvedPrev = prevIcon ?: ElIcons.ArrowLeft
    val resolvedNext = nextIcon ?: ElIcons.ArrowRight
    val resolvedPrevYear = prevYearIcon ?: ElIcons.DArrowLeft
    val resolvedNextYear = nextYearIcon ?: ElIcons.DArrowRight
    fun moveLeftMonth(delta: Int) {
        left = left.plus(delta, DateTimeUnit.MONTH)
        if (!unlinkPanels) right = shiftPanel(left, pickerType, 1)
    }

    fun moveLeftYear(delta: Int) {
        left = if (pickerType == ElDatePickerType.Date) left.plus(delta * 12, DateTimeUnit.MONTH)
        else shiftPanel(left, pickerType, delta)
        if (!unlinkPanels) right = shiftPanel(left, pickerType, 1)
    }

    fun moveRightMonth(delta: Int) {
        if (unlinkPanels) right = right.plus(delta, DateTimeUnit.MONTH) else moveLeftMonth(delta)
    }

    fun moveRightYear(delta: Int) {
        if (unlinkPanels) {
            right = if (pickerType == ElDatePickerType.Date) right.plus(delta * 12, DateTimeUnit.MONTH)
            else shiftPanel(right, pickerType, delta)
        } else {
            moveLeftYear(delta)
        }
    }

    Column(modifier.then(panelSurfaceModifier(border))) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
            RangeCalendarUnit(
                pickerType = pickerType,
                anchor = left,
                rangeStart = start,
                rangeEnd = end,
                hovered = hovered,
                today = today,
                minDate = minDate,
                maxDate = maxDate,
                disabledDate = disabledDate,
                disabled = disabled,
                showWeekNumber = showWeekNumber,
                cellContent = cellContent,
                cellModifier = cellModifier,
                prevIcon = resolvedPrev,
                nextIcon = resolvedNext,
                prevYearIcon = resolvedPrevYear,
                nextYearIcon = resolvedNextYear,
                onPrevMonth = { moveLeftMonth(-1) },
                onNextMonth = { moveLeftMonth(1) },
                onPrevYear = { moveLeftYear(-1) },
                onNextYear = { moveLeftYear(1) },
                onHover = { hovered = it },
                onPick = ::pick,
            )
            if (!singlePanel) {
                RangeCalendarUnit(
                    pickerType = pickerType,
                    anchor = right,
                    rangeStart = start,
                    rangeEnd = end,
                    hovered = hovered,
                    today = today,
                    minDate = minDate,
                    maxDate = maxDate,
                    disabledDate = disabledDate,
                    disabled = disabled,
                    showWeekNumber = showWeekNumber,
                    cellContent = cellContent,
                    cellModifier = cellModifier,
                    prevIcon = resolvedPrev,
                    nextIcon = resolvedNext,
                    prevYearIcon = resolvedPrevYear,
                    nextYearIcon = resolvedNextYear,
                    onPrevMonth = { moveRightMonth(-1) },
                    onNextMonth = { moveRightMonth(1) },
                    onPrevYear = { moveRightYear(-1) },
                    onNextYear = { moveRightYear(1) },
                    onHover = { hovered = it },
                    onPick = ::pick,
                )
            }
        }
        if (showFooter) {
            PanelFooter(
                rangeText = listOfNotNull(start, end).joinToString(" - ") { formatLocalDate(it) }
                    .ifBlank { null },
                showToday = !disabled,
                onToday = {
                    onValueChange(today, null)
                    left = alignToPeriod(today, pickerType.rootView())
                    if (!unlinkPanels) right = shiftPanel(left, pickerType, 1)
                },
            )
        }
    }
}

/** Date and time panel used by [ElDateTimePicker]. */
@Composable
fun ElDateTimePickerPanel(
    value: LocalDateTime?,
    onValueChange: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier,
    showSeconds: Boolean = true,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    disabledDate: ((LocalDate) -> Boolean)? = null,
    defaultTime: LocalTime? = null,
    disabledHours: ((Int) -> Boolean)? = null,
    disabledMinutes: ((Int) -> Boolean)? = null,
    disabledSeconds: ((Int) -> Boolean)? = null,
    showFooter: Boolean = false,
    dateFormat: String = "YYYY-MM-DD",
    timeFormat: String = "HH:mm:ss",
    disabled: Boolean = false,
    border: Boolean = true,
    cellContent: (@Composable (ElDateCellInfo) -> Unit)? = null,
    cellModifier: ((ElDateCellInfo) -> Modifier)? = null,
    prevIcon: ImageVector? = null,
    nextIcon: ImageVector? = null,
    prevYearIcon: ImageVector? = null,
    nextYearIcon: ImageVector? = null,
) {
    val today = rememberToday()
    val locale = LocalElConfig.current.locale
    var date by remember(value) { mutableStateOf(value?.date ?: today) }
    var time by remember(value) {
        mutableStateOf(value?.time ?: defaultTime ?: LocalTime(0, 0))
    }
    Column(modifier.then(panelSurfaceModifier(border))) {
        CalendarPanelCore(
            type = ElDatePickerType.Date,
            selected = setOf(date),
            today = today,
            initialMonth = date,
            minDate = minDate,
            maxDate = maxDate,
            disabledDate = disabledDate,
            disabled = disabled,
            cellContent = cellContent,
            cellModifier = cellModifier,
            prevIcon = prevIcon,
            nextIcon = nextIcon,
            prevYearIcon = prevYearIcon,
            nextYearIcon = nextYearIcon,
            onSelect = { picked ->
                date = picked
                onValueChange(LocalDateTime(picked, time))
            },
        )
        Row(
            Modifier.fillMaxWidth().height(180.dp).padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TimeSpinner(24, time.hour, Modifier.weight(1f), disabledHours) { hour ->
                time = LocalTime(hour, time.minute, time.second)
                onValueChange(LocalDateTime(date, time))
            }
            TimeSpinner(60, time.minute, Modifier.weight(1f), disabledMinutes) { minute ->
                time = LocalTime(time.hour, minute, time.second)
                onValueChange(LocalDateTime(date, time))
            }
            if (showSeconds) {
                TimeSpinner(60, time.second, Modifier.weight(1f), disabledSeconds) { second ->
                    time = LocalTime(time.hour, time.minute, second)
                    onValueChange(LocalDateTime(date, time))
                }
            }
        }
        if (showFooter) {
            val names = remember(locale) { elDateFormatNames(locale) }
            PanelFooter(
                rangeText = formatDatePattern(LocalDateTime(date, time), "$dateFormat $timeFormat", names),
                showToday = !disabled,
                onToday = {
                    date = today
                    time = defaultTime ?: LocalTime(0, 0)
                    onValueChange(LocalDateTime(date, time))
                },
            )
        }
    }
}

/** Date and time range panel used by [ElDateTimeRangePicker]. */
@Composable
fun ElDateTimeRangePanel(
    start: LocalDateTime?,
    end: LocalDateTime?,
    onValueChange: (LocalDateTime?, LocalDateTime?) -> Unit,
    modifier: Modifier = Modifier,
    singlePanel: Boolean = false,
    showSeconds: Boolean = true,
    dateFormat: String = "YYYY-MM-DD",
    timeFormat: String = "HH:mm:ss",
    defaultStartTime: LocalTime = LocalTime(0, 0),
    defaultEndTime: LocalTime = LocalTime(23, 59, 59),
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    disabledDate: ((LocalDate) -> Boolean)? = null,
    disabledHours: ((Int) -> Boolean)? = null,
    disabledMinutes: ((Int) -> Boolean)? = null,
    disabledSeconds: ((Int) -> Boolean)? = null,
    showFooter: Boolean = true,
    disabled: Boolean = false,
    border: Boolean = true,
    cellContent: (@Composable (ElDateCellInfo) -> Unit)? = null,
    cellModifier: ((ElDateCellInfo) -> Modifier)? = null,
    prevIcon: ImageVector? = null,
    nextIcon: ImageVector? = null,
    prevYearIcon: ImageVector? = null,
    nextYearIcon: ImageVector? = null,
) {
    val today = rememberToday()
    val locale = LocalElConfig.current.locale
    var left by remember { mutableStateOf(alignToPeriod(start?.date ?: today, ElDatePickerType.Date)) }
    var right by remember {
        mutableStateOf(
            alignToPeriod(end?.date ?: start?.date ?: today, ElDatePickerType.Date)
                .plus(1, DateTimeUnit.MONTH),
        )
    }
    var hovered by remember { mutableStateOf<LocalDate?>(null) }
    var startTime by remember { mutableStateOf(start?.time ?: defaultStartTime) }
    var endTime by remember { mutableStateOf(end?.time ?: defaultEndTime) }
    LaunchedEffect(start) { start?.let { startTime = it.time } }
    LaunchedEffect(end) { end?.let { endTime = it.time } }

    fun pick(date: LocalDate) {
        if (disabled) return
        val currentStart = start
        val currentEnd = end
        when {
            currentStart == null || currentEnd != null ->
                onValueChange(LocalDateTime(date, startTime), null)
            LocalDateTime(date, endTime) < currentStart ->
                onValueChange(LocalDateTime(date, startTime), currentStart)
            else ->
                onValueChange(currentStart, LocalDateTime(date, endTime))
        }
    }

    val resolvedPrev = prevIcon ?: ElIcons.ArrowLeft
    val resolvedNext = nextIcon ?: ElIcons.ArrowRight
    val resolvedPrevYear = prevYearIcon ?: ElIcons.DArrowLeft
    val resolvedNextYear = nextYearIcon ?: ElIcons.DArrowRight
    val names = remember(locale) { elDateFormatNames(locale) }

    Column(modifier.then(panelSurfaceModifier(border))) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
            RangeCalendarUnit(
                pickerType = ElDatePickerType.Date,
                anchor = left,
                rangeStart = start?.date,
                rangeEnd = end?.date,
                hovered = hovered,
                today = today,
                minDate = minDate,
                maxDate = maxDate,
                disabledDate = disabledDate,
                disabled = disabled,
                showWeekNumber = false,
                cellContent = cellContent,
                cellModifier = cellModifier,
                prevIcon = resolvedPrev,
                nextIcon = resolvedNext,
                prevYearIcon = resolvedPrevYear,
                nextYearIcon = resolvedNextYear,
                onPrevMonth = { left = left.minus(1, DateTimeUnit.MONTH); right = left.plus(1, DateTimeUnit.MONTH) },
                onNextMonth = { left = left.plus(1, DateTimeUnit.MONTH); right = left.plus(1, DateTimeUnit.MONTH) },
                onPrevYear = { left = left.minus(1, DateTimeUnit.YEAR); right = left.plus(1, DateTimeUnit.MONTH) },
                onNextYear = { left = left.plus(1, DateTimeUnit.YEAR); right = left.plus(1, DateTimeUnit.MONTH) },
                onHover = { hovered = it },
                onPick = ::pick,
            )
            if (!singlePanel) {
                RangeCalendarUnit(
                    pickerType = ElDatePickerType.Date,
                    anchor = right,
                    rangeStart = start?.date,
                    rangeEnd = end?.date,
                    hovered = hovered,
                    today = today,
                    minDate = minDate,
                    maxDate = maxDate,
                    disabledDate = disabledDate,
                    disabled = disabled,
                    showWeekNumber = false,
                    cellContent = cellContent,
                    cellModifier = cellModifier,
                    prevIcon = resolvedPrev,
                    nextIcon = resolvedNext,
                    prevYearIcon = resolvedPrevYear,
                    nextYearIcon = resolvedNextYear,
                    onPrevMonth = { left = left.minus(1, DateTimeUnit.MONTH); right = left.plus(1, DateTimeUnit.MONTH) },
                    onNextMonth = { left = left.plus(1, DateTimeUnit.MONTH); right = left.plus(1, DateTimeUnit.MONTH) },
                    onPrevYear = { left = left.minus(1, DateTimeUnit.YEAR); right = left.plus(1, DateTimeUnit.MONTH) },
                    onNextYear = { left = left.plus(1, DateTimeUnit.YEAR); right = left.plus(1, DateTimeUnit.MONTH) },
                    onHover = { hovered = it },
                    onPick = ::pick,
                )
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DateTimeSpinnerGroup(
                label = locale.datePicker.startTime,
                time = startTime,
                showSeconds = showSeconds,
                disabledHours = disabledHours,
                disabledMinutes = disabledMinutes,
                disabledSeconds = disabledSeconds,
                modifier = Modifier.weight(1f),
            ) { next ->
                startTime = next
                start?.let { current -> onValueChange(LocalDateTime(current.date, next), end) }
            }
            DateTimeSpinnerGroup(
                label = locale.datePicker.endTime,
                time = endTime,
                showSeconds = showSeconds,
                disabledHours = disabledHours,
                disabledMinutes = disabledMinutes,
                disabledSeconds = disabledSeconds,
                modifier = Modifier.weight(1f),
            ) { next ->
                endTime = next
                end?.let { current -> onValueChange(start, LocalDateTime(current.date, next)) }
            }
        }
        if (showFooter) {
            PanelFooter(
                rangeText = listOfNotNull(start, end)
                    .joinToString(" - ") { formatDatePattern(it, "$dateFormat $timeFormat", names) }
                    .ifBlank { null },
                showToday = !disabled,
                onToday = {
                    onValueChange(LocalDateTime(today, startTime), null)
                    left = today
                    right = today.plus(1, DateTimeUnit.MONTH)
                },
            )
        }
    }
}

@Composable
private fun DateTimeSpinnerGroup(
    label: String,
    time: LocalTime,
    showSeconds: Boolean,
    disabledHours: ((Int) -> Boolean)?,
    disabledMinutes: ((Int) -> Boolean)?,
    disabledSeconds: ((Int) -> Boolean)?,
    modifier: Modifier = Modifier,
    onTimeChange: (LocalTime) -> Unit,
) {
    val theme = LocalElTheme.current
    Column(modifier) {
        BasicText(
            text = label,
            style = theme.typography.extraSmall.copy(color = theme.colors.textSecondary),
        )
        Row(
            Modifier.fillMaxWidth().height(140.dp).padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            TimeSpinner(24, time.hour, Modifier.weight(1f), disabledHours) { hour ->
                onTimeChange(LocalTime(hour, time.minute, time.second))
            }
            TimeSpinner(60, time.minute, Modifier.weight(1f), disabledMinutes) { minute ->
                onTimeChange(LocalTime(time.hour, minute, time.second))
            }
            if (showSeconds) {
                TimeSpinner(60, time.second, Modifier.weight(1f), disabledSeconds) { second ->
                    onTimeChange(LocalTime(time.hour, time.minute, second))
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Calendar internals
// ---------------------------------------------------------------------------

@Composable
private fun panelSurfaceModifier(border: Boolean): Modifier {
    if (!border) return Modifier
    val colors = LocalElTheme.current.colors
    val shape = RoundedCornerShape(4.dp)
    return Modifier
        .background(colors.backgroundOverlay, shape)
        .border(1.dp, colors.borderLight, shape)
        .padding(horizontal = 12.dp, vertical = 10.dp)
}

@Composable
private fun CalendarPanelCore(
    type: ElDatePickerType,
    selected: Set<LocalDate>,
    today: LocalDate,
    modifier: Modifier = Modifier,
    rangeStart: LocalDate? = null,
    rangeEnd: LocalDate? = null,
    hovered: LocalDate? = null,
    initialMonth: LocalDate? = null,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    disabledDate: ((LocalDate) -> Boolean)? = null,
    disabled: Boolean = false,
    showWeekNumber: Boolean = false,
    cellContent: (@Composable (ElDateCellInfo) -> Unit)? = null,
    cellModifier: ((ElDateCellInfo) -> Modifier)? = null,
    prevIcon: ImageVector? = null,
    nextIcon: ImageVector? = null,
    prevYearIcon: ImageVector? = null,
    nextYearIcon: ImageVector? = null,
    showFooter: Boolean = false,
    footerRangeText: String? = null,
    showConfirm: Boolean = false,
    onConfirm: (() -> Unit)? = null,
    onHover: (LocalDate?) -> Unit = {},
    onSelect: (LocalDate) -> Unit,
) {
    val locale = LocalElConfig.current.locale
    val root = type.rootView()
    var visible by remember(type) {
        mutableStateOf(alignToPeriod(initialMonth ?: today, root))
    }
    var stage by remember(type) { mutableStateOf(root) }

    fun canSelect(date: LocalDate): Boolean =
        !disabled &&
            (minDate == null || date >= minDate) &&
            (maxDate == null || date <= maxDate) &&
            (disabledDate?.invoke(date) != true)

    fun shiftYears(steps: Int) {
        visible = if (stage == ElDatePickerType.Year) visible.plus(steps * 10, DateTimeUnit.YEAR)
        else visible.plus(steps, DateTimeUnit.YEAR)
    }

    Column(modifier.width(292.dp)) {
        PanelHeader(
            label = panelLabel(stage, visible, locale.datePicker),
            showMonthArrows = stage == ElDatePickerType.Date,
            onPrevYear = { shiftYears(-1) },
            onNextYear = { shiftYears(1) },
            onPrevMonth = if (stage == ElDatePickerType.Date) ({ visible = visible.minus(1, DateTimeUnit.MONTH) }) else null,
            onNextMonth = if (stage == ElDatePickerType.Date) ({ visible = visible.plus(1, DateTimeUnit.MONTH) }) else null,
            onToggle = if (stage == ElDatePickerType.Year && root == ElDatePickerType.Year) null else ({
                stage = when (stage) {
                    ElDatePickerType.Date -> ElDatePickerType.Month
                    ElDatePickerType.Month, ElDatePickerType.Quarter -> ElDatePickerType.Year
                    ElDatePickerType.Year -> when (root) {
                        ElDatePickerType.Year -> ElDatePickerType.Year
                        ElDatePickerType.Quarter -> ElDatePickerType.Quarter
                        else -> ElDatePickerType.Month
                    }
                    else -> stage
                }
            }),
            prevIcon = prevYearIcon ?: ElIcons.DArrowLeft,
            nextIcon = nextYearIcon ?: ElIcons.DArrowRight,
            prevMonthIcon = prevIcon ?: ElIcons.ArrowLeft,
            nextMonthIcon = nextIcon ?: ElIcons.ArrowRight,
        )
        when (stage) {
            ElDatePickerType.Date -> {
                WeekdayHeader(locale, showWeekNumber || type == ElDatePickerType.Week)
                DayGrid(
                    visible = visible,
                    selected = selected,
                    rangeStart = rangeStart,
                    rangeEnd = rangeEnd,
                    hovered = hovered,
                    today = today,
                    weekSelection = type == ElDatePickerType.Week,
                    showWeekNumber = showWeekNumber || type == ElDatePickerType.Week,
                    minDate = minDate,
                    maxDate = maxDate,
                    disabledDate = disabledDate,
                    disabled = disabled,
                    cellContent = cellContent,
                    cellModifier = cellModifier,
                    onHover = onHover,
                    onSelect = onSelect,
                )
            }
            ElDatePickerType.Month, ElDatePickerType.Quarter, ElDatePickerType.Year -> {
                PeriodGrid(
                    type = stage,
                    anchor = visible,
                    selected = selected,
                    rangeStart = rangeStart,
                    rangeEnd = rangeEnd,
                    hovered = hovered,
                    today = today,
                    minDate = minDate,
                    maxDate = maxDate,
                    disabledDate = disabledDate,
                    disabled = disabled,
                    cellContent = cellContent,
                    cellModifier = cellModifier,
                    onSelect = { picked ->
                        val isRoot = stage == root
                        when {
                            isRoot -> onSelect(picked)
                            stage == ElDatePickerType.Month -> {
                                visible = picked
                                stage = ElDatePickerType.Date
                            }
                            stage == ElDatePickerType.Year -> {
                                visible = picked
                                stage = if (root == ElDatePickerType.Quarter) ElDatePickerType.Quarter else ElDatePickerType.Month
                            }
                            else -> onSelect(picked)
                        }
                    },
                )
            }
            else -> Unit
        }
        if (showFooter) {
            PanelFooter(
                rangeText = footerRangeText,
                showToday = !disabled,
                onToday = {
                    val target = alignToPeriod(today, root)
                    visible = target
                    stage = root
                    if (canSelect(target)) onSelect(target)
                },
                confirmText = if (showConfirm) locale.datePicker.confirm else null,
                onConfirm = onConfirm,
            )
        }
    }
}

@Composable
private fun RangeCalendarUnit(
    pickerType: ElDatePickerType,
    anchor: LocalDate,
    rangeStart: LocalDate?,
    rangeEnd: LocalDate?,
    hovered: LocalDate?,
    today: LocalDate,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    disabledDate: ((LocalDate) -> Boolean)?,
    disabled: Boolean,
    showWeekNumber: Boolean,
    cellContent: (@Composable (ElDateCellInfo) -> Unit)?,
    cellModifier: ((ElDateCellInfo) -> Modifier)?,
    prevIcon: ImageVector,
    nextIcon: ImageVector,
    prevYearIcon: ImageVector,
    nextYearIcon: ImageVector,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onPrevYear: () -> Unit,
    onNextYear: () -> Unit,
    onHover: (LocalDate?) -> Unit,
    onPick: (LocalDate) -> Unit,
) {
    val locale = LocalElConfig.current.locale
    Column(Modifier.width(292.dp)) {
        PanelHeader(
            label = panelLabel(pickerType, anchor, locale.datePicker),
            showMonthArrows = pickerType == ElDatePickerType.Date,
            onPrevYear = onPrevYear,
            onNextYear = onNextYear,
            onPrevMonth = if (pickerType == ElDatePickerType.Date) onPrevMonth else null,
            onNextMonth = if (pickerType == ElDatePickerType.Date) onNextMonth else null,
            prevIcon = prevYearIcon,
            nextIcon = nextYearIcon,
            prevMonthIcon = prevIcon,
            nextMonthIcon = nextIcon,
        )
        when (pickerType) {
            ElDatePickerType.Date -> {
                WeekdayHeader(locale, showWeekNumber)
                DayGrid(
                    visible = anchor,
                    selected = emptySet(),
                    rangeStart = rangeStart,
                    rangeEnd = rangeEnd,
                    hovered = hovered,
                    today = today,
                    weekSelection = false,
                    showWeekNumber = showWeekNumber,
                    minDate = minDate,
                    maxDate = maxDate,
                    disabledDate = disabledDate,
                    disabled = disabled,
                    cellContent = cellContent,
                    cellModifier = cellModifier,
                    onHover = onHover,
                    onSelect = onPick,
                )
            }
            else -> PeriodGrid(
                type = pickerType,
                anchor = anchor,
                selected = emptySet(),
                rangeStart = rangeStart,
                rangeEnd = rangeEnd,
                hovered = hovered,
                today = today,
                minDate = minDate,
                maxDate = maxDate,
                disabledDate = disabledDate,
                disabled = disabled,
                cellContent = cellContent,
                cellModifier = cellModifier,
                onSelect = onPick,
            )
        }
    }
}

@Composable
private fun PanelHeader(
    label: String,
    showMonthArrows: Boolean,
    onPrevYear: () -> Unit,
    onNextYear: () -> Unit,
    onPrevMonth: (() -> Unit)? = null,
    onNextMonth: (() -> Unit)? = null,
    onToggle: (() -> Unit)? = null,
    prevIcon: ImageVector = ElIcons.DArrowLeft,
    nextIcon: ImageVector = ElIcons.DArrowRight,
    prevMonthIcon: ImageVector = ElIcons.ArrowLeft,
    nextMonthIcon: ImageVector = ElIcons.ArrowRight,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    Row(Modifier.fillMaxWidth().height(28.dp), verticalAlignment = Alignment.CenterVertically) {
        HeaderIcon(prevIcon, locale.datePicker.prevYear, onPrevYear)
        if (showMonthArrows && onPrevMonth != null) {
            HeaderIcon(prevMonthIcon, locale.datePicker.prevMonth, onPrevMonth)
        }
        Spacer(Modifier.weight(1f))
        BasicText(
            text = label,
            modifier = Modifier
                .then(if (onToggle != null) Modifier.clickable(onClick = onToggle) else Modifier)
                .padding(horizontal = 6.dp),
            style = theme.typography.small.copy(color = colors.textPrimary, fontWeight = FontWeight.Medium),
        )
        Spacer(Modifier.weight(1f))
        if (showMonthArrows && onNextMonth != null) {
            HeaderIcon(nextMonthIcon, locale.datePicker.nextMonth, onNextMonth)
        }
        HeaderIcon(nextIcon, locale.datePicker.nextYear, onNextYear)
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
private fun WeekdayHeader(locale: ElLocale, showWeekNumber: Boolean = false) {
    Row(Modifier.fillMaxWidth()) {
        if (showWeekNumber) Spacer(Modifier.width(26.dp))
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
private fun DayGrid(
    visible: LocalDate,
    selected: Set<LocalDate>,
    rangeStart: LocalDate?,
    rangeEnd: LocalDate?,
    hovered: LocalDate?,
    today: LocalDate,
    weekSelection: Boolean,
    showWeekNumber: Boolean,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    disabledDate: ((LocalDate) -> Boolean)?,
    disabled: Boolean,
    cellContent: (@Composable (ElDateCellInfo) -> Unit)?,
    cellModifier: ((ElDateCellInfo) -> Modifier)?,
    onHover: (LocalDate?) -> Unit,
    onSelect: (LocalDate) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val first = LocalDate(visible.year, visible.month.number, 1)
    val offset = first.dayOfWeek.isoDayNumber % 7
    val startDate = first.minus(offset, DateTimeUnit.DAY)
    val previewEnd = if (rangeStart != null && rangeEnd == null) hovered else null
    val effectiveEnd = rangeEnd ?: previewEnd
    val rangeLow = listOfNotNull(rangeStart, effectiveEnd).minOrNull()
    val rangeHigh = listOfNotNull(rangeStart, effectiveEnd).maxOrNull()

    fun canSelect(date: LocalDate): Boolean =
        !disabled &&
            (minDate == null || date >= minDate) &&
            (maxDate == null || date <= maxDate) &&
            (disabledDate?.invoke(date) != true)

    Column {
        repeat(6) { week ->
            val weekStart = startDate.plus(week * 7, DateTimeUnit.DAY)
            val weekEnd = weekStart.plus(6, DateTimeUnit.DAY)
            val weekSelected = weekSelection && selected.any { it >= weekStart && it <= weekEnd }
            Row(Modifier.fillMaxWidth().height(32.dp)) {
                if (showWeekNumber) {
                    val weekReference = weekStart.plus(1, DateTimeUnit.DAY)
                    val weekNumber = isoWeekNumber(weekReference)
                    Box(
                        Modifier
                            .width(26.dp)
                            .fillMaxHeight()
                            .clickable(enabled = weekSelection && !disabled) { onSelect(weekReference) },
                        contentAlignment = Alignment.Center,
                    ) {
                        BasicText(
                            text = weekNumber.toString(),
                            style = theme.typography.extraSmall.copy(
                                color = if (weekSelected) colors.primary else colors.textSecondary,
                                fontWeight = if (weekSelected) FontWeight.Medium else FontWeight.Normal,
                            ),
                        )
                    }
                }
                repeat(7) { dayIndex ->
                    val date = weekStart.plus(dayIndex, DateTimeUnit.DAY)
                    val inMonth = date.month.number == visible.month.number && date.year == visible.year
                    val selectedCell = date in selected
                    val isStart = date == rangeStart
                    val isEnd = date == rangeEnd
                    val inRange = rangeLow != null && rangeHigh != null && date > rangeLow && date < rangeHigh
                    val enabled = canSelect(date)
                    val interaction = remember(date) { MutableInteractionSource() }
                    val hoverState = rememberElInteractionState(interaction).hovered
                    LaunchedEffect(hoverState) {
                        if (hoverState) onHover(date) else if (hovered == date) onHover(null)
                    }
                    val info = ElDateCellInfo(
                        date = date,
                        text = date.day.toString(),
                        type = when {
                            inMonth -> ElDateCellType.CurrentMonth
                            date < first -> ElDateCellType.PrevMonth
                            else -> ElDateCellType.NextMonth
                        },
                        isCurrent = selectedCell || isStart || isEnd,
                        isToday = date == today,
                        disabled = !enabled,
                        isRangeStart = isStart,
                        isRangeEnd = isEnd,
                        inRange = inRange,
                    )
                    Box(
                        Modifier
                            .weight(1f)
                            .height(32.dp)
                            .padding(1.dp)
                            .then(cellModifier?.invoke(info) ?: Modifier)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                when {
                                    selectedCell || isStart || isEnd -> colors.primary
                                    inRange || weekSelected -> colors.light(ElType.Primary, 9)
                                    hoverState && enabled -> colors.fillLight
                                    else -> Color.Transparent
                                }
                            )
                            .hoverable(interaction)
                            .clickable(enabled = enabled) { onSelect(date) },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (cellContent != null) {
                            cellContent(info)
                        } else {
                            BasicText(
                                text = date.day.toString(),
                                style = theme.typography.small.copy(
                                    color = when {
                                        !inMonth -> colors.textDisabled
                                        !enabled -> colors.textDisabled
                                        selectedCell || isStart || isEnd -> colors.white
                                        date == today -> colors.primary
                                        else -> colors.textRegular
                                    },
                                    fontWeight = if (date == today || selectedCell) FontWeight.Medium else FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class PeriodCell(val date: LocalDate, val text: String)

@Composable
private fun PeriodGrid(
    type: ElDatePickerType,
    anchor: LocalDate,
    selected: Set<LocalDate>,
    rangeStart: LocalDate?,
    rangeEnd: LocalDate?,
    hovered: LocalDate?,
    today: LocalDate,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    disabledDate: ((LocalDate) -> Boolean)?,
    disabled: Boolean,
    cellContent: (@Composable (ElDateCellInfo) -> Unit)?,
    cellModifier: ((ElDateCellInfo) -> Modifier)?,
    onSelect: (LocalDate) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val cells = periodCells(type, anchor, locale.datePicker)
    val columns = if (type == ElDatePickerType.Quarter) 2 else 4
    val cellHeight = if (type == ElDatePickerType.Quarter) 56.dp else 52.dp
    val previewEnd = if (rangeStart != null && rangeEnd == null) hovered else null
    val effectiveEnd = rangeEnd ?: previewEnd
    val rangeLow = listOfNotNull(rangeStart, effectiveEnd).minOrNull()
    val rangeHigh = listOfNotNull(rangeStart, effectiveEnd).maxOrNull()

    fun periodEnd(date: LocalDate): LocalDate = when (type) {
        ElDatePickerType.Month, ElDatePickerType.Months ->
            date.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
        ElDatePickerType.Quarter, ElDatePickerType.Quarters ->
            date.plus(3, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
        else -> LocalDate(date.year, 12, 31)
    }

    Column(Modifier.fillMaxWidth()) {
        cells.chunked(columns).forEach { row ->
            Row(Modifier.fillMaxWidth().height(cellHeight)) {
                row.forEach { cell ->
                    val cellEnd = periodEnd(cell.date)
                    val enabled = !disabled &&
                        (minDate == null || cellEnd >= minDate) &&
                        (maxDate == null || cell.date <= maxDate) &&
                        (disabledDate?.invoke(cell.date) != true)
                    val isSelected = cell.date in selected || cell.date == rangeStart || cell.date == rangeEnd
                    val inRange = rangeLow != null && rangeHigh != null &&
                        cell.date > rangeLow && cell.date < rangeHigh
                    val info = ElDateCellInfo(
                        date = cell.date,
                        text = cell.text,
                        isCurrent = isSelected,
                        isToday = cell.date <= today && periodEnd(cell.date) >= today,
                        disabled = !enabled,
                        isRangeStart = cell.date == rangeStart,
                        isRangeEnd = cell.date == rangeEnd,
                        inRange = inRange,
                    )
                    Box(
                        Modifier
                            .weight(1f)
                            .padding(2.dp)
                            .then(cellModifier?.invoke(info) ?: Modifier)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when {
                                    isSelected -> colors.primary
                                    inRange -> colors.light(ElType.Primary, 9)
                                    else -> Color.Transparent
                                }
                            )
                            .clickable(enabled = enabled) { onSelect(cell.date) },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (cellContent != null) {
                            cellContent(info)
                        } else {
                            BasicText(
                                text = cell.text,
                                style = theme.typography.small.copy(
                                    color = when {
                                        !enabled -> colors.textDisabled
                                        isSelected -> colors.white
                                        else -> colors.textRegular
                                    },
                                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                ),
                            )
                        }
                    }
                }
                repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

private fun periodCells(
    type: ElDatePickerType,
    anchor: LocalDate,
    locale: ElDatePickerLocaleRef,
): List<PeriodCell> = when (type) {
    ElDatePickerType.Month, ElDatePickerType.Months ->
        (1..12).map { month -> PeriodCell(LocalDate(anchor.year, month, 1), locale.monthsShort[month - 1]) }
    ElDatePickerType.Quarter, ElDatePickerType.Quarters ->
        (1..4).map { quarter -> PeriodCell(quarterStart(anchor.year, quarter), "Q$quarter") }
    ElDatePickerType.Year, ElDatePickerType.Years -> {
        val startYear = anchor.year / 10 * 10
        (0 until 10).map { offset ->
            val year = startYear + offset
            PeriodCell(LocalDate(year, 1, 1), year.toString())
        }
    }
    else -> emptyList()
}

@Composable
private fun PanelFooter(
    rangeText: String? = null,
    showToday: Boolean = true,
    onToday: (() -> Unit)? = null,
    confirmText: String? = null,
    onConfirm: (() -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    Row(
        Modifier.fillMaxWidth().padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (rangeText != null) {
            BasicText(
                text = rangeText,
                style = theme.typography.extraSmall.copy(color = colors.textSecondary),
            )
        }
        Spacer(Modifier.weight(1f))
        if (showToday && onToday != null) {
            BasicText(
                text = locale.datePicker.today,
                modifier = Modifier.clickable(onClick = onToday).padding(horizontal = 4.dp, vertical = 2.dp),
                style = theme.typography.small.copy(color = colors.primary),
            )
        }
        if (confirmText != null && onConfirm != null) {
            Spacer(Modifier.width(8.dp))
            BasicText(
                text = confirmText,
                modifier = Modifier.clickable(onClick = onConfirm).padding(horizontal = 4.dp, vertical = 2.dp),
                style = theme.typography.small.copy(color = colors.primary),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Fields
// ---------------------------------------------------------------------------

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

@Composable
private fun DateEditorField(
    display: String?,
    placeholder: String?,
    onClick: () -> Unit,
    onClear: (() -> Unit)?,
    modifier: Modifier = Modifier,
    size: ElSize? = null,
    disabled: Boolean = false,
    icon: ImageVector = ElIcons.Calendar,
    clearIcon: ImageVector = ElIcons.CircleClose,
    prefixIcon: ImageVector? = null,
    editable: Boolean = false,
    readOnly: Boolean = false,
    onCommitText: ((String) -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedSize = resolveElSize(size)
    val shape = RoundedCornerShape(theme.radius.base)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val textStyle = theme.typography.base.copy(
        fontSize = resolvedSize.fontSize,
        color = if (disabled) colors.disabledText else colors.textRegular,
    )
    var text by remember { mutableStateOf(display ?: "") }
    LaunchedEffect(display) { text = display ?: "" }
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
        if (prefixIcon != null) {
            ElIcon(prefixIcon, size = 14.dp, tint = colors.textPlaceholder)
            Spacer(Modifier.width(6.dp))
        }
        if (editable && !readOnly && !disabled) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { if (it.isFocused) onClick() }
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown && event.key == Key.Enter) {
                            onCommitText?.invoke(text)
                            true
                        } else {
                            false
                        }
                    },
                textStyle = textStyle.copy(color = colors.textRegular),
                singleLine = true,
                cursorBrush = SolidColor(colors.primary),
                decorationBox = { inner ->
                    Box {
                        if (text.isEmpty()) {
                            BasicText(
                                text = placeholder ?: "",
                                style = textStyle.copy(color = colors.textPlaceholder),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        inner()
                    }
                },
            )
        } else {
            BasicText(
                text = display ?: placeholder ?: "",
                modifier = Modifier.weight(1f),
                style = textStyle.copy(color = if (display != null) colors.textRegular else colors.textPlaceholder),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (onClear != null && display != null && hovered && !disabled) {
            ElIcon(
                clearIcon,
                size = 14.dp,
                tint = colors.textPlaceholder,
                contentDescription = locale.datePicker.clear,
                modifier = Modifier.clickable { onClear() },
            )
            Spacer(Modifier.width(4.dp))
        }
        ElIcon(icon, size = 14.dp, tint = colors.textPlaceholder)
    }
}

@Composable
private fun RangeDateField(
    startDisplay: String?,
    endDisplay: String?,
    startPlaceholder: String,
    endPlaceholder: String,
    separator: String,
    onClick: () -> Unit,
    onClear: (() -> Unit)?,
    modifier: Modifier = Modifier,
    size: ElSize? = null,
    disabled: Boolean = false,
    icon: ImageVector = ElIcons.Calendar,
    clearIcon: ImageVector = ElIcons.CircleClose,
    prefixIcon: ImageVector? = null,
    editable: Boolean = false,
    readOnly: Boolean = false,
    onCommitText: ((String) -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val resolvedSize = resolveElSize(size)
    val shape = RoundedCornerShape(theme.radius.base)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val textStyle = theme.typography.base.copy(
        fontSize = resolvedSize.fontSize,
        color = if (disabled) colors.disabledText else colors.textRegular,
    )
    val combined = listOfNotNull(startDisplay, endDisplay).joinToString(separator)
    var text by remember { mutableStateOf(combined) }
    LaunchedEffect(startDisplay, endDisplay) { text = combined }
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
        if (prefixIcon != null) {
            ElIcon(prefixIcon, size = 14.dp, tint = colors.textPlaceholder)
            Spacer(Modifier.width(6.dp))
        }
        if (editable && !readOnly && !disabled) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { if (it.isFocused) onClick() }
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown && event.key == Key.Enter) {
                            onCommitText?.invoke(text)
                            true
                        } else {
                            false
                        }
                    },
                textStyle = textStyle.copy(color = colors.textRegular),
                singleLine = true,
                cursorBrush = SolidColor(colors.primary),
                decorationBox = { inner ->
                    Box {
                        if (text.isEmpty()) {
                            BasicText(
                                text = "$startPlaceholder$separator$endPlaceholder",
                                style = textStyle.copy(color = colors.textPlaceholder),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        inner()
                    }
                },
            )
        } else {
            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                BasicText(
                    text = startDisplay ?: startPlaceholder,
                    modifier = Modifier.weight(1f),
                    style = textStyle.copy(color = if (startDisplay != null) colors.textRegular else colors.textPlaceholder),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                BasicText(
                    text = separator,
                    modifier = Modifier.padding(horizontal = 4.dp),
                    style = textStyle.copy(color = colors.textPlaceholder),
                    maxLines = 1,
                )
                BasicText(
                    text = endDisplay ?: endPlaceholder,
                    modifier = Modifier.weight(1f),
                    style = textStyle.copy(color = if (endDisplay != null) colors.textRegular else colors.textPlaceholder),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (onClear != null && (startDisplay != null || endDisplay != null) && hovered && !disabled) {
            ElIcon(
                clearIcon,
                size = 14.dp,
                tint = colors.textPlaceholder,
                modifier = Modifier.clickable { onClear() },
            )
            Spacer(Modifier.width(4.dp))
        }
        ElIcon(icon, size = 14.dp, tint = colors.textPlaceholder)
    }
}

@Composable
private fun ShortcutColumn(
    entries: List<Pair<String, () -> Unit>>,
    modifier: Modifier = Modifier,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    Column(
        modifier.padding(end = 10.dp).widthIn(min = 84.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        entries.forEach { (label, action) ->
            BasicText(
                text = label,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = action)
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                style = theme.typography.small.copy(color = colors.primary),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Pickers
// ---------------------------------------------------------------------------

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
    defaultValue: LocalDate? = null,
    editable: Boolean = false,
    readOnly: Boolean = false,
    shortcuts: List<ElDateShortcut> = emptyList(),
    showConfirm: Boolean = false,
    prefixIcon: ImageVector? = null,
    cellContent: (@Composable (ElDateCellInfo) -> Unit)? = null,
    cellModifier: ((ElDateCellInfo) -> Modifier)? = null,
    prevIcon: ImageVector? = null,
    nextIcon: ImageVector? = null,
    prevYearIcon: ImageVector? = null,
    nextYearIcon: ImageVector? = null,
    showWeekNumber: Boolean = false,
    valueFormat: String? = null,
    onFormattedValueChange: ((String?) -> Unit)? = null,
    clearIcon: ImageVector? = null,
    placement: ElPlacement = ElPlacement.BottomStart,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val names = remember(locale) { elDateFormatNames(locale) }
    var expanded by remember { mutableStateOf(false) }

    fun setExpanded(next: Boolean) {
        if (expanded == next) return
        expanded = next
        onVisibleChange?.invoke(next)
    }

    fun commit(next: LocalDate?) {
        onValueChange(next)
        onFormattedValueChange?.invoke(next?.let { formatLocalDateNamed(it, valueFormat ?: format, names) })
    }

    val display = value?.let { formatLocalDateNamed(it, format, names) }
    Box(modifier) {
        DateEditorField(
            display = display,
            placeholder = placeholder ?: locale.datePicker.selectDate,
            onClick = { setExpanded(true) },
            onClear = if (clearable) ({ commit(null) }) else null,
            modifier = Modifier,
            size = size,
            disabled = disabled,
            icon = ElIcons.Calendar,
            clearIcon = clearIcon ?: ElIcons.CircleClose,
            prefixIcon = prefixIcon,
            editable = editable,
            readOnly = readOnly,
            onCommitText = { text ->
                parseDatePattern(text, format)?.let { parsed ->
                    val allowed = (minDate == null || parsed >= minDate) &&
                        (maxDate == null || parsed <= maxDate) &&
                        (disabledDate?.invoke(parsed) != true)
                    if (allowed) commit(parsed)
                }
            },
        )
        ElPopper(
            visible = expanded,
            onDismissRequest = { setExpanded(false) },
            placement = placement,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 4.dp),
            focusable = !editable,
        ) {
            ElPopperSurface(Modifier.padding(10.dp)) {
                Row {
                    if (shortcuts.isNotEmpty()) {
                        ShortcutColumn(
                            shortcuts.map { shortcut ->
                                shortcut.text to {
                                    val target = shortcut.value
                                    if (target != null) {
                                        commit(target)
                                        if (!showConfirm) setExpanded(false)
                                    }
                                }
                            },
                        )
                    }
                    Column {
                        ElDatePickerPanel(
                            value = value,
                            onValueChange = { picked ->
                                commit(picked)
                                if (!showConfirm) setExpanded(false)
                            },
                            type = type,
                            minDate = minDate,
                            maxDate = maxDate,
                            disabledDate = disabledDate,
                            initialMonth = value,
                            defaultValue = defaultValue,
                            disabled = disabled,
                            border = false,
                            showFooter = true,
                            showWeekNumber = showWeekNumber,
                            cellContent = cellContent,
                            cellModifier = cellModifier,
                            prevIcon = prevIcon,
                            nextIcon = nextIcon,
                            prevYearIcon = prevYearIcon,
                            nextYearIcon = nextYearIcon,
                        )
                        if (showConfirm) {
                            Row(
                                Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                BasicText(
                                    text = locale.datePicker.confirm,
                                    modifier = Modifier
                                        .clickable { setExpanded(false) }
                                        .padding(horizontal = 6.dp, vertical = 3.dp),
                                    style = theme.typography.small.copy(color = colors.primary),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** `<el-date-picker>` with `dates` / `months` / `years` / `quarters` selection. */
@Composable
fun ElDateMultiplePicker(
    values: List<LocalDate>,
    onValueChange: (List<LocalDate>) -> Unit,
    modifier: Modifier = Modifier,
    type: ElDatePickerType = ElDatePickerType.Dates,
    placeholder: String? = null,
    size: ElSize? = null,
    format: String = "YYYY-MM-DD",
    disabled: Boolean = false,
    clearable: Boolean = true,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    disabledDate: ((LocalDate) -> Boolean)? = null,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    editable: Boolean = false,
    readOnly: Boolean = false,
    prefixIcon: ImageVector? = null,
    cellContent: (@Composable (ElDateCellInfo) -> Unit)? = null,
    cellModifier: ((ElDateCellInfo) -> Modifier)? = null,
    prevIcon: ImageVector? = null,
    nextIcon: ImageVector? = null,
    prevYearIcon: ImageVector? = null,
    nextYearIcon: ImageVector? = null,
    showWeekNumber: Boolean = false,
    clearIcon: ImageVector? = null,
    placement: ElPlacement = ElPlacement.BottomStart,
) {
    val locale = LocalElConfig.current.locale
    val names = remember(locale) { elDateFormatNames(locale) }
    var expanded by remember { mutableStateOf(false) }
    val display = values.takeIf { it.isNotEmpty() }
        ?.joinToString(", ") { formatLocalDateNamed(it, format, names) }
    Box(modifier) {
        DateEditorField(
            display = display,
            placeholder = placeholder ?: when (type) {
                ElDatePickerType.Months, ElDatePickerType.Quarters -> locale.datePicker.selectDate
                ElDatePickerType.Years -> locale.datePicker.year
                else -> locale.datePicker.selectDate
            },
            onClick = { expanded = true; onVisibleChange?.invoke(true) },
            onClear = if (clearable) ({ onValueChange(emptyList()) }) else null,
            modifier = Modifier,
            size = size,
            disabled = disabled,
            icon = ElIcons.Calendar,
            clearIcon = clearIcon ?: ElIcons.CircleClose,
            prefixIcon = prefixIcon,
            editable = editable,
            readOnly = readOnly,
            onCommitText = { text ->
                val parsed = text.split(",")
                    .mapNotNull { parseDatePattern(it, format) }
                    .filter { parsedDate ->
                        (minDate == null || parsedDate >= minDate) &&
                            (maxDate == null || parsedDate <= maxDate) &&
                            (disabledDate?.invoke(parsedDate) != true)
                    }
                onValueChange(parsed)
            },
        )
        ElPopper(
            visible = expanded,
            onDismissRequest = { expanded = false; onVisibleChange?.invoke(false) },
            placement = placement,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 4.dp),
            focusable = !editable,
        ) {
            ElPopperSurface(Modifier.padding(10.dp)) {
                ElDateMultiplePanel(
                    values = values,
                    onValueChange = onValueChange,
                    type = type,
                    minDate = minDate,
                    maxDate = maxDate,
                    disabledDate = disabledDate,
                    initialMonth = values.firstOrNull(),
                    disabled = disabled,
                    border = false,
                    showWeekNumber = showWeekNumber,
                    cellContent = cellContent,
                    cellModifier = cellModifier,
                    prevIcon = prevIcon,
                    nextIcon = nextIcon,
                    prevYearIcon = prevYearIcon,
                    nextYearIcon = nextYearIcon,
                )
            }
        }
    }
}

/** Range picker for date, month, year and quarter ranges. */
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
    type: ElDateRangeType = ElDateRangeType.Date,
    startPlaceholder: String? = null,
    endPlaceholder: String? = null,
    rangeSeparator: String? = null,
    singlePanel: Boolean = false,
    unlinkPanels: Boolean = false,
    defaultValue: LocalDate? = null,
    defaultEndValue: LocalDate? = null,
    editable: Boolean = false,
    readOnly: Boolean = false,
    shortcuts: List<ElDateShortcut> = emptyList(),
    showConfirm: Boolean = false,
    prefixIcon: ImageVector? = null,
    cellContent: (@Composable (ElDateCellInfo) -> Unit)? = null,
    cellModifier: ((ElDateCellInfo) -> Modifier)? = null,
    prevIcon: ImageVector? = null,
    nextIcon: ImageVector? = null,
    prevYearIcon: ImageVector? = null,
    nextYearIcon: ImageVector? = null,
    showWeekNumber: Boolean = false,
    valueFormat: String? = null,
    onFormattedValueChange: ((String?, String?) -> Unit)? = null,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    clearIcon: ImageVector? = null,
    placement: ElPlacement = ElPlacement.BottomStart,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val names = remember(locale) { elDateFormatNames(locale) }
    var expanded by remember { mutableStateOf(false) }
    val separator = rangeSeparator ?: " - "

    fun setExpanded(next: Boolean) {
        if (expanded == next) return
        expanded = next
        onVisibleChange?.invoke(next)
    }

    fun commit(nextStart: LocalDate?, nextEnd: LocalDate?) {
        onValueChange(nextStart, nextEnd)
        onFormattedValueChange?.invoke(
            nextStart?.let { formatLocalDateNamed(it, valueFormat ?: format, names) },
            nextEnd?.let { formatLocalDateNamed(it, valueFormat ?: format, names) },
        )
        if (!showConfirm && nextStart != null && nextEnd != null) setExpanded(false)
    }

    val startDisplay = start?.let { formatLocalDateNamed(it, format, names) }
    val endDisplay = end?.let { formatLocalDateNamed(it, format, names) }
    Box(modifier) {
        RangeDateField(
            startDisplay = startDisplay,
            endDisplay = endDisplay,
            startPlaceholder = startPlaceholder ?: placeholder ?: locale.datePicker.startDate,
            endPlaceholder = endPlaceholder ?: placeholder ?: locale.datePicker.endDate,
            separator = separator,
            onClick = { setExpanded(true) },
            onClear = if (clearable) ({ commit(null, null) }) else null,
            modifier = Modifier,
            size = size,
            disabled = disabled,
            icon = ElIcons.Calendar,
            clearIcon = clearIcon ?: ElIcons.CircleClose,
            prefixIcon = prefixIcon,
            editable = editable,
            readOnly = readOnly,
            onCommitText = { text ->
                val parts = text.split(separator)
                val first = parseDatePattern(parts.getOrElse(0) { "" }, format)
                val second = parts.getOrNull(1)?.let { parseDatePattern(it, format) }
                if (first != null) commit(first, second)
            },
        )
        ElPopper(
            visible = expanded,
            onDismissRequest = { setExpanded(false) },
            placement = placement,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 4.dp),
            focusable = !editable,
        ) {
            ElPopperSurface(Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    if (shortcuts.isNotEmpty()) {
                        ShortcutColumn(
                            shortcuts.map { shortcut ->
                                shortcut.text to {
                                    val nextStart = shortcut.start ?: shortcut.value
                                    val nextEnd = shortcut.end
                                    if (nextStart != null) commit(nextStart, nextEnd)
                                }
                            },
                        )
                    }
                    Column {
                        ElDateRangePanel(
                            start = start,
                            end = end,
                            onValueChange = { nextStart, nextEnd -> commit(nextStart, nextEnd) },
                            type = type,
                            minDate = minDate,
                            maxDate = maxDate,
                            disabledDate = disabledDate,
                            initialMonth = start ?: end,
                            defaultValue = defaultValue,
                            defaultEndValue = defaultEndValue,
                            singlePanel = singlePanel,
                            unlinkPanels = unlinkPanels,
                            disabled = disabled,
                            border = false,
                            showFooter = true,
                            showWeekNumber = showWeekNumber,
                            cellContent = cellContent,
                            cellModifier = cellModifier,
                            prevIcon = prevIcon,
                            nextIcon = nextIcon,
                            prevYearIcon = prevYearIcon,
                            nextYearIcon = nextYearIcon,
                        )
                        if (showConfirm) {
                            Row(
                                Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                BasicText(
                                    text = locale.datePicker.confirm,
                                    modifier = Modifier
                                        .clickable { setExpanded(false) }
                                        .padding(horizontal = 6.dp, vertical = 3.dp),
                                    style = theme.typography.small.copy(color = colors.primary),
                                )
                            }
                        }
                    }
                }
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
    dateFormat: String? = null,
    timeFormat: String? = null,
    defaultTime: LocalTime? = null,
    defaultValue: LocalDate? = null,
    disabledHours: ((Int) -> Boolean)? = null,
    disabledMinutes: ((Int) -> Boolean)? = null,
    disabledSeconds: ((Int) -> Boolean)? = null,
    editable: Boolean = false,
    readOnly: Boolean = false,
    shortcuts: List<ElDateTimeShortcut> = emptyList(),
    showConfirm: Boolean = true,
    prefixIcon: ImageVector? = null,
    cellContent: (@Composable (ElDateCellInfo) -> Unit)? = null,
    cellModifier: ((ElDateCellInfo) -> Modifier)? = null,
    prevIcon: ImageVector? = null,
    nextIcon: ImageVector? = null,
    prevYearIcon: ImageVector? = null,
    nextYearIcon: ImageVector? = null,
    valueFormat: String? = null,
    onFormattedValueChange: ((String?) -> Unit)? = null,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    clearIcon: ImageVector? = null,
    placement: ElPlacement = ElPlacement.BottomStart,
) {
    val locale = LocalElConfig.current.locale
    val names = remember(locale) { elDateFormatNames(locale) }
    var expanded by remember { mutableStateOf(false) }
    val displayPattern = if (dateFormat != null || timeFormat != null) {
        "${dateFormat ?: "YYYY-MM-DD"} ${timeFormat ?: "HH:mm:ss"}"
    } else {
        format
    }

    fun setExpanded(next: Boolean) {
        if (expanded == next) return
        expanded = next
        onVisibleChange?.invoke(next)
    }

    fun commit(next: LocalDateTime?) {
        onValueChange(next)
        onFormattedValueChange?.invoke(next?.let { formatLocalDateTimeNamed(it, valueFormat ?: displayPattern, names) })
    }

    val display = value?.let { formatLocalDateTimeNamed(it, displayPattern, names) }
    Box(modifier) {
        DateEditorField(
            display = display,
            placeholder = placeholder ?: locale.datePicker.selectDate,
            onClick = { setExpanded(true) },
            onClear = if (clearable) ({ commit(null) }) else null,
            modifier = Modifier,
            size = size,
            disabled = disabled,
            icon = ElIcons.Calendar,
            clearIcon = clearIcon ?: ElIcons.CircleClose,
            prefixIcon = prefixIcon,
            editable = editable,
            readOnly = readOnly,
            onCommitText = { text ->
                parseLocalDateTimePattern(text, displayPattern)?.let { parsed ->
                    val allowed = (minDate == null || parsed.date >= minDate) &&
                        (maxDate == null || parsed.date <= maxDate) &&
                        (disabledDate?.invoke(parsed.date) != true)
                    if (allowed) commit(parsed)
                }
            },
        )
        ElPopper(
            visible = expanded,
            onDismissRequest = { setExpanded(false) },
            placement = placement,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 4.dp),
            focusable = !editable,
        ) {
            ElPopperSurface(Modifier.padding(10.dp)) {
                Row {
                    if (shortcuts.isNotEmpty()) {
                        ShortcutColumn(
                            shortcuts.map { shortcut ->
                                shortcut.text to {
                                    shortcut.value?.let { target ->
                                        commit(target)
                                        setExpanded(false)
                                    }
                                }
                            },
                        )
                    }
                    Column {
                        ElDateTimePickerPanel(
                            value = value,
                            onValueChange = { picked ->
                                commit(picked)
                                if (!showConfirm) setExpanded(false)
                            },
                            showSeconds = showSeconds,
                            minDate = minDate,
                            maxDate = maxDate,
                            disabledDate = disabledDate,
                            defaultTime = defaultTime,
                            disabledHours = disabledHours,
                            disabledMinutes = disabledMinutes,
                            disabledSeconds = disabledSeconds,
                            dateFormat = dateFormat ?: "YYYY-MM-DD",
                            timeFormat = timeFormat ?: "HH:mm:ss",
                            disabled = disabled,
                            border = false,
                            cellContent = cellContent,
                            cellModifier = cellModifier,
                            prevIcon = prevIcon,
                            nextIcon = nextIcon,
                            prevYearIcon = prevYearIcon,
                            nextYearIcon = nextYearIcon,
                        )
                        Row(
                            Modifier.fillMaxWidth().padding(top = 6.dp),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            BasicText(
                                text = locale.datePicker.now,
                                modifier = Modifier
                                    .clickable {
                                        commit(nowDateTime(defaultValue))
                                        setExpanded(false)
                                    }
                                    .padding(horizontal = 6.dp, vertical = 3.dp),
                                style = LocalElTheme.current.typography.small.copy(
                                    color = LocalElTheme.current.colors.textSecondary,
                                ),
                            )
                            if (showConfirm) {
                                Spacer(Modifier.width(6.dp))
                                BasicText(
                                    text = locale.datePicker.confirm,
                                    modifier = Modifier
                                        .clickable { setExpanded(false) }
                                        .padding(horizontal = 6.dp, vertical = 3.dp),
                                    style = LocalElTheme.current.typography.small.copy(
                                        color = LocalElTheme.current.colors.primary,
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun nowDateTime(defaultValue: LocalDate?): LocalDateTime {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return LocalDateTime(defaultValue ?: now.date, now.time)
}

/** Date and time range picker (`type="datetimerange"`). */
@Composable
fun ElDateTimeRangePicker(
    start: LocalDateTime?,
    end: LocalDateTime?,
    onValueChange: (LocalDateTime?, LocalDateTime?) -> Unit,
    modifier: Modifier = Modifier,
    startPlaceholder: String? = null,
    endPlaceholder: String? = null,
    size: ElSize? = null,
    format: String = "YYYY-MM-DD HH:mm:ss",
    dateFormat: String? = null,
    timeFormat: String? = null,
    disabled: Boolean = false,
    clearable: Boolean = true,
    showSeconds: Boolean = true,
    singlePanel: Boolean = false,
    rangeSeparator: String? = null,
    defaultStartTime: LocalTime = LocalTime(0, 0),
    defaultEndTime: LocalTime = LocalTime(23, 59, 59),
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    disabledDate: ((LocalDate) -> Boolean)? = null,
    disabledHours: ((Int) -> Boolean)? = null,
    disabledMinutes: ((Int) -> Boolean)? = null,
    disabledSeconds: ((Int) -> Boolean)? = null,
    shortcuts: List<ElDateTimeShortcut> = emptyList(),
    editable: Boolean = false,
    readOnly: Boolean = false,
    prefixIcon: ImageVector? = null,
    showConfirm: Boolean = true,
    valueFormat: String? = null,
    onFormattedValueChange: ((String?, String?) -> Unit)? = null,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    clearIcon: ImageVector? = null,
    placement: ElPlacement = ElPlacement.BottomStart,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val names = remember(locale) { elDateFormatNames(locale) }
    var expanded by remember { mutableStateOf(false) }
    val separator = rangeSeparator ?: " - "
    val displayPattern = if (dateFormat != null || timeFormat != null) {
        "${dateFormat ?: "YYYY-MM-DD"} ${timeFormat ?: "HH:mm:ss"}"
    } else {
        format
    }

    fun setExpanded(next: Boolean) {
        if (expanded == next) return
        expanded = next
        onVisibleChange?.invoke(next)
    }

    fun commit(nextStart: LocalDateTime?, nextEnd: LocalDateTime?) {
        onValueChange(nextStart, nextEnd)
        onFormattedValueChange?.invoke(
            nextStart?.let { formatLocalDateTimeNamed(it, valueFormat ?: displayPattern, names) },
            nextEnd?.let { formatLocalDateTimeNamed(it, valueFormat ?: displayPattern, names) },
        )
        if (!showConfirm && nextStart != null && nextEnd != null) setExpanded(false)
    }

    val startDisplay = start?.let { formatLocalDateTimeNamed(it, displayPattern, names) }
    val endDisplay = end?.let { formatLocalDateTimeNamed(it, displayPattern, names) }
    Box(modifier) {
        RangeDateField(
            startDisplay = startDisplay,
            endDisplay = endDisplay,
            startPlaceholder = startPlaceholder ?: locale.datePicker.startDate,
            endPlaceholder = endPlaceholder ?: locale.datePicker.endDate,
            separator = separator,
            onClick = { setExpanded(true) },
            onClear = if (clearable) ({ commit(null, null) }) else null,
            modifier = Modifier,
            size = size,
            disabled = disabled,
            icon = ElIcons.Calendar,
            clearIcon = clearIcon ?: ElIcons.CircleClose,
            prefixIcon = prefixIcon,
            editable = editable,
            readOnly = readOnly,
            onCommitText = { text ->
                val parts = text.split(separator)
                val first = parseLocalDateTimePattern(parts.getOrElse(0) { "" }, displayPattern)
                val second = parts.getOrNull(1)?.let { parseLocalDateTimePattern(it, displayPattern) }
                if (first != null) commit(first, second)
            },
        )
        ElPopper(
            visible = expanded,
            onDismissRequest = { setExpanded(false) },
            placement = placement,
            offset = androidx.compose.ui.unit.DpOffset(0.dp, 4.dp),
            focusable = !editable,
        ) {
            ElPopperSurface(Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    if (shortcuts.isNotEmpty()) {
                        ShortcutColumn(
                            shortcuts.map { shortcut ->
                                shortcut.text to {
                                    val nextStart = shortcut.start ?: shortcut.value
                                    val nextEnd = shortcut.end
                                    if (nextStart != null) commit(nextStart, nextEnd)
                                }
                            },
                        )
                    }
                    Column {
                        ElDateTimeRangePanel(
                            start = start,
                            end = end,
                            onValueChange = { nextStart, nextEnd -> commit(nextStart, nextEnd) },
                            singlePanel = singlePanel,
                            showSeconds = showSeconds,
                            dateFormat = dateFormat ?: "YYYY-MM-DD",
                            timeFormat = timeFormat ?: "HH:mm:ss",
                            defaultStartTime = defaultStartTime,
                            defaultEndTime = defaultEndTime,
                            minDate = minDate,
                            maxDate = maxDate,
                            disabledDate = disabledDate,
                            disabledHours = disabledHours,
                            disabledMinutes = disabledMinutes,
                            disabledSeconds = disabledSeconds,
                            disabled = disabled,
                            border = false,
                            showFooter = !showConfirm,
                        )
                        if (showConfirm) {
                            Row(
                                Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                BasicText(
                                    text = locale.datePicker.confirm,
                                    modifier = Modifier
                                        .clickable { setExpanded(false) }
                                        .padding(horizontal = 6.dp, vertical = 3.dp),
                                    style = theme.typography.small.copy(color = colors.primary),
                                )
                            }
                        }
                    }
                }
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
    isDisabled: ((Int) -> Boolean)? = null,
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
            val enabled = isDisabled?.invoke(value) != true
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isSelected) colors.light(ElType.Primary, 9) else Color.Transparent)
                    .clickable(enabled = enabled) { onSelect(value) },
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = value.toString().padStart(2, '0'),
                    style = theme.typography.small.copy(
                        color = when {
                            !enabled -> colors.textDisabled
                            isSelected -> colors.primary
                            else -> colors.textRegular
                        },
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    ),
                )
            }
        }
    }
}
