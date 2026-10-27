package cn.enaium.elementplus.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.round
import kotlin.time.Clock

/**
 * `<el-statistic>` with grouping, precision, separators, prefix/suffix and
 * formatter. The title/prefix/suffix can also be composed with slots.
 */
@Composable
fun ElStatistic(
    value: Double,
    title: String,
    modifier: Modifier = Modifier,
    precision: Int = 0,
    prefix: String? = null,
    suffix: String? = null,
    groupSeparator: String = ",",
    formatter: ((Double) -> String)? = null,
    valueStyle: TextStyle? = null,
    titleStyle: TextStyle? = null,
    color: Color? = null,
    decimalSeparator: String = ".",
    valueText: String? = null,
    titleContent: (@Composable () -> Unit)? = null,
    prefixContent: (@Composable () -> Unit)? = null,
    suffixContent: (@Composable () -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val text = valueText ?: formatter?.invoke(value)
        ?: formatStatistic(value, precision, groupSeparator, decimalSeparator)
    Column(modifier) {
        if (titleContent != null) {
            titleContent()
        } else if (title.isNotEmpty()) {
            BasicText(
                text = title,
                style = titleStyle ?: theme.typography.small.copy(color = colors.textSecondary),
            )
        }
        Row(verticalAlignment = Alignment.Bottom) {
            if (prefixContent != null) {
                prefixContent()
                Spacer(Modifier.width(2.dp))
            } else if (prefix != null) {
                BasicText(
                    text = prefix,
                    style = (valueStyle ?: theme.typography.large.copy(fontWeight = FontWeight.Medium)).copy(
                        fontSize = 18.sp,
                        color = color ?: colors.textPrimary,
                    ),
                )
                Spacer(Modifier.width(2.dp))
            }
            BasicText(
                text = text,
                style = valueStyle ?: theme.typography.large.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = color ?: colors.textPrimary,
                ),
            )
            if (suffixContent != null) {
                Spacer(Modifier.width(2.dp))
                suffixContent()
            } else if (suffix != null) {
                Spacer(Modifier.width(2.dp))
                BasicText(
                    text = suffix,
                    style = (valueStyle ?: theme.typography.large).copy(
                        fontSize = 18.sp,
                        color = color ?: colors.textPrimary,
                    ),
                )
            }
        }
    }
}

/**
 * `<el-countdown>`: a statistic that ticks down to [value] (epoch millis) and
 * formats the remaining time with dayjs-like tokens (`Y`, `M`, `D`, `H`, `m`,
 * `s`, `S`; bracketed text is kept verbatim).
 */
@Composable
fun ElCountdown(
    value: Long,
    modifier: Modifier = Modifier,
    title: String? = null,
    format: String = "HH:mm:ss",
    prefix: String? = null,
    suffix: String? = null,
    valueStyle: TextStyle? = null,
    titleStyle: TextStyle? = null,
    color: Color? = null,
    onFinish: (() -> Unit)? = null,
    onChange: ((Long) -> Unit)? = null,
    titleContent: (@Composable () -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    var now by remember { mutableStateOf(Clock.System.now().toEpochMilliseconds()) }
    LaunchedEffect(value, format) {
        while (true) {
            val current = Clock.System.now().toEpochMilliseconds()
            now = current
            val remaining = (value - current).coerceAtLeast(0L)
            onChange?.invoke(remaining)
            if (remaining <= 0L) {
                onFinish?.invoke()
                break
            }
            delay(1000L - current % 1000L)
        }
    }
    val remaining = (value - now).coerceAtLeast(0L)
    Column(modifier) {
        if (titleContent != null) {
            titleContent()
        } else if (!title.isNullOrEmpty()) {
            BasicText(
                text = title,
                style = titleStyle ?: theme.typography.small.copy(color = colors.textSecondary),
            )
        }
        Row(verticalAlignment = Alignment.Bottom) {
            if (prefix != null) {
                BasicText(
                    text = prefix,
                    style = (valueStyle ?: theme.typography.large.copy(fontWeight = FontWeight.Medium)).copy(
                        fontSize = 18.sp,
                        color = color ?: colors.textPrimary,
                    ),
                )
                Spacer(Modifier.width(2.dp))
            }
            BasicText(
                text = formatCountdown(remaining, format),
                style = valueStyle ?: theme.typography.large.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = color ?: colors.textPrimary,
                ),
            )
            if (suffix != null) {
                Spacer(Modifier.width(2.dp))
                BasicText(
                    text = suffix,
                    style = (valueStyle ?: theme.typography.large).copy(
                        fontSize = 18.sp,
                        color = color ?: colors.textPrimary,
                    ),
                )
            }
        }
    }
}

fun formatStatistic(
    value: Double,
    precision: Int = 0,
    groupSeparator: String = ",",
    decimalSeparator: String = ".",
): String {
    val factor = 10.0.let { base -> generatorPow(base, precision.coerceAtLeast(0)) }
    val rounded = round(value * factor) / factor
    val negative = rounded < 0
    val absolute = abs(rounded)
    val integer = absolute.toLong().toString()
    val grouped = if (groupSeparator.isEmpty()) integer else integer.reversed().chunked(3).joinToString(groupSeparator).reversed()
    val decimals = if (precision > 0) {
        val fraction = round((absolute - absolute.toLong()) * factor).toLong().toString().padStart(precision, '0').take(precision)
        "$decimalSeparator$fraction"
    } else ""
    return (if (negative) "-" else "") + grouped + decimals
}

private val countdownUnits = listOf(
    "Y" to 1000L * 60 * 60 * 24 * 365,
    "M" to 1000L * 60 * 60 * 24 * 30,
    "D" to 1000L * 60 * 60 * 24,
    "H" to 1000L * 60 * 60,
    "m" to 1000L * 60,
    "s" to 1000L,
    "S" to 1L,
)

/** Formats [timestamp] milliseconds with the same tokens as upstream countdown. */
fun formatCountdown(timestamp: Long, format: String = "HH:mm:ss"): String {
    var timeLeft = timestamp
    var text = format
    for ((name, unit) in countdownUnits) {
        val regex = Regex("$name+(?![^\\[\\]]*\\])")
        if (regex.containsMatchIn(text)) {
            val value = timeLeft / unit
            timeLeft -= value * unit
            text = regex.replace(text) { match -> value.toString().padStart(match.value.length, '0') }
        }
    }
    return text.replace(Regex("\\[([^\\]]*)]"), "$1")
}

private fun generatorPow(base: Double, exponent: Int): Double {
    var result = 1.0
    repeat(exponent) { result *= base }
    return result
}
