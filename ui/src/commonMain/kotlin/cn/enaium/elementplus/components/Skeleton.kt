package cn.enaium.elementplus.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElSkeletonVariant { Text, H1, H3, Caption, Button, Image, Circle, Rect }

/**
 * `<el-skeleton>`. When [loading] is false and [content] is provided the real
 * content is rendered instead of the placeholders.
 */
@Composable
fun ElSkeleton(
    modifier: Modifier = Modifier,
    loading: Boolean = true,
    animated: Boolean = true,
    rows: Int = 3,
    avatar: Boolean = false,
    title: Boolean = true,
    content: (@Composable () -> Unit)? = null,
) {
    if (!loading && content != null) {
        content()
        return
    }
    val theme = LocalElTheme.current
    val alpha = if (animated) skeletonAlpha() else 1f
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (avatar || title) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (avatar) {
                    SkeletonBlock(40.dp, 40.dp, CircleShape, theme.colors.fill, alpha)
                    Spacer(Modifier.width(12.dp))
                }
                if (title) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SkeletonBlock(Modifier.fillMaxWidth(0.45f), 16.dp, theme.colors.fill, alpha)
                        SkeletonBlock(Modifier.fillMaxWidth(0.3f), 12.dp, theme.colors.fill, alpha)
                    }
                }
            }
        }
        repeat(rows) { index ->
            val fraction = when (index % 3) {
                0 -> 1f
                1 -> 0.9f
                else -> 0.65f
            }
            SkeletonBlock(Modifier.fillMaxWidth(fraction), 14.dp, theme.colors.fill, alpha)
        }
    }
}

@Composable
fun ElSkeletonItem(
    variant: ElSkeletonVariant = ElSkeletonVariant.Text,
    modifier: Modifier = Modifier,
    animated: Boolean = true,
) {
    val theme = LocalElTheme.current
    val alpha = if (animated) skeletonAlpha() else 1f
    when (variant) {
        ElSkeletonVariant.Circle -> SkeletonBlock(40.dp, 40.dp, CircleShape, theme.colors.fill, alpha, modifier)
        ElSkeletonVariant.Image -> SkeletonBlock(Modifier.fillMaxWidth().height(100.dp), theme.colors.fill, alpha)
        ElSkeletonVariant.Button -> SkeletonBlock(80.dp, 32.dp, RoundedCornerShape(4.dp), theme.colors.fill, alpha, modifier)
        ElSkeletonVariant.Rect -> SkeletonBlock(Modifier.fillMaxWidth().height(16.dp), theme.colors.fill, alpha)
        ElSkeletonVariant.H1 -> SkeletonBlock(Modifier.fillMaxWidth(0.35f).height(28.dp), theme.colors.fill, alpha)
        ElSkeletonVariant.H3 -> SkeletonBlock(Modifier.fillMaxWidth(0.25f).height(22.dp), theme.colors.fill, alpha)
        ElSkeletonVariant.Caption -> SkeletonBlock(Modifier.width(80.dp).height(12.dp), theme.colors.fill, alpha)
        ElSkeletonVariant.Text -> SkeletonBlock(Modifier.fillMaxWidth().height(14.dp), theme.colors.fill, alpha)
    }
}

@Composable
private fun skeletonAlpha(): Float {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "skeleton-alpha",
    )
    return alpha
}

@Composable
private fun SkeletonBlock(
    width: Dp,
    height: Dp,
    shape: androidx.compose.ui.graphics.Shape,
    color: Color,
    alpha: Float,
    modifier: Modifier = Modifier,
) {
    Box(modifier.width(width).height(height).background(color.copy(alpha = alpha), shape))
}

@Composable
private fun SkeletonBlock(
    modifier: Modifier,
    height: Dp,
    color: Color,
    alpha: Float,
) {
    Box(modifier.height(height).background(color.copy(alpha = alpha), RoundedCornerShape(2.dp)))
}

@Composable
private fun SkeletonBlock(
    modifier: Modifier,
    color: Color,
    alpha: Float,
) {
    Box(modifier.background(color.copy(alpha = alpha), RoundedCornerShape(2.dp)))
}
