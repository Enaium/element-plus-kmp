package cn.enaium.elementplus.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import kotlinx.coroutines.launch

enum class ElDrawerDirection { Left, Right, Top, Bottom }

/**
 * Resize bounds for a resizable [ElDrawer], mirroring Element Plus'
 * `resizable` object (`{ minSize, maxSize }`).
 */
data class ElDrawerResizable(
    val minSize: Dp = 0.dp,
    val maxSize: Dp = Dp.Infinity,
)

/**
 * `<el-drawer>` modal side panel with header, body and optional footer.
 *
 * The panel slides in from its edge; the scrim remains static.
 *
 * @param withHeader hides the whole header (title and close icon), matching
 * `with-header={false}`.
 * @param closeOnPressEscape closes the drawer on back/Escape independently of
 * [closeOnClickModal].
 * @param sizePercent optional fractional size (`0.5f` matches `size="50%"`).
 * Ignored while [resizable] is set so the dragged pixel size stays authoritative.
 * @param resizable enables the drag handle on the inner edge; [onResize] reports
 * the new size while dragging.
 * @param beforeClose async close gate; the drawer stays open until `done()` is
 * invoked. Every close path (scrim, Esc, close icon) runs through it.
 * @param destroyOnClose disposes the body as soon as closing starts instead of
 * keeping it composed for the exit animation.
 * @param modalFade fades the scrim in and out with the panel (`modal-fade`).
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
    withHeader: Boolean = true,
    closeOnPressEscape: Boolean = true,
    beforeClose: (suspend (done: () -> Unit) -> Unit)? = null,
    destroyOnClose: Boolean = false,
    resizable: ElDrawerResizable? = null,
    sizePercent: Float? = null,
    modalFade: Boolean = true,
    scrimColor: Color = Color.Black.copy(alpha = 0.3f),
    onResize: ((Dp) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val animation = rememberModalAnimationState(visible)
    val scope = rememberCoroutineScope()
    var gateRunning by remember { mutableStateOf(false) }
    val requestClose: () -> Unit = {
        val gate = beforeClose
        if (gate == null) {
            onClose()
        } else if (!gateRunning) {
            gateRunning = true
            scope.launch {
                try {
                    gate { onClose() }
                } finally {
                    gateRunning = false
                }
            }
        }
    }
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
        onDismissRequest = { if (closeOnPressEscape) requestClose() },
        properties = DialogProperties(
            dismissOnBackPress = closeOnPressEscape,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(Modifier.fillMaxSize()) {
            if (modal) {
                val scrim: @Composable () -> Unit = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(scrimColor)
                            .clickable(enabled = closeOnClickModal) { requestClose() },
                    )
                }
                if (modalFade) {
                    AnimatedVisibility(
                        visible = animation.contentVisible,
                        enter = fadeIn(tween(ElModalAnimationMillis)),
                        exit = fadeOut(tween(ElModalAnimationMillis)),
                    ) { scrim() }
                } else if (animation.contentVisible) {
                    scrim()
                }
            }
            BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = alignment) {
                val initialSize = if (sizePercent != null && resizable == null) {
                    (if (side) maxWidth else maxHeight) * sizePercent
                } else {
                    size
                }
                var currentSize by remember { mutableStateOf(initialSize) }
                LaunchedEffect(initialSize) { currentSize = initialSize }
                val containerWidth = maxWidth
                val containerHeight = maxHeight
                val density = LocalDensity.current
                AnimatedVisibility(visible = animation.contentVisible, enter = enter, exit = exit) {
                    Box {
                        Column(
                            modifier = modifier
                                .then(
                                    if (side) Modifier.width(currentSize).fillMaxHeight()
                                    else Modifier.height(currentSize).fillMaxWidth()
                                )
                                .background(colors.background, shape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) { },
                        ) {
                            if (withHeader && (header != null || title != null || showClose)) {
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
                                            modifier = Modifier.clickable(onClick = requestClose).padding(2.dp),
                                        )
                                    }
                                }
                            }
                            val bodyModifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 20.dp)
                            if (scrollable) {
                                Box(bodyModifier.verticalScroll(rememberScrollState())) {
                                    if (!destroyOnClose || visible) content()
                                }
                            } else {
                                Box(bodyModifier) {
                                    if (!destroyOnClose || visible) content()
                                }
                            }
                            if (footer != null) {
                                Row(
                                    Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.End,
                                ) { footer() }
                            }
                        }
                        val resizeBounds = resizable
                        if (resizeBounds != null) {
                            val minBound = resizeBounds.minSize
                            val maxBound = resizeBounds.maxSize
                            val handleAlignment = when (direction) {
                                ElDrawerDirection.Left -> Alignment.CenterEnd
                                ElDrawerDirection.Right -> Alignment.CenterStart
                                ElDrawerDirection.Top -> Alignment.BottomCenter
                                ElDrawerDirection.Bottom -> Alignment.TopCenter
                            }
                            val handleBase = if (side) {
                                Modifier.fillMaxHeight().width(8.dp)
                            } else {
                                Modifier.fillMaxWidth().height(8.dp)
                            }
                            val maxByContainer = if (side) containerWidth else containerHeight
                            val upperBound = minOf(maxBound, maxByContainer)
                            Box(
                                handleBase
                                    .align(handleAlignment)
                                    .background(colors.borderLighter.copy(alpha = 0.6f))
                                    .pointerInput(direction, minBound, maxBound, upperBound) {
                                        detectDragGestures { change, dragAmount ->
                                            change.consume()
                                            val delta = with(density) {
                                                when (direction) {
                                                    ElDrawerDirection.Left -> dragAmount.x.toDp()
                                                    ElDrawerDirection.Right -> -dragAmount.x.toDp()
                                                    ElDrawerDirection.Top -> dragAmount.y.toDp()
                                                    ElDrawerDirection.Bottom -> -dragAmount.y.toDp()
                                                }
                                            }
                                            currentSize = (currentSize + delta)
                                                .coerceIn(minOf(minBound, upperBound), upperBound)
                                            onResize?.invoke(currentSize)
                                        }
                                    },
                            )
                        }
                    }
                }
            }
        }
    }
}
