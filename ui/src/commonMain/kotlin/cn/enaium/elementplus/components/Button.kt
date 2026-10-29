package cn.enaium.elementplus.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
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

internal data class ElButtonGroupPosition(
    val first: Boolean,
    val last: Boolean,
    val size: ElSize? = null,
    val type: ElType? = null,
)

internal val LocalElButtonGroup = staticCompositionLocalOf<ElButtonGroupPosition?> { null }

/**
 * Compose equivalent of the `tag` prop: selects the clickable semantics of the
 * rendered surface. `Button` and `Link` expose the button role (Compose has no
 * dedicated link role), while `Div` renders without a role. Pass the `href`
 * parameter of [ElButton] together with `Link` to open a URL.
 */
enum class ElButtonTag { Button, Link, Div }

/** Resolved colors for a custom-colored button (one per interaction state). */
private data class ElButtonCustomColors(val background: Color, val border: Color, val content: Color)

/**
 * Element Plus button.
 *
 * The content slot replaces the label and receives the resolved foreground
 * color so custom content stays legible. [color] derives the hover/active
 * shades automatically ([dark] inverts that shading); [loadingContent] and
 * [iconContent] replace the loading spinner and leading icon with composable
 * content; [tag] selects the clickable semantics.
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
    icon: ImageVector? = null,
    autoInsertSpace: Boolean? = null,
    content: (@Composable (color: Color) -> Unit)? = null,
    color: Color? = null,
    dark: Boolean = false,
    loadingIcon: ImageVector? = null,
    loadingContent: (@Composable (color: Color) -> Unit)? = null,
    iconContent: (@Composable (color: Color) -> Unit)? = null,
    tag: ElButtonTag = ElButtonTag.Button,
    autofocus: Boolean = false,
    href: String? = null,
) {
    val theme = LocalElTheme.current
    val config = LocalElConfig.current
    val group = LocalElButtonGroup.current
    val resolvedSize = resolveElSize(size ?: group?.size)
    val resolvedType = if (type == ElType.Default) group?.type ?: config.button.type ?: type else type
    val resolvedPlain = plain ?: config.button.plain
    val resolvedText = textButton ?: config.button.text
    val resolvedRound = round ?: config.button.round
    val resolvedDashed = dashed ?: config.button.dashed
    val contentColor = buttonContentColor(
        type = resolvedType,
        plain = resolvedPlain,
        textButton = resolvedText,
        link = link,
        dashed = resolvedDashed,
        disabled = disabled,
    )
    val interaction = remember { MutableInteractionSource() }
    val state = rememberElInteractionState(interaction)
    val typeColor = theme.colors.colorOf(resolvedType)
    val isDefault = resolvedType == ElType.Default
    val customBase = color?.let {
        customButtonColors(
            color = it,
            dark = dark,
            plain = resolvedPlain,
            textButton = resolvedText,
            link = link,
            bg = bg,
            disabled = disabled,
            white = theme.colors.white,
            fill = theme.colors.fillLight,
        )
    }
    val customHover = color?.let {
        customButtonColors(
            color = it,
            dark = dark,
            plain = resolvedPlain,
            textButton = resolvedText,
            link = link,
            bg = bg,
            disabled = disabled,
            white = theme.colors.white,
            fill = theme.colors.fillLight,
            hovered = true,
        )
    }
    val customPressed = color?.let {
        customButtonColors(
            color = it,
            dark = dark,
            plain = resolvedPlain,
            textButton = resolvedText,
            link = link,
            bg = bg,
            disabled = disabled,
            white = theme.colors.white,
            fill = theme.colors.fillLight,
            pressed = true,
        )
    }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(autofocus) {
        if (autofocus && !disabled) {
            runCatching { focusRequester.requestFocus() }
        }
    }

    val bgColor = customBase?.background ?: when {
        disabled -> theme.colors.disabledBackground
        resolvedDashed -> if (isDefault) theme.colors.fillBlank else theme.colors.light(resolvedType, 9)
        resolvedPlain -> buttonPlainBackground(resolvedType)
        resolvedText || link -> if (bg) theme.colors.fillLight else Color.Transparent
        isDefault -> theme.colors.fillBlank
        else -> typeColor
    }
    val pressedColor = customPressed?.background ?: when {
        disabled || resolvedText || link -> bgColor
        resolvedDashed -> if (isDefault) theme.colors.fillBlank else theme.colors.light(resolvedType, 9)
        resolvedPlain -> if (isDefault) theme.colors.light(ElType.Primary, 9) else theme.colors.dark(resolvedType)
        !isDefault -> theme.colors.dark(resolvedType)
        else -> theme.colors.light(ElType.Primary, 9)
    }
    val hoverColor = customHover?.background ?: when {
        disabled || resolvedText || link -> if ((resolvedText || link) && bg) theme.colors.fill else bgColor
        resolvedDashed -> if (isDefault) theme.colors.fillBlank else theme.colors.light(resolvedType, 9)
        resolvedPlain -> typeColor
        !isDefault -> theme.colors.light(resolvedType, 3)
        else -> theme.colors.light(ElType.Primary, 9)
    }
    val animatedBg by animateColorAsState(
        targetValue = when {
            state.pressed -> pressedColor
            state.hovered -> hoverColor
            else -> bgColor
        },
        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
        label = "el-button-bg",
    )
    val borderColor = if (customBase != null) {
        when {
            state.pressed -> (customPressed ?: customBase).border
            state.hovered -> (customHover ?: customBase).border
            else -> customBase.border
        }
    } else when {
        disabled -> theme.colors.disabledBorder
        resolvedPlain -> when {
            state.pressed -> if (isDefault) theme.colors.light(ElType.Primary, 5) else theme.colors.dark(resolvedType)
            state.hovered -> typeColor
            else -> theme.colors.light(resolvedType, 5)
        }
        resolvedDashed -> when {
            state.pressed -> if (isDefault) theme.colors.dark(ElType.Primary) else theme.colors.dark(resolvedType)
            state.hovered -> if (isDefault) theme.colors.primary else theme.colors.light(resolvedType, 3)
            else -> if (isDefault) theme.colors.border else theme.colors.light(resolvedType, 5)
        }
        resolvedText || link -> Color.Transparent
        isDefault -> if (state.hovered) theme.colors.light(ElType.Primary, 7) else theme.colors.border
        else -> animatedBg
    }
    val contentColorAnimated by animateColorAsState(
        targetValue = if (customBase != null) {
            when {
                state.pressed -> (customPressed ?: customBase).content
                state.hovered -> (customHover ?: customBase).content
                else -> customBase.content
            }
        } else when {
            disabled -> theme.colors.disabledText
            state.pressed -> when {
                resolvedPlain -> theme.colors.white
                resolvedDashed -> if (isDefault) theme.colors.dark(ElType.Primary) else theme.colors.dark(resolvedType)
                resolvedText || link -> theme.colors.dark(resolvedType)
                !isDefault -> theme.colors.white
                else -> theme.colors.primary
            }
            state.hovered -> when {
                resolvedPlain -> theme.colors.white
                resolvedDashed -> if (isDefault) theme.colors.primary else typeColor
                resolvedText || link -> theme.colors.primary
                else -> contentColor
            }
            else -> contentColor
        },
        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
        label = "el-button-content",
    )
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

    val defaultBg = if (customBase == null && (resolvedText || link)) Color.Transparent else animatedBg
    val defaultBorder = if (customBase == null && (resolvedText || link)) Color.Transparent else borderColor
    val autoSpace = autoInsertSpace ?: config.button.autoInsertSpace
    val label = text?.let { value ->
        if (autoSpace && value.length == 2 && value.all { it.code in 0x4E00..0x9FFF }) {
            value.map { it.toString() }.joinToString("\u2009")
        } else value
    }
    val role = when (tag) {
        ElButtonTag.Button -> Role.Button
        ElButtonTag.Link -> Role.Button
        ElButtonTag.Div -> null
    }
    val hasIcon = icon != null || iconContent != null
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    Row(
        modifier = modifier
            .then(if (height != Dp.Unspecified) Modifier.height(height) else Modifier)
            .clip(shape)
            .background(defaultBg, shape)
            .then(
                if (resolvedDashed) Modifier.elDashedBorder(defaultBorder, borderRadius)
                else Modifier.border(1.dp, defaultBorder, shape)
            )
            .then(
                if (tag == ElButtonTag.Div) Modifier
                else Modifier.focusRequester(focusRequester)
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = !disabled && !loading,
                role = role,
                onClick = {
                    onClick()
                    if (href != null) uriHandler.openUri(href)
                },
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
            if (loadingContent != null) {
                CompositionLocalProvider(cn.enaium.elementplus.theme.LocalElContentColor provides contentColorAnimated) {
                    loadingContent(contentColorAnimated)
                }
            } else {
                ElIcon(
                    loadingIcon ?: ElIcons.Loading,
                    size = 14.dp,
                    tint = contentColorAnimated,
                    spin = loadingIcon == null,
                    contentDescription = "loading",
                )
            }
            if (content != null || label != null) {
                androidx.compose.foundation.layout.Spacer(Modifier.width(iconGap(resolvedSize)))
            }
        } else if (hasIcon) {
            if (icon != null) {
                ElIcon(icon, size = 14.dp, tint = contentColorAnimated)
            } else {
                CompositionLocalProvider(cn.enaium.elementplus.theme.LocalElContentColor provides contentColorAnimated) {
                    iconContent?.invoke(contentColorAnimated)
                }
            }
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
    dashed: Boolean,
    disabled: Boolean,
): Color {
    val colors = LocalElTheme.current.colors
    return when {
        disabled -> colors.disabledText
        plain || textButton || link -> colors.colorOf(type)
        dashed -> if (type == ElType.Default) colors.textRegular else colors.colorOf(type)
        type == ElType.Default -> colors.textRegular
        else -> colors.white
    }
}

/**
 * Groups buttons horizontally or vertically. Attached borders and only the
 * outer corners are rounded, matching Element Plus' button group. [size] and
 * [type] act as defaults for buttons that do not set them.
 */
