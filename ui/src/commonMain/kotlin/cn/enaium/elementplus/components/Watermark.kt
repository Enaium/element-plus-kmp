package cn.enaium.elementplus.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * `<el-watermark>`: tiles text or an image over the content. The watermark is
 * drawn after the content, matching Element Plus' overlay behavior.
 *
 * [mark] is the Compose equivalent of Element Plus' `mark` option: when set it
 * receives the draw scope and the tile size, replacing the default text/image
 * rendering for every tile. [image] enables the image watermark (with
 * [imageWidth]/[imageHeight] mirroring `width`/`height`).
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
    image: Painter? = null,
    imageWidth: Dp = 120.dp,
    imageHeight: Dp = 64.dp,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    fontFamily: FontFamily? = null,
    textAlign: TextAlign? = null,
    lineGap: Dp = 3.dp,
    mark: ((DrawScope, Size) -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) {
    val measurer = rememberTextMeasurer()
    val layouts = text.split('\n').map { line ->
        measurer.measure(
            text = line,
            style = TextStyle(
                fontSize = fontSize,
                color = color,
                fontWeight = fontWeight,
                fontStyle = fontStyle,
                fontFamily = fontFamily,
            ),
        )
    }
    val textWidth = layouts.maxOfOrNull { it.size.width }?.toFloat() ?: 0f
    val imagePainter = image
    val markCallback = mark
    val density = LocalDensity.current
    val lineGapPx = with(density) { lineGap.toPx() }
    val textHeight = layouts.sumOf { it.size.height }.toFloat() +
        lineGapPx * (layouts.size - 1).coerceAtLeast(0)
    val tileWidth = if (imagePainter != null) with(density) { imageWidth.toPx() } else textWidth
    val tileHeight = if (imagePainter != null) with(density) { imageHeight.toPx() } else textHeight
    Box(
        modifier
            .fillMaxSize()
            .drawWithContent {
                drawContent()
                val stepX = tileWidth + gapX.toPx()
                val stepY = tileHeight + gapY.toPx()
                clipRect {
                    var y = offsetY.toPx()
                    while (y < size.height + tileHeight) {
                        var x = offsetX.toPx()
                        while (x < size.width + tileWidth) {
                            withTransform({
                                rotate(degrees = rotate, pivot = Offset(x, y))
                            }) {
                                when {
                                    markCallback != null -> {
                                        withTransform({
                                            translate(left = x, top = y)
                                        }) {
                                            markCallback.invoke(this, Size(stepX, stepY))
                                        }
                                    }
                                    imagePainter != null -> {
                                        withTransform({
                                            translate(left = x, top = y)
                                        }) {
                                            with(imagePainter) {
                                                draw(size = Size(tileWidth, tileHeight))
                                            }
                                        }
                                    }
                                    else -> {
                                        var lineY = y
                                        layouts.forEachIndexed { index, lineLayout ->
                                            val alignmentOffset = when (textAlign) {
                                                TextAlign.Center -> (textWidth - lineLayout.size.width) / 2f
                                                TextAlign.Right, TextAlign.End ->
                                                    textWidth - lineLayout.size.width
                                                else -> 0f
                                            }
                                            drawText(
                                                lineLayout,
                                                topLeft = Offset(x + alignmentOffset, lineY),
                                            )
                                            lineY += lineLayout.size.height
                                            if (index != layouts.lastIndex) lineY += lineGapPx
                                        }
                                    }
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
