package cn.enaium.elementplus.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.abs
import kotlin.math.roundToInt

/** Format used when displaying a color in [ColorPicker] and its panel. */
enum class ElColorFormat { Hex, Rgb, Hsl }

fun Color.toHex(includeAlpha: Boolean = false): String {
    val argb = toArgb()
    val r = (argb shr 16) and 0xFF
    val g = (argb shr 8) and 0xFF
    val b = argb and 0xFF
    val a = (argb ushr 24) and 0xFF
    val base = "#" + hex2(r) + hex2(g) + hex2(b)
    return if (includeAlpha) base + hex2(a) else base
}

private fun hex2(value: Int): String = value.toString(16).padStart(2, '0').uppercase()

fun Color.toRgbString(includeAlpha: Boolean = false): String {
    val argb = toArgb()
    val r = (argb shr 16) and 0xFF
    val g = (argb shr 8) and 0xFF
    val b = argb and 0xFF
    val a = ((argb ushr 24) and 0xFF) / 255.0
    return if (includeAlpha) "rgba($r, $g, $b, ${formatAlpha(a)})" else "rgb($r, $g, $b)"
}

fun Color.toHslString(includeAlpha: Boolean = false): String {
    val (h, s, l) = rgbToHsl(this)
    val alpha = if (includeAlpha) ", ${formatAlpha(alpha.toDouble())}" else ""
    return "hsla(${h.roundToInt()}, ${(s * 100).roundToInt()}%, ${(l * 100).roundToInt()}%$alpha)"
}

fun formatElColor(color: Color, format: ElColorFormat, showAlpha: Boolean = false): String = when (format) {
    ElColorFormat.Hex -> color.toHex(showAlpha)
    ElColorFormat.Rgb -> color.toRgbString(showAlpha)
    ElColorFormat.Hsl -> color.toHslString(showAlpha)
}

private fun formatAlpha(alpha: Double): String {
    val rounded = (alpha * 100).roundToInt() / 100.0
    return if (rounded == rounded.roundToInt().toDouble()) rounded.roundToInt().toString() else rounded.toString()
}

/** Parses `#rgb`, `#rgba`, `#rrggbb`, `#rrggbbaa`, `rgb()` and `rgba()`. */
fun parseElColor(input: String): Color? {
    val text = input.trim().lowercase()
    if (text.isEmpty()) return null
    if (text.startsWith("#")) {
        val hex = text.drop(1)
        return when (hex.length) {
            3 -> Color(
                red = hex.substring(0, 1).repeat(2).toInt(16),
                green = hex.substring(1, 2).repeat(2).toInt(16),
                blue = hex.substring(2, 3).repeat(2).toInt(16),
            )
            4 -> Color(
                red = hex.substring(0, 1).repeat(2).toInt(16),
                green = hex.substring(1, 2).repeat(2).toInt(16),
                blue = hex.substring(2, 3).repeat(2).toInt(16),
                alpha = hex.substring(3, 4).repeat(2).toInt(16),
            )
            6 -> Color(
                red = hex.substring(0, 2).toIntOrNull(16) ?: return null,
                green = hex.substring(2, 4).toIntOrNull(16) ?: return null,
                blue = hex.substring(4, 6).toIntOrNull(16) ?: return null,
            )
            8 -> Color(
                red = hex.substring(0, 2).toIntOrNull(16) ?: return null,
                green = hex.substring(2, 4).toIntOrNull(16) ?: return null,
                blue = hex.substring(4, 6).toIntOrNull(16) ?: return null,
                alpha = hex.substring(6, 8).toIntOrNull(16) ?: return null,
            )
            else -> null
        }
    }
    if (text.startsWith("hsl")) {
        val values = text.substringAfter('(').substringBefore(')').split(',').map { it.trim() }
        if (values.size !in 3..4) return null
        val hue = values[0].removeSuffix("deg").toFloatOrNull() ?: return null
        val saturation = values[1].removeSuffix("%").toFloatOrNull()?.div(100f) ?: return null
        val lightness = values[2].removeSuffix("%").toFloatOrNull()?.div(100f) ?: return null
        val alpha = if (values.size == 4) {
            val raw = values[3]
            if (raw.endsWith("%")) raw.dropLast(1).toFloatOrNull()?.div(100f) ?: return null
            else raw.toFloatOrNull() ?: return null
        } else 1f
        return hslToColor(hue, saturation.coerceIn(0f, 1f), lightness.coerceIn(0f, 1f), alpha.coerceIn(0f, 1f))
    }
    if (text.startsWith("rgb")) {
        val values = text.substringAfter('(').substringBefore(')').split(',').map { it.trim() }
        if (values.size !in 3..4) return null
        fun channel(v: String): Int = if (v.endsWith("%")) {
            (v.dropLast(1).toFloatOrNull()?.times(2.55f) ?: return -1).roundToInt().coerceIn(0, 255)
        } else v.toFloatOrNull()?.roundToInt()?.coerceIn(0, 255) ?: -1
        val r = channel(values[0]); val g = channel(values[1]); val b = channel(values[2])
        if (r < 0 || g < 0 || b < 0) return null
        val a = if (values.size == 4) {
            val raw = values[3]
            if (raw.endsWith("%")) (raw.dropLast(1).toFloatOrNull()?.div(100f) ?: return null)
            else raw.toFloatOrNull() ?: return null
        } else 1f
        return Color(r, g, b, (a.coerceIn(0f, 1f) * 255).roundToInt())
    }
    return null
}

