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
fun CollapseDocs() {
    DemoExample("Basic usage", "Multiple panels can be open at once.") {
        var basic by remember { mutableStateOf(listOf("1")) }
        ElCollapse(activeNames = basic, onActiveChange = { basic = it }) {
            ElCollapseItem("1", "Consistency") {
                BasicText("Consistency makes products feel predictable.", style = LocalElTheme.current.typography.base)
            }
            ElCollapseItem("2", "Feedback") {
                BasicText("Every action should produce visible feedback.", style = LocalElTheme.current.typography.base)
            }
            ElCollapseItem("3", "Efficiency") {
                BasicText("Reduce the number of steps needed to finish a task.", style = LocalElTheme.current.typography.base)
            }
        }
    }
    DemoExample("Accordion", "Only one panel is open at a time.") {
        var accordion by remember { mutableStateOf(listOf("1")) }
        ElCollapse(activeNames = accordion, onActiveChange = { accordion = it }, accordion = true) {
            ElCollapseItem("1", "Panel one") { BasicText("Panel one content") }
            ElCollapseItem("2", "Panel two") { BasicText("Panel two content") }
            ElCollapseItem("3", "Panel three") { BasicText("Panel three content") }
        }
    }
    DemoExample("Disabled", "Disable an individual panel.") {
        var basic by remember { mutableStateOf(listOf("1")) }
        ElCollapse(activeNames = basic, onActiveChange = { basic = it }) {
            ElCollapseItem("1", "Enabled") { BasicText("Enabled content") }
            ElCollapseItem("2", "Disabled", disabled = true) { BasicText("Disabled content") }
        }
    }
    DemoExample("Custom content", "Panel content can be any composable.") {
        var basic by remember { mutableStateOf(listOf("1")) }
        ElCollapse(activeNames = basic, onActiveChange = { basic = it }) {
            ElCollapseItem("custom", "A form inside a panel") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    BasicText("Use the panel for settings or secondary content.", style = LocalElTheme.current.typography.base)
                    ElButton(onClick = {}, text = "Save", type = ElType.Primary)
                }
            }
        }
    }

    DemoExample("Prevent collapsing", "Keep at least one panel open by rejecting empty active lists.") {
        var names by remember { mutableStateOf(listOf("1")) }
        ElCollapse(activeNames = names, onActiveChange = { if (it.isNotEmpty()) names = it }) {
            ElCollapseItem("1", "Panel one") { BasicText("Panel one content") }
            ElCollapseItem("2", "Panel two") { BasicText("Panel two content") }
        }
    }

    DemoExample("Custom title", "Render a custom title instead of plain text.") {
        var names by remember { mutableStateOf(listOf("1")) }
        ElCollapse(activeNames = names, onActiveChange = { names = it }) {
            ElCollapseItem(
                "1",
                "",
                titleContent = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        ElTag("New", type = ElType.Success, size = ElSize.Small)
                        BasicText("Custom title", style = LocalElTheme.current.typography.base)
                    }
                },
            ) { BasicText("Custom title content") }
            ElCollapseItem("2", "Panel two") { BasicText("Panel two content") }
        }
    }

    DemoExample("Custom icon and position", "Choose the expand icon and place it before or after the title.") {
        var names by remember { mutableStateOf(listOf("1")) }
        ElCollapse(activeNames = names, onActiveChange = { names = it }) {
            ElCollapseItem("1", "Icon at start", icon = ElIcons.CaretBottom, iconPosition = ElCollapseIconPosition.Start) {
                BasicText("Content for the start icon panel")
            }
            ElCollapseItem("2", "Icon at end", icon = ElIcons.CaretBottom, iconPosition = ElCollapseIconPosition.End) {
                BasicText("Content for the end icon panel")
            }
        }
    }
}
