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
fun VirtualizedSelectDocs() {
    val options = remember { (1..1000).map { ElSelectOption("item-$it", "Item $it") } }
    var disabledValue by remember { mutableStateOf<String?>("item-42") }
    DemoExample("Basic usage", "A select backed by a lazy option list.") {
        var value by remember { mutableStateOf<String?>(null) }
        ElVirtualizedSelect(
            value = value,
            onValueChange = { value = it },
            options = options,
            placeholder = "Choose from 1000 options",
            modifier = Modifier.width(280.dp),
        )
        DemoHint("Selected: ${value ?: "-"}")
    }
    DemoExample("Filterable", "Filtering is enabled by default for the virtualized select.") {
        var filterValue by remember { mutableStateOf<String?>(null) }
        ElVirtualizedSelect(
            value = filterValue,
            onValueChange = { filterValue = it },
            options = options,
            filterable = true,
            placeholder = "Type to filter",
            modifier = Modifier.width(280.dp),
        )
    }
    DemoExample("Disabled", "Disable the field while keeping the value.") {
        ElVirtualizedSelect(
            value = disabledValue,
            onValueChange = {},
            options = options,
            disabled = true,
            modifier = Modifier.width(280.dp),
        )
    }

    DemoExample("Multi select", "Use the virtualized multiple select for large option sets.") {
        var values by remember { mutableStateOf(listOf("1")) }
        ElSelectMultiple(
            values = values,
            onValueChange = { values = it },
            options = (1..1000).map { ElSelectOption(it.toString(), "Option $it") },
            virtualized = true,
            filterable = true,
            placeholder = "Select options",
            modifier = Modifier.width(320.dp),
        )
    }

    DemoExample("Sizes", "Virtualized selects follow the component size scale.") {
        var value by remember { mutableStateOf<String?>(null) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElVirtualizedSelect(value = value, onValueChange = { value = it }, options = options, size = ElSize.Large, modifier = Modifier.width(280.dp))
            ElVirtualizedSelect(value = value, onValueChange = { value = it }, options = options, size = ElSize.Small, modifier = Modifier.width(280.dp))
        }
    }
    DemoExample("Clearable and disabled option", "Clear the selection and disable individual options.") {
        var value by remember { mutableStateOf<String?>("item-1") }
        ElVirtualizedSelect(
            value = value,
            onValueChange = { value = it },
            options = options + ElSelectOption("disabled", "Disabled option", disabled = true),
            clearable = true,
            modifier = Modifier.width(280.dp),
        )
    }
    DemoExample("Hide extra tags", "Collapse selected tags when many options are selected.") {
        var values by remember { mutableStateOf(listOf("item-1", "item-2", "item-3")) }
        ElSelectMultiple(
            values = values,
            onValueChange = { values = it },
            options = options.take(20),
            virtualized = true,
            collapseTags = true,
            maxCollapseTags = 2,
            modifier = Modifier.width(360.dp),
        )
    }
}
