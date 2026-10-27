package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

/**
 * `<el-text>`: semantic inline text with size, type, truncation and decoration.
 */
@Composable
fun ElText(
    text: String,
    modifier: Modifier = Modifier,
    type: ElType? = null,
    size: ElSize = resolveElSize(),
    truncated: Boolean = false,
    lineClamp: Int = 1,
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
    strikethrough: Boolean = false,
    disabled: Boolean = false,
    mark: Boolean = false,
    color: Color? = null,
    onClick: (() -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val sizes = when (size) {
        ElSize.Large -> theme.typography.large
        ElSize.Default -> theme.typography.base
        ElSize.Small -> theme.typography.extraSmall
    }
    val contentColor = when {
        disabled -> theme.colors.disabledText
        color != null -> color
        type == null || type == ElType.Default -> theme.colors.textRegular
        else -> theme.colors.colorOf(type)
    }
    val style = sizes.copy(
        color = contentColor,
        fontWeight = if (bold) FontWeight.SemiBold else sizes.fontWeight,
        fontStyle = if (italic) FontStyle.Italic else sizes.fontStyle,
        textDecoration = when {
            underline && strikethrough -> TextDecoration.combine(listOf(TextDecoration.Underline, TextDecoration.LineThrough))
            underline -> TextDecoration.Underline
            strikethrough -> TextDecoration.LineThrough
            else -> null
        },
    )
    val interaction = remember { MutableInteractionSource() }
    BasicText(
        text = text,
        modifier = modifier
            .then(if (mark) Modifier.background(theme.colors.light(ElType.Warning, 8), theme.let { androidx.compose.foundation.shape.RoundedCornerShape(2.dp) }) else Modifier)
            .then(if (onClick != null) Modifier.clickable(interactionSource = interaction, indication = null, onClick = onClick) else Modifier),
        style = style,
        maxLines = if (truncated) lineClamp else Int.MAX_VALUE,
        overflow = if (truncated) TextOverflow.Ellipsis else TextOverflow.Clip,
    )
}

/** `<el-link>`: inline link with type, underline modes and optional icon. */
enum class ElLinkUnderline { Always, Hover, Never }

@Composable
fun ElLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: ElType = ElType.Primary,
    disabled: Boolean = false,
    underline: ElLinkUnderline = ElLinkUnderline.Hover,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
    val theme = LocalElTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered = cn.enaium.elementplus.internal.rememberElInteractionState(interaction).hovered
    val color = if (disabled) theme.colors.disabledText else theme.colors.colorOf(type)
    val decoration = when {
        underline == ElLinkUnderline.Always -> TextDecoration.Underline
        underline == ElLinkUnderline.Hover && hovered && !disabled -> TextDecoration.Underline
        else -> TextDecoration.None
    }
    androidx.compose.foundation.layout.Row(
        modifier = modifier
            .then(if (disabled) Modifier else Modifier.clickable(interactionSource = interaction, indication = null, onClick = onClick))
            .padding(vertical = 2.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        if (icon != null) {
            cn.enaium.elementplus.icons.ElIcon(icon, size = 14.dp, tint = color)
            androidx.compose.foundation.layout.Spacer(Modifier.padding(start = 4.dp))
        }
        BasicText(
            text = text,
            style = theme.typography.base.copy(color = color, textDecoration = decoration),
        )
    }
}

/** `<el-typography>`: convenience wrapper around the documented heading scale. */
@Composable
fun ElTypography(
    text: String,
    modifier: Modifier = Modifier,
    level: Int = 1,
    type: ElType? = null,
    truncated: Boolean = false,
    bold: Boolean = false,
) {
    val theme = LocalElTheme.current
    val size = when (level.coerceIn(1, 6)) {
        1 -> theme.typography.large.copy(fontSize = theme.typography.extraLarge.fontSize * 2f)
        2 -> theme.typography.extraLarge.copy(fontWeight = FontWeight.SemiBold)
        3 -> theme.typography.large.copy(fontWeight = FontWeight.SemiBold)
        4 -> theme.typography.medium.copy(fontWeight = FontWeight.SemiBold)
        5 -> theme.typography.base.copy(fontWeight = FontWeight.SemiBold)
        else -> theme.typography.small.copy(fontWeight = FontWeight.SemiBold)
    }
    val color = when {
        type == null || type == ElType.Default -> theme.colors.textPrimary
        else -> theme.colors.colorOf(type)
    }
    BasicText(
        text = text,
        modifier = modifier,
        style = size.copy(color = color, fontWeight = if (bold) FontWeight.Bold else size.fontWeight),
        maxLines = if (truncated) 1 else Int.MAX_VALUE,
        overflow = if (truncated) TextOverflow.Ellipsis else TextOverflow.Clip,
    )
}

@Composable
fun ElTitle(
    text: String,
    modifier: Modifier = Modifier,
    level: Int = 1,
) = ElTypography(text = text, modifier = modifier, level = level)

@Composable
fun ElParagraph(
    text: String,
    modifier: Modifier = Modifier,
    type: ElType? = null,
) {
    val theme = LocalElTheme.current
    BasicText(
        text = text,
        modifier = modifier,
        style = theme.typography.base.copy(
            color = if (type == null) theme.colors.textRegular else theme.colors.colorOf(type),
        ),
    )
}
