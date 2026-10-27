package cn.enaium.elementplus.icons

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.theme.LocalElContentColor
import cn.enaium.elementplus.theme.LocalElTheme

/**
 * Drop-in replacement for `<el-icon>`.
 *
 * Icons inherit the surrounding text color unless [tint] is given. [spin] keeps
 * the icon rotating (used by loading indicators).
 */
@Composable
fun ElIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    size: Dp = 16.dp,
    tint: Color = LocalElContentColor.current.takeIf { it != Color.Unspecified }
        ?: LocalElTheme.current.colors.textRegular,
    spin: Boolean = false,
) {
    val rotation = if (spin) {
        val transition = rememberInfiniteTransition(label = "el-icon-spin")
        val value by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Restart),
            label = "el-icon-rotation",
        )
        value
    } else {
        0f
    }
    Image(
        painter = rememberVectorPainter(icon),
        contentDescription = contentDescription,
        modifier = modifier
            .size(size)
            .graphicsLayer { rotationZ = rotation },
        colorFilter = ColorFilter.tint(tint),
        contentScale = ContentScale.Fit,
    )
}
