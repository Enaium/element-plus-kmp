package cn.enaium.elementplus.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.rememberElInteractionState
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElTheme
import kotlin.math.ceil
import kotlin.math.floor

/**
 * `<el-rate>` with half stars, threshold based colors/icons, custom void
 * colors and clear-on-same-click.
 *
 * @param lowThreshold / [highThreshold] score thresholds used to pick a color
 *   (and icon) from [colors] / [icons]; the low threshold value itself belongs
 *   to the low level, the high threshold value belongs to the high level.
 * @param icons per-level active icons (`[low, medium, high]`), falling back to
 *   [icon].
 * @param voidColor / [disabledVoidColor] color of unselected icons, and of
 *   unselected icons while disabled.
 * @param disabledVoidIcon icon for unselected stars while disabled.
 * @param scoreTemplate template for [showScore], where `{value}` is replaced by
 *   the current score.
 * @param onChange commit event fired when the selected score changes.
 */
@Composable
fun ElRate(
    value: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    max: Int = 5,
    disabled: Boolean = false,
    readOnly: Boolean = false,
    allowHalf: Boolean = false,
    clearable: Boolean = true,
    showText: Boolean = false,
    showScore: Boolean = false,
    texts: List<String> = emptyList(),
    colors: List<Color> = emptyList(),
    size: ElSize? = null,
    icon: ImageVector = ElIcons.StarFilled,
    voidIcon: ImageVector = ElIcons.Star,
    lowThreshold: Int = 2,
    highThreshold: Int = 4,
    voidColor: Color? = null,
    disabledVoidColor: Color? = null,
    disabledVoidIcon: ImageVector? = null,
    icons: List<ImageVector> = emptyList(),
    textColor: Color? = null,
    scoreTemplate: String = "{value}",
    onChange: ((Double) -> Unit)? = null,
    ariaLabel: String? = null,
) {
    val theme = LocalElTheme.current
    val resolvedSize = resolveElSize(size)
    val starSize = when (resolvedSize) {
        ElSize.Large -> 22.dp
        ElSize.Default -> 18.dp
        ElSize.Small -> 14.dp
    }
    val inertColor = if (disabled) {
        disabledVoidColor ?: voidColor ?: theme.colors.textDisabled
    } else {
        voidColor ?: theme.colors.textDisabled
    }
    val inertIcon = if (disabled) disabledVoidIcon ?: voidIcon else voidIcon
    val latestOnChange by rememberUpdatedState(onChange)
    val rateLabel = ariaLabel
    var hovered by remember { mutableIntStateOf(0) }
    val display = if (hovered > 0) hovered.toDouble() else value

    fun levelIndex(v: Double): Int = when {
        v <= lowThreshold -> 0
        v < highThreshold -> 1
        else -> 2
    }

    fun fillColor(v: Double): Color {
        if (colors.isEmpty()) return Color(0xFFF7BA2A)
        if (colors.size == 1) return colors[0]
        val index = levelIndex(v)
        return colors.getOrNull(index) ?: colors.last()
    }

    fun fillIcon(v: Double): ImageVector {
        if (icons.isEmpty()) return icon
        if (icons.size == 1) return icons[0]
        return icons.getOrNull(levelIndex(v)) ?: icons.last()
    }

    Row(
        modifier = modifier
            .then(
                if (rateLabel != null) {
                    Modifier.semantics { contentDescription = rateLabel }
                } else {
                    Modifier
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        repeat(max) { index ->
            val source = remember { MutableInteractionSource() }
            val state = rememberElInteractionState(source)
            val fraction = (display - index).coerceIn(0.0, 1.0).toFloat()
            androidx.compose.runtime.LaunchedEffect(state.hovered) {
                if (state.hovered && !disabled && !readOnly) hovered = index + 1
                else if (!state.hovered && hovered == index + 1) hovered = 0
            }
            Box(
                modifier = Modifier
                    .size(starSize)
                    .hoverable(source)
                    .pointerInput(disabled, readOnly, allowHalf, index) {
                        detectTapGestures { offset ->
                            if (disabled || readOnly) return@detectTapGestures
                            val half = allowHalf && offset.x < starSize.toPx() / 2f
                            val next = index + if (half) 0.5 else 1.0
                            val resolved = if (clearable && next == value) 0.0 else next
                            onValueChange(resolved)
                            if (resolved != value) latestOnChange?.invoke(resolved)
                        }
                    },
            ) {
                ElIcon(inertIcon, size = starSize, tint = inertColor)
                Box(
                    Modifier
                        .size(starSize)
                        .clipToBounds()
                        .drawWithContent {
                            clipRect(left = 0f, top = 0f, right = this.size.width * fraction, bottom = this.size.height) {
                                this@drawWithContent.drawContent()
                            }
                        },
                ) {
                    ElIcon(fillIcon(display), size = starSize, tint = fillColor(display))
                }
            }
        }
        if (showText || showScore) {
            Spacer(Modifier.width(8.dp))
            val text = when {
                showScore -> scoreTemplate.replace(
                    "{value}",
                    formatScore(if (disabled) value else display),
                )
                else -> textFor(display, texts) ?: ""
            }
            if (text.isNotEmpty()) {
                BasicText(
                    text = text,
                    style = theme.typography.small.copy(
                        color = textColor ?: theme.colors.textSecondary,
                    ),
                )
            }
        }
    }
}

private fun formatScore(value: Double): String =
    if (value == floor(value)) value.toInt().toString() else value.toString()

private fun textFor(value: Double, texts: List<String>): String? {
    if (texts.isEmpty()) return null
    val index = ceil(value).toInt() - 1
    return texts.getOrNull(index)
}
