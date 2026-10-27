package cn.enaium.elementplus.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.theme.LocalElTheme

/**
 * `<el-image>` with placeholder/error slots and an optional full-screen preview.
 * Images are passed as [Painter]s so the library stays free of a specific image
 * loading backend.
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
) {
    var previewVisible by remember { mutableStateOf(false) }
    var previewIndex by remember { mutableIntStateOf(0) }
    val theme = LocalElTheme.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(theme.radius.base))
            .background(theme.colors.fillLight),
        contentAlignment = Alignment.Center,
    ) {
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = fit,
            )
            if (previewEnabled) {
                Box(
                    Modifier
                        .align(Alignment.Center)
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .clickable {
                            previewIndex = previewImages.indexOf(painter).coerceAtLeast(0)
                            previewVisible = true
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    ElIcon(ElIcons.ZoomIn, size = 18.dp, tint = Color.White)
                }
            }
        } else {
            if (error != null) error() else placeholder?.invoke() ?: DefaultImagePlaceholder()
        }
    }
    if (previewVisible) {
        ElImageViewer(
            visible = previewVisible,
            images = previewImages.ifEmpty { listOfNotNull(painter) },
            initialIndex = previewIndex,
            onClose = { previewVisible = false },
        )
    }
}

@Composable
private fun DefaultImagePlaceholder() {
    val colors = LocalElTheme.current.colors
    ElIcon(ElIcons.Picture, size = 28.dp, tint = colors.textPlaceholder)
}

/** `<el-image-viewer>`: full-screen pager with close and arrow controls. */
@Composable
fun ElImageViewer(
    visible: Boolean,
    images: List<Painter>,
    onClose: () -> Unit,
    initialIndex: Int = 0,
    showProgress: Boolean = true,
) {
    if (!visible) return
    var index by remember(initialIndex) { mutableIntStateOf(initialIndex.coerceIn(0, (images.size - 1).coerceAtLeast(0))) }
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.9f))) {
            if (images.isNotEmpty()) {
                Image(
                    painter = images[index],
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentScale = ContentScale.Fit,
                )
            }
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
            if (images.size > 1) {
                Box(
                    Modifier
                        .align(Alignment.CenterStart)
                        .padding(16.dp)
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                        .clickable { index = (index - 1 + images.size) % images.size },
                    contentAlignment = Alignment.Center,
                ) {
                    ElIcon(ElIcons.ArrowLeft, size = 20.dp, tint = Color.White)
                }
                Box(
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(16.dp)
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                        .clickable { index = (index + 1) % images.size },
                    contentAlignment = Alignment.Center,
                ) {
                    ElIcon(ElIcons.ArrowRight, size = 20.dp, tint = Color.White)
                }
            }
            if (showProgress && images.isNotEmpty()) {
                BasicText(
                    text = "${index + 1} / ${images.size}",
                    modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp),
                    style = LocalElTheme.current.typography.small.copy(color = Color.White),
                )
            }
        }
    }
}
