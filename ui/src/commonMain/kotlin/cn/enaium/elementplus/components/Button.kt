package cn.enaium.elementplus.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.elDashedBorder
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.internal.rememberElInteractionState
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

internal data class ElButtonGroupPosition(val first: Boolean, val last: Boolean)

internal val LocalElButtonGroup = staticCompositionLocalOf<ElButtonGroupPosition?> { null }

/**
 * Element Plus button.
 *
 * The content slot replaces the label and receives the resolved foreground
 * color so custom content stays legible.
 */
@Composable
fun ElButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    type: ElType = ElType.Default,
    size: ElSize? = null,
    plain: Boolean? = null,
    textButton: Boolean? = null,
    link: Boolean = false,
    bg: Boolean = false,
    round: Boolean? = null,
    circle: Boolean = false,
    dashed: Boolean? = null,
    disabled: Boolean = false,
    loading: Boolean = false,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    autoInsertSpace: Boolean? = null,
    content: (@Composable (color: Color) -> Unit)? = null,
) {
    val theme = LocalElTheme.current
    val config = LocalElConfig.current
    val resolvedSize = resolveElSize(size)
    val resolvedPlain = plain ?: config.button.plain
    val resolvedText = textButton ?: config.button.text
    val resolvedRound = round ?: config.button.round
    val resolvedDashed = dashed ?: config.button.dashed
    val contentColor = buttonContentColor(
        type = type,
        plain = resolvedPlain,
        textButton = resolvedText,
        link = link,
        disabled = disabled,
    )
    val interaction = remember { MutableInteractionSource() }
    val state = rememberElInteractionState(interaction)
    val typeColor = theme.colors.colorOf(type)
    val isDefault = type == ElType.Default

    val bgColor = when {
        disabled -> theme.colors.disabledBackground
        resolvedPlain -> buttonPlainBackground(type)
        resolvedText || link -> if (bg) theme.colors.fillLight else Color.Transparent
        isDefault -> theme.colors.fillBlank
        else -> typeColor
    }
    val pressedColor = when {
        disabled || resolvedText || link -> bgColor
        resolvedPlain -> typeColor
        !isDefault -> theme.colors.dark(type)
        else -> theme.colors.light(ElType.Primary, 9)
    }
    val hoverColor = when {
        disabled || resolvedText || link -> if ((resolvedText || link) && bg) theme.colors.fill else bgColor
        resolvedPlain -> typeColor
        !isDefault -> theme.colors.light(type, 3)
        else -> theme.colors.light(ElType.Primary, 9)
    }
    val animatedBg by animateColorAsState(
        targetValue = when {
            state.pressed -> pressedColor
            state.hovered -> hoverColor
            else -> bgColor
        },
        label = "el-button-bg",
    )
    val borderColor = when {
        disabled -> theme.colors.disabledBorder
        resolvedPlain -> if (state.hovered) typeColor else theme.colors.light(type, 5)
        resolvedDashed -> if (state.hovered) theme.colors.light(type, 3) else theme.colors.light(type, 5)
        resolvedText || link || circle && false -> Color.Transparent
        isDefault -> if (state.hovered) theme.colors.light(ElType.Primary, 7) else theme.colors.border
        else -> animatedBg
    }
    val contentColorAnimated by animateColorAsState(
        targetValue = when {
            disabled -> theme.colors.disabledText
            state.pressed -> when {
                resolvedPlain -> theme.colors.white
                resolvedText || link -> theme.colors.dark(type)
                !isDefault -> theme.colors.white
                else -> theme.colors.primary
            }
            state.hovered -> when {
                resolvedPlain -> theme.colors.white
                resolvedText || link -> theme.colors.primary
                else -> contentColor
            }
            else -> contentColor
        },
        label = "el-button-content",
    )
    val group = LocalElButtonGroup.current
    val shape = when {
        circle -> CircleShape
        resolvedRound -> RoundedCornerShape(theme.radius.round)
        group != null -> RoundedCornerShape(
            topStart = if (group.first) theme.radius.base else 0.dp,
            bottomStart = if (group.first) theme.radius.base else 0.dp,
            topEnd = if (group.last) theme.radius.base else 0.dp,
            bottomEnd = if (group.last) theme.radius.base else 0.dp,
        )
        else -> RoundedCornerShape(theme.radius.base)
    }
    val height = if (resolvedText || link) Dp.Unspecified else resolvedSize.height
    val horizontalPadding = when {
        resolvedText || link -> 2.dp
        circle -> 0.dp
        else -> resolvedSize.horizontalPadding
    }
    val borderRadius = when {
        circle -> height.coerceAtLeast(resolvedSize.height) / 2
        resolvedRound -> theme.radius.round
        group != null -> 0.dp
        else -> theme.radius.base
    }

    val defaultBg = if (resolvedText || link) Color.Transparent else animatedBg
    val defaultBorder = if (resolvedText || link) Color.Transparent else borderColor
    val autoSpace = autoInsertSpace ?: config.button.autoInsertSpace
    val label = text?.let { value ->
        if (autoSpace && value.length == 2 && value.all { it.code in 0x4E00..0x9FFF }) {
            value.map { it.toString() }.joinToString("\u2009")
        } else value
    }

    Row(
        modifier = modifier
            .then(if (height != Dp.Unspecified) Modifier.height(height) else Modifier)
            .clip(shape)
            .background(defaultBg, shape)
            .then(
                if (resolvedDashed) Modifier.elDashedBorder(defaultBorder, borderRadius)
                else Modifier.border(1.dp, defaultBorder, shape)
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = !disabled && !loading,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = horizontalPadding)
            .then(
                if (circle) Modifier.width(resolvedSize.height)
                else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (loading) {
            ElIcon(
                ElIcons.Loading,
                size = 14.dp,
                tint = contentColorAnimated,
                spin = true,
                contentDescription = "loading",
            )
            if (content != null || label != null) {
                androidx.compose.foundation.layout.Spacer(Modifier.width(iconGap(resolvedSize)))
            }
        } else if (icon != null) {
            ElIcon(icon, size = 14.dp, tint = contentColorAnimated)
            if (content != null || label != null) {
                androidx.compose.foundation.layout.Spacer(Modifier.width(iconGap(resolvedSize)))
            }
        }
        if (content != null) {
            CompositionLocalProvider(cn.enaium.elementplus.theme.LocalElContentColor provides contentColorAnimated) {
                content(contentColorAnimated)
            }
        } else if (label != null) {
            BasicText(
                text = label,
                style = theme.typography.base.copy(
                    fontSize = resolvedSize.fontSize,
                    fontWeight = FontWeight.Medium,
                    color = contentColorAnimated,
                    letterSpacing = if (autoSpace && label.contains('\u2009')) 0.3.em else androidx.compose.ui.unit.TextUnit.Unspecified,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun buttonPlainBackground(type: ElType): Color {
    val theme = LocalElTheme.current
    return if (type == ElType.Default) theme.colors.fillBlank else theme.colors.light(type, 9)
}

private fun iconGap(size: ElSize): Dp = when (size) {
    ElSize.Large -> 8.dp
    ElSize.Default -> 6.dp
    ElSize.Small -> 4.dp
}

@Composable
private fun buttonContentColor(
    type: ElType,
    plain: Boolean,
    textButton: Boolean,
    link: Boolean,
    disabled: Boolean,
): Color {
    val colors = LocalElTheme.current.colors
    return when {
        disabled -> colors.disabledText
        plain || textButton || link -> colors.colorOf(type)
        type == ElType.Default -> colors.textRegular
        else -> colors.white
    }
}

/**
 * Groups buttons horizontally or vertically. Attached borders and only the
 * outer corners are rounded, matching Element Plus' button group.
 */
@Composable
fun ElButtonGroup(
    modifier: Modifier = Modifier,
    vertical: Boolean = false,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(LocalElTheme.current.radius.base)
    CompositionLocalProvider(LocalElButtonGroup provides ElButtonGroupPosition(first = false, last = false)) {
        if (vertical) {
            androidx.compose.foundation.layout.Column(
                modifier.clip(shape),
                verticalArrangement = Arrangement.spacedBy((-1).dp),
            ) { content() }
        } else {
            Row(
                modifier.clip(shape),
                horizontalArrangement = Arrangement.spacedBy((-1).dp),
            ) { content() }
        }
    }
}

/** Marks a button inside [ElButtonGroup]; [first]/[last] round the outer edges. */
@Composable
fun ElButtonGroupItem(
    first: Boolean,
    last: Boolean,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalElButtonGroup provides ElButtonGroupPosition(first, last)) { content() }
}
