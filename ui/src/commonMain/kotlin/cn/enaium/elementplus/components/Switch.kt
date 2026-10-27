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
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.internal.resolveElSize
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * `<el-switch>` with inline prompts, icons, action slots, loading state and
 * an optional async [beforeChange] guard.
 *
 * @param activeIcon / [inactiveIcon] icon shown in the active/inactive label
 *   (and inline inside the track when [inlinePrompt] is set), the Compose
 *   equivalent of `active-icon` / `inactive-icon`.
 * @param activeActionIcon / [inactiveActionIcon] icon rendered inside the
 *   thumb, the Compose equivalent of `active-action-icon` /
 *   `inactive-action-icon`.
 * @param activeContent / [inactiveContent] composable label content (`#active`
 *   / `#inactive` slots); [activeAction] / [inactiveAction] replace the thumb
 *   content (`#active-action` / `#inactive-action` slots).
 * @param beforeChange suspend guard invoked before a toggle. Returning `false`
 *   (or throwing) keeps the current value; the thumb shows a spinner while the
 *   guard is running.
 * @param borderColor border of the switch track, the Compose equivalent of
 *   `border-color`.
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
    activeIcon: ImageVector? = null,
    inactiveIcon: ImageVector? = null,
    activeActionIcon: ImageVector? = null,
    inactiveActionIcon: ImageVector? = null,
    activeContent: (@Composable () -> Unit)? = null,
    inactiveContent: (@Composable () -> Unit)? = null,
    activeAction: (@Composable () -> Unit)? = null,
    inactiveAction: (@Composable () -> Unit)? = null,
    borderColor: Color? = null,
    beforeChange: (suspend () -> Boolean)? = null,
) {
    SwitchCore(
        checked = checked,
        onToggle = { onCheckedChange(!checked) },
        modifier = modifier,
        disabled = disabled,
        loading = loading,
        size = size,
        activeText = activeText,
        inactiveText = inactiveText,
        inlinePrompt = inlinePrompt,
        activeColor = activeColor,
        inactiveColor = inactiveColor,
        width = width,
        activeIcon = activeIcon,
        inactiveIcon = inactiveIcon,
        activeActionIcon = activeActionIcon,
        inactiveActionIcon = inactiveActionIcon,
        activeContent = activeContent,
        inactiveContent = inactiveContent,
        activeAction = activeAction,
        inactiveAction = inactiveAction,
        borderColor = borderColor,
        beforeChange = beforeChange,
    )
}

/**
 * `<el-switch>` bound to an arbitrary value, the Compose equivalent of
 * `active-value` / `inactive-value` (`'100'` / `'0'`, numbers, enums, …).
 */
@Composable
fun <T> ElSwitch(
    value: T,
    onValueChange: (T) -> Unit,
    activeValue: T,
    inactiveValue: T,
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
    activeIcon: ImageVector? = null,
    inactiveIcon: ImageVector? = null,
    activeActionIcon: ImageVector? = null,
    inactiveActionIcon: ImageVector? = null,
    activeContent: (@Composable () -> Unit)? = null,
    inactiveContent: (@Composable () -> Unit)? = null,
    activeAction: (@Composable () -> Unit)? = null,
    inactiveAction: (@Composable () -> Unit)? = null,
    borderColor: Color? = null,
    beforeChange: (suspend () -> Boolean)? = null,
) {
    val checked = value == activeValue
    SwitchCore(
        checked = checked,
        onToggle = { onValueChange(if (checked) inactiveValue else activeValue) },
        modifier = modifier,
        disabled = disabled,
        loading = loading,
        size = size,
        activeText = activeText,
        inactiveText = inactiveText,
        inlinePrompt = inlinePrompt,
        activeColor = activeColor,
        inactiveColor = inactiveColor,
        width = width,
        activeIcon = activeIcon,
        inactiveIcon = inactiveIcon,
        activeActionIcon = activeActionIcon,
        inactiveActionIcon = inactiveActionIcon,
        activeContent = activeContent,
        inactiveContent = inactiveContent,
        activeAction = activeAction,
        inactiveAction = inactiveAction,
        borderColor = borderColor,
        beforeChange = beforeChange,
    )
}

