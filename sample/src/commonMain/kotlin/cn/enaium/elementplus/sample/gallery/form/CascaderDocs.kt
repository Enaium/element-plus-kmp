package cn.enaium.elementplus.sample.gallery.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import cn.enaium.elementplus.components.ElButton
import cn.enaium.elementplus.components.ElText
import cn.enaium.elementplus.components.ElTag
import cn.enaium.elementplus.components.ElTagEffect
import cn.enaium.elementplus.components.ElTreeNode
import cn.enaium.elementplus.components.ElMessage
import cn.enaium.elementplus.components.ElMessageType
import cn.enaium.elementplus.components.*
import cn.enaium.elementplus.sample.gallery.*
import cn.enaium.elementplus.theme.ElSize
import cn.enaium.elementplus.theme.ElType
import cn.enaium.elementplus.theme.LocalElTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber

@Composable
fun CascaderDocs() {
    val options = listOf(
        ElCascaderOption(
            "sichuan", "Sichuan", listOf(
                ElCascaderOption("chengdu", "Chengdu"),
                ElCascaderOption("mianyang", "Mianyang"),
            ),
        ),
        ElCascaderOption(
            "guangdong", "Guangdong", listOf(
                ElCascaderOption("guangzhou", "Guangzhou"),
                ElCascaderOption("shenzhen", "Shenzhen"),
                ElCascaderOption("zhuhai", "Zhuhai (disabled)", disabled = true),
            ),
        ),
    )
    var basic by remember { mutableStateOf(listOf("sichuan", "chengdu")) }
    var filterable by remember { mutableStateOf(emptyList<String>()) }
    DemoExample("Basic usage", "Select a path through cascading columns.") {
        ElCascader(
            value = basic,
            onValueChange = { basic = it },
            options = options,
            placeholder = "Select a city",
            modifier = Modifier.width(280.dp),
        )
        DemoHint("Path: ${basic.joinToString(" / ")}")
    }
    DemoExample("Filterable", "Search among all leaf labels.") {
        ElCascader(
            value = filterable,
            onValueChange = { filterable = it },
            options = options,
            filterable = true,
            placeholder = "Type to filter",
            modifier = Modifier.width(280.dp),
        )
    }
    DemoExample("Disabled", "Disable selection while keeping the current path visible.") {
        ElCascader(
            value = listOf("guangdong", "shenzhen"),
            onValueChange = {},
            options = options,
            disabled = true,
            modifier = Modifier.width(280.dp),
        )
    }

    DemoExample("Clearable", "Clear the current path.") {
        var clearable by remember { mutableStateOf(listOf("sichuan", "chengdu")) }
        ElCascader(
            value = clearable,
            onValueChange = { clearable = it },
            options = options,
            placeholder = "Clearable cascader",
            clearable = true,
            modifier = Modifier.width(280.dp),
        )
    }
    DemoExample("Display only the last level", "Show only the selected leaf in the field.") {
        var leaf by remember { mutableStateOf(listOf("guangdong", "shenzhen")) }
        ElCascader(
            value = leaf,
            onValueChange = { leaf = it },
            options = options,
            showAllLevels = false,
            modifier = Modifier.width(280.dp),
        )
    }
    DemoExample("Disabled option", "Disabled options cannot be selected.") {
        var disabled by remember { mutableStateOf(listOf("sichuan", "chengdu")) }
        ElCascader(
            value = disabled,
            onValueChange = { disabled = it },
            options = options,
            modifier = Modifier.width(280.dp),
        )
        DemoHint("Zhuhai is disabled in the province list.")
    }
}
