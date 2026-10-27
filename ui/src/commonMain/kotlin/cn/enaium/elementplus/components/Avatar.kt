package cn.enaium.elementplus.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.ElPlacement
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElAvatarShape { Circle, Square }

/** `<el-avatar>` with image, icon, initials and a custom fallback. */
@Composable
fun ElAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    shape: ElAvatarShape = ElAvatarShape.Circle,
    painter: Painter? = null,
    text: String? = null,
    icon: ImageVector = ElIcons.User,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    background: Color? = null,
    color: Color? = null,
    content: (@Composable () -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val shapeModifier = if (shape == ElAvatarShape.Circle) CircleShape else RoundedCornerShape(4.dp)
    Box(
        modifier = modifier
            .size(size)
            .clip(shapeModifier)
            .background(background ?: theme.colors.fillLight)
            .border(1.dp, theme.colors.borderLighter, shapeModifier),
        contentAlignment = Alignment.Center,
    ) {
        when {
            painter != null -> Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = Modifier.size(size),
                contentScale = contentScale,
            )
            content != null -> content()
            text != null -> BasicText(
                text = if (text.length > 3) text.take(2) else text,
                style = theme.typography.base.copy(
                    color = color ?: theme.colors.textRegular,
                    fontSize = when {
                        size >= 56.dp -> 20.sp
                        size >= 40.dp -> 14.sp
                        else -> 12.sp
                    },
                ),
            )
            else -> ElIcon(icon, size = size * 0.55f, tint = color ?: theme.colors.textPlaceholder)
        }
    }
}

/** Overlapping avatar row; [content] renders the individual avatars. */
@Composable
fun ElAvatarGroup(
    count: Int,
    modifier: Modifier = Modifier,
    max: Int = count,
    size: Dp = 40.dp,
    extra: String? = null,
    shape: ElAvatarShape = ElAvatarShape.Circle,
    collapseAvatars: Boolean = false,
    maxCollapseAvatars: Int = 1,
    collapseAvatarsTooltip: Boolean = false,
    collapseAvatarsTooltipPlacement: ElPlacement = ElPlacement.Top,
    collapseModifier: Modifier = Modifier,
    collapseContent: (@Composable (hiddenCount: Int, hiddenIndices: List<Int>) -> Unit)? = null,
    content: @Composable (index: Int) -> Unit,
) {
    val theme = LocalElTheme.current
    val visible = if (collapseAvatars && count > maxCollapseAvatars) {
        maxCollapseAvatars.coerceAtLeast(0)
    } else {
        count.coerceAtMost(max).coerceAtLeast(0)
    }
    val hiddenIndices = if (visible < count) (visible until count).toList() else emptyList()
    val shapeModifier = if (shape == ElAvatarShape.Circle) CircleShape else RoundedCornerShape(4.dp)
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        repeat(visible) { index ->
            Box(
                Modifier
                    .offset(x = if (index == 0) 0.dp else (-8).dp)
                    .border(2.dp, theme.colors.background, shapeModifier),
            ) { content(index) }
        }
        if (hiddenIndices.isNotEmpty() || extra != null) {
            Spacer(Modifier.width(6.dp))
            val collapseAvatar: @Composable () -> Unit = {
                Box(
                    Modifier
                        .size(size)
                        .clip(shapeModifier)
                        .background(theme.colors.fillLight)
                        .then(collapseModifier),
                    contentAlignment = Alignment.Center,
                ) {
                    if (collapseContent != null) {
                        collapseContent(hiddenIndices.size, hiddenIndices)
                    } else {
                        BasicText(
                            text = extra ?: "+${hiddenIndices.size}",
                            style = theme.typography.small.copy(color = theme.colors.textRegular),
                        )
                    }
                }
            }
            if (collapseAvatarsTooltip && hiddenIndices.isNotEmpty()) {
                ElTooltip(
                    placement = collapseAvatarsTooltipPlacement,
                    effect = ElTooltipEffect.Light,
                    tooltip = {
                        Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)) {
                            hiddenIndices.forEach { index -> content(index) }
                        }
                    },
                ) {
                    collapseAvatar()
                }
            } else {
                collapseAvatar()
            }
        }
    }
}