@Composable
private fun SwitchCore(
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier,
    disabled: Boolean,
    loading: Boolean,
    size: ElSize?,
    activeText: String?,
    inactiveText: String?,
    inlinePrompt: Boolean,
    activeColor: Color?,
    inactiveColor: Color?,
    width: Dp?,
    activeIcon: ImageVector?,
    inactiveIcon: ImageVector?,
    activeActionIcon: ImageVector?,
    inactiveActionIcon: ImageVector?,
    activeContent: (@Composable () -> Unit)?,
    inactiveContent: (@Composable () -> Unit)?,
    activeAction: (@Composable () -> Unit)?,
    inactiveAction: (@Composable () -> Unit)?,
    borderColor: Color?,
    beforeChange: (suspend () -> Boolean)?,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val resolvedSize = resolveElSize(size)
    val scope = rememberCoroutineScope()
    var pending by remember { mutableStateOf(false) }
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
    val hasInlineIcon = inlinePrompt && (activeIcon != null || inactiveIcon != null ||
        activeContent != null || inactiveContent != null)
    val inlineIconWidth = if (hasInlineIcon) thumbSize + 8.dp else 0.dp
    val switchWidth = width ?: if (inlinePrompt) {
        with(density) {
            (longestInlineText.toDp() + thumbSize + 12.dp).coerceAtLeast(defaultWidth)
        }.coerceAtLeast(inlineIconWidth)
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
    val labelIconSize = when (resolvedSize) {
        ElSize.Large -> 16.dp
        ElSize.Default -> 14.dp
        ElSize.Small -> 12.dp
    }
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val busy = loading || pending
    val guard = beforeChange

    fun toggle() {
        if (guard == null) {
            onToggle()
            return
        }
        if (pending) return
        scope.launch {
            pending = true
            val allowed = try {
                guard()
            } catch (_: Throwable) {
                false
            }
            pending = false
            if (allowed) onToggle()
        }
    }

    @Composable
    fun ActionLabel(
        icon: ImageVector?,
        text: String?,
        content: (@Composable () -> Unit)?,
        tint: Color,
    ) {
        when {
            content != null -> content()
            icon != null -> ElIcon(icon, size = labelIconSize, tint = tint)
            text != null -> BasicText(
                text = text,
                style = theme.typography.small.copy(color = if (disabled) colors.disabledText else tint),
            )
        }
    }

    val hasInactiveLabel = inactiveIcon != null || inactiveText != null || inactiveContent != null
    val hasActiveLabel = activeIcon != null || activeText != null || activeContent != null
    Row(
        modifier = modifier
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = !disabled && !busy,
            ) { toggle() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!inlinePrompt && hasInactiveLabel) {
            ActionLabel(
                icon = inactiveIcon,
                text = inactiveText,
                content = inactiveContent,
                tint = if (!checked) active else colors.textRegular,
            )
            Spacer(Modifier.width(6.dp))
        }
        Box(
            Modifier
                .width(switchWidth)
                .height(switchHeight)
                .background(background, CircleShape)
                .border(
                    1.dp,
                    borderColor ?: if (hovered && !disabled && !checked) active else Color.Transparent,
                    CircleShape,
                ),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (inlinePrompt) {
                Box(
                    Modifier.padding(
                        start = if (checked) 6.dp else thumbSize + 4.dp,
                        end = if (checked) thumbSize + 4.dp else 6.dp,
                    ),
                ) {
                    val inlineContent = if (checked) activeContent else inactiveContent
                    val inlineIcon = if (checked) activeIcon else inactiveIcon
                    val inlineText = if (checked) activeText else inactiveText
                    when {
                        inlineContent != null -> inlineContent()
                        inlineIcon != null -> ElIcon(inlineIcon, size = labelIconSize, tint = textColor)
                        inlineText != null -> BasicText(
                            text = inlineText,
                            style = inlineTextStyle.copy(color = textColor),
                            maxLines = 1,
                        )
                    }
                }
            }
            Box(
                Modifier
                    .padding(start = thumbOffset)
                    .size(thumbSize)
                    .background(if (disabled) colors.white.copy(alpha = 0.8f) else colors.white, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (busy) {
                    ElIcon(ElIcons.Loading, size = thumbSize - 4.dp, tint = active, spin = true)
                } else {
                    val actionContent = if (checked) activeAction else inactiveAction
                    val actionIcon = if (checked) activeActionIcon else inactiveActionIcon
                    when {
                        actionContent != null -> actionContent()
                        actionIcon != null -> ElIcon(
                            actionIcon,
                            size = thumbSize - 6.dp,
                            tint = if (checked) active else inactive,
                        )
                    }
                }
            }
        }
        if (!inlinePrompt && hasActiveLabel) {
            Spacer(Modifier.width(6.dp))
            ActionLabel(
                icon = activeIcon,
                text = activeText,
                content = activeContent,
                tint = if (checked) active else colors.textRegular,
            )
        }
    }
}
