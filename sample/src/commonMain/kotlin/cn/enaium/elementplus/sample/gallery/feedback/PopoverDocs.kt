package cn.enaium.elementplus.sample.gallery.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.ElTag
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.components.ElInput
import cn.enaium.elementplus.components.ElSwitch
import cn.enaium.elementplus.icons.ElIcons
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme

@Composable
fun PopoverDocs() {
    DemoExample("Basic usage", "Click the trigger to open rich content.") {
        ElPopover(
            title = "Popover title",
            triggerContent = { ElButton(onClick = {}, text = "Click popover", type = ElType.Primary, plain = true) },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BasicText("This popover can contain arbitrary content.", style = LocalElTheme.current.typography.small)
                ElButton(onClick = {}, text = "Action", size = ElSize.Small)
            }
        }
    }
    DemoExample("Hover trigger", "Open the popover on hover.") {
        ElPopover(
            title = "Hover popover",
            trigger = ElPopoverTrigger.Hover,
            triggerContent = { ElText("Hover me", type = ElType.Primary) },
        ) {
            BasicText("Hover popover content", style = LocalElTheme.current.typography.small)
        }
    }
    DemoExample("Placement", "Place popovers in different directions.") {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ElPopover(
                title = "Top",
                placement = cn.enaium.elementplus.internal.ElPlacement.Top,
                triggerContent = { ElButton(onClick = {}, text = "Top") },
            ) { BasicText("Top content", style = LocalElTheme.current.typography.small) }
            ElPopover(
                title = "Bottom",
                placement = cn.enaium.elementplus.internal.ElPlacement.Bottom,
                triggerContent = { ElButton(onClick = {}, text = "Bottom") },
            ) { BasicText("Bottom content", style = LocalElTheme.current.typography.small) }
        }
    }

    DemoExample("Rich content", "Popovers can contain arbitrary composables.") {
        ElPopover(
            title = "Details",
            trigger = ElPopoverTrigger.Click,
            width = 240.dp,
            triggerContent = {
                ElButton(onClick = {}, text = "Open rich popover")
            },
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BasicText("A richer popover body", style = LocalElTheme.current.typography.small)
                    ElTag("New", type = ElType.Success, size = ElSize.Small)
                    ElButton(onClick = {}, text = "Action", size = ElSize.Small, type = ElType.Primary)
                }
            },
        )
    }

    DemoExample("Nested operation", "Use buttons and secondary popovers inside a popover.") {
        ElPopover(
            title = "Actions",
            width = 260.dp,
            triggerContent = { ElButton(onClick = {}, text = "Open actions") },
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BasicText("Choose an action", style = LocalElTheme.current.typography.small)
                    ElPopover(
                        title = "More",
                        width = 200.dp,
                        triggerContent = { ElButton(onClick = {}, text = "More options", size = ElSize.Small) },
                        content = { BasicText("Nested popover content", style = LocalElTheme.current.typography.small) },
                    )
                }
            },
        )
    }
}
