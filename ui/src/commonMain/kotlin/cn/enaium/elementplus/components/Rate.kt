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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.rememberElInteractionState
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElTheme
import kotlin.math.ceil

/**
 * `<el-rate>` with half stars, colors, texts and clear-on-same-click.
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
) {
    val theme = LocalElTheme.current
    val resolvedSize = resolveElSize(size)
    val starSize = when (resolvedSize) {
        ElSize.Large -> 22.dp
        ElSize.Default -> 18.dp
        ElSize.Small -> 14.dp
    }
    val inactive = theme.colors.textDisabled
    val fillColor = when {
        colors.isEmpty() -> Color(0xFFF7BA2A)
        colors.size == 1 -> colors[0]
        else -> {
            val index = ((value / max) * (colors.size - 1)).toInt().coerceIn(0, colors.size - 1)
            colors[index]
        }
    }
    var hovered by remember { mutableIntStateOf(0) }
    val display = if (hovered > 0) hovered.toDouble() else value

    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
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
                            val resolved = if (clearable && !half && next == value) 0.0 else next
                            onValueChange(resolved)
                        }
                    },
            ) {
                ElIcon(voidIcon, size = starSize, tint = inactive)
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
                    ElIcon(icon, size = starSize, tint = fillColor)
                }
            }
        }
        if (showText || showScore) {
            Spacer(Modifier.width(8.dp))
            val text = when {
                showScore -> display.toString()
                textFor(display, texts) != null -> textFor(display, texts) ?: ""
                else -> ""
            }
            if (text.isNotEmpty()) {
                BasicText(
                    text = text,
                    style = theme.typography.small.copy(color = theme.colors.textSecondary),
                )
            }
        }
    }
}

private fun textFor(value: Double, texts: List<String>): String? {
    if (texts.isEmpty()) return null
    val index = ceil(value).toInt() - 1
    return texts.getOrNull(index)
}
