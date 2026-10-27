package cn.enaium.elementplus.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * `<el-watermark>`: tiles text over the content. The watermark is drawn after
 * the content, matching Element Plus' overlay behavior.
 */
@Composable
fun ElWatermark(
    modifier: Modifier = Modifier,
    text: String = "Element Plus",
    fontSize: TextUnit = 14.sp,
    color: Color = Color.Black.copy(alpha = 0.16f),
    rotate: Float = -22f,
    gapX: Dp = 100.dp,
    gapY: Dp = 100.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    content: (@Composable () -> Unit)? = null,
) {
    val measurer = rememberTextMeasurer()
    val layouts = text.split('\n').map { line ->
        measurer.measure(text = line, style = TextStyle(fontSize = fontSize, color = color))
    }
    val textWidth = layouts.maxOfOrNull { it.size.width }?.toFloat() ?: 0f
    val textHeight = layouts.sumOf { it.size.height }.toFloat()
    Box(
        modifier
            .fillMaxSize()
            .drawWithContent {
                drawContent()
                val stepX = textWidth + gapX.toPx()
                val stepY = textHeight + gapY.toPx()
                clipRect {
                    var y = offsetY.toPx()
                    while (y < size.height + textHeight) {
                        var x = offsetX.toPx()
                        while (x < size.width + textWidth) {
                            withTransform({
                                rotate(degrees = rotate, pivot = Offset(x, y))
                            }) {
                                var lineY = y
                                layouts.forEach { lineLayout ->
                                    drawText(lineLayout, topLeft = Offset(x, lineY))
                                    lineY += lineLayout.size.height
                                }
                            }
                            x += stepX
                        }
                        y += stepY
                    }
                }
            },
    ) {
        content?.invoke()
    }
}
