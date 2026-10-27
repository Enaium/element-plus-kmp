package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

/**
 * Compose equivalent of the `tag` prop: the semantic HTML element. Each tag
 * carries its default presentation (`b` bold, `i` italic, `sub`/`sup` shifted
 * and smaller, `ins` underlined, `del` struck through, `mark` highlighted).
 */
enum class ElTextTag { Span, Paragraph, Bold, Italic, Sub, Sup, Insert, Delete, Mark }

/**
 * `<el-text>`: semantic inline text with size, type, truncation and decoration.
 *
 * [maxLines] clamps the text even when [truncated] is false (the upstream
 * `line-clamp` behaviour); [tag] applies the semantic element presentation.
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
    tag: ElTextTag = ElTextTag.Span,
    maxLines: Int? = null,
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
    val isSub = tag == ElTextTag.Sub
    val isSup = tag == ElTextTag.Sup
    val isUnderline = underline || tag == ElTextTag.Insert
    val isStrikethrough = strikethrough || tag == ElTextTag.Delete
    val isMark = mark || tag == ElTextTag.Mark
    val style = sizes.copy(
        fontSize = if (isSub || isSup) sizes.fontSize * 0.75f else sizes.fontSize,
        color = contentColor,
        fontWeight = if (bold || tag == ElTextTag.Bold) FontWeight.SemiBold else sizes.fontWeight,
        fontStyle = if (italic || tag == ElTextTag.Italic) FontStyle.Italic else sizes.fontStyle,
        textDecoration = when {
            isUnderline && isStrikethrough -> TextDecoration.combine(listOf(TextDecoration.Underline, TextDecoration.LineThrough))
            isUnderline -> TextDecoration.Underline
            isStrikethrough -> TextDecoration.LineThrough
            else -> null
        },
    )
    val interaction = remember { MutableInteractionSource() }
    val rendered: AnnotatedString = when {
        isSub -> buildAnnotatedString { withStyle(SpanStyle(baselineShift = BaselineShift.Subscript)) { append(text) } }
        isSup -> buildAnnotatedString { withStyle(SpanStyle(baselineShift = BaselineShift.Superscript)) { append(text) } }
        else -> AnnotatedString(text)
    }
    val maxLineCount = (maxLines ?: if (truncated) lineClamp else Int.MAX_VALUE).coerceAtLeast(1)
    BasicText(
        text = rendered,
        modifier = modifier
            .then(if (isMark) Modifier.background(theme.colors.light(ElType.Warning, 8), theme.let { androidx.compose.foundation.shape.RoundedCornerShape(2.dp) }) else Modifier)
            .then(if (onClick != null) Modifier.clickable(interactionSource = interaction, indication = null, onClick = onClick) else Modifier),
        style = style,
        maxLines = maxLineCount,
        overflow = if (truncated || maxLines != null) TextOverflow.Ellipsis else TextOverflow.Clip,
    )
}

/**
 * `<el-text>` with a default slot: mixes inline text, icons and other
 * components in a single line (the Compose equivalent of nested markup).
 */
@Composable
fun ElText(
    modifier: Modifier = Modifier,
    type: ElType? = null,
    disabled: Boolean = false,
    mark: Boolean = false,
    color: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.(Color) -> Unit,
) {
    val theme = LocalElTheme.current
    val contentColor = when {
        disabled -> theme.colors.disabledText
        color != null -> color
        type == null || type == ElType.Default -> theme.colors.textRegular
        else -> theme.colors.colorOf(type)
    }
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .then(if (mark) Modifier.background(theme.colors.light(ElType.Warning, 8), androidx.compose.foundation.shape.RoundedCornerShape(2.dp)) else Modifier)
            .then(if (onClick != null) Modifier.clickable(interactionSource = interaction, indication = null, onClick = onClick) else Modifier),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        androidx.compose.runtime.CompositionLocalProvider(
            cn.enaium.elementplus.theme.LocalElContentColor provides contentColor,
        ) {
            content(contentColor)
        }
    }
}

/** `<el-link>`: inline link with type, underline modes and optional icon. */
enum class ElLinkUnderline { Always, Hover, Never }

