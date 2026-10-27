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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.internal.ElPopper
import cn.enaium.elementplus.internal.ElPopperSurface
import cn.enaium.elementplus.internal.rememberElInteractionState
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.datetime.LocalTime

/**
 * `<el-time-picker>`: spinner based time selection with confirm/cancel.
 *
 * @param disabledHours / [disabledMinutes] / [disabledSeconds] disable spinner
 *   entries; minute and second predicates receive the already selected larger
 *   units.
 * @param minTime / [maxTime] restrict selectable times (inclusive).
 * @param editable allows typing the time directly instead of only picking it
 *   from the panel, the Compose equivalent of `editable`.
 * @param onChange commit event fired when a time is confirmed or typed.
 */
@Composable
fun ElTimePicker(
    value: LocalTime?,
    onValueChange: (LocalTime?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: ElSize? = null,
    format: String = "HH:mm:ss",
    disabled: Boolean = false,
    clearable: Boolean = true,
    showSeconds: Boolean = true,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    disabledHours: ((Int) -> Boolean)? = null,
    disabledMinutes: ((Int, Int) -> Boolean)? = null,
    disabledSeconds: ((Int, Int, Int) -> Boolean)? = null,
    minTime: LocalTime? = null,
    maxTime: LocalTime? = null,
    editable: Boolean = false,
    readOnly: Boolean = false,
    placement: ElPlacement = ElPlacement.BottomStart,
    onChange: ((LocalTime?) -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    var expanded by remember { mutableStateOf(false) }
    var draft by remember { mutableStateOf(value ?: minTime ?: LocalTime(0, 0)) }
    val display = value?.let { formatTimeText(it, format, locale.timePicker.am, locale.timePicker.pm) }
    val constraints = TimeConstraints(
        disabledHours = disabledHours,
        disabledMinutes = disabledMinutes,
        disabledSeconds = disabledSeconds,
        minTime = minTime,
        maxTime = maxTime,
    )

    fun emit(next: LocalTime?) {
        onValueChange(next)
        onChange?.invoke(next)
    }

    fun setExpanded(next: Boolean) {
        if (next) {
            draft = firstEnabledTime(value ?: minTime ?: LocalTime(0, 0), constraints)
                ?: (value ?: LocalTime(0, 0))
        }
        expanded = next
        onVisibleChange?.invoke(next)
    }

    Box(modifier) {
        if (editable && !readOnly) {
            var text by remember(value) { mutableStateOf(display ?: "") }
            EditableTimeField(
                text = text,
                onTextChange = { text = it },
                onCommit = {
                    val parsed = parseTimeValue(text, format)
                    when {
                        parsed == null -> text = display ?: ""
                        else -> {
                            if (parsed != value) emit(parsed)
                            setExpanded(false)
                        }
                    }
                },
                onOpen = { if (!disabled) setExpanded(true) },
                placeholder = placeholder ?: locale.timePicker.selectTime,
                size = size,
                disabled = disabled,
                clearable = clearable,
                hasValue = value != null,
                onClear = {
                    text = ""
                    emit(null)
                },
            )
        } else {
            DateField(
                display = display,
                placeholder = placeholder ?: locale.timePicker.selectTime,
                onClick = { setExpanded(true) },
                onClear = if (clearable) ({ emit(null) }) else null,
                modifier = Modifier,
                size = size,
                disabled = disabled,
                icon = ElIcons.Clock,
            )
        }
        ElPopper(
            visible = expanded,
            onDismissRequest = { setExpanded(false) },
            placement = placement,
            offset = DpOffset(0.dp, 4.dp),
            focusable = !editable,
        ) {
            ElPopperSurface(Modifier.padding(10.dp)) {
                Column(Modifier.width(240.dp)) {
                    BasicText(
                        text = locale.timePicker.selectTime,
                        style = theme.typography.small.copy(color = colors.textSecondary),
                    )
                    Spacer(Modifier.height(6.dp))
                    TimeColumns(
                        value = draft,
                        onValueChange = { draft = it },
                        showSeconds = showSeconds,
                        constraints = constraints,
                    )
                    val valid = !isTimeDisabled(
                        draft.hour,
                        draft.minute,
                        draft.second,
                        constraints,
                    )
                    Row(
                        Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BasicText(
                            text = formatTimeText(draft, format, locale.timePicker.am, locale.timePicker.pm),
                            style = theme.typography.small.copy(color = colors.primary),
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            BasicText(
                                text = locale.timePicker.cancel,
                                modifier = Modifier.clickable { setExpanded(false) }.padding(4.dp),
                                style = theme.typography.small.copy(color = colors.textRegular),
                            )
                            BasicText(
                                text = locale.timePicker.confirm,
                                modifier = Modifier
                                    .clickable(enabled = valid) {
                                        if (!valid) return@clickable
                                        emit(draft)
                                        setExpanded(false)
                                    }
                                    .padding(4.dp),
                                style = theme.typography.small.copy(
                                    color = if (valid) colors.primary else colors.textDisabled,
                                    fontWeight = FontWeight.Medium,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * `<el-time-picker is-range>`: two spinner groups in one panel, with range and
 * per-column time constraints.
 */
@Composable
fun ElTimeRangePicker(
    start: LocalTime?,
    end: LocalTime?,
    onValueChange: (LocalTime?, LocalTime?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    startPlaceholder: String? = null,
    endPlaceholder: String? = null,
    rangeSeparator: String = "-",
    size: ElSize? = null,
    format: String = "HH:mm:ss",
    disabled: Boolean = false,
    clearable: Boolean = true,
    showSeconds: Boolean = true,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    disabledHours: ((Int) -> Boolean)? = null,
    disabledMinutes: ((Int, Int) -> Boolean)? = null,
    disabledSeconds: ((Int, Int, Int) -> Boolean)? = null,
    minTime: LocalTime? = null,
    maxTime: LocalTime? = null,
    placement: ElPlacement = ElPlacement.BottomStart,
    onChange: ((LocalTime?, LocalTime?) -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val constraints = TimeConstraints(
        disabledHours = disabledHours,
        disabledMinutes = disabledMinutes,
        disabledSeconds = disabledSeconds,
        minTime = minTime,
        maxTime = maxTime,
    )
    var expanded by remember { mutableStateOf(false) }
    var draftStart by remember { mutableStateOf(start ?: minTime ?: LocalTime(0, 0)) }
    var draftEnd by remember { mutableStateOf(end ?: maxTime ?: LocalTime(0, 0)) }

    fun emit(nextStart: LocalTime?, nextEnd: LocalTime?) {
        onValueChange(nextStart, nextEnd)
        onChange?.invoke(nextStart, nextEnd)
    }

    fun setExpanded(next: Boolean) {
        if (next) {
            draftStart = firstEnabledTime(start ?: minTime ?: LocalTime(0, 0), constraints)
                ?: (start ?: LocalTime(0, 0))
            draftEnd = firstEnabledTime(end ?: maxTime ?: draftStart, constraints)
                ?: (end ?: draftStart)
            if (draftEnd < draftStart) draftEnd = draftStart
        }
        expanded = next
        onVisibleChange?.invoke(next)
    }

    Box(modifier) {
        TimeRangeField(
            startText = start?.let { formatTimeText(it, format, locale.timePicker.am, locale.timePicker.pm) },
            endText = end?.let { formatTimeText(it, format, locale.timePicker.am, locale.timePicker.pm) },
            startPlaceholder = startPlaceholder ?: placeholder ?: locale.timePicker.startTime,
            endPlaceholder = endPlaceholder ?: placeholder ?: locale.timePicker.endTime,
            separator = rangeSeparator,
            size = size,
            disabled = disabled,
            clearable = clearable,
            onClear = { emit(null, null) },
            onClick = { setExpanded(true) },
        )
        ElPopper(
            visible = expanded,
            onDismissRequest = { setExpanded(false) },
            placement = placement,
            offset = DpOffset(0.dp, 4.dp),
            focusable = true,
        ) {
            ElPopperSurface(Modifier.padding(10.dp)) {
                Column(Modifier.width(420.dp)) {
                    Row(Modifier.fillMaxWidth()) {
                        BasicText(
                            text = locale.timePicker.startTime,
                            modifier = Modifier.weight(1f),
                            style = theme.typography.small.copy(color = colors.textSecondary),
                        )
                        BasicText(
                            text = locale.timePicker.endTime,
                            modifier = Modifier.weight(1f),
                            style = theme.typography.small.copy(color = colors.textSecondary),
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.weight(1f)) {
                            TimeColumns(
                                value = draftStart,
                                onValueChange = { next ->
                                    draftStart = next
                                    if (draftEnd < next) draftEnd = next
                                },
                                showSeconds = showSeconds,
                                constraints = constraints,
                            )
                        }
                        Box(Modifier.weight(1f)) {
                            TimeColumns(
                                value = draftEnd,
                                onValueChange = { next ->
                                    draftEnd = next
                                    if (draftStart > next) draftStart = next
                                },
                                showSeconds = showSeconds,
                                constraints = constraints,
                            )
                        }
                    }
                    val valid = draftStart <= draftEnd &&
                        !isTimeDisabled(draftStart.hour, draftStart.minute, draftStart.second, constraints) &&
                        !isTimeDisabled(draftEnd.hour, draftEnd.minute, draftEnd.second, constraints)
                    Row(
                        Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BasicText(
                            text = listOf(draftStart, draftEnd).joinToString(" $rangeSeparator ") {
                                formatTimeText(it, format, locale.timePicker.am, locale.timePicker.pm)
                            },
                            style = theme.typography.small.copy(color = colors.primary),
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            BasicText(
                                text = locale.timePicker.cancel,
                                modifier = Modifier.clickable { setExpanded(false) }.padding(4.dp),
                                style = theme.typography.small.copy(color = colors.textRegular),
                            )
                            BasicText(
                                text = locale.timePicker.confirm,
                                modifier = Modifier
                                    .clickable(enabled = valid) {
                                        if (!valid) return@clickable
                                        emit(draftStart, draftEnd)
                                        setExpanded(false)
                                    }
                                    .padding(4.dp),
                                style = theme.typography.small.copy(
                                    color = if (valid) colors.primary else colors.textDisabled,
                                    fontWeight = FontWeight.Medium,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * `<el-time-select>`: a list of generated times between [start] and [end].
 *
 * @param minTime / [maxTime] disable options before/after a linked time; used
 *   to link a start selector with an end selector (options equal to the
 *   boundary stay disabled, mirroring Element Plus).
 * @param includeEndTime includes [end] in the generated options.
 * @param editable allows typing the time directly.
 * @param onChange commit event fired when an option is picked or typed.
 */
@Composable
fun ElTimeSelect(
    value: LocalTime?,
    onValueChange: (LocalTime?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: ElSize? = null,
    start: LocalTime = LocalTime(9, 0),
    end: LocalTime = LocalTime(18, 0),
    step: Int = 30,
    disabled: Boolean = false,
    clearable: Boolean = true,
    format: String = "HH:mm",
    minTime: LocalTime? = null,
    maxTime: LocalTime? = null,
    includeEndTime: Boolean = true,
    editable: Boolean = false,
    readOnly: Boolean = false,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    placement: ElPlacement = ElPlacement.BottomStart,
    onChange: ((LocalTime?) -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    var expanded by remember { mutableStateOf(false) }
    val options = remember(start, end, step, includeEndTime) {
        buildList {
            var current = start.hour * 60 + start.minute
            val limit = end.hour * 60 + end.minute
            while (current <= limit) {
                add(LocalTime(current / 60, current % 60, 0))
                current += step.coerceAtLeast(1)
            }
            if (!includeEndTime && lastOrNull() == LocalTime(limit / 60, limit % 60, 0)) {
                removeAt(lastIndex)
            }
        }
    }
    val display = value?.let { formatTimeText(it, format, locale.timePicker.am, locale.timePicker.pm) }

    fun emit(next: LocalTime?) {
        onValueChange(next)
        onChange?.invoke(next)
    }

    fun setExpanded(next: Boolean) {
        expanded = next
        onVisibleChange?.invoke(next)
    }

    fun enabled(option: LocalTime): Boolean =
        (minTime == null || option > minTime) && (maxTime == null || option < maxTime)

    Box(modifier) {
        if (editable && !readOnly) {
            var text by remember(value) { mutableStateOf(display ?: "") }
            EditableTimeField(
                text = text,
                onTextChange = { text = it },
                onCommit = {
                    val parsed = parseTimeValue(text, format)
                    val match = parsed?.let { time ->
                        options.firstOrNull {
                            it.hour == time.hour && it.minute == time.minute && time.second == 0
                        }
                    }
                    when {
                        match == null || !enabled(match) -> text = display ?: ""
                        else -> {
                            if (match != value) emit(match)
                            setExpanded(false)
                        }
                    }
                },
                onOpen = { if (!disabled) setExpanded(true) },
                placeholder = placeholder ?: locale.timePicker.selectTime,
                size = size,
                disabled = disabled,
                clearable = clearable,
                hasValue = value != null,
                onClear = {
                    text = ""
                    emit(null)
                },
            )
        } else {
            DateField(
                display = display,
                placeholder = placeholder ?: locale.timePicker.selectTime,
                onClick = { setExpanded(true) },
                onClear = if (clearable) ({ emit(null) }) else null,
                modifier = Modifier,
                size = size,
                disabled = disabled,
                icon = ElIcons.Clock,
            )
        }
        ElPopper(
            visible = expanded,
            onDismissRequest = { setExpanded(false) },
            placement = placement,
            offset = DpOffset(0.dp, 4.dp),
            focusable = !editable,
        ) {
            ElPopperSurface(Modifier.width(180.dp)) {
                Column(Modifier.heightIn(max = 274.dp).verticalScroll(rememberScrollState())) {
                    options.forEach { option ->
                        val selected = option == value
                        val optionEnabled = enabled(option)
                        val interaction = remember(option) { MutableInteractionSource() }
                        val isHovered = rememberElInteractionState(interaction).hovered && optionEnabled
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    when {
                                        selected -> colors.light(ElType.Primary, 9)
                                        isHovered -> colors.fillLight
                                        else -> colors.backgroundOverlay
                                    }
                                )
                                .clickable(enabled = optionEnabled) {
                                    if (!optionEnabled) return@clickable
                                    emit(option)
                                    setExpanded(false)
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                        ) {
                            BasicText(
                                text = formatTimeText(option, format, locale.timePicker.am, locale.timePicker.pm),
                                style = theme.typography.base.copy(
                                    color = when {
                                        !optionEnabled -> colors.textDisabled
                                        selected -> colors.primary
                                        else -> colors.textRegular
                                    },
                                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Formatting rules shared by the time components, including 12-hour patterns. */
internal fun formatTimeText(
    time: LocalTime,
    pattern: String,
    am: String = "AM",
    pm: String = "PM",
): String {
    val hour12 = if (time.hour % 12 == 0) 12 else time.hour % 12
    val meridiem = if (time.hour < 12) am else pm
    return pattern
        .replace("HH", time.hour.toString().padStart(2, '0'))
        .replace("H", time.hour.toString())
        .replace("hh", hour12.toString().padStart(2, '0'))
        .replace("h", hour12.toString())
        .replace("mm", time.minute.toString().padStart(2, '0'))
        .replace("m", time.minute.toString())
        .replace("ss", time.second.toString().padStart(2, '0'))
        .replace("s", time.second.toString())
        .replace("A", meridiem)
        .replace("a", meridiem.lowercase())
}

/** Parses a time from user input using the digits of [pattern]. */
internal fun parseTimeValue(text: String, pattern: String): LocalTime? {
    val groups = Regex("\\d+").findAll(text.trim())
        .mapNotNull { it.value.toIntOrNull() }
        .toList()
    if (groups.isEmpty()) return null
    var cursor = 0
    val hourValue = if (pattern.contains('H') || pattern.contains('h')) {
        groups.getOrNull(cursor++) ?: return null
    } else {
        0
    }
    val minute = if (pattern.contains('m')) groups.getOrNull(cursor++) ?: return null else 0
    val second = if (pattern.contains('s')) groups.getOrNull(cursor) ?: 0 else 0
    if (minute !in 0..59 || second !in 0..59) return null
    val hour = if (pattern.contains('h') && !pattern.contains('H')) {
        if (hourValue !in 1..12) return null
        val isPm = (pattern.contains('A') || pattern.contains('a')) &&
            text.trim().uppercase().contains("PM")
        (hourValue % 12) + if (isPm) 12 else 0
    } else {
        hourValue
    }
    if (hour !in 0..23) return null
    return LocalTime(hour, minute, second)
}

internal class TimeConstraints(
    val disabledHours: ((Int) -> Boolean)? = null,
    val disabledMinutes: ((Int, Int) -> Boolean)? = null,
    val disabledSeconds: ((Int, Int, Int) -> Boolean)? = null,
    val minTime: LocalTime? = null,
    val maxTime: LocalTime? = null,
)

private fun isHourDisabled(hour: Int, constraints: TimeConstraints): Boolean =
    constraints.disabledHours?.invoke(hour) == true ||
        (constraints.minTime != null && LocalTime(hour, 59, 59) < constraints.minTime) ||
        (constraints.maxTime != null && LocalTime(hour, 0, 0) > constraints.maxTime)

private fun isMinuteDisabled(hour: Int, minute: Int, constraints: TimeConstraints): Boolean =
    constraints.disabledMinutes?.invoke(hour, minute) == true ||
        (constraints.minTime != null && LocalTime(hour, minute, 59) < constraints.minTime) ||
        (constraints.maxTime != null && LocalTime(hour, minute, 0) > constraints.maxTime)

private fun isTimeDisabled(
    hour: Int,
    minute: Int,
    second: Int,
    constraints: TimeConstraints,
): Boolean {
    val time = LocalTime(hour, minute, second)
    return constraints.disabledHours?.invoke(hour) == true ||
        constraints.disabledMinutes?.invoke(hour, minute) == true ||
        constraints.disabledSeconds?.invoke(hour, minute, second) == true ||
        (constraints.minTime != null && time < constraints.minTime) ||
        (constraints.maxTime != null && time > constraints.maxTime)
}

private fun firstEnabledTime(start: LocalTime, constraints: TimeConstraints): LocalTime? {
    val startMinute = start.hour * 60 + start.minute
    for (offset in 0 until 24 * 60) {
        val total = (startMinute + offset) % (24 * 60)
        val hour = total / 60
        val minute = total % 60
        for (second in 0..59) {
            if (!isTimeDisabled(hour, minute, second, constraints)) {
                return LocalTime(hour, minute, second)
            }
        }
    }
    return null
}

/** Three connected spinners with per-column disabled entries. */
@Composable
private fun TimeColumns(
    value: LocalTime,
    onValueChange: (LocalTime) -> Unit,
    showSeconds: Boolean,
    constraints: TimeConstraints,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth().height(196.dp)) {
        TimeColumn(
            count = 24,
            selected = value.hour,
            modifier = Modifier.weight(1f),
            enabled = { !isHourDisabled(it, constraints) },
        ) { hour ->
            var minute = value.minute
            var second = value.second
            if (isMinuteDisabled(hour, minute, constraints)) {
                minute = (0..59).firstOrNull { !isMinuteDisabled(hour, it, constraints) } ?: minute
            }
            if (isTimeDisabled(hour, minute, second, constraints)) {
                second = (0..59).firstOrNull { !isTimeDisabled(hour, minute, it, constraints) } ?: second
            }
            onValueChange(LocalTime(hour, minute, second))
        }
        TimeColumn(
            count = 60,
            selected = value.minute,
            modifier = Modifier.weight(1f),
            enabled = { !isMinuteDisabled(value.hour, it, constraints) },
        ) { minute ->
            var second = value.second
            if (isTimeDisabled(value.hour, minute, second, constraints)) {
                second = (0..59).firstOrNull {
                    !isTimeDisabled(value.hour, minute, it, constraints)
                } ?: second
            }
            onValueChange(LocalTime(value.hour, minute, second))
        }
        if (showSeconds) {
            TimeColumn(
                count = 60,
                selected = value.second,
                modifier = Modifier.weight(1f),
                enabled = { !isTimeDisabled(value.hour, value.minute, it, constraints) },
            ) { second ->
                onValueChange(LocalTime(value.hour, value.minute, second))
            }
        }
    }
}

@Composable
private fun TimeColumn(
    count: Int,
    selected: Int,
    modifier: Modifier = Modifier,
    enabled: (Int) -> Boolean,
    onSelect: (Int) -> Unit,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val state = rememberLazyListState(
        initialFirstVisibleItemIndex = (selected - 2).coerceAtLeast(0),
    )
    LaunchedEffect(selected) {
        val visible = state.layoutInfo.visibleItemsInfo
        if (visible.none { it.index == selected }) {
            state.animateScrollToItem(selected)
        }
    }
    LazyColumn(modifier.heightIn(max = 180.dp), state = state) {
        items((0 until count).toList()) { value ->
            val optionEnabled = enabled(value)
            val isSelected = value == selected
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isSelected) colors.light(ElType.Primary, 9) else Color.Transparent)
                    .clickable(enabled = optionEnabled) {
                        if (optionEnabled) onSelect(value)
                    },
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = value.toString().padStart(2, '0'),
                    style = theme.typography.small.copy(
                        color = when {
                            !optionEnabled -> colors.textDisabled
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

/** Editable single-line field used by `editable` time pickers. */
@Composable
private fun EditableTimeField(
    text: String,
    onTextChange: (String) -> Unit,
    onCommit: () -> Unit,
    onOpen: () -> Unit,
    placeholder: String,
    size: ElSize?,
    disabled: Boolean,
    clearable: Boolean,
    hasValue: Boolean,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val resolvedSize = resolveElSize(size)
    val shape = RoundedCornerShape(theme.radius.base)
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val focusManager = LocalFocusManager.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(resolvedSize.height)
            .clip(shape)
            .background(if (disabled) colors.disabledBackground else colors.fillBlank, shape)
            .border(
                1.dp,
                if (disabled) colors.disabledBorder
                else if (hovered) colors.textDisabled
                else colors.border,
                shape,
            )
            .hoverable(interaction)
            .padding(horizontal = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { if (it.isFocused) onOpen() else onCommit() },
            enabled = !disabled,
            singleLine = true,
            textStyle = theme.typography.base.copy(
                fontSize = resolvedSize.fontSize,
                color = if (disabled) colors.disabledText else colors.textRegular,
            ),
            cursorBrush = SolidColor(colors.primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                onCommit()
                focusManager.clearFocus()
            }),
            decorationBox = { innerTextField ->
                Box {
                    if (text.isEmpty()) {
                        BasicText(
                            text = placeholder,
                            style = theme.typography.base.copy(
                                fontSize = resolvedSize.fontSize,
                                color = colors.textPlaceholder,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    innerTextField()
                }
            },
        )
        if (clearable && hasValue && hovered && !disabled) {
            ElIcon(
                ElIcons.CircleClose,
                size = 14.dp,
                tint = colors.textPlaceholder,
                modifier = Modifier.clickable { onClear() },
            )
            Spacer(Modifier.width(4.dp))
        }
        ElIcon(ElIcons.Clock, size = 14.dp, tint = colors.textPlaceholder)
    }
}

/** Single bordered field with a start/end segment for range time pickers. */
@Composable
private fun TimeRangeField(
    startText: String?,
    endText: String?,
    startPlaceholder: String,
    endPlaceholder: String,
    separator: String,
    size: ElSize?,
    disabled: Boolean,
    clearable: Boolean,
    onClear: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
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
            .border(
                1.dp,
                if (disabled) colors.disabledBorder
                else if (hovered) colors.textDisabled
                else colors.border,
                shape,
            )
            .hoverable(interaction)
            .clickable(enabled = !disabled, onClick = onClick)
            .padding(horizontal = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = startText ?: startPlaceholder,
            modifier = Modifier.weight(1f),
            style = theme.typography.base.copy(
                fontSize = resolvedSize.fontSize,
                color = if (startText != null) colors.textRegular else colors.textPlaceholder,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        BasicText(
            text = separator,
            modifier = Modifier.padding(horizontal = 6.dp),
            style = theme.typography.small.copy(color = colors.textSecondary),
        )
        BasicText(
            text = endText ?: endPlaceholder,
            modifier = Modifier.weight(1f),
            style = theme.typography.base.copy(
                fontSize = resolvedSize.fontSize,
                color = if (endText != null) colors.textRegular else colors.textPlaceholder,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (clearable && (startText != null || endText != null) && hovered && !disabled) {
            ElIcon(
                ElIcons.CircleClose,
                size = 14.dp,
                tint = colors.textPlaceholder,
                modifier = Modifier.clickable { onClear() },
            )
            Spacer(Modifier.width(4.dp))
        }
        ElIcon(ElIcons.Clock, size = 14.dp, tint = colors.textPlaceholder)
    }
}
