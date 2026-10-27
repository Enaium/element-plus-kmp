package cn.enaium.elementplus.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.elementplus.theme.ElConfig
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import cn.enaium.elementplus.theme.LocalElContentColor

/** Form items override the global component size for their subtree. */
val LocalElFormSize = staticCompositionLocalOf<ElSize?> { null }

@Composable
fun resolveElSize(size: ElSize? = null): ElSize =
    size ?: LocalElFormSize.current ?: LocalElConfig.current.size ?: ElSize.Default

@Immutable
data class ElInteractiveState(
    val hovered: Boolean,
    val pressed: Boolean,
    val focused: Boolean,
)

@Composable
fun rememberElInteractionState(source: MutableInteractionSource): ElInteractiveState {
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    val focused by source.collectIsFocusedAsState()
    return ElInteractiveState(hovered = hovered, pressed = pressed, focused = focused)
}

/** A subtle pressed/hover tint helpers used by buttons, menu items, etc. */
fun hoverTint(base: Color, hover: Color, active: Boolean): Color =
    if (active) hover else base

/** Draws a rounded border with an optional dashed stroke. */
fun Modifier.elBorder(
    color: Color,
    width: Dp = 1.dp,
    radius: Dp = 4.dp,
    dashed: Boolean = false,
): Modifier = drawBehind {
    val strokeWidth = width.toPx()
    val half = strokeWidth / 2f
    val radiusPx = radius.toPx()
    drawRoundRect(
        color = color,
        topLeft = Offset(half, half),
        size = Size(size.width - strokeWidth, size.height - strokeWidth),
        cornerRadius = CornerRadius(radiusPx, radiusPx),
        style = Stroke(
            width = strokeWidth,
            pathEffect = if (dashed) PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())) else null,
        ),
    )
}

/**
 * A soft symmetric shadow similar to Element Plus' `box-shadow-light`
 * (`0 0 12px rgba(0, 0, 0, 0.12)`). Compose's elevation shadow is offset and
 * much darker, so the halo is painted explicitly with concentric translucent
 * rounded rectangles.
 */
fun Modifier.elSoftShadow(
    cornerRadius: Dp,
    blur: Dp = 12.dp,
    alpha: Float = 0.08f,
): Modifier = drawBehind {
    val steps = 24
    val blurPx = blur.toPx()
    val radiusPx = cornerRadius.toPx()
    val layerAlpha = alpha / steps
    for (i in 1..steps) {
        val spread = blurPx * i / steps
        drawRoundRect(
            color = Color.Black.copy(alpha = layerAlpha),
            topLeft = Offset(-spread, -spread),
            size = Size(size.width + spread * 2, size.height + spread * 2),
            cornerRadius = CornerRadius(radiusPx + spread * 0.8f, radiusPx + spread * 0.8f),
        )
    }
}

fun Modifier.elDashedBorder(
    color: Color,
    radius: Dp,
    width: Dp = 1.dp,
): Modifier = drawBehind {
    val strokeWidth = width.toPx()
    val half = strokeWidth / 2f
    val radiusPx = radius.toPx()
    drawRoundRect(
        color = color,
        topLeft = Offset(half, half),
        size = Size(size.width - strokeWidth, size.height - strokeWidth),
        cornerRadius = CornerRadius(radiusPx, radiusPx),
        style = Stroke(
            width = strokeWidth,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())),
        ),
    )
}

fun Modifier.elDottedBorder(
    color: Color,
    radius: Dp,
    width: Dp = 1.dp,
): Modifier = drawBehind {
    val strokeWidth = width.toPx()
    val half = strokeWidth / 2f
    val radiusPx = radius.toPx()
    drawRoundRect(
        color = color,
        topLeft = Offset(half, half),
        size = Size(size.width - strokeWidth, size.height - strokeWidth),
        cornerRadius = CornerRadius(radiusPx, radiusPx),
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.1f, 4.dp.toPx())),
        ),
    )
}

fun Modifier.elBottomBorder(color: Color, width: Dp = 1.dp): Modifier = drawBehind {
    drawRect(color = color, topLeft = Offset(0f, size.height - width.toPx()), size = Size(size.width, width.toPx()))
}

fun Modifier.elTopBorder(color: Color, width: Dp = 1.dp): Modifier = drawBehind {
    drawRect(color = color, size = Size(size.width, width.toPx()))
}

fun Modifier.elLeftBorder(color: Color, width: Dp = 1.dp): Modifier = drawBehind {
    drawRect(color = color, size = Size(width.toPx(), size.height))
}

/** Applies the common form-control surface. */
fun Modifier.elFieldBackground(color: Color, radius: Dp = 4.dp): Modifier =
    background(color, RoundedCornerShape(radius)).clip(RoundedCornerShape(radius))

@Composable
fun ElPlaceholder(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style.copy(color = LocalElTheme.current.colors.textPlaceholder),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun ElIconText(
    text: String,
    icon: @Composable (() -> Unit)? = null,
    style: TextStyle = LocalElTheme.current.typography.base,
    color: Color = LocalElTheme.current.colors.textRegular,
    gap: Dp = 6.dp,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            icon()
            Spacer(Modifier.width(gap))
        }
        BasicText(text = text, style = style.copy(color = color), maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/** Increments/decrements a nullable numeric value safely. */
fun Double.coerceAtLeastSafe(min: Double?): Double = min?.let { coerceAtLeast(it) } ?: this

fun formatNumber(value: Double, precision: Int? = null): String =
    if (precision == null) {
        if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()
    } else {
        val factor = pow10(precision)
        val rounded = kotlin.math.round(value * factor) / factor
        val fixed = rounded.toString()
        if (precision == 0) fixed.substringBefore('.') else {
            val intPart = fixed.substringBefore('.')
            val dec = fixed.substringAfter('.', "").padEnd(precision, '0').take(precision)
            "$intPart.$dec"
        }
    }

private fun pow10(precision: Int): Double {
    var result = 1.0
    repeat(precision.coerceAtMost(10)) { result *= 10 }
    return result
}

/** Light/dark aware disabled foreground. */
@Composable
fun disabledContentColor(): Color = LocalElTheme.current.colors.textDisabled

val ElDefaultTextStyle: TextStyle
    get() = TextStyle(fontSize = 14.sp, lineHeight = 21.sp, fontWeight = FontWeight.Normal)

@Composable
fun defaultTextStyle(): TextStyle = LocalElTheme.current.typography.base

@Composable
fun defaultContentColor(): Color =
    LocalElContentColor.current.takeIf { it != Color.Unspecified } ?: LocalElTheme.current.colors.textRegular
