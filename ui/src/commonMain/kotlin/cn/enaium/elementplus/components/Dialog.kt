package cn.enaium.elementplus.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

/**
 * `<el-dialog>` controlled modal with header, content and footer slots.
 *
 * The panel fades and slides in like Element Plus; the scrim itself is static
 * and intentionally lighter than a double-dipped platform dim layer.
 */
@Composable
fun ElDialog(
    visible: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    width: Dp = 480.dp,
    fullscreen: Boolean = false,
    showClose: Boolean = true,
    closeOnClickModal: Boolean = true,
    closeOnPressEscape: Boolean = true,
    modal: Boolean = true,
    top: Dp = 80.dp,
    center: Boolean = false,
    contentPadding: Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    val animation = rememberModalAnimationState(visible)
    if (!animation.mounted) return
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val shape = RoundedCornerShape(if (fullscreen) 0.dp else theme.radius.base)
    val density = LocalDensity.current
    val alignment = if (fullscreen || center) Alignment.Center else Alignment.TopCenter
    Dialog(
        onDismissRequest = { if (closeOnPressEscape) onClose() },
        properties = DialogProperties(
            dismissOnBackPress = closeOnPressEscape,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(Modifier.fillMaxSize()) {
            // Static scrim: only the dialog panel animates.
            if (modal) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(enabled = closeOnClickModal) { onClose() },
                )
            }
            Box(Modifier.fillMaxSize(), contentAlignment = alignment) {
                AnimatedVisibility(
                    visible = animation.contentVisible,
                    modifier = Modifier
                        .then(if (fullscreen) Modifier.fillMaxSize() else Modifier)
                        .then(if (center || fullscreen) Modifier else Modifier.padding(top = top)),
                    enter = fadeIn(tween(ElModalAnimationMillis)) +
                        slideInVertically(tween(ElModalAnimationMillis)) { -with(density) { 20.dp.roundToPx() } },
                    exit = fadeOut(tween(ElModalAnimationMillis)) +
                        slideOutVertically(tween(ElModalAnimationMillis)) { -with(density) { 20.dp.roundToPx() } },
                ) {
                    Column(
                        modifier = modifier
                            .then(if (fullscreen) Modifier.fillMaxSize() else Modifier.width(width).heightIn(max = 720.dp))
                            .background(colors.background, shape)
                            .border(1.dp, colors.borderLighter, shape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { },
                    ) {
                        if (header != null || title != null || showClose) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                when {
                                    header != null -> header()
                                    title != null -> BasicText(
                                        text = title,
                                        modifier = Modifier.weight(1f),
                                        style = theme.typography.large.copy(
                                            color = colors.textPrimary,
                                            fontWeight = FontWeight.Medium,
                                        ),
                                    )
                                    else -> Spacer(Modifier.weight(1f))
                                }
                                if (title != null && header == null) Spacer(Modifier.weight(1f))
                                if (showClose) {
                                    Box(
                                        Modifier
                                            .clickable(onClick = onClose)
                                            .padding(4.dp),
                                    ) {
                                        ElIcon(ElIcons.Close, size = 16.dp, tint = colors.textPlaceholder, contentDescription = locale.dialog.close)
                                    }
                                }
                            }
                        }
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false)
                                .padding(horizontal = contentPadding)
                                .verticalScroll(rememberScrollState()),
                        ) {
                            content()
                        }
                        if (footer != null) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                            ) {
                                footer()
                            }
                        } else {
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}
