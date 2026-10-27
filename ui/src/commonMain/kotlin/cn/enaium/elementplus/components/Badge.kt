package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.layout
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

/**
 * `<el-badge>`. Omit [content] for a standalone badge.
 */
@Composable
fun ElBadge(
    modifier: Modifier = Modifier,
    value: String? = null,
    max: Int = 99,
    isDot: Boolean = false,
    hidden: Boolean = false,
    type: ElType = ElType.Danger,
    showZero: Boolean = true,
    color: Color? = null,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    badgeContent: (@Composable (value: String) -> Unit)? = null,
    badgeModifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colorValue = color ?: theme.colors.colorOf(type)
    val display = value?.let { raw ->
        val number = raw.toIntOrNull()
        if (number != null && number > max) "$max+" else raw
    }
    val valueVisible = !hidden && !isDot && (showZero || display != "0") && !display.isNullOrEmpty()
    val visible = valueVisible || (!hidden && badgeContent != null)
    Box(modifier) {
        content?.invoke()
        val singleCharacter = !isDot && (display?.length ?: 0) <= 1
        val badgeShape = if (isDot || singleCharacter) CircleShape else RoundedCornerShape(percent = 50)
        val badgeBubble: @Composable () -> Unit = {
            Box(
                Modifier
                    .then(
                        if (isDot) {
                            Modifier.size(8.dp).background(colorValue, badgeShape)
                        } else {
                            Modifier
                                .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
                                .background(colorValue, badgeShape)
                                .then(if (content != null) Modifier.border(1.dp, theme.colors.background, badgeShape) else Modifier)
                                .padding(horizontal = if (singleCharacter) 0.dp else 6.dp)
                        }
                    )
                    .then(badgeModifier),
                contentAlignment = Alignment.Center,
            ) {
                if (badgeContent != null) {
                    badgeContent(display ?: "")
                } else if (!isDot) {
                    BasicText(
                        text = display ?: "",
                        style = theme.typography.extraSmall.copy(color = theme.colors.white, fontSize = 12.sp),
                    )
                }
            }
        }
        if (content != null && (visible || isDot)) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = offsetX, y = offsetY)
                    .badgeCorner(),
            ) { badgeBubble() }
        } else if (content == null && (visible || isDot)) {
            if (isDot && badgeContent == null) {
                Box(Modifier.size(8.dp).background(colorValue, CircleShape).then(badgeModifier))
            } else {
                badgeBubble()
            }
        }
    }
}

private fun Modifier.badgeCorner(): Modifier = this.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
    layout(0, 0) {
        placeable.place(-placeable.width / 2, -placeable.height / 2)
    }
}
