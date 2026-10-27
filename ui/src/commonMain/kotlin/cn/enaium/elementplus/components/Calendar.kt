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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.rememberElInteractionState
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/**
 * `<el-calendar>`: month view with previous/next/today controls and optional
 * custom date cells.
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
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    var visibleMonth by remember(value) { mutableStateOf(LocalDate(value.year, value.month.number, 1)) }
    val today = remember { kotlin.time.Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date }
    val first = LocalDate(visibleMonth.year, visibleMonth.month.number, 1)
    val offset = first.dayOfWeek.isoDayNumber % 7
    val start = first.minus(offset, DateTimeUnit.DAY)
    Column(
        modifier
            .fillMaxWidth()
            .border(1.dp, colors.borderLight, RoundedCornerShape(4.dp))
            .background(colors.background, RoundedCornerShape(4.dp)),
    ) {
        if (header != null) {
            header(
                visibleMonth,
                { visibleMonth = visibleMonth.minus(1, DateTimeUnit.MONTH) },
                { visibleMonth = visibleMonth.plus(1, DateTimeUnit.MONTH) },
                {
                    visibleMonth = LocalDate(today.year, today.month.number, 1)
                    onValueChange(today)
                },
            )
        } else {
            Row(
                Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText(
                    text = "${locale.datePicker.months[visibleMonth.month.number - 1]} ${visibleMonth.year}",
                    style = theme.typography.base.copy(color = colors.textPrimary, fontWeight = FontWeight.Medium),
                )
                Spacer(Modifier.weight(1f))
                CalendarNav(ElIcons.ArrowLeft) { visibleMonth = visibleMonth.minus(1, DateTimeUnit.MONTH) }
                CalendarNav(locale.datePicker.today) {
                    visibleMonth = LocalDate(today.year, today.month.number, 1)
                    onValueChange(today)
                }
                CalendarNav(ElIcons.ArrowRight) { visibleMonth = visibleMonth.plus(1, DateTimeUnit.MONTH) }
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
        repeat(6) { week ->
            Row(Modifier.fillMaxWidth().height(cellHeight)) {
                repeat(7) { day ->
                    val date = start.plus(week * 7 + day, DateTimeUnit.DAY)
                    val inMonth = date.month.number == visibleMonth.month.number && date.year == visibleMonth.year
                    val selected = date == value
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
                                    hovered -> colors.fillLighter
                                    else -> Color.Transparent
                                }
                            )
                            .hoverable(interaction)
                            .clickable { onValueChange(date) }
                            .padding(4.dp),
                    ) {
                        if (dateCell != null) {
                            dateCell(date, selected, inMonth)
                        } else {
                            BasicText(
                                text = date.day.toString(),
                                modifier = Modifier.align(Alignment.TopEnd),
                                style = theme.typography.small.copy(
                                    color = when {
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
private fun CalendarNav(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(Modifier.padding(horizontal = 6.dp).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        ElIcon(icon, size = 12.dp, tint = LocalElTheme.current.colors.textSecondary)
    }
}