fun rgbToHsv(color: Color): Triple<Float, Float, Float> {
    val r = color.red
    val g = color.green
    val b = color.blue
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min
    var h = when {
        delta < 1e-6f -> 0f
        max == r -> 60f * (((g - b) / delta) % 6f)
        max == g -> 60f * (((b - r) / delta) + 2f)
        else -> 60f * (((r - g) / delta) + 4f)
    }
    if (h < 0) h += 360f
    val s = if (max == 0f) 0f else delta / max
    return Triple(h, s, max)
}

fun hsvToColor(hue: Float, saturation: Float, value: Float, alpha: Float = 1f): Color {
    val h = ((hue % 360f) + 360f) % 360f
    val c = value * saturation
    val x = c * (1 - abs((h / 60f) % 2f - 1f))
    val m = value - c
    val (r, g, b) = when {
        h < 60f -> Triple(c, x, 0f)
        h < 120f -> Triple(x, c, 0f)
        h < 180f -> Triple(0f, c, x)
        h < 240f -> Triple(0f, x, c)
        h < 300f -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }
    return Color(r + m, g + m, b + m, alpha)
}

fun rgbToHsl(color: Color): Triple<Float, Float, Float> {
    val r = color.red
    val g = color.green
    val b = color.blue
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val l = (max + min) / 2f
    val delta = max - min
    if (delta < 1e-6f) return Triple(0f, 0f, l)
    val s = if (l > 0.5f) delta / (2f - max - min) else delta / (max + min)
    var h = when {
        max == r -> 60f * (((g - b) / delta) % 6f)
        max == g -> 60f * (((b - r) / delta) + 2f)
        else -> 60f * (((r - g) / delta) + 4f)
    }
    if (h < 0) h += 360f
    return Triple(h, s, l)
}

fun hslToColor(hue: Float, saturation: Float, lightness: Float, alpha: Float = 1f): Color {
    val h = ((hue % 360f) + 360f) % 360f
    val c = (1f - abs(2f * lightness - 1f)) * saturation
    val x = c * (1f - abs((h / 60f) % 2f - 1f))
    val m = lightness - c / 2f
    val (r, g, b) = when {
        h < 60f -> Triple(c, x, 0f)
        h < 120f -> Triple(x, c, 0f)
        h < 180f -> Triple(0f, c, x)
        h < 240f -> Triple(0f, x, c)
        h < 300f -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }
    return Color(r + m, g + m, b + m, alpha)
}

fun Color.withAlphaValue(alpha: Float): Color = copy(alpha = alpha.coerceIn(0f, 1f))
