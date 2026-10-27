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
fun SegmentedDocs() {
    val options = listOf(
        ElSegmentedOption("daily", "Daily"),
        ElSegmentedOption("weekly", "Weekly"),
        ElSegmentedOption("monthly", "Monthly"),
    )
    DemoExample("Basic usage", "Select one option from a segmented control.") {
        var basic by remember { mutableStateOf("daily") }
        ElSegmented(options = options, value = basic, onValueChange = { basic = it })
        DemoHint("Selected: $basic")
    }
    DemoExample("Block", "Block mode stretches the control to the available width.") {
        var block by remember { mutableStateOf("week") }
        ElSegmented(
            options = options,
            value = block,
            onValueChange = { block = it },
            block = true,
            modifier = Modifier.width(420.dp),
        )
    }
    DemoExample("Custom content", "Options can include icons.") {
        var icons by remember { mutableStateOf("day") }
        ElSegmented(
            options = listOf(
                ElSegmentedOption("day", "Day", ElIcons.Sunny),
                ElSegmentedOption("week", "Week", ElIcons.Calendar),
                ElSegmentedOption("month", "Month", ElIcons.Clock),
            ),
            value = icons,
            onValueChange = { icons = it },
        )
    }
    DemoExample("Disabled", "Disable individual options or the whole control.") {
        var disabled by remember { mutableStateOf("a") }
        var fullyDisabled by remember { mutableStateOf("daily") }
        ElSegmented(
            options = listOf(
                ElSegmentedOption("a", "Option A"),
                ElSegmentedOption("b", "Option B", disabled = true),
                ElSegmentedOption("c", "Option C"),
            ),
            value = disabled,
            onValueChange = { disabled = it },
        )
        ElSegmented(options = options, value = fullyDisabled, onValueChange = { fullyDisabled = it }, disabled = true, modifier = Modifier.padding(top = 10.dp))
    }
    DemoExample("Sizes", "Segmented controls follow the component size scale.") {
        var large by remember { mutableStateOf("daily") }
        var small by remember { mutableStateOf("daily") }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElSegmented(options = options, value = large, onValueChange = { large = it }, size = ElSize.Large)
            ElSegmented(options = options, value = small, onValueChange = { small = it }, size = ElSize.Small)
        }
    }

    DemoExample("Direction usage", "Use a vertical segmented control by setting vertical and block.") {
        var value by remember { mutableStateOf("a") }
        ElSegmented(
            options = listOf(
                ElSegmentedOption("a", "Option A"),
                ElSegmentedOption("b", "Option B"),
                ElSegmentedOption("c", "Option C"),
            ),
            value = value,
            onValueChange = { value = it },
            vertical = true,
            block = true,
            modifier = Modifier.height(180.dp),
        )
    }
}
