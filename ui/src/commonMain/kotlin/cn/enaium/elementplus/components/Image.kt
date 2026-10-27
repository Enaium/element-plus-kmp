package cn.enaium.elementplus.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElTheme

/**
 * Actions exposed to a custom image-viewer toolbar. Mirrors the `actions`,
 * `prev`, `next`, `reset` and `setActiveItem` arguments of Element Plus'
 * `#toolbar` slot.
 */
class ElImageViewerActions(
    val activeIndex: Int,
    val total: Int,
    val prev: () -> Unit,
    val next: () -> Unit,
    val setActiveItem: (Int) -> Unit,
    val zoomIn: () -> Unit,
    val zoomOut: () -> Unit,
    val rotateClockwise: () -> Unit,
    val rotateAnticlockwise: () -> Unit,
    val reset: () -> Unit,
    val close: () -> Unit,
)

/**
 * `<el-image>` with placeholder/error slots and an optional full-screen preview.
 * Images are passed as [Painter]s so the library stays free of a specific image
 * loading backend.
 *
 * [lazy] mirrors the `lazy` attribute: the image stays on its placeholder until
 * it first enters the window viewport. Because painters decode synchronously,
 * [loading] and [failed] are caller-driven flags that select the placeholder or
 * error content instead of the image.
 */
@Composable
fun ElImage(
    painter: Painter?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    previewEnabled: Boolean = false,
    previewImages: List<Painter> = listOfNotNull(painter),
    fit: ContentScale = contentScale,
    placeholder: (@Composable () -> Unit)? = null,
    error: (@Composable () -> Unit)? = null,
    lazy: Boolean = false,
    loading: Boolean = false,
    previewLoading: Boolean = false,
    previewFailed: Boolean = false,
    failed: Boolean = false,
    initialIndex: Int = 0,
    hideOnClickModal: Boolean = false,
    closeOnPressEscape: Boolean = true,
    infinite: Boolean = true,
    zoomRate: Float = 1.2f,
    minScale: Float = 0.2f,
    maxScale: Float = 7f,
    showProgress: Boolean = false,
    onLoad: (() -> Unit)? = null,
    onError: (() -> Unit)? = null,
    progress: (@Composable (activeIndex: Int, total: Int) -> Unit)? = null,
    toolbar: (@Composable (ElImageViewerActions) -> Unit)? = null,
    viewerPlaceholder: (@Composable (activeIndex: Int, total: Int) -> Unit)? = null,
    viewerError: (@Composable (activeIndex: Int, total: Int) -> Unit)? = null,
) {
    var previewVisible by remember { mutableStateOf(false) }
    var previewIndex by remember { mutableIntStateOf(initialIndex) }
    var revealed by remember(lazy) { mutableStateOf(!lazy) }
    val windowSize = LocalWindowInfo.current.containerSize
    val theme = LocalElTheme.current
    val images = previewImages.ifEmpty { listOfNotNull(painter) }
    val showImage = painter != null && !loading && !failed && (!lazy || revealed)
    LaunchedEffect(showImage, painter, failed) {
        if (showImage) onLoad?.invoke() else if (painter == null || failed) onError?.invoke()
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(theme.radius.base))
            .background(theme.colors.fillLight)
            .onGloballyPositioned { coordinates ->
                if (lazy && !revealed) {
                    val bounds = coordinates.boundsInWindow()
                    revealed = bounds.right > 0f &&
                        bounds.bottom > 0f &&
                        bounds.left < windowSize.width &&
                        bounds.top < windowSize.height
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        if (showImage) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = previewEnabled) {
                        previewIndex = images.indexOf(painter).takeIf { it >= 0 } ?: initialIndex
                        previewVisible = true
                    },
                contentScale = fit,
            )
            if (previewEnabled) {
                Box(
                    Modifier
                        .align(Alignment.Center)
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .clickable {
                            previewIndex = images.indexOf(painter).takeIf { it >= 0 } ?: initialIndex
                            previewVisible = true
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    ElIcon(ElIcons.ZoomIn, size = 18.dp, tint = Color.White)
                }
            }
        } else {
            when {
                error != null && (failed || painter == null) -> error()
                else -> placeholder?.invoke() ?: DefaultImagePlaceholder()
            }
        }
    }
    if (previewVisible) {
        ElImageViewer(
            visible = previewVisible,
            images = images,
            onClose = { previewVisible = false },
            initialIndex = previewIndex,
            showProgress = showProgress,
            infinite = infinite,
            hideOnClickModal = hideOnClickModal,
            closeOnPressEscape = closeOnPressEscape,
            zoomRate = zoomRate,
            minScale = minScale,
            maxScale = maxScale,
            loading = previewLoading,
            failed = previewFailed,
            progress = progress,
            toolbar = toolbar,
            placeholder = viewerPlaceholder,
            error = viewerError,
        )
    }
}

@Composable
private fun DefaultImagePlaceholder() {
    val colors = LocalElTheme.current.colors
    ElIcon(ElIcons.Picture, size = 28.dp, tint = colors.textPlaceholder)
}

/**
 * `<el-image-viewer>`: full-screen pager with zoom, rotate, reset and pan
 * controls. Navigation is exposed back to the caller through [onSwitch], which
 * reports the newly active index after prev/next/setActiveItem actions.
 */
