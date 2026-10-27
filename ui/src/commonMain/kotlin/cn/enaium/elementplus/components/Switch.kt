package cn.enaium.elementplus.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElTheme
import kotlin.math.max

/**
 * `<el-switch>` with inline prompts, loading state and animated thumb.
 */
@Composable
fun ElSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    loading: Boolean = false,
    size: ElSize? = null,
    activeText: String? = null,
    inactiveText: String? = null,
    inlinePrompt: Boolean = false,
    activeColor: Color? = null,
    inactiveColor: Color? = null,
    width: Dp? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val resolvedSize = resolveElSize(size)
    val defaultWidth = when (resolvedSize) {
        ElSize.Large -> 48.dp
        ElSize.Default -> 40.dp
        ElSize.Small -> 32.dp
    }
    val switchHeight = when (resolvedSize) {
        ElSize.Large -> 24.dp
        ElSize.Default -> 20.dp
        ElSize.Small -> 16.dp
    }
    val thumbSize = switchHeight - 4.dp
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val inlineTextStyle = theme.typography.extraSmall
    val longestInlineText = max(
        activeText?.let { textMeasurer.measure(it, style = inlineTextStyle).size.width } ?: 0,
        inactiveText?.let { textMeasurer.measure(it, style = inlineTextStyle).size.width } ?: 0,
    )
    val switchWidth = width ?: if (inlinePrompt) {
        with(density) {
            (longestInlineText.toDp() + thumbSize + 12.dp).coerceAtLeast(defaultWidth)
        }
    } else {
        defaultWidth
    }
    val active = activeColor ?: colors.primary
    val inactive = inactiveColor ?: colors.border
    val background by animateColorAsState(
        targetValue = when {
            disabled -> if (checked) active.copy(alpha = 0.5f) else colors.fill
            checked -> active
            else -> inactive
        },
        animationSpec = tween(200),
        label = "switch-bg",
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) switchWidth - thumbSize - 2.dp else 2.dp,
        animationSpec = tween(200),
        label = "switch-thumb",
    )
    val textColor = if (checked) colors.white else if (disabled) colors.disabledText else colors.textRegular
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Row(
        modifier = modifier
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = !disabled && !loading,
            ) { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!inlinePrompt && activeText != null) {
            BasicText(text = if (checked) activeText else "", style = theme.typography.small.copy(color = if (checked) active else colors.textSecondary))
            Spacer(Modifier.width(6.dp))
        }
        Box(
            Modifier
                .width(switchWidth)
                .height(switchHeight)
                .background(background, CircleShape)
                .border(
                    1.dp,
                    if (hovered && !disabled && !checked) active else Color.Transparent,
                    CircleShape,
                ),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (inlinePrompt) {
                val text = if (checked) activeText else inactiveText
                if (text != null) {
                    BasicText(
                        text = text,
                        modifier = Modifier.padding(
                            start = if (checked) 6.dp else thumbSize + 4.dp,
                            end = if (checked) thumbSize + 4.dp else 6.dp,
                        ),
                        style = inlineTextStyle.copy(color = textColor),
                        maxLines = 1,
                    )
                }
            }
            Box(
                Modifier
                    .padding(start = thumbOffset)
                    .size(thumbSize)
                    .background(if (disabled) colors.white.copy(alpha = 0.8f) else colors.white, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (loading) {
                    ElIcon(ElIcons.Loading, size = thumbSize - 4.dp, tint = active, spin = true)
                }
            }
        }
        if (!inlinePrompt && inactiveText != null) {
            Spacer(Modifier.width(6.dp))
            BasicText(text = if (!checked) inactiveText else "", style = theme.typography.small.copy(color = if (!checked) colors.textSecondary else colors.textSecondary.copy(alpha = 0.5f)))
        }
    }
}
