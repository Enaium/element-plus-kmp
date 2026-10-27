package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElTagEffect { Light, Dark, Plain }

/**
 * `<el-tag>`, including custom color, effects, round and closable variants.
 */
@Composable
fun ElTag(
    text: String,
    modifier: Modifier = Modifier,
    type: ElType = ElType.Primary,
    effect: ElTagEffect = ElTagEffect.Light,
    size: ElSize? = null,
    round: Boolean = false,
    closable: Boolean = false,
    disabled: Boolean = false,
    color: Color? = null,
    onClose: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val resolvedSize = resolveElSize(size)
    val height = when (resolvedSize) {
        ElSize.Large -> 32.dp
        ElSize.Default -> 24.dp
        ElSize.Small -> 20.dp
    }
    val horizontal = when (resolvedSize) {
        ElSize.Large -> 12.dp
        ElSize.Default -> 10.dp
        ElSize.Small -> 8.dp
    }
    val typeColor = color ?: colors.colorOf(type)
    val (bg, border, fg) = when {
        disabled -> Triple(colors.fillLight, colors.borderLight, colors.disabledText)
        color != null -> Triple(color.copy(alpha = 0.12f), color, color)
        effect == ElTagEffect.Dark -> Triple(typeColor, typeColor, colors.white)
        effect == ElTagEffect.Plain -> Triple(Color.Transparent, typeColor, typeColor)
        else -> Triple(colors.light(type, 9), colors.light(type, 8), typeColor)
    }
    val shape = RoundedCornerShape(if (round) 9999.dp else theme.radius.base)
    Row(
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(bg, shape)
            .border(1.dp, border, shape)
            .then(if (onClick != null && !disabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(start = horizontal, end = if (closable) 5.dp else horizontal),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = text,
            style = theme.typography.extraSmall.copy(color = fg, fontSize = if (resolvedSize == ElSize.Small) 11.sp else 12.sp),
        )
        if (closable) {
            Spacer(Modifier.width(6.dp))
            ElIcon(
                ElIcons.Close,
                size = 12.dp,
                tint = fg,
                contentDescription = locale.tag.close,
                modifier = Modifier.clickable(enabled = !disabled) { onClose?.invoke() },
            )
        }
    }
}
