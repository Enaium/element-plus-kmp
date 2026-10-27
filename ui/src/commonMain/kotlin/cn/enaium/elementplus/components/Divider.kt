package cn.enaium.elementplus.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElDividerDirection { Horizontal, Vertical }
enum class ElDividerBorderStyle { Solid, Dashed, Dotted }
enum class ElDividerContentPosition { Left, Center, Right }

/**
 * `<el-divider>` with optional inline content and border style.
 */
@Composable
fun ElDivider(
    modifier: Modifier = Modifier,
    direction: ElDividerDirection = ElDividerDirection.Horizontal,
    borderStyle: ElDividerBorderStyle = ElDividerBorderStyle.Solid,
    contentPosition: ElDividerContentPosition = ElDividerContentPosition.Center,
    color: Color = LocalElTheme.current.colors.border,
    thickness: Dp = 1.dp,
    content: (@Composable () -> Unit)? = null,
) {
    if (direction == ElDividerDirection.Vertical) {
        Box(modifier.fillMaxHeight().width(thickness).elLine(color, vertical = true, borderStyle = borderStyle))
        return
    }
    if (content == null) {
        Box(modifier.fillMaxWidth().height(thickness).elLine(color, vertical = false, borderStyle = borderStyle))
        return
    }
    val theme = LocalElTheme.current
    androidx.compose.foundation.layout.Row(
        modifier = modifier.fillMaxWidth().height(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val leftWeight = when (contentPosition) {
            ElDividerContentPosition.Left -> 1f
            ElDividerContentPosition.Center -> 1f
            ElDividerContentPosition.Right -> 3f
        }
        val rightWeight = when (contentPosition) {
            ElDividerContentPosition.Left -> 3f
            ElDividerContentPosition.Center -> 1f
            ElDividerContentPosition.Right -> 1f
        }
        Box(Modifier.weight(leftWeight).height(thickness).elLine(color, false, borderStyle))
        Box(Modifier.padding(horizontal = 20.dp)) {
            content()
        }
        Box(Modifier.weight(rightWeight).height(thickness).elLine(color, false, borderStyle))
    }
}

private fun Modifier.elLine(
    color: Color,
    vertical: Boolean,
    borderStyle: ElDividerBorderStyle,
): Modifier = this.drawBehind {
    val stroke = Stroke(
        width = 1.dp.toPx(),
        pathEffect = when (borderStyle) {
            ElDividerBorderStyle.Solid -> null
            ElDividerBorderStyle.Dashed -> PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx()))
            ElDividerBorderStyle.Dotted -> PathEffect.dashPathEffect(floatArrayOf(2.dp.toPx(), 4.dp.toPx()))
        },
    )
    if (vertical) {
        drawLine(
            color = color,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height),
            strokeWidth = 1.dp.toPx(),
            pathEffect = stroke.pathEffect,
        )
    } else {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 1.dp.toPx(),
            pathEffect = stroke.pathEffect,
        )
    }
}
