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
fun ColorPickerDocs() {
    var predefined by remember { mutableStateOf<Color?>(Color(0xFF67C23A)) }
    var disabled by remember { mutableStateOf<Color?>(Color(0xFFE6A23C)) }
    val predefine = listOf(Color(0xFF409EFF), Color(0xFF67C23A), Color(0xFFE6A23C), Color(0xFFF56C6C), Color(0xFF909399))
    DemoExample("Basic usage", "Click the field to open the color panel.") {
        var basic by remember { mutableStateOf<Color?>(Color(0xFF409EFF)) }
        ElColorPicker(value = basic, onValueChange = { basic = it }, modifier = Modifier.width(260.dp))
    }
    DemoExample("Predefined colors", "Choose from a fixed palette.") {
        ElColorPicker(
            value = predefined,
            onValueChange = { predefined = it },
            predefine = predefine,
            showAlpha = false,
            modifier = Modifier.width(260.dp),
        )
    }
    DemoExample("Sizes", "Color pickers follow the component size scale.") {
        var large by remember { mutableStateOf<Color?>(Color(0xFF409EFF)) }
        var normal by remember { mutableStateOf<Color?>(Color(0xFF409EFF)) }
        var small by remember { mutableStateOf<Color?>(Color(0xFF409EFF)) }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ElColorPicker(value = large, onValueChange = { large = it }, size = ElSize.Large, modifier = Modifier.width(260.dp))
            ElColorPicker(value = normal, onValueChange = { normal = it }, size = ElSize.Default, modifier = Modifier.width(260.dp))
            ElColorPicker(value = small, onValueChange = { small = it }, size = ElSize.Small, modifier = Modifier.width(260.dp))
        }
    }
    DemoExample("Disabled", "Disable opening and clearing the picker.") {
        ElColorPicker(value = disabled, onValueChange = { disabled = it }, disabled = true, modifier = Modifier.width(260.dp))
    }

    DemoExample("Alpha", "Enable or disable the alpha channel.") {
        var color by remember { mutableStateOf<Color?>(Color(0x80409EFF)) }
        ElColorPicker(value = color, onValueChange = { color = it }, showAlpha = true, modifier = Modifier.width(260.dp))
    }
}
