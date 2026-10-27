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
fun SelectDocs() {
    DemoExample("Basic usage", "Use a select for a single value.") {
        var basic by remember { mutableStateOf<String?>("chengdu") }
        ElSelect(
            value = basic,
            onValueChange = { basic = it },
            options = cityOptions,
            placeholder = "Select a city",
            modifier = Modifier.width(240.dp),
        )
    }
    DemoExample("Multiple", "Select multiple values and show them as tags.") {
        var multiple by remember { mutableStateOf(listOf("beijing")) }
        ElSelectMultiple(
            values = multiple,
            onValueChange = { multiple = it },
            options = cityOptions,
            placeholder = "Select cities",
            modifier = Modifier.width(320.dp),
            clearable = true,
        )
    }
    DemoExample("Filterable", "Filter options by typing in the dropdown.") {
        var filterable by remember { mutableStateOf<String?>(null) }
        ElSelect(
            value = filterable,
            onValueChange = { filterable = it },
            options = cityOptions,
            placeholder = "Type to filter",
            filterable = true,
            modifier = Modifier.width(240.dp),
        )
    }
    DemoExample("Grouped options", "Options can be grouped with headers.") {
        var grouped by remember { mutableStateOf<String?>(null) }
        ElSelect(
            value = grouped,
            onValueChange = { grouped = it },
            options = listOf(
                ElSelectOption("beijing", "Beijing", group = "North"),
                ElSelectOption("harbin", "Harbin", group = "North"),
                ElSelectOption("shanghai", "Shanghai", group = "South"),
                ElSelectOption("shenzhen", "Shenzhen", group = "South"),
            ),
            placeholder = "Grouped select",
            modifier = Modifier.width(240.dp),
        )
    }
    DemoExample("Sizes", "Selects follow the component size scale.") {
        var largeValue by remember { mutableStateOf<String?>("chengdu") }
        var smallValue by remember { mutableStateOf<String?>("chengdu") }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElSelect(value = largeValue, onValueChange = { largeValue = it }, options = cityOptions, size = ElSize.Large, modifier = Modifier.width(240.dp))
            ElSelect(value = smallValue, onValueChange = { smallValue = it }, options = cityOptions, size = ElSize.Small, modifier = Modifier.width(240.dp))
        }
    }
    DemoExample("Loading and empty", "Show a loading state or a custom empty message.") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ElSelect(value = null, onValueChange = {}, options = emptyList(), loading = true, placeholder = "Loading", modifier = Modifier.width(200.dp))
            ElSelect(value = null, onValueChange = {}, options = emptyList(), noDataText = "Nothing here", placeholder = "Empty", modifier = Modifier.width(200.dp))
        }
    }
    DemoExample("Disabled", "Disable the select while keeping the value visible.") {
        ElSelect(value = "shanghai", onValueChange = {}, options = cityOptions, disabled = true, modifier = Modifier.width(240.dp))
    }

    DemoExample("Clearable", "Clear the current selection.") {
        var clearableValue by remember { mutableStateOf<String?>("chengdu") }
        ElSelect(
            value = clearableValue,
            onValueChange = { clearableValue = it },
            options = cityOptions,
            placeholder = "Clearable select",
            clearable = true,
            modifier = Modifier.width(240.dp),
        )
    }

    DemoExample("Disabled options and select", "Disable individual options or the whole selector.") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElSelect(
                value = null as String?,
                onValueChange = {},
                options = cityOptions + ElSelectOption("disabled", "Disabled city", disabled = true),
                placeholder = "A disabled option is shown",
                modifier = Modifier.width(240.dp),
            )
            ElSelect(
                value = "chengdu",
                onValueChange = {},
                options = cityOptions,
                disabled = true,
                modifier = Modifier.width(240.dp),
            )
        }
    }
    DemoExample("Basic multiple select", "Pick multiple values and render them as tags.") {
        var values by remember { mutableStateOf(listOf("chengdu")) }
        ElSelectMultiple(
            values = values,
            onValueChange = { values = it },
            options = cityOptions,
            placeholder = "Select cities",
            modifier = Modifier.width(360.dp),
        )
    }
}
