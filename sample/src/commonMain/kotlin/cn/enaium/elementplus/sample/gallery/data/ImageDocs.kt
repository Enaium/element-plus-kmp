package cn.enaium.elementplus.sample.gallery.data

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.components.ElImage
import cn.enaium.elementplus.components.ElImageViewer
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.DemoExample
import cn.enaium.elementplus.sample.gallery.DemoHint
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay

@Composable
fun ImageDocs() {
    val colors = LocalElTheme.current.colors
    val first = remember { ColorPainter(Color(0xFF409EFF)) }
    val second = remember { ColorPainter(Color(0xFF67C23A)) }
    val third = remember { ColorPainter(Color(0xFFE6A23C)) }
    DemoExample("Basic usage", "Display a painter in a rounded image surface.") {
        ElImage(
            painter = first,
            modifier = Modifier.size(180.dp),
            contentDescription = "Blue image",
            fit = ContentScale.Fit,
        )
    }
    DemoExample("Image preview", "Click the image to open a full-screen viewer.") {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(first, second, third).forEachIndexed { index, painter ->
                ElImage(
                    painter = painter,
                    modifier = Modifier.size(120.dp),
                    previewEnabled = true,
                    previewImages = listOf(first, second, third),
                    contentDescription = "Image ${index + 1}",
                )
            }
        }
    }
    DemoExample("Lazy load", "Images stay on their placeholder until they scroll into the viewport.") {
        val lazyImages = remember {
            listOf(
                ColorPainter(Color(0xFF409EFF)),
                ColorPainter(Color(0xFF67C23A)),
                ColorPainter(Color(0xFFE6A23C)),
                ColorPainter(Color(0xFFF56C6C)),
                ColorPainter(Color(0xFF909399)),
                ColorPainter(Color(0xFF8E44AD)),
            )
        }
        Column(
            Modifier.fillMaxWidth().height(260.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            lazyImages.forEachIndexed { index, painter ->
                ElImage(
                    painter = painter,
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    lazy = true,
                    contentDescription = "Lazy image ${index + 1}",
                    placeholder = {
                        BasicText(
                            text = "Lazy image ${index + 1}",
                            style = LocalElTheme.current.typography.small.copy(color = colors.textPlaceholder),
                        )
                    },
                )
            }
        }
    }
    DemoExample("Placeholder and error", "Provide a custom placeholder for missing images.") {
        ElImage(
            painter = null,
            modifier = Modifier.size(180.dp),
            placeholder = { BasicText("No image", style = LocalElTheme.current.typography.small.copy(color = colors.textSecondary)) },
        )
    }
    DemoExample("Fit", "Control how the image fills its container.") {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ElImage(first, modifier = Modifier.size(100.dp), fit = ContentScale.Crop)
            ElImage(first, modifier = Modifier.size(100.dp), fit = ContentScale.Fit)
        }
    }

    DemoExample("Placeholder", "Render a custom placeholder while no image is available.") {
        ElImage(
            painter = null,
            modifier = Modifier.size(180.dp, 120.dp),
            placeholder = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ElIcon(ElIcons.Picture, size = 28.dp, tint = LocalElTheme.current.colors.textPlaceholder)
                    BasicText("Loading image", style = LocalElTheme.current.typography.extraSmall.copy(color = LocalElTheme.current.colors.textSecondary))
                }
            },
        )
    }
    DemoExample("Load failed", "Render a custom error state.") {
        ElImage(
            painter = null,
            failed = true,
            modifier = Modifier.size(180.dp, 120.dp),
            error = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ElIcon(ElIcons.CircleCloseFilled, size = 28.dp, tint = LocalElTheme.current.colors.danger)
                    BasicText("Failed to load", style = LocalElTheme.current.typography.extraSmall.copy(color = LocalElTheme.current.colors.danger))
                }
            },
        )
    }
    DemoExample("Custom progress", "Replace the viewer progress text with the progress slot.") {
        val images = remember { listOf(first, second, third, first, second, third) }
        ElImage(
            painter = first,
            modifier = Modifier.size(100.dp),
            previewEnabled = true,
            previewImages = images,
            showProgress = true,
            contentDescription = "Preview with custom progress",
            progress = { activeIndex, total ->
                BasicText(
                    text = "${activeIndex + 1}-$total",
                    style = LocalElTheme.current.typography.small.copy(color = Color.White),
                )
            },
        )
    }
    DemoExample("Custom toolbar", "Replace the viewer toolbar with your own zoom, rotate and navigation actions.") {
        val images = remember { listOf(first, second, third) }
        ElImage(
            painter = first,
            modifier = Modifier.size(100.dp),
            previewEnabled = true,
            previewImages = images,
            showProgress = true,
            contentDescription = "Preview with custom toolbar",
            toolbar = { actions ->
                ViewerToolButton(ElIcons.Back) { actions.prev() }
                ViewerToolButton(ElIcons.Right) { actions.next() }
                ViewerToolButton(ElIcons.DArrowRight) { actions.setActiveItem(actions.total - 1) }
                ViewerToolButton(ElIcons.ZoomOut) { actions.zoomOut() }
                ViewerToolButton(ElIcons.ZoomIn) { actions.zoomIn() }
                ViewerToolButton(ElIcons.RefreshRight) { actions.rotateClockwise() }
                ViewerToolButton(ElIcons.RefreshLeft) { actions.rotateAnticlockwise() }
                ViewerToolButton(ElIcons.Refresh) { actions.reset() }
            },
        )
    }
    DemoExample("Viewer placeholder", "Show a loading placeholder inside the viewer until the preview is ready.") {
        var visible by remember { mutableStateOf(false) }
        var loading by remember { mutableStateOf(false) }
        val images = remember { listOf(first, second, third) }
        LaunchedEffect(loading) {
            if (loading) {
                delay(1500)
                loading = false
            }
        }
        ElButton(onClick = { loading = true; visible = true }, text = "Reload preview", size = ElSize.Small)
        ElImageViewer(
            visible = visible,
            images = images,
            onClose = { visible = false },
            loading = loading,
            showProgress = true,
            placeholder = { activeIndex, _ ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ElIcon(ElIcons.Loading, size = 32.dp, tint = Color.White)
                    BasicText(
                        text = "Loading image ${activeIndex + 1}...",
                        style = LocalElTheme.current.typography.small.copy(color = Color.White),
                    )
                }
            },
        )
        DemoHint("ElImage forwards the same slot through its viewerPlaceholder parameter.")
    }
    DemoExample("Zoom and rotate", "Zoom, rotate, pan and reset the previewed image.") {
        ElImage(
            painter = second,
            modifier = Modifier.size(120.dp),
            previewEnabled = true,
            previewImages = remember { listOf(first, second, third) },
            showProgress = true,
            zoomRate = 1.5f,
            minScale = 0.5f,
            maxScale = 4f,
            contentDescription = "Preview with zoom limits",
        )
        DemoHint("Pinch or drag inside the viewer, or use the toolbar's zoom, rotate and reset buttons.")
    }
    DemoExample("Manually open preview", "Use the viewer component directly.") {
        var visible by remember { mutableStateOf(false) }
        var active by remember { mutableIntStateOf(0) }
        val images = listOf(ColorPainter(LocalElTheme.current.colors.primary), ColorPainter(LocalElTheme.current.colors.success))
        ElButton(onClick = { visible = true }, text = "Open image viewer")
        ElImageViewer(
            visible = visible,
            images = images,
            onClose = { visible = false },
            infinite = false,
            onSwitch = { index -> active = index },
        )
        DemoHint("Active image: ${active + 1}")
    }
}

@Composable
private fun ViewerToolButton(icon: ImageVector, onClick: () -> Unit) {
    Box(
        Modifier.size(30.dp).clickable(onClick = onClick).padding(6.dp),
        contentAlignment = Alignment.Center,
    ) {
        ElIcon(icon, size = 16.dp, tint = Color.White)
    }
}
