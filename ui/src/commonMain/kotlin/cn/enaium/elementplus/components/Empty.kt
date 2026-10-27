package cn.enaium.elementplus.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

/** `<el-empty>` with the default illustration, description and extra slot. */
@Composable
fun ElEmpty(
    modifier: Modifier = Modifier,
    description: String? = null,
    imageSize: Dp = 120.dp,
    image: (@Composable () -> Unit)? = null,
    descriptionContent: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (image != null) {
            image()
        } else {
            EmptyIllustration(imageSize, colors.border, colors.fillLight)
        }
        Spacer(Modifier.height(12.dp))
        if (descriptionContent != null) {
            descriptionContent()
        } else {
            BasicText(
                text = description ?: locale.emptyText,
                style = theme.typography.base.copy(color = colors.textSecondary),
            )
        }
        if (content != null) {
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun EmptyIllustration(size: Dp, border: Color, fill: Color) {
    Canvas(Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cardWidth = w * 0.62f
        val cardHeight = h * 0.42f
        val left = (w - cardWidth) / 2
        val top = h * 0.18f
        drawRoundRect(
            color = fill,
            topLeft = Offset(left, top),
            size = Size(cardWidth, cardHeight),
            cornerRadius = CornerRadius(w * 0.03f),
            style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx()))),
        )
        drawLine(
            color = border,
            start = Offset(left + cardWidth * 0.25f, top + cardHeight * 0.55f),
            end = Offset(left + cardWidth * 0.75f, top + cardHeight * 0.55f),
            strokeWidth = 2.dp.toPx(),
        )
        drawLine(
            color = border,
            start = Offset(left + cardWidth * 0.3f, top + cardHeight * 0.72f),
            end = Offset(left + cardWidth * 0.7f, top + cardHeight * 0.72f),
            strokeWidth = 2.dp.toPx(),
        )
        drawRoundRect(
            color = fill,
            topLeft = Offset(w * 0.2f, h * 0.72f),
            size = Size(w * 0.6f, h * 0.08f),
            cornerRadius = CornerRadius(w * 0.02f),
        )
    }
}
