package cn.enaium.elementplus.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.elementplus.theme.LocalElTheme
import kotlin.math.abs
import kotlin.math.round

/** `<el-statistic>` with grouping, precision, prefix/suffix and formatter. */
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
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val text = formatter?.invoke(value) ?: formatStatistic(value, precision, groupSeparator)
    androidx.compose.foundation.layout.Column(modifier) {
        BasicText(
            text = title,
            style = titleStyle ?: theme.typography.small.copy(color = colors.textSecondary),
        )
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
                text = text,
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

fun formatStatistic(value: Double, precision: Int = 0, groupSeparator: String = ","): String {
    val factor = 10.0.let { base -> generatorPow(base, precision.coerceAtLeast(0)) }
    val rounded = round(value * factor) / factor
    val negative = rounded < 0
    val absolute = abs(rounded)
    val integer = absolute.toLong().toString()
    val grouped = if (groupSeparator.isEmpty()) integer else integer.reversed().chunked(3).joinToString(groupSeparator).reversed()
    val decimals = if (precision > 0) {
        val fraction = round((absolute - absolute.toLong()) * factor).toLong().toString().padStart(precision, '0').take(precision)
        ".$fraction"
    } else ""
    return (if (negative) "-" else "") + grouped + decimals
}

private fun generatorPow(base: Double, exponent: Int): Double {
    var result = 1.0
    repeat(exponent) { result *= base }
    return result
}
