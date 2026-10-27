package cn.enaium.elementplus.internal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Small triangular pointer used by tooltip/popover/popconfirm surfaces. */
@Composable
fun ElPopperArrow(
    placement: ElPlacement,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 8.dp,
) {
    Canvas(modifier.size(size * 2)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path()
        when {
            placement.isTop -> {
                path.moveTo(0f, 0f)
                path.lineTo(w, 0f)
                path.lineTo(w / 2f, h)
            }
            placement.isBottom -> {
                path.moveTo(w / 2f, 0f)
                path.lineTo(0f, h)
                path.lineTo(w, h)
            }
            placement.isLeft -> {
                path.moveTo(0f, 0f)
                path.lineTo(w, h / 2f)
                path.lineTo(0f, h)
            }
            else -> {
                path.moveTo(w, 0f)
                path.lineTo(w, h)
                path.lineTo(0f, h / 2f)
            }
        }
        path.close()
        drawPath(path, color)
    }
}
