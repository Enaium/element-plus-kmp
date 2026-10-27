package cn.enaium.elementplus.sample.gallery.form

import cn.enaium.elementplus.components.ElBorder
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
fun ColorPickerPanelDocs() {
    var basic by remember { mutableStateOf<Color>(Color(0xFF409EFF)) }
    var noAlpha by remember { mutableStateOf<Color>(Color(0xFF67C23A)) }
    var predefined by remember { mutableStateOf<Color>(Color(0xFFF56C6C)) }
    DemoExample("Basic usage", "Saturation/value, hue and alpha controls in one panel.") {
        ElColorPickerPanel(color = basic, onColorChange = { basic = it }, modifier = Modifier.width(300.dp))
    }
    DemoExample("Alpha", "Disable the alpha slider for opaque colors.") {
        ElColorPickerPanel(color = noAlpha, onColorChange = { noAlpha = it }, showAlpha = false, modifier = Modifier.width(300.dp))
    }
    DemoExample("Predefined colors", "Provide a row of selectable swatches.") {
        ElColorPickerPanel(
            color = predefined,
            onColorChange = { predefined = it },
            predefine = listOf(Color(0xFF409EFF), Color(0xFF67C23A), Color(0xFFE6A23C), Color(0xFFF56C6C), Color(0xFF909399)),
            modifier = Modifier.width(300.dp),
        )
    }
    DemoExample("Disabled", "Disable all panel interactions.") {
        ElColorPickerPanel(color = Color(0xFF409EFF), onColorChange = {}, disabled = true, modifier = Modifier.width(300.dp))
    }

    DemoExample("Border", "Wrap the panel in a bordered surface.") {
        var color by remember { mutableStateOf<Color>(Color(0xFF409EFF)) }
        ElBorder(Modifier.width(300.dp)) {
            ElColorPickerPanel(color = color, onColorChange = { color = it }, modifier = Modifier.padding(8.dp))
        }
    }
}