@Composable
fun ElImageViewer(
    visible: Boolean,
    images: List<Painter>,
    onClose: () -> Unit,
    initialIndex: Int = 0,
    showProgress: Boolean = true,
    infinite: Boolean = true,
    hideOnClickModal: Boolean = false,
    closeOnPressEscape: Boolean = true,
    zoomRate: Float = 1.2f,
    minScale: Float = 0.2f,
    maxScale: Float = 7f,
    showToolbar: Boolean = true,
    loading: Boolean = false,
    failed: Boolean = false,
    showClose: Boolean = true,
    onSwitch: ((Int) -> Unit)? = null,
    progress: (@Composable (activeIndex: Int, total: Int) -> Unit)? = null,
    placeholder: (@Composable (activeIndex: Int, total: Int) -> Unit)? = null,
    error: (@Composable (activeIndex: Int, total: Int) -> Unit)? = null,
    toolbar: (@Composable (ElImageViewerActions) -> Unit)? = null,
) {
    if (!visible) return
    var index by remember(initialIndex, images.size) {
        mutableIntStateOf(initialIndex.coerceIn(0, (images.size - 1).coerceAtLeast(0)))
    }
    var scale by remember(index) { mutableFloatStateOf(1f) }
    var rotation by remember(index) { mutableFloatStateOf(0f) }
    var offset by remember(index) { mutableStateOf(Offset.Zero) }
    fun goTo(target: Int) {
        if (images.isEmpty()) return
        index = when {
            target < 0 -> if (infinite) images.lastIndex else 0
            target > images.lastIndex -> if (infinite) 0 else images.lastIndex
            else -> target
        }
        onSwitch?.invoke(index)
    }
    val actions = remember(index, images.size, zoomRate, minScale, maxScale) {
        ElImageViewerActions(
            activeIndex = index,
            total = images.size,
            prev = { goTo(index - 1) },
            next = { goTo(index + 1) },
            setActiveItem = { goTo(it) },
            zoomIn = { scale = (scale * zoomRate).coerceIn(minScale, maxScale) },
            zoomOut = { scale = (scale / zoomRate).coerceIn(minScale, maxScale) },
            rotateClockwise = { rotation += 90f },
            rotateAnticlockwise = { rotation -= 90f },
            reset = {
                scale = 1f
                rotation = 0f
                offset = Offset.Zero
            },
            close = onClose,
        )
    }
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = closeOnPressEscape,
            dismissOnClickOutside = false,
        ),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .clickable(
                    enabled = hideOnClickModal,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClose,
                ),
        ) {
            if (images.isEmpty() || loading || failed) {
                val slot = if (failed) error else placeholder
                Box(Modifier.align(Alignment.Center)) {
                    if (slot != null) {
                        slot(index, images.size)
                    } else {
                        ViewerStatusPlaceholder(if (failed) ElIcons.CircleCloseFilled else ElIcons.Picture)
                    }
                }
            } else {
                Image(
                    painter = images[index],
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(48.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            rotationZ = rotation
                            translationX = offset.x
                            translationY = offset.y
                        }
                        .pointerInput(index) {
                            detectTransformGestures { _, pan, zoom, rotate ->
                                scale = (scale * zoom).coerceIn(minScale, maxScale)
                                rotation += rotate
                                if (scale > 1f) offset += pan
                            }
                        },
                    contentScale = ContentScale.Fit,
                )
            }
            if (showClose) {
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(32.dp)
                        .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center,
                ) {
                    ElIcon(ElIcons.Close, size = 18.dp, tint = Color.White)
                }
            }
            if (images.size > 1) {
                ViewerArrow(
                    icon = ElIcons.ArrowLeft,
                    modifier = Modifier.align(Alignment.CenterStart).padding(16.dp),
                ) { goTo(index - 1) }
                ViewerArrow(
                    icon = ElIcons.ArrowRight,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(16.dp),
                ) { goTo(index + 1) }
            }
            if (showToolbar) {
                Row(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = if (showProgress) 44.dp else 18.dp)
                        .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(22.dp))
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (toolbar != null) {
                        toolbar(actions)
                    } else {
                        ViewerToolIcon(ElIcons.ArrowLeft) { actions.prev() }
                        ViewerToolIcon(ElIcons.ArrowRight) { actions.next() }
                        ViewerToolIcon(ElIcons.ZoomOut) { actions.zoomOut() }
                        ViewerToolIcon(ElIcons.ZoomIn) { actions.zoomIn() }
                        ViewerToolIcon(ElIcons.RefreshLeft) { actions.rotateAnticlockwise() }
                        ViewerToolIcon(ElIcons.RefreshRight) { actions.rotateClockwise() }
                        ViewerToolIcon(ElIcons.Refresh) { actions.reset() }
                    }
                }
            }
            if (showProgress) {
                Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)) {
                    if (progress != null) {
                        progress(index, images.size)
                    } else {
                        BasicText(
                            text = "${index + 1} / ${images.size}",
                            style = LocalElTheme.current.typography.small.copy(color = Color.White),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ViewerArrow(icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier
            .size(36.dp)
            .background(Color.Black.copy(alpha = 0.35f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        ElIcon(icon, size = 20.dp, tint = Color.White)
    }
}

@Composable
private fun ViewerToolIcon(icon: ImageVector, onClick: () -> Unit) {
    Box(
        Modifier
            .size(30.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        ElIcon(icon, size = 16.dp, tint = Color.White)
    }
}

@Composable
private fun ViewerStatusPlaceholder(icon: ImageVector) {
    val colors = LocalElTheme.current.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ElIcon(icon, size = 32.dp, tint = Color.White.copy(alpha = 0.75f))
        BasicText(
            text = "Loading image…",
            style = LocalElTheme.current.typography.small.copy(color = colors.white.copy(alpha = 0.75f)),
        )
    }
}
