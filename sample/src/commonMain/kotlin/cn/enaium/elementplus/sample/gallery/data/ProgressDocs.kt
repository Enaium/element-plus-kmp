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
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.icons.ElIcons
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
fun ProgressDocs() {
    val colors = LocalElTheme.current.colors
    DemoExample("Linear progress bar", "Progress can be shown with or without text.") {
        Column(Modifier.width(420.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ElProgress(percentage = 50.0)
            ElProgress(percentage = 80.0, textInside = true)
        }
    }
    DemoExample("Status", "Use a status to color the progress bar.") {
        Column(Modifier.width(420.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ElProgress(percentage = 100.0, status = ElProgressStatus.Success)
            ElProgress(percentage = 60.0, status = ElProgressStatus.Warning)
            ElProgress(percentage = 30.0, status = ElProgressStatus.Exception)
        }
    }
    DemoExample("Custom color", "Override the progress color.") {
        ElProgress(percentage = 65.0, color = colors.success)
    }
    DemoExample("Striped", "Striped progress makes activity more visible.") {
        ElProgress(percentage = 70.0, striped = true, status = ElProgressStatus.Warning)
    }
    DemoExample("Circular progress bar", "Circle progress supports a text label in the center.") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            ElProgress(percentage = 75.0, type = ElProgressType.Circle, width = 110.dp)
            ElProgress(percentage = 42.0, type = ElProgressType.Circle, width = 110.dp, color = colors.warning)
        }
    }
    DemoExample("Dashboard progress bar", "A dashboard progress uses a partial circle.") {
        ElProgress(percentage = 55.0, type = ElProgressType.Dashboard, width = 120.dp)
    }

    DemoExample("Internal percentage", "Place the percentage inside the progress bar.") {
        ElProgress(percentage = 70.0, textInside = true, width = 300.dp)
    }
    DemoExample("Customized content", "Format the progress text with a custom formatter.") {
        ElProgress(percentage = 70.0, format = { "Done ${it.toInt()} / 100" }, width = 300.dp)
    }

    DemoExample("Indeterminate progress", "An indeterminate bar cycles continuously; duration controls the animation speed.") {
        Column(Modifier.width(420.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ElProgress(percentage = 50.0, indeterminate = true)
            ElProgress(
                percentage = 100.0,
                indeterminate = true,
                format = { if (it >= 100.0) "Full" else "${it.toInt()}%" },
            )
            ElProgress(percentage = 100.0, status = ElProgressStatus.Success, indeterminate = true, duration = 5)
            ElProgress(percentage = 100.0, status = ElProgressStatus.Warning, indeterminate = true, duration = 1)
            ElProgress(percentage = 50.0, status = ElProgressStatus.Exception, indeterminate = true)
        }
    }

    DemoExample("Striped flow", "Animated stripes make an active bar more visible.") {
        var percent by remember { mutableStateOf(70.0) }
        Column(Modifier.width(420.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElProgress(percentage = 50.0, strokeWidth = 15.dp, striped = true)
            ElProgress(percentage = 30.0, strokeWidth = 15.dp, status = ElProgressStatus.Warning, striped = true, stripedFlow = true)
            ElProgress(
                percentage = percent,
                strokeWidth = 15.dp,
                status = ElProgressStatus.Exception,
                striped = true,
                stripedFlow = true,
                duration = 2,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ElButton(onClick = { percent = (percent - 10.0).coerceAtLeast(0.0) }, text = "-10")
                ElButton(onClick = { percent = (percent + 10.0).coerceAtMost(100.0) }, text = "+10")
            }
        }
    }

    DemoExample("Segmented and dynamic colors", "Color the bar with stops or a function of the percentage.") {
        Column(Modifier.width(420.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElProgress(
                percentage = 65.0,
                colorFunction = { p ->
                    when {
                        p < 30 -> colors.info
                        p < 70 -> colors.warning
                        else -> colors.success
                    }
                },
            )
            ElProgress(
                percentage = 75.0,
                colorStops = listOf(
                    ElProgressColor(colors.danger, 25.0),
                    ElProgressColor(colors.warning, 50.0),
                    ElProgressColor(colors.success, 100.0),
                ),
            )
        }
    }

    DemoExample("Custom content", "Replace the percentage text with any composable.") {
        Column(Modifier.width(460.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ElProgress(percentage = 50.0, content = { p -> DemoHint("${p.toInt()}% complete") })
            ElProgress(percentage = 50.0, textInside = true, strokeWidth = 20.dp, content = { BasicText("Content") })
            ElProgress(percentage = 100.0, type = ElProgressType.Circle, status = ElProgressStatus.Success, content = {
                ElIcon(ElIcons.Check, size = 26.dp, tint = colors.success)
            })
            ElProgress(percentage = 80.0, type = ElProgressType.Dashboard, content = { p ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BasicText("${p.toInt()}%", style = LocalElTheme.current.typography.large)
                    DemoHint("Progressing")
                }
            })
        }
    }

    DemoExample("Stroke line cap", "Circle and dashboard strokes support round, square and butt caps.") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            ElProgress(percentage = 60.0, type = ElProgressType.Circle, width = 100.dp, strokeWidth = 10.dp, strokeLinecap = ElProgressStrokeLinecap.Round)
            ElProgress(percentage = 60.0, type = ElProgressType.Circle, width = 100.dp, strokeWidth = 10.dp, strokeLinecap = ElProgressStrokeLinecap.Square)
            ElProgress(percentage = 60.0, type = ElProgressType.Circle, width = 100.dp, strokeWidth = 10.dp, strokeLinecap = ElProgressStrokeLinecap.Butt)
        }
    }
}
