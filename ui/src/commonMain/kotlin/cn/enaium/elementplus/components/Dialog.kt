package cn.enaium.elementplus.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElConfig
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Panel enter/exit animation presets for [ElDialog]. */
enum class ElDialogAnimation { FadeSlide, Fade, Scale, Slide, Bounce }

/**
 * `<el-dialog>` controlled modal with header, content and footer slots.
 *
 * The panel animates in and out like Element Plus; the scrim itself is static
 * and intentionally lighter than a double-dipped platform dim layer.
 *
 * @param center lays the panel out in the vertical center of the screen.
 * @param centerContent centers the header title and the footer actions
 * (Element Plus' `center` prop). The body content is left aligned.
 * @param widthPercent optional fractional width (`0.5f` matches `width="50%"`).
 * @param beforeClose async close gate. The dialog stays open until the lambda
 * invokes `done()`; unprotected calls (scrim, Esc, close icon, header) all run
 * through it. Mirrors `before-close`.
 * @param destroyOnClose disposes the body content as soon as closing starts
 * instead of keeping it composed for the exit animation. Content is always
 * created lazily on the first open.
 * @param draggable lets the user move the panel by dragging its header.
 * @param overflow allows dragging the panel outside the viewport; when false
 * the offset is clamped so the panel stays visible.
 * @param openDelay / closeDelay delays in milliseconds (`open-delay` / `close-delay`).
 * @param onOpen fired when the dialog starts opening (`open`).
 * @param onOpened fired once the enter animation settles (`opened`).
 * @param onClosed fired once the exit animation settles (`closed`).
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
    widthPercent: Float? = null,
    centerContent: Boolean = false,
    beforeClose: (suspend (done: () -> Unit) -> Unit)? = null,
    destroyOnClose: Boolean = false,
    draggable: Boolean = false,
    overflow: Boolean = false,
    animation: ElDialogAnimation = ElDialogAnimation.FadeSlide,
    animationMillis: Int = ElModalAnimationMillis,
    openDelay: Int = 0,
    closeDelay: Int = 0,
    scrimColor: Color = Color.Black.copy(alpha = 0.3f),
    onOpen: (() -> Unit)? = null,
    onOpened: (() -> Unit)? = null,
    onClosed: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val animationState = rememberModalAnimationState(
        visible = visible,
        durationMillis = animationMillis,
        openDelayMillis = openDelay,
        closeDelayMillis = closeDelay,
        onOpened = onOpened,
        onClosed = onClosed,
    )
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
    val currentOnOpen by rememberUpdatedState(onOpen)
    LaunchedEffect(visible) { if (visible) currentOnOpen?.invoke() }
    if (!animationState.mounted) return

    var dragX by remember { mutableStateOf(0f) }
    var dragY by remember { mutableStateOf(0f) }
    var panelWidth by remember { mutableStateOf(0) }
    var panelHeight by remember { mutableStateOf(0) }
    var containerWidth by remember { mutableStateOf(0) }
    var containerHeight by remember { mutableStateOf(0) }
    LaunchedEffect(visible) {
        if (!visible) {
            dragX = 0f
            dragY = 0f
        }
    }

    val theme = LocalElTheme.current
    val colors = theme.colors
    val config = LocalElConfig.current
    val locale = config.locale
    val effectiveCenter = center || config.dialog.alignCenter == true
    val shape = RoundedCornerShape(if (fullscreen) 0.dp else theme.radius.base)
    val density = LocalDensity.current
    val alignment = if (fullscreen || effectiveCenter) Alignment.Center else Alignment.TopCenter
    val enter: EnterTransition = when (animation) {
        ElDialogAnimation.FadeSlide -> fadeIn(tween(animationMillis)) +
            slideInVertically(tween(animationMillis)) { -with(density) { 20.dp.roundToPx() } }
        ElDialogAnimation.Fade -> fadeIn(tween(animationMillis))
        ElDialogAnimation.Scale -> fadeIn(tween(animationMillis)) +
            scaleIn(tween(animationMillis), initialScale = 0.5f)
        ElDialogAnimation.Slide -> fadeIn(tween(animationMillis)) +
            slideInVertically(tween(animationMillis)) { -it }
        ElDialogAnimation.Bounce -> fadeIn(tween(animationMillis)) + scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessMedium,
            ),
            initialScale = 0.3f,
        )
    }
    val exit: ExitTransition = when (animation) {
        ElDialogAnimation.FadeSlide -> fadeOut(tween(animationMillis)) +
            slideOutVertically(tween(animationMillis)) { -with(density) { 20.dp.roundToPx() } }
        ElDialogAnimation.Fade -> fadeOut(tween(animationMillis))
        ElDialogAnimation.Scale -> fadeOut(tween(animationMillis)) +
            scaleOut(tween(animationMillis), targetScale = 0.5f)
        ElDialogAnimation.Slide -> fadeOut(tween(animationMillis)) +
            slideOutVertically(tween(animationMillis)) { -it }
        ElDialogAnimation.Bounce -> fadeOut(tween(animationMillis)) +
            scaleOut(tween(animationMillis), targetScale = 0.3f)
    }
    val topPx = with(density) { top.roundToPx() }.toFloat()
    val headerDragModifier = if (draggable) {
        Modifier.pointerInput(Unit) {
            detectDragGestures { change, delta ->
                change.consume()
                var nextX = dragX + delta.x
                var nextY = dragY + delta.y
                if (!overflow) {
                    val maxX = ((containerWidth - panelWidth) / 2f).coerceAtLeast(0f)
                    val halfY = ((containerHeight - panelHeight) / 2f).coerceAtLeast(0f)
                    val minY = if (effectiveCenter || fullscreen) -halfY else -topPx
                    val maxY = if (effectiveCenter || fullscreen) halfY else (containerHeight - topPx - panelHeight).coerceAtLeast(0f)
                    nextX = nextX.coerceIn(-maxX, maxX)
                    nextY = nextY.coerceIn(minY, maxY)
                }
                dragX = nextX
                dragY = nextY
            }
        }
    } else {
        Modifier
    }

    Dialog(
        onDismissRequest = { if (closeOnPressEscape) requestClose() },
        properties = DialogProperties(
            dismissOnBackPress = closeOnPressEscape,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(Modifier.fillMaxSize().onSizeChanged { containerWidth = it.width; containerHeight = it.height }) {
            // Static scrim: only the dialog panel animates.
            if (modal) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(scrimColor)
                        .clickable(enabled = closeOnClickModal) { requestClose() },
                )
            }
            Box(Modifier.fillMaxSize(), contentAlignment = alignment) {
                AnimatedVisibility(
                    visible = animationState.contentVisible,
                    modifier = Modifier
                        .then(if (fullscreen) Modifier.fillMaxSize() else Modifier)
                        .then(if (effectiveCenter || fullscreen) Modifier else Modifier.padding(top = top)),
                    enter = enter,
                    exit = exit,
                ) {
                    Column(
                        modifier = modifier
                            .then(
                                when {
                                    fullscreen -> Modifier.fillMaxSize()
                                    widthPercent != null -> Modifier.fillMaxWidth(widthPercent).heightIn(max = 720.dp)
                                    else -> Modifier.width(width).heightIn(max = 720.dp)
                                },
                            )
                            .offset { IntOffset(dragX.roundToInt(), dragY.roundToInt()) }
                            .onSizeChanged { panelWidth = it.width; panelHeight = it.height }
                            .background(colors.background, shape)
                            .border(1.dp, colors.borderLighter, shape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { },
                    ) {
                        if (header != null || title != null || showClose || draggable) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .then(headerDragModifier)
                                    .padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                // Balance the close button so a centered title sits in the middle.
                                if (centerContent && showClose) Spacer(Modifier.width(24.dp))
                                when {
                                    header != null -> if (centerContent) {
                                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { header() }
                                    } else {
                                        header()
                                    }
                                    title != null -> BasicText(
                                        text = title,
                                        modifier = Modifier.weight(1f),
                                        style = theme.typography.large.copy(
                                            color = colors.textPrimary,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = if (centerContent) TextAlign.Center else TextAlign.Start,
                                        ),
                                    )
                                    else -> Spacer(Modifier.weight(1f))
                                }
                                if (title != null && header == null && !centerContent) Spacer(Modifier.weight(1f))
                                if (showClose) {
                                    Box(
                                        Modifier
                                            .clickable(onClick = requestClose)
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
                            if (!destroyOnClose || visible) content()
                        }
                        if (footer != null) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = if (centerContent) Arrangement.Center else Arrangement.End,
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
