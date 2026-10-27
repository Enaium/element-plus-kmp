package cn.enaium.elementplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.icons.ElIcon
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

enum class ElTimelinePlacement { Top, Bottom }

/** Relative position of the timeline and its content (upstream `mode`). */
enum class ElTimelineMode { Start, Alternate, AlternateReverse, End }

enum class ElTimelineNodeSize { Normal, Large }

class ElTimelineItem(
    val content: @Composable () -> Unit,
    val timestamp: String? = null,
    val type: ElType = ElType.Primary,
    val hollow: Boolean = false,
    val placement: ElTimelinePlacement = ElTimelinePlacement.Bottom,
    val color: Color? = null,
    val center: Boolean = false,
    val hideTimestamp: Boolean = false,
    val icon: ImageVector? = null,
    val size: ElTimelineNodeSize = ElTimelineNodeSize.Normal,
    val timestampContent: (@Composable () -> Unit)? = null,
)

/**
 * `<el-timeline>` with dots, connectors, timestamps and hollow variants.
 * [mode] mirrors upstream (`start`, `alternate`, `alternate-reverse`, `end`)
 * and items can center their node, hide the timestamp, use a custom icon or a
 * large node.
 */
@Composable
fun ElTimeline(
    items: List<ElTimelineItem>,
    modifier: Modifier = Modifier,
    reverse: Boolean = false,
    mode: ElTimelineMode = ElTimelineMode.Start,
) {
    val ordered = if (reverse) items.reversed() else items
    Column(modifier) {
        ordered.forEachIndexed { index, item ->
            val centeredRail = mode == ElTimelineMode.Alternate || mode == ElTimelineMode.AlternateReverse
            val contentOnLeft = when (mode) {
                ElTimelineMode.Start -> false
                ElTimelineMode.End -> true
                ElTimelineMode.Alternate -> index % 2 == 1
                ElTimelineMode.AlternateReverse -> index % 2 == 0
            }
            TimelineRow(
                item = item,
                isFirst = index == 0,
                isLast = index == ordered.lastIndex,
                centeredRail = centeredRail,
                contentOnLeft = contentOnLeft,
            )
        }
    }
}

@Composable
private fun TimelineRow(
    item: ElTimelineItem,
    isFirst: Boolean,
    isLast: Boolean,
    centeredRail: Boolean,
    contentOnLeft: Boolean,
) {
    val nodeSize = if (item.size == ElTimelineNodeSize.Large) 14.dp else 12.dp
    val railWidth = 16.dp
    val horizontalPadding = if (centeredRail) 20.dp else 10.dp
    Row(
        Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top,
    ) {
        if (centeredRail) {
            TimelineContent(
                item = item,
                visible = contentOnLeft,
                alignEnd = true,
                isLast = isLast,
                horizontalPadding = horizontalPadding,
                modifier = Modifier.weight(1f),
            )
            TimelineRail(
                item = item,
                isFirst = isFirst,
                isLast = isLast,
                verticallyCentered = item.center,
                nodeSize = nodeSize,
                modifier = Modifier.width(railWidth),
            )
            TimelineContent(
                item = item,
                visible = !contentOnLeft,
                alignEnd = false,
                isLast = isLast,
                horizontalPadding = horizontalPadding,
                modifier = Modifier.weight(1f),
            )
        } else if (contentOnLeft) {
            TimelineContent(
                item = item,
                visible = true,
                alignEnd = true,
                isLast = isLast,
                horizontalPadding = horizontalPadding,
                modifier = Modifier.weight(1f),
            )
            TimelineRail(
                item = item,
                isFirst = isFirst,
                isLast = isLast,
                verticallyCentered = item.center,
                nodeSize = nodeSize,
                modifier = Modifier.width(railWidth),
            )
        } else {
            TimelineRail(
                item = item,
                isFirst = isFirst,
                isLast = isLast,
                verticallyCentered = item.center,
                nodeSize = nodeSize,
                modifier = Modifier.width(railWidth),
            )
            TimelineContent(
                item = item,
                visible = true,
                alignEnd = false,
                isLast = isLast,
                horizontalPadding = horizontalPadding,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TimelineRail(
    item: ElTimelineItem,
    isFirst: Boolean,
    isLast: Boolean,
    verticallyCentered: Boolean,
    nodeSize: Dp,
    modifier: Modifier = Modifier,
) {
    val colors = LocalElTheme.current.colors
    val dotColor = item.color ?: colors.colorOf(item.type)
    Box(
        modifier.fillMaxHeight(),
        contentAlignment = if (verticallyCentered) Alignment.Center else Alignment.TopCenter,
    ) {
        // Connector line. Vertically centered nodes use half lines at the first/last item.
        if (verticallyCentered) {
            when {
                isFirst && isLast -> Unit
                isLast -> Box(
                    Modifier
                        .align(Alignment.TopCenter)
                        .width(2.dp)
                        .fillMaxHeight(0.5f)
                        .background(colors.borderLighter),
                )
                isFirst -> Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .width(2.dp)
                        .fillMaxHeight(0.5f)
                        .background(colors.borderLighter),
                )
                else -> Box(
                    Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(colors.borderLighter),
                )
            }
        } else if (!isLast) {
            Box(
                Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .padding(top = 12.dp)
                    .background(colors.borderLighter),
            )
        }
        Box(
            Modifier
                .then(if (verticallyCentered) Modifier else Modifier.padding(top = 4.dp))
                .size(nodeSize)
                .then(
                    if (item.hollow) Modifier
                        .background(colors.background, CircleShape)
                        .border(2.dp, dotColor, CircleShape)
                    else Modifier.background(dotColor, CircleShape)
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (item.icon != null) {
                ElIcon(
                    item.icon,
                    size = nodeSize - 4.dp,
                    tint = if (item.hollow) dotColor else colors.white,
                )
            }
        }
    }
}

@Composable
private fun TimelineContent(
    item: ElTimelineItem,
    visible: Boolean,
    alignEnd: Boolean,
    isLast: Boolean,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier,
) {
    if (!visible) {
        Box(modifier.fillMaxHeight())
        return
    }
    val theme = LocalElTheme.current
    val colors = theme.colors
    Column(
        modifier = modifier.padding(
            start = if (alignEnd) 0.dp else horizontalPadding,
            end = if (alignEnd) horizontalPadding else 0.dp,
            bottom = if (isLast) 0.dp else 20.dp,
        ),
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start,
    ) {
        if (!item.hideTimestamp && item.placement == ElTimelinePlacement.Top) {
            TimelineTimestamp(item, alignEnd, Modifier.padding(bottom = 4.dp))
        }
        item.content()
        if (!item.hideTimestamp && item.placement == ElTimelinePlacement.Bottom) {
            TimelineTimestamp(item, alignEnd, Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun TimelineTimestamp(
    item: ElTimelineItem,
    alignEnd: Boolean,
    modifier: Modifier = Modifier,
) {
    val theme = LocalElTheme.current
    val colors = theme.colors
    val timestampContent = item.timestampContent
    if (timestampContent != null) {
        Box(modifier, contentAlignment = if (alignEnd) Alignment.CenterEnd else Alignment.CenterStart) {
            timestampContent()
        }
    } else if (item.timestamp != null) {
        BasicText(
            text = item.timestamp,
            modifier = modifier.fillMaxWidth(),
            style = theme.typography.extraSmall.copy(
                color = colors.textSecondary,
                textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
            ),
        )
    }
}
