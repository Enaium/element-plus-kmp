package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElTimelinePlacement { Top, Bottom }

class ElTimelineItem(
    val content: @Composable () -> Unit,
    val timestamp: String? = null,
    val type: ElType = ElType.Primary,
    val hollow: Boolean = false,
    val placement: ElTimelinePlacement = ElTimelinePlacement.Bottom,
    val color: Color? = null,
)

/** `<el-timeline>` with dots, connectors, timestamps and hollow variants. */
@Composable
fun ElTimeline(
    items: List<ElTimelineItem>,
    modifier: Modifier = Modifier,
    reverse: Boolean = false,
) {
    val ordered = if (reverse) items.reversed() else items
    Column(modifier) {
        ordered.forEachIndexed { index, item ->
            TimelineRow(
                item = item,
                isLast = index == ordered.lastIndex,
            )
        }
    }
}

@Composable
private fun TimelineRow(item: ElTimelineItem, isLast: Boolean) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val dotColor = item.color ?: colors.colorOf(item.type)
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Box(Modifier.width(16.dp).fillMaxHeight(), contentAlignment = Alignment.TopCenter) {
            if (!isLast) {
                Box(Modifier.width(2.dp).fillMaxHeight().padding(top = 12.dp).background(colors.borderLighter))
            }
            Box(
                Modifier
                    .padding(top = 4.dp)
                    .size(12.dp)
                    .then(
                        if (item.hollow) Modifier
                            .background(colors.background, CircleShape)
                            .border(2.dp, dotColor, CircleShape)
                        else Modifier.background(dotColor, CircleShape)
                    ),
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 20.dp)) {
            if (item.placement == ElTimelinePlacement.Top && item.timestamp != null) {
                BasicText(
                    text = item.timestamp,
                    modifier = Modifier.padding(bottom = 4.dp),
                    style = theme.typography.extraSmall.copy(color = colors.textSecondary),
                )
            }
            item.content()
            if (item.placement == ElTimelinePlacement.Bottom && item.timestamp != null) {
                BasicText(
                    text = item.timestamp,
                    modifier = Modifier.padding(top = 4.dp),
                    style = theme.typography.extraSmall.copy(color = colors.textSecondary),
                )
            }
        }
    }
}
