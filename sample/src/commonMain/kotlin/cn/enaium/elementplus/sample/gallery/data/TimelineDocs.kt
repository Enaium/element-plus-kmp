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
fun TimelineDocs() {
    val theme = LocalElTheme.current
    DemoExample("Basic usage", "A vertical timeline shows events in order.") {
        ElTimeline(
            items = listOf(
                ElTimelineItem({ BasicText("Create a repository", style = theme.typography.base) }, timestamp = "2026-10-01"),
                ElTimelineItem({ BasicText("Implement components", style = theme.typography.base) }, timestamp = "2026-10-08"),
                ElTimelineItem({ BasicText("Publish the sample", style = theme.typography.base) }, timestamp = "2026-10-26"),
            ),
        )
    }
    DemoExample("Timestamp placement", "Place timestamps above or below the content.") {
        ElTimeline(
            items = listOf(
                ElTimelineItem({ BasicText("Top timestamp", style = theme.typography.base) }, timestamp = "09:00", placement = ElTimelinePlacement.Top),
                ElTimelineItem({ BasicText("Bottom timestamp", style = theme.typography.base) }, timestamp = "10:30", placement = ElTimelinePlacement.Bottom),
            ),
        )
    }
    DemoExample("Hollow nodes and colors", "Customize the node style and color.") {
        ElTimeline(
            items = listOf(
                ElTimelineItem({ BasicText("Success", style = theme.typography.base) }, type = ElType.Success),
                ElTimelineItem({ BasicText("Hollow", style = theme.typography.base) }, hollow = true, type = ElType.Warning),
                ElTimelineItem({ BasicText("Custom color", style = theme.typography.base) }, color = Color(0xFF8E44AD)),
            ),
        )
    }
    DemoExample("Reverse", "Reverse the order of timeline items.") {
        ElTimeline(
            items = listOf(
                ElTimelineItem({ BasicText("First") }),
                ElTimelineItem({ BasicText("Second") }),
                ElTimelineItem({ BasicText("Third") }),
            ),
            reverse = true,
        )
    }

    DemoExample("Custom node", "Render a custom node content and timestamp.") {
        ElTimeline(
            items = listOf(
                ElTimelineItem(
                    timestamp = "2026-10-26 09:30",
                    content = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            ElTag("Update", type = ElType.Success, size = ElSize.Small)
                            BasicText("Deployment completed", style = LocalElTheme.current.typography.base)
                        }
                    },
                ),
                ElTimelineItem(
                    timestamp = "2026-10-25 18:10",
                    type = ElType.Warning,
                    content = { BasicText("Review requested", style = LocalElTheme.current.typography.base) },
                ),
            ),
        )
    }
}