/**
 * Inline link. [href] opens the URL through the platform URI handler; [target]
 * accepts the native values and `"_self"` keeps navigation in-app (the click
 * handler runs instead of the platform handler).
 */
@Composable
fun ElLink(
    text: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    type: ElType = ElType.Primary,
    disabled: Boolean = false,
    underline: ElLinkUnderline = ElLinkUnderline.Hover,
    icon: ImageVector? = null,
    href: String? = null,
    target: String? = null,
    iconContent: (@Composable (Color) -> Unit)? = null,
) {
    ElLinkSurface(
        modifier = modifier,
        type = type,
        disabled = disabled,
        underline = underline,
        onClick = onClick,
        href = href,
        target = target,
    ) { color, decoration ->
        if (icon != null) {
            cn.enaium.elementplus.icons.ElIcon(icon, size = 14.dp, tint = color)
            androidx.compose.foundation.layout.Spacer(Modifier.padding(start = 4.dp))
        } else if (iconContent != null) {
            iconContent(color)
            androidx.compose.foundation.layout.Spacer(Modifier.padding(start = 4.dp))
        }
        BasicText(
            text = text,
            style = LocalElTheme.current.typography.base.copy(color = color, textDecoration = decoration),
        )
    }
}

/** Boolean shorthand for [ElLinkUnderline]: `true` is always, `false` is never. */
@Composable
fun ElLink(
    text: String,
    underline: Boolean,
    modifier: Modifier = Modifier,
    type: ElType = ElType.Primary,
    disabled: Boolean = false,
    onClick: (() -> Unit)? = null,
    icon: ImageVector? = null,
    href: String? = null,
    target: String? = null,
) = ElLink(
    text = text,
    onClick = onClick,
    modifier = modifier,
    type = type,
    disabled = disabled,
    underline = if (underline) ElLinkUnderline.Always else ElLinkUnderline.Never,
    icon = icon,
    href = href,
    target = target,
)

/** Link with a default slot, so arbitrary content (e.g. a trailing icon) is possible. */
@Composable
fun ElLink(
    modifier: Modifier = Modifier,
    type: ElType = ElType.Primary,
    disabled: Boolean = false,
    underline: ElLinkUnderline = ElLinkUnderline.Hover,
    href: String? = null,
    target: String? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.(Color, TextDecoration) -> Unit,
) {
    ElLinkSurface(
        modifier = modifier,
        type = type,
        disabled = disabled,
        underline = underline,
        onClick = onClick,
        href = href,
        target = target,
        content = content,
    )
}

@Composable
private fun ElLinkSurface(
    modifier: Modifier,
    type: ElType,
    disabled: Boolean,
    underline: ElLinkUnderline,
    onClick: (() -> Unit)?,
    href: String?,
    target: String?,
    content: @Composable RowScope.(Color, TextDecoration) -> Unit,
) {
    val theme = LocalElTheme.current
    val interaction = remember { MutableInteractionSource() }
    val hovered = cn.enaium.elementplus.internal.rememberElInteractionState(interaction).hovered
    val linkConfig = LocalElConfig.current.link
    val resolvedType = if (type == ElType.Primary) linkConfig.type ?: type else type
    val resolvedUnderline = if (underline == ElLinkUnderline.Hover) linkConfig.underline ?: underline else underline
    val color = if (disabled) theme.colors.disabledText else theme.colors.colorOf(resolvedType)
    val decoration = when {
        resolvedUnderline == ElLinkUnderline.Always -> TextDecoration.Underline
        resolvedUnderline == ElLinkUnderline.Hover && hovered && !disabled -> TextDecoration.Underline
        else -> TextDecoration.None
    }
    val uriHandler = LocalUriHandler.current
    val clickable = !disabled && (onClick != null || href != null)
    Row(
        modifier = modifier
            .then(
                if (clickable) {
                    Modifier.clickable(interactionSource = interaction, indication = null) {
                        onClick?.invoke()
                        if (href != null && (target != "_self" || onClick == null)) {
                            uriHandler.openUri(href)
                        }
                    }
                } else {
                    Modifier
                },
            )
            .padding(vertical = 2.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        content(color, decoration)
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
