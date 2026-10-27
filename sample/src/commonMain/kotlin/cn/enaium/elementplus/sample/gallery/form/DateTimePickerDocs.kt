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
fun DateTimePickerDocs() {
    var noSeconds by remember { mutableStateOf<LocalDateTime?>(LocalDateTime(2026, 10, 26, 9, 30)) }
    DemoExample("Basic usage", "Select date and time in one popup.") {
        var value by remember { mutableStateOf<LocalDateTime?>(LocalDateTime(2026, 10, 26, 9, 30)) }
        ElDateTimePicker(value = value, onValueChange = { value = it }, modifier = Modifier.width(300.dp))
        DemoHint("Selected: $value")
    }
    DemoExample("Date and time formats", "Omit seconds when they are not needed.") {
        ElDateTimePicker(
            value = noSeconds,
            onValueChange = { noSeconds = it },
            showSeconds = false,
            format = "YYYY-MM-DD HH:mm",
            modifier = Modifier.width(280.dp),
        )
    }
    DemoExample("Disabled", "Disable date and time selection.") {
        ElDateTimePicker(value = LocalDateTime(2026, 10, 26, 9, 30), onValueChange = {}, disabled = true, modifier = Modifier.width(300.dp))
    }

    DemoExample("Single panel", "Embed the date-time panel directly.") {
        var value by remember { mutableStateOf<LocalDateTime?>(LocalDateTime(2026, 10, 26, 9, 30)) }
        ElDateTimePickerPanel(value = value, onValueChange = { value = it }, modifier = Modifier.width(300.dp))
        DemoHint("Value: $value")
    }
}
