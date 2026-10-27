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
fun TimeSelectDocs() {
    var step by remember { mutableStateOf<LocalTime?>(null) }
    DemoExample("Basic usage", "Choose a time from a generated list.") {
        var basic by remember { mutableStateOf<LocalTime?>(LocalTime(10, 0)) }
        ElTimeSelect(value = basic, onValueChange = { basic = it }, modifier = Modifier.width(200.dp))
    }
    DemoExample("Time range", "Set start, end and step to generate options.") {
        ElTimeSelect(
            value = step,
            onValueChange = { step = it },
            start = LocalTime(8, 0),
            end = LocalTime(12, 0),
            step = 15,
            format = "HH:mm",
            modifier = Modifier.width(200.dp),
        )
    }
    DemoExample("Disabled", "Disable the time select field.") {
        ElTimeSelect(value = LocalTime(10, 0), onValueChange = {}, disabled = true, modifier = Modifier.width(200.dp))
    }

    DemoExample("Time formats", "Customize the fixed option format.") {
        var value by remember { mutableStateOf<LocalTime?>(LocalTime(9, 30)) }
        ElTimeSelect(value = value, onValueChange = { value = it }, format = "HH:mm", step = 60, modifier = Modifier.width(200.dp))
    }
}
