package cn.enaium.elementplus.sample.gallery.data

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.ElMessageType
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElSwitch
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElTextAlign
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

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
            fit = androidx.compose.ui.layout.ContentScale.Fit,
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
    DemoExample("Placeholder and error", "Provide a custom placeholder for missing images.") {
        ElImage(
            painter = null,
            modifier = Modifier.size(180.dp),
            placeholder = { BasicText("No image", style = LocalElTheme.current.typography.small.copy(color = colors.textSecondary)) },
        )
    }
    DemoExample("Fit", "Control how the image fills its container.") {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ElImage(first, modifier = Modifier.size(100.dp), fit = androidx.compose.ui.layout.ContentScale.Crop)
            ElImage(first, modifier = Modifier.size(100.dp), fit = androidx.compose.ui.layout.ContentScale.Fit)
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
            modifier = Modifier.size(180.dp, 120.dp),
            error = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ElIcon(ElIcons.CircleCloseFilled, size = 28.dp, tint = LocalElTheme.current.colors.danger)
                    BasicText("Failed to load", style = LocalElTheme.current.typography.extraSmall.copy(color = LocalElTheme.current.colors.danger))
                }
            },
        )
    }
    DemoExample("Manually open preview", "Use the viewer component directly.") {
        var visible by remember { mutableStateOf(false) }
        val images = listOf(ColorPainter(LocalElTheme.current.colors.primary), ColorPainter(LocalElTheme.current.colors.success))
        ElButton(onClick = { visible = true }, text = "Open image viewer")
        ElImageViewer(visible = visible, images = images, onClose = { visible = false })
    }
}