@Composable
fun ElButtonGroup(
    modifier: Modifier = Modifier,
    vertical: Boolean = false,
    size: ElSize? = null,
    type: ElType? = null,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(LocalElTheme.current.radius.base)
    CompositionLocalProvider(
        LocalElButtonGroup provides ElButtonGroupPosition(first = false, last = false, size = size, type = type),
    ) {
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
    val group = LocalElButtonGroup.current
    CompositionLocalProvider(
        LocalElButtonGroup provides ElButtonGroupPosition(first, last, group?.size, group?.type),
    ) { content() }
}

/**
 * Compose port of `useButtonCustomStyle`: derives the base, hover and pressed
 * colors from a custom button color, with `dark` inverting the shading.
 */
private fun customButtonColors(
    color: Color,
    dark: Boolean,
    plain: Boolean,
    textButton: Boolean,
    link: Boolean,
    bg: Boolean,
    disabled: Boolean,
    white: Color,
    fill: Color,
    hovered: Boolean = false,
    pressed: Boolean = false,
): ElButtonCustomColors {
    fun darken(amount: Float): Color = lerp(color, Color(0xFF141414), amount / 100f)
    fun tint(amount: Float): Color = lerp(color, white, amount / 100f)
    val activeBg = if (dark) tint(20f) else darken(20f)
    val hoverShade = if (dark) darken(30f) else tint(30f)
    val disabledShade = if (dark) darken(50f) else tint(50f)
    val transparent = Color.Transparent

    return when {
        plain -> {
            val baseBg = if (dark) darken(90f) else tint(90f)
            val baseBorder = if (dark) darken(50f) else tint(50f)
            when {
                disabled -> ElButtonCustomColors(
                    background = baseBg,
                    border = if (dark) darken(80f) else tint(80f),
                    content = disabledShade,
                )
                pressed -> ElButtonCustomColors(activeBg, activeBg, white)
                hovered -> ElButtonCustomColors(color, color, white)
                else -> ElButtonCustomColors(baseBg, baseBorder, color)
            }
        }
        textButton || link -> {
            val baseBg = if (bg) fill else transparent
            when {
                disabled -> ElButtonCustomColors(transparent, transparent, disabledShade)
                pressed -> ElButtonCustomColors(baseBg, transparent, activeBg)
                hovered -> ElButtonCustomColors(
                    if (bg) fill else transparent,
                    transparent,
                    if (dark) darken(30f) else tint(30f),
                )
                else -> ElButtonCustomColors(baseBg, transparent, color)
            }
        }
        else -> {
            val textColor = if (color.isCssDark()) white else Color.Black
            when {
                disabled -> ElButtonCustomColors(
                    background = disabledShade,
                    border = disabledShade,
                    content = if (dark) white.copy(alpha = 0.5f) else white,
                )
                pressed -> ElButtonCustomColors(activeBg, activeBg, textColor)
                hovered -> ElButtonCustomColors(hoverShade, hoverShade, textColor)
                else -> ElButtonCustomColors(color, color, textColor)
            }
        }
    }
}

/** Mirrors tinycolor's `isDark()` brightness test used by Element Plus. */
private fun Color.isCssDark(): Boolean {
    val brightness = (red * 299f + green * 587f + blue * 114f) / 1000f
    return brightness < 0.5f
}
