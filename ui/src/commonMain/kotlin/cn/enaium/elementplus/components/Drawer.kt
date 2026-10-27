package cn.enaium.elementplus.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElDrawerDirection { Left, Right, Top, Bottom }

/**
 * `<el-drawer>` modal side panel with header, body and optional footer.
 *
 * The panel slides in from its edge; the scrim remains static.
 */
@Composable
fun ElDrawer(
    visible: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    header: (@Composable () -> Unit)? = null,
    direction: ElDrawerDirection = ElDrawerDirection.Right,
    size: Dp = 300.dp,
    showClose: Boolean = true,
    closeOnClickModal: Boolean = true,
    modal: Boolean = true,
    scrollable: Boolean = true,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val animation = rememberModalAnimationState(visible)
    if (!animation.mounted) return
    val theme = LocalElTheme.current
    val colors = theme.colors
    val locale = LocalElConfig.current.locale
    val side = direction == ElDrawerDirection.Left || direction == ElDrawerDirection.Right
    val shape = when (direction) {
        ElDrawerDirection.Left -> RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
        ElDrawerDirection.Right -> RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)
        ElDrawerDirection.Top -> RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
        ElDrawerDirection.Bottom -> RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
    }
    val alignment = when (direction) {
        ElDrawerDirection.Left -> Alignment.CenterStart
        ElDrawerDirection.Right -> Alignment.CenterEnd
        ElDrawerDirection.Top -> Alignment.TopCenter
        ElDrawerDirection.Bottom -> Alignment.BottomCenter
    }
    val enter = when (direction) {
        ElDrawerDirection.Left -> slideInHorizontally(tween(ElModalAnimationMillis)) { -it }
        ElDrawerDirection.Right -> slideInHorizontally(tween(ElModalAnimationMillis)) { it }
        ElDrawerDirection.Top -> slideInVertically(tween(ElModalAnimationMillis)) { -it }
        ElDrawerDirection.Bottom -> slideInVertically(tween(ElModalAnimationMillis)) { it }
    }
    val exit = when (direction) {
        ElDrawerDirection.Left -> slideOutHorizontally(tween(ElModalAnimationMillis)) { -it }
        ElDrawerDirection.Right -> slideOutHorizontally(tween(ElModalAnimationMillis)) { it }
        ElDrawerDirection.Top -> slideOutVertically(tween(ElModalAnimationMillis)) { -it }
        ElDrawerDirection.Bottom -> slideOutVertically(tween(ElModalAnimationMillis)) { it }
    }
    Dialog(
        onDismissRequest = { if (closeOnClickModal) onClose() },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(Modifier.fillMaxSize()) {
            if (modal) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(enabled = closeOnClickModal) { onClose() },
                )
            }
            Box(Modifier.fillMaxSize(), contentAlignment = alignment) {
                AnimatedVisibility(visible = animation.contentVisible, enter = enter, exit = exit) {
                    Column(
                        modifier = modifier
                            .then(
                                if (side) Modifier.width(size).fillMaxHeight()
                                else Modifier.height(size).fillMaxWidth()
                            )
                            .background(colors.background, shape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { },
                    ) {
                        if (header != null || title != null || showClose) {
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (header != null) {
                                    Box(Modifier.weight(1f)) { header() }
                                } else {
                                    BasicText(
                                        text = title ?: "",
                                        modifier = Modifier.weight(1f),
                                        style = theme.typography.medium.copy(
                                            color = colors.textPrimary,
                                            fontWeight = FontWeight.Medium,
                                        ),
                                    )
                                }
                                if (showClose) {
                                    ElIcon(
                                        ElIcons.Close,
                                        size = 16.dp,
                                        tint = colors.textPlaceholder,
                                        contentDescription = locale.drawer.close,
                                        modifier = Modifier.clickable(onClick = onClose).padding(2.dp),
                                    )
                                }
                            }
                        }
                        val bodyModifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 20.dp)
                        if (scrollable) {
                            Box(bodyModifier.verticalScroll(rememberScrollState())) { content() }
                        } else {
                            Box(bodyModifier) { content() }
                        }
                        if (footer != null) {
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.End,
                            ) { footer() }
                        }
                    }
                }
            }
        }
    }
}
