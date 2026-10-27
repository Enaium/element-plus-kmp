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
fun InputTagDocs() {
    var limited by remember { mutableStateOf(listOf("One")) }
    var delimiter by remember { mutableStateOf(emptyList<String>()) }
    DemoExample("Basic usage", "Enter text and press Enter to create a tag.") {
        var basic by remember { mutableStateOf(listOf("Kotlin", "Compose")) }
        ElInputTag(values = basic, onValueChange = { basic = it }, placeholder = "Add tag")
        DemoHint("Tags: ${basic.joinToString()}")
    }
    DemoExample("Max", "Limit the number of tags.") {
        ElInputTag(values = limited, onValueChange = { limited = it }, max = 3, placeholder = "Up to 3 tags")
    }
    DemoExample("Delimiter", "A delimiter creates a tag automatically.") {
        ElInputTag(
            values = delimiter,
            onValueChange = { delimiter = it },
            delimiter = ",",
            placeholder = "Type a,b,c",
            modifier = Modifier.width(360.dp),
        )
    }
    DemoExample("Sizes and disabled", "Tag inputs follow the size scale.") {
        var large by remember { mutableStateOf(listOf("Kotlin")) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElInputTag(values = large, onValueChange = { large = it }, size = ElSize.Large)
            ElInputTag(values = listOf("Compose"), onValueChange = {}, disabled = true)
        }
    }

    DemoExample("Space trigger", "Create tags by pressing Space.") {
        var values by remember { mutableStateOf(listOf("Kotlin")) }
        ElInputTag(
            values = values,
            onValueChange = { values = it },
            trigger = ElInputTagTrigger.Space,
            placeholder = "Type and press Space",
            modifier = Modifier.width(360.dp),
        )
    }

    DemoExample("Clearable and sizes", "Tags can be cleared and follow the size scale.") {
        var values by remember { mutableStateOf(listOf("Alpha", "Beta")) }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ElInputTag(values = values, onValueChange = { values = it }, size = ElSize.Large, clearable = true, modifier = Modifier.width(360.dp))
            ElInputTag(values = values, onValueChange = { values = it }, size = ElSize.Small, clearable = true, modifier = Modifier.width(360.dp))
        }
    }
}
